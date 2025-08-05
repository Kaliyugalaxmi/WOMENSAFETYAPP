package com.example.womensafetyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class SOSListAdapter extends ArrayAdapter<SOSCase> {
    private Context context;
    private List<SOSCase> sosCaseList;
    private DatabaseReference sosCasesRef;
    private DatabaseReference usersRef; // 🔹 Declare usersRef

    public SOSListAdapter(Context context, List<SOSCase> sosCaseList) {
        super(context, 0, sosCaseList);
        this.context = context;
        this.sosCaseList = sosCaseList;
        this.sosCasesRef = FirebaseDatabase.getInstance().getReference("SOS_Cases");
        this.usersRef = FirebaseDatabase.getInstance().getReference("Users"); // 🔹 Initialize usersRef
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.sos_alert_item, parent, false);
        }

        // Get the current SOS case
        SOSCase sosCase = getItem(position);
        if (sosCase == null) {
            return convertView; // 🔹 Prevent null issues
        }

        // Find UI components
        TextView userNameTextView = convertView.findViewById(R.id.userNameTextView);
        TextView userPhoneTextView = convertView.findViewById(R.id.userPhoneTextView);
        TextView locationTextView = convertView.findViewById(R.id.LocationTextView);
        TextView timeStampTextView = convertView.findViewById(R.id.TimeStampTextView);
        TextView activeTextView = convertView.findViewById(R.id.ActiveTextView);
        Button resolveButton = convertView.findViewById(R.id.resolveButton);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);

        // Set values from SOSCase object
        userNameTextView.setText("Name: " + sosCase.getUserName());
        locationTextView.setText("Location: " + sosCase.getLocation());
        timeStampTextView.setText("Time: " + sosCase.getTimestamp());
        activeTextView.setText("Status: " + sosCase.getStatus());

        // 🔹 Fetch user's phone number using userId
        usersRef.child(sosCase.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    userPhoneTextView.setText("Phone: " + user.number);
                } else {
                    userPhoneTextView.setText("Phone: Not Available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userPhoneTextView.setText("Phone: Error Fetching");
            }
        });

        // Resolve button click event
        resolveButton.setOnClickListener(v -> {
            DatabaseReference caseRef = sosCasesRef.child(sosCase.getCaseId());
            caseRef.child("status").setValue("Resolved")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "SOS case marked as resolved", Toast.LENGTH_SHORT).show();

                        // 🔹 Change the button color when clicked
                        resolveButton.setBackgroundColor(context.getResources().getColor(R.color.green));
                        resolveButton.setText("Resolved"); // Optional: Change button text
                        resolveButton.setEnabled(false);  // Optional: Disable button after resolving

                        sosCase.setStatus("Resolved");
                        notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
                    });
        });


        // Delete button click event
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Alert")
                    .setMessage("Are you sure you want to delete this alert?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        sosCasesRef.child(sosCase.getCaseId()).removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Alert Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    sosCaseList.remove(position);
                                    notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to Delete Alert", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return convertView;
    }
}
