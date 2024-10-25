package com.example.test2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.test2.databinding.ActivityMainBinding;
import com.example.test2.ui.main.MainFragment;
import com.example.test2.ui.tema.TemaFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements TemaFragment.OnThemeSelectedListener {

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
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_main,
                R.id.navigation_calendar,
                R.id.navigation_diary,
                R.id.navigation_tema)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);
    }

    // TemaFragment에서 호출되는 onThemeSelected 메서드 구현
    @Override
    public void onThemeSelected(String theme) {
        // 현재 표시 중인 MainFragment를 찾고 테마 변경 요청
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment navHostFragment = fragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main);

        if (navHostFragment != null) {
            Fragment currentFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
            if (currentFragment instanceof MainFragment) {
                ((MainFragment) currentFragment).updateBackground(theme);
            }
        }
    }
}
