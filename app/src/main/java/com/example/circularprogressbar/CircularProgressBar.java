package com.example.circularprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class CircularProgressBar extends View {
    // 画圆环背景以及圆环进度的画笔
    private Paint mCircularPaint;
    private Paint mCircularProgressPaint;
    private Paint mDigitProgress;

    // 背景圆环颜色
    private int mBGCircularColor = getColorWithId(R.color.bg_progress);
    // 圆环进度颜色
    private int mFGCircularColor = getColorWithId(R.color.fg_progress);
    // 圆环的宽度
    private float mCircularWidth = 6;
    // 进度
    private int mTotal = 100;
    private int mProgress = 0;
    // 起始角度
    private int mStartAngle = -90;
    // 动画
    private ProgressAnimation mProgressAnimation = new ProgressAnimation();

    public CircularProgressBar( Context context ) {
        super(context);
        initControls();
    }

    public CircularProgressBar( Context context, AttributeSet attrs ) {
        super(context, attrs);
        initWithAttrs(attrs);
        initControls();
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
        mStartAngle = typedArray.getInt(
                R.styleable.CircularProgressBar_start_angle,
                mStartAngle);
        setProgress(typedArray.getInt(
                R.styleable.CircularProgressBar_progress,
                mProgress));

        // 主动回收资源
        typedArray.recycle();
    }

    private void initControls(){
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

        mDigitProgress = new Paint();
        mDigitProgress.setColor(mFGCircularColor);
        mDigitProgress.setAntiAlias(true);
        mDigitProgress.setStyle(Paint.Style.FILL);
        mDigitProgress.setTextAlign(Paint.Align.CENTER);
        mDigitProgress.setStrokeWidth(2f);
        mDigitProgress.setTextSize(60f);

        // for test
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick( View v ) {
                mProgressAnimation.setDelta(mProgress);
                mProgress = 0;
                startAnimation(mProgressAnimation);
            }
        });
    }

    private class ProgressAnimation extends Animation {
        private int mBaseProgress;
        private int mDelta;

        @Override
        public void initialize( int width, int height, int parentWidth, int parentHeight ) {
            super.initialize(width, height, parentWidth, parentHeight);
            setDuration(1000);
            setInterpolator(new LinearInterpolator());
            mBaseProgress = mProgress;
        }

        @Override
        protected void applyTransformation( float interpolatedTime, Transformation t ) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                mProgress = mBaseProgress + (int) (mDelta * interpolatedTime);
            } else {
                mProgress = mBaseProgress + mDelta;
            }
            postInvalidate();
        }

        public void setDelta( int delta ) {
            mDelta = delta;
        }
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
        int end = mTotal == mProgress ? 360 : 360 * mProgress / mTotal;
        drawArc(canvas, mCircularProgressPaint, xCenter, yCenter, radius, mStartAngle, end);

        // 画数字
        String progressText = (mProgress * 100) / mTotal + "%";
        Rect rc = new Rect();
        mDigitProgress.getTextBounds(progressText, 0, progressText.length(), rc);
        canvas.drawText(progressText, xCenter, yCenter + rc.height()/2, mDigitProgress);
    }

    public void setProgress(int progress) {
        clearAnimation();
        int newProgress = progress > mTotal ? mTotal : progress;
        mProgressAnimation.setDelta(newProgress - mProgress);
        startAnimation(mProgressAnimation);
    }

    public int getProgress() {
        return mProgress;
    }

    public void setTotal(int total) {
        mTotal = total;
    }

    public int getTotal(){
        return mTotal;
    }


}
