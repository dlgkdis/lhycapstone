package com.example.test2.ui.tema;

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

        // 각 버튼 클릭 시 테마 설정 및 스타일 적용
        binding.btnTemaHome.setOnClickListener(v -> setTheme("tema_home", v));
        binding.btnTemaAirport.setOnClickListener(v -> setTheme("tema_airport", v));
        binding.btnTemaSubmarine.setOnClickListener(v -> setTheme("tema_submarine", v));
        binding.btnTemaRocket.setOnClickListener(v -> setTheme("tema_rocket", v));
        binding.btnTemaIsland.setOnClickListener(v -> setTheme("tema_island", v));
    }

    private void setTheme(String theme, View button) {
        // ThemeViewModel에 테마 설정
        themeViewModel.setTheme(theme);

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
