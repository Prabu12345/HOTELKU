package com.example.hotelku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.*;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;

public class Register extends AppCompatActivity {

    private EditText namaET, passwordET, tanggalLahirET, alamatET, emailET;
    private RadioGroup jenisKelamin;
    private Button button;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    void initUI() {
        namaET = findViewById(R.id.editTextNama);
        passwordET = findViewById(R.id.passEditText);
        jenisKelamin = findViewById(R.id.jenisKelamin);
        tanggalLahirET = findViewById(R.id.editTextDate);
        alamatET = findViewById(R.id.editTextAlamat);
        emailET = findViewById(R.id.email);
        button = findViewById(R.id.buttonRegister);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        initUI();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tanggalLahirET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String username = namaET.getText().toString().trim();
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String address = alamatET.getText().toString().trim();
        String birthdate = tanggalLahirET.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || birthdate.isEmpty()) {
            Toast.makeText(Register.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedGenderId = jenisKelamin.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(Register.this, "Gender is required", Toast.LENGTH_SHORT).show();
            return;
        }

        String gender = selectedGenderId == R.id.radioMale ? "Male" : "Female";

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();

                            UserProfile profile = new UserProfile(username, email, address, birthdate, gender);
                            db.collection("users").document(userId).set(profile)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(Register.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Register.this, MainActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(Register.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            String errorMessage = task.getException().getMessage();
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                errorMessage = "This email address is already in use.";
                            }
                            Toast.makeText(Register.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
            tanggalLahirET.setText(formattedDate);
        });

        // Tampilkan date picker
        materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private static class UserProfile {
        public String username;
        public String email;
        public String address;
        public String birthdate;
        public String gender;

        public UserProfile(String username, String email, String address, String birthdate, String gender) {
            this.username = username;
            this.email = email;
            this.address = address;
            this.birthdate = birthdate;
            this.gender = gender;
        }
    }
}