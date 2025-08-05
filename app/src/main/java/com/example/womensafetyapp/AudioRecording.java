package com.example.womensafetyapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class AudioRecording extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1001;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private Button start, stop, save, viewRecordings;
    private File audioDir;

    private ImageButton backToDashboard;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recording);

        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        save = findViewById(R.id.save);
        viewRecordings = findViewById(R.id.viewRecordings);
        backToDashboard=findViewById(R.id.backToDashboard);

        backToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AudioRecording.this, Dashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // Handle permission request
        if (!checkPermissions()) {
            requestPermissions();
        }

        // Setup Audio Storage Directory
        audioDir = new File(getExternalFilesDir(null), "AudioRecords");
        if (!audioDir.exists()) {
            audioDir.mkdirs();
        }

        // Set button click listeners
        start.setOnClickListener(v -> startRecording());
        stop.setOnClickListener(v -> stopRecording());
        save.setOnClickListener(v -> saveRecording());
        viewRecordings.setOnClickListener(v -> {
            Intent intent = new Intent(AudioRecording.this, Recordings.class);
            startActivity(intent);
        });
    }

    private void startRecording() {
        try {
            String fileName = String.format(Locale.getDefault(), "audio_%d.3gp", System.currentTimeMillis());
            audioFilePath = new File(audioDir, fileName).getAbsolutePath();

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();

            start.setEnabled(false);
            stop.setEnabled(true);
            save.setEnabled(false);

            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to start recording", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;

                start.setEnabled(true);
                stop.setEnabled(false);
                save.setEnabled(true);
                Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
            } catch (RuntimeException e) {
                Toast.makeText(this, "Error stopping recording: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveRecording() {
        Toast.makeText(this, "Recording saved", Toast.LENGTH_SHORT).show();
        save.setEnabled(false);
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Optional: Play the last recorded audio file
    private void playRecording() {
        if (audioFilePath == null) {
            Toast.makeText(this, "No recording found!", Toast.LENGTH_SHORT).show();
            return;
        }

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            Toast.makeText(this, "Playing recording...", Toast.LENGTH_SHORT).show();

            mediaPlayer.setOnCompletionListener(mp -> {
                Toast.makeText(this, "Playback finished", Toast.LENGTH_SHORT).show();
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show();
        }


    }
}
