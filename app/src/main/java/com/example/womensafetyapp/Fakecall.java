package com.example.womensafetyapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Fakecall extends AppCompatActivity {

    private EditText etCallerName, etCallerNumber;
    private Spinner spinnerDelay;
    private Button btnStartFakeCall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fakecall);
        etCallerName = findViewById(R.id.etCallerName);
        etCallerNumber = findViewById(R.id.etCallerNumber);
        spinnerDelay = findViewById(R.id.spinnerDelay);
        btnStartFakeCall = findViewById(R.id.btnStartFakeCall);

        String[] delayOptions = {"5 seconds", "10 seconds", "15 seconds"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, delayOptions);
        spinnerDelay.setAdapter(adapter);

        btnStartFakeCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleFakeCall();
            }
        });
    }

    private void scheduleFakeCall() {
        String callerName = etCallerName.getText().toString();
        String callerNumber = etCallerNumber.getText().toString();
        int delay = getSelectedDelay();

        if (callerName.isEmpty() || callerNumber.isEmpty()) {
            Toast.makeText(this, "Please enter caller name and number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create intent to start Fake Incoming Call
        Intent intent = new Intent(this, FakeIncomingCall.class);
        intent.putExtra("CALLER_NAME", callerName);
        intent.putExtra("CALLER_NUMBER", callerNumber);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long triggerTime = System.currentTimeMillis() + delay;

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }

        Toast.makeText(this, "Fake call scheduled in " + (delay / 1000) + " seconds", Toast.LENGTH_SHORT).show();
    }

    private int getSelectedDelay() {
        int position = spinnerDelay.getSelectedItemPosition();
        switch (position) {
            case 1:
                return 10000; // 10 seconds
            case 2:
                return 15000; // 15 seconds
            default:
                return 5000; // 5 seconds
        }
    }
}