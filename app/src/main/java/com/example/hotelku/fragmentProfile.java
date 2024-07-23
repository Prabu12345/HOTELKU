package com.example.hotelku;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hotelku.module.function;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class fragmentProfile extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private TextView nameText, emailText;
    private Button signoutButton, editAccButton;
    private ImageView avatarView;

    void initUI(View view) {
        signoutButton = view.findViewById(R.id.signOutButton);
        editAccButton = view.findViewById(R.id.editProfileButton);
        nameText = view.findViewById(R.id.namaCard);
        emailText = view.findViewById(R.id.emailCard);
        avatarView = view.findViewById(R.id.avatarImage);
    }

    public fragmentProfile() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initUI(view);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadUserProfile();

        signoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), Login.class));
            getActivity().finish();
        });

        editAccButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), editProfile.class)));

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            DatabaseReference userRef = mDatabase.child("users").child(userId);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.child("username").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String avatar = dataSnapshot.child("avatarUrl").getValue(String.class);

                        function.loadAvatarIntoImageView(getActivity(), avatar, avatarView);
                        nameText.setText(username);
                        emailText.setText(email);
                    } else {
                        Toast.makeText(getActivity(), "User profile not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("Error getting data: " + databaseError.getMessage());
                }
            });
        }
    }
}
