package com.example.womensafetyapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FakeCallAnswered extends AppCompatActivity {

    private TextView callStatus,callerName, callerNumber;
    private boolean isCallActive = false;
    private Handler timerHandler = new Handler();
    private int callDuration = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fake_call_answered);
        callStatus = findViewById(R.id.callTimer);
        callerName = findViewById(R.id.caller_name);
        callerNumber = findViewById(R.id.caller_number);
        ImageButton btnEndCall = findViewById(R.id.btn_end_call);

        // Get Name and Number
        String name = getIntent().getStringExtra("CALLER_NAME");
        String number = getIntent().getStringExtra("CALLER_NUMBER");

        // Display Name and Number
        callerName.setText(name != null ? name : "Unknown");
        callerNumber.setText(number != null ? number : "No Number");

        startCallTimer();

        btnEndCall.setOnClickListener(view -> endCall());
    }

    private void startCallTimer() {
        isCallActive = true;
        timerHandler.postDelayed(callTimer, 1000);
    }

    private void endCall() {
        if (isCallActive) {
            timerHandler.removeCallbacks(callTimer);
        }
        finish();
    }

    private Runnable callTimer = new Runnable() {
        @Override
        public void run() {
            callDuration++;
            int minutes = callDuration / 60;
            int seconds = callDuration % 60;
            callStatus.setText(String.format("%02d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(callTimer);
    }
}