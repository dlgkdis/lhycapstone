package com.example.test2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.app.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;

// Firebase Firestore 관련 import 추가
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

// 데이터 초기화를 위한 HashMap 및 ArrayList 사용
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

// 로그 사용을 위한 import
import android.util.Log;


// 데이터 초기화를 위한 HashMap 사용
import java.util.HashMap;
import java.util.Map;



public class LoginActivity extends AppCompatActivity {

    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> activityResultLauncher;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login2);

        init();

        signInButton = findViewById(R.id.GoogleLoginButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

         ImageButton backButton = findViewById(R.id.backButton);
         if (backButton != null) {
             backButton.setOnClickListener(v -> finish());
         }
    }
    private void init(){
         activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                 new ActivityResultCallback<ActivityResult>() {
                     @Override
                     public void onActivityResult(ActivityResult result) {
                         if(result.getResultCode()== Activity.RESULT_OK){
                             Intent intent = result.getData();
                             Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                             try{
                                 //successful
                                 GoogleSignInAccount account = task.getResult(ApiException.class);
                                 firebaseAuthWithGoogle(account);
                             }  catch (ApiException e){
                                 //failed
                                 Toast.makeText(getApplicationContext(), "google sign in failed", Toast.LENGTH_LONG).show();
                             }
                         }
                     }
                 });
         configSignIn();
         initAuth();
    }

    private void configSignIn(){
         GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                 .requestIdToken(getString(R.string.default_web_client_id))
                 .requestEmail()
                 .build();
         mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
    }

    private void initAuth(){
         mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart(){
         super.onStart();
         if(isUserNonNull()){
             updateUI();
         }
    }

    private boolean isUserNonNull(){
         if(mAuth.getCurrentUser()==null){
             return false;
         }else{
             return true;
         }
    }

    private void updateUI(){

    }

    private void signIn(){
         Intent signInIntent = mGoogleSignInClient.getSignInIntent();
         activityResultLauncher.launch(signInIntent);
    }

    //[START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();

                            // 기존 데이터 확인
                            checkExistingUserData(userId);
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // 초기 데이터 생성 메서드
    private void createUserData(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> userData = new HashMap<>();
        userData.put("coinStatus", 0);
        userData.put("purchasedObjects", new ArrayList<>());
        userData.put("purchasedThemes", new ArrayList<>());

        db.collection("users").document(userId).set(userData)
                .addOnSuccessListener(aVoid -> Log.d("LoginActivity", "초기 데이터 설정 완료"))
                .addOnFailureListener(e -> Log.e("LoginActivity", "초기 데이터 설정 실패", e));
    }

    // 기존 데이터 확인 메서드
    private void checkExistingUserData(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // users 컬렉션에서 사용자 확인
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 기존 데이터가 있으므로 로그인 성공으로 처리
                        Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_LONG).show();
                        navigateToMain();
                    } else {
                        // groups 컬렉션에서 사용자 확인
                        db.collection("groups")
                                .whereEqualTo("ownerUserId", userId)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        // groups 컬렉션에 데이터가 있는 경우 로그인 성공으로 처리
                                        Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_LONG).show();
                                        navigateToMain();
                                    } else {
                                        // 기존 데이터가 없으므로 새 사용자 데이터 생성
                                        createUserData(userId);
                                        Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_LONG).show();
                                        navigateToMain();
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("LoginActivity", "그룹 데이터 확인 실패", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("LoginActivity", "사용자 데이터 확인 실패", e));
    }

    // 초기 데이터 생성 후 MainActivity로 이동
    private void navigateToMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void signOut(){
         //Firebase SignOut
         mAuth.signOut();

         //Google SignOut
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void revokeAccess(){
         mAuth.signOut();

         mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                 new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_LONG).show();
                     }
                 });
    }
}