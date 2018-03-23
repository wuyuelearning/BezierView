package com.example.admin.beziertest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by admin on 2018/3/23.
 */

public class BezierView2 extends View {

    EndPoint endPoint;
    Paint paint;
    Path path;

    int startX;
    int startY;

    int initEndX;
    int initEndY;

    int middleX;
    int middleY;

    public BezierView2(Context context) {
        super(context);
        init();
    }

    public BezierView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BezierView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        path.reset();
        path.moveTo(startX, startY);
        path.quadTo(middleX, middleY, endPoint.x, endPoint.y);
        path.lineTo(endPoint.x, endPoint.y);
        canvas.drawPath(path, paint);
    }

    private void init() {

        endPoint = new EndPoint();

        startX = 100;
        startY = 100;

        initEndX = 150;
        initEndY = 150;

        endPoint.x = initEndX;
        endPoint.y = initEndY;

        ensureMiddlePoint();

        path = new Path();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("TAG", "down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("TAG", "move");
                endPoint.x = (int) event.getX();
                endPoint.y = (int) event.getY();
                ensureMiddlePoint();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                endPoint.x = initEndX;
                endPoint.y = initEndY;
                ensureMiddlePoint();
                invalidate();
                break;

        }
        return true;
    }

    private void ensureMiddlePoint() {
        middleX = (startX + endPoint.x) / 2;
        middleY = (startY + endPoint.y) / 2 + 100;
    }
}

class EndPoint {
    int x;
    int y;
}
