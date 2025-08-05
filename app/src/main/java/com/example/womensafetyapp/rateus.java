package com.example.womensafetyapp;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class rateus extends Fragment {

    private float userRate = 0; // Store user rating
    private DatabaseReference databaseReference; // Firebase Realtime Database reference
    private FirebaseAuth firebaseAuth; // Firebase Authentication instance

    public rateus() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rateus, container, false);

        final AppCompatButton rateNowbtn = view.findViewById(R.id.rateNowbtn);
        final AppCompatButton laterbtn = view.findViewById(R.id.laterbtn);
        final RatingBar ratingBar = view.findViewById(R.id.ratingbtn);
        final ImageView ratingImage = view.findViewById(R.id.ratingImage);

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Ratings");

        rateNowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRatingToFirebase(userRate);
            }
        });

        laterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack(); // Hide rating dialog
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating <= 1) {
                    ratingImage.setImageResource(R.drawable.start);
                } else if (rating <= 2) {
                    ratingImage.setImageResource(R.drawable.second);
                } else if (rating <= 3) {
                    ratingImage.setImageResource(R.drawable.three3);
                } else if (rating <= 4) {
                    ratingImage.setImageResource(R.drawable.four4);
                } else if (rating <= 5) {
                    ratingImage.setImageResource(R.drawable.five);
                }
                animateImage(ratingImage);
                userRate = rating; // Store selected rating
            }
        });

        return view;
    }

    private void saveRatingToFirebase(float rating) {
        if (rating == 0) {
            Toast.makeText(getContext(), "Please select a rating before submitting.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "You must be logged in to rate.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid(); // Get user ID
        String userEmail = currentUser.getEmail(); // Optional: Get user email
        String ratingId = databaseReference.child(userId).push().getKey(); // Unique rating ID

        // Creating a data object to store rating with user info
        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("userId", userId);
        ratingData.put("userEmail", userEmail); // Optional
        ratingData.put("rating", rating);
        ratingData.put("timestamp", System.currentTimeMillis());

        // Store rating in Realtime Database under user's node
        assert ratingId != null;
        databaseReference.child(userId).child(ratingId).setValue(ratingData)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(), "Thank you for your feedback!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to submit rating. Try again!", Toast.LENGTH_SHORT).show());
    }

    private void animateImage(ImageView ratingImage) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1f, 0, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(200);
        ratingImage.startAnimation(scaleAnimation);
    }
}
