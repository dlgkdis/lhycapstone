package com.example.test2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
                findViewById(R.id.imageButton49),
                findViewById(R.id.imageButton51),
                findViewById(R.id.imageButton50),
                findViewById(R.id.imageButton48)
        };

        // 숫자 버튼 초기화
        int[] buttonIds = {
                R.id.imageButton37, R.id.imageButton38, R.id.imageButton39,
                R.id.imageButton40, R.id.imageButton41, R.id.imageButton42,
                R.id.imageButton43, R.id.imageButton44, R.id.imageButton45,
                R.id.imageButton46
        };

        // 숫자 버튼 클릭 리스너 설정
        for (int i = 0; i < buttonIds.length; i++) {
            int number = i; // 0부터 9까지의 숫자
            ImageButton button = findViewById(buttonIds[i]);
            button.setOnClickListener(v -> addDigit(number));
        }

        // 뒤로가기 버튼 설정
        ImageButton backButton = findViewById(R.id.imageButton47);
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
        StringBuilder password = new StringBuilder();
        for (int digit : passwordDigits) {
            password.append(digit);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_password", password.toString());
        editor.apply();

        Toast.makeText(this, "비밀번호가 설정되었습니다.", Toast.LENGTH_SHORT).show();

        // 설정 후 MainActivity로 이동
        startActivity(new Intent(PasswordSettingActivity.this, MainActivity.class));
        finish(); // PasswordSettingActivity 종료
    }
}