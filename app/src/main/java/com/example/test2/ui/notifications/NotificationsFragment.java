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

public class NotificationsFragment extends Fragment {

    private FragmentDiaryBinding binding;
    private Calendar calendar;
    private TextView monthText;
    private GridLayout calendarGrid;
    private SharedPreferences sharedPreferences;
    private TextView selectedDayView;
    private int selectedDay;  // 사용자가 선택한 날짜 저장

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDiaryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferences = requireContext().getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE);

        binding.button14.setOnClickListener(v -> {
            saveSelectedDateToPreferences();  // 일기 작성 화면으로 날짜 전달
            Navigation.findNavController(v).navigate(R.id.action_notificationsFragment_to_diaryWriteFragment);
        });

        calendar = Calendar.getInstance();
        monthText = binding.monthText;
        calendarGrid = binding.calendarGrid;

        Typeface customFont = ResourcesCompat.getFont(getContext(), R.font.nanumfont);
        monthText.setTypeface(customFont, Typeface.BOLD);
        monthText.setTextSize(30);

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

        // 요일 맞추기 위한 빈칸 추가
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
            dayView.setPadding(8, 8, 8, 8);
            dayView.setTypeface(customFont);

            // 모든 날짜 텍스트 색상 블랙으로 설정
            dayView.setTextColor(Color.BLACK);

            dayView.setOnClickListener(v -> onDaySelected(dayView, currentDay));

            calendarGrid.addView(dayView, new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f)
            ));
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

    }

    private void onDaySelected(TextView dayView, int day) {
        if (selectedDayView != null) selectedDayView.setTextColor(Color.BLACK);
        dayView.setTextColor(Color.RED);
        selectedDayView = dayView;
        selectedDay = day;

        String selectedDateKey = getSelectedDateKey(day);
        displayDiaryContent(selectedDateKey);
    }

    private String getSelectedDateKey(int day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Calendar selectedCalendar = (Calendar) calendar.clone();
        selectedCalendar.set(Calendar.DAY_OF_MONTH, day);
        return "diary_content_" + dateFormat.format(selectedCalendar.getTime());
    }

    private void displayDiaryContent(String dateKey) {
        String diaryContent = sharedPreferences.getString(dateKey, "작성된 일기가 없습니다.");
        binding.diaryContentBox.setText(diaryContent);
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