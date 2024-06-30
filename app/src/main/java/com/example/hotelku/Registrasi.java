package com.example.hotelku;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class Registrasi extends AppCompatActivity {

    private EditText nama, password, tanggalLahir, alamat, email;
    private RadioGroup jenisKelamin;
    private RadioButton pilihanJenisKelamin;
    private String inputPassword, inputNama, inputPilihanJenisKelamin, inputTanggalLahir, inputAlamat, inputEmail;
    private Button button;
    private int idPilihanJenisKelamin, tahun, bulan, tanggal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nama = findViewById(R.id.editTextNama);
        password = findViewById(R.id.password);
        jenisKelamin = findViewById(R.id.jenisKelamin);
        tanggalLahir = findViewById(R.id.editTextDate);
        alamat = findViewById(R.id.editTextAlamat);
        email = findViewById(R.id.email);
        button = findViewById(R.id.buttonRegister);

        tanggalLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar kalender = Calendar.getInstance();
                tahun = kalender.get(Calendar.YEAR);
                bulan = kalender.get(Calendar.MONTH);
                tanggal = kalender.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog;
                dialog = new DatePickerDialog(Registrasi.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tahun = year;
                        bulan = month;
                        tanggal = dayOfMonth;

                        tanggalLahir.setText(tanggal + "-" + (bulan + 1) + "-" + tahun);
                    }
                }, tahun, bulan, tanggal);
                dialog.show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idPilihanJenisKelamin = jenisKelamin.getCheckedRadioButtonId();
                pilihanJenisKelamin = findViewById(idPilihanJenisKelamin);

                inputNama = nama.getText().toString();
                inputPassword = password.getText().toString();
                inputPilihanJenisKelamin = pilihanJenisKelamin.getText().toString();
                inputTanggalLahir = tanggalLahir.getText().toString();
                inputAlamat = alamat.getText().toString();
                inputEmail = email.getText().toString();

                if (inputNama.length() < 1 || inputPassword.length() < 1 || inputPilihanJenisKelamin.length() < 1 || inputAlamat.length() < 1 || inputEmail.length() < 1) {
                    Toast.makeText(getApplicationContext(), "Tolong Isi kolom", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent kirim = new Intent(Registrasi.this, MainActivity.class);
                kirim.putExtra("nama", inputNama);
                kirim.putExtra("password", inputPassword);
                kirim.putExtra("jenisKelamin", inputPilihanJenisKelamin);
                kirim.putExtra("tanggalLahir", inputTanggalLahir);
                kirim.putExtra("alamat", inputAlamat);
                kirim.putExtra("email", inputEmail);
                startActivity(kirim);
                finish();
            }
        });
    }
}