package com.example.test2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login2); // 두 번째 XML 파일 (예: login_layout.xml)

        // 뒤로가기 버튼 (imageButton56) 설정
        ImageButton backButton = findViewById(R.id.imageButton56);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();  // 현재 액티비티 종료 후 이전 화면으로 돌아가기
            }
        });
    }
}
