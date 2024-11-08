package com.example.test2.ui.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.test2.R;
import com.example.test2.databinding.FragmentDiaryBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import android.util.Log;


public class NotificationsFragment extends Fragment {

    private FragmentDiaryBinding binding;
    private Calendar calendar;
    private TextView monthText;
    private GridLayout calendarGrid;
    private SharedPreferences sharedPreferences;
    private TextView selectedDayView;
    private int selectedDay;
    private LinearLayout diaryContentContainer;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;
    private Typeface customFont;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDiaryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.diaryContentContainer.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        sharedPreferences = requireContext().getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE);
        diaryContentContainer = binding.diaryContentContainer;

        binding.button14.setVisibility(View.GONE);

        binding.button14.setOnClickListener(v -> {
            saveSelectedDateToPreferences();
            String selectedDateKey = getSelectedDateKey(selectedDay);

            Bundle bundle = new Bundle();
            bundle.putString("selectedDateKey", selectedDateKey);
            Navigation.findNavController(v).navigate(R.id.action_notificationsFragment_to_diaryWriteFragment, bundle);
        });

        calendar = Calendar.getInstance();
        monthText = binding.monthText;
        calendarGrid = binding.calendarGrid;

        Typeface customFont = ResourcesCompat.getFont(getContext(), R.font.nanumfont);
        monthText.setTypeface(customFont, Typeface.BOLD);
        monthText.setTextSize(35);

        setWeekdayFonts(customFont);
        updateMonthText();
        updateCalendar();

        binding.prevMonthButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateMonthText();
            updateCalendar();
        });

        binding.nextMonthButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateMonthText();
            updateCalendar();
        });

        return root;
    }

    private void setWeekdayFonts(Typeface customFont) {
        binding.sunText.setTypeface(customFont, Typeface.BOLD);
        binding.monText.setTypeface(customFont, Typeface.BOLD);
        binding.tueText.setTypeface(customFont, Typeface.BOLD);
        binding.wedText.setTypeface(customFont, Typeface.BOLD);
        binding.thuText.setTypeface(customFont, Typeface.BOLD);
        binding.friText.setTypeface(customFont, Typeface.BOLD);
        binding.satText.setTypeface(customFont, Typeface.BOLD);
    }

    private void updateMonthText() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy년 MM월", Locale.getDefault());
        String monthYear = monthFormat.format(calendar.getTime());
        monthText.setText(monthYear);
    }

    private void updateCalendar() {
        calendarGrid.removeAllViews();

        Calendar tempCalendar = (Calendar) calendar.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < firstDayOfWeek; i++) {
            TextView emptyView = new TextView(getContext());
            emptyView.setLayoutParams(new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f)
            ));
            calendarGrid.addView(emptyView);
        }

        Typeface customFont = ResourcesCompat.getFont(getContext(), R.font.nanumfont);

        for (int day = 1; day <= daysInMonth; day++) {
            final int currentDay = day;

            TextView dayView = new TextView(getContext());
            dayView.setText(String.valueOf(day));
            dayView.setGravity(Gravity.CENTER);
            dayView.setTextSize(30);
            dayView.setPadding(0, 32, 0, 32);
            dayView.setTypeface(customFont);
            dayView.setTextColor(Color.BLACK);

            dayView.setOnClickListener(v -> onDaySelected(dayView, currentDay));

            // 날짜에 따라 텍스트 색상 설정
            String dateKey = getSelectedDateKey(currentDay);
            fetchDiaryPreview(dateKey, dayView); // 다이어리 작성 여부 확인 후 색상 설정

            calendarGrid.addView(dayView, new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f)
            ));
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void onDaySelected(TextView dayView, int day) {
        if (selectedDayView != null) {
            resetDayViewColor(selectedDayView);
        }

        dayView.setBackgroundResource(R.drawable.black_circle);
        dayView.setTextColor(Color.WHITE);
        selectedDayView = dayView;
        selectedDay = day;

        binding.button14.setVisibility(View.VISIBLE);

        String selectedDateKey = getSelectedDateKey(selectedDay);
        fetchDiaryPreview(selectedDateKey, dayView);
    }

    private void resetDayViewColor(TextView dayView) {

        if (dayView.getCurrentTextColor() == Color.parseColor("#60A637")) {
            dayView.setTextColor(Color.parseColor("#60A637"));
        }else {
            dayView.setTextColor(Color.BLACK);
        }
        dayView.setBackgroundResource(0);
    }

    private void fetchDiaryPreview(String dateKey, TextView dayView) {
        if (userId == null) {
            Log.e("NotificationsFragment", "User not logged in");
            return;
        }

        db.collection("groups")
                .whereEqualTo("ownerUserId", userId)
                .get()
                .addOnSuccessListener(groupQuerySnapshot -> {
                    if (!groupQuerySnapshot.isEmpty()) {
                        DocumentReference groupRef = groupQuerySnapshot.getDocuments().get(0).getReference();
                        getDiaryPreviewFromCollection(groupRef.collection("diaries"), dateKey, dayView, true);
                    } else {
                        db.collection("groups")
                                .whereEqualTo("invitedUserId", userId)
                                .get()
                                .addOnSuccessListener(inviteQuerySnapshot -> {
                                    if (!inviteQuerySnapshot.isEmpty()) {
                                        DocumentReference invitedGroupRef = inviteQuerySnapshot.getDocuments().get(0).getReference();
                                        getDiaryPreviewFromCollection(invitedGroupRef.collection("diaries"), dateKey, dayView, true);
                                    } else {
                                        DocumentReference userRef = db.collection("users").document(userId);
                                        getDiaryPreviewFromCollection(userRef.collection("diaries"), dateKey, dayView, true);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("NotificationsFragment", "Error checking invited groups", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("NotificationsFragment", "Error checking group ownership", e));
    }


    private void getDiaryPreviewFromCollection(CollectionReference diariesRef, String dateKey, TextView dayView, boolean setButtonAction) {
        diariesRef.whereEqualTo("date", dateKey)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot diaryDoc = querySnapshot.getDocuments().get(0);
                        String content = (String) diaryDoc.get("content");
                        String weather = (String) diaryDoc.get("weather");

                        if (content != null && !content.isEmpty()) {
                            String preview = content.split("\n")[0];
                            preview = preview.length() > 50 ? preview.substring(0, 50) + "..." : preview;
                            String previewText = "날씨: " + formatWeather(weather) + "\n" + preview;

                            // binding이 null이 아닌지 확인 후 diaryContentBox에 접근
                            if (binding != null) {
                                binding.diaryContentBox.setText(previewText);
                            } else {
                                Log.e("NotificationsFragment", "Binding is null, cannot set diary content preview.");
                            }

                            dayView.setTextColor(Color.parseColor("#60A637"));
                            dayView.setTypeface(dayView.getTypeface(), Typeface.BOLD);

                            // 버튼을 '수정'으로 변경하고 `DiaryFixFragment`로 이동하도록 설정
                            if (setButtonAction && binding != null) {
                                binding.button14.setText("수정");
                                binding.button14.setOnClickListener(v -> {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("selectedDateKey", dateKey);
                                    Navigation.findNavController(v).navigate(R.id.action_notificationsFragment_to_diaryFixFragment, bundle);
                                });
                            }
                        }
                    } else {
                        if (binding != null) {
                            binding.diaryContentBox.setText("작성된 일기가 없습니다.");
                            binding.button14.setText("일기작성");
                            binding.button14.setOnClickListener(v -> {
                                Bundle bundle = new Bundle();
                                bundle.putString("selectedDateKey", dateKey);
                                Navigation.findNavController(v).navigate(R.id.action_notificationsFragment_to_diaryWriteFragment, bundle);
                            });
                        } else {
                            Log.e("NotificationsFragment", "Binding is null, cannot set button action or diary content preview.");
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("NotificationsFragment", "Failed to fetch diary preview", e));
    }



    // 날씨 정보 형식을 한글로 변환하는 메서드
    private String formatWeather(String weather) {
        switch (weather) {
            case "sunny":
                return "맑음";
            case "cloud":
                return "흐림";
            case "rain":
                return "비";
            default:
                return "알 수 없음"; // 예기치 않은 값 처리
        }
    }


    private String getSelectedDateKey(int day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Calendar selectedCalendar = (Calendar) calendar.clone();
        selectedCalendar.set(Calendar.DAY_OF_MONTH, day);
        return dateFormat.format(selectedCalendar.getTime());
    }

    private void saveSelectedDateToPreferences() {
        String selectedDateKey = getSelectedDateKey(selectedDay);
        sharedPreferences.edit().putString("selected_date_key", selectedDateKey).apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
