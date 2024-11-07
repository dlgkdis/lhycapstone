package com.example.test2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import java.util.List;

public class Reward extends AppCompatActivity {

    private FirebaseFirestore db;
    private static final int MAX_REWARD_STEP = 10;
    private String userId;
    private int currentRewardStep = 1;
    private String rewardDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewardpage);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        ImageButton backButton = findViewById(R.id.imageButton66);
        backButton.setOnClickListener(v -> finish());

        retrieveRewardData();
    }

    private void retrieveRewardData() {
        checkGroupMembership((isInGroup, reference) -> {
            reference.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.contains("rewardStep")) {
                    currentRewardStep = documentSnapshot.getLong("rewardStep").intValue();
                }
                displayRewardButtons();
            });
        });
    }

    private void displayRewardButtons() {
        checkGroupMembership((isInGroup, reference) -> {
            reference.get().addOnSuccessListener(documentSnapshot -> {
                String todayDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Calendar.getInstance().getTime());
                if (documentSnapshot.contains("rewardDate")) {
                    rewardDate = documentSnapshot.getString("rewardDate");
                }

                boolean alreadyClaimedToday = todayDate.equals(rewardDate);

                for (int i = 1; i <= MAX_REWARD_STEP; i++) {
                    final int rewardStep = i;
                    ImageButton rewardButton = findViewById(getRewardButtonId(rewardStep));

                    boolean isClaimed = rewardStep < currentRewardStep;
                    if (isClaimed) {
                        markRewardAsClaimed(rewardStep);  // Show overlay on previously claimed rewards
                    }

                    rewardButton.setEnabled(!alreadyClaimedToday && rewardStep == currentRewardStep);

                    if (rewardStep == currentRewardStep) {
                        rewardButton.setOnClickListener(v -> {
                            if (currentRewardStep < MAX_REWARD_STEP) {
                                claimReward(rewardStep, rewardButton);
                            } else {
                                Toast.makeText(this, "모든 보상을 수령하였습니다.", Toast.LENGTH_SHORT).show();
                                rewardButton.setEnabled(false);
                            }
                        });
                    } else {
                        rewardButton.setOnClickListener(null);
                    }
                }
            }).addOnFailureListener(e -> Log.e("Reward", "Error fetching reward date", e));
        });
    }

    private void claimReward(int rewardStep, ImageButton rewardButton) {
        // 보상 지급 처리
        switch (rewardStep) {
            case 1:
                grantReward("coin", 1);
                break;
            case 2:
                grantReward("coin", 1);
                break;
            case 3:
                grantReward("coin", 3);
                break;
            case 4:
                grantReward("coin", 1);
                break;
            case 5:
                grantReward("object", 1); // Object shop2 추가
                break;
            case 6:
                grantReward("coin", 1);
                break;
            case 7:
                grantReward("object", 2); // Object shop5 추가
                break;
            case 8:
                grantReward("coin", 1);
                break;
            case 9:
                grantReward("coin", 3);
                break;
            case 10:
                Toast.makeText(this, "모든 보상을 수령하였습니다.", Toast.LENGTH_SHORT).show();
                rewardButton.setEnabled(false);
                return;  // MAX_REWARD_STEP에 도달 시 종료
        }

        markRewardAsClaimed(rewardStep);
        updateRewardStep(rewardStep);
        updateRewardDate();
    }

    private void updateRewardDate() {
        String todayDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        checkGroupMembership((isInGroup, reference) -> {
            reference.update("rewardDate", todayDate)
                    .addOnSuccessListener(aVoid -> Log.d("Reward", "Reward date updated"))
                    .addOnFailureListener(e -> Log.e("Reward", "Failed to update reward date", e));
        });
    }

    private void updateRewardStep(int rewardStep) {
        int nextStep = rewardStep < MAX_REWARD_STEP ? rewardStep + 1 : MAX_REWARD_STEP;

        checkGroupMembership((isInGroup, reference) -> {
            reference.update("rewardStep", nextStep)
                    .addOnSuccessListener(aVoid -> {
                        currentRewardStep = nextStep;
                        displayRewardButtons();
                    })
                    .addOnFailureListener(e -> Log.e("Reward", "Failed to update reward step", e));
        });
    }

    private void grantReward(String type, int amount) {
        if ("coin".equals(type)) {
            incrementCoin(amount);
            Toast.makeText(this, "타코야키 " + amount + "개 획득!", Toast.LENGTH_SHORT).show();
        } else if ("object".equals(type)) {
            addPurchasedObject(amount == 1 ? "shop2" : "shop5");
            Toast.makeText(this, amount == 1 ? "시계 오브제 획득!" : "컵 오브제 획득!", Toast.LENGTH_SHORT).show();
        }
    }

    private void incrementCoin(int amount) {
        checkGroupMembership((isInGroup, reference) -> {
            reference.update("coinStatus", FieldValue.increment(amount))
                    .addOnSuccessListener(aVoid -> Log.d("Reward", "Coin updated"))
                    .addOnFailureListener(e -> Log.e("Reward", "Failed to update coin", e));
        });
    }

    private void addPurchasedObject(String objectId) {
        checkGroupMembership((isInGroup, reference) -> {
            // 먼저 purchasedObjects 배열에 objectId가 있는지 확인합니다.
            reference.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // purchasedObjects 배열을 가져와 objectId가 포함되어 있는지 확인
                            if (documentSnapshot.contains("purchasedObjects") &&
                                    documentSnapshot.get("purchasedObjects") instanceof List) {

                                List<String> purchasedObjects = (List<String>) documentSnapshot.get("purchasedObjects");

                                if (purchasedObjects.contains(objectId)) {
                                    // objectId가 이미 있으면 코인 3개 추가
                                    incrementCoin(3);
                                    Toast.makeText(this, "이미 보유 중인 오브제입니다. 타코야키 3개를 받았습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // objectId가 없으면 새로운 오브제 추가
                                    addNewObjectToCollection(reference, objectId);
                                }
                            } else {
                                // purchasedObjects 배열이 비어있는 경우, 오브제 추가
                                addNewObjectToCollection(reference, objectId);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Reward", "Error checking purchased objects", e));
        });
    }

    private void addNewObjectToCollection(DocumentReference reference, String objectId) {
        reference.update("purchasedObjects", FieldValue.arrayUnion(objectId))
                .addOnSuccessListener(aVoid -> {
                    Log.d("Reward", "Object added to collection");
                })
                .addOnFailureListener(e -> Log.e("Reward", "Failed to add object to collection", e));
    }


    private void markRewardAsClaimed(int rewardStep) {
        // 보상 단계에 따라 해당하는 success 이미지의 ID를 동적으로 가져옵니다.
        String successImageId = "reward" + rewardStep + "_success";
        int resId = getResources().getIdentifier(successImageId, "id", getPackageName());

        // 성공 이미지의 visibility를 VISIBLE로 설정
        ImageView successImageView = findViewById(resId);
        if (successImageView != null) {
            successImageView.setVisibility(View.VISIBLE); // 해당 이미지 표시
        } else {
            Log.e("Reward", "Success image not found for reward step: " + rewardStep);
        }

        // 해당 보상 버튼을 비활성화
        ImageButton rewardButton = findViewById(getRewardButtonId(rewardStep));
        if (rewardButton != null) {
            rewardButton.setEnabled(false);
        }
    }




    private int getRewardButtonId(int rewardStep) {
        switch (rewardStep) {
            case 1: return R.id.reward1;
            case 2: return R.id.reward2;
            case 3: return R.id.reward3;
            case 4: return R.id.reward4;
            case 5: return R.id.reward5;
            case 6: return R.id.reward6;
            case 7: return R.id.reward7;
            case 8: return R.id.reward8;
            case 9: return R.id.reward9;
            case 10: return R.id.imageView35;
            default: return R.id.reward1;
        }
    }

    private void checkGroupMembership(GroupCheckCallback callback) {
        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnSuccessListener(groupQuerySnapshot -> {
                    if (!groupQuerySnapshot.isEmpty()) {
                        callback.onGroupCheckCompleted(true, groupQuerySnapshot.getDocuments().get(0).getReference());
                    } else {
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnSuccessListener(inviteQuerySnapshot -> {
                                    if (!inviteQuerySnapshot.isEmpty()) {
                                        callback.onGroupCheckCompleted(true, inviteQuerySnapshot.getDocuments().get(0).getReference());
                                    } else {
                                        callback.onGroupCheckCompleted(false, db.collection("users").document(userId));
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Reward", "Error checking invited groups", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Reward", "Error checking group ownership", e));
    }

    interface GroupCheckCallback {
        void onGroupCheckCompleted(boolean isInGroup, DocumentReference reference);
    }
}
