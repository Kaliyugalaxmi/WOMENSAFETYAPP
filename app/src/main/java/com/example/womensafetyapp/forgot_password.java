package com.example.womensafetyapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class forgot_password extends AppCompatActivity {
    private EditText emailEditText;
    private Button sendMailButton;
    private TextView login;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        // Initialize UI element

            // Initialize UI elements
            emailEditText = findViewById(R.id.email);
            sendMailButton = findViewById(R.id.sendMail);
            login = findViewById(R.id.login);
            auth = FirebaseAuth.getInstance();

            // Forgot Password Logic
            sendMailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = emailEditText.getText().toString().trim();

                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(forgot_password.this, "Enter your email", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(forgot_password.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(forgot_password.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });

            // Navigate Back to Login Page
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(forgot_password.this, login.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }