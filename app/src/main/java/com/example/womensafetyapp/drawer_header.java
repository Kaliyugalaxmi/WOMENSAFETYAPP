package com.example.womensafetyapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class drawer_header extends AppCompatActivity {

    FirebaseUser user;
    String UserID;
    DatabaseReference reference;
    TextView user_name, emailID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_drawer_header);

        user_name = findViewById(R.id.user_name);
        emailID = findViewById(R.id.emailID);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            Log.e("drawer_header", "User is null");
            return;
        }

        UserID = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("username", "User");
        String savedEmail = sharedPreferences.getString("email", "user@example.com");

        user_name.setText(savedUsername);
        emailID.setText(savedEmail);

        reference.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User userDetails = dataSnapshot.getValue(User.class);

                    if (userDetails != null) {
                        String name = userDetails.name;
                        String email = userDetails.email;

                        user_name.setText(name);
                        emailID.setText(email);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", name);
                        editor.putString("email", email);
                        editor.apply();

                        Log.d("drawer_header", "User data fetched and saved successfully.");
                    }
                } else {
                    Toast.makeText(drawer_header.this, "User data not found in Firebase", Toast.LENGTH_SHORT).show();
                    Log.e("drawer_header", "DataSnapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(drawer_header.this, "Failed to read user data", Toast.LENGTH_SHORT).show();
                Log.e("drawer_header", "Database error: " + databaseError.getMessage());
            }
        });
    }
}
