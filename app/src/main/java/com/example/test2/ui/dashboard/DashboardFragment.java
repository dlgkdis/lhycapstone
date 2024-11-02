package com.example.test2.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.test2.R;
import com.example.test2.databinding.FragmentDashboardBinding;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import androidx.navigation.Navigation;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private Calendar calendar;
    private TextView monthText;
    private GridLayout calendarGrid;
    private TextView scheduleBox; // 일정 표시 네모 박스

    private static final String SHARED_PREFS = "sharedPrefs";
    private SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy년 MM월", Locale.getDefault());
    private SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        calendar = Calendar.getInstance();
        monthText = root.findViewById(R.id.monthText);
        calendarGrid = root.findViewById(R.id.calendarGrid);
        scheduleBox = root.findViewById(R.id.scheduleBox); // 네모 박스

        scheduleBox.setVisibility(View.GONE); // 초기에는 숨김

        Button prevMonthButton = root.findViewById(R.id.prevMonthButton);
        Button nextMonthButton = root.findViewById(R.id.nextMonthButton);

        prevMonthButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        nextMonthButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        updateCalendar();

        return root;
    }

    private void updateCalendar() {
        monthText.setText(monthFormat.format(calendar.getTime()));
        calendarGrid.removeAllViews();

        Calendar tempCalendar = (Calendar) calendar.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1;

        Calendar today = Calendar.getInstance(); // 현재 날짜 가져오기

        for (int i = 0; i < firstDayOfWeek; i++) {
            TextView emptyView = new TextView(getContext());
            GridLayout.LayoutParams emptyParams = new GridLayout.LayoutParams();
            emptyParams.width = 0;
            emptyParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            emptyParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            emptyView.setLayoutParams(emptyParams);
            calendarGrid.addView(emptyView);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            final int currentDay = day;
            TextView dayView = new TextView(getContext());
            dayView.setText(String.valueOf(day));
            dayView.setGravity(Gravity.CENTER);
            dayView.setPadding(16, 16, 16, 16);

            GridLayout.LayoutParams dayParams = new GridLayout.LayoutParams();
            dayParams.width = 0;
            dayParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dayParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            dayView.setLayoutParams(dayParams);

            Typeface customFont = ResourcesCompat.getFont(getContext(), R.font.nanumfont);
            dayView.setTypeface(customFont);
            dayView.setTextSize(30);

            int dayOfWeek = (firstDayOfWeek + day - 1) % 7;
            if (dayOfWeek == 0) {
                dayView.setTextColor(Color.RED);
            } else if (dayOfWeek == 6) {
                dayView.setTextColor(Color.BLUE);
            } else {
                dayView.setTextColor(Color.BLACK);
            }

            // 현재 날짜에 빨간 동그라미 표시 추가
            if (tempCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    tempCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    currentDay == today.get(Calendar.DAY_OF_MONTH)) {
                dayView.setBackgroundResource(R.drawable.red_circle); // 빨간색 테두리 원 배경 설정
            }

            String fullDate = dayFormat.format(calendar.getTime()) + " " + currentDay + "일";
            String savedTitle = loadData(fullDate);
            if (savedTitle != null) {
                dayView.setTextColor(Color.parseColor("#60A637"));  // 일정이 있는 날짜는 녹색으로 표시
            }

            dayView.setOnClickListener(v -> {
                String selectedDate = dayFormat.format(calendar.getTime()) + " " + currentDay + "일";
                String schedule = loadData(selectedDate);

                if (schedule != null) {
                    scheduleBox.setText("일정: " + schedule); // 등록한 일정 표시
                    scheduleBox.setVisibility(View.VISIBLE);
                } else {
                    scheduleBox.setVisibility(View.GONE);
                    Bundle bundle = new Bundle();
                    bundle.putString("selectedDate", selectedDate);
                    Navigation.findNavController(v).navigate(R.id.action_dashboard_to_scheduleAdd, bundle);
                }
            });

            calendarGrid.addView(dayView);
        }
    }

    private String loadData(String date) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(date + "_title", null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}