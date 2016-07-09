package com.jonas.jdiagram.models;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import java.text.DecimalFormat;

/**
 * @author jwx338756.
 * @date 2016/1/27
 * @des [添加 虚线]
 * @since [产品/模版版本]
 */
public class Jchart implements Cloneable {
    private String mShowMsg;
    private int index; //序号
    private float mWidth;//柱状 的 宽
    private float mHeight;//折线的y 画图的时候会被缩放
    private PointF mStart = new PointF();//矩形左下角起点
    private float mMidX;//中点 折线的x
    private int mColor;
    private float mNum; //当前数字
    private float mMax; //总数据
    private float percent;//占比
    private String textMsg; //要显示的信息
    private String mXmsg; //横坐标信息
    private float mUpper;
    private float mLower;
    private String tag;
    private float mAniratio = 1;
    private ValueAnimator mValueAnimator = ValueAnimator.ofFloat(0, 1);
    ;
    private long DURATION = 1000;
//    private TimeInterpolator INTERPOLATOR = new BounceInterpolator();
    private TimeInterpolator INTERPOLATOR = new OvershootInterpolator(3);

    public Jchart(float num, int color) {
        this(0, num, "", color);
    }

    public Jchart(float lower, float upper, String mXmsg) {
        this(lower, upper, mXmsg, Color.GRAY);
    }

    public Jchart(float lower, float upper, String mXmsg, int color) {
        mUpper = upper;
        mLower = lower;
        mHeight = mNum = upper - lower;
        mStart.y = mLower;
        this.mColor = color;
        this.mXmsg = TextUtils.isEmpty(mXmsg) ? new DecimalFormat("##").format(mHeight) : mXmsg;
        mShowMsg = new DecimalFormat("##").format(mHeight);
    }

    public RectF getRectF() {
        return new RectF(mStart.x, mStart.y - mHeight * mAniratio, mStart.x + mWidth, mStart.y);
    }

    /**
     * 柱子顶部中间的点坐标
     *
     * @return
     */
    public PointF getMidPointF() {
        return new PointF(getMidX(), mStart.y - mHeight * mAniratio);
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
        return mHeight;
    }


    public void setHeight(float height) {
        this.mHeight = height;
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
        mHeight = mUpper - mLower;
        mShowMsg = new DecimalFormat("##.#").format(mUpper);
    }


    public float getLower() {
        return mLower;
    }


    public void setLower(float lower) {
        mLower = lower;
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

    public Jchart aniHeight(final View view) {
        mValueAnimator.setDuration(DURATION);
        mValueAnimator.setInterpolator(INTERPOLATOR);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAniratio = (float) animation.getAnimatedValue();
                view.postInvalidate();
            }
        });
        if (!mValueAnimator.isRunning() && mAniratio < 0.8) {
            mValueAnimator.start();
        }
        return this;
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
