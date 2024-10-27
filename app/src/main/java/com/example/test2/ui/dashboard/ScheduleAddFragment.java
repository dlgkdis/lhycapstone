package com.example.test2.ui.dashboard;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.test2.R;
import android.widget.Toast;
import android.app.AlertDialog;
import java.util.Calendar;
import java.util.Locale;
import android.widget.ArrayAdapter;
import android.graphics.Typeface;
import android.util.Log;

public class ScheduleAddFragment extends Fragment {

    private String selectedDate;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String SAVED_TITLE = "savedTitle";
    private static final String SAVED_CONTENT = "savedContent";
    private String[] hours = new String[24];

    public ScheduleAddFragment() {}

    public static ScheduleAddFragment newInstance(String date) {
        ScheduleAddFragment fragment = new ScheduleAddFragment();
        Bundle args = new Bundle();
        args.putString("selectedDate", date);// 선택한 날짜를 전달
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_add, container, false);

        // 24시간 목록을 초기화
        for (int i = 0; i < 24; i++) {
            hours[i] = String.format(Locale.getDefault(), "%02d:00", i);
        }

        // 선택한 날짜를 받아옴
        if (getArguments() != null) {
            selectedDate = getArguments().getString("selectedDate", "날짜 없음"); // 기본값 설정
        }

        // 날짜를 startday와 endday TextView에 설정
        TextView startDayTextView = view.findViewById(R.id.startday);
        TextView endDayTextView = view.findViewById(R.id.endday);
        startDayTextView.setText(selectedDate);
        endDayTextView.setText(selectedDate);

        // startday와 endday 클릭 리스너 설정
        startDayTextView.setOnClickListener(v -> showDatePickerDialog(startDayTextView));
        endDayTextView.setOnClickListener(v -> showDatePickerDialog(endDayTextView));

        // 나머지 코드 (EditText와 Button 설정 등)
        EditText titleEditText = view.findViewById(R.id.titleEditText);
        EditText contentEditText = view.findViewById(R.id.contentEditText);
        loadData(titleEditText, contentEditText);

        Button saveButton = view.findViewById(R.id.button4);
        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String content = contentEditText.getText().toString();

            if (!title.isEmpty() && !content.isEmpty()) {
                saveData(title, content);

                // "일정이 저장되었습니다" 다이얼로그 표시
                CalendarEndDialogFragment dialog = new CalendarEndDialogFragment();
                dialog.show(getParentFragmentManager(), "CalendarEndDialog");

                // 프래그먼트 종료
                Navigation.findNavController(v).popBackStack();
            } else {
                Toast.makeText(getContext(), "제목과 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 뒤로 가기 버튼 설정
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        // 시간 선택 TextView 찾기 및 클릭 리스너 설정
        TextView startTimeTextView = view.findViewById(R.id.textView6);
        TextView endTimeTextView = view.findViewById(R.id.textView7);

        startTimeTextView.setOnClickListener(v -> showHourSelectionDialog(startTimeTextView));
        endTimeTextView.setOnClickListener(v -> showHourSelectionDialog(endTimeTextView));

        return view;
    }

    // 날짜 선택 다이얼로그 표시 메서드
    private void showDatePickerDialog(final TextView dateTextView) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), R.style.CustomDatePickerDialog,
                (view, year, month, dayOfMonth) -> {
                    // 선택된 날짜를 설정하는 부분
                    String selectedDate = String.format(Locale.getDefault(), "%d년 %d월 %d일", year, month + 1, dayOfMonth);
                    dateTextView.setText(selectedDate);

                    // 로그 추가하여 선택된 날짜 확인
                    Log.d("ScheduleAddFragment", "Selected date: " + selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void showHourSelectionDialog(final TextView targetTextView) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        dialogBuilder.setTitle("시간 선택");

        dialogBuilder.setItems(hours, (dialog, which) -> {
            // 선택된 시간을 해당 TextView에 설정
            targetTextView.setText(hours[which]);
        });

        dialogBuilder.show();
    }

    private void saveData(String title, String content) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SAVED_TITLE, title);
        editor.putString(SAVED_CONTENT, content);
        editor.apply();
    }

    private void loadData(EditText titleEditText, EditText contentEditText) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String savedTitle = sharedPreferences.getString(SAVED_TITLE, "");
        String savedContent = sharedPreferences.getString(SAVED_CONTENT, "");
        titleEditText.setText(savedTitle);
        contentEditText.setText(savedContent);
    }
}
