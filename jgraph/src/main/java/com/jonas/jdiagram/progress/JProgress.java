package com.jonas.jdiagram.progress;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
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
import android.view.animation.DecelerateInterpolator;

import com.jonas.jdiagram.R;
import com.jonas.jdiagram.utils.DisplayUtils;

import java.text.DecimalFormat;

/**
 * Created by jiangzuyun on 2015/9/2.
 */
public class JProgress extends View {

    private Paint mPaint;
    private int mTextToRec = DisplayUtils.dip2px(getContext(), 4f);
    private int mHeight;
    private int mWidth;
    private String mUnit = "M";
    private int msgColor = Color.BLACK;
    private float Max = 100;
    private float current = 100;
    private int mRecColor = Color.RED;
    private float recRound;
    /**
     * 画进度条 文字 需要的 动画变量
     */
    private float progressAni = 1;
    /**
     * float转 string的格式化工具 去掉小数点末尾的0
     */
    private DecimalFormat format = new DecimalFormat("##.##");
    //矩形进度的背景颜色
    private int backColor;
    //进度条需要的矩形
    private RectF mrect = new RectF();
    //获取字体的长宽
    private Rect bounds = new Rect();
    /**
     * msg的显示模式  0 层次  1 排列
     */
    private int msgMode;
    private long ANIMATEDURATION = 2000;
    private Paint textPaint;
    private boolean getLine;
    private float k;
    private float b;
    private ObjectAnimator mProgressAanimator = new ObjectAnimator();
    private TimeInterpolator mInterpolator = new DecelerateInterpolator();
    //    private TimeInterpolator mInterpolator = OvershootInterpolator;
    /**
     * 多个msg(进度数字)中 最长的长度 只适用在 并排模式下
     */
    private float bigmsgLength;
    private int mjust;
    private String msg;

    //    private
    public JProgress(Context context){
        this(context, null);
    }

    public JProgress(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public JProgress(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        if(attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JProgress, defStyle, 0);
            mRecColor = typedArray.getColor(R.styleable.JProgress_recColor, Color.RED);
            msgColor = typedArray.getColor(R.styleable.JProgress_msgColor, Color.BLACK);
            backColor = typedArray.getColor(R.styleable.JProgress_backColor, Color.TRANSPARENT);
            mUnit = typedArray.getString(R.styleable.JProgress_unit);
            recRound = typedArray.getDimension(R.styleable.JProgress_recRound, 5);
            current = typedArray.getFloat(R.styleable.JProgress_currprogress, 50);
            Max = typedArray.getFloat(R.styleable.JProgress_maxprogress, 100);
            msgMode = typedArray.getInt(R.styleable.JProgress_msgMode, 0);
            typedArray.recycle();
        }
        init();
    }

    private void init(){

        mUnit = TextUtils.isEmpty(mUnit) ? "" : mUnit;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        msg = format.format(current)+mUnit;
        //处理大于100%的情况
        current = current>Max ? Max : current;

        mProgressAanimator.setTarget(this);
        mProgressAanimator.setPropertyName("progressAni");
        mProgressAanimator.setFloatValues(0, 1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        mHeight = getHeight();
        mjust = mWidth>mHeight ? mHeight : mWidth;

        recRound = recRound<mjust/2 ? recRound : mjust/2;
        mTextToRec = mTextToRec<recRound ? (int)recRound : mTextToRec;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        textPaint.setColor(msgColor);
        textPaint.setTextSize(mHeight);

        textPaint.getTextBounds(msg, 0, msg.length(), bounds);

        //当前进度msg
        float msgLength = textPaint.measureText(msg);

        //并排模式下：最长的进度条长度 = 控件长度 - 最长的msg文字的长度
        float recLenth = msgMode == 0 ? mWidth : mWidth-textPaint
                .measureText(format.format(bigmsgLength == 0 ? Max : bigmsgLength)+mUnit)-mTextToRec;

        // 画进度条的背景
        mrect.set(0, 0, recLenth, mHeight);
        mPaint.setColor(backColor);
        canvas.drawRoundRect(mrect, recRound, recRound, mPaint);

        //画进度条
        mrect.set(0, 0, current*progressAni/Max*recLenth, mHeight);
        mPaint.setColor(mRecColor);
        canvas.drawRoundRect(mrect, recRound, recRound, mPaint);

        //画文字
        if(msgMode == 1) {//并排
            //计算出 最长的长度
            canvas.drawText(msg, current*progressAni/Max*recLenth+mTextToRec, mHeight/2+bounds.height()/2, textPaint);
        }else {//层叠 最长进度就是控件长度
            float t = progressAni*current/Max*recLenth+msgLength+mTextToRec;//当前msg末尾进度
            if(t<=mWidth) {
                canvas.drawText(msg, t-msgLength, mHeight/2+bounds.height()/2, textPaint);
            }else {
                if(!getLine) {
                    //msg末尾进度的直线方程
                    PointF msgEnd = new PointF();
                    PointF progressEnd = new PointF();
                    msgEnd.x = ( recLenth-msgLength-mTextToRec )/current*Max/recLenth;
                    msgEnd.y = recLenth;
                    progressEnd.x = 1;
                    progressEnd.y = current/Max*recLenth;
                    //y=k*x+b
                    k = ( progressEnd.y-msgEnd.y )/( progressEnd.x-msgEnd.x );
                    b = -k*progressEnd.x+progressEnd.y;
                    getLine = true;
                }
                canvas.drawText(msg, k*progressAni+b-msgLength-mTextToRec, mHeight/2+bounds.height()/2, textPaint);
            }
        }

    }

    //==========================================一系列的get  set 方法=================================================================
    public float getMax(){
        return Max;
    }

    public void setMax(float max){
        Max = max;
    }

    public float getCurrent(){
        return current;
    }

    /**
     * 设置百分百进度
     * @param current
     */
    public void setCurrentPercent(float current){
        DecimalFormat format = new DecimalFormat("##.##%");
        msg = format.format(current/Max);
        this.current = current>Max ? Max : current;
        getLine = false;
    }
     public void setCurrentPercentAni(float current){
         setCurrentPercent(current);
         animateShow();
     }

    public void setCurrent(float current){
        msg = format.format(current)+mUnit;
        //处理大于100%的情况
        this.current = current>Max ? Max : current;
        getLine = false;//从新设置进度 需要重置getLine 重新计算msg的运动轨迹
    }

    public void setCurrentAni(float current){
        msg = format.format(current)+mUnit;
        //处理大于100%的情况
        this.current = current>Max ? Max : current;
        getLine = false;//从新设置进度 需要重置getLine 重新计算msg的运动轨迹
        animateShow(mInterpolator);
    }

    public float getProgressAni(){
        return progressAni;
    }

    public void setProgressAni(float progressAni){
        this.progressAni = progressAni;
        postInvalidate();
    }

    /**
     * 以动画形式 展示
     */
    public void animateShow(){
        //        mProgressAanimator.setDuration(ANIMATEDURATION);
        //        mProgressAanimator.setInterpolator(new OvershootInterpolator());
        //        mProgressAanimator.cancel();
        //        mProgressAanimator.start();
        animateShow(mInterpolator);
    }

    /**
     * 以动画形式 展示 自定义 加速器
     */
    public void animateShow(TimeInterpolator interpolator){
        mProgressAanimator.setDuration(ANIMATEDURATION);
        mProgressAanimator.setInterpolator(interpolator);
        mProgressAanimator.cancel();
        mProgressAanimator.start();
    }

    /**
     * 设置自定义加速器
     *
     * @param interpolator
     */
    public void setInterpolator(TimeInterpolator interpolator){
        mInterpolator = interpolator;
    }

    /**
     * 设置进度条动画 执行时间
     *
     * @param ANIMATEDURATION
     */
    public void setANIMATEDURATION(long ANIMATEDURATION){
        this.ANIMATEDURATION = ANIMATEDURATION;
    }


    /**
     * 设置 文字与进度条的距离
     * 单位：dp
     *
     * @param textToRec
     */
    public void setTextToRec(int textToRec){
        mTextToRec = DisplayUtils.dip2px(getContext(), textToRec);
        mTextToRec = mTextToRec<recRound ? (int)recRound : mTextToRec;
        getLine = false;//从新设置文字与进度条的距离 需要重置getLine  重新计算msg的运动轨迹
    }

    public int getMsgMode(){
        return msgMode;
    }

    /**
     * 设置 msg的显示模式
     * 1 并排
     * 0 层叠
     *
     * @param msgMode
     */
    public void setMsgMode(int msgMode){
        this.msgMode = msgMode;
    }

    /**
     * 设置进度条的矩形的圆角半径
     *
     * @param recRound
     */
    public void setRecRound(float recRound){
        this.recRound = recRound<mjust/2 ? recRound : mjust/2;
    }

    /**
     * 在并排模式下 同时 有多个进度条相比较
     * 需要设置最长的msg 比如：22，98.99，100 此时应该设置98.99
     * 为的是保证每条进度展示的总长度max相同
     * 默认：进度展示的总长度为Max的长度
     *
     * @param bigmsgLength
     */
    public void setBigmsgLength(float bigmsgLength){
        this.bigmsgLength = bigmsgLength;
    }

    /**
     * 设置msg的颜色
     *
     * @param msgColor
     */
    public void setMsgColor(int msgColor){
        this.msgColor = msgColor;
    }

    /**
     * 设置进度条的颜色
     *
     * @param recColor
     */
    public void setRecColor(int recColor){
        mRecColor = recColor;
    }

    /**
     * 设置进度条的背景颜色 默认透明
     *
     * @param backColor
     */
    public void setProgressBackGroundColor(int backColor){
        this.backColor = backColor;
    }
}
