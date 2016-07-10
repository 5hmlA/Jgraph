package com.jonas.jdiagram.progress;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 *
 */
public class ChargeView extends Progress {

    private Paint mPaint;
    private int height;
    private int width;
    private float sweepAngle = 0;
    private float startAngle = -90;
    private int ringColor = Color.parseColor("#ED9E07");
    private int ringWidth = 15;
    private int ringPading = 0;
    private int boxRadius = ringWidth/2;
    private int boxColor = ringColor;
    private int roundColor = Color.YELLOW;
    private int textColor = Color.GREEN;
    /**
     * 圆环 内字 的内容
     */
    private float centerText ;
    /**
     * 圆环 内字 的大小
     */
    private int centerTextSize = 35;
    private float sweepAngleTemp;
    private double mSweepHudufinish;
    private float progress = 1;
    private float mRadius;
    private RectF oval;
    private PointF mCPoint;
    private Path mOuterPath;
    private int mJust;
    private Paint mBgPaint;
    private long next;
    private long ANIDURATION = 2500;
    private ObjectAnimator mProgress = new ObjectAnimator();
    public boolean showRing = true;
    private boolean showRingProgress = true;

    {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ringWidth = boxRadius*2;
    }

    public ChargeView(Context context){
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ChargeView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth();
        height = getHeight();
        mJust = width>height ? height : width;
        mCPoint = new PointF(width/2f, height/2f);
        float wRadius = mJust/2-ringPading;
        float nRadius = wRadius-boxRadius*2;
        mRadius = mJust/2-ringPading-ringWidth/2f;
        oval = new RectF(mCPoint.x-( mJust/2-ringPading ), mCPoint.y-( mJust/2-ringPading ), mCPoint.x+mJust/2-ringPading,
                mCPoint.y+mJust/2-ringPading);
        mOuterPath = new Path();
        mOuterPath.addCircle(mCPoint.x, mCPoint.y, mJust/2-ringPading, Path.Direction.CW);
        mOuterPath.addCircle(mCPoint.x, mCPoint.y, mJust/2-ringPading-ringWidth, Path.Direction.CW);
        mOuterPath.setFillType(Path.FillType.EVEN_ODD);
//        sweepAngleTemp = startAngle;
    }

    private double toRadian(float angle){
        angle = angle+360-360*(int)( ( angle+360 )/360 );
        return angle/180f*Math.PI;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        canvas.save();//保存之前的画布

        canvas.clipPath(mOuterPath);//剪切出圆环

        PointF mCircleStart = new PointF();
        mCircleStart.x = (float)( mRadius*Math.cos(toRadian(startAngle)) )+mCPoint.x;
        mCircleStart.y = (float)( mRadius*Math.sin(toRadian(startAngle)) )+mCPoint.y;
        mPaint.setColor(boxColor);
        canvas.drawCircle(mCircleStart.x, mCircleStart.y, boxRadius, mPaint);
        if(360*progress<=sweepAngleTemp) {
            sweepAngle = 360*progress;
        }else {
            float mSweepHudu = (float)( toRadian(( 360*progress+startAngle )) );
            mCircleStart.x = (float)( mRadius*Math.cos(mSweepHudu) )+mCPoint.x;
            mCircleStart.y = (float)( mRadius*Math.sin(mSweepHudu) )+mCPoint.y;
            mPaint.setColor(boxColor);
            canvas.drawCircle(mCircleStart.x, mCircleStart.y, boxRadius, mPaint);
            //            if(Math.abs(mSweepHudu-mSweepHudufinish)>=1) {
            //                mPaint.setColor(Color.RED);
            //                float mSweepHudu2 = mSweepHudu-1;
            //                mCircleFinish.x = (float)( mRadius*Math.cos(mSweepHudu2) )+mCPoint.x;
            //                mCircleFinish.y = (float)( mRadius*Math.sin(mSweepHudu2) )+mCPoint.y;
            //                //            mPaint.setColor(boxColor);
            //                canvas.drawCircle(mCircleFinish.x, mCircleFinish.y, boxRadius, mPaint);
            //            }
        }
        if(showRing) {
            drawProgress(canvas);
        }

        canvas.restore();//恢复之前的画布

//        drawProgressText(canvas);
    }

    private void drawProgress(Canvas canvas){
        if(showRingProgress) {
            mPaint.setColor(ringColor);
            canvas.drawArc(oval, startAngle, sweepAngle, true, mPaint);
        }
        //        mBgPaint.setColor(Color.WHITE);
        //        canvas.drawCircle(mCPoint.x, mCPoint.y, mJust/2-ringWidth-ringPading, mBgPaint);
        mPaint.setColor(boxColor);
        double mSweepHudu = toRadian(sweepAngle+startAngle);
        PointF mCircleP = new PointF();
        mCircleP.x = (float)( mRadius*Math.cos(mSweepHudu) )+mCPoint.x;
        mCircleP.y = (float)( mRadius*Math.sin(mSweepHudu) )+mCPoint.y;
        canvas.drawCircle(mCircleP.x, mCircleP.y, boxRadius, mPaint);
    }

    private void drawProgressText(Canvas canvas){
//        String curStr = numFormat.format(centerText*progress);
        String curStr = centerText*progress+"";
        if(TextUtils.isEmpty(curStr)) {
            //如过 文字为空的话 就不画
            return;
        }
        // 圆环中心 写字
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(centerTextSize);
        // mpaint.setTextSize(55);
        // C 语言 的思想
        Rect bounds = new Rect();
        mPaint.getTextBounds(curStr, 0, curStr.length(), bounds);
        // 获取 所画的字的宽和高
        // mpaint.measureText(text);//返回的是字的宽度
        int textWidth = bounds.width();
        int textHeight = bounds.height();

        canvas.drawText(curStr, width/2-textWidth/2, height/2+textHeight/2, mPaint);
    }

    public void setAniSweepAngle(float sweepAngle){
        centerText = this.sweepAngle = sweepAngleTemp = sweepAngle;
        mProgress.cancel();
        mProgress = ObjectAnimator.ofFloat(this, "progress", 0, 1).setDuration(ANIDURATION);
        mProgress.setInterpolator(new AccelerateDecelerateInterpolator());
        mProgress.start();

    }

    public void setAniSweepAngle(float sweepAngle, long delay){
        centerText = this.sweepAngle = sweepAngleTemp = sweepAngle;
        mProgress.cancel();
        mProgress = ObjectAnimator.ofFloat(this, "progress", 0, 1).setDuration(ANIDURATION);
        mProgress.setInterpolator(new AccelerateDecelerateInterpolator());
        mProgress.setStartDelay(delay);
        mProgress.start();

    }

    public void setAniSweepAngle(float startAngle, float sweepAngle){
        this.startAngle = sweepAngleTemp = startAngle;
        ObjectAnimator.ofFloat(this, "progress", 0, 1).setDuration(ANIDURATION).start();
    }

    public float getSweepAngle(){
        return sweepAngle;
    }

    public void setSweepAngle(float sweepAngle){
        this.sweepAngle = sweepAngle;
        postInvalidate();
    }

    /**
     * @return 圆环的颜色
     */
    public int getRingColor(){
        return ringColor;
    }

    /**
     * @param ringColor
     *         圆环的颜色
     */
    public void setRingColor(int ringColor){
        this.ringColor = ringColor;
    }

    /**
     * @return 圆环的 环半径
     */
    public int getRingWidth(){
        return ringWidth;
    }

    /**
     * @return 圆环的 环半径
     */
    public void setRingWidth(int ringWidth){
        this.ringWidth = ringWidth;
    }

    /**
     * @return 圆环 局周围的 边距
     */
    public int getRingPading(){
        return ringPading;
    }

    /**
     * @return 圆环 局周围的 边距
     */
    public void setRingPading(int ringPading){
        this.ringPading = ringPading;
    }

    /**
     * @return 滚动小球的半径
     */
    public int getBoxRadius(){
        return boxRadius;
    }

    /**
     * @return 滚动小球的半径
     */
    public void setBoxRadius(int boxRadius){
        this.boxRadius = boxRadius;
    }

    /**
     * @return 滚动小球的颜色
     */
    public int getBoxColor(){
        return boxColor;
    }

    /**
     * @return 滚动小球的颜色
     */
    public void setBoxColor(int boxColor){
        this.boxColor = boxColor;
    }

    /**
     * @return 内圆的颜色 圆环背景色
     */
    public int getRoundColor(){
        return roundColor;
    }

    /**
     * @return 内圆的颜色 圆环背景色
     */
    public void setRoundColor(int roundColor){
        this.roundColor = roundColor;
    }

    /**
     * @return 文字的颜色
     */
    public int getTextColor(){
        return textColor;
    }

    /**
     * @return 文字的颜色
     */
    public void setTextColor(int textColor){
        this.textColor = textColor;
    }

//    /**
//     * @return 文字的内容
//     */
//    public String getCenterText(){
//        return centerText;
//    }
//
//    /**
//     * @return 文字的内容，当内容为空时 不显示 默认不显示
//     */
//    public void setCenterText(String centerText){
//        this.centerText = centerText;
//    }

    /**
     * @return 文字的大小
     */
    public int getCenterTextSize(){
        return centerTextSize;
    }

    /**
     * @return 文字的大小
     */
    public void setCenterTextSize(int centerTextSize){
        this.centerTextSize = centerTextSize;
    }

    public float getProgress(){
        return progress;
    }

    public void setProgress(float progress){
        this.progress = progress;
        postInvalidate();
    }
}
