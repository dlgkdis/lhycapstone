// Bell.java
package com.example.test2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Bell extends AppCompatActivity {

    private FirebaseHelper firebaseHelper;
    private RecyclerView notificationRecyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Map<String, Object>> notificationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bell);

        // FirebaseHelper 초기화
        firebaseHelper = new FirebaseHelper();

        // 뒤로가기 버튼 클릭 이벤트
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // RecyclerView 초기화
        notificationRecyclerView = findViewById(R.id.notificationRecyclerView);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationAdapter = new NotificationAdapter(notificationList);
        notificationRecyclerView.setAdapter(notificationAdapter);

        // Firestore에서 알림 데이터 가져오기
        firebaseHelper.getNotifications(data -> {
            notificationList.clear();
            notificationList.addAll(data);
            notificationAdapter.notifyDataSetChanged();
        });
    }
}
