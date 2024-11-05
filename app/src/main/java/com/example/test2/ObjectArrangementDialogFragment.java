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

    // 배치 완료 시 호출되는 리스너 인터페이스 정의
    public interface OnObjectArrangementCompleteListener {
        void onObjectArranged();
    }

    private OnObjectArrangementCompleteListener arrangementCompleteListener;

    // newInstance 메서드에서 리스너를 전달받도록 수정
    public static ObjectArrangementDialogFragment newInstance(int imageResource, OnObjectArrangementCompleteListener listener) {
        ObjectArrangementDialogFragment fragment = new ObjectArrangementDialogFragment();
        fragment.arrangementCompleteListener = listener; // 리스너 설정
        Bundle args = new Bundle();
        args.putInt("imageResource", imageResource);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.object_arrangement, container, false);

        if (getArguments() != null) {
            imageResource = getArguments().getInt("imageResource", -1);
        }

        // imageView33에 이미지 리소스 설정
        ImageView imageView33 = view.findViewById(R.id.imageView33);
        if (imageResource != -1) {
            imageView33.setImageResource(imageResource);
        }

        // "뒤로가기" 버튼 설정
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> dismiss());

        // "마이룸에 배치" 버튼 설정
        Button arrangeButton = view.findViewById(R.id.button2);
        arrangeButton.setOnClickListener(v -> {
            // 배치 완료 후 콜백을 통해 shop1을 표시
            if (arrangementCompleteListener != null) {
                arrangementCompleteListener.onObjectArranged();
            }
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.white); // 흰색 배경 설정
        }
    }
}
