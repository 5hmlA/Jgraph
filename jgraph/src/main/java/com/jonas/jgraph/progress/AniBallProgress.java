package com.jonas.jgraph.progress;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.jonas.jgraph.R;
import com.jonas.jgraph.inter.IProgress;
import com.jonas.jgraph.models.Jball;

import java.util.ArrayList;

/**
 * @author yun.
 * @date 2016/6/8
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public class AniBallProgress extends View implements IProgress {

    private Paint mProgPaint;
    private int mShorter;
    /**
     * 圆环的宽度
     */
    private float mProgWidth = 10;
    /**
     * 圆环 据周边的距离
     */
    private int ringPading = 10;
    /**
     * 圆环上 滚动小球的颜色
     */
    private int[] mBallColor;
    /**
     * 圆环 内字 的内容
     */
    private String centerText;
    private PointF mCenPoint;
    private ArrayList<Jball> mAutoBalls = new ArrayList<>();
    private RectF mRingRectf;
    private Paint mTextPaint;
    private float mProgress;
    private float mMax = 100;
    private int mProgColor = Color.RED;
    private float mRingRadius;
    private Jball mHeadBall;
    private Jball mFootBall;
    private long mAniduration = 3000;
    private float mAniRatio;
    private long mBallDelay = 200;
    private int mBallNums = 3;
    private float mRotate = -90;


    public AniBallProgress(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public AniBallProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.JProgress);
        mProgColor = a.getColor(R.styleable.JProgress_progressColor, Color.RED);
        mMax = a.getFloat(R.styleable.JProgress_max, 100);
        mProgress = a.getFloat(R.styleable.JProgress_progress, 0);
        mProgWidth = a.getFloat(R.styleable.JProgress_progwidth, 10);
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mProgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgPaint.setStyle(Paint.Style.STROKE);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        initialDate();
    }

    private void initialDate() {
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setTextSize(35);
        mProgPaint.setColor(mProgColor);
        mProgPaint.setStrokeWidth(mProgWidth);
        mHeadBall = new Jball(mProgWidth / 2);
        mFootBall = new Jball(mProgWidth / 2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        // 以小的边为主
        mShorter = w > h ? h : w;
        mCenPoint = new PointF(w / 2f, h / 2f);
        mRingRadius = mShorter / 2f - ringPading - mProgWidth / 2f;
        //圆环用的外部矩形
        mRingRectf = new RectF(mCenPoint.x - mRingRadius, mCenPoint.y - mRingRadius, mCenPoint.x + mRingRadius,
                mCenPoint.y + mRingRadius);
        if (mAutoBalls.size() == 0) {
            if (mBallColor != null) {
                mBallNums = mBallColor.length;
            }
            for (int i = 0; i < mBallNums; i++) {
                Jball jball = new Jball(mProgWidth / 2);
                if (mBallColor == null) {
                    jball.setColor(mProgColor);
                } else {
                    jball.setColor(mBallColor[i]);
                }
                jball.setRouteCenter(mCenPoint)
                        .setRouteRadius(mRingRadius)
                        .setIndex(i);
//                jball.setColor(Color.YELLOW);
                mAutoBalls.add(jball);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.rotate(mRotate, mCenPoint.x, mCenPoint.y);
        //方式一
        drawRing_Balls(canvas);
        canvas.restore();

        if (TextUtils.isEmpty(centerText)) {
            //如过 文字为空的话 就不画
            return;
        }
        // C 语言 的思想
        @SuppressLint("DrawAllocation") Rect bounds = new Rect();
        mProgPaint.getTextBounds(centerText, 0, centerText.length(), bounds);
        // 获取 所画的字的宽和高
        // mpaint.measureText(text);//返回的是字的宽度
        int textWidth = bounds.width();
        int textHeight = bounds.height();
        canvas.drawText(centerText, mCenPoint.x - textWidth / 2, mCenPoint.y + textHeight / 2, mProgPaint);
    }

    private void drawRing_Balls(Canvas canvas) {
        //小圆球
        for (int i = 0; i < mAutoBalls.size(); i++) {
            Jball autoBall = mAutoBalls.get(i);
            autoBall.setBallRadius(mProgWidth / 2).setRouteRadius(mRingRadius).drawCircle(canvas, this);
        }
        //画环形
        drawRing(canvas, mAniRatio * mMax <= mProgress ? mAniRatio * mMax : mProgress);
    }

    /**
     * @param canvas
     * @param sweepProg
     */
    private void drawRing(Canvas canvas, float sweepProg) {
        //直接画环形
        float sweepAngle = sweepProg / mMax * 360;
        canvas.drawArc(mRingRectf, 0, sweepAngle, false, mProgPaint);
        mHeadBall.setColor(mProgColor).setBallRadius(mProgWidth / 2).setBallCenter(getBallCenterPoint(0)).drawCircle(canvas, this);
        mFootBall.setColor(mProgColor).setBallRadius(mProgWidth / 2).setBallCenter(getBallCenterPoint(sweepAngle)).drawCircle(canvas, this);
    }

    private PointF getBallCenterPoint(float sweepAngle) {
        // 在圆环末端 画小球 角度=弧度/PI * 180 弧度＝(角度/180) *PI
        // 在圆环末端 的小球 的轨迹是圆 半径为 内圆半径+圆环环宽度的一半
        double sweepHudu = sweepAngle / 180 * Math.PI;
        float cenx = (float) (mRingRadius * Math.cos(sweepHudu)) + mCenPoint.x;
        float ceny = (float) (mRingRadius * Math.sin(sweepHudu)) + mCenPoint.y;
        return new PointF(cenx, ceny);
    }

    @Override
    public IProgress setJProgress(float progress) {
        mProgress = progress;
        mAniRatio = mProgress / mMax;
        return this;
    }

    public float getAniRatio() {
        return mAniRatio;
    }

    public void setAniRatio(float aniRatio) {
        mAniRatio = aniRatio;
    }

    @Override
    public IProgress setAniProgress(float progress) {
        mProgress = progress;
        ObjectAnimator oa = ObjectAnimator.ofFloat(this, "aniRatio", 0, 1)
                .setDuration(mAniduration);
        oa.setInterpolator(new AccelerateDecelerateInterpolator());
        oa.start();
        for (int i = 0; i < mAutoBalls.size(); i++) {
            Jball autoBall = mAutoBalls.get(i);
            autoBall.cancelAnimation();
            autoBall.setStartAniSweep(0, mAniduration, mBallDelay * i);
        }
        return this;
    }

    public AniBallProgress setBallDelay(long ballDelay) {
        mBallDelay = ballDelay;
        return this;
    }

    /**
     * @return 圆环 局周围的 边距
     */
    public int getRingPading() {
        return ringPading;
    }

    /**
     * @return 圆环 局周围的 边距
     */
    public AniBallProgress setRingPading(int ringPading) {
        this.ringPading = ringPading;
        return this;
    }

    /**
     * @return 滚动小球的颜色
     */
    public AniBallProgress setBallColor(int... ballColor) {
        this.mBallColor = ballColor;
        if (mAutoBalls.size() > 0) {
            for (int i = 0; i < mBallColor.length; i++) {
                mAutoBalls.get(i).setColor(mBallColor[i]);
            }
        }
        return this;
    }

    /**
     * @return 文字的画笔
     */
    public Paint getTextPaint() {
        return mTextPaint;
    }

    public Paint getProgPaint() {
        return mProgPaint;
    }

    /**
     * @return 文字的内容
     */
    public String getCenterText() {
        return centerText;
    }

    /**
     * @return 文字的内容，当内容为空时 不显示 默认不显示
     */
    public IProgress setCenterText(String centerText) {
        this.centerText = centerText;
        return this;
    }

    @Override
    public IProgress setAniDuration(long duration) {
        mAniduration = duration;
        return this;
    }

    @Override
    public float getProgress() {
        return mProgress;
    }

    @Override
    public IProgress setProgressWidth(float progWidth) {
        mProgWidth = progWidth;
        mProgPaint.setStrokeWidth(mProgWidth);
        if (mAutoBalls.size() > 0) {
            for (Jball autoBall : mAutoBalls) {
                autoBall.setBallRadius(mProgWidth / 2);
            }
        }
        return this;
    }

    @Override
    public IProgress setMax(float max) {
        mMax = max;
        return this;
    }

    @Override
    public IProgress setProgressBackground(int color) {
        return this;
    }

    @Override
    public IProgress setProgressColor(int color) {
        mProgColor = color;
        mProgPaint.setColor(color);
        return this;
    }
}
