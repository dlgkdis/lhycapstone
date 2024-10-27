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
import java.util.Locale;

public class DiaryWriteFragment extends Fragment {

    private EditText diaryEditText;
    private SharedPreferences sharedPreferences;
    private String selectedDateKey;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.drawing_diary, container, false);

        // SharedPreferences 초기화 및 선택한 날짜 키 가져오기
        sharedPreferences = requireContext().getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE);
        selectedDateKey = sharedPreferences.getString("selected_date_key", getTodayDateKey());

        // EditText 초기화
        diaryEditText = root.findViewById(R.id.diaryEditText);

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

        // SharedPreferences에 일기 저장
        sharedPreferences.edit().putString(selectedDateKey, diaryContent).apply();
    }

    // 오늘 날짜를 기본으로 사용하는 메서드
    private String getTodayDateKey() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        return "diary_content_" + dateFormat.format(calendar.getTime());
    }
}