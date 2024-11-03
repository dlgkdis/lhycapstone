// FirebaseHelper.java
package com.example.test2;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.FirebaseFirestoreException;
import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;

    private ListenerRegistration coinListener;
    private ListenerRegistration purchasedObjectsListener;
    private ListenerRegistration purchasedThemesListener;
    private ListenerRegistration diariesListener;
    private ListenerRegistration calendarSchedulesListener;

    public FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
    }

    public void getCoinData(OnDataListener<Long> listener) {
        listenToData("coinStatus", listener, registration -> coinListener = registration);
    }

    public void getPurchasedObjects(OnDataListener<List<String>> listener) {
        listenToData("purchasedObjects", listener, registration -> purchasedObjectsListener = registration);
    }

    public void getPurchasedThemes(OnDataListener<List<String>> listener) {
        listenToData("purchasedThemes", listener, registration -> purchasedThemesListener = registration);
    }

    public void getDiaries(OnDataListener<List<Map<String, Object>>> listener) {
        listenToData("diaries", listener, registration -> diariesListener = registration);
    }

    public void getCalendarSchedules(OnDataListener<List<Map<String, Object>>> listener) {
        listenToData("calendarSchedules", listener, registration -> calendarSchedulesListener = registration);
    }

    private <T> void listenToData(String fieldName, OnDataListener<T> listener, OnListenerRegistered onRegistered) {
        if (userId == null) {
            Log.e(TAG, "User is not logged in");
            return;
        }

        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // User is an owner in a group
                        DocumentReference reference = querySnapshot.getDocuments().get(0).getReference();
                        registerSnapshotListener(reference, fieldName, listener, onRegistered);
                    } else {
                        // Check if user is an invited member in a group
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnSuccessListener(inviteSnapshot -> {
                                    if (!inviteSnapshot.isEmpty()) {
                                        // User is invited in a group
                                        DocumentReference reference = inviteSnapshot.getDocuments().get(0).getReference();
                                        registerSnapshotListener(reference, fieldName, listener, onRegistered);
                                    } else {
                                        // User is not in a group; get from "users" collection
                                        DocumentReference userDoc = db.collection("users").document(userId);
                                        registerSnapshotListener(userDoc, fieldName, listener, onRegistered);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch group info", e));
    }


    private <T> void registerSnapshotListener(DocumentReference reference, String fieldName, OnDataListener<T> listener, OnListenerRegistered onRegistered) {
        ListenerRegistration registration = reference.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "Listen failed: ", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                T fieldData = (T) snapshot.get(fieldName);
                listener.onDataFetched(fieldData);

                // 특정 조건에 따라 알림 생성 (예: 일정 추가 감지 시)
                if ("calendarSchedules".equals(fieldName) && fieldData != null) {
                    // 알림 메시지를 담은 Map 생성
                    Map<String, Object> notificationData = new HashMap<>();
                    notificationData.put("message", "일정이 추가되었습니다");
                    notificationData.put("timestamp", System.currentTimeMillis());

                    // addNotification 호출 시 Map과 OnCompleteListener 전달
                    addNotification(notificationData, success -> {
                        if (success) {
                            Log.d(TAG, "Notification added successfully");
                        } else {
                            Log.e(TAG, "Failed to add notification");
                        }
                    });
                }
            } else {
                listener.onDataFetched(null);
            }
        });
        onRegistered.onRegistered(registration);
    }



    public void removeListeners() {
        if (coinListener != null) coinListener.remove();
        if (purchasedObjectsListener != null) purchasedObjectsListener.remove();
        if (purchasedThemesListener != null) purchasedThemesListener.remove();
        if (diariesListener != null) diariesListener.remove();
        if (calendarSchedulesListener != null) calendarSchedulesListener.remove();
    }

    public interface OnDataListener<T> {
        void onDataFetched(T data);
    }

    private interface OnListenerRegistered {
        void onRegistered(ListenerRegistration registration);
    }

    public interface OnCompleteListener {
        void onComplete(boolean success);
    }

    public void addNotification(Map<String, Object> notificationData, OnCompleteListener listener) {
        if (userId == null) {
            Log.e(TAG, "User is not logged in");
            listener.onComplete(false);
            return;
        }

        // 1. 먼저 groups 컬렉션에서 소유자 또는 초대된 사용자로서의 그룹이 있는지 확인
        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // 소유자 그룹이 있으면 해당 그룹에 알림 추가
                        DocumentReference groupRef = querySnapshot.getDocuments().get(0).getReference();
                        groupRef.collection("notifications")
                                .add(notificationData)
                                .addOnSuccessListener(documentReference -> listener.onComplete(true))
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to add notification to groups collection", e);
                                    listener.onComplete(false);
                                });
                    } else {
                        // 소유자 그룹이 없으면 초대된 사용자로서의 그룹 확인
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnSuccessListener(inviteSnapshot -> {
                                    if (!inviteSnapshot.isEmpty()) {
                                        // 초대된 그룹이 있으면 해당 그룹에 알림 추가
                                        DocumentReference groupRef = inviteSnapshot.getDocuments().get(0).getReference();
                                        groupRef.collection("notifications")
                                                .add(notificationData)
                                                .addOnSuccessListener(documentReference -> listener.onComplete(true))
                                                .addOnFailureListener(e -> {
                                                    Log.e(TAG, "Failed to add notification to groups collection", e);
                                                    listener.onComplete(false);
                                                });
                                    } else {
                                        // 그룹이 없으면 users 컬렉션에 알림 추가
                                        db.collection("users")
                                                .document(userId)
                                                .collection("notifications")
                                                .add(notificationData)
                                                .addOnSuccessListener(documentReference -> listener.onComplete(true))
                                                .addOnFailureListener(e -> {
                                                    Log.e(TAG, "Failed to add notification to users collection", e);
                                                    listener.onComplete(false);
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to fetch invited group info", e);
                                    listener.onComplete(false);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch owner group info", e);
                    listener.onComplete(false);
                });
    }




    // FirebaseHelper.java
    public void getNotifications(OnDataListener<List<Map<String, Object>>> listener) {
        if (userId == null) {
            Log.e(TAG, "User is not logged in");
            listener.onDataFetched(new ArrayList<>()); // 빈 리스트 반환
            return;
        }

        // 먼저 groups 컬렉션에서 소유자 또는 초대된 사용자로서의 그룹이 있는지 확인
        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // 소유자 그룹이 있으면 해당 그룹에서 알림 데이터 가져오기
                        DocumentReference groupRef = querySnapshot.getDocuments().get(0).getReference();
                        fetchNotificationsFromReference(groupRef, listener);
                    } else {
                        // 소유자 그룹이 없으면 초대된 사용자로서의 그룹 확인
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnSuccessListener(inviteSnapshot -> {
                                    if (!inviteSnapshot.isEmpty()) {
                                        // 초대된 그룹이 있으면 해당 그룹에서 알림 데이터 가져오기
                                        DocumentReference groupRef = inviteSnapshot.getDocuments().get(0).getReference();
                                        fetchNotificationsFromReference(groupRef, listener);
                                    } else {
                                        // 그룹이 없으면 users 컬렉션에서 알림 데이터 가져오기
                                        DocumentReference userDoc = db.collection("users").document(userId);
                                        fetchNotificationsFromReference(userDoc, listener);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to fetch invited group info", e);
                                    listener.onDataFetched(new ArrayList<>());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch owner group info", e);
                    listener.onDataFetched(new ArrayList<>());
                });
    }
    // 알림 데이터를 가져오는 보조 메서드
    private void fetchNotificationsFromReference(DocumentReference reference, OnDataListener<List<Map<String, Object>>> listener) {
        reference.collection("notifications")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Map<String, Object>> notifications = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        notifications.add(document.getData());
                    }
                    listener.onDataFetched(notifications);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch notifications", e);
                    listener.onDataFetched(new ArrayList<>()); // 빈 리스트 반환
                });
    }

}
