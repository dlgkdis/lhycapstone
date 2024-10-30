package com.example.test2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;



import androidx.appcompat.app.AppCompatActivity;

public class Bell extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bell);


        // imageButton56 (뒤로가기 버튼) 클릭 이벤트 처리
        ImageButton backButton = findViewById(R.id.backButton);

// setOnClickListener 메서드를 통해 클릭 이벤트 처리
        backButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v){
                // 다른 액티비티로 전환하기 위한 Intent
                Intent intent = new Intent(Bell.this, MainActivity.class);
                startActivity(intent);  // MainActivity 시작
            }
        });
    }


}







