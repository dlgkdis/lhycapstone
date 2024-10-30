// MainFragment.java
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

import com.example.test2.R;
import com.example.test2.Store;
import com.example.test2.Person;
import com.example.test2.Reward;
import com.example.test2.Bell;
import com.example.test2.databinding.FragmentMainBinding;
import com.example.test2.ui.ThemeViewModel;
import com.example.test2.FirebaseHelper;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private ThemeViewModel themeViewModel;
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_SELECTED_THEME = "selected_theme";
    private FirebaseHelper firebaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel 초기화
        themeViewModel = new ViewModelProvider(requireActivity()).get(ThemeViewModel.class);

        // FirebaseHelper 초기화
        firebaseHelper = new FirebaseHelper();  // 여기서 인스턴스 생성

        // Load coin data and set it on the UI
        loadCoinData();

        // SharedPreferences에서 초기 테마 값을 가져와 ViewModel에 설정
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedTheme = sharedPreferences.getString(KEY_SELECTED_THEME, "tema_home");
        themeViewModel.initTheme(savedTheme);

        // 테마 변경을 관찰하여 배경 업데이트
        themeViewModel.getSelectedTheme().observe(getViewLifecycleOwner(), this::updateBackground);

        // 버튼 클릭 리스너 설정
        binding.btnStore.setOnClickListener(v -> startActivity(new Intent(getActivity(), Store.class)));
        binding.btnProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), Person.class)));
        binding.btnReward.setOnClickListener(v -> startActivity(new Intent(getActivity(), Reward.class)));
        binding.btnBell.setOnClickListener(v -> startActivity(new Intent(getActivity(), Bell.class)));
    }

    // 코인 데이터를 불러와 UI에 표시하는 메서드
    private void loadCoinData() {
        firebaseHelper.getCoinData(coinStatus -> {
            if (coinStatus != null) {
                // 코인 데이터를 UI에 표시
                binding.coinTextView.setText(""+coinStatus);
            } else {
                Toast.makeText(getContext(), "코인 데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 배경 이미지를 변경하는 메서드
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

        // SharedPreferences에 테마 저장
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SELECTED_THEME, theme);
        editor.apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}