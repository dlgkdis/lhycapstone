package com.example.test2;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class CoinManager {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "CoinManager";

    public void checkAndDeductCoins(String userId, int amount, OnCoinDeductListener listener) {
        getCoinStatus(userId, currentCoins -> {
            if (currentCoins >= amount) {
                deductCoins(userId, amount, listener); // 충분한 코인이 있을 때 차감
            } else {
                listener.onComplete(false, currentCoins); // 코인이 부족한 경우
            }
        });
    }

    public void getCoinStatus(String userId, OnCoinStatusListener listener) {
        // ownerUserId로 그룹 확인
        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // ownerUserId로 그룹이 있는 경우 해당 그룹에서 코인 상태 가져오기
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        Long coinStatus = document.getLong("coinStatus");
                        listener.onCoinStatusRetrieved(coinStatus != null ? coinStatus : 0L);
                    } else {
                        // 그룹 소유자가 아닌 경우 invitedUserId로 그룹 확인
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnCompleteListener(inviteTask -> {
                                    if (inviteTask.isSuccessful() && inviteTask.getResult() != null && !inviteTask.getResult().isEmpty()) {
                                        // 초대된 그룹이 있는 경우 해당 그룹에서 코인 상태 가져오기
                                        DocumentSnapshot document = inviteTask.getResult().getDocuments().get(0);
                                        Long coinStatus = document.getLong("coinStatus");
                                        listener.onCoinStatusRetrieved(coinStatus != null ? coinStatus : 0L);
                                    } else {
                                        // 그룹이 없으면 users 컬렉션에서 코인 상태 가져오기
                                        DocumentReference userDoc = db.collection("users").document(userId);
                                        userDoc.get()
                                                .addOnCompleteListener(userTask -> {
                                                    if (userTask.isSuccessful() && userTask.getResult() != null) {
                                                        Long coinStatus = userTask.getResult().getLong("coinStatus");
                                                        listener.onCoinStatusRetrieved(coinStatus != null ? coinStatus : 0L);
                                                    } else {
                                                        Log.e(TAG, "Failed to fetch coin status from users collection");
                                                        listener.onCoinStatusRetrieved(0L);
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to fetch invited group info", e);
                                    listener.onCoinStatusRetrieved(0L);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch owner group info", e);
                    listener.onCoinStatusRetrieved(0L);
                });
    }

    public void deductCoins(String userId, int amount, OnCoinDeductListener listener) {
        getCoinStatus(userId, currentCoins -> {
            if (currentCoins >= amount) {
                // ownerUserId로 그룹 확인 후 차감
                db.collection("groups")
                        .whereEqualTo("ownerUserId", userId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                                // ownerUserId 그룹에서 코인 차감
                                DocumentReference groupRef = task.getResult().getDocuments().get(0).getReference();
                                updateCoinStatus(groupRef, currentCoins, amount, listener);
                            } else {
                                // ownerUserId 그룹이 없으면 invitedUserId 그룹 확인
                                db.collection("groups")
                                        .whereEqualTo("invitedUserId", userId)
                                        .get()
                                        .addOnCompleteListener(inviteTask -> {
                                            if (inviteTask.isSuccessful() && inviteTask.getResult() != null && !inviteTask.getResult().isEmpty()) {
                                                // 초대된 그룹에서 코인 차감
                                                DocumentReference groupRef = inviteTask.getResult().getDocuments().get(0).getReference();
                                                updateCoinStatus(groupRef, currentCoins, amount, listener);
                                            } else {
                                                // 그룹이 없으면 users 컬렉션에서 코인 차감
                                                DocumentReference userDoc = db.collection("users").document(userId);
                                                updateCoinStatus(userDoc, currentCoins, amount, listener);
                                            }
                                        });
                            }
                        });
            } else {
                listener.onComplete(false, currentCoins);
            }
        });
    }

    // CoinStatus 업데이트 메서드
    private void updateCoinStatus(DocumentReference docRef, long currentCoins, int amount, OnCoinDeductListener listener) {
        docRef.update("coinStatus", currentCoins - amount)
                .addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        listener.onComplete(true, currentCoins - amount);
                    } else {
                        listener.onComplete(false, currentCoins);
                    }
                });
    }

    public interface OnCoinStatusListener {
        void onCoinStatusRetrieved(Long coinStatus);
    }

    public interface OnCoinDeductListener {
        void onComplete(boolean success, long remainingCoins);
    }
}
