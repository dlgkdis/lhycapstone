package com.example.test2.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.test2.R;

public class ScheduleAddFragment extends Fragment {

    private String selectedDate;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String SAVED_TITLE = "savedTitle";
    private static final String SAVED_CONTENT = "savedContent";

    // 매개변수가 없는 기본 생성자
    public ScheduleAddFragment() {}

    // 날짜를 전달하는 newInstance 메서드
    public static ScheduleAddFragment newInstance(String date) {
        ScheduleAddFragment fragment = new ScheduleAddFragment();
        Bundle args = new Bundle();
        args.putString("selectedDate", date);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_add, container, false);

        // 전달된 날짜 값 받기
        if (getArguments() != null) {
            selectedDate = getArguments().getString("selectedDate");
        }

        // 날짜 표시
        TextView dateTextView5 = view.findViewById(R.id.textView5);
        TextView dateTextView4 = view.findViewById(R.id.textView4);
        dateTextView5.setText(selectedDate);
        dateTextView4.setText(selectedDate);

        // 제목과 내용 입력
        EditText titleEditText = view.findViewById(R.id.titleEditText);
        EditText contentEditText = view.findViewById(R.id.contentEditText);

        // 저장된 일정 데이터 불러오기
        loadData(titleEditText, contentEditText);

        // 확인 버튼 설정
        Button saveButton = view.findViewById(R.id.button4);
        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String content = contentEditText.getText().toString();

            // 제목과 내용을 입력했을 때만 저장
            if (!title.isEmpty() && !content.isEmpty()) {
                saveData(title, content);
                Toast.makeText(getContext(), "일정이 저장되었습니다!", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack(); // 프래그먼트 종료
            } else {
                Toast.makeText(getContext(), "제목과 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // SharedPreferences에 제목과 내용 저장
    private void saveData(String title, String content) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SAVED_TITLE, title);
        editor.putString(SAVED_CONTENT, content);
        editor.apply();
    }

    // SharedPreferences에서 제목과 내용 불러오기
    private void loadData(EditText titleEditText, EditText contentEditText) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String savedTitle = sharedPreferences.getString(SAVED_TITLE, "");
        String savedContent = sharedPreferences.getString(SAVED_CONTENT, "");
        titleEditText.setText(savedTitle);
        contentEditText.setText(savedContent);
    }
}