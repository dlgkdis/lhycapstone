package com.example.test2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Store extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store);

        sharedPreferences = getSharedPreferences("purchase_status", Context.MODE_PRIVATE);

        // 각 버튼에 대해 setupShopButton 호출 시 타코야키 개수 추가
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

        ImageButton backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                Intent intent = new Intent(Store.this, MainActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupShopButton(View shopButton, String itemId, int imageResource, int tacoCount) {
        ImageView lockIcon = findViewById(getResources().getIdentifier(itemId + "_lock", "id", getPackageName()));

        // 구매 상태 확인
        boolean isPurchased = sharedPreferences.getBoolean(itemId, false);

        // 자물쇠 상태 설정
        lockIcon.setVisibility(isPurchased ? View.GONE : View.VISIBLE);

        // 클릭 이벤트 설정
        shopButton.setOnClickListener(v -> {
            if (isPurchased) {
                // 자물쇠가 열려있으면 ObjectArrangementActivity로 이동
                Intent intent = new Intent(Store.this, ObjectArrangementActivity.class);
                intent.putExtra("imageResource", imageResource); // 이미지 리소스 전달
                startActivity(intent);
            } else {
                // 자물쇠가 잠겨 있으면 ObjectBuyActivity로 이동
                Intent intent = new Intent(Store.this, ObjectBuyActivity.class);
                intent.putExtra("itemId", itemId); // itemId 전달
                intent.putExtra("imageResource", imageResource); // 이미지 리소스 전달
                intent.putExtra("tacoCount", tacoCount); // 타코야키 개수 전달
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String itemId = data.getStringExtra("itemId");
            if (itemId != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(itemId, true); // 구매 상태 저장
                editor.apply();

                // 상태 업데이트를 위해 setupShopButton 재호출
                setupShopButton(
                        findViewById(getResources().getIdentifier(itemId, "id", getPackageName())),
                        itemId,
                        getResources().getIdentifier(itemId, "drawable", getPackageName()),
                        data.getIntExtra("tacoCount", 0)
                );
            }
        }
    }
}