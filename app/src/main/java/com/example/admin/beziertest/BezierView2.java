package com.example.admin.beziertest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v7.view.menu.MenuAdapter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by admin on 2018/3/23.
 */

public class BezierView2 extends View {

    Point startPoint1;
    Point endPoint1;

    Point endPoint2;
    Point startPoint2;
    
    // 
    Point middlePoint1;
    Point middlePoint2;

    Circle fixedCircle;
    Circle touchCircle;

    Paint paint;
    Paint paintCircle;
    Path path;

    float initEndX;
    float initEndY;

    boolean isShowFixedCircle = true;
    boolean onceDisppeared = false; // 用于判断 小圆是否消失 ，使得 如果消失过 当大圆再回到20r的范围内 小圆不再出现

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

        ensureBezierPoint();
        path.reset();
        path.moveTo(startPoint1.x, startPoint1.y);
        path.quadTo(middlePoint1.x, middlePoint1.y, endPoint1.x, endPoint1.y);
        path.lineTo(endPoint1.x, endPoint1.y);

        path.moveTo(startPoint2.x, startPoint2.y);
        path.quadTo(middlePoint2.x, middlePoint2.y, endPoint2.x, endPoint2.y);
        path.lineTo(endPoint2.x, endPoint2.y);


        //  小圆消失后 贝塞尔曲线也消失
        if (isShowFixedCircle) {
            canvas.drawPath(path, paint);
            canvas.drawCircle(fixedCircle.x, fixedCircle.y, fixedCircle.r, paintCircle);   //   固定小圆
        }

        canvas.drawCircle(touchCircle.x, touchCircle.y, touchCircle.r, paintCircle);    //    动圆


    }

    private void init() {

        startPoint1 = new Point();
        endPoint1 = new Point();

        startPoint2 = new Point();
        endPoint2 = new Point();

        middlePoint1 = new Point();
        middlePoint2 = new Point();

        initEndX = 100;
        initEndY = 100;

        fixedCircle = new Circle();
        touchCircle = new Circle();

        fixedCircle.x =touchCircle.x = initEndX;
        fixedCircle.y =touchCircle.y = initEndY;

        fixedCircle.r = 20;
        touchCircle.r = 30;
        
        ensureBezierPoint();
        ensureMiddlePoint();

        path = new Path();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCircle.setStyle(Paint.Style.FILL);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float tempX = event.getX();
        float tempY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("TAG", "down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("TAG", "move");
                motionMove(tempX, tempY);
                break;
            case MotionEvent.ACTION_UP:
                motionUp();
                break;

        }
        return true;
    }

    private void motionMove(float tempX, float tempY) {

        //  如果 点击位置未超出大圆的范围，则大圆圆心为点击点的坐标
        if (pointRange(touchCircle.x, touchCircle.y, tempX, tempY, touchCircle.r)) {
            touchCircle.x = tempX;
            touchCircle.y = tempY;
        } else {
            touchCircle.x = initEndX;
            touchCircle.y = initEndY;
        }
        ensureBezierPoint();
        ensureMiddlePoint();
        if (onceDisppeared) {
            isShowFixedCircle = false;
        } else {
            isShowFixedCircle = true;
        }
        // 如果 两个圆之间的距离大于小圆的三倍则小圆消失
        if (distanceCircle(touchCircle.x, touchCircle.y) > 20 * fixedCircle.r) {
            isShowFixedCircle = false;
            onceDisppeared = true;
        }
        invalidate();
    }

    private void motionUp() {
        //isShowFixedCircle = false;
        touchCircle.x = initEndX;
        touchCircle.y = initEndY;
        ensureBezierPoint();
        ensureMiddlePoint();
        isShowFixedCircle = true;
        onceDisppeared = false;
        invalidate();
    }

    private boolean pointRange(float circleX, float circleY, float touchPointX, float touchPointY, float r) {
        if ((circleX - touchPointX) * (circleX - touchPointX) + (circleY - touchPointY) * (circleY - touchPointY) <= r * r) {
            return true;
        } else {
            return false;
        }
    }

    private float distanceCircle(float touchX, float touchY) {
        float distance = (touchX - fixedCircle.x) * (touchX - fixedCircle.x) + (touchY - fixedCircle.y) * (touchY - fixedCircle.y);
        return (float) Math.sqrt(distance);
    }

    private void ensureMiddlePoint() {
        //如果两个圆心的坐标距离小于小圆的半径,则贝塞尔曲线不显示
        if (pointRange(fixedCircle.x, fixedCircle.y, touchCircle.x, touchCircle.y, fixedCircle.r)) {
            middlePoint1.x = startPoint1.x;
            middlePoint1.y = startPoint1.y;

            middlePoint2.x = startPoint2.x;
            middlePoint2.y = startPoint2.y;
        } else {
            //  取两个圆心的中点为控制点，而不是曲线的起始点和终点 ,此处仍然使用 middlePoint1，middlePoint2 进行
            // 赋值，是为了以后两条曲线的控制点不相同做准备
            middlePoint1.x = middlePoint2.x = (fixedCircle.x + touchCircle.x) / 2;
            middlePoint1.y = middlePoint2.y = (fixedCircle.y + touchCircle.y) / 2;
        }
    }

    private void ensureBezierPoint() {

        float atan = (float) Math.atan((touchCircle.y - fixedCircle.y) / (touchCircle.x - fixedCircle.x)); //  求出的角度为需要角度的余角

        float sin = (float) Math.sin(atan);
        float cos = (float) Math.cos(atan);

        startPoint1.x = fixedCircle.x + fixedCircle.r * sin;
        startPoint1.y = fixedCircle.y - fixedCircle.r * cos;

        endPoint1.x = touchCircle.x + touchCircle.r * sin;
        endPoint1.y = touchCircle.y - touchCircle.r * cos;

        startPoint2.x = fixedCircle.x - fixedCircle.r * sin;
        startPoint2.y = fixedCircle.x + fixedCircle.r * cos;

        endPoint2.x = touchCircle.x - touchCircle.r * sin;
        endPoint2.y = touchCircle.y + touchCircle.r * cos;

    }
}

class Point {
    float x;
    float y;

}

class Circle {
    float x;
    float y;
    float r;
}
