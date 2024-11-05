package com.example.test2.ui.dashboard;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.test2.R;
import com.example.test2.FirebaseHelper;
import com.example.test2.databinding.FragmentDashboardBinding;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import android.app.AlertDialog;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;



public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private Calendar calendar;
    private TextView monthText;
    private GridLayout calendarGrid;
    private LinearLayout scheduleBox;
    private Button addScheduleButton;
    private TextView selectedDayView;
    private Typeface nanumFont;

    private SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy년 MM월", Locale.getDefault());
    private SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());
    private FirebaseHelper firebaseHelper;

    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebaseHelper = new FirebaseHelper();
        calendar = Calendar.getInstance();
        monthText = root.findViewById(R.id.monthText);
        calendarGrid = root.findViewById(R.id.calendarGrid);
        scheduleBox = root.findViewById(R.id.scheduleBox);
        addScheduleButton = root.findViewById(R.id.addScheduleButton);
        nanumFont = ResourcesCompat.getFont(requireContext(), R.font.nanumfont);

        // 추가적인 초기화 코드들
        scheduleBox.setVisibility(View.GONE);
        addScheduleButton.setVisibility(View.GONE);

        Button prevMonthButton = root.findViewById(R.id.prevMonthButton);
        Button nextMonthButton = root.findViewById(R.id.nextMonthButton);

        prevMonthButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        nextMonthButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        addScheduleButton.setOnClickListener(v -> {
            String selectedDate = (String) addScheduleButton.getTag();
            Bundle bundle = new Bundle();
            bundle.putString("selectedDate", selectedDate);
            Navigation.findNavController(v).navigate(R.id.action_dashboard_to_scheduleAdd, bundle);
        });

        updateCalendar();
        return root;
    }

    private void updateCalendar() {
        monthText.setText(monthFormat.format(calendar.getTime()));
        calendarGrid.removeAllViews();

        Calendar tempCalendar = (Calendar) calendar.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        Calendar today = Calendar.getInstance();

        for (int i = 0; i < firstDayOfWeek; i++) {
            TextView emptyView = new TextView(getContext());
            GridLayout.LayoutParams emptyParams = new GridLayout.LayoutParams();
            emptyParams.width = 0;
            emptyParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            emptyParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            emptyView.setLayoutParams(emptyParams);
            calendarGrid.addView(emptyView);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            final int currentDay = day;
            TextView dayView = new TextView(getContext());
            dayView.setText(String.valueOf(day));
            dayView.setGravity(Gravity.CENTER);
            dayView.setPadding(0, 32, 0, 32);

            GridLayout.LayoutParams dayParams = new GridLayout.LayoutParams();
            dayParams.width = 0;
            dayParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dayParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            dayView.setLayoutParams(dayParams);

            Typeface customFont = ResourcesCompat.getFont(getContext(), R.font.nanumfont);
            dayView.setTypeface(customFont);
            dayView.setTextSize(30);

            int dayOfWeek = (firstDayOfWeek + day - 1) % 7;
            if (dayOfWeek == 0) {
                dayView.setTextColor(Color.RED);
            } else if (dayOfWeek == 6) {
                dayView.setTextColor(Color.BLUE);
            } else {
                dayView.setTextColor(Color.BLACK);
            }

            if (tempCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    tempCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    currentDay == today.get(Calendar.DAY_OF_MONTH)) {
                dayView.setBackgroundResource(R.drawable.red_circle);
                dayView.setTextColor(Color.WHITE);
                dayView.setTypeface(dayView.getTypeface(), Typeface.BOLD);
            }

            Calendar selectedCalendar = (Calendar) tempCalendar.clone();
            selectedCalendar.set(Calendar.DAY_OF_MONTH, currentDay);
            String selectedDate = dayFormat.format(selectedCalendar.getTime());

            dayView.setOnClickListener(v -> {
                loadData(selectedDate);

                if (selectedDayView != null && selectedDayView != dayView) {
                    Calendar prevSelectedCalendar = (Calendar) calendar.clone();
                    prevSelectedCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(selectedDayView.getText().toString()));

                    if (prevSelectedCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                            prevSelectedCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                            prevSelectedCalendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
                        selectedDayView.setBackgroundResource(R.drawable.red_circle);
                        selectedDayView.setTextColor(Color.WHITE);
                        selectedDayView.setTypeface(dayView.getTypeface(), Typeface.BOLD);
                    } else {
                        selectedDayView.setBackgroundResource(android.R.color.transparent);
                        int previousDayOfWeek = prevSelectedCalendar.get(Calendar.DAY_OF_WEEK) - 1;
                        selectedDayView.setTextColor(previousDayOfWeek == 0 ? Color.RED : (previousDayOfWeek == 6 ? Color.BLUE : Color.BLACK));
                        selectedDayView.setTypeface(customFont, Typeface.NORMAL);
                    }
                }

                dayView.setBackgroundResource(R.drawable.black_circle);
                dayView.setTextColor(Color.WHITE);
                dayView.setTypeface(dayView.getTypeface(), Typeface.BOLD);

                selectedDayView = dayView;
                addScheduleButton.setVisibility(View.VISIBLE);
                addScheduleButton.setTag(selectedDate);
            });

            calendarGrid.addView(dayView);
        }
    }

    private void loadData(String selectedDate) {
        firebaseHelper.listenToScheduleUpdates(selectedDate, scheduleList -> {
            scheduleBox.removeAllViews(); // 기존 일정 목록을 제거합니다.

            if (scheduleList != null && !scheduleList.isEmpty()) {
                for (Map<String, Object> scheduleData : scheduleList) {
                    TextView titleTextView = new TextView(getContext());
                    String title = (String) scheduleData.get("title");

                    titleTextView.setText(title);
                    titleTextView.setTextSize(16);
                    titleTextView.setGravity(Gravity.LEFT);
                    titleTextView.setPadding(16, 16, 16, 16);
                    titleTextView.setTextColor(Color.BLACK);
                    titleTextView.setBackgroundResource(R.drawable.schedule_background);

                    // 폰트 적용
                    if (nanumFont != null) {
                        titleTextView.setTypeface(nanumFont);
                    }

                    // LayoutParams를 통해 마진 추가
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(0, 8, 0, 8);
                    titleTextView.setLayoutParams(layoutParams);

                    // 클릭 시 세부 정보 팝업 창을 띄움
                    titleTextView.setOnClickListener(v -> showDetailDialog(scheduleData));

                    scheduleBox.addView(titleTextView); // `scheduleBox`에 제목 추가
                }
                scheduleBox.setVisibility(View.VISIBLE); // 일정이 있으면 `scheduleBox`를 표시
            } else {
                // 일정이 없을 때 비어 있다는 메시지를 표시하도록 설정합니다.
                TextView emptyTextView = new TextView(getContext());
                emptyTextView.setText("일정이 없습니다.");
                emptyTextView.setTextSize(16);
                emptyTextView.setGravity(Gravity.CENTER);
                emptyTextView.setTextColor(Color.GRAY);
                scheduleBox.addView(emptyTextView);
                scheduleBox.setVisibility(View.VISIBLE);
            }
        }, error -> {
            // 오류 발생 시 사용자에게 알림
            Toast.makeText(getContext(), "데이터를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        });
    }



    private void showDetailDialog(Map<String, Object> scheduleData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // 팝업창 커스텀 레이아웃 설정
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View popupView = inflater.inflate(R.layout.schedule_popup, null);

        TextView popupTitle = popupView.findViewById(R.id.popupTitle);
        TextView popupContent = popupView.findViewById(R.id.popupContent);

        popupTitle.setText("제목: " + scheduleData.get("title"));
        popupContent.setText("내용: " + scheduleData.get("content") + "\n\n시작일: " + scheduleData.get("startDay") + " " + scheduleData.get("startTime") + "\n종료일: " + scheduleData.get("endDay") + " " + scheduleData.get("endTime"));

        builder.setView(popupView);

        // 삭제 버튼 추가
        builder.setPositiveButton("삭제", (dialog, which) -> {
            // 삭제 확인 후 Firebase에서 일정 삭제
            String documentId = (String) scheduleData.get("documentId"); // 이 ID가 필요함
            if (documentId != null) {
                FirebaseHelper firebaseHelper = new FirebaseHelper();
                firebaseHelper.deleteCalendarSchedule(documentId, success -> {
                    if (success) {
                        // 일정 삭제 성공 후 알림 메시지 데이터 구성
                        Map<String, Object> notificationData = new HashMap<>();
                        notificationData.put("message", "일정이 삭제되었습니다: " + scheduleData.get("title"));
                        notificationData.put("timestamp", System.currentTimeMillis());

                        // FirebaseHelper의 addNotification 메서드를 호출하여 알림 추가
                        firebaseHelper.addNotification(notificationData, notificationSuccess -> {
                            if (notificationSuccess) {
                                Toast.makeText(getContext(), "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "알림 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        // 화면을 업데이트하거나 필요한 작업 수행
                    } else {
                        Toast.makeText(getContext(), "일정 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "일정 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });


        // 확인 버튼 추가
        builder.setNegativeButton("확인", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
