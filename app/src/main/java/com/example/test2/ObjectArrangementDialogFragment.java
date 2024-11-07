package com.example.test2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ObjectArrangementDialogFragment extends DialogFragment {

    private int imageResource;
    private String itemId;
    private List<String> allowedThemes;
    private ArrangeManager arrangeManager;

    public static ObjectArrangementDialogFragment newInstance(int imageResource, String itemId, List<String> allowedThemes) {
        ObjectArrangementDialogFragment fragment = new ObjectArrangementDialogFragment();
        Bundle args = new Bundle();
        args.putInt("imageResource", imageResource);
        args.putString("itemId", itemId);
        args.putStringArrayList("allowedThemes", new ArrayList<>(allowedThemes));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.object_arrangement, container, false);
        arrangeManager = new ArrangeManager(requireContext());

        if (getArguments() != null) {
            imageResource = getArguments().getInt("imageResource", -1);
            itemId = getArguments().getString("itemId");
            allowedThemes = getArguments().getStringArrayList("allowedThemes");
        }

        ImageView imageView33 = view.findViewById(R.id.imageView33);
        if (imageResource != -1) {
            imageView33.setImageResource(imageResource);
        }

        TextView checkTemaText = view.findViewById(R.id.checktemaText);
        if (allowedThemes != null && !allowedThemes.isEmpty()) {
            String themesText = "적용 테마: " + getTranslatedThemes(allowedThemes);
            checkTemaText.setText(themesText);
        }

        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> dismiss());

        Button arrangeButton = view.findViewById(R.id.button2);
        arrangeButton.setOnClickListener(v -> checkThemeAndAddToData());

        return view;
    }

    private String getTranslatedThemes(List<String> themes) {
        Map<String, String> themeTranslations = new HashMap<>();
        themeTranslations.put("tema_home", "집");
        themeTranslations.put("tema_airport", "비행기");
        themeTranslations.put("tema_island", "섬");
        themeTranslations.put("tema_submarine", "잠수함");
        themeTranslations.put("tema_rocket", "우주선");

        List<String> translatedThemes = new ArrayList<>();
        for (String theme : themes) {
            String translated = themeTranslations.getOrDefault(theme, theme);
            translatedThemes.add(translated);
        }
        return String.join(", ", translatedThemes);
    }

    private void checkThemeAndAddToData() {
        checkGroupMembership((isInGroup, reference) -> {
            reference.get().addOnSuccessListener(documentSnapshot -> {
                String currentTheme = documentSnapshot.contains("tema_background") ?
                        documentSnapshot.getString("tema_background") : null;

                if (allowedThemes.contains(currentTheme)) {
                    arrangeManager.updateArrangementStatus(itemId, false, success -> {
                        if (success) {
                            Toast.makeText(getContext(), "배치되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "배치 실패.", Toast.LENGTH_SHORT).show();
                        }
                        dismiss();  // 다이얼로그 닫기
                    });
                } else {
                    Toast.makeText(getContext(), "이 오브제는 현재 테마에서 배치할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Log.e("ObjectArrangement", "Failed to fetch theme background", e));
        });
    }

    private void checkGroupMembership(GroupCheckCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnSuccessListener(groupQuerySnapshot -> {
                    if (!groupQuerySnapshot.isEmpty()) {
                        DocumentReference groupRef = groupQuerySnapshot.getDocuments().get(0).getReference();
                        callback.onGroupCheckCompleted(true, groupRef);
                    } else {
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnSuccessListener(inviteQuerySnapshot -> {
                                    if (!inviteQuerySnapshot.isEmpty()) {
                                        DocumentReference invitedGroupRef = inviteQuerySnapshot.getDocuments().get(0).getReference();
                                        callback.onGroupCheckCompleted(true, invitedGroupRef);
                                    } else {
                                        DocumentReference userRef = db.collection("users").document(userId);
                                        callback.onGroupCheckCompleted(false, userRef);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("ObjectArrangement", "Error checking invited groups", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("ObjectArrangement", "Error checking group ownership", e));
    }

    interface GroupCheckCallback {
        void onGroupCheckCompleted(boolean isInGroup, DocumentReference reference);
    }
}
