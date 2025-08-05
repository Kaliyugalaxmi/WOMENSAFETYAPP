package com.example.womensafetyapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Sos extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private Button sos;
    private LocationManager locationManager;
    private String currentLocation = "";
    private DatabaseReference userRef, sosCasesRef;
    private ImageButton backToDashboard;
    private String userId, userName, userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        sos = findViewById(R.id.sos);
        backToDashboard = findViewById(R.id.backToDashboard);

        backToDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(Sos.this, Dashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        sosCasesRef = FirebaseDatabase.getInstance().getReference("SOS_Cases");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        sos.setOnClickListener(view -> showConfirmationDialog());
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Send Alert Message")
                .setMessage("Do you want to send an alert message to your emergency contacts and police?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                    } else {
                        getLocationAndSendMessage();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void getLocationAndSendMessage() {
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    currentLocation = "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                    locationManager.removeUpdates(this);
                    saveAlertToDatabase();
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void saveAlertToDatabase() {
        String caseId = sosCasesRef.push().getKey();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                userName = snapshot.child("name").getValue(String.class);
                userPhone = snapshot.child("phone").getValue(String.class);

                SOSCase sosCase = new SOSCase(caseId, userId, userName, userPhone, currentLocation, "Pending", timestamp);
                sosCasesRef.child(caseId).setValue(sosCase)
                        .addOnSuccessListener(aVoid -> sendAlertMessageToContacts())
                        .addOnFailureListener(e -> Toast.makeText(Sos.this, "Failed to save alert", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void sendAlertMessageToContacts() {
        userRef.child("EmergencyContacts").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String alertMessage = "I'm in danger. Please help! My location: " + currentLocation;
                SmsManager smsManager = SmsManager.getDefault();

                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    String phoneNumber = contactSnapshot.child("Phone").getValue(String.class);
                    if (phoneNumber != null && !phoneNumber.isEmpty()) {
                        try {
                            smsManager.sendTextMessage(phoneNumber, null, alertMessage, null, null);
                            Toast.makeText(Sos.this, "Alert message sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(Sos.this, "Failed to send message to " + phoneNumber, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else {
                Toast.makeText(Sos.this, "No emergency contacts found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(Sos.this, "Failed to retrieve contacts", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndSendMessage();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
