package com.example.fitapp.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitapp.R;
import com.example.fitapp.classes.Exercise;

import java.util.ArrayList;
import java.util.List;

public class WorkoutDetailActivity extends AppCompatActivity {

    private TextView tvWorkoutName, tvWorkoutInfo;
    private RecyclerView recyclerExercises;
    private ExerciseAdapter adapter;
    private List<Exercise> exerciseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);

        tvWorkoutName = findViewById(R.id.tvDetailWorkoutName);
        tvWorkoutInfo = findViewById(R.id.tvDetailWorkoutInfo);
        recyclerExercises = findViewById(R.id.recyclerExercises);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        String workoutName = getIntent().getStringExtra("WORKOUT_NAME");
        tvWorkoutName.setText(workoutName);

        // Load exercises passed from TrainingFragment
        ArrayList<Exercise> passedExercises = (ArrayList<Exercise>) getIntent().getSerializableExtra("EXERCISES");
        if (passedExercises != null && !passedExercises.isEmpty()) {
            exerciseList.addAll(passedExercises);
        } else {
            // Fallback mock data if nothing was passed
            loadMockExercises();
        }

        recyclerExercises.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseAdapter(exerciseList);
        recyclerExercises.setAdapter(adapter);
    }

    private void loadMockExercises() {
        exerciseList.add(new Exercise("Bench Press", 4, 10, 90));
        exerciseList.add(new Exercise("Incline Dumbbell Press", 3, 12, 60));
        exerciseList.add(new Exercise("Chest Flys", 3, 15, 60));
        exerciseList.add(new Exercise("Tricep Pushdowns", 4, 12, 45));
        exerciseList.add(new Exercise("Overhead Extension", 3, 12, 45));
    }

    private static class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
        private List<Exercise> exercises;

        ExerciseAdapter(List<Exercise> exercises) {
            this.exercises = exercises;
        }

        @NonNull
        @Override
        public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
            return new ExerciseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
            Exercise ex = exercises.get(position);
            holder.tvName.setText(ex.getExerciseName());
            holder.tvDetails.setText(ex.getSets() + " sets x " + ex.getReps() + " reps");
            holder.tvRest.setText(ex.getRestTime() + "s Rest");
        }

        @Override
        public int getItemCount() {
            return exercises.size();
        }

        static class ExerciseViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvDetails, tvRest;

            ExerciseViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvExerciseName);
                tvDetails = itemView.findViewById(R.id.tvExerciseDetails);
                tvRest = itemView.findViewById(R.id.tvRestTime);
            }
        }
    }
}
