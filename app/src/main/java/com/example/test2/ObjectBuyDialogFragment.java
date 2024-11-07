package com.example.test2;

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
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;
import java.util.ArrayList;

public class ObjectBuyDialogFragment extends DialogFragment {

    private String itemId;
    private int imageResource;
    private int tacoCount;
    private List<String> allowedThemes;
    private OnPurchaseCompleteListener purchaseCompleteListener;

    public interface OnPurchaseCompleteListener {
        void onPurchaseComplete(String itemId);
    }

    public static ObjectBuyDialogFragment newInstance(String itemId, int imageResource, int tacoCount, OnPurchaseCompleteListener listener, List<String> allowedThemes) {
        ObjectBuyDialogFragment fragment = new ObjectBuyDialogFragment();
        fragment.purchaseCompleteListener = listener;
        fragment.allowedThemes = allowedThemes;
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

        // 테마 설명 표시
        TextView themeTextView = view.findViewById(R.id.checktemaText);
        themeTextView.setText(getThemeNames(allowedThemes));

        Button buyButton = view.findViewById(R.id.button2);
        buyButton.setOnClickListener(v -> {
            if (itemId != null) {
                CoinManager coinManager = new CoinManager();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                coinManager.checkAndDeductCoins(userId, tacoCount, new CoinManager.OnCoinDeductListener() {
                    @Override
                    public void onComplete(boolean success, long remainingCoins) {
                        if (success) {
                            FirebaseHelper firebaseHelper = new FirebaseHelper();
                            firebaseHelper.addPurchasedObject(itemId, purchaseSuccess -> {
                                if (purchaseSuccess) {
                                    if (purchaseCompleteListener != null) {
                                        purchaseCompleteListener.onPurchaseComplete(itemId);
                                    }
                                    dismiss();
                                } else {
                                    Toast.makeText(getContext(), "구입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "코인이 부족합니다. 현재 코인: " + remainingCoins, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> dismiss());

        return view;
    }

    private String getThemeNames(List<String> allowedThemes) {
        // 테마 ID와 설명 매핑
        List<String> themeNames = new ArrayList<>();
        for (String themeId : allowedThemes) {
            switch (themeId) {
                case "tema_home":
                    themeNames.add("집");
                    break;
                case "tema_airport":
                    themeNames.add("비행기");
                    break;
                case "tema_island":
                    themeNames.add("섬");
                    break;
                case "tema_submarine":
                    themeNames.add("잠수함");
                    break;
                case "tema_rocket":
                    themeNames.add("우주선");
                    break;
                default:
                    themeNames.add("알 수 없는 테마");
                    break;
            }
        }
        return "적용 테마: " + String.join(", ", themeNames);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.white);
        }
    }
}
