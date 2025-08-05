package com.example.womensafetyapp;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private EditText name, email, password, number;
    private Button signup;
    private TextView login;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        number = findViewById(R.id.number);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);

        // Check if user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(signup.this, Dashboard.class));
            finish();
        }

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup.this, login.class));
                finish();
            }
        });
    }

    private void registerUser() {
        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String userNumber = number.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            name.setError("Enter your name");
            return;
        }
        if (TextUtils.isEmpty(userEmail)) {
            email.setError("Enter your email");
            return;
        }
        if (TextUtils.isEmpty(userPassword)) {
            password.setError("Enter your password");
            return;
        }
        if (userPassword.length() < 6) {
            password.setError("Password must be at least 6 characters");
            return;
        }
        if (TextUtils.isEmpty(userNumber)) {
            number.setError("Enter your mobile number");
            return;
        }
        if (userNumber.length() != 10) {
            number.setError("Enter a valid 10-digit mobile number");
            return;
        }

        // Register user in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            // ✅ Creating and saving User object using separate User.java class
                            User userData = new User(userName, userEmail, userNumber);
                            reference.setValue(userData).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(signup.this, "Signup Successful!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(signup.this, ContactPicker.class));
                                    finish();
                                } else {
                                    Toast.makeText(signup.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(signup.this, "Signup Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
