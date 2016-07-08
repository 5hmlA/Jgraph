package com.jonas.jdiagram.graph;

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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jonas.jdiagram.inter.SuperGraph;
import com.jonas.jdiagram.models.Jchart;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @author jiangzuyun.
 * @date 2016/6/8
 * @des [一句话描述]
 * @since [产品/模版版本]
 */

public class LineChar extends SuperGraph {

    private boolean moved;
    private float mDownX;

    private boolean lineFirstMoved;
    private float mBarWidth = -1;
    /**
     * 最高的点
     */
    private Jchart mHeightestExcel;
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
    private Paint mAbscisDashPaint;
    /**
     * 动画用的变俩
     */
    private float aniRatio = 1;
    private Path mLinePath = new Path();
    /**
     * 是否固定 柱子宽度
     */
    private boolean mFixBarWidth;

    /**
     * 图表出现动画旋转角度
     */
    private float mAniRotateRatio = 0;
    private float mChartRithtest_x;
    /**
     * 图表区域的宽度
     */
    private float mCharAreaWidth;
    private boolean mNeedY_abscissMasg;

    public LineChar(Context context) {
        super(context);
    }

    public LineChar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineChar(Context context, AttributeSet attrs, int defStyleAttr) {
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
//        refreshExcels();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(mAniRotateRatio);
        canvas.translate(mSliding, 0);//大于0 往右移动 小于0往左移动
//        mLinePaint.setStrokeWidth(1);
//        canvas.drawRect(mChartArea, mLinePaint);
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
            String yaxismax = mYaxis_Max + "";
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
        if (mJcharts.size() > 0) {
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
                            if (mJcharts != null && mJcharts.size() > 0) {
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
        for (int i = 0; i < mJcharts.size(); i++) {
            Jchart excel = mJcharts.get(i);
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
//        Path mPath = new Path();
//        mPath.moveTo(50, 150);
//        mPath.cubicTo(125, 150, 125, 50, 200, 50);
//        canvas.drawPath(mPath, mLinePaint);
        if (mShaderColors != null && mShaderColors.length > 1) {
            float[] position = new float[mShaderColors.length];
            float v = 1f / mShaderColors.length;
            float temp = 0;
            for (int i = 0; i < mShaderColors.length; i++) {
                position[i] = temp;
                temp += v;
            }

            mLinePaint.setShader(new LinearGradient(mChartArea.left, mChartArea.top, mChartArea.left, mChartArea.bottom
                    , mShaderColors, position, Shader.TileMode.CLAMP));
        }
        canvas.drawPath(mLinePath, mLinePaint);

//        mLinePath.reset();
        for (int i = 0; i < mJcharts.size(); i++) {
            Jchart jchart = mJcharts.get(i);
//            if (i<mJcharts.size()-1) {
//                PointF startPoint = mJcharts.get(i).getMidPointF();
//                PointF endPoint = mJcharts.get(i + 1).getMidPointF();
//                if (i == 0) mLinePath.moveTo(startPoint.x, startPoint.y);
//                float controllA_X = (startPoint.x + endPoint.x) /2;
//                float controllA_Y = startPoint.y;
//                float controllB_X = (startPoint.x + endPoint.x) /2;
//                float controllB_Y = endPoint.y;
//                mLinePath.cubicTo(controllA_X, controllA_Y, controllB_X, controllB_Y, endPoint.x, endPoint.y);
//                canvas.drawCircle(controllA_X,controllA_Y,2,mLinePaint);
//                canvas.drawCircle(controllB_X,controllB_Y,2,mLinePaint);
//                canvas.drawCircle(startPoint.x,startPoint.y,2,mLinePaint);
//                canvas.drawLine(startPoint.x,startPoint.y,controllA_X,controllA_Y,mLinePaint);
//                canvas.drawLine(endPoint.x,endPoint.y,controllB_X,controllB_Y,mLinePaint);
//            }
            drawAbscissaMsg(canvas, jchart);
        }
    }

    private void lineSkip0Point(Canvas canvas) {
        mLinePath.reset();
        for (int i = 0; i < mJcharts.size(); i++) {
            Jchart jchart = mJcharts.get(i);
            PointF midPointF = jchart.getMidPointF();
            if (i == 0) {
                mLinePath.moveTo(midPointF.x, midPointF.y);
            } else {
                mLinePath.lineTo(midPointF.x, midPointF.y);
            }
            drawAbscissaMsg(canvas, jchart);
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
                if (mJcharts != null && mJcharts.size() > 0) {
                    dashPath.lineTo(mChartRithtest_x, levelCoordinate);
                } else {
                    dashPath.lineTo(mChartArea.right, levelCoordinate);
                }
                mAbscisDashPaint.setPathEffect(pathDashEffect(new float[]{4, 4}));
                canvas.drawPath(dashPath, mAbscisDashPaint);
            }
        }
    }

    /**
     * 画 对应的 横轴信息
     *
     * @param canvas
     * @param excel
     */
    private void drawAbscissaMsg(Canvas canvas, Jchart excel) {
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

        if (mJcharts != null && mJcharts.size() > 0) {
            canvas.drawLine(mChartArea.left, mChartArea.bottom, mJcharts.get(mJcharts.size() - 1).getMidPointF().x, mChartArea.bottom, mCoordinatePaint);
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
    public void cmdFill(List<Jchart> jchartList) {
        lineFirstMoved = false;
        mSelected = -1;
        mJcharts.clear();
        if (jchartList != null && jchartList.size() > 0) {
            mHeightestExcel = jchartList.get(0);
            for (Jchart jchart : jchartList) {
                mHeightestExcel = mHeightestExcel.getHeight() > jchart.getHeight() ? mHeightestExcel : jchart;
            }
            for (int i = 0; i < jchartList.size(); i++) {
                Jchart jchart = jchartList.get(i);
                jchart.setWidth(mBarWidth);
                PointF start = jchart.getStart();
                start.x = mInterval * (i + 1) + mBarWidth * i;
                mJcharts.add(jchart);
            }
            if (!mScrollAble && mForceFixNums && mJcharts.size() > mVisibleNums) {
                //如果不可滚动的话 同时要显示固定个数 那么为防止显示不全 将可见个数设置为柱子数量
                mVisibleNums = mJcharts.size();
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
                    mBarWidth = (mCharAreaWidth - mInterval * ( mJcharts.size() - 1)) / mJcharts.size();
                }
            } else {
                if (mVisibleNums == -1) {
                    //默认初始化 可见个数
                    mVisibleNums = mJcharts.size() < 5 ? mJcharts.size() : 5;
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
                    mInterval = (mCharAreaWidth - mBarWidth * mJcharts.size()) / ( mJcharts.size() - 1);
                }
            } else {
                if (mVisibleNums == -1) {
                    //默认初始化 可见个数
                    mVisibleNums = mJcharts.size() < 5 ? mJcharts.size() : 5;
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
        if (mYaxis_Max <= 0) {
            mYaxis_Max = mHeightestExcel.getHeight();
        }
//        mHeightRatio = (mChartArea.bottom - mChartArea.top) / (mHeightestExcel.getHeight() - mYaxis_min);
        mHeightRatio = (mChartArea.bottom - mChartArea.top) / (mYaxis_Max - mYaxis_min);

        for (int i = 0; i < mJcharts.size(); i++) {
            Jchart jchart = mJcharts.get(i);
            jchart.setHeight(( jchart.getHeight() - mYaxis_min) * mHeightRatio);//刷新在画布中的高度
            jchart.setWidth(mBarWidth);
            PointF start = jchart.getStart();
            //刷新 每个柱子矩阵左下角坐标
            start.x = mChartArea.left + mBarWidth * i + mInterval * i;
            start.y = mChartArea.bottom - mAbove - jchart.getLower();
            jchart.setColor(mNormalColor);
        }

        //填充 path
        mLinePath.reset();
        for (int i = 0; i < mJcharts.size(); i++) {
            Jchart jchart = mJcharts.get(i);

//            PointF midPointF = jchart.getMidPointF();
//            if (i == 0) {
//                mLinePath.moveTo(midPointF.x, midPointF.y );
//            } else {
//                mLinePath.lineTo(midPointF.x, midPointF.y );
//            }

            if (i < mJcharts.size() - 1) {
                PointF startPoint = jchart.getMidPointF();
                PointF endPoint = mJcharts.get(i + 1).getMidPointF();//下一个点
                if (i == 0) mLinePath.moveTo(startPoint.x, startPoint.y);
                float controllA_X = (startPoint.x + endPoint.x) / 2;
                float controllA_Y = startPoint.y;
                float controllB_X = (startPoint.x + endPoint.x) / 2;
                float controllB_Y = endPoint.y;
                mLinePath.cubicTo(controllA_X, controllA_Y, controllB_X, controllB_Y, endPoint.x, endPoint.y);
            }
        }
        mChartRithtest_x = mJcharts.get(mJcharts.size() - 1).getMidPointF().x;
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


}
//可滚动   根据可见个数 计算barwidth
//
//不可滚动
//   1 无视mVisibleNums 所有柱子平均显示
//   2 固定最多个数mVisibleNums 大于等于mExecels的数量
//