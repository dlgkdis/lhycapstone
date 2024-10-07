package com.example.test2;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.test2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Data Binding을 사용하여 레이아웃 설정
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // BottomNavigationView 설정
        BottomNavigationView navView = binding.navView;

        // AppBarConfiguration 설정
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_main,    // 수정된 부분
                R.id.navigation_calendar,
                R.id.navigation_diary,
                R.id.navigation_tema)    // 추가된 부분
                .build();

        // NavController 설정
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // ActionBar와 NavController 연결 (필요에 따라)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // BottomNavigationView와 NavController 연결
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}
