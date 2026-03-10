package com.example.fitapp;

import static com.example.fitapp.FBRef.refAuth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Main activity that hosts the fragments of the application.
 * Manages fragment transactions based on BottomNavigationView selection.
 */
public class FragmentsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    /**
     * Initializes the activity, sets up the window insets, and configures the bottom navigation.
     * Loads the initial fragment.
     * @param savedInstanceState Bundle containing activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        loadFragment(new CalorieTrackingFragment());
    }

    /**
     * Replaces the current fragment in the container with a new one.
     * @param fragment The fragment to be loaded.
     * @return true if the fragment was successfully replaced, false otherwise.
     */
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    /**
     * Handles navigation item selection from the BottomNavigationView.
     * Maps menu items to their corresponding fragments or triggers logout.
     * @param item The selected menu item.
     * @return true if the item was handled.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_calorie_tracking) {
            fragment = new CalorieTrackingFragment();
        } else if (itemId == R.id.navigation_sleep_tracking) {
            fragment = new SleepTrackingFragment();
        } else if (itemId == R.id.navigation_training) {
            fragment = new TrainingFragment();
        } else if (itemId == R.id.navigation_knowledge) {
            fragment = new KnowledgeFragment();
        } else if (itemId == R.id.navigation_logout) {
            logout();
            return true;
        }
        return loadFragment(fragment);
    }

    /**
     * Signs out the user from Firebase, clears "Remember Me" preference,
     * and redirects to the Login screen.
     */
    private void logout() {
        refAuth.signOut();
        SharedPreferences settings = getSharedPreferences("RemeberMe", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("stayConnect", false);
        editor.apply();

        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
