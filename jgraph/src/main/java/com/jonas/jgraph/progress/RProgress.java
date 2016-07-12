package com.jonas.jgraph.progress;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * @author yun.
 * @date 2015/10/7
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public class RProgress extends View {

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint background = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mCenx;
    private float mCeny;
    private int mHeight;
    private int mWidth;
    private Path backPath = new Path();
    private Path progressPath = new Path();
    private float radius = 200;
    private RectF mBackRect = new RectF();
    private float rx;
    private float ry;
    private int bgColor = Color.WHITE;
    private int progColor = Color.RED;
    private int mJust;
    private float cProgress = 10;
    private float progress = 1;
    private float mProgress = 100;
    private ValueAnimator pAnimation = new ValueAnimator();
    private long ANIDURATION = 1000;
    private TimeInterpolator interpolator = new OvershootInterpolator();
    private boolean showInner = false;

    {
        background.setColor(bgColor);
        mPaint.setColor(progColor);
    }

    public RProgress(Context context){
        this(context, null);
    }

    public RProgress(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public RProgress(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        //        if(attrs != null) {
        //            context.obtainStyledAttributes()
        //        }
        init();
    }

    private void init(){
        ////        mPaint.setStyle(Paint.Style.STROKE);
        //        mPaint.setStrokeWidth(3);
        //        mPaint.setColor(Color.RED);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        mHeight = getHeight();
        mCenx = mWidth/2f;
        mCeny = mHeight/2f;

        mJust = mWidth>mHeight ? mHeight-getPaddingBottom()-getPaddingTop() : mWidth-getPaddingBottom()-getPaddingTop();
        rx = ry = ry == 0 || rx == 0 ? mJust/2 : rx;
        mBackRect.set(getPaddingLeft(), getPaddingTop(), mWidth-getPaddingRight(), mHeight-getPaddingBottom());
        backPath.addRoundRect(mBackRect, rx, ry, Path.Direction.CCW);
        if(showInner) {
            mBackRect.set(0, 0, mWidth, mHeight);
            backPath.addRect(mBackRect, Path.Direction.CW);
            backPath.setFillType(Path.FillType.EVEN_ODD);
        }

    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if(showInner) {
            //画进度
            mBackRect.set(getPaddingLeft(), getPaddingTop(), cProgress*mWidth/mProgress*progress, mHeight-getPaddingBottom());
            canvas.drawRect(mBackRect, mPaint);

            //画背景  每次都得画   矩形在进度上面画背景 该背景是在矩形上去掉一个内部的圆角矩形（Path.FillType.EVEN_ODD 去掉重复部分)
            //那么就只能够 透过圆角矩形 看到 下面的进度了
            canvas.drawPath(backPath, background);
        }else {
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG|Paint.ANTI_ALIAS_FLAG));
            canvas.clipPath(backPath);//剪切出 圆角矩形区域 然后再上面画矩形  显示的就是两边为圆角的进度
//            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            //画进度
            mBackRect.set(getPaddingLeft(), getPaddingTop(), cProgress*mWidth/mProgress*progress, mHeight-getPaddingBottom());
            canvas.drawRect(mBackRect, mPaint);
        }
        if(progress<1) {
            //递归onDraw方法 实现动画效果 缺点是 无法添加速度效果
            progress += 0.01;
            postInvalidate();
        }
    }

    public void setCurrentProgress(float currentProgress){
        this.cProgress = currentProgress;
        progress = 1;
    }

    /**
     * 需要先设置setMaxProgress 否则总进度默认为 100
     * 设置当前进度 带有动画
     *
     * @param currentProgress
     *         当前进度
     */
    public void setAniCurrentProgress(float currentProgress){
        //        this.cProgress = currentProgress;
        //        progress = 0;
        //        postInvalidate();//无法使用各种加速效果
        pAnimation.cancel();
        pAnimation.setFloatValues(0, currentProgress);
        pAnimation.setDuration(ANIDURATION);
        pAnimation.setInterpolator(interpolator);
        pAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation){
                cProgress = (float)animation.getAnimatedValue();

                postInvalidate();
            }
        });
        pAnimation.start();
    }

    /**
     * @param maxProgress
     *         进度条最大的进度
     */
    public void setMaxProgress(float maxProgress){
        this.mProgress = maxProgress;
    }
}

