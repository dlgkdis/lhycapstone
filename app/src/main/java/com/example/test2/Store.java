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
import android.util.Log;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Arrays;



public class Store extends AppCompatActivity implements ObjectBuyDialogFragment.OnPurchaseCompleteListener {

    private SharedPreferences sharedPreferences;
    private TextView coinTextView;
    private FirebaseHelper firebaseHelper;
    private List<String> purchasedObjects;
    private ArrangeManager arrangeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store);

        sharedPreferences = getSharedPreferences("purchase_status", Context.MODE_PRIVATE);
        firebaseHelper = new FirebaseHelper();
        arrangeManager = new ArrangeManager(this);

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
        setupShopButton(findViewById(R.id.shop1), "shop1", R.drawable.shop1, 11, getAllowedThemes("shop1"));
        setupShopButton(findViewById(R.id.shop2), "shop2", R.drawable.shop2, 4, getAllowedThemes("shop2"));
        setupShopButton(findViewById(R.id.shop3), "shop3", R.drawable.shop3, 18, getAllowedThemes("shop3"));
        setupShopButton(findViewById(R.id.shop4), "shop4", R.drawable.shop4, 8, getAllowedThemes("shop4"));
        setupShopButton(findViewById(R.id.shop5), "shop5", R.drawable.shop5, 3, getAllowedThemes("shop5"));
        setupShopButton(findViewById(R.id.shop6), "shop6", R.drawable.shop6, 7, getAllowedThemes("shop6"));
        setupShopButton(findViewById(R.id.shop7), "shop7", R.drawable.shop7, 13, getAllowedThemes("shop7"));
        setupShopButton(findViewById(R.id.shop8), "shop8", R.drawable.shop8, 7, getAllowedThemes("shop8"));
        setupShopButton(findViewById(R.id.shop9), "shop9", R.drawable.shop9, 6, getAllowedThemes("shop9"));
        setupShopButton(findViewById(R.id.shop10), "shop10", R.drawable.shop10, 6, getAllowedThemes("shop10"));
        setupShopButton(findViewById(R.id.shop11), "shop11", R.drawable.shop11, 4, getAllowedThemes("shop11"));
        setupShopButton(findViewById(R.id.shop12), "shop12", R.drawable.shop12, 10, getAllowedThemes("shop12"));
        setupShopButton(findViewById(R.id.shop13), "shop13", R.drawable.shop13, 5, getAllowedThemes("shop13"));
        setupShopButton(findViewById(R.id.shop14), "shop14", R.drawable.shop14, 16, getAllowedThemes("shop14"));
        setupShopButton(findViewById(R.id.shop15), "shop15", R.drawable.shop15, 20, getAllowedThemes("shop15"));
        setupShopButton(findViewById(R.id.shop16), "shop16", R.drawable.shop16, 9, getAllowedThemes("shop16"));
    }

    private void setupShopButton(View shopButton, String itemId, int imageResource, int tacoCount, List<String> allowedThemes) {
        ImageView lockIcon = findViewById(getResources().getIdentifier(itemId + "_lock", "id", getPackageName()));

        boolean isPurchased = purchasedObjects != null && purchasedObjects.contains(itemId);
        lockIcon.setVisibility(isPurchased ? View.GONE : View.VISIBLE);

        shopButton.setOnClickListener(v -> {
            if (isPurchased) {
                checkThemeRestrictionAndArrange(itemId, imageResource, allowedThemes);
            } else {
                // allowedThemes를 포함하여 ObjectBuyDialogFragment 인스턴스 생성
                ObjectBuyDialogFragment buyDialog = ObjectBuyDialogFragment.newInstance(itemId, imageResource, tacoCount, this, allowedThemes);
                buyDialog.show(getSupportFragmentManager(), "ObjectBuyDialogFragment");
            }
        });
    }


    private void checkThemeRestrictionAndArrange(String itemId, int imageResource, List<String> allowedThemes) {
        // 배치 다이얼로그를 열고 테마 검증을 다이얼로그 내에서 수행하도록 설정
        ObjectArrangementDialogFragment arrangementDialog = ObjectArrangementDialogFragment.newInstance(imageResource, itemId, allowedThemes);
        arrangementDialog.show(getSupportFragmentManager(), "ObjectArrangementDialogFragment");
    }

    private void checkGroupMembership(GroupCheckCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnSuccessListener(groupQuerySnapshot -> {
                    if (!groupQuerySnapshot.isEmpty()) {
                        DocumentReference groupRef = groupQuerySnapshot.getDocuments().get(0).getReference();
                        callback.onGroupCheckCompleted(true, groupRef);
                    } else {
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnSuccessListener(inviteQuerySnapshot -> {
                                    if (!inviteQuerySnapshot.isEmpty()) {
                                        DocumentReference invitedGroupRef = inviteQuerySnapshot.getDocuments().get(0).getReference();
                                        callback.onGroupCheckCompleted(true, invitedGroupRef);
                                    } else {
                                        DocumentReference userRef = db.collection("users").document(userId);
                                        callback.onGroupCheckCompleted(false, userRef);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Store", "Error checking invited groups", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Store", "Error checking group ownership", e));
    }

    //테마별 오브제 설정
    private List<String> getAllowedThemes(String itemId) {
        switch (itemId) {
            case "shop1":
                return Arrays.asList("tema_island");
            case "shop2":
                return Arrays.asList("tema_home", "tema_airport");
            // 각 오브제에 맞는 테마 목록 추가
            default:
                return Arrays.asList("tema_home");
        }
    }

    @Override
    public void onPurchaseComplete(String itemId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(itemId, true);
        editor.apply();

        firebaseHelper.addPurchasedObject(itemId, success -> {
            if (success) {
                if (purchasedObjects != null) {
                    purchasedObjects.add(itemId);
                }

                View shopButton = findViewById(getResources().getIdentifier(itemId, "id", getPackageName()));
                int imageResource = getResources().getIdentifier(itemId, "drawable", getPackageName());
                setupShopButton(shopButton, itemId, imageResource, 0, getAllowedThemes(itemId));
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

    interface GroupCheckCallback {
        void onGroupCheckCompleted(boolean isInGroup, DocumentReference reference);
    }
}

