package com.example.test2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import android.content.Context;
import android.util.Log;

public class ArrangeManager {
    private FirebaseFirestore db;
    private String userID;

    public ArrangeManager(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.userID = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
    }

    public void updateArrangementStatus(String itemId, boolean isArranged) {
        if (userID == null) {
            Log.e("ArrangeManager", "User not logged in.");
            return;
        }

        // 먼저 groups에서 userID 확인
        db.collection("groups").document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // groups 컬렉션에서 arrangeObjects 업데이트
                        updateArrangementInCollection("groups", itemId, isArranged);
                    } else {
                        // users 컬렉션에서 arrangeObjects 업데이트
                        updateArrangementInCollection("users", itemId, isArranged);
                    }
                })
                .addOnFailureListener(e -> Log.e("ArrangeManager", "Failed to check user in groups", e));
    }

    private void updateArrangementInCollection(String collection, String itemId, boolean isArranged) {
        DocumentReference docRef = db.collection(collection).document(userID);
        docRef.update("arrangeObjects", isArranged ? FieldValue.arrayUnion(itemId) : FieldValue.arrayRemove(itemId))
                .addOnSuccessListener(aVoid -> Log.d("ArrangeManager", "Arrangement status updated for item: " + itemId))
                .addOnFailureListener(e -> Log.e("ArrangeManager", "Failed to update arrangement status", e));
    }
}

