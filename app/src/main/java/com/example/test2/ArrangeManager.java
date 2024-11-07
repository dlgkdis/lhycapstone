package com.example.test2;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
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

    public void updateArrangementStatus(String itemId, boolean isArranged, OnFirestoreUpdateListener listener) {
        if (userId == null) {
            Log.e(TAG, "User not logged in.");
            listener.onUpdate(false);
            return;
        }

        checkGroupMembership((reference) -> {
            if (isArranged) {
                // Delete itemId from arrangeObjects if isArranged is true
                reference.update("arrangeObjects", FieldValue.arrayRemove(itemId))
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Successfully removed " + itemId + " from arrangement in Firestore");
                            listener.onUpdate(true);
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to remove item " + itemId + " from arrangement in Firestore", e);
                            listener.onUpdate(false);
                        });
            } else {
                // Add itemId to arrangeObjects if isArranged is false
                reference.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("arrangeObjects")) {
                        // If arrangeObjects exists, add itemId directly
                        reference.update("arrangeObjects", FieldValue.arrayUnion(itemId))
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Successfully arranged " + itemId + " in Firestore");
                                    listener.onUpdate(true);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to arrange item " + itemId + " in Firestore", e);
                                    listener.onUpdate(false);
                                });
                    } else {
                        // If arrangeObjects doesn't exist, create and add itemId
                        Map<String, Object> initialData = new HashMap<>();
                        initialData.put("arrangeObjects", new ArrayList<String>() {{ add(itemId); }});
                        reference.set(initialData, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Successfully initialized and arranged " + itemId + " in Firestore");
                                    listener.onUpdate(true);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to initialize and arrange item " + itemId + " in Firestore", e);
                                    listener.onUpdate(false);
                                });
                    }
                });
            }
        });
    }

    private void checkGroupMembership(GroupCheckCallback callback) {
        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnSuccessListener(groupQuerySnapshot -> {
                    if (!groupQuerySnapshot.isEmpty()) {
                        DocumentReference groupRef = groupQuerySnapshot.getDocuments().get(0).getReference();
                        callback.onGroupCheckCompleted(groupRef);
                    } else {
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnSuccessListener(inviteQuerySnapshot -> {
                                    if (!inviteQuerySnapshot.isEmpty()) {
                                        DocumentReference invitedGroupRef = inviteQuerySnapshot.getDocuments().get(0).getReference();
                                        callback.onGroupCheckCompleted(invitedGroupRef);
                                    } else {
                                        DocumentReference userRef = db.collection("users").document(userId);
                                        callback.onGroupCheckCompleted(userRef);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e(TAG, "Error checking invited groups", e));
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking group ownership", e));
    }

    public interface GroupCheckCallback {
        void onGroupCheckCompleted(DocumentReference reference);
    }

    public interface OnFirestoreUpdateListener {
        void onUpdate(boolean success);
    }

    public interface OnLoadArrangementStatusListener {
        void onArrangementStatusLoaded(List<String> arrangedItems);
    }

    public void loadArrangementStatus(OnLoadArrangementStatusListener listener) {
        if (userId == null) {
            Log.e(TAG, "User not logged in.");
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
