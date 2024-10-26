package com.example.test2.ui.dashboard;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.test2.R;
import com.example.test2.databinding.FragmentDashboardBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private Calendar calendar;
    private TextView monthText;
    private GridLayout calendarGrid;

    private Map<String, String> scheduleMap = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        // 캘린더 초기 설정
        calendar = Calendar.getInstance();
        monthText = root.findViewById(R.id.monthText);
        calendarGrid = root.findViewById(R.id.calendarGrid);

        updateCalendar();

        // 이전, 다음 버튼 설정
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

        return root;
    }


    private void updateCalendar() {
        // 월 표시 업데이트
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy년 MM월", Locale.getDefault());
        monthText.setText(monthFormat.format(calendar.getTime()));

        // 그리드 초기화
        calendarGrid.removeAllViews();

        // 현재 달의 첫째 날로 설정
        Calendar tempCalendar = (Calendar) calendar.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1; // 일요일 시작(0)

        // 요일에 맞춰 첫 번째 날짜가 시작되도록 빈 칸 추가
        for (int i = 0; i < firstDayOfWeek; i++) {
            TextView emptyView = new TextView(getContext());
            GridLayout.LayoutParams emptyParams = new GridLayout.LayoutParams();
            emptyParams.width = 0;
            emptyParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            emptyParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            emptyView.setLayoutParams(emptyParams);
            calendarGrid.addView(emptyView);
        }

        // 날짜 채우기
        for (int day = 1; day <= daysInMonth; day++) {
            final int currentDay = day;
            TextView dayView = new TextView(getContext());
            dayView.setText(String.valueOf(day));
            dayView.setGravity(Gravity.CENTER);
            dayView.setPadding(16, 16, 16, 16);

            // 날짜 셀 레이아웃 파라미터 설정
            GridLayout.LayoutParams dayParams = new GridLayout.LayoutParams();
            dayParams.width = 0;
            dayParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dayParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            dayView.setLayoutParams(dayParams);

            Typeface customFont = ResourcesCompat.getFont(getContext(), R.font.nanumfont);
            dayView.setTypeface(customFont);
            dayView.setTextSize(30);

            // 주말 색상 설정
            int dayOfWeek = (firstDayOfWeek + day - 1) % 7;
            if (dayOfWeek == 0) { // 일요일
                dayView.setTextColor(Color.RED);
            } else if (dayOfWeek == 6) { // 토요일
                dayView.setTextColor(Color.BLUE);
            } else {
                dayView.setTextColor(Color.BLACK);
            }

            // 날짜 클릭 이벤트 - ScheduleAddFragment로 전환
            dayView.setOnClickListener(v -> {
                String selectedDate = monthFormat.format(calendar.getTime()) + " " + currentDay + "일";
                Bundle bundle = new Bundle();
                bundle.putString("selectedDate", selectedDate);
                Navigation.findNavController(v).navigate(R.id.action_dashboard_to_scheduleAdd, bundle);
            });

            calendarGrid.addView(dayView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}