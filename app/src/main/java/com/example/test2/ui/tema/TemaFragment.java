package com.example.test2.ui.tema;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.test2.R;

public class TemaFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tema, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 여기서 뷰와 상호작용을 할 수 있습니다.
        // 예를 들어, ImageButton 클릭 이벤트 설정:
        view.findViewById(R.id.imageButton).setOnClickListener(v -> {
            // "테마_홈" 버튼 클릭 시 처리할 동작
        });

        view.findViewById(R.id.imageButton2).setOnClickListener(v -> {
            // "테마_비행기" 버튼 클릭 시 처리할 동작
        });

        view.findViewById(R.id.imageButton3).setOnClickListener(v -> {
            // "테마_잠수함" 버튼 클릭 시 처리할 동작
        });

        view.findViewById(R.id.imageButton5).setOnClickListener(v -> {
            // "테마_로켓" 버튼 클릭 시 처리할 동작
        });

        view.findViewById(R.id.imageButton6).setOnClickListener(v -> {
            // "테마_섬" 버튼 클릭 시 처리할 동작
        });
    }
}
