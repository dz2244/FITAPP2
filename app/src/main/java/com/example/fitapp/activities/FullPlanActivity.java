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

public class FullPlanActivity extends AppCompatActivity {

    private TextView tvFullPlanTitle;
    private RecyclerView recyclerWeeks;
    private WeekAdapter adapter;
    private List<TrainingWeek> weekList = new ArrayList<>();

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

    private static class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.WeekViewHolder> {
        private List<TrainingWeek> weeks;

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

        static class WeekViewHolder extends RecyclerView.ViewHolder {
            TextView tvWeekNumber, tvWeekStatus;
            ProgressBar pbWeekProgress;

            WeekViewHolder(View itemView) {
                super(itemView);
                tvWeekNumber = itemView.findViewById(R.id.tvWeekNumber);
                tvWeekStatus = itemView.findViewById(R.id.tvWeekStatus);
                pbWeekProgress = itemView.findViewById(R.id.pbWeekProgress);
            }
        }
    }
}
