package com.example.test2;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.tasks.Task;

public class CoinManager {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void checkAndDeductCoins(String userId, int amount, OnCoinDeductListener listener) {
        getCoinStatus(userId, currentCoins -> {
            if (currentCoins >= amount) {
                deductCoins(userId, amount, listener);  // 충분한 코인이 있을 때 차감
            } else {
                listener.onComplete(false, currentCoins);  // 코인이 부족한 경우
            }
        });
    }

    public void getCoinStatus(String userId, OnCoinStatusListener listener) {
        db.collection("groups")
                .whereEqualTo("invitedUserId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        Long coinStatus = document.getLong("coinStatus");

                        if (coinStatus != null) {
                            listener.onCoinStatusRetrieved(coinStatus);
                        } else {
                            listener.onCoinStatusRetrieved(0L); // 기본값 설정
                        }
                    } else {
                        Log.e("CoinManager", "Failed to get document for userId: " + userId);
                        listener.onCoinStatusRetrieved(0L); // 기본값 설정
                    }
                });
    }

    public void deductCoins(String userId, int amount, OnCoinDeductListener listener) {
        getCoinStatus(userId, currentCoins -> {
            if (currentCoins >= amount) {
                db.collection("groups")
                        .whereEqualTo("invitedUserId", userId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                DocumentReference docRef = document.getReference();

                                docRef.update("coinStatus", currentCoins - amount)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                listener.onComplete(true, currentCoins - amount);
                                            } else {
                                                listener.onComplete(false, currentCoins);
                                            }
                                        });
                            } else {
                                Log.e("CoinManager", "Failed to find group document for coin deduction.");
                                listener.onComplete(false, currentCoins);
                            }
                        });
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
