package com.example.test2.ui.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class DiaryWriteFragment extends Fragment {

    private EditText diaryEditText;
    private SharedPreferences sharedPreferences;
    private String selectedDateKey;
    private TextView textView8; // 날짜 표시 TextView

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.drawing_diary, container, false);

        // SharedPreferences 초기화
        sharedPreferences = requireContext().getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE);

        // 인자로 전달된 selectedDateKey 받기
        if (getArguments() != null) {
            selectedDateKey = getArguments().getString("selectedDateKey", getTodayDateKey());
        } else {
            selectedDateKey = getTodayDateKey();
        }

        // EditText 초기화
        diaryEditText = root.findViewById(R.id.diaryEditText);

        // 날짜 표시 TextView 초기화
        textView8 = root.findViewById(R.id.textView8);
        updateDateTextView(); // 선택된 날짜를 TextView에 설정

        // 등록 버튼 클릭 리스너 설정
        Button registerButton = root.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDiaryContent();

                // DiarySubmitDialogFragment 팝업 표시
                DiaryEndDialogFragment dialog = new DiaryEndDialogFragment();
                dialog.show(getParentFragmentManager(), "DiaryEndDialogFragment");

                // 작성 후 이전 화면으로 돌아가기
                Navigation.findNavController(v).navigate(R.id.action_diaryWriteFragment_to_NotificationsFragment);
            }
        });

        // 뒤로가기 버튼 설정
        ImageButton backButton = root.findViewById(R.id.imageButton64);
        backButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_diaryWriteFragment_to_NotificationsFragment));

        // DrawingView와 TextView 참조 가져오기
        DrawingView drawingView = root.findViewById(R.id.drawingView);
        TextView drawingHintText = root.findViewById(R.id.drawingHintText);

        drawingView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                drawingHintText.setVisibility(View.GONE);
            }
            return drawingView.onTouchEvent(event);
        });

        // 버튼 및 빨간색 동그라미 설정
        ImageButton button1 = root.findViewById(R.id.imageButton3);
        ImageButton button2 = root.findViewById(R.id.imageButton);
        ImageButton button3 = root.findViewById(R.id.imageButton2);

        View redDot1 = root.findViewById(R.id.redDot3);
        View redDot2 = root.findViewById(R.id.redDot);
        View redDot3 = root.findViewById(R.id.redDot2);

        button1.setOnClickListener(v -> {
            redDot1.setVisibility(View.VISIBLE);
            redDot2.setVisibility(View.INVISIBLE);
            redDot3.setVisibility(View.INVISIBLE);
        });

        button2.setOnClickListener(v -> {
            redDot1.setVisibility(View.INVISIBLE);
            redDot2.setVisibility(View.VISIBLE);
            redDot3.setVisibility(View.INVISIBLE);
        });

        button3.setOnClickListener(v -> {
            redDot1.setVisibility(View.INVISIBLE);
            redDot2.setVisibility(View.INVISIBLE);
            redDot3.setVisibility(View.VISIBLE);
        });

        return root;
    }

    private void saveDiaryContent() {
        String diaryContent = diaryEditText.getText().toString();
        Set<String> diaryEntries = sharedPreferences.getStringSet(selectedDateKey, new HashSet<>());
        diaryEntries.add(diaryContent);
        sharedPreferences.edit().putStringSet(selectedDateKey, diaryEntries).apply();
    }

    // 오늘 날짜를 기본으로 사용하는 메서드
    private String getTodayDateKey() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        return "diary_content_" + dateFormat.format(calendar.getTime());
    }

    // 선택된 날짜를 textView8에 표시하는 메서드
    private void updateDateTextView() {
        SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            // 날짜 키에서 날짜를 파싱하여 표시
            calendar.setTime(new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).parse(selectedDateKey.replace("diary_content_", "")));
        } catch (Exception e) {
            calendar = Calendar.getInstance();  // 파싱 실패 시 오늘 날짜 사용
        }
        String displayDate = "<" + displayFormat.format(calendar.getTime()) + ">";
        textView8.setText(displayDate);
    }
}