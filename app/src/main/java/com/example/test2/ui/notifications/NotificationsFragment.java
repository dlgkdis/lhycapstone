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
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
public class NotificationsFragment extends Fragment {

    private FragmentDiaryBinding binding;
    private Calendar calendar;
    private TextView monthText;
    private GridLayout calendarGrid;
    private SharedPreferences sharedPreferences;
    private TextView selectedDayView;
    private int selectedDay;
    private LinearLayout diaryContentContainer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDiaryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.diaryContentContainer.setVisibility(View.GONE);

        sharedPreferences = requireContext().getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE);
        diaryContentContainer = binding.diaryContentContainer;

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
            dayView.setTextColor(Color.BLACK);

            dayView.setOnClickListener(v -> onDaySelected(dayView, currentDay));

            // 날짜에 따라 텍스트 색상 설정
            String dateKey = getSelectedDateKey(currentDay);
            if (isDiaryWritten(dateKey)) {
                // 일기 작성된 날짜는 초록색
                dayView.setTextColor(Color.parseColor("#60A637"));
            } else {
                // 일기 없는 날짜는 기본 검은색
                int dayOfWeek = (firstDayOfWeek + day - 1) % 7;
                if (dayOfWeek == 0) {
                    dayView.setTextColor(Color.RED);
                } else if (dayOfWeek == 6) {
                    dayView.setTextColor(Color.BLUE);
                } else {
                    dayView.setTextColor(Color.BLACK);
                }
            }

            calendarGrid.addView(dayView, new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f)
            ));
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private boolean isDiaryWritten(String selectedDateKey) {
        // 선택된 날짜의 일기 내용을 가져옵니다.
        Set<String> diarySet = sharedPreferences.getStringSet(selectedDateKey, new HashSet<>());
        return diarySet != null && !diarySet.isEmpty();
    }

    private void onDaySelected(TextView dayView, int day) {
        if (selectedDayView != null) selectedDayView.setTextColor(Color.BLACK);
        dayView.setTextColor(Color.RED);
        selectedDayView = dayView;
        selectedDay = day;

        String selectedDateKey = getSelectedDateKey(selectedDay);
        Set<String> diaryEntriesSet = sharedPreferences.getStringSet(selectedDateKey, new HashSet<>());
        List<String> diaryEntries = new ArrayList<>(diaryEntriesSet);

        displayDiaryEntries(diaryEntries);
    }

    private void displayDiaryEntries(List<String> diaryEntries) {
        if (diaryEntries != null && !diaryEntries.isEmpty()) {
            binding.diaryContentBox.setText(diaryEntries.get(diaryEntries.size() - 1));
        } else {
            binding.diaryContentBox.setText("작성된 일기가 없습니다."); // 일기가 없는 경우 표시할 텍스트
        }

        // 일기 두 개 이상일 경우만 diaryContentContainer 보이도록 설정
        diaryContentContainer.removeAllViews();
        if (diaryEntries.size() > 1) {
            for (int i = 0; i < diaryEntries.size() - 1; i++) {
                TextView diaryView = new TextView(getContext());
                diaryView.setText(diaryEntries.get(i));
                diaryView.setBackgroundResource(R.drawable.box_background);
                diaryView.setPadding(16, 16, 16, 16);
                diaryView.setTextSize(25);
                diaryView.setTextColor(Color.BLACK);
                diaryView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.nanumfont), Typeface.BOLD);
                diaryView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);

                diaryContentContainer.addView(diaryView);
            }
            diaryContentContainer.setVisibility(View.VISIBLE);
        } else {
            diaryContentContainer.setVisibility(View.GONE);
        }
    }



    private String getSelectedDateKey(int day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Calendar selectedCalendar = (Calendar) calendar.clone();
        selectedCalendar.set(Calendar.DAY_OF_MONTH, day);
        return "diary_content_" + dateFormat.format(selectedCalendar.getTime());
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