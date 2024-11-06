// ArrangeManager.java
package com.example.test2;

import android.content.Context;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ArrangeManager {
    private final FirebaseFirestore db;

    public ArrangeManager(Context context) {
        db = FirebaseFirestore.getInstance();
    }

    // 오브제 배치 상태 업데이트 메서드
    public void updateArrangementStatus(String itemId, boolean isArranged) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId == null) {
            Log.e("ArrangeManager", "User not logged in.");
            return;
        }

        // groups에서 ownerUserId로 확인
        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentReference groupRef = task.getResult().getDocuments().get(0).getReference();
                        updateArrangeObjectsField(groupRef, itemId, isArranged);
                    } else {
                        // 초대된 사용자로서의 그룹 확인
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnCompleteListener(inviteTask -> {
                                    if (inviteTask.isSuccessful() && inviteTask.getResult() != null && !inviteTask.getResult().isEmpty()) {
                                        DocumentReference groupRef = inviteTask.getResult().getDocuments().get(0).getReference();
                                        updateArrangeObjectsField(groupRef, itemId, isArranged);
                                    } else {
                                        // 그룹이 없으면 users 컬렉션에 업데이트
                                        DocumentReference userRef = db.collection("users").document(userId);
                                        updateArrangeObjectsField(userRef, itemId, isArranged);
                                    }
                                });
                    }
                });
    }

    // arrangeObjects 필드를 업데이트하는 메서드
    private void updateArrangeObjectsField(DocumentReference ref, String itemId, boolean isArranged) {
        Map<String, Object> updateData = new HashMap<>();
        if (isArranged) {
            updateData.put("arrangeObjects", FieldValue.arrayUnion(itemId));
        } else {
            updateData.put("arrangeObjects", FieldValue.arrayRemove(itemId));
        }

        ref.update(updateData)
                .addOnSuccessListener(aVoid -> Log.d("ArrangeManager", "Arrangement status updated successfully for " + itemId))
                .addOnFailureListener(e -> Log.e("ArrangeManager", "Failed to update arrangement status", e));
    }
}
