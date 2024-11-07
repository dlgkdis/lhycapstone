package com.example.test2.ui.tema;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.test2.FirebaseHelper;
import com.example.test2.R;
import com.example.test2.databinding.FragmentTemaBinding;
import com.example.test2.ui.ThemeViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

public class TemaFragment extends Fragment {

    private FragmentTemaBinding binding;
    private ThemeViewModel themeViewModel;
    private View selectedButton = null;
    private FirebaseHelper firebaseHelper;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
            FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
    private static final String TAG = "TemaFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTemaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (binding == null) {
            Log.e(TAG, "Binding is null");
            return;
        }

        themeViewModel = new ViewModelProvider(requireActivity()).get(ThemeViewModel.class);
        firebaseHelper = new FirebaseHelper();

        // 자물쇠 상태 설정 및 해제 조건 확인
        setLockState();
        loadSavedTheme();

        // 테마 버튼 클릭 리스너 설정
        binding.btnTemaHome.setOnClickListener(v -> setTheme("tema_home", v));
        binding.btnTemaAirport.setOnClickListener(v -> setTheme("tema_airport", v));
        binding.btnTemaSubmarine.setOnClickListener(v -> setTheme("tema_submarine", v));
        binding.btnTemaRocket.setOnClickListener(v -> setTheme("tema_rocket", v));
        binding.btnTemaIsland.setOnClickListener(v -> setTheme("tema_island", v));
    }

    private void setLockState() {
        if (binding == null) {
            Log.e(TAG, "Binding is null in setLockState");
            return;
        }

        firebaseHelper.getPurchasedObjects(purchasedObjects -> {
            int purchasedObjectCount = purchasedObjects.size();
            Log.d(TAG, "현재 구매된 오브제 개수: " + purchasedObjectCount);

            if (purchasedObjectCount >= 4) {
                binding.airportLock.setVisibility(View.GONE);
                binding.btnTemaAirport.setEnabled(true);
                Log.d(TAG, "airport 테마 잠금 해제");
            } else {
                binding.btnTemaAirport.setEnabled(false);
            }

            if (purchasedObjectCount >= 8) {
                binding.islandLock.setVisibility(View.GONE);
                binding.btnTemaIsland.setEnabled(true);
                Log.d(TAG, "island 테마 잠금 해제");
            } else {
                binding.btnTemaIsland.setEnabled(false);
            }

            if (purchasedObjectCount >= 12) {
                binding.rocketLock.setVisibility(View.GONE);
                binding.btnTemaRocket.setEnabled(true);
                Log.d(TAG, "rocket 테마 잠금 해제");
            } else {
                binding.btnTemaRocket.setEnabled(false);
            }

            if (purchasedObjectCount >= 16) {
                binding.submarineLock.setVisibility(View.GONE);
                binding.btnTemaSubmarine.setEnabled(true);
                Log.d(TAG, "submarine 테마 잠금 해제");
            } else {
                binding.btnTemaSubmarine.setEnabled(false);
            }
        });
    }

    private void loadSavedTheme() {
        checkGroupMembership((isInGroup, reference) -> {
            reference.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.contains("tema_background")) {
                    String savedTheme = documentSnapshot.getString("tema_background");
                    themeViewModel.initTheme(savedTheme);
                    updateButtonSelection(savedTheme);
                }
            });
        });
    }

    private void updateButtonSelection(String theme) {
        View button = null;
        switch (theme) {
            case "tema_home":
                button = binding.btnTemaHome;
                break;
            case "tema_airport":
                button = binding.btnTemaAirport;
                break;
            case "tema_submarine":
                button = binding.btnTemaSubmarine;
                break;
            case "tema_rocket":
                button = binding.btnTemaRocket;
                break;
            case "tema_island":
                button = binding.btnTemaIsland;
                break;
        }
        if (button != null) setTheme(theme, button);
    }

    private void setTheme(String theme, View button) {
        themeViewModel.setTheme(theme);

        checkGroupMembership((isInGroup, reference) -> {
            reference.update("tema_background", theme)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Theme background updated in Firestore"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to update theme background in Firestore", e));
        });

        if (selectedButton != null) {
            selectedButton.setBackgroundResource(R.drawable.rounded_button_default);
        }

        button.setBackgroundResource(R.drawable.rounded_button_selected);
        selectedButton = button;
    }

    private void checkGroupMembership(GroupCheckCallback callback) {
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
                                .addOnFailureListener(e -> Log.e(TAG, "Error checking invited groups", e));
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking group ownership", e));
    }

    interface GroupCheckCallback {
        void onGroupCheckCompleted(boolean isInGroup, DocumentReference reference);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
