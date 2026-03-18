package com.example.fitapp;

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

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fragment for training management.
 * Displays a list of workouts and tracks weekly progress.
 */
public class TrainingFragment extends Fragment {

    /** RecyclerView for displaying the list of workouts. */
    private RecyclerView recyclerWorkouts;
    /** ProgressBar for showing weekly workout progress. */
    private ProgressBar progressWeekly;
    /** TextView for the weekly workouts count. */
    private TextView tvWeeklyWorkoutsValue;
    /** TextView for the completion percentage. */
    private TextView tvCompletionPercent;

    /** TextViews for user profile data. */
    private TextView tvPlanName, tvPlanGoal, tvPlanFrequency, tvPlanDuration;

    /** List of workout items. */
    private final List<WorkoutItem> workoutList = new ArrayList<>();
    /** Adapter for the workout RecyclerView. */
    private WorkoutAdapter workoutAdapter;

    /**
     * Default constructor for the TrainingFragment.
     */
    public TrainingFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the layout for the TrainingFragment.
     * @param inflater The LayoutInflater object to inflate the view.
     * @param container If non-null, this is the parent view.
     * @param savedInstanceState Bundle containing fragment state.
     * @return The inflated view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training, container, false);
    }

    /**
     * Initializes views and sets up the workout data after the view is created.
     * @param view The fragment's root view.
     * @param savedInstanceState Bundle containing fragment state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        fetchUserData();
        setupWorkoutData();
        setupRecyclerView();
        setupButtons(view);
        updateProgressUI();
    }

    /**
     * Finds and initializes UI components from the inflated view.
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
    }

    /**
     * Fetches the current user's profile data from Firebase Realtime Database.
     */
    private void fetchUserData() {
        FirebaseUser currentUser = FBRef.refAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            FBRef.refUsers.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        updateProfileUI(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Updates the UI with data from the User object.
     * @param user The user profile data.
     */
    private void updateProfileUI(User user) {
        if (tvPlanName != null) {
            tvPlanName.setText(user.getUsername() + "'s Plan");
        }

        if (tvPlanGoal != null && user.getGoals() != null) {
            StringBuilder goalsStr = new StringBuilder("Goals: ");
            for (Map.Entry<String, Integer> entry : user.getGoals().entrySet()) {
                goalsStr.append(entry.getKey()).append(" ");
            }
            tvPlanGoal.setText(goalsStr.toString().trim());
        }

        if (tvPlanFrequency != null) {
            tvPlanFrequency.setText("Workouts per week: " + user.getWorkoutsPerWeek());
        }

        // Duration is not directly in User, so we keep the default or hide it if needed.
        // For now, let's just show age/weight as part of the info.
        if (tvPlanDuration != null) {
            tvPlanDuration.setText("Weight: " + user.getWeight() + "kg | Age: " + user.getAge());
        }
    }

    /**
     * Sets up mock workout data for display.
     */
    private void setupWorkoutData() {
        workoutList.clear();

        workoutList.add(new WorkoutItem("Workout A - Chest & Triceps", "5 exercises • 45 min", false));
        workoutList.add(new WorkoutItem("Workout B - Back & Biceps", "6 exercises • 50 min", true));
        workoutList.add(new WorkoutItem("Workout C - Legs", "5 exercises • 55 min", false));
        workoutList.add(new WorkoutItem("Workout D - Shoulders & Core", "4 exercises • 40 min", true));
    }

    /**
     * Configures the RecyclerView with a layout manager and adapter.
     */
    private void setupRecyclerView() {
        recyclerWorkouts.setLayoutManager(new LinearLayoutManager(requireContext()));
        workoutAdapter = new WorkoutAdapter(workoutList, new WorkoutActionListener() {
            @Override
            public void onMarkDoneClicked(int position) {
                WorkoutItem item = workoutList.get(position);
                item.setCompleted(!item.isCompleted());
                workoutAdapter.notifyItemChanged(position);
                updateProgressUI();
            }

            @Override
            public void onViewClicked(int position) {
                WorkoutItem item = workoutList.get(position);
                Toast.makeText(requireContext(),
                        "Open details for: " + item.getName(),
                        Toast.LENGTH_SHORT).show();

                // Later you can open WorkoutDetailsFragment here
            }
        });

        recyclerWorkouts.setAdapter(workoutAdapter);
    }

    /**
     * Sets up click listeners for the fragment's buttons.
     * @param view The root view of the fragment.
     */
    private void setupButtons(View view) {
        Button btnViewFullPlan = view.findViewById(R.id.btnViewFullPlan);
        btnViewFullPlan.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Open full training plan", Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * Updates the progress bar and completion text based on the workout list.
     */
    private void updateProgressUI() {
        int completedCount = 0;
        for (WorkoutItem item : workoutList) {
            if (item.isCompleted()) {
                completedCount++;
            }
        }

        int total = workoutList.size();
        int percent = total == 0 ? 0 : (completedCount * 100) / total;

        tvWeeklyWorkoutsValue.setText(completedCount + "/" + total);
        progressWeekly.setProgress(percent);
        tvCompletionPercent.setText(percent + "% completed");
    }

    /**
     * Internal model for a workout item.
     */
    public static class WorkoutItem {
        private String name;
        private String details;
        private boolean completed;

        public WorkoutItem(String name, String details, boolean completed) {
            this.name = name;
            this.details = details;
            this.completed = completed;
        }

        public String getName() {
            return name;
        }

        public String getDetails() {
            return details;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }

    /**
     * Interface for handling actions on workout items.
     */
    interface WorkoutActionListener {
        void onMarkDoneClicked(int position);
        void onViewClicked(int position);
    }

    /**
     * Adapter for the workout items in the RecyclerView.
     */
    public static class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

        private final List<WorkoutItem> items;
        private final WorkoutActionListener listener;

        public WorkoutAdapter(List<WorkoutItem> items, WorkoutActionListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_workout_card, parent, false);
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
        public int getItemCount() {
            return items.size();
        }

        static class WorkoutViewHolder extends RecyclerView.ViewHolder {
            TextView tvWorkoutName, tvWorkoutDetails, tvWorkoutStatus;
            Button btnViewWorkout, btnMarkDone;

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
