package com.example.test2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class PasswordSettingActivity extends AppCompatActivity {

    private ArrayList<Integer> passwordDigits = new ArrayList<>();
    private static final int PASSWORD_LENGTH = 4;  // 비밀번호는 4자리
    private SharedPreferences sharedPreferences;

    // taco 이미지 버튼 배열
    private ImageButton[] tacoButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_setting);

        sharedPreferences = getSharedPreferences("PasswordPrefs", MODE_PRIVATE);

        // taco 이미지 버튼 초기화
        tacoButtons = new ImageButton[] {
                findViewById(R.id.imageButton51),
                findViewById(R.id.imageButton49),
                findViewById(R.id.imageButton50),
                findViewById(R.id.imageButton48)
        };

        // 숫자 버튼 초기화
        int[] buttonIds = {
                R.id.password0,
                R.id.password1, R.id.password2, R.id.password3,
                R.id.password4, R.id.password5, R.id.password6,
                R.id.password7, R.id.password8, R.id.password9
        };

        // 숫자 버튼 클릭 리스너 설정
        for (int i = 0; i < buttonIds.length; i++) {
            int number = i; // 0부터 9까지의 숫자
            ImageButton button = findViewById(buttonIds[i]);
            button.setOnClickListener(v -> addDigit(number));
        }

        // 뒤로가기 버튼 설정
        ImageButton backButton = findViewById(R.id.passwordback);
        backButton.setOnClickListener(v -> removeLastDigit());
    }

    private void addDigit(int digit) {
        if (passwordDigits.size() < PASSWORD_LENGTH) {
            passwordDigits.add(digit);
            updatePasswordDisplay();

            if (passwordDigits.size() == PASSWORD_LENGTH) {
                savePassword();
            }
        } else {
            Toast.makeText(this, "비밀번호는 4자리여야 합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeLastDigit() {
        if (!passwordDigits.isEmpty()) {
            passwordDigits.remove(passwordDigits.size() - 1);
            updatePasswordDisplay();
        }
    }

    private void updatePasswordDisplay() {
        // 입력된 자릿수에 따라 taco 버튼을 octopus 이미지로 업데이트
        for (int i = 0; i < tacoButtons.length; i++) {
            if (i < passwordDigits.size()) {
                tacoButtons[i].setImageResource(R.drawable.octopus); // 입력된 자리수만큼 octopus로 변경
            } else {
                tacoButtons[i].setImageResource(R.drawable.taco1); // 기본 taco 이미지로 되돌림
            }
        }
    }

    private void savePassword() {
        // 비밀번호를 문자열로 변환
        StringBuilder passwordBuilder = new StringBuilder();
        for (int digit : passwordDigits) {
            passwordBuilder.append(digit);
        }
        String password = passwordBuilder.toString();

        // SharedPreferences에 비밀번호 저장
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("password", password);
        editor.apply();

        // 비밀번호 설정 완료 알림
        Toast.makeText(this, "비밀번호가 설정되었습니다.", Toast.LENGTH_SHORT).show();

        // 설정 완료 후 MainActivity로 이동
        Intent intent = new Intent(PasswordSettingActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // PasswordSettingActivity 종료
    }
}