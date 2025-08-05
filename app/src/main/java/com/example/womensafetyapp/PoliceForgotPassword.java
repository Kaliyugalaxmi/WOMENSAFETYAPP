package com.example.womensafetyapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class PoliceForgotPassword extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton;
    private TextView backToLoginText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_forgot_password);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        emailEditText = findViewById(R.id.email);
        resetPasswordButton = findViewById(R.id.btnResetPassword);
        backToLoginText = findViewById(R.id.tvBackToLogin);

        // Handle Reset Password button click
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(PoliceForgotPassword.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Send password reset email
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PoliceForgotPassword.this, "Reset instructions sent to your email", Toast.LENGTH_LONG).show();
                    } else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(PoliceForgotPassword.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // Handle Back to Login click
        backToLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to Police Login page
                Intent intent = new Intent(PoliceForgotPassword.this, PoliceLogin.class);
                startActivity(intent);
                finish();
            }
        });
    }
}