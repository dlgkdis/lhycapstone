package com.example.test2;

import android.content.Context;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrangeManager {
    private final FirebaseFirestore db;
    private final String userId;
    private static final String TAG = "ArrangeManager";

    public ArrangeManager(Context context) {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
    }

    public void updateArrangementStatus(String itemId, boolean isArranged) {
        if (userId == null) {
            Log.e("ArrangeManager", "User not logged in.");
            return;
        }

        Log.d("ArrangeManager", "Attempting to update arrangement status for itemId: " + itemId + " with status: " + isArranged);

        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentReference groupRef = querySnapshot.getDocuments().get(0).getReference();
                        updateArrangeObjectsField(groupRef, itemId, isArranged);
                    } else {
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnSuccessListener(inviteSnapshot -> {
                                    if (!inviteSnapshot.isEmpty()) {
                                        DocumentReference groupRef = inviteSnapshot.getDocuments().get(0).getReference();
                                        updateArrangeObjectsField(groupRef, itemId, isArranged);
                                    } else {
                                        DocumentReference userRef = db.collection("users").document(userId);
                                        updateArrangeObjectsField(userRef, itemId, isArranged);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("ArrangeManager", "Failed to fetch invited group info", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("ArrangeManager", "Failed to fetch owner group info", e));
    }


    private void updateArrangeObjectsField(DocumentReference ref, String itemId, boolean isArranged) {
        Log.d("ArrangeManager", "Updating arrangeObjects for itemId: " + itemId + " with isArranged: " + isArranged);

        if (isArranged) {
            ref.update("arrangeObjects", FieldValue.arrayUnion(itemId))
                    .addOnSuccessListener(aVoid -> Log.d("ArrangeManager", "Successfully arranged " + itemId + " in Firestore"))
                    .addOnFailureListener(e -> Log.e("ArrangeManager", "Failed to arrange item " + itemId + " in Firestore", e));
        } else {
            ref.update("arrangeObjects", FieldValue.arrayRemove(itemId))
                    .addOnSuccessListener(aVoid -> Log.d("ArrangeManager", "Successfully removed " + itemId + " from arrangement in Firestore"))
                    .addOnFailureListener(e -> Log.e("ArrangeManager", "Failed to remove item " + itemId + " from arrangement in Firestore", e));
        }
    }

    // Firestore의 arrangeObjects 필드를 업데이트하는 메서드
    private void modifyArrangeObjects(DocumentReference ref, String itemId, boolean isArranged) {
        if (isArranged) {
            ref.update("arrangeObjects", FieldValue.arrayUnion(itemId))
                    .addOnSuccessListener(aVoid -> Log.d("ArrangeManager", "Arrangement status added for " + itemId))
                    .addOnFailureListener(e -> Log.e("ArrangeManager", "Failed to add arrangement status", e));
        } else {
            ref.update("arrangeObjects", FieldValue.arrayRemove(itemId))
                    .addOnSuccessListener(aVoid -> Log.d("ArrangeManager", "Arrangement status removed for " + itemId))
                    .addOnFailureListener(e -> Log.e("ArrangeManager", "Failed to remove arrangement status", e));
        }
    }


    public interface OnLoadArrangementStatusListener {
        void onArrangementStatusLoaded(List<String> arrangedItems);
    }

    public void loadArrangementStatus(OnLoadArrangementStatusListener listener) {
        if (userId == null) {
            Log.e("ArrangeManager", "User not logged in.");
            return;
        }

        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentReference groupRef = querySnapshot.getDocuments().get(0).getReference();
                        fetchArrangeObjects(groupRef, listener);
                    } else {
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnSuccessListener(inviteSnapshot -> {
                                    if (!inviteSnapshot.isEmpty()) {
                                        DocumentReference groupRef = inviteSnapshot.getDocuments().get(0).getReference();
                                        fetchArrangeObjects(groupRef, listener);
                                    } else {
                                        DocumentReference userRef = db.collection("users").document(userId);
                                        fetchArrangeObjects(userRef, listener);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch invited group info", e));
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch owner group info", e));
    }

    private void fetchArrangeObjects(DocumentReference ref, OnLoadArrangementStatusListener listener) {
        ref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> arrangedItems = (List<String>) documentSnapshot.get("arrangeObjects");
                if (arrangedItems == null) {
                    arrangedItems = new ArrayList<>();
                }
                listener.onArrangementStatusLoaded(arrangedItems);
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to fetch arrangeObjects", e));
    }
}
