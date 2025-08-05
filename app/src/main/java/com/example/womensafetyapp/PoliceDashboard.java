package com.example.womensafetyapp;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PoliceDashboard extends AppCompatActivity {
    private ListView listView;
    private SOSListAdapter adapter;
    private DatabaseReference sosCasesRef;
    private List<SOSCase> sosCasesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_dashboard);

        // Initialize UI components
        listView = findViewById(R.id.listView);
        sosCasesList = new ArrayList<>();
        adapter = new SOSListAdapter(this, sosCasesList);
        listView.setAdapter(adapter);

        // Reference Firebase database
        sosCasesRef = FirebaseDatabase.getInstance().getReference("SOS_Cases");

        // Fetch SOS Cases
        fetchSOSCases();
    }

    private void fetchSOSCases() {
        sosCasesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sosCasesList.clear(); // Clear old data
                for (DataSnapshot caseSnapshot : snapshot.getChildren()) {
                    SOSCase sosCase = caseSnapshot.getValue(SOSCase.class);
                    if (sosCase != null) {
                        sosCasesList.add(sosCase);
                    }
                }
                adapter.notifyDataSetChanged(); // Refresh ListView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PoliceDashboard.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
