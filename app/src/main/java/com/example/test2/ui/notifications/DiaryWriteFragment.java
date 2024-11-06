package com.example.test2.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.test2.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.auth.FirebaseAuth;
import java.util.HashMap;
import java.util.Map;
import android.widget.Toast;
import android.util.Log;
import com.google.firebase.firestore.DocumentReference;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DiaryWriteFragment extends Fragment {

    private EditText diaryEditText;
    private FirebaseFirestore db;
    private String selectedDateKey;
    private TextView textView8;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
    private String weather;
    private String content;
    private String date;
    private DrawingView drawingView; // DrawingView 추가
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.drawing_diary, container, false);

        db = FirebaseFirestore.getInstance();
        drawingView = root.findViewById(R.id.drawingView); // DrawingView 초기화

        sharedPreferences = requireContext().getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE);

        if (getArguments() != null) {
            selectedDateKey = getArguments().getString("selectedDateKey", getTodayDateKey());
        } else {
            selectedDateKey = getTodayDateKey();
        }

        diaryEditText = root.findViewById(R.id.diaryEditText);
        textView8 = root.findViewById(R.id.dateTextView);
        updateDateTextView();

        Button registerButton = root.findViewById(R.id.saveButton);
        registerButton.setOnClickListener(v -> {
            saveDiaryContentToFirestore();
            DiaryEndDialogFragment dialog = new DiaryEndDialogFragment();
            dialog.show(getParentFragmentManager(), "DiaryEndDialogFragment");
            Navigation.findNavController(v).navigate(R.id.action_diaryWriteFragment_to_NotificationsFragment);
        });

        ImageButton backButton = root.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_diaryWriteFragment_to_NotificationsFragment));

        TextView drawingHintText = root.findViewById(R.id.drawingHintText);
        drawingView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                drawingHintText.setVisibility(View.GONE);
            }
            return drawingView.onTouchEvent(event);
        });

        ImageButton button1 = root.findViewById(R.id.sunnyButton);
        ImageButton button2 = root.findViewById(R.id.cloudButton);
        ImageButton button3 = root.findViewById(R.id.rainButton);

        View redDot1 = root.findViewById(R.id.redDot3);
        View redDot2 = root.findViewById(R.id.redDot);
        View redDot3 = root.findViewById(R.id.redDot2);

        button1.setOnClickListener(v -> {
            redDot1.setVisibility(View.VISIBLE);
            redDot2.setVisibility(View.INVISIBLE);
            redDot3.setVisibility(View.INVISIBLE);
            weather = "sunny";
        });

        button2.setOnClickListener(v -> {
            redDot1.setVisibility(View.INVISIBLE);
            redDot2.setVisibility(View.VISIBLE);
            redDot3.setVisibility(View.INVISIBLE);
            weather = "cloud";
        });

        button3.setOnClickListener(v -> {
            redDot1.setVisibility(View.INVISIBLE);
            redDot2.setVisibility(View.INVISIBLE);
            redDot3.setVisibility(View.VISIBLE);
            weather = "rain";
        });

        return root;
    }

    private void saveDiaryContentToFirestore() {
        content = diaryEditText.getText().toString();
        date = selectedDateKey;

        if (userId == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String drawingBase64 = convertDrawingToBase64(drawingView);

        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnSuccessListener(groupQuerySnapshot -> {
                    if (!groupQuerySnapshot.isEmpty()) {
                        DocumentReference groupRef = groupQuerySnapshot.getDocuments().get(0).getReference();
                        saveDiaryInCollection(groupRef.collection("diaries"), weather, drawingBase64, content, date);
                    } else {
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnSuccessListener(inviteQuerySnapshot -> {
                                    if (!inviteQuerySnapshot.isEmpty()) {
                                        DocumentReference invitedGroupRef = inviteQuerySnapshot.getDocuments().get(0).getReference();
                                        saveDiaryInCollection(invitedGroupRef.collection("diaries"), weather, drawingBase64, content, date);
                                    } else {
                                        DocumentReference userRef = db.collection("users").document(userId);
                                        saveDiaryInCollection(userRef.collection("diaries"), weather, drawingBase64, content, date);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("DiaryWriteFragment", "Error checking invited groups", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("DiaryWriteFragment", "Error checking group ownership", e));
    }

    private void saveDiaryInCollection(CollectionReference diariesRef, String weather, String drawingBase64, String content, String date) {
        Map<String, Object> diaryData = new HashMap<>();
        diaryData.put("weather", weather);
        diaryData.put("drawing", drawingBase64);
        diaryData.put("content", content);
        diaryData.put("date", date);

        diariesRef.add(diaryData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "일기를 저장했습니다.", Toast.LENGTH_SHORT).show();
                    updateDiaryCountAndCoin();

                    // 저장 성공 시 알림 추가
                    Map<String, Object> notificationData = new HashMap<>();
                    notificationData.put("message", "새로운 일기가 작성되었습니다: " + date);
                    notificationData.put("timestamp", System.currentTimeMillis());

                    // 알림 데이터를 Firestore에 저장
                    db.collection("groups")
                            .whereEqualTo("ownerUserId", userId)
                            .get()
                            .addOnSuccessListener(groupQuerySnapshot -> {
                                if (!groupQuerySnapshot.isEmpty()) {
                                    // 그룹이 있는 경우 해당 그룹에 알림 추가
                                    DocumentReference groupRef = groupQuerySnapshot.getDocuments().get(0).getReference();
                                    groupRef.collection("notifications").add(notificationData)
                                            .addOnSuccessListener(docRef -> Log.d("DiaryWriteFragment", "Notification added to group"))
                                            .addOnFailureListener(e -> Log.e("DiaryWriteFragment", "Failed to add notification to group", e));
                                } else {
                                    // 그룹 소유자가 아닌 경우 초대된 사용자로 확인
                                    db.collection("groups")
                                            .whereEqualTo("invitedUserId", userId)
                                            .get()
                                            .addOnSuccessListener(inviteQuerySnapshot -> {
                                                if (!inviteQuerySnapshot.isEmpty()) {
                                                    DocumentReference invitedGroupRef = inviteQuerySnapshot.getDocuments().get(0).getReference();
                                                    invitedGroupRef.collection("notifications").add(notificationData)
                                                            .addOnSuccessListener(docRef -> Log.d("DiaryWriteFragment", "Notification added to invited group"))
                                                            .addOnFailureListener(e -> Log.e("DiaryWriteFragment", "Failed to add notification to invited group", e));
                                                } else {
                                                    // 그룹이 없으므로 사용자의 notifications 컬렉션에 추가
                                                    db.collection("users").document(userId).collection("notifications").add(notificationData)
                                                            .addOnSuccessListener(docRef -> Log.d("DiaryWriteFragment", "Notification added to user"))
                                                            .addOnFailureListener(e -> Log.e("DiaryWriteFragment", "Failed to add notification to user", e));
                                                }
                                            })
                                            .addOnFailureListener(e -> Log.e("DiaryWriteFragment", "Error checking invited groups", e));
                                }
                            })
                            .addOnFailureListener(e -> Log.e("DiaryWriteFragment", "Error checking group ownership", e));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "일기 저장 실패", Toast.LENGTH_SHORT).show());
    }


    private String convertDrawingToBase64(DrawingView drawingView) {
        Bitmap bitmap = drawingView.getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

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

    // 다이어리 작성 시 `diaryCount`와 코인 증가를 처리하는 메서드
    private void updateDiaryCountAndCoin() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String lastUpdatedDate = sharedPreferences.getString("lastUpdatedDate", "");
        String todayDate = getTodayDate(); // 오늘 날짜를 가져오는 메서드

        // 마지막 업데이트 날짜가 오늘과 다르면 `diaryCount` 초기화
        if (!todayDate.equals(lastUpdatedDate)) {
            editor.putInt("diaryCount", 0); // 초기화
            editor.putString("lastUpdatedDate", todayDate); // 오늘 날짜로 갱신
        }

        int diaryCount = sharedPreferences.getInt("diaryCount", 0);

        // 하루 두 번까지만 `diaryCount` 증가 및 코인 추가
        if (diaryCount < 2) {
            editor.putInt("diaryCount", diaryCount + 1);
            editor.apply();
            incrementCoin(); // 코인 증가 메서드 호출
        } else {
            Toast.makeText(getContext(), "하루에 최대 2번만 코인을 받을 수 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 오늘 날짜를 `yyyyMMdd` 형식으로 가져오는 메서드
    private String getTodayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    // 코인을 증가시키는 메서드
    private void incrementCoin() {
        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnSuccessListener(groupQuerySnapshot -> {
                    if (!groupQuerySnapshot.isEmpty()) {
                        // 그룹이 있으면 해당 그룹의 `coinstatus` 증가
                        DocumentReference groupRef = groupQuerySnapshot.getDocuments().get(0).getReference();
                        groupRef.update("coinStatus", FieldValue.increment(1))
                                .addOnSuccessListener(aVoid -> Log.d("DiaryWriteFragment", "Coin updated in group"))
                                .addOnFailureListener(e -> Log.e("DiaryWriteFragment", "Failed to update coin in group", e));
                    } else {
                        // 그룹이 없으면 `users` 컬렉션의 `coinstatus` 증가
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnSuccessListener(inviteQuerySnapshot -> {
                                    if (!inviteQuerySnapshot.isEmpty()) {
                                        DocumentReference invitedGroupRef = inviteQuerySnapshot.getDocuments().get(0).getReference();
                                        invitedGroupRef.update("coinStatus", FieldValue.increment(1))
                                                .addOnSuccessListener(aVoid -> Log.d("DiaryWriteFragment", "Coin updated in invited group"))
                                                .addOnFailureListener(e -> Log.e("DiaryWriteFragment", "Failed to update coin in invited group", e));
                                    } else {
                                        // 그룹이 없으면 `users` 컬렉션의 `coinstatus` 증가
                                        DocumentReference userRef = db.collection("users").document(userId);
                                        userRef.update("coinStatus", FieldValue.increment(1))
                                                .addOnSuccessListener(aVoid -> Log.d("DiaryWriteFragment", "Coin updated in user"))
                                                .addOnFailureListener(e -> Log.e("DiaryWriteFragment", "Failed to update coin in user", e));
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("DiaryWriteFragment", "Error checking invited groups", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("DiaryWriteFragment", "Error checking group ownership", e));
    }
}
