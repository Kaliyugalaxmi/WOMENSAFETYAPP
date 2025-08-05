package com.example.womensafetyapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class profilePage extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail, tvUserMobile;
    private Spinner spinnerAgeGroup, spinnerLocation;
    private DatabaseReference mDatabase;
    private Button btnSaveProfile;
    private ArrayAdapter<String> ageGroupAdapter, locationAdapter;
    private String userId;  // Declare userId as a global variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserMobile = findViewById(R.id.tvUserMobile);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        // Initialize Spinners
        spinnerAgeGroup = findViewById(R.id.spinnerAgeGroup);
        spinnerLocation = findViewById(R.id.spinnerLocation);

        // Age Group Spinner
        String[] ageGroups = {"18-25", "26-35", "36-45", "46-60", "60+"};
        ageGroupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ageGroups);
        ageGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAgeGroup.setAdapter(ageGroupAdapter);

        // Location Spinner
        String[] locations = {"Mumbai", "Pune", "Nagpur", "Nashik", "Aurangabad", "Solapur", "Kolhapur", "Thane", "Kalyan", "Sangli"};
        locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationAdapter);

        // Firebase instance
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get the current user ID
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            Toast.makeText(this, "User not logged in. Please log in first.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch user details from Firebase
        mDatabase.child("Users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("name").getValue(String.class);
                    String userEmail = dataSnapshot.child("email").getValue(String.class);
                    String userMobile = dataSnapshot.child("number").getValue(String.class);
                    String userAgeGroup = dataSnapshot.child("ageGroup").getValue(String.class);
                    String userLocation = dataSnapshot.child("location").getValue(String.class);

                    tvUserName.setText(userName);
                    tvUserEmail.setText(userEmail);
                    tvUserMobile.setText(userMobile);

                    if (userAgeGroup != null) {
                        int ageIndex = ageGroupAdapter.getPosition(userAgeGroup);
                        if (ageIndex >= 0) spinnerAgeGroup.setSelection(ageIndex);
                    }
                    if (userLocation != null) {
                        int locationIndex = locationAdapter.getPosition(userLocation);
                        if (locationIndex >= 0) spinnerLocation.setSelection(locationIndex);
                    }
                } else {
                    Log.d("ProfilePage", "No data found for this user");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Error fetching data: " + databaseError.getMessage());
            }
        });

        // Save Button Action
        btnSaveProfile.setOnClickListener(v -> {
            if (userId != null) {
                String selectedAgeGroup = spinnerAgeGroup.getSelectedItem().toString();
                String selectedLocation = spinnerLocation.getSelectedItem().toString();

                Map<String, Object> updates = new HashMap<>();
                updates.put("ageGroup", selectedAgeGroup);
                updates.put("location", selectedLocation);

                mDatabase.child("Users").child(userId).updateChildren(updates)
                        .addOnSuccessListener(aVoid -> Toast.makeText(profilePage.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(profilePage.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}
