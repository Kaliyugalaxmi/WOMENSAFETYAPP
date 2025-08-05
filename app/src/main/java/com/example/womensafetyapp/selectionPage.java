package com.example.womensafetyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class selectionPage extends AppCompatActivity {

    private Button buttonPolice, buttonUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_selection_page);

        buttonPolice = findViewById(R.id.buttonPolice);
        buttonUser = findViewById(R.id.buttonUser);

        // Handling Police Login Button
       /* buttonPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent policeIntent = new Intent(selectionPage.this, PoliceLoginActivity.class);
                startActivity(policeIntent);
            }
        });*/

        // Handling User Login Button
        buttonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(selectionPage.this, login.class); // Assuming MainActivity is User Login
                startActivity(userIntent);
            }
        });
    }
}
