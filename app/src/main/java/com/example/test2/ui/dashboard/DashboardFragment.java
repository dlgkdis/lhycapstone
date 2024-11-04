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
import androidx.navigation.Navigation;
import com.example.test2.R;
import com.example.test2.databinding.FragmentDashboardBinding;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private Calendar calendar;
    private TextView monthText;
    private GridLayout calendarGrid;
    private TextView scheduleBox;
    private Button addScheduleButton;
    private TextView selectedDayView; // 이전에 선택된 날짜를 추적하기 위한 변수

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
        scheduleBox = root.findViewById(R.id.scheduleBox);
        addScheduleButton = root.findViewById(R.id.addScheduleButton);

        scheduleBox.setVisibility(View.GONE);
        addScheduleButton.setVisibility(View.GONE);  // 기본적으로 숨김

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

        addScheduleButton.setOnClickListener(v -> {
            String selectedDate = (String) addScheduleButton.getTag();  // 선택된 날짜 정보 가져오기
            Bundle bundle = new Bundle();
            bundle.putString("selectedDate", selectedDate);
            Navigation.findNavController(v).navigate(R.id.action_dashboard_to_scheduleAdd, bundle);
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

        Calendar today = Calendar.getInstance();

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
            dayView.setPadding(0, 32, 0, 32);

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

            if (tempCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    tempCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    currentDay == today.get(Calendar.DAY_OF_MONTH)) {

                dayView.setBackgroundResource(R.drawable.red_circle);
                dayView.setTextColor(Color.WHITE); // 흰색 텍스트 설정
                dayView.setTypeface(dayView.getTypeface(), Typeface.BOLD); // 텍스트를 굵게 설정
            }

            String fullDate = dayFormat.format(calendar.getTime()) + " " + currentDay + "일";
            String[] scheduleData = loadData(fullDate);

            if (!scheduleData[0].isEmpty()) {
                dayView.setTextColor(Color.parseColor("#60A637"));
            }

            dayView.setOnClickListener(v -> {
                // 선택한 날짜를 `selectedDate`로 설정 (현재 월과 선택한 일만 포함)
                Calendar selectedCalendar = (Calendar) tempCalendar.clone();
                selectedCalendar.set(Calendar.DAY_OF_MONTH, currentDay);
                String selectedDate = dayFormat.format(selectedCalendar.getTime());

                String[] loadedData = loadData(selectedDate);

                if (!loadedData[0].isEmpty()) {
                    String displayText = "제목: " + loadedData[0] + "\n내용: " + loadedData[1] +
                            "\n시작일: " + loadedData[2] + loadedData[3] + "\n종료일: " + loadedData[4] + loadedData[5];
                    scheduleBox.setText(displayText);
                    scheduleBox.setVisibility(View.VISIBLE);
                } else {
                    scheduleBox.setVisibility(View.GONE);
                }

                // 이전에 선택된 날짜가 있으면 기본 상태로 되돌림
                if (selectedDayView != null && selectedDayView != dayView) {
                    // 이전 선택 항목을 일반 상태로 되돌리기
                    Calendar prevSelectedCalendar = (Calendar) calendar.clone();
                    prevSelectedCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(selectedDayView.getText().toString()));

                    // 이전 선택 날짜가 현재 날짜인 경우 빨간색 동그라미로 되돌림
                    if (prevSelectedCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                            prevSelectedCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                            prevSelectedCalendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
                        selectedDayView.setBackgroundResource(R.drawable.red_circle); // 현재 날짜는 빨간 동그라미
                        selectedDayView.setTextColor(Color.WHITE);
                        selectedDayView.setTypeface(dayView.getTypeface(), Typeface.BOLD);
                    } else {
                        selectedDayView.setBackgroundResource(android.R.color.transparent); // 투명 배경
                        int previousDayOfWeek = prevSelectedCalendar.get(Calendar.DAY_OF_WEEK) - 1;
                        selectedDayView.setTextColor(previousDayOfWeek == 0 ? Color.RED : (previousDayOfWeek == 6 ? Color.BLUE : Color.BLACK)); // 요일에 따른 기본 텍스트 색상
                        selectedDayView.setTypeface(customFont, Typeface.NORMAL);
                    }
                }

                // 현재 선택된 날짜 스타일 적용
                dayView.setBackgroundResource(R.drawable.black_circle); // black_circle 배경 적용
                dayView.setTextColor(Color.WHITE); // 흰색 텍스트 설정
                dayView.setTypeface(dayView.getTypeface(), Typeface.BOLD); // 굵은 텍스트 설정

                // 선택된 날짜를 `selectedDayView`에 저장
                selectedDayView = dayView;

                // 플러스 버튼에 선택한 날짜 저장
                addScheduleButton.setVisibility(View.VISIBLE);
                addScheduleButton.setTag(selectedDate);
            });




            calendarGrid.addView(dayView);
        }
    }

    private String[] loadData(String date) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String title = sharedPreferences.getString(date + "_title", "");
        String content = sharedPreferences.getString(date + "_content", "");
        String startDay = sharedPreferences.getString(date + "_startDate", "");
        String startTime = sharedPreferences.getString(date + "_startTime", "");
        String endDay = sharedPreferences.getString(date + "_endDate", "");
        String endTime = sharedPreferences.getString(date + "_endTime", "");

        return new String[]{title, content, startDay, startTime, endDay, endTime};
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
