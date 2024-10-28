// Store.java
package com.example.test2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Store extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store);

        // "더보기" 버튼 클릭 이벤트
        Button moreButton = findViewById(R.id.button8); // 더보기 버튼 ID
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Store.this, StorePlus.class);
                startActivity(intent);  // StorePlus 액티비티 시작
            }
        });

        // 뒤로가기 버튼 클릭 이벤트
        ImageButton backButton = findViewById(R.id.imageButton81);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Store.this, MainActivity.class);
                startActivity(intent);  // MainActivity 시작
            }
        });
    }
}
