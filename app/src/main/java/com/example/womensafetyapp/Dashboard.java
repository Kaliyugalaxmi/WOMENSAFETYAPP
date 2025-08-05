package com.example.womensafetyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import android.util.Log;

public class Dashboard extends AppCompatActivity {

    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    TextView greetings, user_name;
    ImageButton settingsButton;
    ImageView fakeCallButton,profile,location,helpline,chatbot,SOS,Audio,safetyTips;
    NavigationView navigationView;

    DrawerLayout drawerLayout;
    FirebaseUser user;
    String UserID;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        location=findViewById(R.id.location);
        chatbot=findViewById(R.id.chatbot);
        helpline=findViewById(R.id.helpline);
        fakeCallButton = findViewById(R.id.fakecallbutton);
        greetings = findViewById(R.id.tv_greeting);
        settingsButton = findViewById(R.id.settingsButton);
        drawerLayout = findViewById(R.id.drawerLayout);
        user_name = findViewById(R.id.tv_user_name);
        navigationView = findViewById(R.id.navigationView);
        googleSignInOptions =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        profile=findViewById(R.id.profile);
        SOS=findViewById(R.id.SOS);
        Audio=findViewById(R.id.Audio);
        safetyTips=findViewById(R.id.safetyTips);

        // Get the current hour
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // Display greeting based on the time of day
        if (hour >= 0 && hour < 12) {
            greetings.setText("Good Morning!");
        } else if (hour >= 12 && hour < 18) {
            greetings.setText("Good Afternoon!");
        } else {
            greetings.setText("Good Evening!");
        }
        if (fakeCallButton != null) {
            Log.d("DashboardActivity", "ImageButton Found Successfully");
            fakeCallButton.setVisibility(View.VISIBLE);  // Ensure visibility
            fakeCallButton.setClickable(true);

            //Log.d(TAG, "This is a debug log message");// Ensure it's clickable
            System.out.println("Hello");
            fakeCallButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Hello2");
                    Log.d("FakeCallButton", "Fake Call Button Clicked");
                    Toast.makeText(Dashboard.this, "Fake Call Button Clicked", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Dashboard.this, Fakecall.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.d("DashboardActivity", "ImageButton Not Found");
        }

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, profilePage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        safetyTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, SafetyTips.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        Audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, AudioRecording.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        SOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Sos.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });



        chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, ChatBot.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        helpline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, helpline.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, LocationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the drawer
                drawerLayout.openDrawer(GravityCompat.END); // Use END if your drawer is on the right
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        UserID = user.getUid();

        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("username", "User");

        user_name.setText(savedUsername);
        reference.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userDetails = dataSnapshot.getValue(User.class);
                if (userDetails != null) {
                    String name = userDetails.name;
                    user_name.setText(name);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", name);
                    editor.apply();
                }// Deserialize to User object

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                TextView userNameTextView = findViewById(R.id.user_name);
                userNameTextView.setText("Error retrieving user data");
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id= menuItem.getItemId();
                if(id==R.id.home){
                    Intent intent = new Intent(Dashboard.this, Dashboard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the activity stack
                    startActivity(intent);
                    finish();
                } else if (id==R.id.language) {
                    Toast.makeText(Dashboard.this, "lanugae", Toast.LENGTH_SHORT).show();
                }
                else if (id==R.id.Changepassword) {
                    replaceFragment(new password());
                }
                else if (id==R.id.Emergencycontact) {
                    replaceFragment(new EmergencyContact());

                }
                else if (id==R.id.Rateus) {
                    replaceFragment(new rateus());
                }
                else if (id==R.id.Logout) {
                    replaceFragment(new logOut());

                }
                drawerLayout.closeDrawer(GravityCompat.END);

                return true;
            }
        });

        GoogleSignInAccount acct=GoogleSignIn.getLastSignedInAccount(this);
        if(acct!=null){
            String personName=acct.getDisplayName();
            user_name.setText(personName);
        }

    }



    private void replaceFragment (Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.drawerLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

}

