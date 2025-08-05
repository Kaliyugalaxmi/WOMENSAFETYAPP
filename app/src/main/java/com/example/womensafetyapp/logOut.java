package com.example.womensafetyapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class logOut extends Fragment {

    private Button buttonYes, buttonNo;
    private FirebaseAuth mAuth;

    public logOut() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_out, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonYes = view.findViewById(R.id.buttonYes);
        buttonNo = view.findViewById(R.id.buttonNo);
        mAuth = FirebaseAuth.getInstance();

        // Handling Yes button (Logout)
        buttonYes.setOnClickListener(v -> {
            mAuth.signOut();  // Logs out the user from Firebase
            Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Redirect to LoginActivity (MainActivity in your case)
            Intent intent = new Intent(getActivity(), login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        // Handling No button (Stay in the app)
        buttonNo.setOnClickListener(v -> {
            // Go back to the previous fragment (Dashboard or any other fragment)
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();  // This will take you to the previous fragment
        });
    }
}
