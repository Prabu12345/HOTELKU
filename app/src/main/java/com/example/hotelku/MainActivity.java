package com.example.hotelku;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private FrameLayout container;
    private BottomNavigationView botNav;
    private fragmentHome homeFragment = new fragmentHome();
    private fragmentProfile profileFragment = new fragmentProfile();


    void initUI() {
        container = findViewById(R.id.container);
        botNav = findViewById(R.id.bottomNav);
    }

    void transisiFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        initUI();
        transisiFragment(homeFragment);

        botNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_home) {
                    transisiFragment(homeFragment);
                } else if(id == R.id.menu_profile) {
                    transisiFragment(profileFragment);
                }
                return true;
            }
        });
    }
}
