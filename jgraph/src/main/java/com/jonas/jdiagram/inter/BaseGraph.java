package com.jonas.jdiagram.inter;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.BounceInterpolator;

import com.jonas.jdiagram.BuildConfig;
import com.jonas.jdiagram.R;
import com.jonas.jdiagram.models.Jchart;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Author jwx338756
 * @Date: 2016
 * @Description: 折线+虚线
 * @Others: {https://github.com/mychoices}
 */
public abstract class BaseGraph extends View {
    private static final String TAG = BaseGraph.class.getSimpleName();
    /**
     * 选中的 柱状图
     */
    protected int mSelected = -1;
    protected int mHeight;
    protected int mWidth;
    protected Paint mCoordinatePaint;
    /**
     * 画坐标信息
     */
    protected Paint mAbscissaPaint;
    protected Paint mAbscisDashPaint;
    protected Paint mSelectedTextBgPaint;
    protected Paint mSelectedTextPaint;

    protected boolean mNeedY_abscissMasg = true;

    /**
     * 存放 纵轴信息
     */
    protected ArrayList<String> mYaxis_msg;
    protected float mYaxis_Max = 0;
    protected float mYaxis_min;
    /**
     * y轴最长的文字
     */
    protected String mYaxismax = "100";

    /**
     * 允许范围内的误差
     */
    private float mAllowError = 3;
    protected float mAbscisDashLineWidth = 0.5f;

    /**
     * 图表区域 最右边 横坐标
     */
    protected float mChartRithtest_x;
    protected float mChartLeftest_x;
    /**
     * 图表区域的宽度
     */
    protected float mCharAreaWidth;
    private float mHeightRatio;

    /**
     * 图表显示的区域 x轴起点  左边为刻度
     */
    protected RectF mChartArea;

    protected boolean moved;
    protected float mDownX;

    /**
     * 顶部选中文字 三角尖的高度
     */
    protected float mBgTriangleHeight;

    protected float mBarWidth = -1;

    /**
     * 选中文字背景 三角尖处 距离图表的距离
     */
    protected float mSelectedTextMarging = 4;

    protected ArrayList<PointF> mAllPoints = new ArrayList<>();

    /**
     * 间隔
     */
    protected float mInterval = 0;

    /**
     * 图表 在横轴 上方多少距离
     */
    private int mAbove;

    /**
     * 是否允许 滚动
     */
    protected boolean mScrollAble;
    /**
     * 是否固定 柱子宽度
     */
    protected boolean mFixBarWidth;

    /**
     * 控件 中心 点
     */
    protected PointF mCenterPoint;

    /**
     * 可见的 个数(柱状图(最多可见的柱子数量)/折线图(最多可见的点))
     */
    protected int mVisibleNums = -1;

    /**
     * 在不可滚动时 最多显示可见个数
     * <p color="red">mVisibleNums>=mExecels.size 否则部分不可见
     */
    protected boolean mForceFixNums;

    /**
     * 选中模式 为-1 表示不处理点击选中状态
     */
    protected int mSelectedMode = -1;
    /**
     * 最高的点
     */
    protected Jchart mHeightestChart;
    protected Jchart mMinestChart;
    protected Jchart mLastJchart;
    protected Jchart mFirstJchart;
    /**
     * 横轴 文字大小
     */
    private int mAbscissaMsgSize;
    protected int mState = 0;

    public interface SelectedMode {
        /**
         * 选中的 颜色变  显示所有柱子 文字
         */
        int selectedActivated = 0;
        /**
         * 选中的 显示 柱子 文字 其他不显示
         */
        int selecetdMsgShow_Top = 1;
    }

    protected interface State {
        int aniChange = 1;
        int aniShow = 2;
        int aniFinish = 3;
    }

    public interface GraphStyle {
        int BAR = 1;
        int LINE = 2;
        int BAR_LINE = 3;
    }

    protected Context mContext;
    /**
     * 系统认为发生滑动的最小距离
     */
    protected int mTouchSlop;

    /**
     * 图表 数据集合
     */
    protected List<Jchart> mJcharts = new ArrayList<>();

    /**
     * 柱状图 选中的颜色
     */
    protected int mActivationColor = Color.RED;

    /**
     * 柱状图 未选中选中的颜色
     */
    protected int mNormalColor = Color.DKGRAY;

    /**
     * 要画的 图表的 风格
     */
    protected int mGraphStyle = GraphStyle.LINE;

    /**
     * 滑动 距离
     */
    protected float mSliding = 0;
    /**
     * 虚线 用移动
     */
    protected float mPhase = 3;

    /**
     * 存储原始数据 /上一次的数据
     */
    protected ArrayList<PointF> mAllLastPoints;
    protected ValueAnimator mValueAnimator = ValueAnimator.ofFloat(0, 1);

    /**
     * 越来越快	AccelerateInterpolator()
     * 越来越慢	DecelerateInterpolator()
     * 先快后慢	AccelerateDecelerateInterpolator()
     * 先后退一小步然后向前加速	AnticipateInterpolator()
     * 快速到达终点超出一小步然后回到终点	OvershootInterpolator()
     * 到达终点超出一小步然后回到终点	AnticipateOvershootInterpolator()
     * 弹球效果，弹几下回到终点	BounceInterpolator()
     * 均匀速度	LinearInterpolator()
     */
    private TimeInterpolator mInterpolator = new BounceInterpolator();
    private static final long ANIDURATION = 1100;

    public BaseGraph(Context context) {
        super(context);
        init(context);
    }


    public BaseGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AndroidJgraph);
        mGraphStyle = a.getInt(R.styleable.AndroidJgraph_graphstyle, 1);
        mScrollAble = a.getBoolean(R.styleable.AndroidJgraph_scrollable, false);
        mNeedY_abscissMasg = a.getBoolean(R.styleable.AndroidJgraph_showymsg, true);
        mNormalColor = a.getColor(R.styleable.AndroidJgraph_normolcolor, Color.parseColor("#676567"));
        a.recycle();
        init(context);
    }


    public BaseGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    protected void init(Context context) {
        mContext = context;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mCoordinatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCoordinatePaint.setStyle(Paint.Style.STROKE);
        mCoordinatePaint.setColor(Color.parseColor("#AFAFB0"));
        mCoordinatePaint.setStrokeWidth(1f);

        mAbscissaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAbscissaPaint.setTextAlign(Paint.Align.CENTER);
        mAbscissaMsgSize = sp2px(12);
        mAbscissaPaint.setTextSize(mAbscissaMsgSize);
        mAbscissaPaint.setColor(Color.parseColor("#556A73"));

        //虚线
        mAbscisDashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAbscisDashPaint.setStrokeWidth(mAbscisDashLineWidth);
        mAbscisDashPaint.setStyle(Paint.Style.STROKE);
        mAbscisDashPaint.setColor(Color.parseColor("#556A73"));

        mSelectedTextBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //显示在顶部 选中的文字背景
        mSelectedTextBgPaint.setColor(Color.GRAY);
        mBgTriangleHeight = dip2px(6);

        mSelectedTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //顶部选中文字 颜色
        mSelectedTextPaint.setTextAlign(Paint.Align.CENTER);
        mSelectedTextPaint.setColor(Color.WHITE);
        mSelectedTextPaint.setTextSize(sp2px(12));

        mBarWidth = dip2px(16);//默认的柱子宽度
        mInterval = dip2px(4);//默认的间隔大小
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h - getPaddingBottom() - getPaddingTop();
        mWidth = w - getPaddingLeft() - getPaddingRight();
        mCenterPoint = new PointF(w / 2f, h / 2f);
        if (mGraphStyle == GraphStyle.BAR || mGraphStyle == GraphStyle.LINE) {
            refreshChartArea();
            refreshChartSetData();
        }
    }

    /**
     * 刷新 画图表的区域
     */
    protected void refreshChartArea() {
        float yMsgLength = 0;
        float yMsgHeight = 0;
        Rect bounds = new Rect();
        mAbscissaPaint.getTextBounds(mYaxismax, 0, mYaxismax.length(), bounds);
        if (mNeedY_abscissMasg) {
            //如果 需要 纵轴坐标的时候
            yMsgLength = mAbscissaPaint.measureText(mYaxismax, 0, mYaxismax.length());
            yMsgLength = bounds.width() < yMsgLength ? bounds.width() : yMsgLength;
        }
        if (mSelectedMode == SelectedMode.selecetdMsgShow_Top) {
            yMsgHeight = bounds.height() + 2.5f * mBgTriangleHeight;
        } else {
            yMsgHeight = bounds.height();
        }
        mChartArea = new RectF(yMsgLength + getPaddingLeft(), getPaddingTop() + yMsgHeight, mWidth + getPaddingLeft(),
                getPaddingTop() + mHeight - 2 * mAbscissaMsgSize);
    }

    /**
     * 获取屏幕 宽高 后 更新 图表区域 矩阵数据 柱子宽 间隔宽度
     */
    protected void refreshChartSetData() {
        if (mGraphStyle == GraphStyle.BAR) {
            //柱状图默认 间隔固定
            mInterval = mInterval == dip2px(4) ? dip2px(4) : mInterval;//没设置宽度的时候 默认4设置了就用设置的
//            mFixBarWidth = false;
        } else {
            //折线图 默认柱子宽度固定 小点
            mBarWidth = 6f;//没设置宽度的时候 默认4设置了就用设置的
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
                    mBarWidth = (mCharAreaWidth - mInterval * (mJcharts.size() - 1)) / mJcharts.size();
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
                    mInterval = (mCharAreaWidth - mBarWidth * mJcharts.size()) / (mJcharts.size() - 1);
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
    protected void refreshExcels() {
        if (mJcharts == null) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "数据为空 ");
            }
            return;
        }
        findTheBestChart();
        mHeightRatio = (mChartArea.bottom - mChartArea.top) / (mYaxis_Max - mYaxis_min);
        for (int i = 0; i < mJcharts.size(); i++) {
            Jchart jchart = mJcharts.get(i);
            jchart.setHeight((jchart.getHeight() - mYaxis_min) * mHeightRatio);//刷新在画布中的高度
            jchart.setWidth(mBarWidth);
            PointF start = jchart.getStart();
            jchart.setIndex(i);
            //刷新 每个柱子矩阵左下角坐标
            start.x = mChartArea.left + mBarWidth * i + mInterval * i;
            start.y = mChartArea.bottom - mAbove - jchart.getLower();
//            jchart.setColor(mNormalColor);
            refreshOthersWithEveryChart(i, jchart);
        }
        mChartRithtest_x = mJcharts.get(mJcharts.size() - 1).getMidPointF().x;
        mChartLeftest_x = mJcharts.get(0).getMidPointF().x;
    }

    /**
     * 根据每一个chart刷新其他需要的数据
     *
     * @param i
     * @param jchart
     */
    protected void refreshOthersWithEveryChart(int i, Jchart jchart) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null != mJcharts && mJcharts.size() > 0) {
            if (mNeedY_abscissMasg && mYaxis_msg != null) {
                drawYabscissaMsg(canvas);
            }
            if (mGraphStyle == GraphStyle.BAR) {
                drawSugExcel_BAR(canvas);
            } else if (mGraphStyle == GraphStyle.LINE) {
                drawSugExcel_LINE(canvas);
            } else {
                drawSugExcel_BAR(canvas);
                drawSugExcel_LINE(canvas);
            }
            //选中模式启用的时候
            if (mSelectedMode == SelectedMode.selecetdMsgShow_Top && !mValueAnimator.isRunning()) {
                if (mSelected > -1) {
                    drawSelectedText(canvas, mJcharts.get(mSelected));
                } else {
                    drawSelectedText(canvas, mJcharts.get(mHeightestChart.getIndex()));
                }
            }
            for (Jchart excel : mJcharts) {
                drawAbscissaMsg(canvas, excel);
            }
        }
        drawCoordinateAxes(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mSelectedMode == -1 && !mScrollAble) {
            return false;
        }
        if (mJcharts.size() > 0) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = event.getX();
//                    if (!mScrollAble) {
//                        return false;
//                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mScrollAble) {
                        float moveX = event.getX();
                        float moving = moveX - mDownX;
                        mSliding += moving;
                        if (Math.abs(moving) > mTouchSlop) {
                            moved = true;
                            mDownX = moveX;
                            if (mJcharts != null && mJcharts.size() > 0) {
                                //防止 图表 滑出界面看不到
                                mSliding = mSliding >= 0 ? 0 : mSliding <= -(mChartRithtest_x - mCharAreaWidth) ? -(mChartRithtest_x - mCharAreaWidth) : mSliding;
                            } else {
                                mSliding = mSliding >= 0 ? 0 : mSliding;
                            }
                            System.out.println(mSliding);
                            invalidate();
                        }
                    } else {
                        PointF tup = new PointF(event.getX(), event.getY());
                        mSelected = clickWhere(tup);
                        if (BuildConfig.DEBUG) Log.d(TAG, "selected " + mSelected);
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!moved) {
                        PointF tup = new PointF(event.getX(), event.getY());
                        mSelected = clickWhere(tup);
                        if (BuildConfig.DEBUG) Log.d(TAG, "selected " + mSelected);
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
    protected int clickWhere(PointF tup) {
        if (mChartArea == null) {
            return -1;
        }
        float clickEffective_x = tup.x - mChartArea.left;
        if (clickEffective_x > 0) {
            clickEffective_x = tup.x - mChartArea.left - mBarWidth - mInterval / 2;
            if (clickEffective_x > 0) {
                int maybeSelected = (int) (clickEffective_x / (mBarWidth + mInterval)) + 1;
                //判断y
                Jchart jchart = mJcharts.get(maybeSelected);
                if (tup.y > jchart.getMidPointF().y - mAllowError) {
                    return maybeSelected;
                } else {
                    return -1;
                }
            } else {
                //判断 y
                Jchart jchart = mJcharts.get(0);
                if (tup.y > jchart.getMidPointF().y - mAllowError) {
                    return 0;
                } else {
                    return -1;
                }
            }
        } else {
            return -1;
        }
        //        for(int i = 0; i<mJcharts.size(); i++) {
        //            Jchart excel = mJcharts.get(i);
        //            PointF start = excel.getStart();
        //            if(start.x>tup.x) {
        //                return -1;
        //            }else if(start.x-mSliding<=tup.x) {
        //                if(start.x-mSliding+excel.getWidth()>tup.x && ( start.y>tup.y && start.y-excel.getHeight()<tup.y )) {
        //                    return i;
        //                }
        //            }
        //        }
        //        return -1;
    }

    /**
     * 画选中的 顶部文字和背景
     *
     * @param canvas
     * @param excel
     */
    protected void drawSelectedText(Canvas canvas, Jchart excel) {
        PointF midPointF = excel.getMidPointF();
        //        String msg = excel.getUpper() + excel.getUnit();
        String msg = excel.getShowMsg();
        Rect mBounds = new Rect();
        mSelectedTextPaint.getTextBounds(msg, 0, msg.length(), mBounds);
        Path textBg = new Path();

        float bgWidth = dip2px(8);

        textBg.moveTo(midPointF.x, midPointF.y - mSelectedTextMarging);
        textBg.lineTo(midPointF.x - bgWidth / 2, midPointF.y - mSelectedTextMarging - mBgTriangleHeight - 1.5f);
        textBg.lineTo(midPointF.x + bgWidth / 2, midPointF.y - mSelectedTextMarging - mBgTriangleHeight - 1.5f);
        textBg.close();
        canvas.drawPath(textBg, mSelectedTextBgPaint);

        RectF rectF = new RectF(midPointF.x - mBounds.width() / 2f - bgWidth,
                midPointF.y - mSelectedTextMarging - mBgTriangleHeight - mBounds.height() - mBgTriangleHeight * 2f,
                midPointF.x + mBounds.width() / 2f + bgWidth, midPointF.y - mSelectedTextMarging - mBgTriangleHeight);
        float dffw = 0;
        if (!mScrollAble) {
            //防止 画出到屏幕外
            dffw = rectF.right - mWidth - getPaddingRight() - getPaddingLeft();
        }
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
        canvas.drawRoundRect(rectF, 3, 3, mSelectedTextBgPaint);
        canvas.drawText(msg, msgX, midPointF.y - mSelectedTextMarging - mBgTriangleHeight * 2, mSelectedTextPaint);
    }

    /**
     * 画柱状图
     *
     * @param canvas
     */
    protected void drawSugExcel_BAR(Canvas canvas) {
    }

    /**
     * 画纵坐标信息
     *
     * @param canvas
     */
    protected void drawYabscissaMsg(Canvas canvas) {
        mAbscissaPaint.setTextAlign(Paint.Align.LEFT);
        float diffCoordinate = mChartArea.height() / (mYaxis_msg.size() - 1);
        for (int i = 0; i < mYaxis_msg.size(); i++) {
            float levelCoordinate = mChartArea.bottom - diffCoordinate * i;
            canvas.drawText(mYaxis_msg.get(i), getPaddingLeft(), levelCoordinate,
                    mAbscissaPaint);
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
    protected void drawAbscissaMsg(Canvas canvas, Jchart excel) {
        mAbscissaPaint.setTextAlign(Paint.Align.CENTER);
        float mWidth = this.mWidth + getPaddingLeft() + getPaddingRight();
        if (null != excel) {
            PointF midPointF = excel.getMidPointF();
            if (!TextUtils.isEmpty(excel.getXmsg())) {
                String xmsg = excel.getXmsg();
                float w = mAbscissaPaint.measureText(xmsg, 0, xmsg.length());
                if (!mScrollAble) {
                    if (midPointF.x - w / 2 < 0) {
                        //最左边
                        canvas.drawText(excel.getXmsg(), w / 2, mChartArea.bottom + dip2px(3) + mAbscissaMsgSize,
                                mAbscissaPaint);
                    } else if (midPointF.x + w / 2 > mWidth) {
                        //最右边
                        canvas.drawText(excel.getXmsg(), mWidth - w / 2, mChartArea.bottom + dip2px(3) + mAbscissaMsgSize,
                                mAbscissaPaint);
                    } else {
                        canvas.drawText(excel.getXmsg(), midPointF.x, mChartArea.bottom + dip2px(3) + mAbscissaMsgSize,
                                mAbscissaPaint);
                    }
                } else {
                    canvas.drawText(excel.getXmsg(), midPointF.x, mChartArea.bottom + dip2px(3) + mAbscissaMsgSize,
                            mAbscissaPaint);
                }
            }
        }
    }

    /**
     * 画 折线
     */
    protected void drawSugExcel_LINE(Canvas canvas) {
    }

    /**
     * 画 坐标轴  横轴
     */
    protected void drawCoordinateAxes(Canvas canvas) {
        if (mScrollAble) {
            canvas.drawLine(mChartArea.left, mChartArea.bottom, mChartRithtest_x + mBarWidth / 2, mChartArea.bottom, mCoordinatePaint);
        } else {
            canvas.drawLine(mChartArea.left, mChartArea.bottom, mChartArea.right, mChartArea.bottom, mCoordinatePaint);
        }
//        canvas.drawRect(mChartArea, mCoordinatePaint);
    }

    /**
     * 为画笔 设置 渲染器
     *
     * @param paint
     */
    protected void paintSetShader(Paint paint, int[] shaders, float x0, float y0, float x1, float y1) {
        if (shaders != null && shaders.length > 1) {
            float[] position = new float[shaders.length];
            float v = 1f / shaders.length;
            float temp = 0;
            if (shaders.length > 2) {
                for (int i = 0; i < shaders.length; i++) {
                    position[i] = temp;
                    temp += v;
                }
            } else {
                position[0] = 0;
                position[1] = 1;
            }
            paint.setShader(new LinearGradient(x0, y0, x1, y1, shaders, position, Shader.TileMode.CLAMP));
        }
    }

    protected DashPathEffect pathDashEffect(float[] intervals) {                     //线，段，线，段
        DashPathEffect dashEffect = new DashPathEffect(intervals, mPhase);
        return dashEffect;
    }


    public void aniShowChar(float start, final float end) {
        aniShowChar(start, end, mInterpolator, ANIDURATION);
    }

    public void aniShowChar(float start, final float end, TimeInterpolator interpolator) {
        aniShowChar(start, end, interpolator, 800);
    }

    public void aniShowChar(float start, final float end, TimeInterpolator interpolator, long duration) {
        if (mValueAnimator == null) {
            mValueAnimator = ValueAnimator.ofFloat(start, end);
        } else {
            mValueAnimator.cancel();
            mValueAnimator.setFloatValues(start, end);
        }
        //        if(mLineStyle == LineStyle.LINE_CURVE) {
        //            mLinePath.rewind();//倒序
        //        }
        //        mValueAnimator.isRunning()
        mValueAnimator.setDuration(duration);
        mValueAnimator.setInterpolator(interpolator);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                onAnimationUpdating(animation);
            }
        });
        mValueAnimator.start();
    }

    protected void onAnimationUpdating(ValueAnimator animation) {
    }

    /**
     * 传入 数据
     */
    public void cmdFill(@NonNull Jchart... jcharts) {
        cmdFill(new ArrayList<Jchart>(Arrays.asList(jcharts)));
    }

    /**
     * 传入 数据
     */
    public void cmdFill(@NonNull List<Jchart> jchartList) {
        mSelected = -1;
        mJcharts.clear();
        if (jchartList != null && jchartList.size() > 0) {
            mJcharts.addAll(jchartList);
            mAllLastPoints = new ArrayList<>(jchartList.size());
            for (int i = 0; i < mJcharts.size(); i++) {
                Jchart jchart = mJcharts.get(i);
                jchart.setIndex(i);
                mAllLastPoints.add(new PointF(jchart.getMidX(), -1));
            }
            if (!mScrollAble && mForceFixNums && mJcharts.size() > mVisibleNums) {
                //如果不可滚动的话 同时要显示固定个数 那么为防止显示不全 将可见个数设置为柱子数量
                mVisibleNums = mJcharts.size();
            }
//            findTheBestChart();
            if (mWidth > 0) {
                //已经显示在界面上了 重新设置数据
                refreshChartSetData();
            }

        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "数据异常 ");
        }
    }

    protected void findTheBestChart() {
        mFirstJchart = mJcharts.get(0);
        mLastJchart = mJcharts.get(mJcharts.size() - 1);
        mHeightestChart = Collections.max(mJcharts, new Comparator<Jchart>() {
            @Override
            public int compare(Jchart lhs, Jchart rhs) {
                return (int) (lhs.getUpper() - rhs.getUpper());
            }
        });
        mMinestChart = Collections.min(mJcharts, new Comparator<Jchart>() {
            @Override
            public int compare(Jchart lhs, Jchart rhs) {
                return (int) (lhs.getUpper() - rhs.getUpper());
            }
        });
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "最大值：" + mHeightestChart.getUpper());
        }
        if (mYaxis_msg == null || mYaxis_msg.size() == 0) {
            //默认 y轴显示两段三个刻度
            setYaxisValues(getCeil10(mHeightestChart.getUpper()), 3);
        } else {
            if (mYaxis_Max < mHeightestChart.getUpper() || mYaxis_min > mMinestChart.getUpper()) {
                mYaxis_Max = mYaxis_Max < mHeightestChart.getUpper() ? getCeil10(mHeightestChart.getUpper()) : mYaxis_Max;
                mYaxis_min = mYaxis_min > mMinestChart.getUpper() ? getCast10(mMinestChart.getUpper()) : mYaxis_min;
                setYaxisValues((int) mYaxis_min, (int) mYaxis_Max, mYaxis_msg.size());
            }
        }
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        mInterpolator = interpolator;
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

    public int getGraphStyle() {
        return mGraphStyle;
    }

    public void setNeedY_abscissMasg(boolean needY_abscissMasg) {
        mNeedY_abscissMasg = needY_abscissMasg;
    }

    /**
     * 设置y轴 刻度 信息
     *
     * @param min      y轴 显示的最小值
     * @param max      y轴显示的最大值
     * @param showYnum y轴显示 刻度数量 建议为奇数
     */
    public void setYaxisValues(int min, int max, int showYnum) {

        mYaxis_msg = new ArrayList<>(showYnum);
        float diffLevel = (max - min) / ((float) showYnum - 1);
        for (int i = 0; i < showYnum; i++) {
            mYaxis_msg.add(new DecimalFormat("#").format(min + diffLevel * i));
        }
        mYaxis_Max = max;
        mYaxismax = new DecimalFormat("##.#").format(mYaxis_Max);
        mYaxis_min = min;
    }

    /**
     * 设置y轴 刻度 信息
     *
     * @param showMsg y轴显示 内容
     */
    public void setYaxisValues(@NonNull List<String> showMsg) {
        mYaxis_msg = new ArrayList<>(showMsg.size());
        mYaxismax = showMsg.get(0);
        for (int i = 0; i < showMsg.size(); i++) {
            if (mYaxismax.length() < showMsg.get(i).length()) {
                mYaxismax = showMsg.get(i);
            }
            mYaxis_msg.add(showMsg.get(i));
        }
    }

    /**
     * 设置y轴 刻度 信息
     *
     * @param showMsg y轴显示 内容
     */
    public void setYaxisValues(@NonNull String... showMsg) {
        setYaxisValues(Arrays.asList(showMsg));

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
     * 坐标 信息 文字大小
     *
     * @param abscissaMsgSize
     */
    public void setAbscissaMsgSize(int abscissaMsgSize) {
        mAbscissaMsgSize = abscissaMsgSize;
    }

    /**
     * 设置 图表类型  柱状 折线  折线+柱状
     */
    public void setGraphStyle(int graphStyle) {
        mGraphStyle = graphStyle;
    }

    /**
     * 设置 滑动距离
     *
     * @param sliding
     */
    public void setSliding(float sliding) {
        mSliding = sliding;
    }

    /**
     * 图表 距离 横轴的距离
     *
     * @param above
     */
    public void setAbove(int above) {
        mAbove = above;
        refreshExcels();
    }

    public boolean isScrollAble() {
        return mScrollAble;
    }

    public void setScrollAble(boolean scrollAble) {
        mScrollAble = scrollAble;
    }

    public float getInterval() {
        return mInterval;
    }

    public void setInterval(float interval) {
        mInterval = interval;
    }

    public int getSelected() {
        return mSelected;
    }

    /**
     * 设置 选中的
     *
     * @param selected
     */
    public void setSelected(int selected) {
        mSelected = selected;
    }

    public int getVisibleNums() {
        return mVisibleNums;
    }

    public void setSelectedTextSize(float textSize) {
        mSelectedTextPaint.setTextSize(textSize);
    }

    /**
     * 设置 可见的柱子个数 点个数
     * 可滚动的情况下 默认可见5个
     *
     * @param visibleNums
     */
    public void setVisibleNums(int visibleNums) {
        mVisibleNums = visibleNums;
        //防止 不可滚动时visibleNums设置太小
        if (!mScrollAble && mForceFixNums && mJcharts.size() > mVisibleNums) {
            //如果不可滚动的话 同时要显示固定个数 那么为防止显示不全 将可见个数设置为柱子数量
            mVisibleNums = mJcharts.size();
        }
    }

    public void setForceFixNums(boolean forceFixNums) {
        mForceFixNums = forceFixNums;
    }

    public void setSelectedMode(int selectedMode) {
        mSelectedMode = selectedMode;
    }

    public void aniChangeData(List<Jchart> jchartList) {
    }

    public Paint getPaintAbsicssa() {
        return mAbscissaPaint;
    }

    public Paint getPaintCoordinate() {
        return mCoordinatePaint;
    }

    public Paint getPaintAbscisDash() {
        return mAbscisDashPaint;
    }

    public Paint getSelectedTextBg() {
        return mSelectedTextBgPaint;
    }

    public Paint getSelectedText() {
        return mSelectedTextPaint;
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

    /**
     * @param num
     * @return 最接近num的数 这个数同时是5的倍数
     */
    public int getRound5(float num) {
        return ((int) (num + 2.5)) / 5 * 5;
    }

    /**
     * @param num
     * @return 根据num向上取数 这个数同时是5的倍数
     */
    public int getCeil5(float num) {
        return ((int) (num + 4.9999999)) / 5 * 5;
    }

    public int getCeil10(float num) {
        return ((int) (num + 9.9999999)) / 10 * 10;
    }

    public int getRound10(float num) {
        return ((int) (num + 5)) / 10 * 10;
    }

    public int getCast10(float num) {
        return ((int) (num)) / 10 * 10;
    }
}
