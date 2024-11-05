// ObjectDeleteDialogFragment.java
package com.example.test2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ObjectDeleteDialogFragment extends DialogFragment {

    private OnObjectDeleteListener deleteListener;

    public interface OnObjectDeleteListener {
        void onObjectDeleted();
    }

    public static ObjectDeleteDialogFragment newInstance(OnObjectDeleteListener listener) {
        ObjectDeleteDialogFragment fragment = new ObjectDeleteDialogFragment();
        fragment.deleteListener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.object_delete, container, false);

        // "배치 삭제" 버튼 설정
        Button deleteButton = view.findViewById(R.id.button2);
        deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onObjectDeleted(); // MainFragment에 삭제 완료 알림
            }
            dismiss();
        });

        // "뒤로가기" 버튼 설정
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> dismiss());

        return view;
    }
}
