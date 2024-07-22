package com.example.hotelku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelku.module.function;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Reservation extends AppCompatActivity {
    private viewAdapter adapter;
    private RecyclerView recyclerView;
    private viewAdapter.OnItemClickListener listener;
    private ImageButton backBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    private void initUI() {
        backBtn = findViewById(R.id.backButton);
    }

    public interface RealtimeDatabaseCallback {
        void onCallback(List<detailModel> list);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        initUI();

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("room"); // Referensi ke Realtime Database

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fetchData();
    }

    private void fetchData() {
        getData(new RealtimeDatabaseCallback() {
            @Override
            public void onCallback(List<detailModel> list) {
                recyclerView = findViewById(R.id.recyclerView);
                listener = new viewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, String room) {
                        Intent send = new Intent(Reservation.this, Details.class);
                        send.putExtra("room", room);
                        startActivity(send);
                    }
                };
                adapter = new viewAdapter(list, getApplication(), listener);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(Reservation.this));
            }
        });
    }

    private void getData(RealtimeDatabaseCallback callback) {
        List<detailModel> list = new ArrayList<>();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String room = snapshot.getKey();
                        Boolean status = snapshot.child("status").getValue(Boolean.class);
                        String classType = snapshot.child("classType").getValue(String.class);

                        list.add(new detailModel(room, function.toAvailable(status), classType));
                    }
                    callback.onCallback(list);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Reservation.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
