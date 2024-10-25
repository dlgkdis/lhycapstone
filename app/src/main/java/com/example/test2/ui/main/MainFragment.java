package com.example.test2.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.test2.R;
import com.example.test2.Store;
import com.example.test2.Person;
import com.example.test2.Reward;
import com.example.test2.Bell;
import com.example.test2.databinding.FragmentMainBinding;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 버튼 클릭 리스너 설정
        binding.btnStore.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Store.class);
            startActivity(intent);
        });

        binding.btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Person.class);
            startActivity(intent);
        });

        binding.btnReward.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Reward.class);
            startActivity(intent);
        });

        binding.btnBell.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Bell.class);
            startActivity(intent);
        });
    }

    // 배경 이미지를 변경하는 메서드
    public void updateBackground(String theme) {
        if (binding == null) return;

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
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 메모리 누수 방지
    }
}
