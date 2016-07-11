package com.jonas.jdiagram.progress;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import java.util.Random;

/**
 * @author yun.
 * @date 2015/10/7
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public class WaveProgress extends View {

    private Paint wPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint background = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mCenx;
    private float mCeny;
    private int mHeight;
    private int mWidth;
    private Path backPath = new Path();
    /**
     * 波浪 路径
     */
    private Path wPath = new Path();
    private RectF mBackRect = new RectF();
    private int bgColor = Color.WHITE;
    /**
     * 波浪的颜色
     */
    private int wColor = Color.RED;
    private int mJust;
    /**
     * 当前进度
     */
    private float cProgress = 80;

    /**
     * 水波的速度
     */
    private float waveSpeed = 10;
    private float waveMove = 0;
    /**
     * 最大进度
     */
    private float mProgress = 100;
    private ValueAnimator pAnimation = new ValueAnimator();
    private long ANIDURATION = 1000;
    private TimeInterpolator interpolator = new OvershootInterpolator();
    /**
     * 水波球的半径
     */
    private float wRadius;
    /**
     * 波浪振幅
     */
    private float range = 25;
    private Random mRandom;

    {
        background.setColor(bgColor);
        wPaint.setColor(wColor);
        mRandom = new Random();
    }

    public WaveProgress(Context context){
        this(context, null);
    }

    public WaveProgress(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public WaveProgress(Context context, AttributeSet attrs, int defStyleAttr){
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
        mJust = mWidth>mHeight ? mHeight : mWidth;

        wRadius = wRadius == 0 ? mJust/2 : wRadius;
        backPath.addCircle(mCenx, mCeny, wRadius, Path.Direction.CCW);

//        range = wRadius
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        canvas.clipPath(backPath);//剪切出圆形画布 画在外面的部分不显示
        canvas.drawColor(bgColor);

        //mCeny+wRadius-cProgress 为当前进度 所在的y轴坐标
        float currentY = mCeny+wRadius-cProgress/mProgress*wRadius*2;
        wPath.reset();
        wPath.moveTo(0,currentY);//起点

        //波浪进度 的path
        for(int i = 0; i<=mJust; i++) {
//            range = mRandom.nextInt(30)+10;
            //画cos曲线  cos曲线往左或者往右一直移动出现波浪波动效果
            wPath.lineTo(i, currentY+range*(float)Math.cos(( i+waveMove )*( 480f/mJust )*Math.PI/180f));
        }
        wPath.lineTo(mWidth, mHeight);//右下角
        wPath.lineTo(0, mHeight);//左下角  之后会闭合到起点

        //画波浪进度
        canvas.drawPath(wPath, wPaint);

        //中间线条
        canvas.drawLine(0, mCeny, mWidth, mCeny, background);
        canvas.drawLine(0, currentY, mWidth, currentY, background);
        waveMove +=waveSpeed+mRandom.nextInt(20);
        postInvalidate();
    }

    public void setCurrentProgress(float currentProgress){
        this.cProgress = currentProgress;
        waveSpeed = 1;
    }

    /**
     * 需要先设置setMaxProgress 否则总进度默认为 100
     * 设置当前进度 带有动画
     *
     * @param currentProgress
     *         当前进度
     */
    public void setAniCurrentProgress(float currentProgress){
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

