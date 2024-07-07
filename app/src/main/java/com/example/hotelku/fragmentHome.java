package com.example.hotelku;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class fragmentHome extends Fragment {

    private FirebaseAuth mAuth;

    private TextView tampilNama, tampilTanggalLahir, tampilEmail;
    private ImageButton chInButton, reservButton, chOutButton;

    void initUI(View view) {
        tampilNama = view.findViewById(R.id.namaCard);
        tampilTanggalLahir = view.findViewById(R.id.tanggalLahirCard);
        tampilEmail = view.findViewById(R.id.emailCard);
        chInButton = view.findViewById(R.id.checkinButton);
        chOutButton = view.findViewById(R.id.checkoutButton);
        reservButton = view.findViewById(R.id.reservationButton);

    }

    public fragmentHome() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_beranda, container, false);

        initUI(view);

        mAuth = FirebaseAuth.getInstance();

        loadUserProfile();

        chInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Checkin.class));
                getActivity().finish();
            }
        });

        chOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Checkout.class));
                getActivity().finish();
            }
        });

        reservButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Reservation.class));
                getActivity().finish();
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
                            String birthdate = documentSnapshot.getString("birthdate");

                            tampilNama.setText(username);
                            tampilEmail.setText(email);
                            tampilTanggalLahir.setText(birthdate);
                        } else {
                            Toast.makeText(getActivity(), "User profile not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to load user profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}