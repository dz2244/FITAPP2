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

public class SleepHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerSleepWeeks;
    private SleepWeekAdapter adapter;
    private List<SleepWeekData> sleepWeeks = new ArrayList<>();

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

    private void processSessionsIntoWeeks(List<SleepSession> sessions) {
        sleepWeeks.clear();
        for (int i = 0; i < 12; i++) {
            sleepWeeks.add(new SleepWeekData(i + 1));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        
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

    private static class SleepWeekData {
        int weekNumber;
        int daysTracked = 0;
        double totalHours = 0;

        SleepWeekData(int weekNumber) {
            this.weekNumber = weekNumber;
        }

        void addSession(double hours) {
            daysTracked++;
            totalHours += hours;
        }

        double getAvgHours() {
            return daysTracked == 0 ? 0 : totalHours / daysTracked;
        }
    }

    private static class SleepWeekAdapter extends RecyclerView.Adapter<SleepWeekAdapter.ViewHolder> {
        private List<SleepWeekData> data;

        SleepWeekAdapter(List<SleepWeekData> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sleep_week, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SleepWeekData week = data.get(position);
            holder.tvWeekNumber.setText("Week " + week.weekNumber);
            holder.tvStatus.setText(week.daysTracked + "/7 Days tracked");
            holder.tvAvg.setText(String.format(Locale.getDefault(), "Avg: %.1fh", week.getAvgHours()));
            
            int progress = (week.daysTracked * 100) / 7;
            holder.pbProgress.setProgress(progress);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvWeekNumber, tvStatus, tvAvg;
            ProgressBar pbProgress;

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
