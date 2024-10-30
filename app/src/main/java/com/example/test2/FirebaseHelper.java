// FirebaseHelper.java
package com.example.test2;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
}
