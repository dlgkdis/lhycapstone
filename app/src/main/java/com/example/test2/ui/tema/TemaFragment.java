package com.example.test2.ui.tema;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.test2.R;
import com.example.test2.databinding.FragmentTemaBinding;

public class TemaFragment extends Fragment {

    public interface OnThemeSelectedListener {
        void onThemeSelected(String theme);
    }

    private OnThemeSelectedListener themeSelectedListener;
    private FragmentTemaBinding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnThemeSelectedListener) {
            themeSelectedListener = (OnThemeSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnThemeSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTemaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 각 테마 버튼에 클릭 리스너 설정
        binding.btnTemaHome.setOnClickListener(v -> {
            if (themeSelectedListener != null) themeSelectedListener.onThemeSelected("tema_home");
        });

        binding.btnTemaAirport.setOnClickListener(v -> {
            if (themeSelectedListener != null) themeSelectedListener.onThemeSelected("tema_airport");
        });

        binding.btnTemaSubmarine.setOnClickListener(v -> {
            if (themeSelectedListener != null) themeSelectedListener.onThemeSelected("tema_submarine");
        });

        binding.btnTemaRocket.setOnClickListener(v -> {
            if (themeSelectedListener != null) themeSelectedListener.onThemeSelected("tema_rocket");
        });

        binding.btnTemaIsland.setOnClickListener(v -> {
            if (themeSelectedListener != null) themeSelectedListener.onThemeSelected("tema_island");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 메모리 누수 방지
    }
}
