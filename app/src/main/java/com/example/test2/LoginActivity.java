package com.example.test2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 레이아웃 설정 먼저 호출
        setContentView(R.layout.login);

        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String storedPassword = sharedPreferences.getString("user_password", "");

        if (!storedPassword.isEmpty()) {
            // 비밀번호가 설정된 경우 LockScreenActivity로 이동
            Intent intent = new Intent(LoginActivity.this, LockScreenActivity.class);
            startActivity(intent);
            finish(); // LoginActivity 종료
        } else {
            // 비밀번호가 설정되지 않은 경우 MainActivity로 이동
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // LoginActivity 종료
        }

        // 비밀번호 설정 화면으로 이동하는 버튼 설정
        ImageButton passwordButton = findViewById(R.id.imageButton4);
        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PasswordSettingActivity.class);
                startActivity(intent);
            }
        });
    }
}