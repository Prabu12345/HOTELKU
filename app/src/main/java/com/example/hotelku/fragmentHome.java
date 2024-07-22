package com.example.hotelku;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.hotelku.module.function;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class fragmentHome extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private TextView tampilNama, tampilTanggalLahir, tampilEmail, noRoom, checkin, startDateTV, endDateTV;
    private ImageButton chInButton, reservButton, chOutButton;
    private ImageView avatarView;
    private String room;

    void initUI(View view) {
        tampilNama = view.findViewById(R.id.namaCard);
        tampilTanggalLahir = view.findViewById(R.id.tanggalLahirCard);
        tampilEmail = view.findViewById(R.id.emailCard);
        chInButton = view.findViewById(R.id.checkinButton);
        chOutButton = view.findViewById(R.id.checkoutButton);
        reservButton = view.findViewById(R.id.reservationButton);
        avatarView = view.findViewById(R.id.profile);
        noRoom = view.findViewById(R.id.bookStatusKamar);
        checkin = view.findViewById(R.id.bookTimeIn);
        startDateTV = view.findViewById(R.id.bookStartDate);
        endDateTV = view.findViewById(R.id.bookEndDate);
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
        View view = inflater.inflate(R.layout.fragment_beranda, container, false);

        initUI(view);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadUserProfile();

        chInButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), Checkin.class)));

        chOutButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), Checkout.class)));

        reservButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), Reservation.class)));

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            DatabaseReference userRef = mDatabase.child("users").child(userId);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.child("username").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String avatar = dataSnapshot.child("avatarUrl").getValue(String.class);
                        Long birthdateMillis = dataSnapshot.child("birthdate").getValue(Long.class);
                        Date birthdate = birthdateMillis != null ? new Date(birthdateMillis) : null;

                        function.loadAvatarIntoImageView(getActivity(), avatar, avatarView);
                        tampilNama.setText(username);
                        tampilEmail.setText(email);
                        tampilTanggalLahir.setText(function.convertDateToString(birthdate));
                    } else {
                        Toast.makeText(getActivity(), "User profile not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "Failed to load user profile: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            DatabaseReference roomRef = mDatabase.child("room");
            roomRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        room = snapshot.getKey();
                        String resivBy = snapshot.child("resivBy").getValue(String.class);
                        Long checkInDateMillis = snapshot.child("checkIn").child("date").getValue(Long.class);
                        Long startDateMillis = snapshot.child("resivDateStart").getValue(Long.class);
                        Long endDateMillis = snapshot.child("resivDateEnd").getValue(Long.class);
                        Boolean isCheck = snapshot.child("checkIn").child("isCheck").getValue(Boolean.class);
                        Date endDate = snapshot.child("resivDateEnd").getValue(Long.class) != null ? new Date(snapshot.child("resivDateEnd").getValue(Long.class)) : null;

                        if (resivBy != null && resivBy.equals(userId)) {
                            noRoom.setText(room);
                            if (isCheck != null && isCheck) {
                                checkin.setText(function.convertDateToString(checkInDateMillis != null ? new Date(checkInDateMillis) : null));
                            }
                            if (startDateMillis != null) {
                                startDateTV.setText(function.convertDateToString(startDateMillis != null ? new Date(startDateMillis) : null));
                            }
                            if (endDateMillis != null) {
                                endDateTV.setText(function.convertDateToString(endDateMillis != null ? new Date(endDateMillis) : null));
                            }
                            checkoutprosses(endDate);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "Failed to load room data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void checkoutprosses(Date endDate) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Calendar todayCalender = Calendar.getInstance();
            Date today = todayCalender.getTime();
            if (endDate.before(today) || endDate.equals(today)) {
                if (room != null) {
                    DatabaseReference roomRef = mDatabase.child("room").child(room);
                    Map<String, Object> roomData = new HashMap<>();
                    Map<String, Object> checkinData = new HashMap<>();
                    checkinData.put("isCheck", false);
                    checkinData.put("date", new Date().getTime());
                    roomData.put("checkIn", checkinData);
                    roomData.put("resivBy", "");
                    roomData.put("status", false);

                    roomRef.updateChildren(roomData).addOnCompleteListener(task -> {
                        room = null;
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Failed to update room data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        } else {
            startActivity(new Intent(getActivity(), Login.class));
            getActivity().finish();
        }
    }
}
