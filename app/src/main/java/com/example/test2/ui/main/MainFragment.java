package com.example.test2.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private ThemeViewModel themeViewModel;
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_SELECTED_THEME = "selected_theme";

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

    // 배경 이미지를 변경하는 메서드
    private void updateBackground(String theme) {
        // 배경 업데이트
        switch (theme) {
            case "tema_home":
                binding.imgBackground.setImageResource(R.drawable.myroom_basic);
                break;
            case "tema_airport":
                binding.imgBackground.setImageResource(R.drawable.myroom_airport);
                break;
            case "tema_submarine":
                binding.imgBackground.setImageResource(R.drawable.myroom_submarine);
                break;
            case "tema_rocket":
                binding.imgBackground.setImageResource(R.drawable.myroom_rocket);
                break;
            case "tema_island":
                binding.imgBackground.setImageResource(R.drawable.myroom_island);
                break;
            default:
                binding.imgBackground.setImageResource(R.drawable.myroom_basic);
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
