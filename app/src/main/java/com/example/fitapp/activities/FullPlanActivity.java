package com.example.fitapp.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitapp.R;
import com.example.fitapp.classes.FBRef;
import com.example.fitapp.classes.TrainingWeek;
import com.example.fitapp.classes.User;
import com.example.fitapp.classes.WorkoutProgram;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that displays the full training plan for the user.
 * It shows a list of training weeks and their completion progress.
 */
public class FullPlanActivity extends AppCompatActivity {

    /** TextView for the title of the full plan. */
    private TextView tvFullPlanTitle;
    /** RecyclerView to display the list of training weeks. */
    private RecyclerView recyclerWeeks;
    /** Adapter for the training weeks RecyclerView. */
    private WeekAdapter adapter;
    /** List of training weeks to be displayed. */
    private List<TrainingWeek> weekList = new ArrayList<>();

    /**
     * Initializes the activity, sets the content view, and binds UI components.
     * Starts fetching the user's workout program.
     * @param savedInstanceState Bundle containing activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_plan);

        tvFullPlanTitle = findViewById(R.id.tvFullPlanTitle);
        recyclerWeeks = findViewById(R.id.recyclerWeeks);
        findViewById(R.id.btnBackFromPlan).setOnClickListener(v -> finish());

        recyclerWeeks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeekAdapter(weekList);
        recyclerWeeks.setAdapter(adapter);

        fetchUserProgram();
    }

    /**
     * Fetches the current user's profile from Firebase to retrieve their current program ID.
     */
    private void fetchUserProgram() {
        FirebaseUser firebaseUser = FBRef.refAuth.getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            FBRef.refUsers.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getCurrentProgramId() != null) {
                        tvFullPlanTitle.setText(user.getUsername() + "'s Transformation");
                        loadProgram(user.getCurrentProgramId());
                    } else {
                        Toast.makeText(FullPlanActivity.this, "No program found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    /**
     * Loads the specific workout program details from Firebase using the program ID.
     * @param programId The ID of the workout program to load.
     */
    private void loadProgram(String programId) {
        FBRef.refWorkoutPrograms.child(programId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                WorkoutProgram program = snapshot.getValue(WorkoutProgram.class);
                if (program != null && program.getWeeks() != null) {
                    weekList.clear();
                    weekList.addAll(program.getWeeks());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * RecyclerView Adapter for displaying individual training weeks.
     */
    private static class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.WeekViewHolder> {
        /** The list of training weeks. */
        private List<TrainingWeek> weeks;

        /**
         * Constructor for WeekAdapter.
         * @param weeks The list of training weeks to display.
         */
        WeekAdapter(List<TrainingWeek> weeks) {
            this.weeks = weeks;
        }

        @NonNull
        @Override
        public WeekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_training_week, parent, false);
            return new WeekViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WeekViewHolder holder, int position) {
            TrainingWeek week = weeks.get(position);
            holder.tvWeekNumber.setText("Week " + (position + 1));
            
            int completed = week.getWorkoutsThisWeek();
            // In a real app, the total might vary per week. 
            // For now, we use the number of training days if available, otherwise default to 4.
            int total = (week.getTrainingDays() != null && !week.getTrainingDays().isEmpty()) 
                    ? week.getTrainingDays().size() : 4;

            holder.tvWeekStatus.setText(completed + "/" + total + " Workouts completed");
            
            int progress = (total == 0) ? 0 : (completed * 100) / total;
            holder.pbWeekProgress.setProgress(progress);
        }

        @Override
        public int getItemCount() {
            return weeks.size();
        }

        /**
         * ViewHolder for a training week item.
         */
        static class WeekViewHolder extends RecyclerView.ViewHolder {
            /** TextView for the week number. */
            TextView tvWeekNumber;
            /** TextView for the workout completion status. */
            TextView tvWeekStatus;
            /** ProgressBar for the weekly progress. */
            ProgressBar pbWeekProgress;

            /**
             * Constructor for WeekViewHolder.
             * @param itemView The view of the week item.
             */
            WeekViewHolder(View itemView) {
                super(itemView);
                tvWeekNumber = itemView.findViewById(R.id.tvWeekNumber);
                tvWeekStatus = itemView.findViewById(R.id.tvWeekStatus);
                pbWeekProgress = itemView.findViewById(R.id.pbWeekProgress);
            }
        }
    }
}
