package com.example.test2.ui.notifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class DrawingView extends View {
    private Paint paint;
    private Path path;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private TextView drawingHintText;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (bitmapCanvas == null) {
            initializeBitmapCanvas();
        }

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                if (bitmapCanvas != null) {
                    bitmapCanvas.drawPath(path, paint);
                }
                path.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void clearDrawing() {
        path.reset();
        if (bitmapCanvas != null) {
            bitmapCanvas.drawColor(Color.WHITE);
        }
        invalidate();
    }

    public void setDrawingHintText(TextView textView) {
        this.drawingHintText = textView;
    }

    public void setDrawingHintVisibility(boolean isVisible) {
        if (drawingHintText != null) {
            drawingHintText.setVisibility(isVisible ? VISIBLE : GONE);
        }
    }

    public Bitmap getBitmap() {
        if (bitmap == null) {
            initializeBitmapCanvas();
        }
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmapCanvas = new Canvas(this.bitmap);
        invalidate();
    }

    private void initializeBitmapCanvas() {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmapCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); // 투명 배경 설정
    }
}
