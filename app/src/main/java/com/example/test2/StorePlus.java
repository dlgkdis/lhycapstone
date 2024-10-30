// StorePlus.java
package com.example.test2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class StorePlus extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_plus); // store_plus.xml 레이아웃 설정

        // 뒤로가기 버튼 클릭 이벤트 추가
        ImageButton backButton = findViewById(R.id.imageButton36);
        backButton.setOnClickListener(v -> finish());
    }
}
