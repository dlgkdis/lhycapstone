package com.example.test2;

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
}