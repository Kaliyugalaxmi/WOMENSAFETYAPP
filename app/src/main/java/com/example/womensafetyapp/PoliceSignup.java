package com.example.womensafetyapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PoliceSignup extends AppCompatActivity {
    private EditText name, email, password, number;
    private Button btnSignup;
    private TextView tvLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference policeDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_signup);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        policeDatabase = FirebaseDatabase.getInstance().getReference("PoliceUsers");

        // Initialize Views
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        number = findViewById(R.id.number);
        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);

        // Signup Button Click Listener
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = name.getText().toString().trim();
                String userEmail = email.getText().toString().trim();
                String userPassword = password.getText().toString().trim();
                String userNumber = number.getText().toString().trim();

                if (TextUtils.isEmpty(userName)) {
                    name.setError("Please enter your name");
                    return;
                }
                if (TextUtils.isEmpty(userEmail)) {
                    email.setError("Please enter your email");
                    return;
                }
                if (TextUtils.isEmpty(userPassword)) {
                    password.setError("Please enter your password");
                    return;
                }
                if (userPassword.length() < 6) {
                    password.setError("Password must be at least 6 characters");
                    return;
                }
                if (TextUtils.isEmpty(userNumber)) {
                    number.setError("Please enter your mobile number");
                    return;
                }

                // Register User with Firebase Authentication
                mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Store User Details in Firebase Realtime Database
                                    String userId = mAuth.getCurrentUser().getUid();
                                    HashMap<String, Object> userMap = new HashMap<>();
                                    userMap.put("userId", userId);
                                    userMap.put("name", userName);
                                    userMap.put("email", userEmail);
                                    userMap.put("number", userNumber);

                                    policeDatabase.child(userId).setValue(userMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(PoliceSignup.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(PoliceSignup.this, PoliceDashboard.class)); // Redirect to Police Dashboard
                                                        finish();
                                                    } else {
                                                        Toast.makeText(PoliceSignup.this, "Database Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(PoliceSignup.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // Redirect to Login Page
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PoliceSignup.this, PoliceLogin.class));  // Redirect to Police Login page
            }
        });
    }
}
