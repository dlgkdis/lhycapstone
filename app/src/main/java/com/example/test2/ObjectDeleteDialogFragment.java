// ObjectDeleteDialogFragment.java
package com.example.test2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.auth.FirebaseAuth;


public class ObjectDeleteDialogFragment extends DialogFragment {

    private OnObjectDeleteListener deleteListener;
    private String itemId;
    private int imageResource;

    public interface OnObjectDeleteListener {
        void onObjectDeleted();
    }

    public static ObjectDeleteDialogFragment newInstance(String itemId, int imageResource, OnObjectDeleteListener listener) {
        ObjectDeleteDialogFragment fragment = new ObjectDeleteDialogFragment();
        Bundle args = new Bundle();
        args.putString("itemId", itemId);
        args.putInt("imageResource", imageResource);
        fragment.setArguments(args);
        fragment.deleteListener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.object_delete, container, false);

        if (getArguments() != null) {
            itemId = getArguments().getString("itemId");
            imageResource = getArguments().getInt("imageResource", -1);
        }

        // 오브제 이미지를 설정
        ImageView imageView = view.findViewById(R.id.imageView33);
        if (imageResource != -1) {
            imageView.setImageResource(imageResource);
        }

        // "배치 삭제" 버튼 설정
        Button deleteButton = view.findViewById(R.id.button2);
        deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onObjectDeleted();
            }
            deleteObjectFromFirestore(); // Firestore에서 데이터 삭제
            dismiss();
        });

        // "뒤로가기" 버튼 설정
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> dismiss());

        return view;
    }

    private void deleteObjectFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId != null) {
            db.collection("groups")
                    .whereEqualTo("ownerUserId", userId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            querySnapshot.getDocuments().get(0).getReference()
                                    .update("arrangeObjects", FieldValue.arrayRemove(itemId))
                                    .addOnSuccessListener(aVoid -> Log.d("ObjectDelete", "Object removed from Firestore"))
                                    .addOnFailureListener(e -> Log.e("ObjectDelete", "Failed to remove object", e));
                        } else {
                            db.collection("groups")
                                    .whereEqualTo("invitedUserId", userId)
                                    .get()
                                    .addOnSuccessListener(inviteSnapshot -> {
                                        if (!inviteSnapshot.isEmpty()) {
                                            inviteSnapshot.getDocuments().get(0).getReference()
                                                    .update("arrangeObjects", FieldValue.arrayRemove(itemId))
                                                    .addOnSuccessListener(aVoid -> Log.d("ObjectDelete", "Object removed from Firestore"))
                                                    .addOnFailureListener(e -> Log.e("ObjectDelete", "Failed to remove object", e));
                                        } else {
                                            db.collection("users").document(userId)
                                                    .update("arrangeObjects", FieldValue.arrayRemove(itemId))
                                                    .addOnSuccessListener(aVoid -> Log.d("ObjectDelete", "Object removed from Firestore"))
                                                    .addOnFailureListener(e -> Log.e("ObjectDelete", "Failed to remove object", e));
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e("ObjectDelete", "Error checking invited groups", e));
                        }
                    })
                    .addOnFailureListener(e -> Log.e("ObjectDelete", "Error checking group ownership", e));
        }
    }
}
