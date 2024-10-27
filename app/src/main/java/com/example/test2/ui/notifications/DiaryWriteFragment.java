package com.example.test2.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.test2.R;

public class DiaryWriteFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.drawing_diary, container, false); // drawing_diary는 XML 파일의 이름

        // 뒤로가기 버튼을 설정
        ImageButton backButton = root.findViewById(R.id.imageButton64); // 뒤로가기 버튼의 ID
        backButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_diaryWriteFragment_to_NotificationsFragment));

        // DrawingView와 TextView 참조 가져오기
        DrawingView drawingView = root.findViewById(R.id.drawingView);
        TextView drawingHintText = root.findViewById(R.id.drawingHintText); // 텍스트 ID에 맞게 수정

        drawingView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 텍스트 숨기기
                drawingHintText.setVisibility(View.GONE);
            }
            // 드로잉 뷰에서 터치 이벤트를 처리하도록 전달
            return drawingView.onTouchEvent(event);
        });

        return root; // 추가된 부분
    }
}