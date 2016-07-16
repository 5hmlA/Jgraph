package com.jonas.jgraph.progress;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.jonas.jgraph.R;
import com.jonas.jgraph.inter.IProgress;
import com.jonas.jgraph.utils.DisplayUtils;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author yun.
 * @date 2016/6/8
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public class JWaveBallProg extends View implements IProgress {

    private Paint mTierPaint;

    public static interface ProgMode {
        int PROG_WAVE = 0;
        int PROG_NORM = 1;
    }

    /**
     * 进度的颜色
     */
    private int progressColor;
    private static final int MODE_WAVE = 0;
    private static final int MODE_NORMAL = 1;
    private int mProgMode = ProgMode.PROG_WAVE;
    /**
     * 进度球的背景颜色
     */
    private int ballgroundColor;
    /**
     * onDraw中使用的进度 动画使用的进度
     */
    private float mProgress;
    private Paint mTemWavePaint;
    private Paint mWavePaint;
    private int mWidth;
    private int mHeight;
    /**
     * 小球中心点 坐标
     */
    private PointF mCenter;
    private RectF mProgRectf;
    private DecimalFormat numFormat;

    private Paint mTextPaint;
    private int textColor;
    /**
     * 进度球的半径
     */
    private int mWaveBallRadius;
    private float mTextSize = DisplayUtils.sp2px(getContext(), 20);
    public long ANIDURATION = 5000;
    private TimeInterpolator Interpolator = new DecelerateInterpolator();
    public ValueAnimator mAnimator = new ValueAnimator();
    private List<Integer> mTierColor = new ArrayList<>();
    /**
     * 画波纹需要的 路劲
     */
    private Path mWavePath;
    /**
     * 波浪振幅
     */
    private float mAmplitude;
    /**
     * 最小振幅
     */
    private float mAmplitudeMin;
    /**
     * 存放原始的 波浪振幅
     */
    private float mAmplitudeTemp = 20;
    /**
     * 波纹 移动产生波浪效果 需要的变量
     */
    private Random mRandom;
    /**
     * 波纹 移动产生波浪效果 需要的变量
     * 移动的距离
     */
    private double waveMove = 0;
    /**
     * 波纹 移动产生波浪效果 需要的变量
     * 移动的速度
     * 可能出现的最大速度是设置的2倍
     */
    private double waveSpeed;
    /**
     * 最小的 移动速度
     */
    private float mWaveSpeedMin;
    /**
     * 关闭 波纹的 滚动波浪效果
     */
    private boolean stopWaving = false;
    /**
     * 波峰波谷的数量
     * <p>也能达到 调节速度的效果 值越大速度看着越快
     * <p>一个波峰一个PI 一个波谷一个PI  水波纹最多显示多少个波峰波谷
     */
    private float waveNum = 5;
    /**
     * 显示辅助进度直线  实际的进度
     */
    private boolean showProgressLine;
    private Path mBallPath;
    private Path mWaveBallPath;
    private float mMax = 100;
    private long DURATION = 2000;
    private TimeInterpolator mInterpolator = new DecelerateInterpolator();
    private ObjectAnimator mOa = ObjectAnimator.ofFloat(this, "progress", 0, 1);

    {
        mTemWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTierPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //圆
        mBallPath = new Path();
        //波浪
        mWavePath = new Path();
        //波浪圆
        mWaveBallPath = new Path();
        mRandom = new SecureRandom();

        //"##.##0"小数位0不能放#后面    "0##.##"整数位0不能放#前面
        numFormat = new DecimalFormat("#0.##%");
    }

    public JWaveBallProg(Context context){
        this(context, null);
    }

    public JWaveBallProg(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public JWaveBallProg(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        if(attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.JProgress);
            progressColor = a.getColor(R.styleable.JProgress_progressColor, Color.RED);
            ballgroundColor = a.getColor(R.styleable.JProgress_backgroundColor, Color.TRANSPARENT);
            textColor = a.getColor(R.styleable.JProgress_balltextColor, Color.BLACK);
            mMax = a.getFloat(R.styleable.JProgress_max, 100);
            mProgress = a.getFloat(R.styleable.JProgress_progress, 0);
            //            mAmplitude = mAmplitudeTemp = typedArray.getFloat(R.styleable.JProgBall_amplitude, 10);
            mProgMode = a.getInt(R.styleable.JProgress_progMode, ProgMode.PROG_WAVE);
            //获取字体大小
            mTextSize = a.getDimensionPixelSize(R.styleable.JProgress_balltextSize, 40);
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        mWavePaint.setColor(progressColor);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTierPaint.setStrokeWidth(1f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCenter = new PointF(w/2, h/2);
        int mJust = mWidth>mHeight ? mHeight : mWidth;
        mWaveBallRadius = mWaveBallRadius == 0 ? mJust/2 : mWaveBallRadius;

        mProgRectf = new RectF(mCenter.x-mWaveBallRadius, mCenter.y-mWaveBallRadius, mCenter.x+mWaveBallRadius,
                mCenter.y+mWaveBallRadius);
        mBallPath.addCircle(mCenter.x, mCenter.y, mWaveBallRadius, Path.Direction.CCW);

        mAmplitudeMin = mWaveBallRadius/30f;
        mWaveSpeedMin = mWaveBallRadius/30f;
        //设置一些默认值
        mAmplitude = mAmplitudeTemp = mAmplitude<mAmplitudeMin ? mAmplitudeMin : mAmplitude;
        waveSpeed = waveSpeed>mWaveSpeedMin ? waveSpeed : mWaveSpeedMin;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        //超过100%的多少层
        int tier = (int)Math.ceil(( mProgress/mMax )-1);//当前画的是第tier+1层

        if(tier>0) {
            mWavePaint.setColor(mTierColor.get(tier+1));
            mTierPaint.setColor(mTierColor.get(tier));
            //画 超出 的前一层
            canvas.drawCircle(mCenter.x, mCenter.y, mWaveBallRadius, mTierPaint);
        }else {
            mTierPaint.setColor(ballgroundColor);
            canvas.drawCircle(mCenter.x, mCenter.y, mWaveBallRadius, mTierPaint);
        }

        if(mProgMode == ProgMode.PROG_WAVE) {
            //            drawWaveProgress(canvas, (float) (getWaveLength()/3));
            //            mWavePaint.setColor(Color.GREEN);
            drawWaveProgress(canvas, 0);
        }else {
            //画进度
            drawProgress(canvas);
        }

        String currMsg = numFormat.format(mProgress/mMax);
        @SuppressLint("DrawAllocation") Rect bounds = new Rect();
        mTextPaint.getTextBounds(currMsg, 0, currMsg.length(), bounds);
        mTextPaint.setTextSize(mTextSize);
        canvas.drawText(currMsg, mCenter.x, mCenter.y+bounds.height()/2, mTextPaint);

    }

    /**
     * 获取 单个波 的波长
     *
     * @return
     */
    private double getWaveLength(){
        return 2*mWaveBallRadius/waveNum;
    }

    /**
     * 像素 转为 角度
     * <p>一个波峰一个PI 一个波谷一个PI  水波纹最多显示多少个波峰波谷 最长的显示区域(2*mWaveBallRadius)
     *
     * @param px
     * @return
     */
    private double px2degle(double px){
        return px*waveNum*Math.PI/( 2*mWaveBallRadius );
    }

    private double degle2px(double degle){
        return degle*2*mWaveBallRadius/( waveNum*Math.PI );
    }

    private void drawWaveProgress(Canvas canvas, float offset){

        float waveProg = mProgress-mMax*( ( (int)( mProgress/mMax ) ) );//mProgress==mMax时为0
        if(waveProg == 0 && mProgress>0) {
            waveProg = mMax;
        }
        //下往上
        float currentY = mCenter.y+mWaveBallRadius-waveProg/mMax*mWaveBallRadius*2;
        mWavePath.reset();
        mWavePath.moveTo(mCenter.x-mWaveBallRadius, currentY);//起点

        //波浪进度 的path
        //        for (int i = (int) (mCenter.x - mWaveBallRadius); i <= mCenter.x + mWaveBallRadius + 1; i++) {
        for(int i = 0; i<=2*mWaveBallRadius; i++) {
            float cx = i+mCenter.x-mWaveBallRadius;
            //            mAmplitude = mRandom.nextInt((int) mAmplitudeTemp); //打开 会出现较大的锯齿 有点像声波
            //画cos曲线  cos曲线往左或者往右一直移动出现波浪波动效果
            //            mWavePath.lineTo(i, currentY + mAmplitude * (float) Math.cos((i + waveMove) * (waveNum * 180f / (2 * mWaveBallRadius)) * Math.PI / 180f));
            //长度转角度 0~mWaveBallRadius == 0~PI
            mWavePath.lineTo(offset+cx, currentY+mAmplitude*(float)Math.cos(px2degle(cx+waveMove)));
        }
        mWavePath.lineTo(mCenter.x+mWaveBallRadius, mCenter.y+mWaveBallRadius);//右下角
        mWavePath.lineTo(mCenter.x-mWaveBallRadius, mCenter.y+mWaveBallRadius);//左下角  之后会闭合到起点
        mWavePath.close();
        //        drawBallWaveByOP(canvas, currentY);
        drawBallWaveBybitmapShader(canvas, currentY);

        if(!stopWaving) {
            waveMove += waveSpeed+mRandom.nextInt((int)( waveSpeed ));
            //            waveMove = waveMove > waveNum * mWaveBallRadius ? waveMove - waveNum * mWaveBallRadius : waveMove;
            waveMove = waveMove>degle2px(2*Math.PI) ? waveMove-degle2px(2*Math.PI) : waveMove;
            //当最后一层进度满100% 时波浪慢慢消失
            if(mProgress == mMax) {
                if(mAmplitude>0) {
                    mAmplitude = mAmplitude-mAmplitudeTemp/600f;//波浪600ms之后平息
                    postInvalidate();
                }
            }else {
                postInvalidate();
                mAmplitude = mAmplitudeTemp;
            }
        }
    }

    private void drawBallWaveByOP(Canvas canvas, float currentY){
        //整合波浪球路径
        boolean op = mWaveBallPath.op(mBallPath, mWavePath, Path.Op.INTERSECT);
        if(op) {
            canvas.drawPath(mWaveBallPath, mWavePaint);
        }else {
            postInvalidate();
        }
        if(showProgressLine) {
            //中间线条  显示辅助的进度线条 也就是实际的进度
            canvas.drawLine(0, currentY, mWidth, currentY, mTierPaint);
        }
    }

    private void drawBallWaveBybitmapShader(Canvas canvas, float currentY){
        Bitmap waveBitm = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_4444);
        Canvas waveCanv = new Canvas(waveBitm);
        waveCanv.drawPath(mWavePath, mWavePaint);
        if(showProgressLine) {
            //中间线条  显示辅助的进度线条 也就是实际的进度
            waveCanv.drawLine(0, currentY, mWidth, currentY, mTierPaint);
        }

        mTemWavePaint.setShader(new BitmapShader(waveBitm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawPath(mBallPath, mTemWavePaint);

    }

    /**
     * 画扇形进度
     *
     * @param canvas
     *         画笔
     */
    private void drawProgress(Canvas canvas){

        float waveProg = mProgress-mMax*( ( (int)( mProgress/mMax ) ) );
        if(waveProg == 0 && mProgress>0) {
            waveProg = mMax;
        }
        waveProg *= 2*mWaveBallRadius/mMax;
        //        角度=弧度/PI * 180     弧度＝(角度/180) *PI
        //        double acos = Math.acos((mWaveBallRadius - waveProg) / mWaveBallRadius) / Math.PI * 180;
        //弧度转角度
        double acos = Math.toDegrees(Math.acos(( mWaveBallRadius-waveProg )/mWaveBallRadius));
        float start = (float)( 90-acos );
        float sweep = (float)( acos*2 );

        canvas.drawArc(mProgRectf, start, sweep, false, mWavePaint);
    }

    /**
     * 需要的话 分配好各层的颜色
     *
     * @param progress
     *         设置进度 并会根据需要配置随机颜色
     */
    private void tierColor(float progress){
        //一共需要画几层
        int tier = (int)Math.ceil(progress/mMax);
        mTierColor.clear();//有add的地方就要注意clear
        for(int i = 0; i<=tier; i++) {
            if(i == 1) {
                //第一层
                mTierColor.add(progressColor);
            }else {
                //第i层
                mTierColor.add(getRanColor());
            }
        }
        mWavePaint.setColor(progressColor);
    }

    /**
     * 获取随机的颜色
     *
     * @return 随机的颜色
     */
    private int getRanColor(){
        Random random = new Random();
        return 0xff000000|random.nextInt(0x00ffffff);
    }

    @Override
    public IProgress setAniDuration(long duration){
        return this;
    }

    @Override
    public float getProgress(){
        return mProgress;
    }

    @Override
    public Paint getProgPaint(){
        return mWavePaint;
    }

    @Override
    public Paint getTextPaint(){
        return mTextPaint;
    }

    public void setProgress(float progress){
        mProgress = progress;
        postInvalidate();
    }

    @Override
    public IProgress setJProgress(float progress){
        mProgress = progress;
        tierColor(progress);
        postInvalidate();
        return this;
    }

    @Override
    public IProgress setAniProgress(float progress){
        mProgress = progress;
        tierColor(progress);
        if(mOa.isRunning()) {
            mOa.cancel();
        }
        mOa.setFloatValues(0, progress);
        //        mOa = ObjectAnimator.ofFloat(this, "progress", 0, progress);
        mOa.setInterpolator(mInterpolator);
        mOa.setDuration(DURATION*( ( (int)( progress/mMax ) )+1 ));
        mOa.start();
        return this;
    }

    @Override
    public IProgress setProgressWidth(float progWidth){
        return this;
    }

    @Override
    public IProgress setMax(float max){
        mMax = max;
        return this;
    }

    @Override
    public IProgress setProgressBackground(int bgColor){
        return this;
    }

    /**
     * 设置 进度条的颜色
     *
     * @param progressColor
     *         进度条的颜色
     */
    public IProgress setProgressColor(int progressColor){
        this.progressColor = progressColor;
        mWavePaint.setColor(progressColor);
        return this;
    }

    /**
     * 修改进度的格式器
     *
     * @param numFormat
     *         进度的格式器
     */
    public void setNumFormat(DecimalFormat numFormat){
        this.numFormat = numFormat;
    }

    /**
     * 设置动画执行的加速器
     *
     * @param interpolator
     *         加速器
     */
    public void setInterpolator(TimeInterpolator interpolator){
        Interpolator = interpolator;
    }

    public int getProgMode(){
        return mProgMode;
    }

    /**
     * 设置 进度球的 进度模式 1，普通模式
     * 0，波纹模式
     *
     * @param progMode
     */
    public void setProgMode(int progMode){
        this.mProgMode = progMode;
    }
}

