package com.example.test2.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.test2.R;

import java.util.Calendar;
import java.util.Locale;
import android.util.Log;
import com.example.test2.FirebaseHelper;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;

import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.NumberPicker;




public class ScheduleAddFragment extends Fragment {

    private String selectedDate;
    private static final String SHARED_PREFS = "sharedPrefs";
    private String[] hours = new String[24];

    public ScheduleAddFragment() {}

    public static ScheduleAddFragment newInstance(String date) {
        ScheduleAddFragment fragment = new ScheduleAddFragment();
        Bundle args = new Bundle();
        args.putString("selectedDate", date); // 선택한 날짜를 전달
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_add, container, false);

        for (int i = 0; i < 24; i++) {
            hours[i] = String.format(Locale.getDefault(), "%02d:00", i);
        }

        if (getArguments() != null) {
            selectedDate = getArguments().getString("selectedDate", "날짜 없음"); // 기본값 설정
        }

        TextView startDayTextView = view.findViewById(R.id.startday);
        TextView endDayTextView = view.findViewById(R.id.endday);
        startDayTextView.setText(selectedDate);
        endDayTextView.setText(selectedDate);

        EditText titleEditText = view.findViewById(R.id.titleEditText);
        EditText contentEditText = view.findViewById(R.id.contentEditText);

        TextView startTimeTextView = view.findViewById(R.id.startTime);
        TextView endTimeTextView = view.findViewById(R.id.endTime);

        startDayTextView.setOnClickListener(v -> showSpinnerDatePickerDialog(startDayTextView));
        endDayTextView.setOnClickListener(v -> showSpinnerDatePickerDialog(endDayTextView));

        startTimeTextView.setOnClickListener(v -> showHourSelectionDialog(startTimeTextView));
        endTimeTextView.setOnClickListener(v -> showHourSelectionDialog(endTimeTextView));

        Button saveButton = view.findViewById(R.id.savebutton);

        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String content = contentEditText.getText().toString();
            String startDay = startDayTextView.getText().toString();
            String endDay = endDayTextView.getText().toString();
            String startTime = startTimeTextView.getText().toString();
            String endTime = endTimeTextView.getText().toString();

            Timestamp startDate = convertToTimestamp(startDay);
            Timestamp endDate = convertToTimestamp(endDay);

            if (!title.isEmpty() && !content.isEmpty()) {
                // 데이터를 HashMap으로 저장
                Map<String, Object> scheduleData = new HashMap<>();
                scheduleData.put("title", title);
                scheduleData.put("content", content);
                scheduleData.put("startDay", startDay);
                scheduleData.put("endDay", endDay);
                scheduleData.put("startTime", startTime);
                scheduleData.put("endTime", endTime);

                scheduleData.put("start",startDate);
                scheduleData.put("end",endDate);

                // FirebaseHelper를 사용하여 Firebase에 저장
                FirebaseHelper firebaseHelper = new FirebaseHelper();
                firebaseHelper.addCalendarSchedule(scheduleData, success -> {
                    if (success) {
                        // 저장 성공 시 알림 추가
                        Map<String, Object> notificationData = new HashMap<>();
                        notificationData.put("message", "새로운 일정이 추가되었습니다: " + title);
                        notificationData.put("timestamp", System.currentTimeMillis());

                        firebaseHelper.addNotification(notificationData, notificationSuccess -> {
                            if (notificationSuccess) {
                                Log.d("ScheduleAddFragment", "Notification saved successfully");
                            } else {
                                Log.e("ScheduleAddFragment", "Failed to save notification");
                            }
                        });

                        if (getContext() != null) {
                            Toast.makeText(getContext(), "일정이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                        Navigation.findNavController(v).popBackStack();
                    } else {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "일정 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(getContext(), "제목과 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });


        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        return view;
    }

    private void showHourSelectionDialog(final TextView targetTextView) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        dialogBuilder.setTitle("시간 선택");
        dialogBuilder.setItems(hours, (dialog, which) -> targetTextView.setText(hours[which]));
        dialogBuilder.show();
    }

    private void showSpinnerDatePickerDialog(final TextView dateTextView) {
        // 현재 날짜 설정
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        // NumberPicker로 연도 선택기 설정
        NumberPicker yearPicker = new NumberPicker(requireContext());
        yearPicker.setMinValue(currentYear - 100);  // 예: 100년 전까지 선택 가능
        yearPicker.setMaxValue(currentYear + 50);   // 예: 50년 후까지 선택 가능
        yearPicker.setValue(currentYear);

        // NumberPicker로 월 선택기 설정
        NumberPicker monthPicker = new NumberPicker(requireContext());
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(currentMonth);

        // NumberPicker로 일 선택기 설정
        NumberPicker dayPicker = new NumberPicker(requireContext());
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // 해당 월의 마지막 날까지 선택 가능
        dayPicker.setValue(currentDay);

        // 월이 변경될 때 일 범위 업데이트
        monthPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            calendar.set(Calendar.MONTH, newVal - 1);
            int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            dayPicker.setMaxValue(maxDay);
        });

        // 레이아웃 설정
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(10, 10, 10, 10);
        layout.addView(yearPicker);
        layout.addView(monthPicker);
        layout.addView(dayPicker);

        // 다이얼로그 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(layout);
        builder.setTitle("날짜 선택");

        builder.setPositiveButton("확인", (dialog, which) -> {
            // 선택된 연도, 월, 일을 문자열로 포맷하여 TextView에 설정
            String selectedDate = String.format(Locale.getDefault(), "%d년 %02d월 %02d일",
                    yearPicker.getValue(),
                    monthPicker.getValue(),
                    dayPicker.getValue());
            dateTextView.setText(selectedDate);
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }



    public static Timestamp convertToTimestamp(String dateString) {
        try {
            // SimpleDateFormat으로 날짜 형식 지정
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());

            // 문자열을 Date로 파싱
            Date date = dateFormat.parse(dateString);

            // Date를 Timestamp로 변환하여 반환
            return new Timestamp(date);

        } catch (Exception e) {
            e.printStackTrace();
            return null; // 예외 발생 시 null 반환
        }
    }

}
