package com.jonas.schart.chartbean;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;

import java.text.DecimalFormat;

/**
 * @author jwx338756.
 * @date 2016/1/27
 * @des [添加 虚线]
 * @since [产品/模版版本]
 */
public class SugExcel implements Cloneable {
    private String mShowMsg;
    private int index;
    private float mWidth;//柱状 的 宽
    private float mHeight;//折线的y
    private PointF mStart = new PointF();//矩形左下角起点
    private float mMidX;//中点 折线的x
    private int mColor;
    private float mNum; //当前数字
    private float mMax; //总数据
    private String textMsg; //要显示的信息
    private String mXmsg; //横坐标信息
    private float mUpper;
    private float mLower;
    /**
     * 单位
     */
    private String unit;

    public SugExcel(float num, String mXmsg, int index) {
        this(0, num, mXmsg);
        this.index = index;
    }

    public SugExcel(float num, String mXmsg) {
        this(0, num, mXmsg);
    }

    public SugExcel(float num, int color) {
        this(0, num, "", "", color);
    }

    public SugExcel(float lower, float upper, String mXmsg) {
        this(lower, upper, mXmsg, Color.GRAY);
    }


    public SugExcel(float lower, float upper, String mXmsg, int color) {
        this(lower, upper, "", mXmsg, Color.GRAY);
    }


    public SugExcel(float num, String unit, String mXmsg) {
        this(0, num, unit, mXmsg, Color.GRAY);
    }


    public SugExcel(float lower, float upper, String unit, String mXmsg, int color) {
        mUpper = upper;
        mLower = lower;
        mHeight = mNum = upper - lower;
        mStart.y = mLower;
        this.unit = unit;
        this.mColor = color;
        this.mXmsg = TextUtils.isEmpty(mXmsg) ? new DecimalFormat("##").format(mHeight) : mXmsg;
        mShowMsg = new DecimalFormat("##").format(mHeight);
    }

    public RectF getRectF() {
        return new RectF(mStart.x, mStart.y - mHeight, mStart.x + mWidth, mStart.y);
    }


    public PointF getMidPointF() {
        return new PointF(getMidX(), mStart.y - mHeight);
    }


    public String getTextMsg() {
        return textMsg;
    }


    public String getUnit() {
        return unit;
    }


    public void setUnit(String unit) {
        this.unit = unit;
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

}
