package com.example.womensafetyapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContactPicker extends AppCompatActivity {

    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_CONTACT_PERMISSION = 2;

    private ImageButton add1, add2, add3;
    private TextView contact1Name, contact1Phone;
    private TextView contact2Name, contact2Phone;
    private TextView contact3Name, contact3Phone;

    private DatabaseReference userRef;

    private int currentContact = 0;

    Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_picker);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("EmergencyContacts");

        add2 = findViewById(R.id.add2);
        add3 = findViewById(R.id.add3);

        contact1Name = findViewById(R.id.contact1_name);
        contact1Phone = findViewById(R.id.contact1_phone);
        contact2Name = findViewById(R.id.contact2_name);
        contact2Phone = findViewById(R.id.contact2_phone);
        contact3Name = findViewById(R.id.contact3_name);
        contact3Phone = findViewById(R.id.contact3_phone);

        buttonSave = findViewById(R.id.buttonSave);

        // Set default contact for Contact 1
        String defaultName = "Police";
        String defaultPhone = "+918237918991";

        contact1Name.setText(defaultName);
        contact1Phone.setText(defaultPhone);

        // Save default contact to Firebase
        saveDefaultContactToFirebase(defaultName, defaultPhone);

        add2.setOnClickListener(v -> selectContact(2));
        add3.setOnClickListener(v -> selectContact(3));

        buttonSave.setOnClickListener(v -> {
            startActivity(new Intent(ContactPicker.this, Dashboard.class));
            finish();
        });
    }

    private void saveDefaultContactToFirebase(String name, String phoneNumber) {
        DatabaseReference contact1Ref = userRef.child("Contact1");
        contact1Ref.child("Name").setValue(name);
        contact1Ref.child("Phone").setValue(phoneNumber);
    }

    private void selectContact(int contactNumber) {
        if (contactNumber > 3) {
            Toast.makeText(this, "You can only add up to three emergency contacts.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT_PERMISSION);
        } else {
            currentContact = contactNumber;
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, REQUEST_CONTACT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CONTACT && resultCode == Activity.RESULT_OK && data != null) {
            Uri contactUri = data.getData();

            if (contactUri != null) {
                Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    int hasPhoneNumber = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    cursor.close();

                    if (hasPhoneNumber > 0) {
                        Cursor phoneCursor = getContentResolver().query(
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
                        Toast.makeText(this, "This contact has no phone number", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void saveContactToFirebase(String name, String phoneNumber) {
        if (currentContact > 3 || currentContact == 0) {
            Toast.makeText(this, "You can only add up to three emergency contacts.", Toast.LENGTH_SHORT).show();
            return;
        }

        String contactKey = "Contact" + currentContact;

        // Save the new contact to Firebase
        userRef.child(contactKey).child("Name").setValue(name, (error, ref) -> {
            if (error != null) {
                Toast.makeText(this, "Failed to save contact. Try again.", Toast.LENGTH_SHORT).show();
            } else {
                userRef.child(contactKey).child("Phone").setValue(phoneNumber, (phoneError, phoneRef) -> {
                    if (phoneError != null) {
                        Toast.makeText(this, "Failed to save contact phone number. Try again.", Toast.LENGTH_SHORT).show();
                    } else {
                        runOnUiThread(() -> {
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
                            Toast.makeText(this, "Contact saved successfully!", Toast.LENGTH_SHORT).show();
                            currentContact = 0;  // Reset after updating UI
                        });
                    }
                });
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
                Toast.makeText(this, "Permission denied to read contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
