package com.example.womensafetyapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PoliceLogin extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView forgotPassword, signupText;
    private FirebaseAuth mAuth;
    private DatabaseReference policeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        signupText = findViewById(R.id.signupText);

        mAuth = FirebaseAuth.getInstance();
        policeRef = FirebaseDatabase.getInstance().getReference("PoliceUsers");

        // Login Button Click Event
        loginButton.setOnClickListener(v -> loginPolice());

        // Redirect to Signup Page
        signupText.setOnClickListener(v -> startActivity(new Intent(PoliceLogin.this, PoliceSignup.class)));

        // Forgot Password Click Event
        forgotPassword.setOnClickListener(v -> startActivity(new Intent(PoliceLogin.this, PoliceForgotPassword.class)));
    }

    private void loginPolice() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Enter your email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Enter your password");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            String userId = currentUser.getUid();

                            // Check if the user exists in the PoliceUsers node
                            policeRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        Toast.makeText(PoliceLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(PoliceLogin.this, PoliceDashboard.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(PoliceLogin.this, "Access Denied. You are not a registered Police User.", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut(); // Sign out if not a registered police user
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(PoliceLogin.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(PoliceLogin.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
