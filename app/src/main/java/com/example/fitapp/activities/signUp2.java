package com.example.fitapp.activities;

import static com.example.fitapp.classes.FBRef.refAuth;
import static com.example.fitapp.classes.FBRef.refUsers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitapp.R;
import com.example.fitapp.classes.User;

import java.util.HashMap;

/**
 * Activity for the second step of user sign-up.
 * Collects basic user information like username, age, gender, height, and weight.
 */
public class signUp2 extends AppCompatActivity {
    /** EditText for username input. */
    private EditText usernameInput;
    /** EditText for age input. */
    private EditText ageInput;
    /** EditText for height input. */
    private EditText heightInput;
    /** EditText for weight input. */
    private EditText weightInput;
    /** RadioGroup for gender selection. */
    private RadioGroup genderGroup;

    /**
     * Initializes the activity, sets the content view, and binds the UI components.
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}. Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        usernameInput = findViewById(R.id.usernameInput);
        ageInput = findViewById(R.id.ageInput);
        genderGroup = findViewById(R.id.genderGroup);
        heightInput = findViewById(R.id.heightInput);
        weightInput = findViewById(R.id.weightInput);
    }

    /**
     * Handles the "Continue" button click. Validates input, creates a User object,
     * saves it to Firebase Realtime Database, and navigates to the next sign-up step.
     * @param view The view that was clicked (the continue button).
     */
    public void clickedContinueBtn(View view) {
        String username = usernameInput.getText().toString();
        String ageStr = ageInput.getText().toString();
        String heightStr = heightInput.getText().toString();
        String weightStr = weightInput.getText().toString();

        if (username.isEmpty() || ageStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);
        double height = Double.parseDouble(heightStr);
        double weight = Double.parseDouble(weightStr);
        
        // true for male, false for female
        boolean gender = (genderGroup.getCheckedRadioButtonId() == R.id.maleBtn);

        String userId = refAuth.getCurrentUser().getUid();
        
        User user = new User(userId, username, age, gender, height, weight, new HashMap<>(), 0, new HashMap<>(), 0);
        
        refUsers.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent si = new Intent(signUp2.this, signUp3.class);
                startActivity(si);
            } else {
                Toast.makeText(signUp2.this, "Failed to save user info", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
