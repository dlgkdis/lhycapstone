package com.example.test2;

import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Person extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView ownerInfoText, invitedInfoText;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserGroupStatus(currentUser.getUid(), currentUser.getEmail());
        } else {
            setContentView(R.layout.login);
            initLoginButton();
            initCommonButtons();
        }
    }

    private void checkUserGroupStatus(String userId, String userEmail) {
        // 현재 사용자가 ownerUserId인 그룹을 먼저 검색
        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        setContentView(R.layout.login_join);
                        initTextViews();  // TextView 초기화
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            groupId = document.getId();
                            String ownerUserEmail = document.getString("ownerUserEmail");
                            String inviteUserEmail = document.getString("inviteUserEmail");

                            ownerInfoText.setText("사용자: " + ownerUserEmail);
                            invitedInfoText.setText("구성원: " + inviteUserEmail);
                        }
                        initLogoutAndBanButtons();
                        initCommonButtons();
                    } else {
                        // invitedUserId로 검색
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnCompleteListener(inviteTask -> {
                                    if (inviteTask.isSuccessful() && !inviteTask.getResult().isEmpty()) {
                                        setContentView(R.layout.login_join);
                                        initTextViews();  // TextView 초기화
                                        for (QueryDocumentSnapshot document : inviteTask.getResult()) {
                                            String ownerUserEmail = document.getString("ownerUserEmail");
                                            String inviteUserEmail = document.getString("inviteUserEmail");

                                            ownerInfoText.setText("사용자: " + inviteUserEmail);
                                            invitedInfoText.setText("방장: " + ownerUserEmail);
                                        }
                                        initLogoutAndBanButtons();
                                        initCommonButtons();
                                    } else {
                                        // 그룹이 없을 경우
                                        setContentView(R.layout.login_complete);
                                        initTextViews();  // TextView 초기화
                                        ownerInfoText.setText("사용자: " + userEmail);
                                        initInviteAndLogoutButtons();
                                        initCommonButtons();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "그룹 상태 확인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    private void initTextViews() {
        ownerInfoText = findViewById(R.id.UserInfoText);
        invitedInfoText = findViewById(R.id.UserInfoText2);
    }

    private void initLogoutAndBanButtons() {
        ImageButton logoutButton = findViewById(R.id.LogoutButton1);
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(Person.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            recreate();
        });

        Button banButton = findViewById(R.id.banButton);
        banButton.setOnClickListener(v -> showLeaveDialog());
    }

    private void initInviteAndLogoutButtons() {
        ImageButton logoutButton = findViewById(R.id.LogoutButton1);
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(Person.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            recreate();
        });

        Button inviteButton = findViewById(R.id.inviteButton);
        inviteButton.setOnClickListener(v -> {
            Intent intent = new Intent(Person.this, InviteActivity.class);
            startActivity(intent);
        });
    }

    private void initLoginButton() {
        ImageButton loginButton = findViewById(R.id.LoginButton1);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(Person.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void initCommonButtons() {
        ImageButton backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        ImageButton passwordButton = findViewById(R.id.pwsettingButton);
        if (passwordButton != null) {
            passwordButton.setOnClickListener(v -> {
                Intent intent = new Intent(Person.this, PasswordSettingActivity.class);
                startActivity(intent);
            });
        }
    }
    private void showLeaveDialog() {
        Intent intent = new Intent(this, LeaveActivity.class);
        Log.d("Person", "Group ID: " + groupId);
        intent.putExtra("groupId", groupId); // 그룹 ID 전달
        startActivity(intent);
    }

}

