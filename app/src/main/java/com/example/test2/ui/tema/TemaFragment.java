package com.example.test2.ui.tema;

import android.content.Context;
import android.content.SharedPreferences;
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

public class TemaFragment extends Fragment {

    private FragmentTemaBinding binding;
    private ThemeViewModel themeViewModel;
    private View selectedButton = null;
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_SELECTED_THEME = "selected_theme";
    private FirebaseHelper firebaseHelper;
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

        themeViewModel = new ViewModelProvider(requireActivity()).get(ThemeViewModel.class);
        firebaseHelper = new FirebaseHelper();

        // 자물쇠 상태 설정 및 해제 조건 확인
        setLockState();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedTheme = sharedPreferences.getString(KEY_SELECTED_THEME, null);

        if (savedTheme != null) {
            switch (savedTheme) {
                case "tema_home":
                    setTheme("tema_home", binding.btnTemaHome);
                    break;
                case "tema_airport":
                    setTheme("tema_airport", binding.btnTemaAirport);
                    break;
                case "tema_submarine":
                    setTheme("tema_submarine", binding.btnTemaSubmarine);
                    break;
                case "tema_rocket":
                    setTheme("tema_rocket", binding.btnTemaRocket);
                    break;
                case "tema_island":
                    setTheme("tema_island", binding.btnTemaIsland);
                    break;
            }
        }

        // 테마 버튼 클릭 리스너 설정
        binding.btnTemaHome.setOnClickListener(v -> setTheme("tema_home", v));
        binding.btnTemaAirport.setOnClickListener(v -> setTheme("tema_airport", v));
        binding.btnTemaSubmarine.setOnClickListener(v -> setTheme("tema_submarine", v));
        binding.btnTemaRocket.setOnClickListener(v -> setTheme("tema_rocket", v));
        binding.btnTemaIsland.setOnClickListener(v -> setTheme("tema_island", v));
    }

    private void setLockState() {
        // 오브제 개수를 확인하여 자물쇠 상태 업데이트
        firebaseHelper.getPurchasedObjects(purchasedObjects -> {
            int purchasedObjectCount = purchasedObjects.size();
            Log.d(TAG, "현재 구매된 오브제 개수: " + purchasedObjectCount);

            // 테마 잠금 해제 조건
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

    private void setTheme(String theme, View button) {
        themeViewModel.setTheme(theme);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SELECTED_THEME, theme);
        editor.apply();

        if (selectedButton != null) {
            selectedButton.setBackgroundResource(R.drawable.rounded_button_default);
        }

        button.setBackgroundResource(R.drawable.rounded_button_selected);
        selectedButton = button;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
