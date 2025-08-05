package com.example.womensafetyapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FakeIncomingCall extends AppCompatActivity {

    private TextView tvCallerName, tvCallerNumber;
    private ImageButton btnAnswer, btnDecline;

    private MediaPlayer ringtonePlayer;
    private Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fake_incoming_call);
        tvCallerName = findViewById(R.id.tvCallerName);
        tvCallerNumber = findViewById(R.id.tvCallerNumber);
        btnAnswer = findViewById(R.id.btnAnswer);
        btnDecline = findViewById(R.id.btnDecline);

        // Get caller details
        Intent intent = getIntent();
        String callerName = intent.getStringExtra("CALLER_NAME");
        String callerNumber = intent.getStringExtra("CALLER_NUMBER");

        if (callerName != null) {
            tvCallerName.setText(callerName);
        }
        if (callerNumber != null) {
            tvCallerNumber.setText(callerNumber);
        }

        // Start ringtone and vibration
        startRingtoneAndVibration();

        btnDecline.setOnClickListener(v -> {
            stopRingtoneAndVibration(); // Stop ringtone and vibration
            finish(); // Close the activity when call is declined
        });

        btnAnswer.setOnClickListener(v -> {
            stopRingtoneAndVibration(); // Stop ringtone and vibration
            answerCall(callerName, callerNumber); // Proceed to call answered screen
        });
    }

    private void startRingtoneAndVibration() {
        try {
            // Play ringtone from raw folder
            ringtonePlayer = MediaPlayer.create(this, R.raw.fake_ringtone);
            if (ringtonePlayer != null) {
                ringtonePlayer.setLooping(true);
                ringtonePlayer.start();
            }

            // Start vibration
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                long[] pattern = {0, 1000, 1000}; // Vibrate for 1 sec, pause for 1 sec, repeat
                vibrator.vibrate(pattern, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRingtoneAndVibration() {
        // Stop ringtone
        if (ringtonePlayer != null && ringtonePlayer.isPlaying()) {
            ringtonePlayer.stop();
            ringtonePlayer.release();
            ringtonePlayer = null;
        }

        // Stop vibration
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    private void answerCall(String name, String number) {
        Intent intent = new Intent(this, FakeCallAnswered.class);
        intent.putExtra("CALLER_NAME", name);
        intent.putExtra("CALLER_NUMBER", number);
        startActivity(intent);
        finish(); // Close the current screen after answering
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRingtoneAndVibration();
    }
}