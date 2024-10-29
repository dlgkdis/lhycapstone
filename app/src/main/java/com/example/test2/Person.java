package com.example.test2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Person extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView userInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            setContentView(R.layout.login_complete); // 로그인 완료된 화면 설정
            userInfoText = findViewById(R.id.UserInfoText);
            userInfoText.setVisibility(View.VISIBLE); // 사용자 정보를 표시하도록 설정

            // 이메일 정보 설정
            userInfoText.setText(currentUser.getEmail());

            // 로그아웃 버튼 초기화
            ImageButton logoutButton = findViewById(R.id.LogoutButton1);
            logoutButton.setOnClickListener(v -> {
                mAuth.signOut();
                Toast.makeText(Person.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                recreate();
            });

            // 초대 버튼 초기화
            Button inviteButton = findViewById(R.id.inviteButton);
            inviteButton.setOnClickListener(v -> {
                Intent intent = new Intent(Person.this, InviteActivity.class);
                startActivity(intent);
            });

        } else {
            setContentView(R.layout.login); // 로그인 전 화면 설정

            // 로그인 화면의 버튼 초기화
            ImageButton loginButton = findViewById(R.id.LoginButton1);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Person.this, LoginActivity.class);
                    startActivity(intent); // LoginActivity 시작
                }
            });
        }

        // 공통으로 사용되는 버튼 초기화
        ImageButton backButton = findViewById(R.id.imageButton82);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Person.this, MainActivity.class);
                    startActivity(intent); // MainActivity 시작
                }
            });
        }

        // 비밀번호 설정 버튼 초기화 (공통 버튼)
        ImageButton passwordButton = findViewById(R.id.pwsettingButton);
        if (passwordButton != null) {
            passwordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Person.this, PasswordSettingActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}
