package com.example.test2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Reward extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewardpage);

        // imageButton66 (뒤로가기 버튼) 클릭 이벤트 처리
        ImageButton backButton = findViewById(R.id.imageButton66);
        backButton.setOnClickListener(v -> finish());
    }
}