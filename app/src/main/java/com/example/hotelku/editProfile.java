package com.example.hotelku;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hotelku.module.function;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class editProfile extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private StorageReference storageReference;
    private Uri avatarUri;

    private EditText nameEt, dateEt, addressEt;
    private RadioGroup genderGroup;
    private Button cancelBtn, editBtn;
    private ImageView avatarView;
    private ImageButton avatarButton;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    avatarUri = result.getData().getData();
                    function.loadAvatarIntoImageView(editProfile.this, avatarUri, avatarView);
                }
            }
    );

    void initUI() {
        cancelBtn = findViewById(R.id.cancelButton);
        editBtn = findViewById(R.id.editButton);
        nameEt = findViewById(R.id.nameEditText);
        dateEt = findViewById(R.id.bodEditText);
        addressEt = findViewById(R.id.addressEditText);
        genderGroup = findViewById(R.id.jenisKelamin);
        avatarView = findViewById(R.id.avatarImage);
        avatarButton = findViewById(R.id.editAvatarButton);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        initUI();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference("user_uploads/" + mAuth.getCurrentUser().getUid() + "/");

        loadUserProfile();

        avatarButton.setOnClickListener(v -> openFileChooser());

        cancelBtn.setOnClickListener(v -> finish());

        editBtn.setOnClickListener(v -> saveUserProfile());

        dateEt.setOnClickListener(v -> function.showDatePickerBOD(editProfile.this, dateEt));
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            db.child(userId).get().addOnSuccessListener(dataSnapshot -> {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String gender = dataSnapshot.child("gender").getValue(String.class);
                    Date bod = new Date(dataSnapshot.child("birthdate").getValue(Long.class));
                    String address = dataSnapshot.child("address").getValue(String.class);
                    String avatarUrl = dataSnapshot.child("avatarUrl").getValue(String.class);

                    function.loadAvatarIntoImageView(editProfile.this, avatarUrl, avatarView);
                    nameEt.setText(username);
                    if (gender != null) {
                        if (gender.equals("Male")) {
                            genderGroup.check(R.id.radioMale);
                        } else if (gender.equals("Female")) {
                            genderGroup.check(R.id.radioFemale);
                        }
                    }
                    dateEt.setText(function.convertDateToString(bod));
                    addressEt.setText(address);
                } else {
                    Toast.makeText(editProfile.this, "User profile not found.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(editProfile.this, "Failed to load user profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            finish();
        }
    }

    private void saveUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            editBtn.setEnabled(false);
            String userId = user.getUid();
            String username = nameEt.getText().toString().trim();
            String address = addressEt.getText().toString().trim();
            String date = dateEt.getText().toString().trim();
            int selectedGenderId = genderGroup.getCheckedRadioButtonId();

            // Pastikan gender sudah dipilih
            if (selectedGenderId == -1) {
                Toast.makeText(editProfile.this, "Gender is required", Toast.LENGTH_SHORT).show();
                return;
            }

            String gender = selectedGenderId == R.id.radioMale ? "Male" : "Female";

            // Pastikan avatarUri tidak null sebelum menggunakan Glide
            if (avatarUri != null) {
                // Menggunakan ExecutorService untuk menjalankan operasi Glide di background thread
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(() -> {
                    try {
                        Bitmap bitmap = Glide.with(editProfile.this)
                                .asBitmap()
                                .load(avatarUri)
                                .submit(256, 256) // Ukuran avatar yang diinginkan
                                .get();

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] avatarByte = baos.toByteArray();

                        // Jika avatarByte tidak null, unggah avatar ke Firebase Storage
                        StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

                        fileReference.putBytes(avatarByte)
                                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                    // Setelah berhasil mengunggah, update data pengguna di Realtime Database
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("username", username);
                                    userData.put("birthdate", function.convertStringToDate(date).getTime());
                                    userData.put("address", address);
                                    userData.put("gender", gender);
                                    userData.put("avatarUrl", uri.toString());

                                    db.child(userId).updateChildren(userData).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            runOnUiThread(() -> {
                                                Toast.makeText(editProfile.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            });
                                        } else {
                                            runOnUiThread(() -> Toast.makeText(editProfile.this, "Failed to update profile.", Toast.LENGTH_SHORT).show());
                                            editBtn.setEnabled(true);
                                        }
                                    });
                                }))
                                .addOnFailureListener(e -> {
                                    runOnUiThread(() -> Toast.makeText(editProfile.this, "Failed to upload avatar", Toast.LENGTH_SHORT).show());
                                    finish();
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(editProfile.this, "Failed to load avatar", Toast.LENGTH_SHORT).show());
                        finish();
                    } finally {
                        executor.shutdown();
                    }
                });
            } else {
                // Jika avatarUri null, update data pengguna di Realtime Database tanpa avatar
                Map<String, Object> userData = new HashMap<>();
                userData.put("username", username);
                userData.put("birthdate", function.convertStringToDate(date).getTime());
                userData.put("address", address);
                userData.put("gender", gender);

                db.child(userId).updateChildren(userData).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(editProfile.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(editProfile.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                        editBtn.setEnabled(true);
                    }
                });
            }
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(intent);
    }
}
