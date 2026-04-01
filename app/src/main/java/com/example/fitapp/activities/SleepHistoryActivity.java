package com.example.fitapp.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitapp.R;
import com.example.fitapp.classes.FBRef;
import com.example.fitapp.classes.SleepSession;
import com.example.fitapp.classes.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity that displays the user's sleep history organized by weeks.
 * It fetches sleep sessions from Firebase Realtime Database and aggregates them into weekly data.
 */
public class SleepHistoryActivity extends AppCompatActivity {

    /** RecyclerView to display the list of weekly sleep data. */
    private RecyclerView recyclerSleepWeeks;
    /** Adapter for the sleep weeks RecyclerView. */
    private SleepWeekAdapter adapter;
    /** List of sleep week data to be displayed. */
    private final List<SleepWeekData> sleepWeeks = new ArrayList<>();

    /**
     * Initializes the activity, sets up the RecyclerView and back button.
     * Triggers the fetching of sleep sessions from Firebase.
     *
     * @param savedInstanceState Bundle containing activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_history);

        recyclerSleepWeeks = findViewById(R.id.recyclerSleepWeeks);
        findViewById(R.id.btnBackFromSleepHistory).setOnClickListener(v -> finish());

        recyclerSleepWeeks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SleepWeekAdapter(sleepWeeks);
        recyclerSleepWeeks.setAdapter(adapter);

        fetchSleepSessions();
    }

    /**
     * Fetches sleep sessions for the currently authenticated user from Firebase Realtime Database.
     */
    private void fetchSleepSessions() {
        FirebaseUser user = FBRef.refAuth.getCurrentUser();
        if (user == null) return;

        FBRef.refSleepSessions.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<SleepSession> sessions = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    SleepSession s = ds.getValue(SleepSession.class);
                    if (s != null) sessions.add(s);
                }
                processSessionsIntoWeeks(sessions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * Processes individual sleep sessions into weekly aggregates.
     *
     * @param sessions The list of raw sleep sessions fetched from the database.
     */
    private void processSessionsIntoWeeks(List<SleepSession> sessions) {
        sleepWeeks.clear();
        for (int i = 0; i < 12; i++) {
            sleepWeeks.add(new SleepWeekData(i + 1));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        // Find the registration date to align weeks, or just use the earliest session
        long baseTime = System.currentTimeMillis();
        if (!sessions.isEmpty()) {
            try {
                Date d = sdf.parse(sessions.get(0).getDate());
                if (d != null) baseTime = d.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        for (SleepSession session : sessions) {
            try {
                Date sessionDate = sdf.parse(session.getDate());
                if (sessionDate != null) {
                    long diff = sessionDate.getTime() - baseTime;
                    int weekIndex = (int) (diff / (1000 * 60 * 60 * 24 * 7));
                    if (weekIndex >= 0 && weekIndex < 12) {
                        SleepWeekData week = sleepWeeks.get(weekIndex);
                        week.addSession(session.getSleepTime());
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * Helper class to store aggregated sleep data for a single week.
     */
    private static class SleepWeekData {
        /** The week number (e.g., 1, 2, ...). */
        int weekNumber;
        /** Number of days in this week that have sleep logs. */
        int daysTracked = 0;
        /** Total hours of sleep logged in this week. */
        double totalHours = 0;

        /**
         * Constructor for SleepWeekData.
         *
         * @param weekNumber The number of the week.
         */
        SleepWeekData(int weekNumber) {
            this.weekNumber = weekNumber;
        }

        /**
         * Adds a sleep session's duration to the weekly totals.
         *
         * @param hours The number of hours slept in a session.
         */
        void addSession(double hours) {
            daysTracked++;
            totalHours += hours;
        }

        /**
         * Calculates the average sleep hours for the days tracked in this week.
         *
         * @return Average sleep hours.
         */
        double getAvgHours() {
            return daysTracked == 0 ? 0 : totalHours / daysTracked;
        }
    }

    /**
     * RecyclerView Adapter for displaying {@link SleepWeekData} items.
     */
    private static class SleepWeekAdapter extends RecyclerView.Adapter<SleepWeekAdapter.ViewHolder> {
        /** The list of weekly sleep data to be displayed. */
        private final List<SleepWeekData> data;

        /**
         * Constructor for SleepWeekAdapter.
         *
         * @param data The list of weekly sleep data.
         */
        SleepWeekAdapter(List<SleepWeekData> data) {
            this.data = data;
        }

        /**
         * Inflates the item layout and creates a new {@link ViewHolder}.
         *
         * @param parent   The parent ViewGroup.
         * @param viewType The view type of the new View.
         * @return A new ViewHolder instance.
         */
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sleep_week, parent, false);
            return new ViewHolder(view);
        }

        /**
         * Binds the weekly sleep data to the ViewHolder and updates UI elements.
         *
         * @param holder   The ViewHolder to update.
         * @param position The position of the item in the data set.
         */
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SleepWeekData week = data.get(position);
            holder.tvWeekNumber.setText("Week " + week.weekNumber);
            holder.tvStatus.setText(week.daysTracked + "/7 Days tracked");
            holder.tvAvg.setText(String.format(Locale.getDefault(), "Avg: %.1fh", week.getAvgHours()));
            
            int progress = (week.daysTracked * 100) / 7;
            holder.pbProgress.setProgress(progress);
        }

        /**
         * Returns the total number of items in the data set.
         *
         * @return The number of weekly data entries.
         */
        @Override
        public int getItemCount() {
            return data.size();
        }

        /**
         * ViewHolder for a sleep week item, holding references to the UI components.
         */
        static class ViewHolder extends RecyclerView.ViewHolder {
            /** TextViews for week number, tracking status, and average sleep. */
            TextView tvWeekNumber, tvStatus, tvAvg;
            /** ProgressBar showing the portion of the week tracked. */
            ProgressBar pbProgress;

            /**
             * Constructor for ViewHolder.
             *
             * @param itemView The view of the sleep week item.
             */
            ViewHolder(View itemView) {
                super(itemView);
                tvWeekNumber = itemView.findViewById(R.id.tvSleepWeekNumber);
                tvStatus = itemView.findViewById(R.id.tvSleepWeekStatus);
                tvAvg = itemView.findViewById(R.id.tvAvgSleepHours);
                pbProgress = itemView.findViewById(R.id.pbSleepWeekProgress);
            }
        }
    }
}
