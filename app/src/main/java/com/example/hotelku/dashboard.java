package com.example.hotelku;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class dashboard extends AppCompatActivity {
    private TextView tampilPassword, tampilNama, tampilJenisKelamin, tampilTanggalLahir, tampilAlamat, tampilEmail;
    private String getPassword = "password", getNama = "nama", getJenisKelamin = "jenisKelamin", getTanggalLahir = "tanggalLahir", getAlamat = "alamat", getEmail = "email";
    private String setPassword, setNama, setJenisKelamin, setTanggalLahir, setAlamat, setEmail;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tampilNama = findViewById(R.id.namaCard);
        // tampilPassword = findViewById(R.id.nikValue);
        // tampilJenisKelamin = findViewById(R.id.jkValue);
        tampilTanggalLahir = findViewById(R.id.tanggalLahirCard);
        // tampilAlamat = findViewById(R.id.alamatValue);
        tampilEmail = findViewById(R.id.emailCard);

        Intent terima = getIntent();
        setNama = terima.getStringExtra(getNama);
        setPassword = terima.getStringExtra(getPassword);
        setJenisKelamin = terima.getStringExtra(getJenisKelamin);
        setTanggalLahir = terima.getStringExtra(getTanggalLahir);
        setAlamat = terima.getStringExtra(getAlamat);
        setEmail = terima.getStringExtra(getEmail);

        tampilNama.setText(setNama);
        // tampilPassword.setText(setPassword);
        // tampilJenisKelamin.setText(setJenisKelamin);
        tampilTanggalLahir.setText(setTanggalLahir);
        // tampilAlamat.setText(setAlamat);
        tampilEmail.setText(setEmail);
    }
}