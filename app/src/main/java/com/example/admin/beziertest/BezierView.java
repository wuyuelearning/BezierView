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
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by admin on 2018/3/22.
 */

public class BezierView extends View {

    //  控制点
    ControlPoint controlPoint;

    Path path;

    Paint paint;

    //  基准线
    int baseLineStartX;
    int baseLineStartY;
    int baseLineEndX;
    int baseLineEndY;

    //  View的宽度和高度
    int viewWidth;
    int viewHeight;

    //  控制点活动的范围 ， 以对角线确定出控制点的活动范围
    int viewStartX;
    int viewStartY;
    int viewEndX;
    int viewEndY;

    //  控制点初始位置
    int initControlPointX;
    int initControlPointY;

    Context context;


    public BezierView(Context context) {
        super(context);
        this.context = context;

    }

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

    }

//    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    private void init() {

        baseLineStartX = 0;
        baseLineStartY = 500;
        baseLineEndX = viewWidth;
        baseLineEndY = baseLineStartY;

        initControlPointX = viewWidth / 2;
        initControlPointY = viewHeight / 3;

        viewStartX = baseLineStartX;
        viewStartY = 0;
        viewEndX = baseLineEndX;
        viewEndY = viewHeight;

        path = new Path();
        controlPoint = new ControlPoint();
        controlPoint.x = initControlPointX;
        controlPoint.y = initControlPointY;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        path.reset();
        path.moveTo(baseLineStartX, baseLineStartY);
        path.quadTo(controlPoint.x, controlPoint.y, baseLineEndX, baseLineEndY);
        path.lineTo(baseLineEndX, baseLineEndY);
        canvas.drawPath(path, paint);

    }

    /**
     * 测量View的宽高，onMeasure在构造方法之后调用，所以在init()中使用宽高就需要在onMeasure之后再调用
     * 原先将init()放在构造方法中，使得 initControlPointX = viewWidth / 2;  一直拿不到View宽高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("TAG", "down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("TAG", "move");
                Log.d("TAG", "( " + event.getX() + " , " + event.getY() + " )");
                ensureControlPoint(event);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                Log.d("TAG", "up");
                controlPoint.x = initControlPointX;
                controlPoint.y = initControlPointY;
                invalidate();
                break;
        }
//        return super.onTouchEvent(event);
        return true;
    }

    /**
     * 确定控制点的位置 控制点要求在View的范围内 当超出边界则将超出的坐标固定为边界值
     *
     * @param event
     */
    private void ensureControlPoint(MotionEvent event) {
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();
        if (touchY < viewStartY) {
            controlPoint.y = viewStartY;
            ensureControlPointX(touchX);
        } else if (viewStartY <= touchY && touchY <= viewEndY) {
            controlPoint.y = touchY;
            ensureControlPointX(touchX);
        } else if (viewEndY < touchY) {
            controlPoint.y = viewEndY;
            ensureControlPointX(touchX);
        }
    }

    /**
     * 在控制点y坐标确定的条件下，讨论x坐标
     *
     * @param touchX
     */
    private void ensureControlPointX(int touchX) {
        if (touchX < viewStartX) {
            controlPoint.x = viewStartX;
        } else if (viewStartX <= touchX && touchX <= viewEndX) {
            controlPoint.x = touchX;
        } else if (viewEndX < touchX) {
            controlPoint.x = viewEndX;
        }
    }

}

// 控制点
class ControlPoint {
    int x;
    int y;
}
