package com.example.test2.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ThemeViewModel extends ViewModel {
    private final MutableLiveData<String> selectedTheme = new MutableLiveData<>();

    public void initTheme(String theme) {
        // 초기 테마를 설정합니다.
        selectedTheme.setValue(theme);
    }

    public void setTheme(String theme) {
        selectedTheme.setValue(theme);
    }

    public LiveData<String> getSelectedTheme() {
        return selectedTheme;
    }
}
