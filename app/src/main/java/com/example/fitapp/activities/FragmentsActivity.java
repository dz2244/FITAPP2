package com.example.fitapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.fitapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Main activity that hosts the fragments of the application.
 * Manages fragment transactions and monitors network connectivity across all fragments.
 */
public class FragmentsActivity extends AppCompatActivity {

    private AlertDialog connectivityDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnCredits = findViewById(R.id.btn_credits);
        btnCredits.setOnClickListener(v -> {
            Intent intent = new Intent(FragmentsActivity.this, Credits.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();
            
            if (itemId == R.id.navigation_training) {
                fragment = new TrainingFragment();
            } else if (itemId == R.id.navigation_calorie_tracking) {
                fragment = new CalorieTrackingFragment();
            } else if (itemId == R.id.navigation_sleep_tracking) {
                fragment = new SleepTrackingFragment();
            } else if (itemId == R.id.navigation_knowledge) {
                fragment = new KnowledgeFragment();
            }

            return loadFragment(fragment);
        });

        if (savedInstanceState == null) {
            loadFragment(new TrainingFragment());
            bottomNavigationView.setSelectedItemId(R.id.navigation_training);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Register BroadcastReceiver for connectivity changes
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        checkConnectivity(); // Initial check when activity starts
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unregister to avoid memory leaks
        unregisterReceiver(networkReceiver);
    }

    /**
     * BroadcastReceiver to monitor network connectivity changes.
     */
    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkConnectivity();
        }
    };

    /**
     * Checks if the device has an active internet connection.
     * Shows an AlertDialog if no connection (cellular or Wi-Fi) is detected.
     */
    private void checkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;

        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                // Check if there's any active internet connection
                isConnected = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        }

        if (!isConnected) {
            showNoConnectionDialog("No Connection", "No celular connection or internet available. Please check your network settings.");
        } else {
            // If connected, hide the dialog if it's currently showing
            if (connectivityDialog != null && connectivityDialog.isShowing()) {
                connectivityDialog.dismiss();
            }
        }
    }

    /**
     * Displays an AlertDialog when there is a connectivity issue.
     */
    private void showNoConnectionDialog(String title, String message) {
        if (connectivityDialog != null && connectivityDialog.isShowing()) return;

        connectivityDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false) // User must click OK to acknowledge
                .create();
        connectivityDialog.show();
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
