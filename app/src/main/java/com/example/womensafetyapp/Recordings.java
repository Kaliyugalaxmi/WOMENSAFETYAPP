package com.example.womensafetyapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Recordings extends AppCompatActivity {

    private ListView listView;
    private ImageButton back;

    private List<String> recordingsList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recordings);

        listView = findViewById(R.id.list_recordings);
        back=findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Recordings.this, AudioRecording.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        // ✅ Assign adapter before setting it to ListView
        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.text_item, recordingsList);
        listView.setAdapter(adapter);

        loadRecordings();

        // Play recording on item click
        listView.setOnItemClickListener((parent, view, position, id) -> playRecording(recordingsList.get(position)));

        // Delete recording on long press
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            deleteRecording(position);
            return true;
        });
    }

    private void loadRecordings() {
        File audioDir = new File(getExternalFilesDir(null), "AudioRecords");
        recordingsList.clear(); // Clear existing list

        if (audioDir.exists() && audioDir.listFiles() != null) {
            for (File file : audioDir.listFiles()) {
                recordingsList.add(file.getName()); // Display file names instead of full path
            }
        } else {
            Toast.makeText(this, "No recordings found", Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
    }

    private void playRecording(String fileName) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();
            File filePath = new File(getExternalFilesDir(null), "AudioRecords/" + fileName);
            mediaPlayer.setDataSource(filePath.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();

            Toast.makeText(this, "Playing: " + fileName, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to play recording", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteRecording(int position) {
        File fileToDelete = new File(getExternalFilesDir(null), "AudioRecords/" + recordingsList.get(position));

        if (fileToDelete.exists() && fileToDelete.delete()) {
            Toast.makeText(this, "Recording deleted", Toast.LENGTH_SHORT).show();
            recordingsList.remove(position);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Failed to delete recording", Toast.LENGTH_SHORT).show();
        }
    }
}
