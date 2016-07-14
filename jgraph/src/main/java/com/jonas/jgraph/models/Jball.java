package com.jonas.jgraph.models;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import static android.R.attr.radius;

/**
 * @author yun.
 * @date 2016/6/8
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public class Jball implements ValueAnimator.AnimatorUpdateListener {

    private int index;
    private int color;
    /**
     * 轨迹半径
     */
    private float mRouteRadius;
    /**
     * 圆球半径
     */
    private float ballRadius;
    /**
     * 路径的圆心
     */
    private PointF mRouteCenter = new PointF();
    /**
     * 小球的圆心
     */
    private PointF ballCenter = new PointF(-100,-100);
    private float aniTime;
    private final Paint mCirclePaint;
    /**
     * 开始的角度
     */
    private float startSweepangle;
    /**
     * 结束的角度
     */
    private float endSweepangle = 360;
    private float aniRatio = 1;
    private ValueAnimator sweepAni = new ValueAnimator();
    private View mView;

    public Jball(float ballRadius) {
        this.ballRadius = ballRadius;
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public Jball(float ballRadius, Paint circlePaint) {
        this.ballRadius = ballRadius;
        mCirclePaint = circlePaint;
    }

    public Jball drawCircle(Canvas canvas, View view) {

        mView = view;
        canvas.drawCircle(ballCenter.x, ballCenter.y, ballRadius, mCirclePaint);
        return this;
    }

    public Jball setStartAniSweep(float startSweepangle, long aniduration, long startDelay) {
        if (!sweepAni.isRunning() && aniRatio == 1) {
            this.startSweepangle = startSweepangle;
            sweepAni.setFloatValues(0, 1);
            sweepAni.setDuration(aniduration);
            sweepAni.setInterpolator(new AccelerateDecelerateInterpolator());
            sweepAni.addUpdateListener(this);
            sweepAni.setStartDelay(startDelay);
            sweepAni.start();
        }
        return this;
    }

    public Jball cancelAnimation(){
        sweepAni.cancel();
        aniRatio = 1;
        return this;
    }

    public Jball setBallCenter(@NonNull PointF ballCenter) {
        this.ballCenter = ballCenter;
        return this;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        aniRatio = (float) animation.getAnimatedValue();
        float cSweep = startSweepangle + (endSweepangle - startSweepangle) * aniRatio;
        computeBollCenter(cSweep);
        mView.postInvalidate();
    }

    private void computeBollCenter(float sweepAngle) {
        double sweepHudu = sweepAngle / 180 * Math.PI;
        ballCenter.x = (float) (mRouteRadius * Math.cos(sweepHudu)) + mRouteCenter.x;
        ballCenter.y = (float) (mRouteRadius * Math.sin(sweepHudu)) + mRouteCenter.y;
    }

    public int getColor() {
        return color;
    }

    public Jball setColor(int color) {
        if (this.color != color) {
            this.color = color;
            mCirclePaint.setColor(color);
        }
        return this;
    }

    public float getRadius() {
        return radius;
    }

    public PointF getRouteCenter() {
        return mRouteCenter;
    }

    public Jball setRouteCenter(PointF routeCenter) {
        this.mRouteCenter = routeCenter;
        return this;
    }

    public float getAniTime() {
        return aniTime;
    }

    public void setAniTime(float aniTime) {
        this.aniTime = aniTime;
    }


    public float getStartSweepangle() {
        return startSweepangle;
    }

    public void setStartSweepangle(float startSweepangle) {
        this.startSweepangle = startSweepangle;
    }

    public float getBallRadius() {
        return ballRadius;
    }

    public Jball setBallRadius(float ballRadius) {
        this.ballRadius = ballRadius;
        return this;
    }

    public float getRouteRadius() {
        return mRouteRadius;
    }

    public Jball setRouteRadius(float routeRadius) {
        this.mRouteRadius = routeRadius;
        return this;
    }

    public float getEndSweepangle() {
        return endSweepangle;
    }

    public void setEndSweepangle(float endSweepangle) {
        this.endSweepangle = endSweepangle;
    }

    public int getIndex() {
        return index;
    }

    public Jball setIndex(int index) {
        this.index = index;
        return this;
    }
}
