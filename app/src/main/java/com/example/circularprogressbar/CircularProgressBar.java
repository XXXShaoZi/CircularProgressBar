package com.example.circularprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

public class CircularProgressBar extends View {
    // 画圆环背景以及圆环进度的画笔
    private Paint mCircularPaint;
    private Paint mCircularProgressPaint;

    // 背景圆环颜色
    private int mBGCircularColor = getColorWithId(R.color.bg_progress);
    // 圆环进度颜色
    private int mFGCircularColor = getColorWithId(R.color.fg_progress);
    // 圆环的宽度
    private float mCircularWidth = 6;
    // 进度
    private int mTotal = 100;
    private int mProgress = 0;

    public CircularProgressBar( Context context ) {
        super(context);
        initPaint();
    }

    public CircularProgressBar( Context context, AttributeSet attrs ) {
        super(context, attrs);
        initWithAttrs(attrs);
        initPaint();
    }

    public CircularProgressBar( Context context, AttributeSet attrs, int defStyleAttr ) {
        super(context, attrs, defStyleAttr);
        initWithAttrs(attrs);
        initPaint();
    }

    private void initWithAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(
                attrs, R.styleable.CircularProgressBar);

        mBGCircularColor = typedArray.getColor(
                R.styleable.CircularProgressBar_bg_color,
                mBGCircularColor);
        mFGCircularColor = typedArray.getColor(
                R.styleable.CircularProgressBar_fg_color,
                mFGCircularColor);
        mCircularWidth = typedArray.getFloat(
                R.styleable.CircularProgressBar_width,
                mCircularWidth);
        mTotal = typedArray.getInt(
                R.styleable.CircularProgressBar_total,
                mTotal);
        mProgress = typedArray.getInt(
                R.styleable.CircularProgressBar_progress,
                mProgress);

        // 主动回收资源
        typedArray.recycle();
    }

    private void initPaint(){
        mCircularPaint = new Paint();
        mCircularPaint.setColor(mBGCircularColor);
        mCircularPaint.setStrokeWidth(mCircularWidth);
        // 抗锯齿
        mCircularPaint.setAntiAlias(true);
        // 设为描边模式，默认是填充模式
        mCircularPaint.setStyle(Paint.Style.STROKE);

        mCircularProgressPaint = new Paint();
        mCircularProgressPaint.setColor(mFGCircularColor);
        mCircularProgressPaint.setStrokeWidth(mCircularWidth);
        mCircularProgressPaint.setAntiAlias(true);
        mCircularProgressPaint.setStyle(Paint.Style.STROKE);
    }

    private int getColorWithId(final int resId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // API 23 以上才支持这种方式
            return getContext().getColor(resId);
        } else {
            return getResources().getColor(resId);
        }
    }

    private void drawArc( Canvas canvas, Paint paint, float cx, float cy,
                          float radius, int startAngle, int endAngle) {
        RectF oval = new RectF(cx - radius,
                cy - radius,
                cx + radius,
                cy + radius);
        canvas.drawArc(oval, startAngle, endAngle, false, paint);
    }

    @Override
    protected void onDraw( Canvas canvas ) {
        super.onDraw(canvas);

        float halfCircularWidth = mCircularWidth / 2;
        float xCenter = (float)(getWidth()) / 2;
        float yCenter = (float)(getHeight()) / 2;
        float radius = xCenter - halfCircularWidth;

        // 画背景
        drawArc(canvas, mCircularPaint, xCenter, yCenter, radius, 0, 360);

        // 画进度
        int start = -90;
        int end = mTotal == mProgress ? 360 : 360 * mProgress / mTotal;
        drawArc(canvas, mCircularProgressPaint, xCenter, yCenter, radius, start, end);
    }

    public synchronized void setProgress(int progress) {
        mProgress = progress > mTotal ? mTotal : progress;
        //重绘
        invalidate();
    }

    public int getProgress() {
        return mProgress;
    }

    public synchronized void setTotal(int total) {
        mTotal = total;
    }

    public int getTotal(){
        return mTotal;
    }
}
