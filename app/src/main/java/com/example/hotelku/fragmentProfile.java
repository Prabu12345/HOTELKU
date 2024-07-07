package com.example.hotelku;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class fragmentProfile extends Fragment {
    private FirebaseAuth mAuth;

    private TextView nameText, emailText;
    private Button signoutButton, editAccButton, historyButton, helpButton;

    void initUI(View view) {
        signoutButton = view.findViewById(R.id.signOutButton);
        editAccButton = view.findViewById(R.id.editProfileButton);
        historyButton = view.findViewById(R.id.transHistoryButton);
        helpButton = view.findViewById(R.id.helpCenterButton);
        nameText = view.findViewById(R.id.namaCard);
        emailText = view.findViewById(R.id.emailCard);
    }

    public fragmentProfile(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        initUI(view);

        mAuth = FirebaseAuth.getInstance();

        loadUserProfile();

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finish();
            }
        });

        editAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), editProfile.class));
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Comming Soon.", Toast.LENGTH_SHORT).show();
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Comming Soon.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            String email = documentSnapshot.getString("email");

                            nameText.setText(username);
                            emailText.setText(email);
                        } else {
                            Toast.makeText(getActivity(), "User profile not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to load user profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
