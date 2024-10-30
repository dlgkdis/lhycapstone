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
import androidx.activity.result.contract.ActivityResultContract;
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
             backButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent intent = new Intent(LoginActivity.this, Person.class);
                     startActivity(intent); // MainActivity 시작
                 }
             });
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
                                 Toast.makeText(getApplicationContext(),"로그인 성공", Toast.LENGTH_LONG).show();
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
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
         //[START_EXCLUDE silent]
         //[END_EXCLUDE]
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            Toast.makeText(getApplicationContext(),"Complete",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();
                            //updateUI(null);
                        }

                        //[START_EXCLUDE]
                        //hideprogressDialog();
                        //[END_EXCLUDE]
                    }
                });
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