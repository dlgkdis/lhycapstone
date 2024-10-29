// InviteActivity.java
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
import java.util.HashMap;
import java.util.Map;

public class InviteActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_complete); // 레이아웃 파일명 변경

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

        db.collection("groups").document(groupId).set(groupData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("InviteActivity", "그룹 생성 성공: " + groupId);
                    sendInviteLink(groupId); // 초대 링크 생성 및 전송
                })
                .addOnFailureListener(e -> {
                    Log.e("InviteActivity", "그룹 생성 실패", e);
                    Toast.makeText(this, "그룹 생성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
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
                    Log.d("InviteActivity", "초대 링크 생성 성공: "+ invitationLink);
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
