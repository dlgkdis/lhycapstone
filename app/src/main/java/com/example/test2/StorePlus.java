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
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store 액티비티로 돌아가기
                Intent intent = new Intent(StorePlus.this, Store.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });
    }
}
