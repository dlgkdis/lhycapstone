package com.example.test2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class Store extends AppCompatActivity implements ObjectBuyDialogFragment.OnPurchaseCompleteListener {

    private SharedPreferences sharedPreferences;
    private TextView coinTextView;
    private FirebaseHelper firebaseHelper;
    private List<String> purchasedObjects;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store);

        sharedPreferences = getSharedPreferences("purchase_status", Context.MODE_PRIVATE);
        firebaseHelper = new FirebaseHelper();

        coinTextView = findViewById(R.id.coinTextView);
        loadCoinData();

        // 구매한 오브제 목록을 불러온 후 UI를 설정
        firebaseHelper.getPurchasedObjects(purchasedList -> {
            purchasedObjects = purchasedList;
            initializeShopButtons();
        });

        ImageButton backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    private void initializeShopButtons() {
        setupShopButton(findViewById(R.id.shop1), "shop1", R.drawable.shop1, 11);
        setupShopButton(findViewById(R.id.shop2), "shop2", R.drawable.shop2, 4);
        setupShopButton(findViewById(R.id.shop3), "shop3", R.drawable.shop3, 18);
        setupShopButton(findViewById(R.id.shop4), "shop4", R.drawable.shop4, 8);
        setupShopButton(findViewById(R.id.shop5), "shop5", R.drawable.shop5, 3);
        setupShopButton(findViewById(R.id.shop6), "shop6", R.drawable.shop6, 7);
        setupShopButton(findViewById(R.id.shop7), "shop7", R.drawable.shop7, 13);
        setupShopButton(findViewById(R.id.shop8), "shop8", R.drawable.shop8, 7);
        setupShopButton(findViewById(R.id.shop9), "shop9", R.drawable.shop9, 6);
        setupShopButton(findViewById(R.id.shop10), "shop10", R.drawable.shop10, 6);
        setupShopButton(findViewById(R.id.shop11), "shop11", R.drawable.shop11, 4);
        setupShopButton(findViewById(R.id.shop12), "shop12", R.drawable.shop12, 10);
        setupShopButton(findViewById(R.id.shop13), "shop13", R.drawable.shop13, 5);
        setupShopButton(findViewById(R.id.shop14), "shop14", R.drawable.shop14, 16);
        setupShopButton(findViewById(R.id.shop15), "shop15", R.drawable.shop15, 20);
        setupShopButton(findViewById(R.id.shop16), "shop16", R.drawable.shop16, 9);
    }

    private void setupShopButton(View shopButton, String itemId, int imageResource, int tacoCount) {
        ImageView lockIcon = findViewById(getResources().getIdentifier(itemId + "_lock", "id", getPackageName()));

        // 구매 목록에 오브제가 있는지 확인하여 자물쇠 상태 설정
        boolean isPurchased = purchasedObjects != null && purchasedObjects.contains(itemId);
        lockIcon.setVisibility(isPurchased ? View.GONE : View.VISIBLE);

        shopButton.setOnClickListener(v -> {
            if (isPurchased) {
                // 오브제가 구매된 상태면 배치 화면으로 이동
                ObjectArrangementDialogFragment arrangementDialog = new ObjectArrangementDialogFragment();
                Bundle args = new Bundle();
                args.putInt("imageResource", imageResource);
                arrangementDialog.setArguments(args);
                arrangementDialog.show(getSupportFragmentManager(), "ObjectArrangementDialogFragment");
            } else {
                // 구매되지 않은 상태면 구매 화면 표시
                ObjectBuyDialogFragment buyDialog = ObjectBuyDialogFragment.newInstance(itemId, imageResource, tacoCount, this);
                buyDialog.show(getSupportFragmentManager(), "ObjectBuyDialogFragment");
            }
        });
    }

    @Override
    public void onPurchaseComplete(String itemId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(itemId, true);
        editor.apply();

        // Firebase에 구입한 오브제 추가
        firebaseHelper.addPurchasedObject(itemId, success -> {
            if (success) {
                // 성공적으로 Firebase에 추가된 경우, 구매 목록에 추가하고 UI 업데이트
                if (purchasedObjects != null) {
                    purchasedObjects.add(itemId);
                }
                setupShopButton(
                        findViewById(getResources().getIdentifier(itemId, "id", getPackageName())),
                        itemId,
                        getResources().getIdentifier(itemId, "drawable", getPackageName()),
                        0
                );
            } else {
                Toast.makeText(this, "오브제를 Firebase에 추가하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCoinData() {
        firebaseHelper.getCoinData(coinData -> {
            if (coinData != null) {
                coinTextView.setText(String.valueOf(coinData));
            } else {
                coinTextView.setText("0");
            }
        });
    }
}
