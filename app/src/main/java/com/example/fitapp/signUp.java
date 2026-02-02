package com.example.fitapp;

import static com.example.fitapp.FBRef.refAuth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

public class signUp extends AppCompatActivity {
    private EditText eTEmail, eTPass;
    private TextView tVMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        eTEmail = findViewById(R.id.emailInput);
        eTPass = findViewById(R.id.passwordInput);
        tVMsg = findViewById(R.id.tVMsg);
    }

    public void createUser(View view) {
        String email = eTEmail.getText().toString();
        String pass = eTPass.getText().toString();

        if (email.isEmpty() || pass.isEmpty()) {
            tVMsg.setText("Please fill all fields");
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

                            if (task.isSuccessful()) {
                                Log.i("MainActivity", "createUserWithEmailAndPassword:success");
                                FirebaseUser user = refAuth.getCurrentUser();
                                tVMsg.setText("User created successfully\nUid: " + user.getUid());
                            } else {
                                Exception exp = task.getException();

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
                    });
        }
    }

    public void goToLogin(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}

