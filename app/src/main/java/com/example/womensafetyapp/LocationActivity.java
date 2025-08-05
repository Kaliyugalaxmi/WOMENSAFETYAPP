package com.example.womensafetyapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_SMS_PERMISSION = 2;
    private ImageButton backToDashboard;

    private FusedLocationProviderClient fusedLocationClient;
    private TextView txtLatitude, txtLongitude, txtAddress;
    private ListView contactListView;
    private ArrayAdapter<String> adapter;
    private List<String> contactNames = new ArrayList<>();
    private List<String> contactNumbers = new ArrayList<>();
    private List<Boolean> contactSelection = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        txtLatitude = findViewById(R.id.txtLatitude);
        txtLongitude = findViewById(R.id.txtLongitude);
        txtAddress = findViewById(R.id.txtAddress);
        contactListView = findViewById(R.id.contactListView);

        Button btnGetLocation = findViewById(R.id.btnGetLocation);
        Button btnSendLocation = findViewById(R.id.btnSendLocation);

        backToDashboard=findViewById(R.id.backToDashboard);

        backToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationActivity.this, Dashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fetchEmergencyContacts();

        btnGetLocation.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            } else {
                requestContinuousLocationUpdates();
            }
        });

        btnSendLocation.setOnClickListener(v -> sendLocationToSelectedContacts());
    }

    private void requestContinuousLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setFastestInterval(500);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        updateUIWithLocation(locationResult.getLastLocation());
                    }
                }
            }, Looper.getMainLooper());
        }
    }

    private void updateUIWithLocation(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            txtLatitude.setText("Latitude: " + latitude);
            txtLongitude.setText("Longitude: " + longitude);
            txtAddress.setText("Address: " + getAddressFromLocation(location));
        }
    }

    private String getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unable to fetch address.";
    }

    private void fetchEmergencyContacts() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference contactsRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("EmergencyContacts");

        contactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactNames.clear();
                contactNumbers.clear();
                contactSelection.clear();

                int count = 0; // Limit the number of displayed contacts
                for (DataSnapshot contactSnapshot : dataSnapshot.getChildren()) {
                    if (count >= 3) break; // Stop after 3 contacts

                    String contactName = contactSnapshot.child("Name").getValue(String.class);
                    String contactNumber = contactSnapshot.child("Phone").getValue(String.class);

                    if (contactName != null && contactNumber != null) {
                        contactNames.add(contactName);
                        contactNumbers.add(contactNumber);
                        contactSelection.add(false);
                        count++;
                    }
                }

                adapter = new ArrayAdapter<>(LocationActivity.this, android.R.layout.simple_list_item_multiple_choice, contactNames);
                contactListView.setAdapter(adapter);
                contactListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                contactListView.setOnItemClickListener((parent, view, position, id) -> contactSelection.set(position, contactListView.isItemChecked(position)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LocationActivity.this, "Failed to fetch contacts.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void sendLocationToSelectedContacts() {
        // Check if location details are available
        if (txtLatitude.getText().toString().isEmpty() || txtLongitude.getText().toString().isEmpty()) {
            Toast.makeText(this, "Location is not available yet. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for SMS permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
            return;
        }

        // Prepare the message to be sent
        String locationMessage = "I need help! My location: "
                + txtAddress.getText().toString();

        SmsManager smsManager = SmsManager.getDefault();

        for (int i = 0; i < contactNumbers.size(); i++) {
            if (contactSelection.get(i)) {
                try {
                    // Break message into parts if necessary
                    ArrayList<String> parts = smsManager.divideMessage(locationMessage);
                    smsManager.sendMultipartTextMessage(contactNumbers.get(i), null, parts, null, null);

                    Toast.makeText(this, "Location sent to: " + contactNames.get(i), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to send message to: " + contactNames.get(i), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestContinuousLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendLocationToSelectedContacts();
            } else {
                Toast.makeText(this, "SMS permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
