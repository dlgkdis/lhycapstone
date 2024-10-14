package com.example.test2;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.test2.databinding.ActivityMainBinding;

import android.content.Intent; // Intent 클래스 import
import android.view.View;      // View 클래스 import
import android.widget.ImageButton;

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

        // BottomNavigationView와 NavController 연결
        NavigationUI.setupWithNavController(navView, navController);

        // ImageButton 찾기 (id가 bell인 이미지 버튼)
        ImageButton imageButton = findViewById(R.id.imageButton65);

        // 버튼 클릭 리스너 설정
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SecondActivity로 전환
                Intent intent = new Intent(MainActivity.this, Bell.class);
                startActivity(intent);
            }
        });

        ImageButton imageButton2 = findViewById(R.id.imageButton60);

        // 버튼 클릭 리스너 설정
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SecondActivity로 전환
                Intent intent = new Intent(MainActivity.this, Store.class);
                startActivity(intent);
            }
        });

        ImageButton imageButton3 = findViewById(R.id.imageButton63);

        // 버튼 클릭 리스너 설정
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SecondActivity로 전환
                Intent intent = new Intent(MainActivity.this, Person.class);
                startActivity(intent);
            }
        });

        ImageButton imageButton4 = findViewById(R.id.imageButton62);

        // 버튼 클릭 리스너 설정
        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SecondActivity로 전환
                Intent intent = new Intent(MainActivity.this,Reward.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}
