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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.hotelku.module.function;
import com.example.hotelku.module.userModel;

import java.util.Date;

public class Register extends AppCompatActivity {

    private EditText namaET, passwordET, tanggalLahirET, alamatET, emailET;
    private RadioGroup jenisKelamin;
    private Button button;

    private FirebaseAuth mAuth;
    private DatabaseReference db;

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
        db = FirebaseDatabase.getInstance().getReference("users"); // Initialize Realtime Database reference

        tanggalLahirET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                function.showDatePickerBOD(Register.this, tanggalLahirET);
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
                            if (user != null) {
                                String userId = user.getUid();

                                userModel profile = new userModel(username, email, address, function.convertStringToDate(birthdate).getTime(), gender, getString(R.string.defaultAvatar));

                                db.child(userId).setValue(profile)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(Register.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Register.this, MainActivity.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(Register.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
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
}
