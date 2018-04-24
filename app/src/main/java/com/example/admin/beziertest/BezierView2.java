package com.example.admin.beziertest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by wuyue on 2018/3/23.
 */

public class BezierView2 extends View {

    private String TAG = "tag";

    Point startPoint1; //曲线1
    Point endPoint1;

    Point endPoint2;  //  曲线2
    Point startPoint2;

    Point middlePoint1;  //  控制点
    Point middlePoint2;

    Circle fixedCircle; // 固定原位的圆
    Circle touchCircle; //  随点击事件移动的圆

    Paint paint;
    Paint paintCircle;
    Path path;

    float screenWidth;
    float screenHeight;

    float initEndX;   //  固定原位的圆的圆心初始位置 ， 移动圆弹回的位置
    float initEndY;

    float fixedCircleMixR; //  固定圆变化最小值，小于这个值固定圆消失，与maxDiatance有比例关系。固定圆的消失与否，可以从这两个值任意一个判断

    float maxDiatance;  // 固定圆与移动圆之间相距的最大距离 ，超出距离 固定圆和曲线消失

    float multipleOfRadiusDistance; // 最大距离与移动圆半径的倍数关系
    float multipleOfRadiusFiexd; // 固定圆最大最小值之间的倍数关系

    float initFixedCircleRadius;  //  固定圆的初始半径
    float initTouchedCircleRadius;  //  移动圆的初始半径


    boolean isShowFixedCircle = true;  // 是否显示固定圆
    boolean onceDisppeared = false; // 用于判断 小圆是否消失 ，使得 如果消失过 当大圆再回到20r的范围内 小圆不再出现

    public BezierView2(Context context) {
        super(context);
//        init();
    }

    public BezierView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        init();
    }

    public BezierView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        ensureBezierPoint();
        path.reset();
        //  六行代码 使画笔形的轨迹成封闭的图形，使得可以填充满颜色
        // https://segmentfault.com/a/1190000000721127
        path.moveTo(startPoint1.x, startPoint1.y);  //  画笔移动至(x,x),不画
        path.quadTo(middlePoint1.x, middlePoint1.y, endPoint1.x, endPoint1.y);
        path.lineTo(endPoint2.x, endPoint2.y);   //  画笔画至(x,x)

        path.moveTo(endPoint2.x, endPoint2.y);
        path.quadTo(middlePoint2.x, middlePoint2.y, startPoint2.x, startPoint2.y);
        path.lineTo(startPoint1.x, startPoint1.y);


        //  原来的这部分代码 画笔的轨迹不是封闭的图形 ，则图像显示的是两条曲线
//        path.moveTo(startPoint1.x, startPoint1.y);
//        path.quadTo(middlePoint1.x, middlePoint1.y, endPoint1.x, endPoint1.y);
//        path.lineTo(endPoint1.x, endPoint1.y);
//
//        path.moveTo(startPoint2.x, startPoint2.y);
//        path.quadTo(middlePoint2.x, middlePoint2.y, endPoint2.x, endPoint2.y);
//        path.lineTo(endPoint2.x, endPoint2.y);

        //  小圆消失后 贝塞尔曲线也消失
        if (isShowFixedCircle) {
            canvas.drawPath(path, paint);
            canvas.drawCircle(fixedCircle.x, fixedCircle.y, fixedCircle.r, paintCircle);   //   固定小圆
        }
        canvas.drawCircle(touchCircle.x, touchCircle.y, touchCircle.r, paintCircle);    //    动圆
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        screenHeight = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init();
    }

    private void init() {

        startPoint1 = new Point();
        endPoint1 = new Point();

        startPoint2 = new Point();
        endPoint2 = new Point();

        middlePoint1 = new Point();
        middlePoint2 = new Point();

        initEndX = screenWidth / 5;
        initEndY = screenHeight / 5;

        fixedCircle = new Circle();
        touchCircle = new Circle();

        fixedCircle.x = touchCircle.x = initEndX;
        fixedCircle.y = touchCircle.y = initEndY;

        touchCircle.r = 30;
        fixedCircle.r = touchCircle.r;
        initTouchedCircleRadius = touchCircle.r;
        initFixedCircleRadius = fixedCircle.r;

        multipleOfRadiusFiexd = 3;
        fixedCircleMixR = fixedCircle.r / multipleOfRadiusFiexd;
        multipleOfRadiusDistance = 10;
        maxDiatance = multipleOfRadiusDistance * fixedCircle.r;

        ensureBezierPoint();
        ensureMiddlePoint();

        path = new Path();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(Color.rgb(0xaa, 0xee, 0xee));

        paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paintCircle.setStyle(Paint.Style.FILL);
        paintCircle.setColor(Color.rgb(0xaa, 0xee, 0xee));

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
        return true;   //  需要返回true
    }

    private void motionMove(float tempX, float tempY) {
        //  如果 点击位置未超出大圆的范围，则大圆圆心跟随移动  否则大圆不动
        if (isBeyondCircle(touchCircle.x, touchCircle.y, tempX, tempY, touchCircle.r)) {
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
        float distance = getDistance(touchCircle.x, touchCircle.y);
        SetRadiusByDistance(distance);
        //  或通过固定圆半径是否缩小到最小来判断  fixedCircle.r > fixedCircleMixR
        if (distance > maxDiatance) {
            isShowFixedCircle = false;
            onceDisppeared = true;
        }
        invalidate();
    }

    private void motionUp() {
        //isShowFixedCircle = false;
        //  抬起点击点  大圆退回原位
        touchCircle.x = initEndX;
        touchCircle.y = initEndY;
        ensureBezierPoint();
        ensureMiddlePoint();
        isShowFixedCircle = true;
        onceDisppeared = false;
        invalidate();
    }

    //判断点击点是否在圆的范围内
    private boolean isBeyondCircle(float circleX, float circleY, float touchPointX, float touchPointY, float r) {
        if ((circleX - touchPointX) * (circleX - touchPointX) + (circleY - touchPointY) * (circleY - touchPointY) <= r * r) {
            return true;
        } else {
            return false;
        }
    }

    private float getDistance(float touchX, float touchY) {
        float distance = (touchX - fixedCircle.x) * (touchX - fixedCircle.x) + (touchY - fixedCircle.y) * (touchY - fixedCircle.y);
        return (float) Math.sqrt(distance);
    }

    private void ensureMiddlePoint() {
        //如果两个圆心的坐标距离小于小圆的半径,则贝塞尔曲线不显示
//        if (isBeyondCircle(fixedCircle.x, fixedCircle.y, touchCircle.x, touchCircle.y, fixedCircle.r)) {
//            middlePoint1.x = middlePoint2.x = initEndX;
//            middlePoint1.y = middlePoint2.y = initEndY;
//        } else {
        //  取两个圆心的中点为控制点，而不是根据曲线的起始点和终点的来确定控制顶
        middlePoint1.x = middlePoint2.x = (fixedCircle.x + touchCircle.x) / 2;
        middlePoint1.y = middlePoint2.y = (fixedCircle.y + touchCircle.y) / 2;
//        }
    }

    /**
     * 确定贝塞尔曲线的起止点坐标
     */
    private void ensureBezierPoint() {

        float atan = (float) Math.atan((touchCircle.y - fixedCircle.y) / (touchCircle.x - fixedCircle.x)); //  求出的角度为需要角度的余角

        float sin = (float) Math.sin(atan);
        float cos = (float) Math.cos(atan);

        startPoint1.x = fixedCircle.x + fixedCircle.r * sin;
        startPoint1.y = fixedCircle.y - fixedCircle.r * cos;

        endPoint1.x = touchCircle.x + touchCircle.r * sin;
        endPoint1.y = touchCircle.y - touchCircle.r * cos;

        startPoint2.x = fixedCircle.x - fixedCircle.r * sin;
        startPoint2.y = fixedCircle.y + fixedCircle.r * cos;

        endPoint2.x = touchCircle.x - touchCircle.r * sin;
        endPoint2.y = touchCircle.y + touchCircle.r * cos;

    }

    //  计算圆心之间的距离与固定圆半径的比例关系,当距离变到最大的时候，固定圆缩小到最小
    private void SetRadiusByDistance(float distance) {
        float multiple = ((1 - multipleOfRadiusFiexd) * initFixedCircleRadius) / (multipleOfRadiusFiexd * multipleOfRadiusDistance * initTouchedCircleRadius);
        Log.d(TAG, "" + multiple);
        float tamp = multiple * distance + initFixedCircleRadius;
        if (tamp >= fixedCircleMixR) {
            fixedCircle.r = tamp;
        } else {
            fixedCircle.r = fixedCircleMixR;
        }
    }

}

// 贝塞尔曲线上的点
class Point {
    float x;
    float y;
}

class Circle {
    float x;
    float y;
    float r;
}
