// ObjectArrangementDialogFragment.java
package com.example.test2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ObjectArrangementDialogFragment extends DialogFragment {

    private int imageResource;
    private String itemId; // 배치할 오브제의 ID
    private ArrangeManager arrangeManager;

    public static ObjectArrangementDialogFragment newInstance(int imageResource, String itemId) {
        ObjectArrangementDialogFragment fragment = new ObjectArrangementDialogFragment();
        Bundle args = new Bundle();
        args.putInt("imageResource", imageResource);
        args.putString("itemId", itemId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.object_arrangement, container, false);
        arrangeManager = new ArrangeManager(requireContext());
        itemId = getArguments() != null ? getArguments().getString("itemId") : null;

        // 배치 버튼 클릭 이벤트
        view.findViewById(R.id.button2).setOnClickListener(v -> {
            arrangeManager.updateArrangementStatus(itemId, true);  // Firestore에 배치 상태 저장
            dismiss();  // 다이얼로그 닫기
        });


        if (getArguments() != null) {
            imageResource = getArguments().getInt("imageResource", -1);
            itemId = getArguments().getString("itemId");
        }

        // imageView33에 이미지 리소스 설정
        ImageView imageView33 = view.findViewById(R.id.imageView33);
        if (imageResource != -1) {
            imageView33.setImageResource(imageResource);
        }

        // "배치하기" 버튼 설정
        Button arrangeButton = view.findViewById(R.id.button2);
        arrangeButton.setOnClickListener(v -> {
            if (itemId != null) {
                arrangeManager.updateArrangementStatus(itemId, true);
            }
            dismiss();
        });

        // "뒤로가기" 버튼 설정
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> dismiss());

        return view;
    }

}
