package com.example.hotelku;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelku.module.function;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Checkout extends AppCompatActivity {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference roomRef;

    TextView tvroom, tvkelas, tvprice, tvtotalPrice, tvstartDate, tvendDate;
    Button checkoutButton;
    ImageButton backButton;
    Long prices;
    String rooms;

    void initUI() {
        tvroom = findViewById(R.id.NoRoom);
        tvkelas = findViewById(R.id.Class);
        tvprice = findViewById(R.id.price);
        tvtotalPrice = findViewById(R.id.TotalPrice);
        tvstartDate = findViewById(R.id.StartDate);
        tvendDate = findViewById(R.id.EndDate);
        backButton = findViewById(R.id.backButton);
        checkoutButton = findViewById(R.id.checkoutButtons);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initUI();

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadUserProfile();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkoutProcess();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            roomRef = database.getReference("room");

            roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean found = false;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        rooms = snapshot.getKey();
                        String reserveBy = snapshot.child("resivBy").getValue(String.class);
                        String classType = snapshot.child("classType").getValue(String.class);
                        Boolean isChecked = snapshot.child("checkIn/isCheck").getValue(Boolean.class);
                        prices = snapshot.child("basePrice").getValue(Long.class);
                        Date startDate = new Date(snapshot.child("resivDateStart").getValue(Long.class));
                        Date endDate = new Date(snapshot.child("resivDateEnd").getValue(Long.class));

                        if (reserveBy != null && reserveBy.equals(userId)) {
                            if (isChecked != null && isChecked) {
                                tvroom.setText(rooms);
                                tvkelas.setText(classType);
                                if (prices != null) {
                                    tvprice.setText(function.formatCurrency(prices));
                                }
                                tvtotalPrice.setText(function.formatCurrency(calculateTotalPrice(prices, function.dateToLocalDate(startDate), function.dateToLocalDate(endDate))));
                                tvstartDate.setText(function.convertDateToString(startDate));
                                tvendDate.setText(function.convertDateToString(endDate));
                                checkoutButton.setEnabled(true);
                                found = true;
                                break;
                            } else {
                                tvroom.setText(rooms);
                                tvkelas.setText(classType);
                                if (prices != null) {
                                    tvprice.setText(function.formatCurrency(prices));
                                }
                                tvtotalPrice.setText(function.formatCurrency(calculateTotalPrice(prices, function.dateToLocalDate(startDate), function.dateToLocalDate(endDate))));
                                tvstartDate.setText(function.convertDateToString(startDate));
                                tvendDate.setText(function.convertDateToString(endDate));
                                checkoutButton.setEnabled(false);
                                checkoutButton.setText("Haven't checked in yet");
                                found = true;
                                break;
                            }
                        }
                    }

                    if (!found) {
                        Toast.makeText(Checkout.this, "You haven't made a reservation yet.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("Error getting data: " + databaseError.getMessage());
                }
            });
        } else {
            startActivity(new Intent(Checkout.this, Login.class));
            finish();
        }
    }

    @SuppressLint("SetTextI18n")
    private void checkoutProcess() {
        checkoutButton.setEnabled(false);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (rooms != null) {
                Map<String, Object> roomData = new HashMap<>();
                Map<String, Object> checkinData = new HashMap<>();
                checkinData.put("date", new Date().getTime());
                checkinData.put("isCheck", false);
                roomData.put("checkIn", checkinData);
                roomData.put("resivBy", "");
                roomData.put("status", false);

                DatabaseReference roomRef = database.getReference("room").child(rooms);
                roomRef.updateChildren(roomData)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Checkout.this, "Check-out successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(Checkout.this, "Failed to Check-out.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            startActivity(new Intent(Checkout.this, Login.class));
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Long calculateTotalPrice(Long price, LocalDate dateStart, LocalDate dateEnd) {
        long daysBetween = ChronoUnit.DAYS.between(dateStart, dateEnd);
        return price * (daysBetween);
    }
}