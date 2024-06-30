package com.example.hotelku;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class fragmentHome extends Fragment {
    private TextView tampilPassword, tampilNama, tampilJenisKelamin, tampilTanggalLahir, tampilAlamat, tampilEmail;
    private String getPassword = "password", getNama = "nama", getJenisKelamin = "jenisKelamin", getTanggalLahir = "tanggalLahir", getAlamat = "alamat", getEmail = "email";
    private String setPassword, setNama, setJenisKelamin, setTanggalLahir, setAlamat, setEmail;
    private Button button;

    public fragmentHome() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_beranda, container, false);

        tampilNama = view.findViewById(R.id.namaCard);
        // tampilPassword = findViewById(R.id.nikValue);
        // tampilJenisKelamin = findViewById(R.id.jkValue);
        tampilTanggalLahir = view.findViewById(R.id.tanggalLahirCard);
        // tampilAlamat = findViewById(R.id.alamatValue);
        tampilEmail = view.findViewById(R.id.emailCard);

        Intent terima = getActivity().getIntent();
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
        return view;
    }
}