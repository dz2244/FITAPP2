package com.example.fitapp;

import static com.example.fitapp.FBRef.refAuth;
import static com.example.fitapp.FBRef.refUsers;
import static com.example.fitapp.FBRef.refWorkoutPrograms;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.vertexai.FirebaseVertexAI;
import com.google.firebase.vertexai.GenerativeModel;
import com.google.firebase.vertexai.java.GenerativeModelFutures;
import com.google.firebase.vertexai.type.Content;
import com.google.firebase.vertexai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Activity for the third step of user sign-up.
 * Collects information about experience level, workout frequency, and fitness goals.
 * Calculates daily target calories and completes the user profile in Firebase.
 * Also generates a personalized workout program using Vertex AI (Gemini).
 */
public class signUp3 extends AppCompatActivity {
    /** RadioGroup for experience level selection. */
    private RadioGroup experienceGroup;
    /** RadioGroup for workouts per week selection. */
    private RadioGroup workoutsGroup;
    /** Currently selected fitness goal. */
    private String selectedGoal = "";
    /** Button for fat loss goal. */
    private ImageButton fatLossBtn;
    /** Button for muscle gain goal. */
    private ImageButton muscleBtn;
    /** Button for general health goal. */
    private ImageButton healthBtn;

    private Executor executor = Executors.newSingleThreadExecutor();

    /**
     * Initializes the activity, sets the content view, and binds the UI components.
     * Sets up click listeners for goal selection buttons.
     * @param savedInstanceState Bundle containing activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up3);

        experienceGroup = findViewById(R.id.experienceGroup);
        workoutsGroup = findViewById(R.id.workoutsGroup);
        fatLossBtn = findViewById(R.id.fatLossBtn);
        muscleBtn = findViewById(R.id.muscleBtn);
        healthBtn = findViewById(R.id.healthBtn);

        fatLossBtn.setOnClickListener(v -> selectGoal("Fat Loss", fatLossBtn));
        muscleBtn.setOnClickListener(v -> selectGoal("Muscle Gain", muscleBtn));
        healthBtn.setOnClickListener(v -> selectGoal("General Health", healthBtn));
    }

    /**
     * Updates the UI to show the selected goal and stores the goal string.
     * @param goal The selected goal string.
     * @param selectedBtn The ImageButton that was clicked.
     */
    private void selectGoal(String goal, ImageButton selectedBtn) {
        selectedGoal = goal;
        fatLossBtn.setBackgroundColor(Color.WHITE);
        muscleBtn.setBackgroundColor(Color.WHITE);
        healthBtn.setBackgroundColor(Color.WHITE);
        selectedBtn.setBackgroundColor(Color.LTGRAY);
    }

    /**
     * Handles the "Get Started" button click.
     * Validates selections, fetches existing user data, calculates calories,
     * updates the user profile in Firebase, generates an AI workout plan,
     * and navigates to the main activity.
     * @param view The clicked view.
     */
    public void clickedGetStartedBtn(View view) {
        int expId = experienceGroup.getCheckedRadioButtonId();
        int workId = workoutsGroup.getCheckedRadioButtonId();

        if (expId == -1 || workId == -1 || selectedGoal.isEmpty()) {
            Toast.makeText(this, "Please select all options", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedExp = findViewById(expId);
        RadioButton selectedWork = findViewById(workId);

        String experience = selectedExp.getText().toString();
        String workoutsPerWeekStr = selectedWork.getText().toString();
        
        int workoutsPerWeek = 0;
        if (workoutsPerWeekStr.equals("1-2")) workoutsPerWeek = 2;
        else if (workoutsPerWeekStr.equals("3-4")) workoutsPerWeek = 4;
        else if (workoutsPerWeekStr.equals("5+")) workoutsPerWeek = 5;

        final int finalWorkoutsPerWeek = workoutsPerWeek;
        String userId = refAuth.getCurrentUser().getUid();

        refUsers.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    int calories = calculateCalories(user, finalWorkoutsPerWeek, selectedGoal);
                    
                    Map<String, Integer> experienceMap = new HashMap<>();
                    experienceMap.put(experience, 1);

                    Map<String, Integer> goalsMap = new HashMap<>();
                    goalsMap.put(selectedGoal, 1);

                    user.setExperienceLevel(experienceMap);
                    user.setWorkoutsPerWeek(finalWorkoutsPerWeek);
                    user.setGoals(goalsMap);
                    user.setDailyTargetCalories(calories);

                    // 1. Update User info in Firebase
                    Task<Void> userUpdateTask = refUsers.child(userId).setValue(user);
                    
                    // 2. Generate AI Workout Program
                    generateAndSaveAIWorkout(user);

                    userUpdateTask.addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(signUp3.this, FragmentsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(signUp3.this, "Failed to update info", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * Uses Vertex AI to generate a workout program based on user profile and saves it to Firebase.
     * @param user The user profile data.
     */
    private void generateAndSaveAIWorkout(User user) {
        GenerativeModel gm = FirebaseVertexAI.getInstance()
                .generativeModel("gemini-1.5-flash");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        String promptText = String.format(
                "Generate a concise workout program for a user: " +
                "Age: %d, Gender: %s, Weight: %.1fkg, Height: %.2fm, Experience: %s, " +
                "Workouts per week: %d, Goal: %s. " +
                "Provide a title for the program and a summary of the routine.",
                user.getAge(), user.isGender() ? "Male" : "Female", user.getWeight(), 
                user.getHeight(), user.getExperienceLevel().keySet().iterator().next(),
                user.getWorkoutsPerWeek(), user.getGoals().keySet().iterator().next()
        );

        Content prompt = new Content.Builder()
                .addText(promptText)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String aiResponse = result.getText();
                Log.d("VertexAI", "Response: " + aiResponse);
                saveProgramToFirebase(user.getUserId(), aiResponse, user);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("VertexAI", "Error generating program", t);
            }
        }, executor);
    }

    /**
     * Parses the AI response and saves the WorkoutProgram to the database.
     * @param userId The user's ID.
     * @param aiText The raw text from Vertex AI.
     * @param user The user object for additional context.
     */
    private void saveProgramToFirebase(String userId, String aiText, User user) {
        String programId = refWorkoutPrograms.push().getKey();
        
        Map<String, Integer> levelMap = new HashMap<>();
        levelMap.put(user.getExperienceLevel().keySet().iterator().next(), 1);
        
        Map<String, Integer> daysMap = new HashMap<>();
        daysMap.put("Weekly", user.getWorkoutsPerWeek());

        WorkoutProgram program = new WorkoutProgram(
                programId,
                user.getUsername() + "'s AI Plan",
                levelMap,
                daysMap
        );

        if (programId != null) {
            refWorkoutPrograms.child(programId).setValue(program);
            // Link program to user
            refUsers.child(userId).child("currentProgramId").setValue(programId);
        }
    }

    /**
     * Calculates the Basal Metabolic Rate (BMR) for the user.
     * @param user The User object containing biometrics.
     * @return Calculated BMR value.
     */
    public double calculateBMR(User user) {
        double heightCm = user.getHeight() * 100;
        if (user.isGender()) {
            return 88.362 + (13.397 * user.getWeight()) + (4.799 * heightCm) - (5.677 * user.getAge());
        } else {
            return 447.593 + (9.247 * user.getWeight()) + (3.098 * heightCm) - (4.330 * user.getAge());
        }
    }

    /**
     * Calculates the daily target calories based on BMR, activity level, and goals.
     * @param user The user object.
     * @param workoutsPerWeek Number of workouts per week.
     * @param goal The fitness goal.
     * @return Recommended daily calorie intake.
     */
    public int calculateCalories(User user, int workoutsPerWeek, String goal) {
        double bmr = calculateBMR(user);
        double activityMultiplier = 1.2 + (workoutsPerWeek * 0.1);

        double tdee = bmr * activityMultiplier;

        if (goal.equalsIgnoreCase("Fat Loss")) {
            tdee -= 500;
        } else if (goal.equalsIgnoreCase("Muscle Gain")) {
            tdee += 250;
        }

        return (int) Math.round(tdee);
    }
}
