// Bell.java
package com.example.test2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        // 전체 삭제 버튼 설정
        Button clearAllButton = findViewById(R.id.clearAllButton);
        clearAllButton.setOnClickListener(v -> clearAllNotifications());

        // Firestore에서 알림 데이터 가져오기
        firebaseHelper.getNotifications(data -> {
            notificationList.clear();
            notificationList.addAll(data);

            // timestamp 기준으로 내림차순 정렬
            Collections.sort(notificationList, (o1, o2) -> {
                Long timestamp1 = (Long) o1.get("timestamp");
                Long timestamp2 = (Long) o2.get("timestamp");
                return timestamp2.compareTo(timestamp1); // 내림차순 정렬
            });

            notificationAdapter.notifyDataSetChanged();
        });
    }

    // 전체 알림 삭제 메서드
    private void clearAllNotifications() {
        firebaseHelper.clearAllNotifications(success -> {
            if (success) {
                notificationList.clear();
                notificationAdapter.notifyDataSetChanged();
                Toast.makeText(this, "모든 알림이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "알림 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
