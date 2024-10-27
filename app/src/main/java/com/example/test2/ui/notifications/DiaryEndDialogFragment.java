package com.example.test2.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.test2.R;

public class DiaryEndDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 두 번째 XML 파일을 inflate하여 다이얼로그의 뷰로 설정
        return inflater.inflate(R.layout.diary_end, container, false); // diary_submit은 두 번째 XML 파일의 이름
    }

    @Override
    public void onStart() {
        super.onStart();
        // 다이얼로그 크기 조정
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}