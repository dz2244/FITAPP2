package com.example.fitapp.activities;

import static com.example.fitapp.classes.FBRef.refAuth;
import static com.example.fitapp.classes.FBRef.refUsers;
import static com.example.fitapp.classes.FBRef.refWorkoutPrograms;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitapp.R;
import com.example.fitapp.classes.Exercise;
import com.example.fitapp.classes.TrainingDay;
import com.example.fitapp.classes.TrainingWeek;
import com.example.fitapp.classes.User;
import com.example.fitapp.classes.WorkoutProgram;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.vertexai.FirebaseVertexAI;
import com.google.firebase.vertexai.GenerativeModel;
import com.google.firebase.vertexai.java.GenerativeModelFutures;
import com.google.firebase.vertexai.type.Content;
import com.google.firebase.vertexai.type.GenerateContentResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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
    /** Currently selected fitness goal (e.g., "Fat Loss", "Muscle Gain"). */
    private String selectedGoal = "";
    /** Button for fat loss goal selection. */
    private ImageButton fatLossBtn;
    /** Button for muscle gain goal selection. */
    private ImageButton muscleBtn;
    /** Button for general health goal selection. */
    private ImageButton healthBtn;

    /** Single-thread executor for handling background tasks like AI processing. */
    private final Executor executor = Executors.newSingleThreadExecutor();

    /**
     * Initializes the activity and sets up button click listeners.
     *
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
     * Updates the UI to reflect the selected fitness goal.
     *
     * @param goal        The goal name.
     * @param selectedBtn The button that was clicked.
     */
    private void selectGoal(String goal, ImageButton selectedBtn) {
        selectedGoal = goal;
        fatLossBtn.setBackgroundColor(Color.WHITE);
        muscleBtn.setBackgroundColor(Color.WHITE);
        healthBtn.setBackgroundColor(Color.WHITE);
        selectedBtn.setBackgroundColor(Color.LTGRAY);
    }

    /**
     * Handles the "Get Started" button click. Finalizes user data collection,
     * updates Firebase, and triggers AI workout generation.
     *
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
                    user.setRegistrationDate(System.currentTimeMillis());

                    Task<Void> userUpdateTask = refUsers.child(userId).setValue(user);
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
     * Generates a personalized workout program using Vertex AI based on the user's profile.
     *
     * @param user The user for whom to generate the program.
     */
    private void generateAndSaveAIWorkout(User user) {
        GenerativeModel gm = FirebaseVertexAI.getInstance()
                .generativeModel("gemini-1.5-flash");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        String splitSuggestion = "";
        int frequency = user.getWorkoutsPerWeek();
        if (frequency == 2) splitSuggestion = "Upper/Lower Split (Day 1: Upper, Day 2: Lower)";
        else if (frequency == 3) splitSuggestion = "Push/Pull/Legs Split";
        else if (frequency == 4) splitSuggestion = "Upper/Lower Split done twice (Upper A, Lower A, Upper B, Lower B)";
        else splitSuggestion = "PPL + Upper/Lower or Full Body";

        String promptText = String.format(
                "Generate a professional workout program using a %s for a user training %d days per week.\n" +
                "User Profile: Age: %d, Gender: %s, Weight: %.1fkg, Height: %.2fm, Experience: %s, Goal: %s.\n" +
                "CRITICAL REQUIREMENTS:\n" +
                "1. Each of the %d workout days MUST be UNIQUE. If a split repeats (like Upper/Lower), 'Upper A' and 'Upper B' MUST have different exercises.\n" +
                "2. Each day should have 6-8 exercises.\n" +
                "3. Provide realistic sets, reps, and rest times (in seconds).\n" +
                "4. Return ONLY a valid JSON object with this structure:\n" +
                "{\n" +
                "  \"programName\": \"String\",\n" +
                "  \"workoutDays\": [\n" +
                "    {\n" +
                "      \"dayName\": \"e.g. Day 1 - Upper A (Chest/Back Focus)\",\n" +
                "      \"estimatedTime\": int,\n" +
                "      \"exercises\": [\n" +
                "        { \"name\": \"String\", \"sets\": int, \"reps\": int, \"rest\": int }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n" +
                "Ensure exactly %d unique workout days are in the 'workoutDays' array.",
                splitSuggestion, frequency,
                user.getAge(), user.isGender() ? "Male" : "Female", user.getWeight(), 
                user.getHeight(), user.getExperienceLevel().keySet().iterator().next(),
                user.getGoals().keySet().iterator().next(),
                frequency, frequency
        );

        Content prompt = new Content.Builder().addText(promptText).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                saveProgramToFirebase(user.getUserId(), result.getText(), user);
            }

            @Override
            public void onFailure(Throwable t) {
                saveBasicProgramToFirebase(user.getUserId(), user);
            }
        }, executor);
    }

    /**
     * Parses the AI-generated response and saves the workout program to Firebase.
     *
     * @param userId The ID of the user.
     * @param aiText The raw response text from the AI.
     * @param user   The user object.
     */
    private void saveProgramToFirebase(String userId, String aiText, User user) {
        try {
            String jsonStr = aiText.trim();
            if (jsonStr.contains("```json")) {
                jsonStr = jsonStr.substring(jsonStr.indexOf("```json") + 7);
                jsonStr = jsonStr.substring(0, jsonStr.lastIndexOf("```"));
            } else if (jsonStr.contains("```")) {
                jsonStr = jsonStr.substring(jsonStr.indexOf("```") + 3);
                jsonStr = jsonStr.substring(0, jsonStr.lastIndexOf("```"));
            }
            jsonStr = jsonStr.trim();
            
            JSONObject jsonObject = new JSONObject(jsonStr);
            String programName = jsonObject.getString("programName");
            JSONArray workoutDaysJson = jsonObject.getJSONArray("workoutDays");

            ArrayList<TrainingDay> days = new ArrayList<>();
            for (int i = 0; i < workoutDaysJson.length(); i++) {
                JSONObject dayJson = workoutDaysJson.getJSONObject(i);
                JSONArray exercisesJson = dayJson.getJSONArray("exercises");
                ArrayList<Exercise> exercises = new ArrayList<>();
                for (int j = 0; j < exercisesJson.length(); j++) {
                    JSONObject exJson = exercisesJson.getJSONObject(j);
                    exercises.add(new Exercise(exJson.getString("name"), exJson.getInt("sets"), exJson.getInt("reps"), exJson.getInt("rest")));
                }
                days.add(new TrainingDay(dayJson.getString("dayName"), 0, dayJson.getInt("estimatedTime"), false, exercises));
            }

            String programId = refWorkoutPrograms.push().getKey();
            ArrayList<TrainingWeek> weeksList = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                weeksList.add(new TrainingWeek("Week " + i, new ArrayList<>(days), 0));
            }

            WorkoutProgram program = new WorkoutProgram(programId, programName, user.getExperienceLevel(), weeksList);
            if (programId != null) {
                refWorkoutPrograms.child(programId).setValue(program);
                refUsers.child(userId).child("currentProgramId").setValue(programId);
            }
        } catch (Exception e) {
            saveBasicProgramToFirebase(userId, user);
        }
    }

    /**
     * Saves a fallback basic workout program if AI generation fails.
     *
     * @param userId The ID of the user.
     * @param user   The user object.
     */
    private void saveBasicProgramToFirebase(String userId, User user) {
        String programId = refWorkoutPrograms.push().getKey();
        ArrayList<TrainingDay> defaultDays = new ArrayList<>();
        int freq = user.getWorkoutsPerWeek();
        
        if (freq == 2) {
            defaultDays.add(new TrainingDay("Day 1 - Upper Body", 0, 50, false, getUpperA()));
            defaultDays.add(new TrainingDay("Day 2 - Lower Body", 0, 50, false, getLowerA()));
        } else if (freq == 3) {
            defaultDays.add(new TrainingDay("Day 1 - Push", 0, 50, false, getPush()));
            defaultDays.add(new TrainingDay("Day 2 - Pull", 0, 50, false, getPull()));
            defaultDays.add(new TrainingDay("Day 3 - Legs", 0, 50, false, getLegs()));
        } else if (freq == 4) {
            defaultDays.add(new TrainingDay("Day 1 - Upper A", 0, 50, false, getUpperA()));
            defaultDays.add(new TrainingDay("Day 2 - Lower A", 0, 50, false, getLowerA()));
            defaultDays.add(new TrainingDay("Day 3 - Upper B", 0, 50, false, getUpperB()));
            defaultDays.add(new TrainingDay("Day 4 - Lower B", 0, 50, false, getLowerB()));
        } else {
            for (int i = 0; i < freq; i++) {
                defaultDays.add(new TrainingDay("Day " + (i + 1) + " - Full Body", 0, 60, false, getFullBody()));
            }
        }

        ArrayList<TrainingWeek> weeksList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            weeksList.add(new TrainingWeek("Week " + i, new ArrayList<>(defaultDays), 0));
        }

        WorkoutProgram program = new WorkoutProgram(programId, user.getUsername() + "'s Plan", user.getExperienceLevel(), weeksList);
        if (programId != null) {
            refWorkoutPrograms.child(programId).setValue(program);
            refUsers.child(userId).child("currentProgramId").setValue(programId);
        }
    }

    private ArrayList<Exercise> getUpperA() {
        ArrayList<Exercise> l = new ArrayList<>();
        l.add(new Exercise("Bench Press", 3, 8, 90));
        l.add(new Exercise("Rows", 3, 10, 90));
        l.add(new Exercise("Overhead Press", 3, 10, 60));
        l.add(new Exercise("Lat Pulldown", 3, 12, 60));
        l.add(new Exercise("Bicep Curls", 3, 12, 45));
        return l;
    }
    private ArrayList<Exercise> getUpperB() {
        ArrayList<Exercise> l = new ArrayList<>();
        l.add(new Exercise("Incline DB Press", 3, 10, 90));
        l.add(new Exercise("Pull Ups", 3, 8, 90));
        l.add(new Exercise("Lateral Raises", 3, 12, 60));
        l.add(new Exercise("Seated Rows", 3, 12, 60));
        l.add(new Exercise("Tricep Pushdowns", 3, 12, 45));
        return l;
    }
    private ArrayList<Exercise> getLowerA() {
        ArrayList<Exercise> l = new ArrayList<>();
        l.add(new Exercise("Squats", 3, 8, 120));
        l.add(new Exercise("Leg Press", 3, 12, 90));
        l.add(new Exercise("Leg Curls", 3, 12, 60));
        l.add(new Exercise("Calf Raises", 4, 15, 45));
        return l;
    }
    private ArrayList<Exercise> getLowerB() {
        ArrayList<Exercise> l = new ArrayList<>();
        l.add(new Exercise("Deadlifts", 3, 5, 120));
        l.add(new Exercise("Lunges", 3, 12, 90));
        l.add(new Exercise("Leg Extensions", 3, 15, 60));
        l.add(new Exercise("Plank", 3, 60, 45));
        return l;
    }
    private ArrayList<Exercise> getPush() { return getUpperA(); }
    private ArrayList<Exercise> getPull() { return getUpperB(); }
    private ArrayList<Exercise> getLegs() { return getLowerA(); }
    private ArrayList<Exercise> getFullBody() { return getUpperA(); }

    /**
     * Calculates the Basal Metabolic Rate (BMR) for the user.
     *
     * @param user The user object.
     * @return Calculated BMR.
     */
    public double calculateBMR(User user) {
        double h = user.getHeight() * 100;
        return user.isGender() ? 88.362 + (13.397 * user.getWeight()) + (4.799 * h) - (5.677 * user.getAge()) : 447.593 + (9.247 * user.getWeight()) + (3.098 * h) - (4.330 * user.getAge());
    }

    /**
     * Calculates the daily target calories based on BMR, activity level, and goals.
     *
     * @param user The user object.
     * @param f    Frequency of workouts per week.
     * @param g    Fitness goal ("Fat Loss", "Muscle Gain", etc.).
     * @return Calculated target calories.
     */
    public int calculateCalories(User user, int f, String g) {
        double tdee = calculateBMR(user) * (1.2 + f * 0.1);
        if (g.equalsIgnoreCase("Fat Loss")) tdee -= 500;
        else if (g.equalsIgnoreCase("Muscle Gain")) tdee += 250;
        return (int) Math.round(tdee);
    }
}
