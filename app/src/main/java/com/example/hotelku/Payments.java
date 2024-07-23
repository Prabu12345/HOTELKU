package com.example.hotelku;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelku.module.function;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Payments extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    TextView tvroom, tvkelas, tvstatus, tvprice, tvtotalPrice;
    String room;
    Long totalPrices;
    Date startDate, endDate;
    Long prices;
    EditText etCard, etExpiry, etCVV;
    ImageButton backButton;
    Button payButton;

    void InitUI() {
        tvroom = findViewById(R.id.NoRoom);
        tvkelas = findViewById(R.id.Class);
        tvstatus = findViewById(R.id.availabilityDetail);
        tvprice = findViewById(R.id.price);
        tvtotalPrice = findViewById(R.id.TotalPrice);
        etCard = findViewById(R.id.editTextCardNumber);
        etExpiry = findViewById(R.id.editTextExpiryDate);
        etCVV = findViewById(R.id.editTextCVV);
        payButton = findViewById(R.id.confirmPay);
        backButton = findViewById(R.id.backButton);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("room"); // Referensi ke Realtime Database

        InitUI();

        Intent terima = getIntent();
        room = terima.getStringExtra("room");
        totalPrices = terima.getLongExtra("totalPrices", -1);
        startDate = new Date(terima.getLongExtra("startDate", -1));
        endDate = new Date(terima.getLongExtra("endDate", -1));

        loadUserProfile(room);

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payButton.setEnabled(false);
                confirmationPayment();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadUserProfile(String selectedRoom) {
        dbRef.child(selectedRoom).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String classType = dataSnapshot.child("classType").getValue(String.class);
                prices = dataSnapshot.child("basePrice").getValue(Long.class);
                Boolean availa = dataSnapshot.child("status").getValue(Boolean.class);

                if (prices != null && availa != null) {
                    if (!availa) {
                        tvroom.setText(selectedRoom);
                        tvkelas.setText(classType);
                        tvstatus.setText(function.toAvailable(availa));
                        tvprice.setText(function.formatCurrency(prices));
                        tvtotalPrice.setText(function.formatCurrency(totalPrices));
                        payButton.setEnabled(true);
                    } else {
                        Toast.makeText(Payments.this, "The room has been booked.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(Payments.this, "Choose another room.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Payments.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void confirmationPayment() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            dbRef.child(room).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean availa = dataSnapshot.child("status").getValue(Boolean.class);

                    if (availa != null) {
                        if (!availa) {
                            if (etCard.getText().toString().isEmpty() || etCVV.getText().toString().isEmpty() || etExpiry.getText().toString().isEmpty()) {
                                Toast.makeText(Payments.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                                payButton.setEnabled(true);
                                return;
                            }

                            Map<String, Object> roomData = new HashMap<>();
                            roomData.put("resivBy", userId);
                            roomData.put("resivDateEnd", endDate.getTime());
                            roomData.put("resivDateStart", startDate.getTime());
                            roomData.put("status", true);

                            dbRef.child(room).updateChildren(roomData)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Payments.this, "Reservation successfully!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(Payments.this, "Failed to Reservation.", Toast.LENGTH_SHORT).show();
                                            payButton.setEnabled(true);
                                        }
                                    });
                        } else {
                            Toast.makeText(Payments.this, "The room has been booked.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(Payments.this, "Choose another roomn.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Payments.this, "Failed to update data.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            startActivity(new Intent(Payments.this, Login.class));
            finish();
        }
    }
}
