package com.example.test2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LeaveActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String groupId; // 그룹 ID
    private static final String TAG = "LeaveActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // 그룹 탈퇴 버튼 초기화 및 클릭 리스너 설정
        Button leaveGroupButton = findViewById(R.id.leaveGroupButton);
        leaveGroupButton.setOnClickListener(v -> {
            Log.d(TAG, "Leave group button clicked");
            leaveGroup();
        });

        // 이전 화면으로 돌아가는 버튼
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // 그룹 ID 가져오기
        groupId = getIntent().getStringExtra("groupId");
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Log.d(TAG, "Group ID: " + groupId);
        Log.d(TAG, "Current User: " + (currentUser != null ? currentUser.getEmail() : "null"));

    }

    private void leaveGroup() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null || groupId == null) {
            Log.e(TAG, "Current user or Group ID is null");
            return;
        }

        // Firestore에서 그룹 문서 삭제
        Log.d(TAG, "Attempting to delete Firestore document");
        db.collection("groups").document(groupId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Group successfully deleted");
                    createUserData(currentUser.getUid()); // 그룹 탈퇴 후 개인 데이터 생성
                    Toast.makeText(LeaveActivity.this, "그룹이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    finish(); // 작업 후 액티비티 닫기
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete group: ", e);
                    Toast.makeText(LeaveActivity.this, "그룹 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    private void createUserData(String userId) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("coinStatus", 0); // 초기 코인 현황
        userData.put("purchasedObjects", new ArrayList<>()); // 구매한 오브제 목록
        userData.put("purchasedThemes", new ArrayList<>()); // 구매한 테마 목록
        userData.put("diaries", new ArrayList<>()); // 작성한 일기
        userData.put("calendarSchedules", new ArrayList<>()); // 캘린더 일정

        db.collection("users").document(userId).set(userData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "초기 개인 데이터 설정 완료"))
                .addOnFailureListener(e -> Log.e(TAG, "초기 개인 데이터 설정 실패", e));
    }
}
