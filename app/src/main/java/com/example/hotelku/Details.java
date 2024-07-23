package com.example.hotelku;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

public class Details extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    TextView room, kelas, status, price, totalPrice;
    EditText startDate, endDate;
    Button payButton;
    ImageButton backButton;
    Long prices;

    void initUI() {
        room = findViewById(R.id.NoRoom);
        kelas = findViewById(R.id.Class);
        status = findViewById(R.id.availabilityDetail);
        price = findViewById(R.id.price);
        totalPrice = findViewById(R.id.TotalPrice);
        startDate = findViewById(R.id.editTextStart);
        endDate = findViewById(R.id.editTextEnd);
        payButton = findViewById(R.id.reservationPay);
        backButton = findViewById(R.id.backButton);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resirvdetail);

        initUI();

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("room"); // Referensi ke Realtime Database

        Intent terima = getIntent();
        loadUserProfile(terima.getStringExtra("room"));

        startDate.addTextChangedListener(textWatcher);
        endDate.addTextChangedListener(textWatcher);

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                function.showDatePickerStartDate(Details.this, startDate);
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startDate.getText().toString().isEmpty()) {
                    Toast.makeText(Details.this, "Please select start date first.", Toast.LENGTH_SHORT).show();
                    return;
                }
                function.showDatePickerEndDate(Details.this, endDate, function.dateToLocalDate(function.convertStringToDate(startDate.getText().toString())));
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    if (startDate == null || endDate == null) {
                        Toast.makeText(Details.this, "View startDate or endDate has not been initialized.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (startDate.getText().toString().isEmpty() || endDate.getText().toString().isEmpty()) {
                        Toast.makeText(Details.this, "Reservation dates required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        LocalDate start = function.dateToLocalDate(function.convertStringToDate(startDate.getText().toString()));
                        LocalDate end = function.dateToLocalDate(function.convertStringToDate(endDate.getText().toString()));

                        Intent send = new Intent(Details.this, Payments.class);
                        send.putExtra("room", terima.getStringExtra("room"));
                        send.putExtra("startDate", function.convertStringToDate(startDate.getText().toString()).getTime());
                        send.putExtra("endDate", function.convertStringToDate(endDate.getText().toString()).getTime());
                        send.putExtra("totalPrices", calculateTotalPrice(prices, start, end));
                        startActivity(send);
                        finish();
                    } catch (NullPointerException e) {
                        Toast.makeText(Details.this, "There is an error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    startActivity(new Intent(Details.this, Login.class));
                    finish();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadUserProfile(String selectedRoom) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            dbRef.get().addOnSuccessListener(dataSnapshot -> {
                if (dataSnapshot.exists()) {
                    Boolean isFound = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String resivCheck =  snapshot.child("resivBy").getValue(String.class);
                        if (resivCheck.equals(userId)) {
                            isFound = true;
                            break;
                        }
                    }

                    if (!isFound) {
                        dbRef.child(selectedRoom).addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String classType = dataSnapshot.child("classType").getValue(String.class);
                                prices = dataSnapshot.child("basePrice").getValue(Long.class);
                                Boolean availa = dataSnapshot.child("status").getValue(Boolean.class);
                                String resivBy = dataSnapshot.child("resivBy").getValue(String.class);

                                if (resivBy != null && resivBy.equals(userId)) {
                                    Toast.makeText(Details.this, "You have Reserved a room.", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }

                                room.setText(selectedRoom);
                                kelas.setText(classType);
                                status.setText(function.toAvailable(availa));
                                if (prices != null) {
                                    price.setText(function.formatCurrency(prices));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(Details.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(Details.this, "You have booked a room.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(Details.this, "Detail room not found.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(Details.this, "Failed to load detail room: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            startActivity(new Intent(Details.this, Login.class));
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Long calculateTotalPrice(Long price, LocalDate dateStart, LocalDate dateEnd) {
        long daysBetween = ChronoUnit.DAYS.between(dateStart, dateEnd);
        return price * daysBetween;
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Tidak diperlukan tindakan sebelum teks berubah
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Tidak diperlukan tindakan saat teks berubah
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void afterTextChanged(Editable s) {
            if (!startDate.getText().toString().isEmpty() && !endDate.getText().toString().isEmpty()) {
                try {
                    LocalDate start = function.dateToLocalDate(function.convertStringToDate(startDate.getText().toString()));
                    LocalDate end = function.dateToLocalDate(function.convertStringToDate(endDate.getText().toString()));

                    if (start != null && end != null && prices != null) {
                        Long totalPriceLong = calculateTotalPrice(prices, start, end);
                        totalPrice.setText(function.formatCurrency(totalPriceLong));
                    } else {
                        Toast.makeText(Details.this, "Invalid date", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(Details.this, "Invalid price", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
