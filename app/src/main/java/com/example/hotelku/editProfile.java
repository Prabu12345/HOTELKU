package com.example.hotelku;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class editProfile extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText nameEt, dateEt, addressEt;
    private RadioGroup genderGroup;
    private Button cancelBtn, editBtn;

    void initUI() {
        cancelBtn = findViewById(R.id.cancelButton);
        editBtn = findViewById(R.id.editButton);
        nameEt = findViewById(R.id.nameEditText);
        dateEt = findViewById(R.id.bodEditText);
        addressEt = findViewById(R.id.addressEditText);
        genderGroup = findViewById(R.id.jenisKelamin);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editprofile);

        initUI();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserProfile();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });

        dateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
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
                            String gender = documentSnapshot.getString("gender");
                            String bod = documentSnapshot.getString("birthdate");
                            String address = documentSnapshot.getString("address");

                            nameEt.setText(username);
                            if (gender != null) {
                                if (gender.equals("Male")) {
                                    genderGroup.check(R.id.radioMale);
                                } else if (gender.equals("Female")) {
                                    genderGroup.check(R.id.radioFemale);
                                }
                            }
                            dateEt.setText(bod);
                            addressEt.setText(address);
                        } else {
                            Toast.makeText(editProfile.this, "User profile not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(editProfile.this, "Failed to load user profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            finish();
        }
    }

    private void showDatePicker() {
        // Buat MaterialDatePicker dengan tipe DATE
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Pilih Tanggal Lahir");

        final MaterialDatePicker<Long> materialDatePicker = builder.build();

        // Set listener saat tanggal dipilih
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            // Format tanggal yang dipilih
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = sdf.format(new Date(selection));
            dateEt.setText(formattedDate);
        });

        // Tampilkan date picker
        materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void saveUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String username = nameEt.getText().toString().trim();
            String address = addressEt.getText().toString().trim();
            String date = dateEt.getText().toString().trim();
            int selectedGenderId = genderGroup.getCheckedRadioButtonId();
            if (selectedGenderId == -1) {
                Toast.makeText(editProfile.this, "Gender is required", Toast.LENGTH_SHORT).show();
                return;
            }
            String gender = selectedGenderId == R.id.radioMale ? "Male" : "Female";

            Map<String, Object> userData = new HashMap<>();
            userData.put("username", username);
            userData.put("birthdate", date);
            userData.put("address", address);
            userData.put("gender", gender);

            DocumentReference userRef = db.collection("users").document(userId);
            userRef.update(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(editProfile.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(editProfile.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
