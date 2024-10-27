package com.example.test2.ui.dashboard;

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


public class ScheduleAddFragment extends Fragment {

    private String selectedDate;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String SAVED_TITLE = "savedTitle";
    private static final String SAVED_CONTENT = "savedContent";

    public ScheduleAddFragment() {}

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

        if (getArguments() != null) {
            selectedDate = getArguments().getString("selectedDate");
        }

        TextView dateTextView5 = view.findViewById(R.id.textView5);
        TextView dateTextView4 = view.findViewById(R.id.textView4);
        dateTextView5.setText(selectedDate);
        dateTextView4.setText(selectedDate);

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

        return view;
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
