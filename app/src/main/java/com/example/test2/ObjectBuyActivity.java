package com.example.test2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ObjectBuyActivity extends AppCompatActivity {

    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.object_buy);

        itemId = getIntent().getStringExtra("itemId");
        int imageResource = getIntent().getIntExtra("imageResource", R.drawable.shop1); // 기본 이미지 설정

        // 전달받은 이미지 리소스를 imageView33에 설정
        ImageView imageView33 = findViewById(R.id.imageView33);
        imageView33.setImageResource(imageResource);

        // "구입" 버튼 설정
        Button buyButton = findViewById(R.id.button2);
        buyButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("itemId", itemId);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // "뒤로가기" 버튼 설정
        ImageButton backButton = findViewById(R.id.imageButton56);
        backButton.setOnClickListener(v -> finish());
    }
}