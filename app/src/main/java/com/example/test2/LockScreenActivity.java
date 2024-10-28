package com.example.test2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

        // 비밀번호가 설정되어 있는지 확인
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        storedPassword = sharedPreferences.getString("password", "");

        if (storedPassword.isEmpty()) {
            // 비밀번호가 설정되지 않은 경우 PasswordSettingActivity로 이동
            startActivity(new Intent(this, PasswordSettingActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.lock_screen);  // 비밀번호가 설정된 경우 lock_screen.xml 표시

        // taco 이미지 버튼 배열 초기화
        tacoButtons = new ImageButton[] {
                findViewById(R.id.imageButton49),
                findViewById(R.id.imageButton51),
                findViewById(R.id.imageButton50),
                findViewById(R.id.imageButton48)
        };

        setupPasswordButtons();
    }

    private void setupPasswordButtons() {
        findViewById(R.id.imageButton37).setOnClickListener(v -> onNumberButtonClicked(1));
        findViewById(R.id.imageButton38).setOnClickListener(v -> onNumberButtonClicked(2));
        findViewById(R.id.imageButton39).setOnClickListener(v -> onNumberButtonClicked(3));
        findViewById(R.id.imageButton40).setOnClickListener(v -> onNumberButtonClicked(4));
        findViewById(R.id.imageButton41).setOnClickListener(v -> onNumberButtonClicked(5));
        findViewById(R.id.imageButton42).setOnClickListener(v -> onNumberButtonClicked(6));
        findViewById(R.id.imageButton43).setOnClickListener(v -> onNumberButtonClicked(7));
        findViewById(R.id.imageButton44).setOnClickListener(v -> onNumberButtonClicked(8));
        findViewById(R.id.imageButton45).setOnClickListener(v -> onNumberButtonClicked(9));
        findViewById(R.id.imageButton46).setOnClickListener(v -> onNumberButtonClicked(0));

        findViewById(R.id.imageButton47).setOnClickListener(v -> onBackButtonClicked()); // 뒤로가기 버튼
    }

    private void onNumberButtonClicked(int number) {
        if (enteredPassword.size() < 4) {
            enteredPassword.add(number);
            updatePasswordDisplay();
            checkPassword();
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
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
                enteredPassword.clear();
                updatePasswordDisplay(); // 잘못된 비밀번호 입력 시 표시 초기화
            }
        }
    }
}