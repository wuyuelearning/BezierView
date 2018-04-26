package com.example.admin.beziertest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by wuyue on 2018/3/23.
 */

public class BezierView2 extends View {

    private static final String TAG = "tag";
    private static final String PATH = "path";

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
        Log.d(TAG, "onDraw");
        ensureBezierPoint();
        path.reset();
        //  六行代码 使画笔形的轨迹成封闭的图形，使得可以填充满颜色
        // https://segmentfault.com/a/1190000000721127
        path.moveTo(startPoint1.x, startPoint1.y);  //  画笔移动至(x,x),不画
        path.quadTo(middlePoint1.x, middlePoint1.y, endPoint1.x, endPoint1.y);
        path.lineTo(endPoint2.x, endPoint2.y);   //  画笔画至(x,x)

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

        //  小圆消失后或两个 贝塞尔曲线也消失

        if (isShowFixedCircle) {
            canvas.drawPath(path, paint);
            canvas.drawCircle(fixedCircle.x, fixedCircle.y, fixedCircle.r, paintCircle);   //   固定圆
//            Log.d(PATH,"isShowFixedCircle");
        }

        float distance = getDistance(touchCircle.x, touchCircle.y);
        // 如果两圆之间的距离小于两圆半径（固定圆半径会变）之和，则贝塞尔曲线消失
        if ((touchCircle.r + fixedCircle.r) >= distance) {
//            Log.d(PATH,"distance");
            canvas.drawPath(path, paint);
        }
        canvas.drawCircle(touchCircle.x, touchCircle.y, touchCircle.r, paintCircle);    //    动圆
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure");

        screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        screenHeight = MeasureSpec.getSize(heightMeasureSpec);

        Log.d(TAG, "onMeasure" + widthMeasureSpec);
        Log.d(TAG, "onMeasure" + heightMeasureSpec);
        Log.d(TAG, "onMeasure" + screenWidth);
        Log.d(TAG, "onMeasure" + screenHeight);
        Log.d(TAG, "onMeasure" + MeasureSpec.getMode(widthMeasureSpec));
        Log.d(TAG, "onMeasure" + MeasureSpec.getMode(heightMeasureSpec));
        Log.d(TAG, "onMeasure" + MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getMode(widthMeasureSpec)));
        Log.d(TAG, "onMeasure" + MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.getMode(heightMeasureSpec)));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left + 100, top + 100, right, bottom);

        Log.d(TAG, "onLayout  " + getMeasuredWidth());
        Log.d(TAG, "onLayout  " + getMeasuredHeight());
        Log.d(TAG, "onLayout  " + left);
        Log.d(TAG, "onLayout  " + top);
        Log.d(TAG, "onLayout  " + right);
        Log.d(TAG, "onLayout  " + bottom);
        Log.d(TAG, "onLayout  ");
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

        Log.d(PATH, "" + initEndX + "  " + initEndY);

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
                break;
            case MotionEvent.ACTION_MOVE:
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
//        Log.d("flag","motionMove");
        invalidate();
    }

    private void motionUp() {
        //isShowFixedCircle = false;
        Log.d(PATH, "motionUp  " + touchCircle.x + "  " + touchCircle.y);
        if (isShowFixedCircle) {
            // 当小圆未消失的情况下，松开控制点 ，显示回弹效果
            startAnimation();
        } else {
            //  抬起点击点  大圆退回原位
            touchCircle.x = initEndX;
            touchCircle.y = initEndY;
            reset();
        }
        invalidate();
    }

    private void reset() {
        ensureBezierPoint();
        ensureMiddlePoint();
        isShowFixedCircle = true;
        onceDisppeared = false;
    }

    private void startAnimation() {


        // ofFloat 第一个参数是x的起始位置（数值touchCircle.x，仅仅是因为此时控制点的x坐标与touchCircle.x数值一直），
        // 第二个为终点位置
        ValueAnimator animatorX = ValueAnimator.ofFloat(touchCircle.x, initEndX);  //  语句1
        animatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //循环地将x坐标位置赋值给 固定球的x坐标，从而得到固定球x的轨迹
                //  语句1是一个轨迹，一种变化 ，如果没有给哪个视图赋值 将没有什么意义，此时赋值给了touchCircle.x
                //  也就是使得控制圆的x坐标的变化与animatorX的变化 一致，
                // 所以 ，可以看成先在animatorX中设置变化内容 ，再将这种变化赋值给某个视图控件，是这种变化
                // 与视图控价联系起来，使视图也按照animatorX的变化而变化
                touchCircle.x = (float) animation.getAnimatedValue();
//                Log.d(PATH, "startAnimation X: " + touchCircle.x);
                ensureBezierPoint();
                ensureMiddlePoint();
                invalidate();
            }
        });


        // ofFloat 第一个参数是y的起始位置，第二个为终点位置
        ValueAnimator animatorY = ValueAnimator.ofFloat(touchCircle.y, initEndY);
        animatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                touchCircle.y = (float) animation.getAnimatedValue();
//                Log.d(PATH,"startAnimation Y: "+touchCircle.y);
                //  需要不断更新 ，否则可能会出现贝塞尔曲线动画滞后的现象
                ensureBezierPoint();
                ensureMiddlePoint();
                invalidate();
            }
        });

        Log.d(PATH, "startAnimation  " + touchCircle.x + "  " + touchCircle.y);

        //  设置动画合集
        AnimatorSet animatorSet = new AnimatorSet();

        // animatorX,animatorY 同时变化
        animatorSet.play(animatorX).with(animatorY);
//        animatorSet.setDuration(700);
//        animatorSet.setInterpolator(new LinearInterpolator());
        //  两种回弹效果
//        animatorSet.setInterpolator(new AnticipateOvershootInterpolator());
        animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.start();

        // 在动画结束后执行
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                reset();
            }
        });

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
//        Log.d(PATH,""+middlePoint1.x+"   "+middlePoint1.y);
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
