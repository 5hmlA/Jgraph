package com.jonas.jdiagram.progress;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.text.DecimalFormat;

/**
 * @author yun.
 * @date 2015/10/7
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public class Progress extends View{

    /**
     * 动画持续时间
     */
    private long ANIDURATION;
    /**
     * 动画使用的加速器
     */
    private AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
    private ObjectAnimator mProgress = new ObjectAnimator();
    private ValueAnimator mAnimator = new ValueAnimator();
    /**
     * 当前进度  可能会在ondraw中改变
     */
    protected float current;
    /**
     * 当前进度 信息 永久值  设置之后不会改变
     */
    protected float currentMsg;
    /**
     * 进度最大值
     */
    protected float maxProgress;
    /**
     * 动画所需的 变化参数
     */
    protected float progressAni;

    /**
     * 数字格式器
     */
    protected DecimalFormat numFormat = new DecimalFormat("#0.##%");
    /**
     * 控件宽度
     */
    protected int mWidth;
    /**
     * 控件高度
     */
    protected int mHeight;
    /**
     * 控件中心坐标
     */
    protected PointF mCPoint;
    /**
     * 控件宽度 和 高度之间  较小者
     */
    protected int mJust;
    private boolean objectAnimation = true;

    public Progress(Context context){
        super(context);
    }

    public Progress(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public Progress(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Progress(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        mHeight = getHeight();
        mJust = mWidth>mHeight ? mHeight : mWidth;
        mCPoint = new PointF(mWidth/2f, mHeight/2f);
    }

    /**
     * 设置当前进度 带动画形式
     * @param current 设置当前进度
     * @param delay 延时时间
     */
    public void setAniCurrent(float current,long delay){
        this.current = currentMsg = current;
        showAnimation(delay);
    }

    /**
     * 展现动画
     */
    public void showAnimation(){
        showAnimation(0);
    }

    /**
     * 延时展现动画
     * @param delay 延时
     */
    public void showAnimation(long delay){
        if(objectAnimation) {
            mProgress.cancel();
            mProgress = ObjectAnimator.ofFloat(this, "progressAni", 0, 1).setDuration(ANIDURATION);
            mProgress.setInterpolator(interpolator);
            mProgress.setStartDelay(delay);
            mProgress.start();
        }else {
            mAnimator.cancel();
            mAnimator.setFloatValues(0, currentMsg);
            mAnimator.setDuration((long)( ANIDURATION*currentMsg/maxProgress ));
            mAnimator.setInterpolator(interpolator);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation){
                    current = (float)animation.getAnimatedValue();//当前进度 有0变大
                    postInvalidate();
                }
            });
            mAnimator.start();
        }
    }

    /**
     * 设置当前进度 带动画形式
     * @param current 设置当前进度
     */
    public void setAniCurrent(float current){
        setAniCurrent(current, 0);
    }

    public float getProgressAni(){
        return progressAni;
    }

    public void setProgressAni(float progressAni){
        this.progressAni = progressAni;
        postInvalidate();
    }

    /**
     * 设置最大进度
     * @param maxProgress 最大进度
     */
    public void setMaxProgress(float maxProgress){
        this.maxProgress = maxProgress;
    }
    public float getMaxProgress(){
        return maxProgress;
    }

    /**
     * 设置 数字格式器
     * @param numFormat  数字格式器
     */
    public void setDecimalFormat(DecimalFormat numFormat){
        this.numFormat = numFormat;
    }
}
