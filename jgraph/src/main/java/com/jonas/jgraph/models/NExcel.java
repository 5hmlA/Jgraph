package com.jonas.jgraph.models;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * @author yun.
 * @date 2016/6/8
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class NExcel {
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

    public NExcel(float num, String mXmsg){
        this(0, num, mXmsg);
    }

    public NExcel(float lower, float upper, String mXmsg){
        this(lower, upper, mXmsg, Color.GRAY);
    }

    public NExcel(float lower, float upper, String mXmsg, int color){
        this(lower, upper, "", mXmsg, Color.GRAY);
    }


    public NExcel(float num, String unit, String mXmsg){
        this(0, num, unit, mXmsg, Color.GRAY);
    }

    public NExcel(float lower, float upper, String unit, String mXmsg, int color){
        mUpper = upper;
        mLower = lower;
        mHeight = mNum = upper-lower;
        mStart.y = mLower;
        this.mXmsg = mXmsg;
        this.unit = unit;
        this.mColor = color;
    }

    public RectF getRectF(){
        return new RectF(mStart.x, mStart.y-mHeight, mStart.x+mWidth, mStart.y);
    }

    public PointF getMidPointF(){
        return new PointF(getMidX(), mStart.y-mHeight);
    }

    public String getTextMsg(){
        return textMsg;
    }

    public String getUnit(){
        return unit;
    }

    public void setUnit(String unit){
        this.unit = unit;
    }

    public void setTextMsg(String textMsg){
        this.textMsg = textMsg;
    }

    public float getWidth(){
        return mWidth;
    }

    public void setWidth(float width){
        this.mWidth = width;
    }

    public float getHeight(){
        return mHeight;
    }

    public void setHeight(float height){
        this.mHeight = height;
    }

    public PointF getStart(){
        return mStart;
    }

    public void setStart(PointF start){
        this.mStart = start;
    }

    public float getMidX(){
        if(null != mStart) {
            mMidX = mStart.x+mWidth/2;
        }else {
            throw new RuntimeException("mStart 不能为空");
        }
        return mMidX;
    }

    public void setMidX(float midX){
        this.mMidX = midX;
    }

    public int getColor(){
        return mColor;
    }

    public void setColor(int color){
        mColor = color;
    }

    public float getNum(){
        return mNum;
    }

    public void setNum(float num){
        this.mNum = num;
    }

    public float getMax(){
        return mMax;
    }

    public void setMax(float max){
        this.mMax = max;
    }

    public String getXmsg(){
        return mXmsg;
    }

    public void setXmsg(String xmsg){
        this.mXmsg = xmsg;
    }

    public float getUpper(){
        return mUpper;
    }

    public void setUpper(float upper){
        mUpper = upper;
    }

    public float getLower(){
        return mLower;
    }

    public void setLower(float lower){
        mLower = lower;
    }
}
