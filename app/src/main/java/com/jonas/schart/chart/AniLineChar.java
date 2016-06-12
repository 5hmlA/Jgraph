package com.jonas.schart.chart;

import android.animation.ValueAnimator;
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
import android.view.animation.DecelerateInterpolator;

import com.jonas.schart.chartbean.SugExcel;
import com.jonas.schart.superi.SuperChart;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @author jiangzuyun.
 * @date 2016/6/8
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 */
public class AniLineChar extends SuperChart {

    private static final long ANIDURATION = 6000;
    private int mLineStyle = LineStyle.LINE_CURVE;
    private PathMeasure mPathMeasure;
    /**
     * linepath动画存储点
     */
    private float[] mCurPosition = new float[2];
    private PointF mPrePoint;
    /**
     * 折线两点的间隔
     */
    private float mBetween2Excel;
    private ValueAnimator mValueAnimator;

    public static interface LineStyle {
        /**
         * 折线
         */
        int LINE_BROKEN = 1;
        /**
         * 曲线
         */
        int LINE_CURVE = 2;

    }

    private boolean moved;
    private float mDownX;

    private float mBarWidth = -1;
    /**
     * 最高的点
     */
    private SugExcel mHeightestExcel;
    /**
     * 图表显示的区域 x轴起点  左边为刻度
     */
    private RectF mChartArea;
    private Paint mLinePaint;
    private float mLineWidth = 23;
    private int mLineColor = Color.RED;
    /**
     * 坐标信息 字体大小
     */
    private int mAbscissaMsgSize;
    /**
     * 横坐标 信息颜色
     */
    private int mAbscissaMsgColor = Color.parseColor("#556A73");
    /**
     * 画坐标信息
     */
    private Paint mAbscissaPaint;
    private float mYaxis_Max = 100;
    private float mYaxis_min;
    private int mYaxis_showYnum;
    private float mHeightRatio;
    /**
     * 图表 在横轴 上方多少距离
     */
    private int mAbove;
    private boolean allowInterval_left_right = true;
    /**
     * 渐变色
     */
    private int[] mShaderColors;
    /**
     * 虚线 用移动
     */
    private float phase = 3;
    private Paint mAbscisDashPaint;
    /**
     * 动画用的变俩
     */
    private float aniRatio = -1;
    private Path mLinePath = new Path();
    private Path mAniLinePath = new Path();
    /**
     * 是否固定 柱子宽度
     */
    private boolean mFixBarWidth;

    /**
     * 图表出现动画旋转角度
     */
    private float mAniRotateRatio = 0;

    /**
     * 图表区域 最右边 横坐标
     */
    private float mChartRithtest_x;
    private float mChartLeftest_x;
    /**
     * 图表区域的宽度
     */
    private float mCharAreaWidth;
    private boolean mNeedY_abscissMasg = true;

    public AniLineChar(Context context) {
        super(context);
    }

    public AniLineChar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AniLineChar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mChartStyle = ChartStyle.LINE;
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAbscissaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAbscisDashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        initializeData();
    }

    private void initializeData() {
        mBarWidth = dip2px(16);//默认的柱子宽度
        mInterval = dip2px(4);//默认的间隔大小
        mAbscissaMsgSize = sp2px(12);//坐标轴信息
        mAbscissaMsgColor = Color.parseColor("#556A73");
        mLineColor = Color.RED;
        mLineWidth = dip2px(3);

        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(mLineWidth);

        mAbscissaPaint.setTextAlign(Paint.Align.CENTER);
        mAbscissaPaint.setTextSize(mAbscissaMsgSize);
        mAbscissaPaint.setColor(mAbscissaMsgColor);
        mAbscisDashPaint.setStrokeWidth(1);
        mAbscisDashPaint.setStyle(Paint.Style.STROKE);
        mAbscisDashPaint.setColor(mAbscissaMsgColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refreshChartArea();
        refreshChartSetData();
        System.out.println("onSizeChanged");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(mAniRotateRatio);
        canvas.translate(mSliding, 0);//大于0 往右移动 小于0往左移动
        super.onDraw(canvas);
        if (mNeedY_abscissMasg) {
            drawYabscissaMsg(canvas);
        }
    }

    /**
     * 刷新 画图表的区域
     */
    private void refreshChartArea() {
        float yMsgLength = 0;
        float yMsgHeight = 0;
        if (mNeedY_abscissMasg) {
            //如果 需要 纵轴坐标的时候
            String yaxismax = new DecimalFormat("##.#").format(mYaxis_Max);
            yMsgLength = mAbscissaPaint.measureText(yaxismax, 0, yaxismax.length());
            Rect bounds = new Rect();
            mAbscissaPaint.getTextBounds(yaxismax, 0, yaxismax.length(), bounds);
            yMsgLength = bounds.width() < yMsgLength ? bounds.width() : yMsgLength;
            yMsgHeight = bounds.height();
        }
        if (allowInterval_left_right) {
            //如过 允许 图表左右两边留有 间隔的时候
            mChartArea = new RectF(yMsgLength + getPaddingLeft() + mInterval, getPaddingTop() + yMsgHeight
                    , mWidth + getPaddingLeft() - mInterval, getPaddingTop() + mHeight - 2 * mAbscissaMsgSize);
        } else {
            mChartArea = new RectF(yMsgLength + getPaddingLeft(), getPaddingTop() + yMsgHeight
                    , mWidth + getPaddingLeft(), getPaddingTop() + mHeight - 2 * mAbscissaMsgSize);
        }
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
                        mSliding += moveX - mDownX;
                        if (Math.abs(mSliding) > mTouchSlop) {
                            moved = true;
                            mDownX = moveX;
                            if (mExcels != null && mExcels.size() > 0) {
                                //防止 图表 滑出界面看不到
                                mSliding = mSliding >= 0 ? 0 : mSliding <= -(mChartRithtest_x - mCharAreaWidth) ? -(mChartRithtest_x - mCharAreaWidth) : mSliding;
                            } else {
                                mSliding = mSliding >= 0 ? 0 : mSliding;
                            }
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

    @Override
    protected void drawSugExcel_LINE(Canvas canvas) {
        //不跳过为0的点
        lineWithEvetyPoint(canvas);
        //跳过为0的点（断开，虚线链接）
//        lineSkip0Point(canvas);

    }

    /**
     * 为画笔 设置 渲染器
     *
     * @param paint
     */
    private void paintSetShader(Paint paint) {
        if (mShaderColors != null && mShaderColors.length > 1) {
            float[] position = new float[mShaderColors.length];
            float v = 1f / mShaderColors.length;
            float temp = 0;
            for (int i = 0; i < mShaderColors.length; i++) {
                position[i] = temp;
                temp += v;
            }
            paint.setShader(new LinearGradient(mChartArea.left, mChartArea.top, mChartArea.left, mChartArea.bottom
                    , mShaderColors, position, Shader.TileMode.CLAMP));
        }
    }


    private void lineWithEvetyPoint(Canvas canvas) {
        paintSetShader(mLinePaint);
        if (aniRatio == -1) {
            canvas.drawPath(mLinePath, mLinePaint);
            for (int i = 0; i < mExcels.size(); i++) {
                SugExcel sugExcel = mExcels.get(i);
                drawAbscissaMsg(canvas, sugExcel);
            }
        } else if (mCurPosition != null) {
            //动画
            if (mCurPosition[0] <= mChartLeftest_x) {
                mPrePoint = mExcels.get(0).getMidPointF();
                mAniLinePath.moveTo(mPrePoint.x, mPrePoint.y);
            } else {
                if (mPrePoint == null) {
                    mPrePoint = mExcels.get(0).getMidPointF();
                }
                if (mLineStyle == LineStyle.LINE_CURVE) {
                    float controllA_X = (mPrePoint.x + mCurPosition[0]) / 2;
                    float controllA_Y = mPrePoint.y;
                    float controllB_X = (mPrePoint.x + mCurPosition[0]) / 2;
                    float controllB_Y = mCurPosition[1];
                    mAniLinePath.cubicTo(controllA_X, controllA_Y, controllB_X, controllB_Y, mCurPosition[0], mCurPosition[1]);
                } else {
                    mAniLinePath.lineTo(mCurPosition[0], mCurPosition[1]);
                }
                mPrePoint.x = mCurPosition[0];
                mPrePoint.y = mCurPosition[1];
            }
            canvas.drawPath(mAniLinePath, mLinePaint);
            SugExcel sugExcel = mExcels.get((int) ((mCurPosition[0]-mChartArea.left)/mBetween2Excel));
            drawAbscissaMsg(canvas, sugExcel);
        }
    }

    private void lineSkip0Point(Canvas canvas) {
//        mLinePath.reset();
        for (int i = 0; i < mExcels.size(); i++) {
            SugExcel sugExcel = mExcels.get(i);
            PointF midPointF = sugExcel.getMidPointF();
            if (i == 0) {
                mLinePath.moveTo(midPointF.x, midPointF.y);
            } else {
                mLinePath.lineTo(midPointF.x, midPointF.y);
            }
            drawAbscissaMsg(canvas, sugExcel);
        }
    }

    /**
     * 画纵坐标信息
     *
     * @param canvas
     */
    private void drawYabscissaMsg(Canvas canvas) {
        mAbscissaPaint.setTextAlign(Paint.Align.LEFT);
        float diffLevel = (mYaxis_Max - mYaxis_min) / ((float) mYaxis_showYnum);
        float diffCoordinate = diffLevel * mHeightRatio;
        for (int i = 0; i <= mYaxis_showYnum; i++) {
            float levelCoordinate = mChartArea.bottom - diffCoordinate * i;
            canvas.drawText(new DecimalFormat("#").format(mYaxis_min + diffLevel * i), getPaddingLeft(), levelCoordinate, mAbscissaPaint);
            if (i > 0) {
                Path dashPath = new Path();
                dashPath.moveTo(mChartArea.left, levelCoordinate);
                if (mExcels != null && mExcels.size() > 0) {
                    dashPath.lineTo(mChartRithtest_x, levelCoordinate);
                } else {
                    dashPath.lineTo(mChartArea.right, levelCoordinate);
                }
                mAbscisDashPaint.setPathEffect(pathDashEffect());
                canvas.drawPath(dashPath, mAbscisDashPaint);
            }
        }
    }

    private DashPathEffect pathDashEffect() {                     //线，段，线，段
        DashPathEffect dashEffect = new DashPathEffect(new float[]{4, 4}, phase);
        return dashEffect;
    }

    /**
     * 画 对应的 横轴信息
     *
     * @param canvas
     * @param excel
     */
    private void drawAbscissaMsg(Canvas canvas, SugExcel excel) {
        mAbscissaPaint.setTextAlign(Paint.Align.CENTER);
        if (null != excel) {
            PointF midPointF = excel.getMidPointF();
            if (!TextUtils.isEmpty(excel.getXmsg())) {
                String xmsg = excel.getXmsg();
                float w = mAbscissaPaint.measureText(xmsg, 0, xmsg.length());
                if (!mScrollAble) {
                    if (midPointF.x - w / 2 < 0) {
                        //最左边
                        canvas.drawText(excel.getXmsg(), w / 2, mChartArea.bottom + dip2px(3) + mAbscissaMsgSize, mAbscissaPaint);
                    } else if (midPointF.x + w / 2 > mWidth) {
                        //最右边
                        canvas.drawText(excel.getXmsg(), mWidth - w / 2, mChartArea.bottom + dip2px(3) + mAbscissaMsgSize, mAbscissaPaint);
                    } else {
                        canvas.drawText(excel.getXmsg(), midPointF.x, mChartArea.bottom + dip2px(3) + mAbscissaMsgSize, mAbscissaPaint);
                    }
                } else {
                    canvas.drawText(excel.getXmsg(), midPointF.x, mChartArea.bottom + dip2px(3) + mAbscissaMsgSize, mAbscissaPaint);
                }
            }
        }
    }

    protected void drawCoordinateAxes(Canvas canvas) {

        if (mExcels != null && mExcels.size() > 0) {
            canvas.drawLine(mChartArea.left, mChartArea.bottom, mExcels.get(mExcels.size() - 1).getMidPointF().x, mChartArea.bottom, mCoordinatePaint);
        } else {
            canvas.drawLine(mChartArea.left, mChartArea.bottom, mChartArea.right, mChartArea.bottom, mCoordinatePaint);
        }
    }

    @Override
    public void setChartStyle(int chartStyle) {
        mChartStyle = ChartStyle.LINE;
    }

    /**
     * 传入 数据
     */
    public void cmdFill(List<SugExcel> sugExcelList) {
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
                mExcels.add(sugExcel);
            }
            if (!mScrollAble && mForceFixNums && mExcels.size() > mVisibleNums) {
                //如果不可滚动的话 同时要显示固定个数 那么为防止显示不全 将可见个数设置为柱子数量
                mVisibleNums = mExcels.size();
            }
            if (mWidth > 0) {
                //已经显示在界面上了 重新设置数据
                refreshChartSetData();
            }
        }
        postInvalidate();
    }

    private void refreshChartSetData() {
        if (mChartStyle == ChartStyle.BAR) {
            //柱状图默认 间隔固定
            mInterval = mInterval == 0 ? 2 : mInterval;//没设置宽度的时候 默认4设置了就用设置的
            mFixBarWidth = false;
        } else {
            //折线图 默认柱子宽度固定 小点
            mBarWidth = mBarWidth == -1 ? 4 : mBarWidth;//没设置宽度的时候 默认4设置了就用设置的
            mFixBarWidth = true;
        }

        //画 图表区域的宽度
        mCharAreaWidth = mChartArea.right - mChartArea.left;
        //不可滚动 则必须全部显示在界面上  无视mVisibleNums
        if (!mFixBarWidth) {
            //间隔 minterval默认 计算柱子宽度
            if (!mScrollAble) {
                //不可滚动的时候
                if (mForceFixNums) {
                    //固定 显示个数
                    //根据mVisibleNums计算mBarWidth宽度mInterval固定
                    mBarWidth = (mCharAreaWidth - mInterval * (mVisibleNums - 1)) / mVisibleNums;
                } else {
                    //所有柱子 平分 整个区域
                    mBarWidth = (mCharAreaWidth - mInterval * (mExcels.size() - 1)) / mExcels.size();
                }
            } else {
                if (mVisibleNums == -1) {
                    //默认初始化 可见个数
                    mVisibleNums = mExcels.size() < 5 ? mExcels.size() : 5;
                }
                mBarWidth = (mCharAreaWidth - mInterval * (mVisibleNums - 1)) / mVisibleNums;
            }
        } else {

            if (!mScrollAble) {
                if (mForceFixNums) {
                    //固定 显示个数 主要作用于 显示个数 大于mExcel.size
                    //根据mVisibleNums计算mBarWidth宽度mInterval固定
                    mInterval = (mCharAreaWidth - mBarWidth * mVisibleNums) / (mVisibleNums - 1);
                } else {
                    //所有柱子 平分 整个区域
                    mInterval = (mCharAreaWidth - mBarWidth * mExcels.size()) / (mExcels.size() - 1);
                }
            } else {
                if (mVisibleNums == -1) {
                    //默认初始化 可见个数
                    mVisibleNums = mExcels.size() < 5 ? mExcels.size() : 5;
                }
                //可滚动
                mInterval = (mCharAreaWidth - mBarWidth * mVisibleNums) / (mVisibleNums - 1);
            }
        }
        refreshExcels();
    }

    /**
     * 主要 刷新高度
     */
    private void refreshExcels() {
        if (mExcels == null) {
            return;
        }
        if (mYaxis_Max <= 0) {
            mYaxis_Max = mHeightestExcel.getHeight();
        }
        mHeightRatio = (mChartArea.bottom - mChartArea.top) / (mYaxis_Max - mYaxis_min);
        //填充 path
        mLinePath.reset();
        for (int i = 0; i < mExcels.size(); i++) {
            SugExcel sugExcel = mExcels.get(i);
            sugExcel.setHeight((sugExcel.getHeight() - mYaxis_min) * mHeightRatio);//刷新在画布中的高度
            sugExcel.setWidth(mBarWidth);
            PointF start = sugExcel.getStart();
            //刷新 每个柱子矩阵左下角坐标
            start.x = mChartArea.left + mBarWidth * i + mInterval * i;
            start.y = mChartArea.bottom - mAbove - sugExcel.getLower();
            sugExcel.setColor(mNormalColor);
            if (mLineStyle == LineStyle.LINE_BROKEN) {
                PointF midPointF = sugExcel.getMidPointF();
                if (i == 0) {
                    mLinePath.moveTo(midPointF.x, midPointF.y);
                } else {
                    mLinePath.lineTo(midPointF.x, midPointF.y);
                }
            }
        }

        if (mLineStyle == LineStyle.LINE_CURVE) {
            for (int i = 0; i < mExcels.size(); i++) {
                SugExcel sugExcel = mExcels.get(i);
                if (i < mExcels.size() - 1) {
                    PointF startPoint = sugExcel.getMidPointF();
                    PointF endPoint = mExcels.get(i + 1).getMidPointF();//下一个点
                    if (i == 0) mLinePath.moveTo(startPoint.x, startPoint.y);
                    float controllA_X = (startPoint.x + endPoint.x) / 2;
                    float controllA_Y = startPoint.y;
                    float controllB_X = (startPoint.x + endPoint.x) / 2;
                    float controllB_Y = endPoint.y;
                    mLinePath.cubicTo(controllA_X, controllA_Y, controllB_X, controllB_Y, endPoint.x, endPoint.y);
                }
            }
        }
        mChartRithtest_x = mExcels.get(mExcels.size() - 1).getMidPointF().x;
        mChartLeftest_x = mExcels.get(0).getMidPointF().x;
        if (mExcels.size()>1) {
            mBetween2Excel = mExcels.get(1).getMidPointF().x - mExcels.get(0).getMidPointF().x;
        }
        aniShowChar_growing();
    }

    public void aniShowChar_growing() {
        mAniLinePath.reset();
        mPathMeasure = new PathMeasure(mLinePath, false);
        mPathMeasure.getPosTan(0, mCurPosition, null);
        aniShowChar(mPathMeasure.getLength());
    }

    public void aniShowChar(final float end) {
        if (mValueAnimator == null) {
            mValueAnimator = ValueAnimator.ofFloat(0, end);
        } else {
            mValueAnimator.cancel();
            mAniLinePath.reset();
            mValueAnimator.setFloatValues(0, end);
        }
        mValueAnimator.setDuration(ANIDURATION);
        mValueAnimator.setInterpolator(new DecelerateInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                aniRatio = (float) animation.getAnimatedValue();
                if (mChartStyle == ChartStyle.LINE) {
                    //mCurPosition必须要初始化mCurPosition = new float[2];
                    mPathMeasure.getPosTan(aniRatio, mCurPosition, null);
                    if (aniRatio == end) {
                        aniRatio = -1;
                    }
                }
                invalidate();
            }
        });
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mValueAnimator.start();
            }
        }, 1000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mExcels.clear();
        mLinePath = null;
        mShaderColors = null;
        mChartArea = null;
        mPathMeasure=null;
        mCurPosition = null;
    }

    @Override
    public void setInterval(float interval) {
        super.setInterval(interval);
        refreshChartSetData();
    }

    /**
     * 设置y轴 刻度 信息
     *
     * @param min      y轴 显示的最小值
     * @param max      y轴显示的最大值
     * @param showYnum y轴显示 刻度数量
     */
    public void setYaxisValues(int min, int max, int showYnum) {
        mYaxis_Max = max;
        mYaxis_min = min;
        mYaxis_showYnum = showYnum;
    }

    /**
     * 设置y轴 刻度 信息  0开始
     *
     * @param max      y轴显示的最大值
     * @param showYnum y轴显示 刻度数量
     */
    public void setYaxisValues(int max, int showYnum) {
        setYaxisValues(0, max, showYnum);
    }

    /**
     * 坐标 信息 文字颜色
     *
     * @param abscissaMsgColor
     */
    public void setAbscissaMsgColor(int abscissaMsgColor) {
        mAbscissaMsgColor = abscissaMsgColor;
    }

    /**
     * 坐标 信息 文字大小
     *
     * @param abscissaMsgSize
     */
    public void setAbscissaMsgSize(int abscissaMsgSize) {
        mAbscissaMsgSize = abscissaMsgSize;
    }

    /**
     * 线条 颜色
     *
     * @param lineColor
     */
    public void setLineColor(int lineColor) {
        mLineColor = lineColor;
    }

    /**
     * 线条 宽度
     *
     * @param lineWidth
     */
    public void setLineWidth(float lineWidth) {
        mLineWidth = lineWidth;
    }

    /**
     * 渐变色
     * 从上到下
     *
     * @param colors
     */
    public void setShaderColors(int... colors) {
        mShaderColors = colors;
    }

    /**
     * y轴 显示的最小刻度
     *
     * @param yaxis_min
     */
    public void setYaxis_min(float yaxis_min) {
        mYaxis_min = yaxis_min;
    }

    /**
     * y轴 显示的刻度数量
     *
     * @param yaxis_showYnum
     */
    public void setYaxis_showYnum(int yaxis_showYnum) {
        mYaxis_showYnum = yaxis_showYnum;
    }

    /**
     * y轴 显示的最大刻度
     *
     * @param yaxis_Max
     */
    public void setYaxis_Max(float yaxis_Max) {
        mYaxis_Max = yaxis_Max;
    }

    /**
     * 图表 最左最右 留间隔
     *
     * @param allowInterval_left_right
     */
    public void setAllowInterval_left_right(boolean allowInterval_left_right) {
        this.allowInterval_left_right = allowInterval_left_right;
    }

    /**
     * 图表 距离 横轴的距离
     *
     * @param above
     */
    public void setAbove(int above) {
        mAbove = above;
    }

    public void setNeedY_abscissMasg(boolean needY_abscissMasg) {
        mNeedY_abscissMasg = needY_abscissMasg;
    }

    public float getAniRatio() {
        return aniRatio;
    }

    public void setAniRatio(float aniRatio) {
        this.aniRatio = aniRatio;
    }

    public float getAniRotateRatio() {
        return mAniRotateRatio;
    }

    public void setAniRotateRatio(float aniRotateRatio) {
        mAniRotateRatio = aniRotateRatio;
        invalidate();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        System.out.println("onWindowVisibilityChanged");
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        System.out.println("onWindowFocusChanged");
    }
}
//可滚动   根据可见个数 计算barwidth
//
//不可滚动
//   1 无视mVisibleNums 所有柱子平均显示
//   2 固定最多个数mVisibleNums 大于等于mExecels的数量
//