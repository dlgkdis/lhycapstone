package com.example.test2.ui.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.test2.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.auth.FirebaseAuth;
import java.util.HashMap;
import java.util.Map;
import android.widget.Toast;
import android.util.Log;
import com.google.firebase.firestore.DocumentReference;


public class DiaryWriteFragment extends Fragment {

    private EditText diaryEditText;
    private FirebaseFirestore db; // Firestore 인스턴스 추가
    private String selectedDateKey;
    private TextView textView8; // 날짜 표시 TextView
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
    private String weather;      // 날씨 정보
    private String drawingUri;   // 그림의 URI 또는 파일 경로
    private String content;      // 일기 내용
    private String date;         // 일기 작성 날짜



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.drawing_diary, container, false);

        // SharedPreferences 초기화
        db = FirebaseFirestore.getInstance();

        // 인자로 전달된 selectedDateKey 받기
        if (getArguments() != null) {
            selectedDateKey = getArguments().getString("selectedDateKey", getTodayDateKey());
        } else {
            selectedDateKey = getTodayDateKey();
        }

        // EditText 초기화
        diaryEditText = root.findViewById(R.id.diaryEditText);

        // 날짜 표시 TextView 초기화
        textView8 = root.findViewById(R.id.textView8);
        updateDateTextView(); // 선택된 날짜를 TextView에 설정

        // 등록 버튼 클릭 리스너 설정
        Button registerButton = root.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            saveDiaryContentToFirestore(weather,);
            DiaryEndDialogFragment dialog = new DiaryEndDialogFragment();
            dialog.show(getParentFragmentManager(), "DiaryEndDialogFragment");
            Navigation.findNavController(v).navigate(R.id.action_diaryWriteFragment_to_NotificationsFragment);
        });

        // 뒤로가기 버튼 설정
        ImageButton backButton = root.findViewById(R.id.imageButton64);
        backButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_diaryWriteFragment_to_NotificationsFragment));

        // DrawingView와 TextView 참조 가져오기
        DrawingView drawingView = root.findViewById(R.id.drawingView);
        TextView drawingHintText = root.findViewById(R.id.drawingHintText);

        drawingView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                drawingHintText.setVisibility(View.GONE);
            }
            return drawingView.onTouchEvent(event);
        });

        // 버튼 및 빨간색 동그라미 설정
        ImageButton button1 = root.findViewById(R.id.imageButton3); //rain
        ImageButton button2 = root.findViewById(R.id.imageButton);  //cloud
        ImageButton button3 = root.findViewById(R.id.imageButton2);  //sunny

        View redDot1 = root.findViewById(R.id.redDot3);
        View redDot2 = root.findViewById(R.id.redDot);
        View redDot3 = root.findViewById(R.id.redDot2);

        button1.setOnClickListener(v -> {
            redDot1.setVisibility(View.VISIBLE);
            redDot2.setVisibility(View.INVISIBLE);
            redDot3.setVisibility(View.INVISIBLE);
            weather = "rain"; // weather 변수 설정
        });

        button2.setOnClickListener(v -> {
            redDot1.setVisibility(View.INVISIBLE);
            redDot2.setVisibility(View.VISIBLE);
            redDot3.setVisibility(View.INVISIBLE);
            weather = "cloud"; // weather 변수 설정
        });

        button3.setOnClickListener(v -> {
            redDot1.setVisibility(View.INVISIBLE);
            redDot2.setVisibility(View.INVISIBLE);
            redDot3.setVisibility(View.VISIBLE);
            weather = "sunny"; // weather 변수 설정
        });

        return root;
    }

    private void saveDiaryContentToFirestore(String weather, String drawingUri, String content, String date) {
        if (userId == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnSuccessListener(groupQuerySnapshot -> {
                    if (!groupQuerySnapshot.isEmpty()) {
                        // 사용자 ID가 그룹의 소유자인 경우 그룹에 저장
                        DocumentReference groupRef = groupQuerySnapshot.getDocuments().get(0).getReference();
                        saveDiaryInCollection(groupRef.collection("diaries"), weather, drawingUri, content, date);
                    } else {
                        // 그룹 소유자가 아닌 경우 초대된 사용자로 확인
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnSuccessListener(inviteQuerySnapshot -> {
                                    if (!inviteQuerySnapshot.isEmpty()) {
                                        DocumentReference invitedGroupRef = inviteQuerySnapshot.getDocuments().get(0).getReference();
                                        saveDiaryInCollection(invitedGroupRef.collection("diaries"), weather, drawingUri, content, date);
                                    } else {
                                        // 그룹에 없으므로 사용자의 diaries 컬렉션에 저장
                                        DocumentReference userRef = db.collection("users").document(userId);
                                        saveDiaryInCollection(userRef.collection("diaries"), weather, drawingUri, content, date);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("DiaryWriteFragment", "Error checking invited groups", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("DiaryWriteFragment", "Error checking group ownership", e));
    }

    private void saveDiaryInCollection(CollectionReference diariesRef, String weather, String drawingUri, String content, String date) {
        Map<String, Object> diaryData = new HashMap<>();
        diaryData.put("weather", weather);
        diaryData.put("drawing", drawingUri);
        diaryData.put("content", content);
        diaryData.put("date", date);

        diariesRef.add(diaryData)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(getContext(), "Diary saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to save diary", Toast.LENGTH_SHORT).show());
    }

    // 오늘 날짜를 기본으로 사용하는 메서드
    private String getTodayDateKey() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }

    private void updateDateTextView() {
        SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).parse(selectedDateKey));
        } catch (Exception e) {
            calendar = Calendar.getInstance();
        }
        textView8.setText("<" + displayFormat.format(calendar.getTime()) + ">");
    }
}