package com.example.test2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ObjectArrangementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.object_arrangement);

        // Intent로 전달된 이미지 리소스를 가져옴
        Intent intent = getIntent();
        int imageResource = intent.getIntExtra("imageResource", -1);

        // imageView33에 이미지 설정
        ImageView imageView33 = findViewById(R.id.imageView33);
        if (imageResource != -1) {
            imageView33.setImageResource(imageResource);
        }

        // 뒤로가기 버튼 설정
        ImageButton backButton = findViewById(R.id.imageButton56);
        backButton.setOnClickListener(v -> finish());

        // "마이룸에 배치" 버튼
        Button arrangeButton = findViewById(R.id.button2);
        arrangeButton.setOnClickListener(v -> {
            // 마이룸 배치 관련 작업을 여기에 추가
        });
    }
}