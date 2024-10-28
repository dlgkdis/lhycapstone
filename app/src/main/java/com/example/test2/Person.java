package com.example.test2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Person extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);  // 첫 번째 XML 파일 (예: main_layout.xml)

        // 뒤로가기 버튼 (imageButton82) 설정
        ImageButton backButton = findViewById(R.id.imageButton82);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Person.this, MainActivity.class);
                startActivity(intent);  // MainActivity 시작
            }
        });

        // 로그인 버튼 클릭 시 LoginActivity로 이동
        ImageButton loginButton = findViewById(R.id.LoginButton1);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Person.this, LoginActivity.class);
                startActivity(intent);  // LoginActivity 시작
            }
        });

        ImageButton passwordButton = findViewById(R.id.pwsettingButton);
        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Person.this, PasswordSettingActivity.class);
                startActivity(intent);
            }
        });
    }
}