package com.example.test2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.test2.databinding.ActivityMainBinding;
import com.example.test2.ui.ThemeViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private ThemeViewModel themeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Data Binding을 사용하여 레이아웃 설정
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 저장된 비밀번호 가져오기 (예: SharedPreferences에서 불러오기)
        SharedPreferences sharedPreferences1 = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String storedPassword = sharedPreferences1.getString("password", "");
        SharedPreferences sharedPreferences2 = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean isPasswordVerified = sharedPreferences2.getBoolean("password_verified", false);

        if (!storedPassword.isEmpty()) {
            if (!isPasswordVerified) {
                // 비밀번호가 확인되지 않았다면 LockScreenActivity로 이동
                startActivity(new Intent(this, LockScreenActivity.class));
                finish();
                return;
            }
        }

        // ViewModel 초기화
        themeViewModel = new ViewModelProvider(this).get(ThemeViewModel.class);

        // BottomNavigationView 설정
        BottomNavigationView navView = binding.navView;
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_main,
                R.id.navigation_calendar,
                R.id.navigation_diary,
                R.id.navigation_tema)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);

    }
    @Override
    protected void onStop() {
        super.onStop();

        // 앱 종료 시 비밀번호 인증 플래그 초기화
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("password_verified", false);
        editor.apply();
    }

}