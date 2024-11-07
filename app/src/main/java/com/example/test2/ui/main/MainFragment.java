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

import com.example.test2.ArrangeManager;
import com.example.test2.R;
import com.example.test2.Store;
import com.example.test2.Person;
import com.example.test2.Reward;
import com.example.test2.Bell;
import com.example.test2.databinding.FragmentMainBinding;
import com.example.test2.ui.ThemeViewModel;
import com.example.test2.FirebaseHelper;
import com.example.test2.ObjectDeleteDialogFragment;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.example.test2.ObjectArrangementDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private ThemeViewModel themeViewModel;
    private FirebaseHelper firebaseHelper;
    private FirebaseFirestore db;
    private ArrangeManager arrangeManager;
    private static final String PREFS_NAME = "main_prefs";
    private static final String PREFS_NAME_TEMA = "theme_prefs";
    private static final String KEY_CHECKIN_DATE = "last_checkin_date";
    private static final String KEY_SELECTED_THEME = "selected_theme";
    private boolean isShop1Arranged = false;
    private boolean isShop2Arranged = false;
    private boolean isShop3Arranged = false;
    private boolean isShop4Arranged = false;
    private boolean isShop5Arranged = false;
    private boolean isShop6Arranged = false;
    private boolean isShop7Arranged = false;
    private boolean isShop8Arranged = false;
    private boolean isShop9Arranged = false;
    private boolean isShop10Arranged = false;
    private boolean isShop11Arranged = false;
    private boolean isShop12Arranged = false;
    private boolean isShop13Arranged = false;
    private boolean isShop14Arranged = false;
    private boolean isShop15Arranged = false;
    private boolean isShop16Arranged = false;

    private String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
            FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        arrangeManager = new ArrangeManager(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        themeViewModel = new ViewModelProvider(requireActivity()).get(ThemeViewModel.class);
        firebaseHelper = new FirebaseHelper();


        // 처음에는 모든 오브제를 숨김
        binding.imgShop1.setVisibility(View.GONE);
        binding.imgShop2.setVisibility(View.GONE);
        binding.imgShop3.setVisibility(View.GONE);
        binding.imgShop4.setVisibility(View.GONE);
        binding.imgShop5.setVisibility(View.GONE);
        binding.imgShop6.setVisibility(View.GONE);
        binding.imgShop7.setVisibility(View.GONE);
        binding.imgShop8.setVisibility(View.GONE);
        binding.imgShop9.setVisibility(View.GONE);
        binding.imgShop10.setVisibility(View.GONE);
        binding.imgShop11.setVisibility(View.GONE);
        binding.imgShop12.setVisibility(View.GONE);
        binding.imgShop13.setVisibility(View.GONE);
        binding.imgShop14.setVisibility(View.GONE);
        binding.imgShop15.setVisibility(View.GONE);
        binding.imgShop16.setVisibility(View.GONE);





        loadCoinData();
        loadPurchasedObjects();
        loadBackgroundTheme();
        loadArrangedObjects(); // Firestore에 저장된 배치 상태에 따라 오브제 보이기 설정

        if (binding != null) {
            setupButtonListeners();
        }
    }

    private void loadArrangedObjects() {
        // ArrangeManager에서 arrangeObjects 필드 로드 후 배치된 오브제만 보이도록 설정
        arrangeManager.loadArrangementStatus(arrangedItems -> {
            if (binding != null) {
                for (String itemId : arrangedItems) {
                    switch (itemId) {
                        case "shop1":
                            binding.imgShop1.setVisibility(View.VISIBLE);
                            isShop1Arranged = true;
                            break;
                        case "shop2":
                            binding.imgShop2.setVisibility(View.VISIBLE);
                            isShop2Arranged = true;
                            break;
                        case "shop3":
                            binding.imgShop3.setVisibility(View.VISIBLE);
                            isShop3Arranged = true;
                            break;
                        case "shop4":
                            binding.imgShop4.setVisibility(View.VISIBLE);
                            isShop4Arranged = true;
                            break;
                        case "shop5":
                            binding.imgShop5.setVisibility(View.VISIBLE);
                            isShop5Arranged = true;
                            break;
                        case "shop6":
                            binding.imgShop6.setVisibility(View.VISIBLE);
                            isShop6Arranged = true;
                            break;
                        case "shop7":
                            binding.imgShop7.setVisibility(View.VISIBLE);
                            isShop7Arranged = true;
                            break;
                        case "shop8":
                            binding.imgShop8.setVisibility(View.VISIBLE);
                            isShop8Arranged = true;
                            break;
                        case "shop9":
                            binding.imgShop9.setVisibility(View.VISIBLE);
                            isShop9Arranged = true;
                            break;
                        case "shop10":
                            binding.imgShop10.setVisibility(View.VISIBLE);
                            isShop10Arranged = true;
                            break;
                        case "shop11":
                            binding.imgShop11.setVisibility(View.VISIBLE);
                            isShop11Arranged = true;
                            break;
                        case "shop12":
                            binding.imgShop12.setVisibility(View.VISIBLE);
                            isShop12Arranged = true;
                            break;
                        case "shop13":
                            binding.imgShop13.setVisibility(View.VISIBLE);
                            isShop13Arranged = true;
                            break;
                        case "shop14":
                            binding.imgShop14.setVisibility(View.VISIBLE);
                            isShop14Arranged = true;
                            break;
                        case "shop15":
                            binding.imgShop15.setVisibility(View.VISIBLE);
                            isShop15Arranged = true;
                            break;


                    }
                }
            }
        });
    }

    private void setupButtonListeners() {
        binding.btnProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), Person.class)));
        binding.btnReward.setOnClickListener(v -> startActivity(new Intent(getActivity(), Reward.class)));
        binding.btnBell.setOnClickListener(v -> startActivity(new Intent(getActivity(), Bell.class)));

        binding.coinButton.setOnClickListener(v -> handleCheckInReward());

        binding.btnStore.setOnClickListener(v -> startActivity(new Intent(getActivity(), Store.class)));

        setupShopItemClickListener("shop1", binding.imgShop1);
        setupShopItemClickListener("shop2", binding.imgShop2);
        setupShopItemClickListener("shop3", binding.imgShop3);
        setupShopItemClickListener("shop4", binding.imgShop1);
        setupShopItemClickListener("shop5", binding.imgShop2);
        setupShopItemClickListener("shop6", binding.imgShop3);
        setupShopItemClickListener("shop7", binding.imgShop1);
        setupShopItemClickListener("shop8", binding.imgShop2);
        setupShopItemClickListener("shop9", binding.imgShop3);
        setupShopItemClickListener("shop10", binding.imgShop1);
        setupShopItemClickListener("shop11", binding.imgShop2);
        setupShopItemClickListener("shop12", binding.imgShop3);
        setupShopItemClickListener("shop13", binding.imgShop1);
        setupShopItemClickListener("shop14", binding.imgShop2);
        setupShopItemClickListener("shop15", binding.imgShop3);
        setupShopItemClickListener("shop16", binding.imgShop3);

    }

    private void setupShopItemClickListener(String itemId, View shopImageView) {
        shopImageView.setOnClickListener(v -> {
            openDeleteDialog(itemId);
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
                for (String itemId : purchasedObjects) {
                    // 구매한 오브제가 있어도 배치하기 버튼을 누르기 전까지는 숨김
                    switch (itemId) {
                        case "shop1":
                            binding.imgShop1.setVisibility(View.GONE);
                            isShop1Arranged = false;
                            break;
                        case "shop2":
                            binding.imgShop2.setVisibility(View.GONE);
                            isShop2Arranged = false;
                            break;
                        case "shop3":
                            binding.imgShop3.setVisibility(View.GONE);
                            isShop3Arranged = false;
                            break;
                        case "shop4":
                            binding.imgShop4.setVisibility(View.GONE);
                            isShop4Arranged = false;
                            break;
                        case "shop5":
                            binding.imgShop5.setVisibility(View.GONE);
                            isShop5Arranged = false;
                            break;
                        case "shop6":
                            binding.imgShop6.setVisibility(View.GONE);
                            isShop6Arranged = false;
                            break;
                        case "shop7":
                            binding.imgShop7.setVisibility(View.GONE);
                            isShop7Arranged = false;
                            break;
                        case "shop8":
                            binding.imgShop8.setVisibility(View.GONE);
                            isShop8Arranged = false;
                            break;
                        case "shop9":
                            binding.imgShop9.setVisibility(View.GONE);
                            isShop9Arranged = false;
                            break;
                        case "shop10":
                            binding.imgShop10.setVisibility(View.GONE);
                            isShop10Arranged = false;
                            break;
                        case "shop11":
                            binding.imgShop11.setVisibility(View.GONE);
                            isShop11Arranged = false;
                            break;
                        case "shop12":
                            binding.imgShop12.setVisibility(View.GONE);
                            isShop12Arranged = false;
                            break;
                        case "shop13":
                            binding.imgShop13.setVisibility(View.GONE);
                            isShop13Arranged = false;
                            break;
                        case "shop14":
                            binding.imgShop14.setVisibility(View.GONE);
                            isShop14Arranged = false;
                            break;
                        case "shop15":
                            binding.imgShop15.setVisibility(View.GONE);
                            isShop15Arranged = false;
                            break;
                        case "shop16":
                            binding.imgShop16.setVisibility(View.GONE);
                            isShop16Arranged = false;
                            break;
                    }
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

    // 오브제를 배치하는 다이얼로그를 열기 위한 메서드
    private void openArrangementDialog(String itemId, int imageResource) {
        ObjectArrangementDialogFragment arrangementDialog = ObjectArrangementDialogFragment.newInstance(imageResource, itemId);
        arrangementDialog.show(getParentFragmentManager(), "ObjectArrangementDialogFragment");
    }



    // 오브제를 삭제하는 다이얼로그를 열기 위한 메서드
    private void openDeleteDialog(String itemId) {
        ObjectDeleteDialogFragment deleteDialog = ObjectDeleteDialogFragment.newInstance(() -> {
            if (binding != null) {
                // 오브제의 ID에 맞게 UI에서 숨김 처리
                switch (itemId) {
                    case "shop1":
                        binding.imgShop1.setVisibility(View.GONE);
                        isShop1Arranged = false;
                        break;
                    case "shop2":
                        binding.imgShop2.setVisibility(View.GONE);
                        isShop2Arranged = false;
                        break;
                    case "shop3":
                        binding.imgShop3.setVisibility(View.GONE);
                        isShop3Arranged = false;
                        break;
                    case "shop4":
                        binding.imgShop4.setVisibility(View.GONE);
                        isShop4Arranged = false;
                        break;
                    case "shop5":
                        binding.imgShop5.setVisibility(View.GONE);
                        isShop5Arranged = false;
                        break;
                    case "shop6":
                        binding.imgShop6.setVisibility(View.GONE);
                        isShop6Arranged = false;
                        break;
                    case "shop7":
                        binding.imgShop7.setVisibility(View.GONE);
                        isShop7Arranged = false;
                        break;
                    case "shop8":
                        binding.imgShop8.setVisibility(View.GONE);
                        isShop8Arranged = false;
                        break;
                    case "shop9":
                        binding.imgShop9.setVisibility(View.GONE);
                        isShop9Arranged = false;
                        break;
                    case "shop10":
                        binding.imgShop10.setVisibility(View.GONE);
                        isShop10Arranged = false;
                        break;
                    case "shop11":
                        binding.imgShop11.setVisibility(View.GONE);
                        isShop11Arranged = false;
                        break;
                    case "shop12":
                        binding.imgShop12.setVisibility(View.GONE);
                        isShop12Arranged = false;
                        break;
                    case "shop13":
                        binding.imgShop13.setVisibility(View.GONE);
                        isShop13Arranged = false;
                        break;
                    case "shop14":
                        binding.imgShop14.setVisibility(View.GONE);
                        isShop14Arranged = false;
                        break;
                    case "shop15":
                        binding.imgShop15.setVisibility(View.GONE);
                        isShop15Arranged = false;
                        break;
                    case "shop16":
                        binding.imgShop16.setVisibility(View.GONE);
                        isShop16Arranged = false;
                        break;
                }
            }

            // Firestore에서 arrangeObjects에서 오브제를 제거
            arrangeManager.updateArrangementStatus(itemId, false);
        });
        deleteDialog.show(getParentFragmentManager(), "ObjectDeleteDialogFragment");
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
