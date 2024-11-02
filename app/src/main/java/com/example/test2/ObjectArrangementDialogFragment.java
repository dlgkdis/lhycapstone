package com.example.test2;

import android.content.Intent;
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

    public static ObjectArrangementDialogFragment newInstance(int imageResource) {
        ObjectArrangementDialogFragment fragment = new ObjectArrangementDialogFragment();
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
            // TODO: "마이룸에 배치" 동작 추가
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
