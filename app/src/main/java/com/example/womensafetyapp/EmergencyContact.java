package com.example.womensafetyapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmergencyContact extends Fragment {

    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_CONTACT_PERMISSION = 2;

    private ImageButton add1, add2, add3;
    private TextView contact1Name, contact1Phone;
    private TextView contact2Name, contact2Phone;
    private TextView contact3Name, contact3Phone;

    private DatabaseReference userRef;
    private int currentContact = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency_contact, container, false);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("EmergencyContacts");

        add1 = view.findViewById(R.id.add1);
        add2 = view.findViewById(R.id.add2);
        add3 = view.findViewById(R.id.add3);

        contact1Name = view.findViewById(R.id.contact1_name);
        contact1Phone = view.findViewById(R.id.contact1_phone);
        contact2Name = view.findViewById(R.id.contact2_name);
        contact2Phone = view.findViewById(R.id.contact2_phone);
        contact3Name = view.findViewById(R.id.contact3_name);
        contact3Phone = view.findViewById(R.id.contact3_phone);

        add1.setOnClickListener(v -> selectContact(1));
        add2.setOnClickListener(v -> selectContact(2));
        add3.setOnClickListener(v -> selectContact(3));

        // Retrieve and display the saved emergency contacts
        loadEmergencyContacts();

        return view;
    }

    private void selectContact(int contactNumber) {
        if (contactNumber > 3) {
            Toast.makeText(getActivity(), "You can only add up to three emergency contacts.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT_PERMISSION);
        } else {
            currentContact = contactNumber;
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, REQUEST_CONTACT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CONTACT && resultCode == Activity.RESULT_OK && data != null) {
            Uri contactUri = data.getData();

            if (contactUri != null) {
                Cursor cursor = getActivity().getContentResolver().query(contactUri, null, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    int hasPhoneNumber = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    cursor.close();

                    if (hasPhoneNumber > 0) {
                        Cursor phoneCursor = getActivity().getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{contactId},
                                null
                        );

                        if (phoneCursor != null && phoneCursor.moveToFirst()) {
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            saveContactToFirebase(name, phoneNumber);
                            phoneCursor.close();
                        }
                    } else {
                        Toast.makeText(getActivity(), "This contact has no phone number", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void saveContactToFirebase(String name, String phoneNumber) {
        if (currentContact > 3 || currentContact == 0) {
            Toast.makeText(getActivity(), "You can only add up to three emergency contacts.", Toast.LENGTH_SHORT).show();
            return;
        }

        String contactKey = "Contact" + currentContact;

        // Save the new contact to Firebase
        userRef.child(contactKey).child("Name").setValue(name);
        userRef.child(contactKey).child("Phone").setValue(phoneNumber);

        switch (currentContact) {
            case 1:
                contact1Name.setText(name);
                contact1Phone.setText(phoneNumber);
                break;
            case 2:
                contact2Name.setText(name);
                contact2Phone.setText(phoneNumber);
                break;
            case 3:
                contact3Name.setText(name);
                contact3Phone.setText(phoneNumber);
                break;
        }

        currentContact = 0;  // Reset after updating UI
    }

    private void loadEmergencyContacts() {
        // Retrieve and display saved emergency contacts
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the data from Firebase
                    for (int i = 1; i <= 3; i++) {
                        String contactKey = "Contact" + i;
                        if (dataSnapshot.child(contactKey).exists()) {
                            String name = dataSnapshot.child(contactKey).child("Name").getValue(String.class);
                            String phone = dataSnapshot.child(contactKey).child("Phone").getValue(String.class);

                            // Set the contact data to UI
                            switch (i) {
                                case 1:
                                    contact1Name.setText(name);
                                    contact1Phone.setText(phone);
                                    break;
                                case 2:
                                    contact2Name.setText(name);
                                    contact2Phone.setText(phone);
                                    break;
                                case 3:
                                    contact3Name.setText(name);
                                    contact3Phone.setText(phone);
                                    break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load contacts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CONTACT_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectContact(currentContact);
            } else {
                Toast.makeText(getActivity(), "Permission denied to read contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
