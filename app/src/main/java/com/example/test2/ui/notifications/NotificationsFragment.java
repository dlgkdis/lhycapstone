package com.example.test2.ui.notifications;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.navigation.Navigation;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.test2.R;
import com.example.test2.databinding.FragmentDiaryBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.graphics.Typeface;
import androidx.core.content.res.ResourcesCompat;
public class NotificationsFragment extends Fragment {

    private FragmentDiaryBinding binding;
    private Calendar calendar;
    private TextView monthText;
    private GridLayout calendarGrid;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDiaryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 일기작성 버튼 클릭 리스너 설정
        binding.button14.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_notificationsFragment_to_diaryWriteFragment)
        );


        calendar = Calendar.getInstance();
        monthText = binding.monthText;
        calendarGrid = binding.calendarGrid;

        updateMonthText();  // 현재 월 표시
        updateCalendar();   // 캘린더 업데이트

        // 이전 달로 이동 버튼 클릭 리스너
        binding.prevMonthButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateMonthText();
            updateCalendar();
        });

        // 다음 달로 이동 버튼 클릭 리스너
        binding.nextMonthButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateMonthText();
            updateCalendar();
        });

        return root;
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

        // 날짜 채우기
        for (int day = 1; day <= daysInMonth; day++) {
            TextView dayView = new TextView(getContext());
            dayView.setText(String.valueOf(day));
            dayView.setGravity(Gravity.CENTER);
            dayView.setTextSize(30);
            dayView.setPadding(8, 8, 8, 8);

            dayView.setTypeface(customFont);

            // 주말 색상 설정
            int dayOfWeek = (firstDayOfWeek + day - 1) % 7;
            if (dayOfWeek == 0) { // 일요일
                dayView.setTextColor(Color.RED);
            } else if (dayOfWeek == 6) { // 토요일
                dayView.setTextColor(Color.BLUE);
            } else {
                dayView.setTextColor(Color.BLACK);
            }

            calendarGrid.addView(dayView, new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f)
            ));
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}