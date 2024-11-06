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

public class MainFragment extends Fragment implements ObjectArrangementDialogFragment.OnObjectArrangementCompleteListener {

    private FragmentMainBinding binding;
    private ThemeViewModel themeViewModel;
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_SELECTED_THEME = "selected_theme";
    private FirebaseHelper firebaseHelper;

    // 플래그 변수: 오브제가 배치되었는지 여부를 확인
    public boolean isShop1Arranged = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        themeViewModel = new ViewModelProvider(requireActivity()).get(ThemeViewModel.class);
        firebaseHelper = new FirebaseHelper();

        loadCoinData();
        loadPurchasedObjects();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedTheme = sharedPreferences.getString(KEY_SELECTED_THEME, "tema_home");
        themeViewModel.initTheme(savedTheme);
        themeViewModel.getSelectedTheme().observe(getViewLifecycleOwner(), this::updateBackground);

        // 버튼 클릭 리스너 설정
        if (binding != null) {
            setupButtonListeners();
        }
    }

    private void setupButtonListeners() {
        binding.btnProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), Person.class)));
        binding.btnReward.setOnClickListener(v -> startActivity(new Intent(getActivity(), Reward.class)));
        binding.btnBell.setOnClickListener(v -> startActivity(new Intent(getActivity(), Bell.class)));

        binding.btnStore.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Store.class);
            intent.putExtra("isShop1Arranged", isShop1Arranged); // 현재 배치 상태 전달
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

    private void loadCoinData() {
        if (binding == null) return;

        firebaseHelper.getCoinData(coinStatus -> {
            if (coinStatus != null && binding != null) { // 비동기 작업에서 binding이 null이 아닌지 확인
                binding.coinTextView.setText(String.valueOf(coinStatus));
            } else {
                Log.e("MainFragment", "Binding is null or coinStatus is null when setting coinTextView");
            }
        });
    }

    private void loadPurchasedObjects() {
        if (binding == null) return;

        firebaseHelper.getPurchasedObjects(purchasedObjects -> {
            if (purchasedObjects != null && binding != null) {
                if (purchasedObjects.contains("shop1")) {
                    binding.imgShop1.setVisibility(View.VISIBLE);
                    isShop1Arranged = true;
                }
            } else {
                Log.e("MainFragment", "Binding is null or purchasedObjects is null when setting imgShop1 visibility");
            }
        });
    }


    private void updateBackground(String theme) {
        if (binding == null) return;

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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SELECTED_THEME, theme);
        editor.apply();
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
