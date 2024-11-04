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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ObjectBuyDialogFragment extends DialogFragment {

    private String itemId;
    private int imageResource;
    private int tacoCount;
    private OnPurchaseCompleteListener purchaseCompleteListener;

    public interface OnPurchaseCompleteListener {
        void onPurchaseComplete(String itemId);
    }

    public static ObjectBuyDialogFragment newInstance(String itemId, int imageResource, int tacoCount, OnPurchaseCompleteListener listener) {
        ObjectBuyDialogFragment fragment = new ObjectBuyDialogFragment();
        fragment.purchaseCompleteListener = listener;
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
            imageResource = getArguments().getInt("imageResource", R.drawable.shop1);
            tacoCount = getArguments().getInt("tacoCount", 0);
        }

        ImageView imageView33 = view.findViewById(R.id.imageView33);
        imageView33.setImageResource(imageResource);

        TextView tacoCountView = view.findViewById(R.id.textView29);
        tacoCountView.setText(String.valueOf(tacoCount));

        Button buyButton = view.findViewById(R.id.button2);
        buyButton.setOnClickListener(v -> {
            if (itemId != null) {
                FirebaseHelper firebaseHelper = new FirebaseHelper();
                firebaseHelper.addPurchasedObject(itemId, success -> {
                    if (success) {
                        if (purchaseCompleteListener != null) {
                            purchaseCompleteListener.onPurchaseComplete(itemId);
                        }
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "구입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> dismiss());

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
