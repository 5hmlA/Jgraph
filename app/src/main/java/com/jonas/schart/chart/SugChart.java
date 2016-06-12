package com.jonas.schart.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.jonas.schart.chartbean.SugExcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @Author jwx338756
 * @Date: 2016
 * @Description: 折线+虚线
 * @Others: {https://github.com/mychoices}
 *
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
    private Paint mDashLinePaint;
    //文字间隔
    private float mTextMarging;
    private float mTextSize = 15;
    //    private float mSugHeightest;
    private SugExcel mHeightestExcel;
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
    private float tempTextMargin;
    private boolean lineFirstMoved = false;
    private Paint mCoordinatePaint;
    private float phase = 1;
    private int mPading;

    public interface ChartStyle {
        /**
         * 心率柱状图
         */
        int BAR = 1;
        int LINE = 2;
        int BAR_LINE = 3;
    }

//    public static class SugExcel {
//        private String mShowMsg;
//        private int index;
//        private float mWidth;//柱状 的 宽
//        private float mHeight;//折线的y
//        private PointF mStart = new PointF();//矩形左下角起点
//        private float mMidX;//中点 折线的x
//        private int mColor;
//        private float mNum; //当前数字
//        private float mMax; //总数据
//        private String textMsg; //要显示的信息
//        private String mXmsg; //横坐标信息
//        private float mUpper;
//        private float mLower;
//        /**
//         * 单位
//         */
//        private String unit;
//
//        public SugExcel(float num, String mXmsg, int index) {
//            this(0, num, mXmsg);
//            this.index = index;
//        }
//
//        public SugExcel(float num, String mXmsg) {
//            this(0, num, mXmsg);
//        }
//
//        public SugExcel(float lower, float upper, String mXmsg) {
//            this(lower, upper, mXmsg, Color.GRAY);
//        }
//
//
//        public SugExcel(float lower, float upper, String mXmsg, int color) {
//            this(lower, upper, "", mXmsg, Color.GRAY);
//        }
//
//
//        public SugExcel(float num, String unit, String mXmsg) {
//            this(0, num, unit, mXmsg, Color.GRAY);
//        }
//
//
//        public SugExcel(float lower, float upper, String unit, String mXmsg, int color) {
//            mUpper = upper;
//            mLower = lower;
//            mHeight = mNum = upper - lower;
//            mStart.y = mLower;
//            this.mXmsg = mXmsg;
//            this.unit = unit;
//            this.mColor = color;
//            mShowMsg = new DecimalFormat("##").format(mHeight);
//        }
//
//        public RectF getRectF() {
//            return new RectF(mStart.x, mStart.y - mHeight, mStart.x + mWidth, mStart.y);
//        }
//
//
//        public PointF getMidPointF() {
//            return new PointF(getMidX(), mStart.y - mHeight);
//        }
//
//
//        public String getTextMsg() {
//            return textMsg;
//        }
//
//
//        public String getUnit() {
//            return unit;
//        }
//
//
//        public void setUnit(String unit) {
//            this.unit = unit;
//        }
//
//
//        public void setTextMsg(String textMsg) {
//            this.textMsg = textMsg;
//        }
//
//
//        public float getWidth() {
//            return mWidth;
//        }
//
//
//        public void setWidth(float width) {
//            this.mWidth = width;
//        }
//
//
//        public float getHeight() {
//            return mHeight;
//        }
//
//
//        public void setHeight(float height) {
//            this.mHeight = height;
//        }
//
//
//        public PointF getStart() {
//            return mStart;
//        }
//
//
//        public void setStart(PointF start) {
//            this.mStart = start;
//        }
//
//
//        public float getMidX() {
//            if (null != mStart) {
//                mMidX = mStart.x + mWidth / 2;
//            } else {
//                throw new RuntimeException("mStart 不能为空");
//            }
//            return mMidX;
//        }
//
//
//        public void setMidX(float midX) {
//            this.mMidX = midX;
//        }
//
//
//        public int getColor() {
//            return mColor;
//        }
//
//
//        public void setColor(int color) {
//            mColor = color;
//        }
//
//
//        public float getNum() {
//            return mNum;
//        }
//
//
//        public void setNum(float num) {
//            this.mNum = num;
//        }
//
//
//        public float getMax() {
//            return mMax;
//        }
//
//
//        public void setMax(float max) {
//            this.mMax = max;
//        }
//
//
//        public String getXmsg() {
//            return mXmsg;
//        }
//
//
//        public void setXmsg(String xmsg) {
//            this.mXmsg = xmsg;
//        }
//
//
//        public float getUpper() {
//            return mUpper;
//        }
//
//
//        public void setUpper(float upper) {
//            mUpper = upper;
//            mHeight = mUpper - mLower;
//            mShowMsg = new DecimalFormat("##.#").format(mUpper);
//        }
//
//
//        public float getLower() {
//            return mLower;
//        }
//
//
//        public void setLower(float lower) {
//            mLower = lower;
//        }
//
//        public int getIndex() {
//            return index;
//        }
//
//        public void setIndex(int index) {
//            this.index = index;
//        }
//
//        public String getShowMsg() {
//            return mShowMsg;
//        }
//
//        public void setShowMsg(String showMsg) {
//            mShowMsg = showMsg;
//        }
//    }

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
     * 柱状图 未选中选中的颜色
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
    private float mBarWidth = 20;
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
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mExecelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);

        mTextBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAbscissaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextBgPaint.setColor(mTextBgColor);
        mAbscissaPaint.setTextSize(mAbscissaMsgSize);
        mAbscissaPaint.setColor(mAbscissaMsgColor);
        mAbscissaPaint.setTextAlign(Paint.Align.CENTER);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(2);
        mLinePaint.setStyle(Paint.Style.STROKE);//画线的时候 必须把 画笔的style设置为 Paint.Style.STROKE
        mDashLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDashLinePaint.setStrokeWidth(2);
        mDashLinePaint.setStyle(Paint.Style.STROKE);//画线的时候 必须把 画笔的style设置为 Paint.Style.STROKE

        mCoordinatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mCoordinatePaint.setStrokeWidth(dip2px(2));
        mCoordinatePaint.setStyle(Paint.Style.STROKE);
    }


    /**
     * 初始化 一些默认数据
     */
    private void initData() {
        mBarWidth = dip2px(36);
        mInterval = dip2px(20);
        tempTextMargin = mTextMarging = dip2px(3);
        mTextSize = sp2px(12);
        mAbscissaMsgSize = sp2px(12);
        mBounds = new Rect();
        mLinePointRadio = dip2px(4);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mHCoordinate = mHeight - Math.abs(2 * mAbscissaMsgSize);
        if (mChartStyle == ChartStyle.LINE) {
            mTextMarging *= 2;
        }
//        mPading = dip2px(15);
        mPading = 0;

        refreshBarWidth_Interval(w);
        if (mExcels.size() > 0) {
            refreshExcels();
        }
        mWidth = w;
    }

    private void refreshBarWidth_Interval(int w) {
        if (!mScrollAble) {
            if (mFixedNums > 0 && mChartStyle == ChartStyle.BAR) {
                mBarWidth = (w - 2 * mPading - 2 * (mFixedNums + 1)) / ((float) mFixedNums);
                mInterval = (w - 2 * mPading - mBarWidth * mExcels.size()) / (mExcels.size() + 1);
            } else {
                mInterval = 2;
                mBarWidth = (w - 2 * mPading - mInterval * (mExcels.size() - 1)) / mExcels.size();
            }
        }
    }


    private void refreshExcels() {
        if (mHCoordinate > 0) {
            mHeightRatio = (mHCoordinate - 2 * mTextSize - mAbove - mTextMarging - mLinePointRadio * 2) / mHeightestExcel.getHeight();
            for (int i = 0; i < mExcels.size(); i++) {
                SugExcel sugExcel = mExcels.get(i);
                sugExcel.setHeight(sugExcel.getHeight() * mHeightRatio);
                sugExcel.setWidth(mBarWidth);
                PointF start = sugExcel.getStart();
                if (mFixedNums > 0 && mChartStyle == ChartStyle.BAR) {
                    start.x = mPading + mInterval * (i + 1) + mBarWidth * i;
                } else {
                    start.x = mPading + mInterval * i + mBarWidth * i;
                }
                start.y = mHCoordinate - mAbove - sugExcel.getLower();
                sugExcel.setColor(mNormalColor);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null != mExcels && mExcels.size() > 0) {
            if (mChartStyle == ChartStyle.BAR) {
                drawSugExcel_BAR(canvas);
            } else if (mChartStyle == ChartStyle.LINE) {
                drawSugExcel_LINE(canvas);
            } else {
                drawSugExcel_BAR(canvas);
                drawSugExcel_LINE(canvas);
            }
        }
        drawCoordinateAxes(canvas);
    }

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

            if (excel.getHeight() > 0) {
                canvas.drawRect(excel.getRectF(), mExecelPaint);
            }
            drawAbscissaMsg(canvas, excel);
        }
        if (mSelected > -1) {
            drawSelectedText(canvas, mExcels.get(mSelected));
        } else {
            drawSelectedText(canvas, mExcels.get(mHeightestExcel.getIndex()));
        }
    }

    private void drawAbscissaMsg(Canvas canvas, SugExcel excel) {
        if (null != excel) {
            mAbscissaPaint.setColor(mAbscissaMsgColor);
            PointF midPointF = excel.getMidPointF();
            if (!TextUtils.isEmpty(excel.getXmsg())) {
                String xmsg = excel.getXmsg();
                float w = mAbscissaPaint.measureText(xmsg, 0, xmsg.length());
                if (midPointF.x - w / 2 < 0) {
                    canvas.drawText(excel.getXmsg(), w / 2, mHCoordinate + dip2px(3) + mTextSize, mAbscissaPaint);
                } else if (midPointF.x + w / 2 > mWidth) {
                    canvas.drawText(excel.getXmsg(), mWidth - w / 2, mHCoordinate + dip2px(3) + mTextSize, mAbscissaPaint);
                } else {
                    canvas.drawText(excel.getXmsg(), midPointF.x, mHCoordinate + dip2px(3) + mTextSize, mAbscissaPaint);
                }
            }
        }
    }

    private void drawSelectedText(Canvas canvas, SugExcel excel) {

        mTextPaint.setColor(mTextColor);
        PointF midPointF = excel.getMidPointF();
//        String msg = excel.getUpper() + excel.getUnit();
        String msg = excel.getShowMsg();
        mTextPaint.getTextBounds(msg, 0, msg.length(), mBounds);
        Path textBg = new Path();

        float bgHeight = dip2px(6);
        float bgWidth = dip2px(8);
        if (mChartStyle == ChartStyle.LINE) {
            mTextMarging = tempTextMargin + mLinePointRadio;
        } else {
            mTextMarging = tempTextMargin;
        }
        textBg.moveTo(midPointF.x, midPointF.y - mTextMarging);
        textBg.lineTo(midPointF.x - bgWidth / 2, midPointF.y - mTextMarging - bgHeight - 1.5f);
        textBg.lineTo(midPointF.x + bgWidth / 2, midPointF.y - mTextMarging - bgHeight - 1.5f);
        textBg.close();
        canvas.drawPath(textBg, mTextBgPaint);

        RectF rectF = new RectF(midPointF.x - mBounds.width() / 2f - bgWidth, midPointF.y - mTextMarging - bgHeight - mBounds.height() - bgHeight * 2f
                , midPointF.x + mBounds.width() / 2f + bgWidth, midPointF.y - mTextMarging - bgHeight);
        float dffw = rectF.right - mWidth;
        float msgX = midPointF.x;
        float magin = 1;
        if (dffw > 0) {
            rectF.right = rectF.right - dffw - magin;
            rectF.left = rectF.left - dffw - magin;
            msgX = midPointF.x - dffw - magin;
        } else if (rectF.left < 0) {
            rectF.right = rectF.right - rectF.left + magin;
            msgX = midPointF.x - rectF.left + magin;
            rectF.left = magin;
        }
        canvas.drawRoundRect(rectF, 3, 3, mTextBgPaint);
        canvas.drawText(msg, msgX, midPointF.y - mTextMarging - bgHeight * 2, mTextPaint);
    }

    /**
     * 折线集合
     */
    private List<Path> mPathList = new ArrayList<>();
    /**
     * 虚线集合
     */
    private List<Path> mDashPathList = new ArrayList<>();

    /**
     * 画 折线
     */
    private void drawSugExcel_LINE(Canvas canvas) {
        pathLine.reset();
        mLinePaint.setColor(mLineColor);
        mDashLinePaint.setColor(mLineColor);
        mPointPaint.setColor(mPointColor);
        if (arrangeLineDate(canvas)) return;
        arrangeDashLineDate(canvas);
        for (Path path : mDashPathList) {
            DashPathEffect effect = new DashPathEffect(new float[]{8, 5}, phase);
            mDashLinePaint.setPathEffect(effect);
            canvas.drawPath(path, mDashLinePaint);
        }
        if (mLinePointRadio > 0) {
            for (SugExcel excel : mExcels) {
                if (excel.getHeight() > 0) {
                    PointF midPointF = excel.getMidPointF();
                    canvas.drawCircle(midPointF.x, midPointF.y, mLinePointRadio, mPointPaint);
//                    mCoordinatePaint.setColor(Color.parseColor("#ffffffff"));
                    mCoordinatePaint.setColor(0Xffffffff);
                    mCoordinatePaint.setStrokeWidth(dip2px(2));
                    canvas.drawCircle(midPointF.x, midPointF.y, mLinePointRadio, mCoordinatePaint);
                }
            }
        }
        if (mSelected > -1) {
            drawSelectedText(canvas, mExcels.get(mSelected));
        } else {
            drawSelectedText(canvas, mExcels.get(mHeightestExcel.getIndex()));
        }
        if (mDashPathList.size() > 0) {
            phase = ++phase % 50;
            postInvalidateDelayed(50);
        }
    }

    private void arrangeDashLineDate(Canvas canvas) {
        lineFirstMoved = false;
        mDashPathList.clear();
        for (int i = 0; i < mPathList.size(); i++) {
            Path path = mPathList.get(i);
            PathMeasure pathMeasure = new PathMeasure(path, false);
            float length = pathMeasure.getLength();

            float[] post = new float[2];
            pathMeasure.getPosTan(0, post, null);
            float[] post_end = new float[2];
            pathMeasure.getPosTan(length, post_end, null);
            if (length > 0.001f) {
                canvas.drawPath(path, mLinePaint);
                path.lineTo(post_end[0], mHCoordinate);
                path.lineTo(post[0], mHCoordinate);//移动到起点
                path.close();
                mExecelPaint.setShader(new LinearGradient(0, 0, 0, mHCoordinate, mShaderColors[0], mShaderColors[1], Shader
                        .TileMode
                        .CLAMP));
                canvas.drawPath(path, mExecelPaint);
            } else {
                post_end[0] = post[0];
                post_end[1] = post[1];
            }
            if (i < mPathList.size() - 1) {
                path = new Path();
                path.moveTo(post_end[0], post_end[1]);
                PathMeasure pathMeasuredotted = new PathMeasure(mPathList.get(i + 1), false);
                pathMeasuredotted.getPosTan(0, post, null);
                path.lineTo(post[0], post[1]);
                mDashPathList.add(path);
            }
        }
    }

    private boolean arrangeLineDate(Canvas canvas) {
        Path pathline = null;
        mPathList.clear();
        for (int i = 0; i < mExcels.size(); i++) {
            if (!lineFirstMoved) {
                pathline = new Path();
            }
            SugExcel excel = null;
            if (pathline != null) {
                excel = mExcels.get(i);
                if (null == excel) {
                    return true;
                }
                PointF start = excel.getStart();
                if (mChartStyle == ChartStyle.LINE) {
                    start.x += mSliding;
                }
                PointF midPointF = excel.getMidPointF();
                if (excel.getHeight() > 0) {
                    if (!lineFirstMoved) {
                        pathline.moveTo(midPointF.x, midPointF.y);
                        lineFirstMoved = true;
                    } else {
                        pathline.lineTo(midPointF.x, midPointF.y);
                    }
                } else {
                    if (!pathline.isEmpty()) {
                        PathMeasure pathMeasure = new PathMeasure(pathline, false);
                        if (i > 0 && pathMeasure.getLength() < 0.001f) {
                            PointF midPointFpre = mExcels.get(i - 1).getMidPointF();
                            pathline.lineTo(midPointFpre.x, midPointFpre.y + 0.001f);
                        }
                        mPathList.add(pathline);
                    }
                    lineFirstMoved = false;
                }
                if (i == mExcels.size() - 1 && lineFirstMoved) {
                    PathMeasure pathMeasure = new PathMeasure(pathline, false);
                    if (i > 0 && pathMeasure.getLength() < 0.001f) {
                        pathline.lineTo(midPointF.x, midPointF.y + 0.001f);
                    }
                    mPathList.add(pathline);
                }
            }
            drawAbscissaMsg(canvas, excel);
        }
        return false;
    }


    /**
     * 画 坐标轴
     */
    private void drawCoordinateAxes(Canvas canvas) {
        mCoordinatePaint.setColor(Color.parseColor("#AFAFB0"));
        mCoordinatePaint.setStrokeWidth(2);
        canvas.drawLine(0, mHCoordinate, mWidth, mHCoordinate, mCoordinatePaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mExcels.size() > 0) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mScrollAble) {
                        float moveX = event.getX();
                        mSliding = moveX - mDownX;
                        if (Math.abs(mSliding) > mTouchSlop) {
                            moved = true;
                            mDownX = moveX;
                            if (mExcels.get(0).getStart().x + mSliding > mInterval ||
                                    mExcels.get(mExcels.size() - 1).getStart().x + mBarWidth + mInterval + mSliding <
                                            mWidth) {
                                return true;
                            }
                            invalidate();
                        }
                    } else {
                        PointF tup = new PointF(event.getX(), event.getY());
                        mSelected = clickWhere(tup);
                        invalidate();
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
        lineFirstMoved = false;
        mSelected = -1;
        mExcels.clear();
        if (sugExcelList != null && sugExcelList.size() > 0) {
            mHeightestExcel = sugExcelList.get(0);
            for (SugExcel sugExcel : sugExcelList) {
                mHeightestExcel = mHeightestExcel.getHeight() > sugExcel.getHeight() ? mHeightestExcel : sugExcel;
            }
            for (int i = 0; i < sugExcelList.size(); i++) {
                SugExcel sugExcel = sugExcelList.get(i);
                sugExcel.setWidth(mBarWidth);
                PointF start = sugExcel.getStart();
                start.x = mInterval * (i + 1) + mBarWidth * i;
                sugExcel.setColor(mNormalColor);
                mExcels.add(sugExcel);
            }
            if (mWidth > 0) {
                //已经显示在界面上了 重新设置数据
                if (!mScrollAble) {
                    if (mFixedNums > 0 && mChartStyle == ChartStyle.BAR) {
                        mBarWidth = (mWidth - 2 * (mFixedNums + 1)) * 1f / mFixedNums;
                        mInterval = (mWidth - mBarWidth * mExcels.size()) / (mExcels.size() + 1);
                    } else {
                        mInterval = 2;
                        mBarWidth = (mWidth - mInterval * (mExcels.size() - 1)) / mExcels.size();
                    }
                }
                refreshExcels();
            }
        }
        postInvalidate();
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
        mLineColor = normalColor;
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
        mAbscissaPaint.setTextSize(dip2px(mAbscissaMsgSize));
        mHCoordinate = mHeight - Math.abs(2 * mAbscissaMsgSize);
        refreshExcels();
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
        tempTextMargin = mTextMarging = dip2px(textMarging);
    }


    public float getHCoordinate() {
        return mHCoordinate;
    }


    /**
     * 渐变色
     *
     * @param colors
     */
    public void setExecelPaintShaderColors(int... colors) {
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
     *
     * @param linePointRadio
     */
    public void setLinePointRadio(float linePointRadio) {
        mLinePointRadio = dip2px(linePointRadio);
    }

    public void setPointColor(int pointColor) {
        mPointColor = pointColor;
    }

    public void setLineWidth(int width) {
        mLinePaint.setStrokeWidth(dip2px(width));
        mDashLinePaint.setStrokeWidth(dip2px(width));
    }

    public void setLineColor(int color) {
        mLineColor = color;
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
