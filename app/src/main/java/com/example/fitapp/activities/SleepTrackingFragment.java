package com.example.fitapp.activities;

import static com.example.fitapp.classes.FBRef.refAuth;
import static com.example.fitapp.classes.FBRef.refSleepSessions;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.fitapp.R;
import com.example.fitapp.classes.SleepSession;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Fragment dedicated to tracking user sleep patterns.
 * Allows users to log sleep duration and quality to monitor recovery.
 */
public class SleepTrackingFragment extends Fragment {

    private TextView tvRecoveryValue, tvRecoveryStatus, tvSleepHours, tvQualityText;
    private ProgressBar pbRecovery;
    private MaterialButton btnLogSleep;
    private View btnViewSleepHistory;

    private Calendar sleepStartTime = Calendar.getInstance();
    private Calendar sleepEndTime = Calendar.getInstance();
    private boolean wokeUpInMiddle = false;

    public SleepTrackingFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep_tracking, container, false);

        // Initialize Views
        tvRecoveryValue = view.findViewById(R.id.tvRecoveryValue);
        tvRecoveryStatus = view.findViewById(R.id.tvRecoveryStatus);
        tvSleepHours = view.findViewById(R.id.tvSleepHours);
        tvQualityText = view.findViewById(R.id.tvQualityText);
        pbRecovery = view.findViewById(R.id.pbRecovery);
        btnLogSleep = view.findViewById(R.id.btnAddSleep);
        btnViewSleepHistory = view.findViewById(R.id.btnViewSleepHistory);

        btnLogSleep.setOnClickListener(v -> showSleepLogDialog());
        
        btnViewSleepHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SleepHistoryActivity.class);
            startActivity(intent);
        });

        fetchSleepData();

        return view;
    }

    private void fetchSleepData() {
        if (refAuth.getCurrentUser() == null) return;
        
        String userId = refAuth.getCurrentUser().getUid();
        refSleepSessions.child(userId).orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        SleepSession lastSession = ds.getValue(SleepSession.class);
                        if (lastSession != null) {
                            updateUI(lastSession);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUI(SleepSession session) {
        double hours = session.getSleepTime();
        int h = (int) hours;
        int m = (int) ((hours - h) * 60);
        
        tvSleepHours.setText(String.format(Locale.getDefault(), "%dh %02dm", h, m));
        
        // Recovery calculation: 8 hours = 100%, subtract 10% if woke up in middle
        double recoveryScoreDouble = (hours / 8.0) * 100;
        if (session.isWokeUpInMiddle()) {
            recoveryScoreDouble -= 10;
        }
        int recoveryScore = (int) Math.max(0, Math.min(recoveryScoreDouble, 100));
        
        tvRecoveryValue.setText(recoveryScore + "%");
        pbRecovery.setProgress(recoveryScore);

        if (recoveryScore >= 90) {
            tvRecoveryStatus.setText("Peak Recovery");
            tvQualityText.setText("Deep");
        } else if (recoveryScore >= 75) {
            tvRecoveryStatus.setText("Primed for Training");
            tvQualityText.setText("Good");
        } else if (recoveryScore >= 50) {
            tvRecoveryStatus.setText("Moderate Recovery");
            tvQualityText.setText("Fair");
        } else {
            tvRecoveryStatus.setText("Needs More Rest");
            tvQualityText.setText("Poor");
        }
    }

    private void showSleepLogDialog() {
        // Step 1: When did you go to bed?
        new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
            sleepStartTime = Calendar.getInstance();
            // If the time is early (e.g. 10 PM), it's probably for "last night"
            // If user logs at 8 AM, and says bedtime was 11 PM, it's yesterday's 11 PM.
            if (hourOfDay > 12) {
                sleepStartTime.add(Calendar.DAY_OF_YEAR, -1);
            }
            sleepStartTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            sleepStartTime.set(Calendar.MINUTE, minute);
            
            pickEndTime();
            
        }, 22, 0, false).show();
    }

    private void pickEndTime() {
        // Step 2: When did you wake up?
        new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            sleepEndTime = Calendar.getInstance(); // Usually "now" or just before
            sleepEndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            sleepEndTime.set(Calendar.MINUTE, minute);

            // If end time is before start time, it means we crossed midnight
            if (sleepEndTime.before(sleepStartTime)) {
                sleepEndTime.add(Calendar.DAY_OF_YEAR, 1);
            }

            askWokeUpInMiddle();

        }, 7, 0, false).show();
    }

    private void askWokeUpInMiddle() {
        // Step 3: Did you wake up in the middle?
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sleep Quality");
        builder.setMessage("Did you wake up in the middle of the night?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            wokeUpInMiddle = true;
            saveSleepSession();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            wokeUpInMiddle = false;
            saveSleepSession();
        });
        builder.show();
    }

    private void saveSleepSession() {
        long diffInMillis = sleepEndTime.getTimeInMillis() - sleepStartTime.getTimeInMillis();
        double hours = diffInMillis / (1000.0 * 60 * 60);

        if (hours <= 0) {
            Toast.makeText(getContext(), "Invalid sleep duration", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = refAuth.getCurrentUser().getUid();
        String sessionId = refSleepSessions.child(userId).push().getKey();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(sleepStartTime.getTime());

        SleepSession session = new SleepSession(sessionId, date, hours, wokeUpInMiddle);

        if (sessionId != null) {
            refSleepSessions.child(userId).child(sessionId).setValue(session)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Sleep logged!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to log sleep", Toast.LENGTH_SHORT).show());
        }
    }
}
