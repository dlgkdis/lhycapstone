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
import android.content.Intent; // Intent 클래스 import
import android.view.View;      // View 클래스 import
import android.widget.ImageButton;
import android.widget.ImageView; // ImageView 추가

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

// 이미지 버튼을 눌렀을 때 TemaActivity를 실행하도록 변경
        ImageButton temaButton = findViewById(R.id.imageButton65); // 테마 버튼
        temaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TemaActivity를 호출하고 결과를 받아옴
                Intent intent = new Intent(MainActivity.this, TemaActivity.class);
                startActivityForResult(intent, 1);  // 1은 requestCode
            }
        });

        // TemaActivity로부터 선택된 테마 데이터를 받아오는 코드 추가
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 1 && resultCode == RESULT_OK) {
                // TemaActivity에서 전달된 선택된 테마 데이터를 받음
                String selectedTheme = data.getStringExtra("selectedTheme");

                // 선택된 테마에 따라 이미지뷰 업데이트
                ImageView imageView = findViewById(R.id.imageView);  // main.xml의 ImageView
                switch (selectedTheme) {
                    case "tema_home":
                        imageView.setImageResource(R.drawable.myroom_basic);  // 해당 이미지 리소스로 변경
                        break;
                    case "tema_airport":
                        imageView.setImageResource(R.drawable.myroom_airport);  // 해당 이미지 리소스로 변경
                        break;
                    case "tema_submarine":
                        imageView.setImageResource(R.drawable.myroom_submarine);
                        break;
                    case "tema_loket":
                        imageView.setImageResource(R.drawable.myroom_locket);
                        break;
                    case "tema_island":
                        imageView.setImageResource(R.drawable.myroom_island);
                        break;
                    // 더 많은 테마 추가 가능
                }
            }
        }


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
