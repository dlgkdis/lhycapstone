package com.example.test2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InviteActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // 그룹 생성 및 초대 링크 전송 시작
        createGroupAndSendInvite();
    }

    private void createGroupAndSendInvite() {
        String groupId = db.collection("groups").document().getId();
        String ownerUserId = auth.getCurrentUser().getUid();

        Map<String, Object> groupData = new HashMap<>();
        groupData.put("ownerUserId", ownerUserId);
        groupData.put("invitedUserId", "");
        groupData.put("createdAt", FieldValue.serverTimestamp());
        groupData.put("ownerUserEmail", auth.getCurrentUser().getEmail());
        groupData.put("inviteUserEmail", "");
        groupData.put("coinStatus", 0); // 초기 코인 현황
        groupData.put("purchasedObjects", new ArrayList<>()); // 구매한 오브제 목록
        groupData.put("purchasedThemes", new ArrayList<>()); // 구매한 테마 목록
        groupData.put("diaries", new ArrayList<>()); // 작성한 일기
        groupData.put("calendarSchedules", new ArrayList<>()); // 캘린더 일정

        db.collection("groups").document(groupId).set(groupData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("InviteActivity", "그룹 생성 성공: " + groupId);

                    // 그룹 생성 후 기존 개인 데이터를 그룹 데이터로 복사
                    copyUserDataToGroup(groupId, ownerUserId);

                    sendInviteLink(groupId); // 초대 링크 생성 및 전송
                })
                .addOnFailureListener(e -> {
                    Log.e("InviteActivity", "그룹 생성 실패", e);
                    Toast.makeText(this, "그룹 생성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    private void copyUserDataToGroup(String groupId, String ownerUserId) {
        Log.d("InviteActivity", "copyUserDataToGroup 시작 - Group ID: " + groupId + ", Owner User ID: " + ownerUserId);

        // 개인 데이터 가져와서 그룹 데이터로 복사
        db.collection("users").document(ownerUserId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Log.d("InviteActivity", "개인 데이터 문서 존재함: " + document.getData());

                        Map<String, Object> userData = document.getData();

                        // 그룹 데이터에 개인 데이터 복사
                        db.collection("groups").document(groupId).update(userData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("InviteActivity", "사용자 데이터 그룹에 복사 성공");

                                    // 개인 데이터 삭제
                                    deleteUserData(ownerUserId);
                                })
                                .addOnFailureListener(e -> Log.e("InviteActivity", "사용자 데이터 그룹 복사 실패", e));
                    } else {
                        Log.w("InviteActivity", "개인 데이터 문서가 존재하지 않음");
                    }
                })
                .addOnFailureListener(e -> Log.e("InviteActivity", "개인 데이터 불러오기 실패", e));
    }

    private void deleteUserData(String ownerUserId) {
        db.collection("users").document(ownerUserId).delete()
                .addOnSuccessListener(aVoid -> Log.d("InviteActivity", "개인 데이터 삭제 성공"))
                .addOnFailureListener(e -> Log.e("InviteActivity", "개인 데이터 삭제 실패", e));
    }


    private void sendInviteLink(String groupId) {
        String link = "https://example.com/invite?groupId=" + groupId;
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://haruniki.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildShortDynamicLink()
                .addOnSuccessListener(shortDynamicLink -> {
                    String invitationLink = shortDynamicLink.getShortLink().toString();

                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    Log.d("InviteActivity", "초대 링크 생성 성공: " + invitationLink);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "초대 링크: " + invitationLink);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, "초대 보내기"));
                })
                .addOnFailureListener(e -> {
                    Log.e("InviteActivity", "다이나믹 링크 생성 실패", e);
                    Toast.makeText(this, "초대 링크 생성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }
}
