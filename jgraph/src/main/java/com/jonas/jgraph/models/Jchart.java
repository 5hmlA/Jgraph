package com.jonas.jgraph.models;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.jonas.jgraph.BuildConfig;

import java.text.DecimalFormat;

import static android.content.ContentValues.TAG;

/**
 * @author yun.
 * @date 2016/6/8
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public class Jchart implements Cloneable {
    private String mShowMsg;
    private int index; //序号
    private float mWidth;//柱状 的 宽
    private float mHeight;//折线的y 画图的时候会被缩放
    private PointF mStart = new PointF();//矩形左下角起点
    private float mMidX;//中点 折线的x
    private int mColor = -1;
    private float mNum; //当前数字
    private float mMax; //总数据
    private float percent;//占比
    private String textMsg; //要显示的信息
    private String mXmsg; //横坐标信息
    private float mUpper;
    private float mLower;
    private float mLowStart;
    private String tag;
    private float mAniratio = 1;
    private ValueAnimator mValueAnimator = ValueAnimator.ofFloat(0, 1);
    ;
    private long DURATION = 700;
    //    private TimeInterpolator INTERPOLATOR = new BounceInterpolator();
    private TimeInterpolator INTERPOLATOR = new OvershootInterpolator(3);
    private float mHeightRatio = 1;

    public Jchart(float num, int color) {
        this(0, num, "", color);
    }

    public Jchart(float lower, float num, int color) {
        this(lower, lower + num, "", color);
    }

    public Jchart(float lower, float upper, String mXmsg) {
        this(lower, upper, mXmsg, Color.GRAY);
    }

    public Jchart(float lower, float upper, String mXmsg, int color) {
        mUpper = upper;
        mLower = lower;
        mHeight = mNum = upper - lower;
        mStart.y = 0;
        this.mColor = color;
        this.mXmsg = TextUtils.isEmpty(mXmsg) ? new DecimalFormat("##").format(mHeight) : mXmsg;
        mShowMsg = new DecimalFormat("##").format(mUpper);
    }

    public RectF getRectF() {
        float bottom = mStart.y - (mLower - mLowStart) * mHeightRatio * mAniratio;
        bottom = bottom < mStart.y ? bottom : mStart.y;
        float top = mStart.y - (mUpper - mLowStart) * mHeightRatio * mAniratio;
        top = top < mStart.y ? top : mStart.y;
        return new RectF(mStart.x, top, mStart.x + mWidth, bottom);
//        return new RectF(mStart.x, mStart.y - (mUpper - mLowStart) * mHeightRatio * mAniratio, mStart.x + mWidth, mStart.y - (mLower - mLowStart) * mHeightRatio * mAniratio);
    }

    /**
     * 柱子顶部中间的点坐标
     *
     * @return
     */
    public PointF getMidPointF() {
        float top = mStart.y - (mUpper - mLowStart) * mHeightRatio * mAniratio;
        top = top < mStart.y ? top : mStart.y;
        return new PointF(getMidX(), top);
    }


    public String getTextMsg() {
        return textMsg;
    }

    public void setTextMsg(String textMsg) {
        this.textMsg = textMsg;
    }


    public float getWidth() {
        return mWidth;
    }


    public void setWidth(float width) {
        this.mWidth = width;
    }


    public float getHeight() {
        //动画需要
        return mHeight * mHeightRatio;
    }


    public void setHeight(float height) {
        this.mHeight = height;
    }

    public float getHeightRatio() {
        return mHeightRatio;
    }

    public void setHeightRatio(float heightRatio) {
        mHeightRatio = heightRatio;
    }

    public PointF getStart() {
        return mStart;
    }


    public void setStart(PointF start) {
        this.mStart = start;
    }


    public float getMidX() {
        if (null != mStart) {
            mMidX = mStart.x + mWidth / 2;
        } else {
            throw new RuntimeException("mStart 不能为空");
        }
        return mMidX;
    }


    public void setMidX(float midX) {
        this.mMidX = midX;
    }


    public int getColor() {
        return mColor;
    }


    public void setColor(int color) {
        mColor = color;
    }


    public float getNum() {
        return mNum;
    }


    public void setNum(float num) {
        this.mNum = num;
    }


    public float getMax() {
        return mMax;
    }


    public void setMax(float max) {
        this.mMax = max;
    }


    public String getXmsg() {
        return mXmsg;
    }


    public void setXmsg(String xmsg) {
        this.mXmsg = xmsg;
    }


    public float getUpper() {
        return mUpper;
    }


    public void setUpper(float upper) {
        mUpper = upper;
        if (mUpper < mLower) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "setUpper error upper must < lower");
            }
            return;
        }
        if ("\\d+".matches(mXmsg)) {
            if (Float.parseFloat(mXmsg) == mHeight) {
                this.mXmsg = new DecimalFormat("##").format(mUpper - mLower);
            }
            mHeight = mUpper - mLower;
            mShowMsg = new DecimalFormat("##.#").format(mUpper);
        }
    }


    public float getLower() {
        return mLower;
    }


    public void setLower(float lower) {
        if (mLower == lower) {
            return;
        }
        mLower = lower;
        mHeight = mUpper - mLower;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getShowMsg() {
        return mShowMsg;
    }

    public void setShowMsg(String showMsg) {
        mShowMsg = showMsg;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public float getAniratio() {
        return mAniratio;
    }

    public void setAniratio(float aniratio) {
        mValueAnimator.cancel();
        mAniratio = aniratio;
    }

    public float getLowStart() {
        return mLowStart;
    }

    /**
     * 起点 默认0
     *
     * @param lowStart
     */
    public void setLowStart(float lowStart) {
        mLowStart = lowStart;
    }

    public Jchart aniHeight(final View view, float from, TimeInterpolator interpolator) {
        if (!mValueAnimator.isRunning() && mAniratio < 0.8) {
            mValueAnimator.setFloatValues(from, 1);
            mValueAnimator.setDuration(DURATION);
            mValueAnimator.setInterpolator(interpolator);
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mAniratio = (float) animation.getAnimatedValue();
                    view.postInvalidate();
                    setPercent(mAniratio);
                }
            });
            mValueAnimator.start();
        }
        return this;
    }

    public Jchart aniHeight(View view) {
        return aniHeight(view, 0, INTERPOLATOR);
    }

    public void draw(Canvas canvas, Paint paint, boolean point) {
        if (point) {
            canvas.drawPoint(getMidPointF().x, getMidPointF().y, paint);
        } else {
            canvas.drawRect(getRectF(), paint);
        }
    }

    public void draw(Canvas canvas, Paint paint, int radius) {
        canvas.drawRoundRect(getRectF(), radius, radius, paint);
    }
}
