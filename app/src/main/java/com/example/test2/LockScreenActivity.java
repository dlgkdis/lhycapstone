package com.example.test2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class LockScreenActivity extends AppCompatActivity {

    private ArrayList<Integer> enteredPassword = new ArrayList<>();
    private String storedPassword;
    private ImageButton[] tacoButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_screen);  // 비밀번호가 설정된 경우 lock_screen.xml 표시

        // 비밀번호를 불러옴
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        storedPassword = sharedPreferences.getString("password", "");

        // taco 이미지 버튼 배열 초기화
        tacoButtons = new ImageButton[] {
                findViewById(R.id.imageButton51),
                findViewById(R.id.imageButton49),
                findViewById(R.id.imageButton50),
                findViewById(R.id.imageButton48)
        };

        setupPasswordButtons();
    }

    private void setupPasswordButtons() {
        findViewById(R.id.password1).setOnClickListener(v -> onNumberButtonClicked(1));
        findViewById(R.id.password2).setOnClickListener(v -> onNumberButtonClicked(2));
        findViewById(R.id.password3).setOnClickListener(v -> onNumberButtonClicked(3));
        findViewById(R.id.password4).setOnClickListener(v -> onNumberButtonClicked(4));
        findViewById(R.id.password5).setOnClickListener(v -> onNumberButtonClicked(5));
        findViewById(R.id.password6).setOnClickListener(v -> onNumberButtonClicked(6));
        findViewById(R.id.password7).setOnClickListener(v -> onNumberButtonClicked(7));
        findViewById(R.id.password8).setOnClickListener(v -> onNumberButtonClicked(8));
        findViewById(R.id.password9).setOnClickListener(v -> onNumberButtonClicked(9));
        findViewById(R.id.password0).setOnClickListener(v -> onNumberButtonClicked(0));

        findViewById(R.id.passwordback).setOnClickListener(v -> onBackButtonClicked()); // 뒤로가기 버튼
    }

    private void onNumberButtonClicked(int number) {
        if (enteredPassword.size() < 4) {
            enteredPassword.add(number);
            updatePasswordDisplay();

            // 4개의 숫자가 입력된 경우 비밀번호 확인
            if (enteredPassword.size() == 4) {
                checkPassword();
            }
        }
    }


    private void onBackButtonClicked() {
        if (!enteredPassword.isEmpty()) {
            enteredPassword.remove(enteredPassword.size() - 1);
            updatePasswordDisplay();
        }
    }

    private void updatePasswordDisplay() {
        for (int i = 0; i < tacoButtons.length; i++) {
            if (i < enteredPassword.size()) {
                tacoButtons[i].setImageResource(R.drawable.octopus); // 비밀번호 입력된 자리 octopus로 변경
            } else {
                tacoButtons[i].setImageResource(R.drawable.taco1); // 기본 taco 이미지
            }
        }
    }

    private void checkPassword() {
        if (enteredPassword.size() == 4) {
            StringBuilder enteredPass = new StringBuilder();
            for (int num : enteredPassword) {
                enteredPass.append(num);
            }

            if (enteredPass.toString().equals(storedPassword)) {
                // 비밀번호가 맞으면 플래그 저장
                SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("password_verified", true);
                editor.apply();

                // MainActivity로 이동
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
                enteredPassword.clear();
                updatePasswordDisplay();
            }
        }
    }

}