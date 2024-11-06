package com.example.test2.ui.notifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.test2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



public class DiaryFixFragment extends Fragment {

    private EditText diaryEditText;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;
    private String selectedDateKey;
    private TextView dateTextView;
    private DrawingView drawingView;
    private String weather;
    private String documentId;  // 수정할 문서의 ID 저장
    private View redDot1, redDot2, redDot3;
    private String date;
    private String content;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fix_diary, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        selectedDateKey = getArguments().getString("selectedDateKey");

        redDot3 = root.findViewById(R.id.redDot3);
        redDot2 = root.findViewById(R.id.redDot);
        redDot1 = root.findViewById(R.id.redDot2);


        diaryEditText = root.findViewById(R.id.diaryEditText);
        dateTextView = root.findViewById(R.id.dateTextView);
        drawingView = root.findViewById(R.id.drawingView);

        Button saveButton = root.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> updateDiaryContent());

        ImageButton backButton = root.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        Button deleteButton = root.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> deleteDiaryEntry());

        // 날씨 버튼 설정 및 선택 시 weather 값 변경
        setupWeatherButtons(root);

        // Firestore에서 기존 일기 데이터 가져오기
        loadDiaryData();

        return root;
    }

    private void loadDiaryData() {
        if (userId == null) {
            Toast.makeText(getContext(), "로그인 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnSuccessListener(groupQuerySnapshot -> {
                    if (!groupQuerySnapshot.isEmpty()) {
                        // 사용자가 그룹의 소유자일 경우 그룹에서 데이터 가져오기
                        DocumentReference groupRef = groupQuerySnapshot.getDocuments().get(0).getReference();
                        fetchDiaryFromCollection(groupRef.collection("diaries"));
                    } else {
                        // 사용자가 그룹의 초대받은 사용자일 경우 확인
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnSuccessListener(inviteQuerySnapshot -> {
                                    if (!inviteQuerySnapshot.isEmpty()) {
                                        DocumentReference invitedGroupRef = inviteQuerySnapshot.getDocuments().get(0).getReference();
                                        fetchDiaryFromCollection(invitedGroupRef.collection("diaries"));
                                    } else {
                                        // 그룹에 속하지 않을 경우 users 컬렉션에서 가져오기
                                        DocumentReference userRef = db.collection("users").document(userId);
                                        fetchDiaryFromCollection(userRef.collection("diaries"));
                                    }
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "그룹 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "그룹 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show());
    }

    private void fetchDiaryFromCollection(CollectionReference diariesRef) {
        diariesRef.whereEqualTo("date", selectedDateKey)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                        documentId = document.getId();

                        String content = document.getString("content");
                        weather = document.getString("weather");
                        String date = document.getString("date");

                        diaryEditText.setText(content);
                        updateDateTextView();

                        // 날씨에 따라 선택 버튼 활성화
                        setWeatherButtonSelected(weather);

                        // 그림 데이터를 Bitmap으로 가져와 DrawingView에 표시
                        loadDrawing(document);
                    } else {
                        Toast.makeText(getContext(), "해당 날짜의 일기 데이터를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "일기 데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show());
    }



    private void loadDrawing(QueryDocumentSnapshot document) {
        // Firestore 문서에서 drawing 필드를 가져옵니다 (Base64 형식)
        String drawingBase64 = document.getString("drawing");

        if (drawingBase64 != null && !drawingBase64.isEmpty()) {
            // Base64 문자열을 Bitmap으로 변환
            byte[] decodedBytes = Base64.decode(drawingBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            // DrawingView에 Bitmap 설정
            drawingView.setBitmap(bitmap);
        } else {
            Toast.makeText(getContext(), "저장된 그림이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateDiaryContent() {
        content = diaryEditText.getText().toString();
        date = selectedDateKey;

        if (userId == null || documentId == null) {
            Toast.makeText(getContext(), "수정할 수 없습니다. 로그인 상태 또는 문서 ID를 확인해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // DrawingView의 그림 데이터를 Base64로 변환
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
                                .addOnFailureListener(e -> Log.e("DiaryFixFragment", "Error checking invited groups", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("DiaryFixFragment", "Error checking group ownership", e));
    }


    private void saveDiaryInCollection(CollectionReference diariesRef, String weather, String drawingBase64, String content, String date) {
        // 기존 데이터를 삭제하고 새 데이터를 저장
        diariesRef.document(documentId).delete()  // 기존 문서 삭제
                .addOnSuccessListener(aVoid -> {
                    // 기존 문서 삭제 후 새로운 데이터 저장
                    Map<String, Object> updatedData = new HashMap<>();
                    updatedData.put("weather", weather);
                    updatedData.put("drawing", drawingBase64);
                    updatedData.put("content", content);
                    updatedData.put("date", date);

                    diariesRef.add(updatedData)
                            .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(getContext(), "일기가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                                // 수정 성공 시 알림 추가
                                Map<String, Object> notificationData = new HashMap<>();
                                notificationData.put("message", "일기가 수정되었습니다: " + date);
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
                                                        .addOnSuccessListener(docRef -> Log.d("DiaryFixFragment", "Notification added to group"))
                                                        .addOnFailureListener(e -> Log.e("DiaryFixFragment", "Failed to add notification to group", e));
                                            } else {
                                                // 그룹 소유자가 아닌 경우 초대된 사용자로 확인
                                                db.collection("groups")
                                                        .whereEqualTo("invitedUserId", userId)
                                                        .get()
                                                        .addOnSuccessListener(inviteQuerySnapshot -> {
                                                            if (!inviteQuerySnapshot.isEmpty()) {
                                                                DocumentReference invitedGroupRef = inviteQuerySnapshot.getDocuments().get(0).getReference();
                                                                invitedGroupRef.collection("notifications").add(notificationData)
                                                                        .addOnSuccessListener(docRef -> Log.d("DiaryFixFragment", "Notification added to invited group"))
                                                                        .addOnFailureListener(e -> Log.e("DiaryFixFragment", "Failed to add notification to invited group", e));
                                                            } else {
                                                                // 그룹이 없으므로 사용자의 notifications 컬렉션에 추가
                                                                db.collection("users").document(userId).collection("notifications").add(notificationData)
                                                                        .addOnSuccessListener(docRef -> Log.d("DiaryFixFragment", "Notification added to user"))
                                                                        .addOnFailureListener(e -> Log.e("DiaryFixFragment", "Failed to add notification to user", e));
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> Log.e("DiaryFixFragment", "Error checking invited groups", e));
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.e("DiaryFixFragment", "Error checking group ownership", e));

                                // 저장 후 이전 화면으로 이동
                                Navigation.findNavController(requireView()).popBackStack();

                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "일기 수정 실패", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "기존 데이터 삭제 실패", Toast.LENGTH_SHORT).show();
                });
    }

    // DrawingView의 그림을 Base64로 변환하는 메서드
    private String convertDrawingToBase64(DrawingView drawingView) {
        Bitmap bitmap = drawingView.getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    private void setupWeatherButtons(View root) {
        ImageButton rainButton = root.findViewById(R.id.rainButton);
        ImageButton cloudButton = root.findViewById(R.id.cloudButton);
        ImageButton sunnyButton = root.findViewById(R.id.sunnyButton);

        rainButton.setOnClickListener(v -> {
            redDot1.setVisibility(View.VISIBLE);
            redDot2.setVisibility(View.INVISIBLE);
            redDot3.setVisibility(View.INVISIBLE);
            weather = "rain";
        });

        cloudButton.setOnClickListener(v -> {
            redDot1.setVisibility(View.INVISIBLE);
            redDot2.setVisibility(View.VISIBLE);
            redDot3.setVisibility(View.INVISIBLE);
            weather = "cloud";
        });

        sunnyButton.setOnClickListener(v -> {
            redDot1.setVisibility(View.INVISIBLE);
            redDot2.setVisibility(View.INVISIBLE);
            redDot3.setVisibility(View.VISIBLE);
            weather = "sunny";
        });
    }

    // 기존 날씨 값에 따라 빨간 점을 표시하는 메서드
    private void setWeatherButtonSelected(String weather) {
        if ("rain".equals(weather)) {
            redDot1.setVisibility(View.VISIBLE);
            redDot2.setVisibility(View.INVISIBLE);
            redDot3.setVisibility(View.INVISIBLE);
        } else if ("cloud".equals(weather)) {
            redDot1.setVisibility(View.INVISIBLE);
            redDot2.setVisibility(View.VISIBLE);
            redDot3.setVisibility(View.INVISIBLE);
        } else if ("sunny".equals(weather)) {
            redDot1.setVisibility(View.INVISIBLE);
            redDot2.setVisibility(View.INVISIBLE);
            redDot3.setVisibility(View.VISIBLE);
        }
    }


    private void updateDateTextView() {
        SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).parse(selectedDateKey));
        } catch (Exception e) {
            calendar = Calendar.getInstance();
        }
        dateTextView.setText("<" + displayFormat.format(calendar.getTime()) + ">");
    }

    private void deleteDiaryEntry() {
        if (userId == null || documentId == null) {
            Toast.makeText(getContext(), "삭제할 수 없습니다. 로그인 상태 또는 문서 ID를 확인해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firestore에서 문서 삭제
        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnSuccessListener(groupQuerySnapshot -> {
                    if (!groupQuerySnapshot.isEmpty()) {
                        DocumentReference groupRef = groupQuerySnapshot.getDocuments().get(0).getReference();
                        deleteFromCollection(groupRef.collection("diaries"));
                    } else {
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnSuccessListener(inviteQuerySnapshot -> {
                                    if (!inviteQuerySnapshot.isEmpty()) {
                                        DocumentReference invitedGroupRef = inviteQuerySnapshot.getDocuments().get(0).getReference();
                                        deleteFromCollection(invitedGroupRef.collection("diaries"));
                                    } else {
                                        DocumentReference userRef = db.collection("users").document(userId);
                                        deleteFromCollection(userRef.collection("diaries"));
                                    }
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "그룹 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "그룹 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show());
    }

    private void deleteFromCollection(CollectionReference diariesRef) {
        diariesRef.document(documentId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "일기가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    // 수정 성공 시 알림 추가
                    Map<String, Object> notificationData = new HashMap<>();
                    notificationData.put("message", "일기가 삭제되었습니다: " + selectedDateKey);
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
                                            .addOnSuccessListener(docRef -> Log.d("DiaryFixFragment", "Notification added to group"))
                                            .addOnFailureListener(e -> Log.e("DiaryFixFragment", "Failed to add notification to group", e));
                                } else {
                                    // 그룹 소유자가 아닌 경우 초대된 사용자로 확인
                                    db.collection("groups")
                                            .whereEqualTo("invitedUserId", userId)
                                            .get()
                                            .addOnSuccessListener(inviteQuerySnapshot -> {
                                                if (!inviteQuerySnapshot.isEmpty()) {
                                                    DocumentReference invitedGroupRef = inviteQuerySnapshot.getDocuments().get(0).getReference();
                                                    invitedGroupRef.collection("notifications").add(notificationData)
                                                            .addOnSuccessListener(docRef -> Log.d("DiaryFixFragment", "Notification added to invited group"))
                                                            .addOnFailureListener(e -> Log.e("DiaryFixFragment", "Failed to add notification to invited group", e));
                                                } else {
                                                    // 그룹이 없으므로 사용자의 notifications 컬렉션에 추가
                                                    db.collection("users").document(userId).collection("notifications").add(notificationData)
                                                            .addOnSuccessListener(docRef -> Log.d("DiaryFixFragment", "Notification added to user"))
                                                            .addOnFailureListener(e -> Log.e("DiaryFixFragment", "Failed to add notification to user", e));
                                                }
                                            })
                                            .addOnFailureListener(e -> Log.e("DiaryFixFragment", "Error checking invited groups", e));
                                }
                            })
                            .addOnFailureListener(e -> Log.e("DiaryFixFragment", "Error checking group ownership", e));

                    Navigation.findNavController(requireView()).popBackStack(); // 삭제 후 이전 화면으로 돌아가기
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "일기 삭제 실패", Toast.LENGTH_SHORT).show());
    }
}
