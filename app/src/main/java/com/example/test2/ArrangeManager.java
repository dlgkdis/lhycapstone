package com.example.test2;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ArrangeManager {

    private final FirebaseFirestore db;
    private final Context context;
    private final String userId;

    public ArrangeManager(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
    }

    // 오브제 배치 상태를 Firestore에 업데이트하는 메서드
    public void updateArrangementStatus(String itemId, boolean isArranged) {
        if (userId == null) {
            Log.e("ArrangeManager", "User is not logged in. Cannot update arrangement status.");
            return;
        }

        // 그룹이 있는지 확인
        db.collection("groups").whereEqualTo("userId", userId).get().addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                // 그룹이 있는 경우 해당 그룹 문서에 업데이트
                String groupId = querySnapshot.getDocuments().get(0).getId();
                updateArrangeObjectsInDocument("groups", groupId, itemId, isArranged);
            } else {
                // 그룹이 없으면 users 컬렉션에 업데이트
                updateArrangeObjectsInDocument("users", userId, itemId, isArranged);
            }
        }).addOnFailureListener(e -> Log.e("ArrangeManager", "Failed to check group existence", e));
    }

    private void updateArrangeObjectsInDocument(String collection, String documentId, String itemId, boolean isArranged) {
        Map<String, Object> updates = new HashMap<>();

        if (isArranged) {
            updates.put("arrangeObjects", FieldValue.arrayUnion(itemId));
        } else {
            updates.put("arrangeObjects", FieldValue.arrayRemove(itemId));
        }

        db.collection(collection).document(documentId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d("ArrangeManager", "Successfully updated arrangement status for item: " + itemId))
                .addOnFailureListener(e -> Log.e("ArrangeManager", "Failed to update arrangement status", e));
    }
}
