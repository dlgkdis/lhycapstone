package com.example.test2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ObjectBuyDialogFragment extends DialogFragment {

    private String itemId;
    private int imageResource;
    private int tacoCount;

    public static ObjectBuyDialogFragment newInstance(String itemId, int imageResource, int tacoCount) {
        ObjectBuyDialogFragment fragment = new ObjectBuyDialogFragment();
        Bundle args = new Bundle();
        args.putString("itemId", itemId);
        args.putInt("imageResource", imageResource);
        args.putInt("tacoCount", tacoCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.object_buy, container, false);

        if (getArguments() != null) {
            itemId = getArguments().getString("itemId");
            imageResource = getArguments().getInt("imageResource", R.drawable.shop1); // 기본 이미지 설정
            tacoCount = getArguments().getInt("tacoCount", 0); // 기본 타코야키 개수 설정
        }

        // 전달받은 이미지 리소스를 imageView33에 설정
        ImageView imageView33 = view.findViewById(R.id.imageView33);
        imageView33.setImageResource(imageResource);

        // 전달받은 타코야키 개수를 textView29에 설정
        TextView tacoCountView = view.findViewById(R.id.textView29);
        tacoCountView.setText(String.valueOf(tacoCount));

        // "구입" 버튼 설정
        Button buyButton = view.findViewById(R.id.button2);
        buyButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("itemId", itemId);
            getTargetFragment().onActivityResult(getTargetRequestCode(), getActivity().RESULT_OK, resultIntent);
            dismiss();
        });

        // "뒤로가기" 버튼 설정
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // 다이얼로그 스타일 설정
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
