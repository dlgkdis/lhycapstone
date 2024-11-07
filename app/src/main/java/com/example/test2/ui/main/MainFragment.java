package com.example.test2.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.util.Log;

import com.example.test2.R;
import com.example.test2.Store;
import com.example.test2.Person;
import com.example.test2.Reward;
import com.example.test2.Bell;
import com.example.test2.databinding.FragmentMainBinding;
import com.example.test2.ui.ThemeViewModel;
import com.example.test2.FirebaseHelper;
import com.example.test2.ObjectArrangementDialogFragment;
import com.example.test2.ObjectDeleteDialogFragment;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainFragment extends Fragment implements ObjectArrangementDialogFragment.OnObjectArrangementCompleteListener {

    private FragmentMainBinding binding;
    private ThemeViewModel themeViewModel;
    private FirebaseHelper firebaseHelper;
    private FirebaseFirestore db;
    private boolean isShop1Arranged = false;
    private String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
            FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        themeViewModel = new ViewModelProvider(requireActivity()).get(ThemeViewModel.class);
        firebaseHelper = new FirebaseHelper();

        loadCoinData();
        loadPurchasedObjects();
        loadBackgroundTheme();

        if (binding != null) {
            setupButtonListeners();
        }
    }

    private void setupButtonListeners() {
        binding.btnProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), Person.class)));
        binding.btnReward.setOnClickListener(v -> startActivity(new Intent(getActivity(), Reward.class)));
        binding.btnBell.setOnClickListener(v -> startActivity(new Intent(getActivity(), Bell.class)));

        binding.coinButton.setOnClickListener(v -> handleCheckInReward());

        binding.btnStore.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Store.class);
            intent.putExtra("isShop1Arranged", isShop1Arranged);
            startActivity(intent);
        });

        binding.imgShop1.setVisibility(View.GONE);
        binding.imgShop1.setOnClickListener(v -> {
            if (isShop1Arranged) {
                openDeleteDialog();
            } else {
                openArrangementDialog(R.drawable.shop1);
            }
        });
    }

    private void handleCheckInReward() {
        checkGroupMembership((isInGroup, reference, field) -> {
            String todayDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Calendar.getInstance().getTime());
            reference.get().addOnSuccessListener(documentSnapshot -> {
                String lastCheckInDate = documentSnapshot.contains(field) ? documentSnapshot.getString(field) : "";
                if (!todayDate.equals(lastCheckInDate)) {
                    incrementCoin();
                    reference.update(field, todayDate)
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "출석 체크! 코인이 1개 증가했습니다.", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Log.e("MainFragment", "Failed to update check-in date", e));
                } else {
                    Toast.makeText(getContext(), "오늘은 이미 출석 체크를 완료했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void incrementCoin() {
        checkGroupMembership((isInGroup, reference, field) -> {
            reference.update("coinStatus", FieldValue.increment(1))
                    .addOnSuccessListener(aVoid -> Log.d("MainFragment", "Coin updated"))
                    .addOnFailureListener(e -> Log.e("MainFragment", "Failed to update coin", e));
        });
    }

    private void loadCoinData() {
        firebaseHelper.getCoinData(coinStatus -> {
            if (coinStatus != null && binding != null) {
                binding.coinTextView.setText(String.valueOf(coinStatus));
            }
        });
    }

    private void loadPurchasedObjects() {
        firebaseHelper.getPurchasedObjects(purchasedObjects -> {
            if (purchasedObjects != null && binding != null) {
                if (purchasedObjects.contains("shop1")) {
                    binding.imgShop1.setVisibility(View.VISIBLE);
                    isShop1Arranged = true;
                }
            }
        });
    }

    private void loadBackgroundTheme() {
        checkGroupMembership((isInGroup, reference, field) -> {
            reference.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.contains("tema_background")) {
                    String theme = documentSnapshot.getString("tema_background");
                    themeViewModel.initTheme(theme);
                    themeViewModel.getSelectedTheme().observe(getViewLifecycleOwner(), this::updateBackground);
                }
            });
        });
    }

    private void updateBackground(String theme) {
        if (binding == null) return;
        int backgroundRes, bedRes;
        switch (theme) {
            case "tema_airport":
                backgroundRes = R.drawable.myroom_airport;
                bedRes = R.drawable.bed_airport;
                break;
            case "tema_submarine":
                backgroundRes = R.drawable.myroom_submarine;
                bedRes = R.drawable.bed_submarine;
                break;
            case "tema_rocket":
                backgroundRes = R.drawable.myroom_rocket;
                bedRes = R.drawable.bed_rocket;
                break;
            case "tema_island":
                backgroundRes = R.drawable.myroom_island;
                bedRes = R.drawable.bed_island;
                break;
            default:
                backgroundRes = R.drawable.myroom_basic;
                bedRes = R.drawable.bed_basic;
                break;
        }
        binding.imgBackground.setImageResource(backgroundRes);
        binding.imgBed.setImageResource(bedRes);
        saveBackgroundTheme(theme);
    }

    private void saveBackgroundTheme(String theme) {
        checkGroupMembership((isInGroup, reference, field) -> {
            reference.update("tema_background", theme)
                    .addOnSuccessListener(aVoid -> Log.d("MainFragment", "Theme background updated"))
                    .addOnFailureListener(e -> Log.e("MainFragment", "Failed to update theme background", e));
        });
    }

    private void openArrangementDialog(int imageResource) {
        ObjectArrangementDialogFragment arrangementDialog = ObjectArrangementDialogFragment.newInstance(imageResource, this);
        arrangementDialog.show(getParentFragmentManager(), "ObjectArrangementDialogFragment");
    }

    private void openDeleteDialog() {
        ObjectDeleteDialogFragment deleteDialog = ObjectDeleteDialogFragment.newInstance(() -> {
            if (binding != null) {
                binding.imgShop1.setVisibility(View.GONE);
            }
            isShop1Arranged = false;
        });
        deleteDialog.show(getParentFragmentManager(), "ObjectDeleteDialogFragment");
    }

    @Override
    public void onObjectArranged() {
        if (binding != null) {
            binding.imgShop1.setVisibility(View.VISIBLE);
        }
        isShop1Arranged = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void checkGroupMembership(GroupCheckCallback callback) {
        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnSuccessListener(groupQuerySnapshot -> {
                    if (!groupQuerySnapshot.isEmpty()) {
                        DocumentReference groupRef = groupQuerySnapshot.getDocuments().get(0).getReference();
                        callback.onGroupCheckCompleted(true, groupRef, "owner_last_checkin");
                    } else {
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnSuccessListener(inviteQuerySnapshot -> {
                                    if (!inviteQuerySnapshot.isEmpty()) {
                                        DocumentReference invitedGroupRef = inviteQuerySnapshot.getDocuments().get(0).getReference();
                                        callback.onGroupCheckCompleted(true, invitedGroupRef, "invited_last_checkin");
                                    } else {
                                        DocumentReference userRef = db.collection("users").document(userId);
                                        callback.onGroupCheckCompleted(false, userRef, "last_checkin");
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("MainFragment", "Error checking invited groups", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("MainFragment", "Error checking group ownership", e));
    }

    interface GroupCheckCallback {
        void onGroupCheckCompleted(boolean isInGroup, DocumentReference reference, String field);
    }
}
