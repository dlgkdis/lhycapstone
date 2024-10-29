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
import android.widget.Toast;
import android.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.test2.R;
import java.util.Calendar;
import java.util.Locale;

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

        startDayTextView.setOnClickListener(v -> showDatePickerDialog(startDayTextView));
        endDayTextView.setOnClickListener(v -> showDatePickerDialog(endDayTextView));

        EditText titleEditText = view.findViewById(R.id.titleEditText);
        EditText contentEditText = view.findViewById(R.id.contentEditText);
        loadData(titleEditText, contentEditText); // 저장된 데이터 불러오기

        Button saveButton = view.findViewById(R.id.button4);
        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String content = contentEditText.getText().toString();

            if (!title.isEmpty() && !content.isEmpty()) {
                saveData(title, content);
                Toast.makeText(getContext(), "일정이 저장되었습니다", Toast.LENGTH_SHORT).show();

                // 일정이 저장되면 DashboardFragment로 돌아가기
                Navigation.findNavController(v).popBackStack();
            } else {
                Toast.makeText(getContext(), "제목과 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        TextView startTimeTextView = view.findViewById(R.id.textView6);
        TextView endTimeTextView = view.findViewById(R.id.textView7);
        startTimeTextView.setOnClickListener(v -> showHourSelectionDialog(startTimeTextView));
        endTimeTextView.setOnClickListener(v -> showHourSelectionDialog(endTimeTextView));

        return view;
    }

    private void showDatePickerDialog(final TextView dateTextView) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%d년 %d월 %d일", year, month + 1, dayOfMonth);
                    dateTextView.setText(selectedDate);
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
        dialogBuilder.setItems(hours, (dialog, which) -> targetTextView.setText(hours[which]));
        dialogBuilder.show();
    }

    private void saveData(String title, String content) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(selectedDate + "_title", title); // 날짜별로 저장
        editor.putString(selectedDate + "_content", content);
        editor.apply();
    }

    private void loadData(EditText titleEditText, EditText contentEditText) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String savedTitle = sharedPreferences.getString(selectedDate + "_title", "");
        String savedContent = sharedPreferences.getString(selectedDate + "_content", "");
        titleEditText.setText(savedTitle);
        contentEditText.setText(savedContent);
    }
}