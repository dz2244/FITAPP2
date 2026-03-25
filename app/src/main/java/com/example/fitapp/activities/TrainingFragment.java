package com.example.fitapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitapp.R;
import com.example.fitapp.classes.Exercise;
import com.example.fitapp.classes.FBRef;
import com.example.fitapp.classes.TrainingDay;
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
 * Fragment for training management.
 * Displays a list of workouts pulled from Firebase based on the current week.
 * Allows users to track their progress and view workout details.
 */
public class TrainingFragment extends Fragment {

    /** RecyclerView to display the list of workouts for the current week. */
    private RecyclerView recyclerWorkouts;
    /** ProgressBar showing the weekly workout completion percentage. */
    private ProgressBar progressWeekly;
    /** TextView displaying the count of completed workouts over the total for the week. */
    private TextView tvWeeklyWorkoutsValue;
    /** TextView displaying the completion percentage text. */
    private TextView tvCompletionPercent;
    /** TextViews for displaying workout plan details. */
    private TextView tvPlanName, tvPlanGoal, tvPlanFrequency, tvPlanDuration;
    /** TextView displaying the current training week title. */
    private TextView tvTrainingTitle;
    /** TextViews for user's biometric targets and status. */
    private TextView tvCaloriesValue, tvCurrentWeightValue;

    /** List of workout items currently displayed in the RecyclerView. */
    private final List<WorkoutItem> workoutList = new ArrayList<>();
    /** Adapter for the workouts RecyclerView. */
    private WorkoutAdapter workoutAdapter;

    /** Current user data retrieved from Firebase. */
    private User currentUserData;
    /** Current workout program assigned to the user. */
    private WorkoutProgram currentProgram;
    /** The index of the current week calculated from the registration date. */
    private int currentWeekIndex = 0;

    /**
     * Required empty public constructor for Fragment instantiation.
     */
    public TrainingFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupButtons(view);
        fetchUserData();
    }

    /**
     * Binds UI components from the layout.
     * @param view The root view of the fragment.
     */
    private void initViews(View view) {
        recyclerWorkouts = view.findViewById(R.id.recyclerWorkouts);
        progressWeekly = view.findViewById(R.id.progressWeekly);
        tvWeeklyWorkoutsValue = view.findViewById(R.id.tvWeeklyWorkoutsValue);
        tvCompletionPercent = view.findViewById(R.id.tvCompletionPercent);

        tvPlanName = view.findViewById(R.id.tvPlanName);
        tvPlanGoal = view.findViewById(R.id.tvPlanGoal);
        tvPlanFrequency = view.findViewById(R.id.tvPlanFrequency);
        tvPlanDuration = view.findViewById(R.id.tvPlanDuration);
        tvTrainingTitle = view.findViewById(R.id.tvTrainingTitle);
        
        tvCaloriesValue = view.findViewById(R.id.tvCaloriesValue);
        tvCurrentWeightValue = view.findViewById(R.id.tvCurrentWeightValue);
    }

    /**
     * Fetches current user data from Firebase and starts the UI update process.
     */
    private void fetchUserData() {
        FirebaseUser firebaseUser = FBRef.refAuth.getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            FBRef.refUsers.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!isAdded()) return;
                    currentUserData = snapshot.getValue(User.class);
                    if (currentUserData != null) {
                        calculateCurrentWeek();
                        updateProfileUI(currentUserData);
                        fetchWorkoutProgram(currentUserData.getCurrentProgramId());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (isAdded()) Toast.makeText(requireContext(), "Error loading user", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Calculates the current week number based on the user's registration date.
     */
    private void calculateCurrentWeek() {
        if (currentUserData.getRegistrationDate() == 0) {
            currentWeekIndex = 0;
            return;
        }

        long currentTime = System.currentTimeMillis();
        long diff = currentTime - currentUserData.getRegistrationDate();
        currentWeekIndex = (int) (diff / 604800000L);
        
        if (currentWeekIndex > 11) currentWeekIndex = 11;
        if (currentWeekIndex < 0) currentWeekIndex = 0;

        if (tvTrainingTitle != null) {
            tvTrainingTitle.setText("Training - Week " + (currentWeekIndex + 1));
        }
    }

    /**
     * Fetches the details of the workout program from Firebase.
     * @param programId The ID of the workout program to fetch.
     */
    private void fetchWorkoutProgram(String programId) {
        if (programId == null || programId.isEmpty()) return;

        FBRef.refWorkoutPrograms.child(programId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;
                currentProgram = snapshot.getValue(WorkoutProgram.class);
                if (currentProgram != null && currentProgram.getWeeks() != null && currentWeekIndex < currentProgram.getWeeks().size()) {
                    loadWorkoutsFromWeek(currentProgram.getWeeks().get(currentWeekIndex));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * Loads the workouts for the current week into the local list and updates the adapter.
     * @param week The TrainingWeek object containing the workouts.
     */
    private void loadWorkoutsFromWeek(TrainingWeek week) {
        workoutList.clear();
        if (week.getTrainingDays() != null) {
            for (int i = 0; i < week.getTrainingDays().size(); i++) {
                TrainingDay day = week.getTrainingDays().get(i);
                workoutList.add(new WorkoutItem(
                        day.getDate() != null ? day.getDate() : "Workout " + (i + 1),
                        day.getEstimatedtime() + " min • " + (day.getExercises() != null ? day.getExercises().size() : 0) + " exercises",
                        day.isCompleted(),
                        day.getExercises()
                ));
            }
        }
        workoutAdapter.notifyDataSetChanged();
        updateProgressUI();
    }

    /**
     * Updates the UI with user-specific profile information.
     * @param user The user object containing the data to display.
     */
    private void updateProfileUI(User user) {
        if (tvPlanName != null) tvPlanName.setText(user.getUsername() + "'s Plan");
        if (tvPlanGoal != null && user.getGoals() != null) {
            StringBuilder goalsStr = new StringBuilder("Goals: ");
            for (String goal : user.getGoals().keySet()) goalsStr.append(goal).append(" ");
            tvPlanGoal.setText(goalsStr.toString().trim());
        }
        if (tvPlanFrequency != null) tvPlanFrequency.setText("Workouts per week: " + user.getWorkoutsPerWeek());
        
        if (tvCaloriesValue != null) tvCaloriesValue.setText(String.valueOf(user.getDailyTargetCalories()));
        if (tvCurrentWeightValue != null) tvCurrentWeightValue.setText(user.getWeight() + "kg");
        
        if (tvPlanDuration != null) {
            tvPlanDuration.setText("Weight: " + user.getWeight() + "kg | Age: " + user.getAge());
        }
    }

    /**
     * Sets up the RecyclerView with its adapter and layout manager.
     */
    private void setupRecyclerView() {
        recyclerWorkouts.setLayoutManager(new LinearLayoutManager(requireContext()));
        workoutAdapter = new WorkoutAdapter(workoutList, new WorkoutActionListener() {
            @Override
            public void onMarkDoneClicked(int position) {
                toggleWorkoutCompletion(position);
            }

            @Override
            public void onViewClicked(int position) {
                WorkoutItem item = workoutList.get(position);
                Intent intent = new Intent(requireContext(), WorkoutDetailActivity.class);
                intent.putExtra("WORKOUT_NAME", item.getName());
                intent.putExtra("EXERCISES", (ArrayList<Exercise>) item.getExercises());
                startActivity(intent);
            }
        });
        recyclerWorkouts.setAdapter(workoutAdapter);
    }

    /**
     * Toggles the completion state of a workout and updates Firebase.
     * @param position The position of the workout in the list.
     */
    private void toggleWorkoutCompletion(int position) {
        WorkoutItem item = workoutList.get(position);
        boolean newState = !item.isCompleted();
        item.setCompleted(newState);
        workoutAdapter.notifyItemChanged(position);
        updateProgressUI();

        if (currentProgram != null && currentUserData != null && currentUserData.getCurrentProgramId() != null) {
            try {
                TrainingWeek week = currentProgram.getWeeks().get(currentWeekIndex);
                TrainingDay day = week.getTrainingDays().get(position);
                day.setCompleted(newState);
                
                int completedInWeek = 0;
                for (TrainingDay d : week.getTrainingDays()) {
                    if (d.isCompleted()) completedInWeek++;
                }
                week.setWorkoutsThisWeek(completedInWeek);

                FBRef.refWorkoutPrograms.child(currentUserData.getCurrentProgramId()).setValue(currentProgram);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets up click listeners for fragment buttons.
     * @param view The root view of the fragment.
     */
    private void setupButtons(View view) {
        view.findViewById(R.id.btnViewFullPlan).setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), FullPlanActivity.class));
        });
    }

    /**
     * Updates the weekly progress bars and text views based on completed workouts.
     */
    private void updateProgressUI() {
        int completedCount = 0;
        for (WorkoutItem item : workoutList) if (item.isCompleted()) completedCount++;
        int total = workoutList.size();
        int percent = total == 0 ? 0 : (completedCount * 100) / total;

        tvWeeklyWorkoutsValue.setText(completedCount + "/" + total);
        progressWeekly.setProgress(percent);
        tvCompletionPercent.setText(percent + "% completed");
    }

    /**
     * Internal data class representing a single workout item for display.
     */
    public static class WorkoutItem {
        private String name;
        private String details;
        private boolean completed;
        private List<Exercise> exercises;

        /**
         * Constructs a new WorkoutItem.
         * @param name Name of the workout.
         * @param details Details string (time and exercise count).
         * @param completed Whether it is finished.
         * @param exercises List of associated exercises.
         */
        public WorkoutItem(String name, String details, boolean completed, List<Exercise> exercises) {
            this.name = name;
            this.details = details;
            this.completed = completed;
            this.exercises = exercises;
        }

        public String getName() { return name; }
        public String getDetails() { return details; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
        public List<Exercise> getExercises() { return exercises; }
    }

    /**
     * Interface for handling interactions with workout items in the list.
     */
    interface WorkoutActionListener {
        /**
         * Triggered when the "Mark Done" button is clicked.
         * @param position The position of the item in the list.
         */
        void onMarkDoneClicked(int position);
        /**
         * Triggered when the "View" button is clicked.
         * @param position The position of the item in the list.
         */
        void onViewClicked(int position);
    }

    /**
     * Adapter for the workouts RecyclerView.
     */
    public static class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
        /** The list of workout items to display. */
        private final List<WorkoutItem> items;
        /** Listener for action callbacks. */
        private final WorkoutActionListener listener;

        /**
         * Constructs a new WorkoutAdapter.
         * @param items The list of workout items.
         * @param listener The listener for click events.
         */
        public WorkoutAdapter(List<WorkoutItem> items, WorkoutActionListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout_card, parent, false);
            return new WorkoutViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
            WorkoutItem item = items.get(position);
            holder.tvWorkoutName.setText(item.getName());
            holder.tvWorkoutDetails.setText(item.getDetails());
            if (item.isCompleted()) {
                holder.tvWorkoutStatus.setText("Completed");
                holder.tvWorkoutStatus.setTextColor(0xFF111827);
                holder.btnMarkDone.setText("Undo");
            } else {
                holder.tvWorkoutStatus.setText("Pending");
                holder.tvWorkoutStatus.setTextColor(0xFF6B7280);
                holder.btnMarkDone.setText("Mark Done");
            }
            holder.btnMarkDone.setOnClickListener(v -> listener.onMarkDoneClicked(position));
            holder.btnViewWorkout.setOnClickListener(v -> listener.onViewClicked(position));
        }

        @Override
        public int getItemCount() { return items.size(); }

        /**
         * ViewHolder class for individual workout cards.
         */
        static class WorkoutViewHolder extends RecyclerView.ViewHolder {
            /** TextViews for workout information. */
            TextView tvWorkoutName, tvWorkoutDetails, tvWorkoutStatus;
            /** Buttons for viewing details and marking completion. */
            Button btnViewWorkout, btnMarkDone;

            /**
             * Constructs a new WorkoutViewHolder.
             * @param itemView The view of the workout card.
             */
            public WorkoutViewHolder(@NonNull View itemView) {
                super(itemView);
                tvWorkoutName = itemView.findViewById(R.id.tvWorkoutName);
                tvWorkoutDetails = itemView.findViewById(R.id.tvWorkoutDetails);
                tvWorkoutStatus = itemView.findViewById(R.id.tvWorkoutStatus);
                btnViewWorkout = itemView.findViewById(R.id.btnViewWorkout);
                btnMarkDone = itemView.findViewById(R.id.btnMarkDone);
            }
        }
    }
}
