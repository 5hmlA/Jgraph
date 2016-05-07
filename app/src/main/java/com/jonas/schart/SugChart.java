package com.jonas.schart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created on 2016/1/10.  by jwx338756
 */

public class SugChart extends View {

    private int mWidth;
    private int mHeight;

    /**
     * x轴 y 位置
     */
    private float mHCoordinate = 0;

    /**
     * x轴 柱状图 在x轴上方
     */
    private float mAbove;

    /**
     * 图表 画笔
     */
    private Paint mExecelPaint;
    private Paint mTextPaint;

    /**
     * 选中的 柱状图
     */
    private int mSelected = -1;
    private float mDownX;
    private boolean moved;
    private Paint mLinePaint;
    //文字间隔
    private float mTextMarging;
    private float mTextSize = 15;
    private DecimalFormat mFormat;
    private float mSugHeightest;
    private float mHeightRatio;
    /**
     * 横坐标 信息颜色
     */
    private int mAbscissaMsgColor = Color.parseColor("#556A73");
    /**
     * 渐变色
     */
    private int[] mShaderColors;
    private Paint mTextBgPaint;
    private int mTextBgColor = Color.parseColor("#556A73");
    private int mTextColor = Color.WHITE;
    private Rect mBounds;
    private boolean mScrollAble = true;
    private int mLineColor = Color.parseColor("#ffe9d1ba");
    private Paint mPointPaint;
    private int mPointColor = Color.parseColor("#CC6500");
    private float mAbscissaMsgSize;
    /**
     * 画横坐标信息
     */
    private Paint mAbscissaPaint;
    private int mFixedNums;//参照的 固定柱状图
    /**
     * 折线 中 折点 圆的半径
     */
    private float mLinePointRadio;

    public interface ChartStyle {
        /**
         * 心率柱状图
         */
        static final int BAR = 1;
        static final int LINE = 2;
        static final int BAR_LINE = 3;
    }

    public static class SugExcel {
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


        public SugExcel(float num, String mXmsg) {
            this(0, num, mXmsg);
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
            this.mXmsg = mXmsg;
            this.unit = unit;
            this.mColor = color;
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
        }


        public float getLower() {
            return mLower;
        }


        public void setLower(float lower) {
            mLower = lower;
        }
    }

    private Context mContext;
    /**
     * 系统认为发生滑动的最小距离
     */
    private int mTouchSlop;
    /**
     * 柱状图间的 间隔
     */
    private float mInterval;

    /**
     * 图表 数据集合
     */
    private List<SugExcel> mExcels = new ArrayList<>();

    /**
     * 柱状图 选中的颜色
     */
    private int mActivationColor = Color.RED;

    /**
     * 柱状图 选中的颜色
     */
    private int mNormalColor = Color.DKGRAY;

    /**
     * 要画的 图表的 风格
     */
    private int mChartStyle = ChartStyle.LINE;

    /**
     * 滑动 距离
     */
    private float mSliding = 0;

    /**
     * 柱形图 宽
     */
    private float mBarWidth = 0;
    private Path pathLine = new Path();


    public SugChart(Context context) {
        super(context);
        init(context);
    }


    public SugChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public SugChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mContext = context;
        initData();

        //系统认为发生滑动的最小距离
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mExecelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);//画笔文字居中
        mTextPaint.setTextSize(mTextSize);

        mTextBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAbscissaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextBgPaint.setColor(mTextBgColor);
        mAbscissaPaint.setColor(mAbscissaMsgColor);
        mAbscissaPaint.setTextAlign(Paint.Align.CENTER);//画笔文字居中

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(2);
        mLinePaint.setStyle(Paint.Style.STROKE);//画线的时候 必须把 画笔的style设置为 Paint.Style.STROKE
        mFormat = new DecimalFormat("##.##");
//        mPointPaint.setStrokeWidth(dip2px(4));
    }


    /**
     * 初始化 一些默认数据
     */
    private void initData() {
        mBarWidth = dip2px(36);
        mInterval = dip2px(20);
        mTextMarging = dip2px(3);
        mTextSize = sp2px(15);
        mAbscissaMsgSize = sp2px(15);
//        mAbove = dip2px(2);
        mBounds = new Rect();
        mLinePointRadio = dip2px(4);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mHCoordinate = mHeight - Math.abs(2*mAbscissaMsgSize);
        if (mChartStyle == ChartStyle.LINE) {
            mTextMarging *= 2;
        }
        if (!mScrollAble) {
            if (mFixedNums != 0 && mChartStyle == ChartStyle.BAR) {
//                mBarWidth = (w - 2 * (mFixedNums - 1)) / mFixedNums;
//                mInterval = (w - mBarWidth * mExcels.size()) / (mExcels.size() - 1);
                mBarWidth = (w - 2 * (mFixedNums + 1)) / mFixedNums;
                mInterval = (w - mBarWidth * mExcels.size()) / (mExcels.size() + 1);
            } else {
                mInterval = 2;
                mBarWidth = (w - mInterval * (mExcels.size() - 1)) / mExcels.size();
            }
        }
        if (mExcels.size() > 0) {
            refreshExcels();
        }
        mWidth = w;
    }


    private void refreshExcels() {
        if (mHCoordinate > 0) {
            mHeightRatio = (mHCoordinate -2 * mTextSize- mAbove - mTextMarging - mLinePointRadio/2) / mSugHeightest;
            for (int i = 0; i < mExcels.size(); i++) {
                SugExcel sugExcel = mExcels.get(i);
                sugExcel.setHeight(sugExcel.getHeight() * mHeightRatio);
                sugExcel.setWidth(mBarWidth);
                PointF start = sugExcel.getStart();
                if (mFixedNums != 0 && mChartStyle == ChartStyle.BAR) {
                    start.x = mInterval * (i + 1) + mBarWidth * i;
                } else {
                    start.x = mInterval * i + mBarWidth * i;
                }
                //            start.x = mInterval * (i + 1) + mBarWidth * i;
                //            if (mWidth == 0) {
                //                start.y = mHCoordinate - mAbove - start.y;
                //            }
                start.y = mHCoordinate - mAbove - sugExcel.getLower();
                sugExcel.setColor(mNormalColor);
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mChartStyle == ChartStyle.BAR) {
            drawSugExcel_BAR(canvas);
        } else if (mChartStyle == ChartStyle.LINE) {
            drawSugExcel_LINE(canvas);
        } else {
            drawSugExcel_BAR(canvas);
            drawSugExcel_LINE(canvas);
        }
        drawCoordinateAxes(canvas);
    }


    /**
     * 画 柱状
     */
    private void drawSugExcel_BAR(Canvas canvas) {
        for (int i = 0; i < mExcels.size(); i++) {
            SugExcel excel = mExcels.get(i);
            PointF start = excel.getStart();
            start.x += mSliding;
            if (null != mShaderColors) {
                mExecelPaint.setShader(new LinearGradient(start.x, start.y - excel.getHeight
                        (), start.x, start.y, mShaderColors[0], mShaderColors[1], Shader
                        .TileMode
                        .CLAMP));
            }
            if (i != mSelected) {
                if (null == mShaderColors) {
                    mExecelPaint.setColor(mNormalColor);
                }
            } else {
                if (null == mShaderColors) {
                    mExecelPaint.setColor(mActivationColor);
                }
            }

            canvas.drawRect(excel.getRectF(), mExecelPaint);
            drawAbscissaMsg(canvas, excel);
        }
        if (mSelected != -1) {
            drawSelectedText(canvas);
        }
    }


    /**
     * 画横坐标 信息
     */
    private void drawAbscissaMsg(Canvas canvas, SugExcel excel) {
        mAbscissaPaint.setColor(mAbscissaMsgColor);
        PointF midPointF = excel.getMidPointF();
        //柱状图横坐标 信息
        canvas.drawText(excel.getXmsg(), midPointF.x, mHCoordinate + mTextMarging + mTextSize, mAbscissaPaint);
    }


    /**
     * 画选择的 柱状图 数据
     */
    private void drawSelectedText(Canvas canvas) {

        mTextPaint.setColor(mTextColor);
        SugExcel excel = mExcels.get(mSelected);
        PointF midPointF = excel.getMidPointF();
        String msg = mFormat.format(excel.getUpper()) + excel.getUnit();
        mTextPaint.getTextBounds(msg, 0, msg.length(), mBounds);

        //画文字背景
        Path textBg = new Path();

        float bgHeight = dip2px(6);
        float bgWidth = dip2px(8);
        float textMarging = dip2px(2);
        if (mChartStyle == SugChart.ChartStyle.LINE) {
            textMarging = dip2px(2) + mLinePointRadio;
        }

        //三角尖 高4宽3
//        textBg.moveTo(midPointF.x, midPointF.y - mTextMarging);
//        textBg.lineTo(midPointF.x - bgWidth / 2, midPointF.y - mTextMarging - bgHeight);
//        textBg.lineTo(midPointF.x - mBounds.width() / 2 - bgWidth*3f/2, midPointF.y - mTextMarging - bgHeight);
//        textBg.lineTo(midPointF.x - mBounds.width() / 2 - bgWidth*3f/2, midPointF.y - mTextMarging - bgHeight - mBounds.height() - bgHeight*2);
//        textBg.lineTo(midPointF.x + mBounds.width() / 2 + bgWidth*3f/2, midPointF.y - mTextMarging - bgHeight - mBounds.height() - bgHeight*2);
//        textBg.lineTo(midPointF.x + mBounds.width() / 2 + bgWidth*3f/2, midPointF.y - mTextMarging - bgHeight);
//        textBg.lineTo(midPointF.x + bgWidth/2, midPointF.y - mTextMarging - bgHeight);
//        textBg.close();

        //画三角形
        textBg.moveTo(midPointF.x, midPointF.y - textMarging);
        textBg.lineTo(midPointF.x - bgWidth / 2, midPointF.y - textMarging - bgHeight - 1.5f);
        textBg.lineTo(midPointF.x + bgWidth / 2, midPointF.y - textMarging - bgHeight - 1.5f);
        textBg.close();
        canvas.drawPath(textBg, mTextBgPaint);
        //画圆角矩形
        RectF rectF = new RectF(midPointF.x - mBounds.width() / 2 - bgWidth, midPointF.y - mTextMarging - bgHeight - mBounds.height() - bgHeight * 2
                , midPointF.x + mBounds.width() / 2 + bgWidth, midPointF.y - mTextMarging - bgHeight);
        canvas.drawRoundRect(rectF, 3, 3, mTextBgPaint);

        //画文字
        canvas.drawText(msg, midPointF.x, midPointF.y - mTextMarging - bgHeight * 2, mTextPaint);
    }


    private void drawSugExcel_text(Canvas canvas, SugExcel excel, int i) {
        mTextPaint.setColor(mNormalColor);
        PointF midPointF = excel.getMidPointF();
        //柱状图横坐标 信息
        canvas.drawText(excel.getXmsg(), midPointF.x, mHCoordinate + mTextMarging + mTextSize, mTextPaint);
        if (mSelected == i) {
            mTextPaint.setColor(mActivationColor);
        }

        canvas.drawText(mFormat.format(excel.getUpper()) + excel.getUnit(), midPointF.x,
                midPointF.y - mTextMarging, mTextPaint);

        if (excel.getLower() > 0 && mChartStyle == ChartStyle.BAR) {
            canvas.drawText(mFormat.format(excel.getLower()) + excel.getUnit(), midPointF.x,
                    excel.getStart().y + mTextMarging + mTextSize, mTextPaint);
        }
    }


    /**
     * 画 折线
     */
    private void drawSugExcel_LINE(Canvas canvas) {
        pathLine.reset();
        mLinePaint.setColor(mLineColor);
        mPointPaint.setColor(mPointColor);
        for (int i = 0; i < mExcels.size(); i++) {
            SugExcel excel = mExcels.get(i);
            PointF start = excel.getStart();
            if (mChartStyle == ChartStyle.LINE) {
                start.x += mSliding;//当 柱状和折线都画的时候 只要其中一个改变起点横坐标即可
            }
            PointF midPointF = excel.getMidPointF();
//            canvas.drawCircle(midPointF.x, midPointF.y,dip2px(4),mPointPaint);
            if (i == 0) {
                pathLine.moveTo(midPointF.x, midPointF.y);
            } else {
                pathLine.lineTo(midPointF.x, midPointF.y);
            }
//            drawSugExcel_text(canvas, excel, i);
            drawAbscissaMsg(canvas, excel);
        }
        canvas.drawPath(pathLine, mLinePaint);

        pathLine.lineTo(mExcels.get(mExcels.size() - 1).getMidX(), mHCoordinate);
        pathLine.lineTo(mExcels.get(0).getMidX(), mHCoordinate);
        pathLine.close();
        mExecelPaint.setShader(new LinearGradient(0, 0, 0, mHCoordinate, mShaderColors[0], mShaderColors[1], Shader
                .TileMode
                .CLAMP));

        canvas.drawPath(pathLine, mExecelPaint);

        if (mLinePointRadio>0) {
            //画原点
            for (SugExcel excel : mExcels) {
                PointF midPointF = excel.getMidPointF();
                canvas.drawCircle(midPointF.x, midPointF.y, mLinePointRadio, mPointPaint);
            }
        }
        //画选中的 item 的文字信息
        if (mSelected != -1) {
            drawSelectedText(canvas);
        }
    }


    /**
     * 画 坐标轴
     */
    private void drawCoordinateAxes(Canvas canvas) {
        mExecelPaint.setColor(Color.BLACK);
        canvas.drawLine(0, mHCoordinate, mWidth, mHCoordinate, mExecelPaint);
//        mLinePaint.setColor(Color.BLACK);
//        canvas.drawLine(0, mHCoordinate, mWidth, mHCoordinate, mLinePaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();

                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                mSliding = moveX - mDownX;
                if (Math.abs(mSliding) > mTouchSlop) {
                    //          pathLine.reset();
                    moved = true;
                    mDownX = moveX;
                    if (mExcels.get(0).getStart().x + mSliding > mInterval ||
                            mExcels.get(mExcels.size() - 1).getStart().x + mBarWidth + mInterval + mSliding <
                                    mWidth) {
                        return true;
                    }
                    if (mScrollAble) {
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!moved) {
                    PointF tup = new PointF(event.getX(), event.getY());
                    mSelected = clickWhere(tup);
                    invalidate();
                }
                moved = false;
                mSliding = 0;
                break;
            default:
                break;
        }
        return true;
    }


    /**
     * 判断 点中哪个柱状图
     */
    private int clickWhere(PointF tup) {
        for (int i = 0; i < mExcels.size(); i++) {
            SugExcel excel = mExcels.get(i);
            PointF start = excel.getStart();
            if (start.x > tup.x) {
                return -1;
            } else if (start.x <= tup.x) {
                if (start.x + excel.getWidth() > tup.x &&
                        (start.y > tup.y && start.y - excel.getHeight() < tup.y)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 传入 数据
     */
    public void cmdFill(SugExcel... sugExcels) {
        cmdFill(Arrays.asList(sugExcels));
    }


    /**
     * 传入 数据
     */
    public void cmdFill(List<SugExcel> sugExcelList) {
        mExcels.clear();
        for (SugExcel sugExcel : sugExcelList) {
            mSugHeightest = mSugHeightest > sugExcel.getHeight() ? mSugHeightest : sugExcel.getHeight();
        }
        for (int i = 0; i < sugExcelList.size(); i++) {
            SugExcel sugExcel = sugExcelList.get(i);
            sugExcel.setWidth(mBarWidth);
            PointF start = sugExcel.getStart();
            start.x = mInterval * (i + 1) + mBarWidth * i;
            sugExcel.setColor(mNormalColor);
            mExcels.add(sugExcel);
        }
        if (mWidth != 0) {
            //已经显示在界面上了 重新设置数据
            if (!mScrollAble) {
                if (mFixedNums != 0 && mChartStyle == ChartStyle.BAR) {
//                mBarWidth = (w - 2 * (mFixedNums - 1)) / mFixedNums;
//                mInterval = (w - mBarWidth * mExcels.size()) / (mExcels.size() - 1);
                    mBarWidth = (mWidth - 2 * (mFixedNums + 1)) / mFixedNums;
                    mInterval = (mWidth - mBarWidth * mExcels.size()) / (mExcels.size() + 1);
                } else {
                    mInterval = 2;
                    mBarWidth = (mWidth - mInterval * (mExcels.size() - 1)) / mExcels.size();
                }
            }
            refreshExcels();
            postInvalidate();
        }
    }


    public float getInterval() {
        return mInterval;
    }


    /**
     * 设置 两柱状图之间的间隔
     */
    public void setInterval(float interval) {
        this.mInterval = interval;
    }


    public int getNormalColor() {
        return mNormalColor;
    }


    /**
     * 默认颜色
     */
    public void setNormalColor(int normalColor) {
        mNormalColor = normalColor;
    }


    public int getActivationColor() {
        return mActivationColor;
    }


    /**
     * 设置 柱状图 被选中的颜色
     */
    public void setActivationColor(int activationColor) {
        mActivationColor = activationColor;
    }


    public int getChartStyle() {
        return mChartStyle;
    }


    /**
     * 设置 图表类型  柱状 折线  折线+柱状
     */
    public void setChartStyle(int chartStyle) {
        mChartStyle = chartStyle;
    }


    /**
     * 获取 图表的 偏移量 -左
     */
    public float getSliding() {
        return mExcels.get(0).getStart().x - mInterval;
    }


    public void setSliding(float sliding) {
        mSliding = sliding;
    }


    public float getBarWidth() {
        return mBarWidth;
    }


    /**
     * 设置 柱状条 的宽度
     */
    public void setBarWidth(float barWidth) {
        mBarWidth = dip2px(barWidth);
    }


    public float getTextSize() {
        return mTextSize;
    }


    /**
     * 设置 文字大小 同时 调整x轴 距离底部位置(为字体大小两倍)
     */
    public void setTextSize(float textSize) {
        mTextSize = sp2px(textSize);
        mHCoordinate = mTextSize * 2;
        mTextPaint.setTextSize(mTextSize);
    }

    public float getAbscissaMsgSize() {
        return mAbscissaMsgSize;
    }

    public void setAbscissaMsgSize(float abscissaMsgSize) {
        mAbscissaMsgSize = abscissaMsgSize;
        mAbscissaPaint.setTextSize(mAbscissaMsgSize);
    }

    public int getTextBgColor() {
        return mTextBgColor;
    }

    public void setTextBgColor(int textBgColor) {
        mTextBgColor = textBgColor;
        mTextBgPaint.setColor(mTextBgColor);
    }

    public int getAbscissaMsgColor() {
        return mAbscissaMsgColor;
    }

    public void setAbscissaMsgColor(int abscissaMsgColor) {
        mAbscissaMsgColor = abscissaMsgColor;
        mAbscissaPaint.setColor(mAbscissaMsgColor);
    }

    public float getTextMarging() {
        return mTextMarging;
    }


    public void setTextMarging(float textMarging) {
        mTextMarging = dip2px(textMarging);
    }


    public float getHCoordinate() {
        return mHCoordinate;
    }


    /**
     * 渐变色
     *
     * @param colors
     */
    public void setExecelPaintShaderColors(int[] colors) {
        mShaderColors = colors;
    }


    /**
     * x轴 的位置
     *
     * @param HCoordinate 距离底部 多少
     */
    public void setHCoordinate(float HCoordinate) {
        mHCoordinate = HCoordinate;
    }


    /**
     * 设置 不可 滚动  柱状图 将平分屏宽
     *
     * @param scrollAble
     */
    public void setScrollAble(boolean scrollAble) {
        mScrollAble = scrollAble;
    }

    /**
     * 设置 参照的 最多/固定的 柱状图
     * 此时 不可滚动
     *
     * @param fixedNums
     */
    public void setFixedWidth(int fixedNums) {
        mScrollAble = false;
        mFixedNums = fixedNums;
    }
    public float getLinePointRadio() {
        return mLinePointRadio;
    }

    /**
     * 设置 折线中 折点 圆的半径
     * @param linePointRadio
     */
    public void setLinePointRadio(float linePointRadio) {
        mLinePointRadio = linePointRadio;
    }
    public int dip2px(float dipValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     */
    public int sp2px(float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
