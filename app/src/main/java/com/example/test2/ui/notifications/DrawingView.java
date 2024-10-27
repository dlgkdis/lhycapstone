package com.example.test2.ui.notifications;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class DrawingView extends View {
    private Paint paint;
    private Path path;
    private TextView drawingHintText; // TextView 참조

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK); // 그리기 색상 설정
        paint.setStrokeWidth(10f);   // 그리기 선의 두께 설정
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                return true; // 터치가 시작되었음을 표시
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                return false; // 다른 경우는 무시
        }
        invalidate(); // 화면을 다시 그리기
        return true; // 터치 이벤트를 소비
    }

    public void clearDrawing() {
        path.reset();
        invalidate();
    }

    // TextView 참조 설정 메서드 추가
    public void setDrawingHintText(TextView textView) {
        this.drawingHintText = textView;
    }

    public void setDrawingHintVisibility(boolean isVisible) {
        if (drawingHintText != null) {
            drawingHintText.setVisibility(isVisible ? VISIBLE : GONE);
        }
    }
}