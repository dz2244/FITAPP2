package com.example.fitapp;

import static com.example.fitapp.FBRef.refAuth;
import static com.example.fitapp.FBRef.refUsers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class signUp2 extends AppCompatActivity {
    private EditText usernameInput, ageInput, heightInput, weightInput;
    private RadioGroup genderGroup;

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
