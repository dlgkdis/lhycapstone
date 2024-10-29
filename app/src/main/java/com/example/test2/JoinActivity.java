// JoinActivity.java
package com.example.test2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class JoinActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login2);  // 적절한 레이아웃 파일을 설정해주세요

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 동적 링크 수신 및 그룹 ID 확인
        handleDynamicLink();
    }

    private void handleDynamicLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        Uri deepLink = pendingDynamicLinkData.getLink();
                        if (deepLink != null) {
                            groupId = deepLink.getQueryParameter("groupId");
                            if (auth.getCurrentUser() == null) {
                                // 로그인 요청
                                startActivity(new Intent(this, LoginActivity.class));
                            } else {
                                // 그룹에 추가
                                addToGroup();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("JoinActivity", "Dynamic Link Error: ", e));
    }

    private void addToGroup() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && groupId != null) {
            String userId = user.getUid();
            String userEmail = user.getEmail();  // 사용자 이메일 가져오기

            // 그룹에 invitedUserId와 inviteUserEmail을 업데이트
            db.collection("groups").document(groupId)
                    .update("invitedUserId", userId, "inviteUserEmail", userEmail)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "그룹에 성공적으로 가입되었습니다!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Log.e("JoinActivity", "그룹 가입 실패: ", e));
        } else {
            Log.e("JoinActivity", "User or Group ID is null");
        }
    }

}
