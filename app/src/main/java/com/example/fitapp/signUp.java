package com.example.fitapp;

import static com.example.fitapp.FBRef.refAuth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity for user sign-up.
 * Handles the creation of a new Firebase Authentication user account.
 */
public class signUp extends AppCompatActivity {
    /** EditText for email input. */
    private EditText eTEmail;
    /** EditText for password input. */
    private EditText eTPass;
    /** TextView for displaying messages and error feedback. */
    private TextView tVMsg;

    /**
     * Initializes the activity, sets the content view, and binds the UI components.
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}. Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        eTEmail = findViewById(R.id.emailInput);
        eTPass = findViewById(R.id.passwordInput);
        tVMsg = findViewById(R.id.msg);
    }

    /**
     * Attempts to create a new user account with the provided email and password.
     * Uses Firebase Authentication.
     * @param view The view that was clicked (the sign-up button).
     */
    public void createUser(View view) {
        String email = eTEmail.getText().toString();
        String pass = eTPass.getText().toString();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
        } else {
            ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Connecting");
            pd.setMessage("Creating user...");
            pd.show();

            refAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if (isFinishing() || isDestroyed()) {
                                return;
                            }

                            if (task.isSuccessful()) {
                                Log.i("MainActivity", "createUserWithEmailAndPassword:success");
                                
                                Intent intent = new Intent(signUp.this, signUp2.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Exception exp = task.getException();
                                if (tVMsg != null) {
                                    if (exp instanceof FirebaseAuthInvalidUserException) {
                                        tVMsg.setText("Invalid email address.");
                                    } else if (exp instanceof FirebaseAuthWeakPasswordException) {
                                        tVMsg.setText("Password too weak.");
                                    } else if (exp instanceof FirebaseAuthUserCollisionException) {
                                        tVMsg.setText("User already exists.");
                                    } else if (exp instanceof FirebaseAuthInvalidCredentialsException) {
                                        tVMsg.setText("General authentication failure.");
                                    } else if (exp instanceof FirebaseNetworkException) {
                                        tVMsg.setText("Network error. Please check your connection.");
                                    } else {
                                        tVMsg.setText("An error occurred. Please try again later.");
                                    }
                                }
                            }
                        }
                    });
        }
    }

    /**
     * Navigates the user back to the login activity.
     * @param view The view that was clicked (the go-to-login button).
     */
    public void goToLogin(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}
