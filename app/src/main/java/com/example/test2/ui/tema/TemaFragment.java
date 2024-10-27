package com.example.test2.ui.tema;

import android.content.Context;
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
import com.example.test2.databinding.FragmentTemaBinding;
import com.example.test2.ui.ThemeViewModel;

public class TemaFragment extends Fragment {

    private FragmentTemaBinding binding;
    private ThemeViewModel themeViewModel;
    private View selectedButton = null; // 이전에 선택된 버튼을 추적하는 변수
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_SELECTED_THEME = "selected_theme";

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

        // SharedPreferences에서 저장된 테마 불러오기
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedTheme = sharedPreferences.getString(KEY_SELECTED_THEME, null);

        // 저장된 테마가 있으면 해당 버튼을 진한 테두리로 설정
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

        // 테마 버튼 클릭 시 테두리 적용과 ThemeViewModel, SharedPreferences에 테마 설정
        binding.btnTemaHome.setOnClickListener(v -> setTheme("tema_home", v));
        binding.btnTemaAirport.setOnClickListener(v -> setTheme("tema_airport", v));
        binding.btnTemaSubmarine.setOnClickListener(v -> setTheme("tema_submarine", v));
        binding.btnTemaRocket.setOnClickListener(v -> setTheme("tema_rocket", v));
        binding.btnTemaIsland.setOnClickListener(v -> setTheme("tema_island", v));
    }

    private void setTheme(String theme, View button) {
        // ThemeViewModel에 테마 설정
        themeViewModel.setTheme(theme);

        // SharedPreferences에 테마 저장
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SELECTED_THEME, theme);
        editor.apply();

        // 이전에 선택된 버튼의 테두리를 기본 배경으로 복원
        if (selectedButton != null) {
            selectedButton.setBackgroundResource(R.drawable.rounded_button_default);
        }

        // 현재 선택된 버튼에 진한 테두리 배경 적용
        button.setBackgroundResource(R.drawable.rounded_button_selected);
        selectedButton = button; // 현재 버튼을 선택된 버튼으로 설정
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
