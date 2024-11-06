package com.example.test2.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainFragment extends Fragment implements ObjectArrangementDialogFragment.OnObjectArrangementCompleteListener {

    private FragmentMainBinding binding;
    private ThemeViewModel themeViewModel;
    private FirebaseHelper firebaseHelper;
    private FirebaseFirestore db;
    private static final String PREFS_NAME = "checkin_prefs";
    private static final String KEY_CHECKIN_DATE = "last_checkin_date";
    private static final String KEY_SELECTED_THEME = "selected_theme";
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
        setupButtonListeners();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedTheme = sharedPreferences.getString(KEY_SELECTED_THEME, "tema_home");
        themeViewModel.initTheme(savedTheme);
        themeViewModel.getSelectedTheme().observe(getViewLifecycleOwner(), this::updateBackground);
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
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lastCheckInDate = sharedPreferences.getString(KEY_CHECKIN_DATE, "");

        String todayDate = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());

        if (!todayDate.equals(lastCheckInDate)) {
            incrementCoin();
            sharedPreferences.edit().putString(KEY_CHECKIN_DATE, todayDate).apply();
            Toast.makeText(getContext(), "출석 체크! 코인이 1개 증가했습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "오늘은 이미 출석 체크를 완료했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void incrementCoin() {
        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnSuccessListener(groupQuerySnapshot -> {
                    if (!groupQuerySnapshot.isEmpty()) {
                        // 그룹이 있으면 해당 그룹의 `coinStatus` 증가
                        DocumentReference groupRef = groupQuerySnapshot.getDocuments().get(0).getReference();
                        groupRef.update("coinStatus", FieldValue.increment(1))
                                .addOnSuccessListener(aVoid -> Log.d("MainFragment", "Coin updated in group"))
                                .addOnFailureListener(e -> Log.e("MainFragment", "Failed to update coin in group", e));
                    } else {
                        // 그룹이 없으면 `users` 컬렉션의 `coinStatus` 증가
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnSuccessListener(inviteQuerySnapshot -> {
                                    if (!inviteQuerySnapshot.isEmpty()) {
                                        DocumentReference invitedGroupRef = inviteQuerySnapshot.getDocuments().get(0).getReference();
                                        invitedGroupRef.update("coinStatus", FieldValue.increment(1))
                                                .addOnSuccessListener(aVoid -> Log.d("MainFragment", "Coin updated in invited group"))
                                                .addOnFailureListener(e -> Log.e("MainFragment", "Failed to update coin in invited group", e));
                                    } else {
                                        // 그룹이 없으면 `users` 컬렉션의 `coinStatus` 증가
                                        DocumentReference userRef = db.collection("users").document(userId);
                                        userRef.update("coinStatus", FieldValue.increment(1))
                                                .addOnSuccessListener(aVoid -> Log.d("MainFragment", "Coin updated in user"))
                                                .addOnFailureListener(e -> Log.e("MainFragment", "Failed to update coin in user", e));
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("MainFragment", "Error checking invited groups", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("MainFragment", "Error checking group ownership", e));
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

    private void updateBackground(String theme) {
        switch (theme) {
            case "tema_home":
                binding.imgBackground.setImageResource(R.drawable.myroom_basic);
                binding.imgBed.setImageResource(R.drawable.bed_basic);
                break;
            case "tema_airport":
                binding.imgBackground.setImageResource(R.drawable.myroom_airport);
                binding.imgBed.setImageResource(R.drawable.bed_airport);
                break;
            case "tema_submarine":
                binding.imgBackground.setImageResource(R.drawable.myroom_submarine);
                binding.imgBed.setImageResource(R.drawable.bed_submarine);
                break;
            case "tema_rocket":
                binding.imgBackground.setImageResource(R.drawable.myroom_rocket);
                binding.imgBed.setImageResource(R.drawable.bed_rocket);
                break;
            case "tema_island":
                binding.imgBackground.setImageResource(R.drawable.myroom_island);
                binding.imgBed.setImageResource(R.drawable.bed_island);
                break;
            default:
                binding.imgBackground.setImageResource(R.drawable.myroom_basic);
                binding.imgBed.setImageResource(R.drawable.bed_basic);
        }

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(KEY_SELECTED_THEME, theme).apply();
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
}
