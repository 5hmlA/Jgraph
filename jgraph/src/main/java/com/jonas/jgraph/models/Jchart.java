package com.jonas.jgraph.models;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

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

    private float mStandedHeight = 0;//标准高度
    private boolean mOp;
    private boolean mActualOp;
    private boolean mPathover;
    private Path mStantedRec = new Path();//标准矩阵 路径
    private Path mActualRec = new Path(); //实际矩阵路径
    private Path mOVerRec = new Path(); //超过部分矩阵路径

    private float mAniratio = 1;
    private ValueAnimator mValueAnimator = ValueAnimator.ofFloat(0, 1);
    ;
    private long DURATION = 700;
    //    private TimeInterpolator INTERPOLATOR = new BounceInterpolator();
    private TimeInterpolator INTERPOLATOR = new OvershootInterpolator(3);
    private float mHeightRatio = 1;
    public boolean mTopRound = true;
    //    private boolean mTopRound;

    public Jchart(float num, int color){
        this(0, num, "", color);
    }

    public Jchart(float lower, float num, int color){
        this(lower, lower+num, "", color);
    }

    public Jchart(float lower, float upper, String mXmsg){
        this(lower, upper, mXmsg, Color.GRAY);
    }

    public Jchart(float lower, float upper, String mXmsg, int color){
        mUpper = upper;
        mLower = lower;
        mHeight = mNum = upper-lower;
        mStart.y = 0;
        this.mColor = color;
        this.mXmsg = TextUtils.isEmpty(mXmsg) ? new DecimalFormat("##").format(mHeight) : mXmsg;
        mShowMsg = new DecimalFormat("##").format(mUpper);
    }

    public RectF getRectF(){
        float bottom = mStart.y-( mLower-mLowStart )*mHeightRatio*mAniratio;
        bottom = bottom<mStart.y ? bottom : mStart.y;
        float top = mStart.y-( mUpper-mLowStart )*mHeightRatio*mAniratio;
        top = top<mStart.y ? top : mStart.y;
        return new RectF(mStart.x, top, mStart.x+mWidth, bottom);
        //        return new RectF(mStart.x, mStart.y - (mUpper - mLowStart) * mHeightRatio * mAniratio, mStart.x + mWidth, mStart.y - (mLower - mLowStart) * mHeightRatio * mAniratio);
    }

    /**
     * 实际数据矩形
     *
     * @return
     */
    public Path getRectFPath(){
        //        if (mHeight > 0) {
        if(!mActualOp || mAniratio<1 && mHeight>0) {
            mActualRec = new Path();
            //            RectF rectF = getSecRectF(mHeight*mHeightRatio*mAniratio);
            //            RectF rectCircle = getFirstRectF(mHeight*mHeightRatio*mAniratio);

            //            RectF rectF = getSecRectF(mLower, mUpper, mAniratio);
            //            RectF rectCircle = getFirstRectF(mLower, mUpper, mAniratio);

            RectF[] helpRectFs = getHelpRectFs(mLower, mUpper, mAniratio);
            RectF rectF = helpRectFs[1];
            RectF rectCircle = helpRectFs[0];
            mActualOp = extraPath(mActualRec, mHeight*mHeightRatio*mAniratio, mActualOp, rectF, rectCircle);
        }
        return mActualRec;
    }

    public Path getRectFPath(float mLower, float mUpper){
        float mHeight = mUpper-mLower;
        if(!mActualOp || mAniratio<1 && mHeight>0) {
            mActualRec = new Path();
            //            RectF rectF = getSecRectF(mHeight*mHeightRatio*mAniratio);
            //            RectF rectCircle = getFirstRectF(mHeight*mHeightRatio*mAniratio);
            RectF rectF = getSecRectF(mLower, mUpper, mAniratio);
            RectF rectCircle = getFirstRectF(mLower, mUpper, mAniratio);
            mActualOp = extraPath(mActualRec, mHeight*mHeightRatio*mAniratio, mActualOp, rectF, rectCircle);
        }
        return mActualRec;
    }

    public RectF getSecRectF(float height){
        return new RectF(mStart.x, mStart.y-height+mWidth/2f, mStart.x+mWidth, mStart.y);
    }

    private RectF[] getHelpRectFs(float mLower, float mUpper, float mAniratio){
        RectF[] helpRectfs = new RectF[2];
        mAniratio = mAniratio>0.9 ? 1 : mAniratio;
        float bottom = mStart.y-( mLower-mLowStart )*mHeightRatio*mAniratio;
        bottom = bottom<mStart.y ? bottom : mStart.y;
        float top = mStart.y-( mUpper-mLowStart )*mHeightRatio*mAniratio+mWidth/2f;
        top = top<mStart.y ? top : mStart.y;
        helpRectfs[1] = new RectF(mStart.x, top, mStart.x+mWidth, bottom);
        helpRectfs[0] = new RectF(mStart.x, top-mWidth/2f, mStart.x+mWidth, top+mWidth/2f);
        return helpRectfs;
    }

    public RectF getSecRectF(float mLower, float mUpper, float mAniratio){
        mAniratio = mAniratio>0.9 ? 1 : mAniratio;
        float bottom = mStart.y-( mLower-mLowStart )*mHeightRatio*mAniratio;
        bottom = bottom<mStart.y ? bottom : mStart.y;
        float top = mStart.y-( mUpper-mLowStart )*mHeightRatio*mAniratio+mWidth/2f;
        top = top<mStart.y ? top : mStart.y;
        return new RectF(mStart.x, top, mStart.x+mWidth, bottom);
    }

    public RectF getFirstRectF(float height){
        return new RectF(mStart.x, mStart.y-height, mStart.x+mWidth, mStart.y-height+mWidth);//实际上 只取矩阵一半
    }

    public RectF getFirstRectF(float mLower, float mUpper, float mAniratio){
        mAniratio = mAniratio>0.9 ? 1 : mAniratio;
        float top = mStart.y-( mUpper-mLowStart )*mHeightRatio*mAniratio;
        top = top<mStart.y ? top : mStart.y;
        float bottom = top+mWidth*mAniratio;
        //        float bottom = mStart.y-( mUpper-mLowStart+mWidth )*mHeightRatio*mAniratio;
        //        bottom = bottom<mStart.y ? bottom : mStart.y;
        return new RectF(mStart.x, top, mStart.x+mWidth, bottom);
    }

    private boolean extraPath(Path mOVerRec, float mOverHeight, boolean op, RectF secRectF, RectF firstRectFC){
        if(mOverHeight>mWidth/2f) {
            Path rec = new Path();
            rec.moveTo(secRectF.left, secRectF.top);
            rec.lineTo(secRectF.left, secRectF.bottom);
            rec.lineTo(secRectF.right, secRectF.bottom);
            rec.lineTo(secRectF.right, secRectF.top);
            mOVerRec.addPath(rec);
            mOVerRec.addArc(firstRectFC, 180, 180);

            //使用op
            //            mOVerRec.addRect(secRectF, Path.Direction.CCW);
            //                circle.addCircle(mStart.x + mWidth / 2f, mStart.y - mHeight + mWidth / 2f, mWidth / 2f, Path.Direction.CCW);
            //                mOVerRec.op(circle, Path.Op.UNION);
            //            Path circle = new Path();
            //            circle.addArc(firstRectFC, 180, 180);
            //            return mOVerRec.op(circle, Path.Op.UNION);
            return true;
        }else {
            firstRectFC.bottom -= ( mWidth-2*mOverHeight );
            mOVerRec.addArc(firstRectFC, 180, 180);
            mOVerRec.close();
            return true;
        }
    }

    /**
     * 超出部分数据矩形
     *
     * @return
     */
    public RectF getOverRectF(){
        float mOverHeight = ( mHeight-mStandedHeight )*mHeightRatio*mAniratio;

        mOverHeight = mOverHeight>0 ? mOverHeight : 0;
        if(mOverHeight>0) {
            return new RectF(mStart.x, mStart.y-mHeight*mHeightRatio*mAniratio, mStart.x+mWidth,
                    mStart.y-mStandedHeight*mHeightRatio*mAniratio);
        }else {
            return new RectF(0, 0, 0, 0);
        }
    }

    /**
     * 超出部分数据矩形
     *
     * @return
     */
    public Path getOverRectFPath(){
        float mOverHeight = ( mHeight-mStandedHeight )*mHeightRatio*mAniratio;
        if(!mPathover || mAniratio<1 && mOverHeight>0) {
            //            mOVerRec = new Path();
            //            RectF[] helpRectFs = getHelpRectFs(mLower, mUpper, mAniratio);
            //            RectF rectF = helpRectFs[1];
            //            RectF rectCircle = helpRectFs[0];

            RectF rectF = new RectF(mStart.x, mStart.y-mHeight*mHeightRatio*mAniratio+mWidth/2f, mStart.x+mWidth,
                    mStart.y-mStandedHeight*mHeightRatio*mAniratio);
            RectF rectCircle = getFirstRectF(mHeight);

            mPathover = extraPath(mOVerRec, mOverHeight, false, rectF, rectCircle);

        }
        return mOVerRec;
    }

    /**
     * 标准数据矩形
     *
     * @return
     */
    public RectF getStandedRectF(){
        return new RectF(mStart.x, mStart.y-mStandedHeight*mHeightRatio, mStart.x+mWidth, mStart.y);
    }

    /**
     * 标准数据矩形
     *
     * @return
     */
    public Path getStandedPath(){
        //        if (mStandedHeight > 0) {
        if(!mOp || mAniratio<1 && mStandedHeight>0) {
            mStantedRec = new Path();
            RectF rectF = getSecRectF(mStandedHeight*mHeightRatio);
            RectF rectCircle = getFirstRectF(mStandedHeight*mHeightRatio);
            mOp = extraPath(mStantedRec, mStandedHeight*mHeightRatio, mOp, rectF, rectCircle);
        }
        return mStantedRec;
    }

    /**
     * 柱子顶部中间的点坐标
     *
     * @return
     */
    public PointF getMidPointF(){
        float top = mStart.y-( mUpper-mLowStart )*mHeightRatio*mAniratio;
        top = top<mStart.y ? top : mStart.y;
        return new PointF(getMidX(), top);
    }


    public String getTextMsg(){
        return textMsg;
    }

    public Jchart setTextMsg(String textMsg){
        this.textMsg = textMsg;
        return this;
    }


    public float getWidth(){
        return mWidth;
    }


    public Jchart setWidth(float width){
        this.mWidth = width;
        return this;
    }


    public float getHeight(){
        //动画需要
        return mHeight*mHeightRatio;
    }


    public Jchart setHeight(float height){
        this.mHeight = height>0 ? height : 0;
        if(mHeight+mLower != mUpper) {
            setUpper(mHeight+mLower);
        }
        openRpath();
        return this;
    }

    public float getHeightRatio(){
        return mHeightRatio;
    }

    public Jchart setHeightRatio(float heightRatio){
        mHeightRatio = heightRatio;
        openRpath();
        return this;
    }

    public PointF getStart(){
        return mStart;
    }


    public Jchart setStart(PointF start){
        this.mStart = start;
        return this;
    }


    public float getMidX(){
        if(null != mStart) {
            mMidX = mStart.x+mWidth/2;
        }else {
            throw new RuntimeException("mStart 不能为空");
        }
        return mMidX;
    }


    public Jchart setMidX(float midX){
        this.mMidX = midX;
        return this;
    }


    public int getColor(){
        return mColor;
    }


    public Jchart setColor(int color){
        mColor = color;
        return this;
    }


    public float getNum(){
        return mNum;
    }


    public Jchart setNum(float num){
        this.mNum = num;
        return this;
    }


    public float getMax(){
        return mMax;
    }


    public Jchart setMax(float max){
        this.mMax = max;
        return this;
    }


    public String getXmsg(){
        return mXmsg;
    }


    public Jchart setXmsg(String xmsg){
        this.mXmsg = xmsg;
        return this;
    }


    public float getUpper(){
        return mUpper;
    }

    /**
     * lower不变
     *
     * @param upper
     */
    public Jchart setUpper(float upper){
        if(upper<mLower) {
            //            if(BuildConfig.DEBUG) {//一直为false
            //                Log.e(TAG, "setUpper error upper must < lower");
            //            }
            upper = mLower;
            Log.e(TAG, "lower > upper than lower = upper = "+mUpper);
        }
        mUpper = upper;
        mHeight = mUpper-mLower;
        if("\\d+".matches(mXmsg)) {
            if(Float.parseFloat(mXmsg) == mHeight) {
                this.mXmsg = new DecimalFormat("##").format(mUpper-mLower);
            }
            mShowMsg = new DecimalFormat("##.#").format(mUpper);
        }
        openRpath();
        return this;
    }

    public float getLower(){
        return mLower;
    }

    /**
     * 高 不变
     *
     * @param lower
     */
    public Jchart setLower2(float lower){
        if(mLower == lower) {
            return this;
        }
        mLower = lower;
        setUpper(mHeight+mLower);
        openRpath();
        return this;
    }

    /**
     * upper不变
     *
     * @param lower
     */
    public Jchart setLower(float lower){
        if(mLower == lower) {
            return this;
        }
        if(lower>mUpper) {
            Log.e(TAG, "lower > upper than lower = upper = "+mUpper);
            lower = mUpper;
        }
        openRpath();
        mLower = lower;
        setHeight(mUpper-mLower);
        return this;
    }

    public int getIndex(){
        return index;
    }

    public Jchart setIndex(int index){
        this.index = index;
        return this;
    }

    public String getShowMsg(){
        return mShowMsg;
    }

    public Jchart setShowMsg(String showMsg){
        mShowMsg = showMsg;
        return this;
    }


    @Override
    public Object clone(){
        Jchart clone = null;
        try {
            clone = (Jchart)super.clone();
            //以下对象 不支持克隆
            clone.mStart = new PointF(mStart.x, mStart.y);
            clone.mStantedRec = new Path(mStantedRec);//标准矩阵 路径
            clone.mActualRec = new Path(mActualRec); //实际矩阵路径
            clone.mOVerRec = new Path(mOVerRec); //超过部分矩阵路径
        }catch(CloneNotSupportedException e) {
            Log.e(TAG, "克隆失败 ");
        }
        return clone;
    }

    public String getTag(){
        return tag;
    }

    public Jchart setTag(String tag){
        this.tag = tag;
        return this;
    }

    public float getPercent(){
        return percent;
    }

    public Jchart setPercent(float percent){
        this.percent = percent;
        return this;
    }

    public float getAniratio(){
        return mAniratio;
    }

    public Jchart setAniratio(float aniratio){
        mValueAnimator.cancel();
        mAniratio = aniratio;
        return this;
    }

    public float getLowStart(){
        return mLowStart;
    }

    /**
     * 起点 默认0
     *
     * @param lowStart
     */
    public Jchart setLowStart(float lowStart){
        mLowStart = lowStart;
        return this;
    }

    public Jchart aniHeight(final View view, float from, TimeInterpolator interpolator){
        if(!mValueAnimator.isRunning() && mAniratio<0.8) {
            mValueAnimator.setFloatValues(from, 1);
            mValueAnimator.setDuration(DURATION);
            mValueAnimator.setInterpolator(interpolator);
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation){
                    mAniratio = (float)animation.getAnimatedValue();
                    view.postInvalidate();
                    setPercent(mAniratio);
                }
            });
            mValueAnimator.start();
        }
        return this;
    }

    public Jchart aniHeight(View view){
        return aniHeight(view, 0, INTERPOLATOR);
    }

    public Jchart draw(Canvas canvas, Paint paint, boolean point){
        if(point) {
            canvas.drawPoint(getMidPointF().x, getMidPointF().y, paint);
        }else {
            if(mTopRound) {
                canvas.drawPath(getRectFPath(), paint);
            }else {
                canvas.drawRect(getRectF(), paint);
            }
        }
        return this;
    }

    public Jchart draw(Canvas canvas, Paint paint, int radius){
        if(mTopRound) {
            canvas.drawPath(getRectFPath(), paint);
        }else {
            canvas.drawRoundRect(getRectF(), radius, radius, paint);
        }
        return this;
    }

    public void setTopRound(boolean topRound){
        this.mTopRound = topRound;
    }

    public float getTopest(){
        return mUpper>mStandedHeight ? mUpper : mStandedHeight;
    }

    public void setStandedHeight(float standedHeight){
        mStandedHeight = standedHeight;
    }

    private void openRpath(){
        mOp = false;
        mActualOp = false;
        mPathover = false;
    }
}
