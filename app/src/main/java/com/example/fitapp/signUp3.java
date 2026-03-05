package com.example.fitapp;

import static com.example.fitapp.FBRef.refAuth;
import static com.example.fitapp.FBRef.refUsers;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class signUp3 extends AppCompatActivity {
    private RadioGroup experienceGroup, workoutsGroup;
    private String selectedGoal = "";
    private ImageButton fatLossBtn, muscleBtn, healthBtn;

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

    private void selectGoal(String goal, ImageButton selectedBtn) {
        selectedGoal = goal;
        fatLossBtn.setBackgroundColor(Color.WHITE);
        muscleBtn.setBackgroundColor(Color.WHITE);
        healthBtn.setBackgroundColor(Color.WHITE);
        selectedBtn.setBackgroundColor(Color.LTGRAY);
    }

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

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("experienceLevel", experienceMap);
                    updates.put("workoutsPerWeek", finalWorkoutsPerWeek);
                    updates.put("goals", goalsMap);
                    updates.put("dailyTargetCalories", calories);

                    refUsers.child(userId).updateChildren(updates).addOnCompleteListener(task -> {
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

    public double calculateBMR(User user) {
        double heightCm = user.getHeight();
        if (user.isGender()) {
            return 88.362 + (13.397 * user.getWeight()) + (4.799 * heightCm) - (5.677 * user.getAge());
        } else {
            return 447.593 + (9.247 * user.getWeight()) + (3.098 * heightCm) - (4.330 * user.getAge());
        }
    }

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
