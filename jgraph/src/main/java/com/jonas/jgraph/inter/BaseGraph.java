package com.jonas.jgraph.inter;

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
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.BounceInterpolator;
import android.widget.Scroller;

import com.jonas.jgraph.BuildConfig;
import com.jonas.jgraph.R;
import com.jonas.jgraph.models.Jchart;
import com.jonas.jgraph.utils.CalloutHelper;
import com.jonas.jgraph.utils.MathHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author yun.
 * @date 2016/7/11
 * @des [图表控件的基类  finish 图表区域，横纵轴数据]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public abstract class BaseGraph extends View implements GestureDetector.OnGestureListener {
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
     * 控件 中心 点
     */
    protected PointF mCenterPoint;

    /**
     * 可见的 个数(柱状图(最多可见的柱子数量)/折线图(最多可见的点))
     * <p>可滚动的时候 mVisibleNums可以小于mchart的数量</p>
     * <p>不可滚动的时候 mVisibleNums必须大于等于mchart的数量</p>
     */
    protected int mVisibleNums = 0;

    /**
     * 在不可滚动时 最多显示可见个数
     * <p color="red">mVisibleNums>=mExecels.size 否则部分不可见
     */
    //    protected boolean mForceFixNums;

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
    private int mXNums;
    private int mXinterval;
    private GestureDetector mGestureDetector;
    private Scroller mScroller;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private float upPlace;
    private OnGraphItemListener mListener;

    public final static int SELECETD_NULL = -1;
    /**
     * 选中的 颜色变  显示所有柱子 文字
     */
    public final static int SELECTED_ACTIVATED = 0;
    /**
     * 选中的 显示 柱子 文字 其他不显示
     */
    public final static int SELECETD_MSG_SHOW_TOP = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SELECETD_NULL, SELECTED_ACTIVATED, SELECETD_MSG_SHOW_TOP})
    public @interface SelectedMode {}

    public final static int aniChange = 1;
    public final static int aniShow = 2;
    public final static int aniFinish = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({aniChange, aniShow, aniFinish})
    protected @interface State {}

    public final static int BAR = 0;
    public final static int LINE = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({BAR, LINE})
    public @interface GraphStyle {}

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
    protected int mGraphStyle = LINE;

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
    protected ArrayList<PointF> mAllLastPoints = new ArrayList<>();
    protected ValueAnimator mValueAnimator = new ValueAnimator();

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

    public BaseGraph(Context context){
        this(context, null);
    }

    public BaseGraph(Context context, AttributeSet attrs){
        this(context, attrs, 0);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AndroidJgraph);
        mGraphStyle = a.getInt(R.styleable.AndroidJgraph_graphstyle, LINE);
        mScrollAble = a.getBoolean(R.styleable.AndroidJgraph_scrollable, false);
        mNeedY_abscissMasg = a.getBoolean(R.styleable.AndroidJgraph_showymsg, true);
        mNormalColor = a.getColor(R.styleable.AndroidJgraph_normolcolor, Color.parseColor("#676567"));
        mActivationColor = a.getColor(R.styleable.AndroidJgraph_activationcolor, Color.RED);
        mVisibleNums = a.getInt(R.styleable.AndroidJgraph_visiblenums, 0);
        a.recycle();
    }

    public BaseGraph(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(Context context){
        mContext = context;
        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        mCoordinatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCoordinatePaint.setStyle(Paint.Style.STROKE);
        mCoordinatePaint.setColor(Color.parseColor("#AFAFB0"));
        mCoordinatePaint.setStrokeWidth(1f);

        mAbscissaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAbscissaPaint.setTextAlign(Paint.Align.CENTER);
        mAbscissaMsgSize = MathHelper.dip2px(mContext, 12);
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
        mBgTriangleHeight = MathHelper.dip2px(mContext, 6);

        mSelectedTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //顶部选中文字 颜色
        mSelectedTextPaint.setTextAlign(Paint.Align.CENTER);
        mSelectedTextPaint.setColor(Color.WHITE);
        mSelectedTextPaint.setTextSize(MathHelper.dip2px(mContext, 12));

        mBarWidth = MathHelper.dip2px(mContext, 10);//默认的柱子宽度
        mInterval = MathHelper.dip2px(mContext, 4);//默认的间隔大小
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        mGestureDetector = new GestureDetector(mContext, this);
        mScroller = new Scroller(mContext);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(mContext);
        mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h-getPaddingBottom()-getPaddingTop();
        mWidth = w-getPaddingLeft()-getPaddingRight();
        mCenterPoint = new PointF(w/2f, h/2f);
        if(mGraphStyle == BAR || mGraphStyle == LINE) {
            refreshChartArea();
        }
    }

    /**
     * 刷新 画图表的区域
     */
    protected void refreshChartArea(){
        float yMsgLength = 0;
        float yMsgHeight = 0;
        Rect bounds = new Rect();
        mAbscissaPaint.getTextBounds(mYaxismax, 0, mYaxismax.length(), bounds);
        if(mNeedY_abscissMasg) {
            //如果 需要 纵轴坐标的时候
            yMsgLength = mAbscissaPaint.measureText(mYaxismax, 0, mYaxismax.length());
            yMsgLength = bounds.width()<yMsgLength ? bounds.width() : yMsgLength;
            yMsgLength += 5;
        }
        if(mSelectedMode == SELECETD_MSG_SHOW_TOP) {
            //选中文字的背景的高度
//            yMsgHeight = mSelectedTextPaint.getTextSize()+3f*mBgTriangleHeight;
            yMsgHeight = CalloutHelper.getCalloutHeight();
        }else {
            yMsgHeight = bounds.height();
        }
        mChartArea = new RectF(yMsgLength+getPaddingLeft(), getPaddingTop()+yMsgHeight, mWidth+getPaddingLeft(),
                getPaddingTop()+mHeight-2*mAbscissaMsgSize);
        refreshChartSetData();
    }

    /**
     * 获取屏幕 宽高 后 更新 图表区域 矩阵数据 柱子宽 间隔宽度
     */
    protected void refreshChartSetData(){
        if(mGraphStyle == BAR) {
            //柱状图默认 间隔固定
            mInterval = mInterval>=MathHelper.dip2px(mContext, 6) ? MathHelper.dip2px(mContext, 6) : mInterval;
            //            mFixBarWidth = false;
        }else {
            //折线图 默认柱子宽度固定 小点
            mBarWidth = 3;
        }

        if(mScrollAble) {
            mVisibleNums = mVisibleNums<=0 ? 5 : mVisibleNums;//可滚动的状态下 默认可见个数为5
        }else {
            //不可滚动的状态下 可见的数量必须 大于等于 数据数量
            mVisibleNums = mVisibleNums>=mJcharts.size() ? mVisibleNums : mJcharts.size();
        }

        //画 图表区域的宽度
        mCharAreaWidth = mChartArea.right-mChartArea.left;
        //不可滚动 则必须全部显示在界面上  无视mVisibleNums
        if(mGraphStyle == BAR) {
            //间隔 minterval默认 计算柱子宽度
            mBarWidth = ( mCharAreaWidth-mInterval*( mVisibleNums-1 ) )/mVisibleNums;
        }else {
            mInterval = ( mCharAreaWidth-mBarWidth*mVisibleNums )/( mVisibleNums-1 );
        }
        refreshExcels();
    }

    /**
     * 主要 刷新高度
     */
    protected void refreshExcels(){
        if(mJcharts == null) {
            if(BuildConfig.DEBUG) {
                Log.e(TAG, "数据为空 ");
            }
            return;
        }
        findTheBestChart();
        mHeightRatio = ( mChartArea.bottom-mChartArea.top )/( mYaxis_Max-mYaxis_min );
        for(int i = 0; i<mJcharts.size(); i++) {
            Jchart jchart = mJcharts.get(i);
            jchart.setLowStart(mYaxis_min);
            jchart.setHeightRatio(mHeightRatio);
            jchart.setWidth(mBarWidth);
            PointF start = jchart.getStart();
            jchart.setIndex(i);
            start.x = mChartArea.left+mBarWidth*i+mInterval*i;
            start.y = mChartArea.bottom-mAbove;
            refreshOthersWithEveryChart(i, jchart);
        }
        mChartRithtest_x = mJcharts.get(mJcharts.size()-1).getMidPointF().x;
        mChartLeftest_x = mJcharts.get(0).getMidPointF().x;
    }

    /**
     * 根据每一个chart刷新其他需要的数据
     *
     * @param i
     * @param jchart
     */
    protected void refreshOthersWithEveryChart(int i, Jchart jchart){

    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(null != mJcharts && mJcharts.size()>0) {
            if(mNeedY_abscissMasg && mYaxis_msg != null) {
                drawYabscissaMsg(canvas);
            }
            if(mGraphStyle == BAR) {
                drawSugExcel_BAR(canvas);
            }else if(mGraphStyle == LINE) {
                drawSugExcel_LINE(canvas);
            }else {
                drawSugExcel_BAR(canvas);
                drawSugExcel_LINE(canvas);
            }
            //选中模式启用的时候
            if(mSelectedMode == SELECETD_MSG_SHOW_TOP && !mValueAnimator.isRunning()) {
                if(mSelected>-1) {
                    drawSelectedText(canvas, mJcharts.get(mSelected));
                }else {
                    drawSelectedText(canvas, mJcharts.get(mHeightestChart.getIndex()));
                }
            }
            for(Jchart excel : mJcharts) {
                drawAbscissaMsg(canvas, excel);
            }
        }
        drawCoordinateAxes(canvas);
        mPhase = ++mPhase%50;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(mSelectedMode == -1 && !mScrollAble) {
            return false;
        }
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e){
        mScroller.forceFinished(true);
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e){
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "onShowPress ");
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e){
        PointF tup = new PointF(e.getX(), e.getY());
        mSelected = clickWhere(tup);
        if(mListener != null) {
            mListener.onItemClick(mSelected);
        }
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "selected "+mSelected);
        }
        invalidate();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
        return judgeSliding(-distanceX);
    }

    @Override
    public void onLongPress(MotionEvent e){
        PointF tup = new PointF(e.getX(), e.getY());
        mSelected = clickWhere(tup);
        if(mListener != null) {
            mListener.onItemLongClick(mSelected);
        }
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "onLongPress selected "+mSelected);
        }
        invalidate();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
        upPlace = e2.getX();
        mScroller
                .fling((int)e2.getX(), (int)e2.getY(), (int)velocityX/2, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        return true;
    }

    @Override
    public void computeScroll(){
        if(mScroller.computeScrollOffset()) {
            float vSliding = mScroller.getCurrX()-upPlace;
            judgeSliding(vSliding);
        }
    }

    protected boolean judgeSliding(float tempSlided){
        mSliding += tempSlided;
        if(mJcharts != null && mJcharts.size()>0) {
            if(mSliding>=0 || mSliding<=-( mChartRithtest_x-mCharAreaWidth )) {
                //跨越两边界了
                mSliding = mSliding>=0 ? 0 : mSliding<=-( mChartRithtest_x-mCharAreaWidth ) ? -( mChartRithtest_x-mCharAreaWidth ) : mSliding;
                invalidate();
                return false;
            }else {
                //正常滑动距离刷新界面
                invalidate();
                return true;
            }
        }else {
            mSliding = mSliding>=0 ? 0 : mSliding;
            invalidate();
            return false;
        }
    }

    /**
     * 判断 点中哪个柱状图
     */
    protected int clickWhere(PointF tup){
        if(mChartArea == null) {
            return -1;
        }
        float clickEffective_x = tup.x-mChartArea.left-mSliding;
        if(clickEffective_x>0) {
            clickEffective_x = tup.x-mChartArea.left-mBarWidth-mInterval/2-mSliding;
            if(clickEffective_x>0) {
                int maybeSelected = (int)( clickEffective_x/( mBarWidth+mInterval ) )+1;
                if(maybeSelected>=mJcharts.size()) {
                    return -1;
                }
                //判断y
                Jchart jchart = mJcharts.get(maybeSelected);
                if(tup.y>jchart.getMidPointF().y-mAllowError) {
                    return maybeSelected;
                }else {
                    return -1;
                }
            }else {
                //判断 y
                Jchart jchart = mJcharts.get(0);
                if(tup.y>jchart.getMidPointF().y-mAllowError) {
                    return 0;
                }else {
                    return -1;
                }
            }
        }else {
            return -1;
        }
    }

    /**
     * 画选中的 顶部文字和背景
     *
     * @param canvas
     * @param excel
     */
    protected void drawSelectedText(Canvas canvas, Jchart excel){
        PointF midPointF = excel.getMidPointF();
        //        String msg = excel.getUpper() + excel.getUnit();
        String msg = excel.getShowMsg();
        Rect mBounds = new Rect();
        mSelectedTextPaint.getTextBounds(msg, 0, msg.length(), mBounds);
        Path textBg = new Path();
        //        mSelectedTextPaint.getTextSize() > mBounds.height()
        float bgWidth = MathHelper.dip2px(mContext, 8);

        textBg.moveTo(midPointF.x, midPointF.y-mSelectedTextMarging);
        textBg.lineTo(midPointF.x-bgWidth/2, midPointF.y-mSelectedTextMarging-mBgTriangleHeight-1.5f);
        textBg.lineTo(midPointF.x+bgWidth/2, midPointF.y-mSelectedTextMarging-mBgTriangleHeight-1.5f);
        textBg.close();
        canvas.drawPath(textBg, mSelectedTextBgPaint);

        RectF rectF = new RectF(midPointF.x-mBounds.width()/2f-bgWidth,
                midPointF.y-mSelectedTextMarging-mBgTriangleHeight-mBounds.height()-mBgTriangleHeight*2f,
                midPointF.x+mBounds.width()/2f+bgWidth, midPointF.y-mSelectedTextMarging-mBgTriangleHeight);
        float dffw = 0;
        if(!mScrollAble) {
            //防止 画出到屏幕外
            dffw = rectF.right-mWidth-getPaddingRight()-getPaddingLeft();
        }
        float msgX = midPointF.x;
        float magin = 1;
        if(dffw>0) {
            rectF.right = rectF.right-dffw-magin;
            rectF.left = rectF.left-dffw-magin;
            msgX = midPointF.x-dffw-magin;
        }else if(rectF.left<0) {
            rectF.right = rectF.right-rectF.left+magin;
            msgX = midPointF.x-rectF.left+magin;
            rectF.left = magin;
        }
        canvas.drawRoundRect(rectF, 3, 3, mSelectedTextBgPaint);
        canvas.drawText(msg, msgX, midPointF.y-mSelectedTextMarging-mBgTriangleHeight*2, mSelectedTextPaint);
    }

    /**
     * 画柱状图
     *
     * @param canvas
     */
    protected void drawSugExcel_BAR(Canvas canvas){
    }

    /**
     * 画纵坐标信息
     *
     * @param canvas
     */
    protected void drawYabscissaMsg(Canvas canvas){
        mAbscissaPaint.setTextAlign(Paint.Align.LEFT);
        float diffCoordinate = mChartArea.height()/( mYaxis_msg.size()-1 );
        for(int i = 0; i<mYaxis_msg.size(); i++) {
            float levelCoordinate = mChartArea.bottom-diffCoordinate*i;
            canvas.drawText(mYaxis_msg.get(i), getPaddingLeft(), levelCoordinate, mAbscissaPaint);
            if(i>0) {
                Path dashPath = new Path();
                dashPath.moveTo(mChartArea.left, levelCoordinate);
                float ydash_x = 0;
                if(mJcharts != null && mJcharts.size()>0) {
                    ydash_x = mChartRithtest_x<mChartArea.right ? mChartArea.right : mChartRithtest_x;
                }else {
                    ydash_x = mChartArea.right;
                }
                dashPath.lineTo(ydash_x, levelCoordinate);
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
    protected void drawAbscissaMsg(Canvas canvas, Jchart excel){
        if(mXinterval != 0 && mXNums != 0) {
            drawAbscissaMsg(canvas);
        }else {
            mAbscissaPaint.setTextAlign(Paint.Align.CENTER);
            float mWidth = this.mWidth+getPaddingLeft()+getPaddingRight();
            if(null != excel) {
                PointF midPointF = excel.getMidPointF();
                if(!TextUtils.isEmpty(excel.getXmsg())) {
                    String xmsg = excel.getXmsg();
                    float w = mAbscissaPaint.measureText(xmsg, 0, xmsg.length());
                    if(!mScrollAble) {
                        if(midPointF.x-w/2<0) {
                            //最左边
                            canvas.drawText(excel.getXmsg(), w/2,
                                    mChartArea.bottom+MathHelper.dip2px(mContext, 3)+mAbscissaMsgSize, mAbscissaPaint);
                        }else if(midPointF.x+w/2>mWidth) {
                            //最右边
                            canvas.drawText(excel.getXmsg(), mWidth-w/2,
                                    mChartArea.bottom+MathHelper.dip2px(mContext, 3)+mAbscissaMsgSize, mAbscissaPaint);
                        }else {
                            canvas.drawText(excel.getXmsg(), midPointF.x,
                                    mChartArea.bottom+MathHelper.dip2px(mContext, 3)+mAbscissaMsgSize, mAbscissaPaint);
                        }
                    }else {
                        canvas.drawText(excel.getXmsg(), midPointF.x,
                                mChartArea.bottom+MathHelper.dip2px(mContext, 3)+mAbscissaMsgSize, mAbscissaPaint);
                    }
                }
            }
        }
    }

    private void drawAbscissaMsg(Canvas canvas){
        int mTotalTime = mXinterval*mXNums;
        String total = String.valueOf(mTotalTime);
        float xWi = mWidth-mChartArea.left-getPaddingRight()-mAbscissaPaint.measureText(total, 0, total.length());
        for(int i = 0; i<mXNums+1; i++) {
            String xmsg = String.valueOf(mXinterval*i);
            float w = mAbscissaPaint.measureText(xmsg, 0, xmsg.length());
            float textX = mChartArea.left+mXinterval*i/mTotalTime*xWi;
            if(textX+w/2>mWidth) {
                textX = mWidth-w;
            }
            canvas.drawText(xmsg, textX, mChartArea.bottom+MathHelper.sp2px(mContext, 3)+mAbscissaMsgSize,
                    mAbscissaPaint);
        }
    }

    /**
     * 画 折线
     */
    protected void drawSugExcel_LINE(Canvas canvas){
    }

    /**
     * 画 坐标轴  横轴
     */
    protected void drawCoordinateAxes(Canvas canvas){
        if(mScrollAble) {
            float coordinate_x = mChartRithtest_x+mBarWidth/2;
            coordinate_x = coordinate_x<mChartArea.right ? mChartArea.right : coordinate_x;
            canvas.drawLine(mChartArea.left, mChartArea.bottom, coordinate_x, mChartArea.bottom, mCoordinatePaint);
        }else {
            canvas.drawLine(mChartArea.left, mChartArea.bottom, mChartArea.right, mChartArea.bottom, mCoordinatePaint);
        }
    }

    /**
     * 为画笔 设置 渲染器
     *
     * @param paint
     */
    protected void paintSetShader(Paint paint, int[] shaders, float x0, float y0, float x1, float y1){
        if(shaders != null && shaders.length>1) {
            float[] position = new float[shaders.length];
            float v = 1f/shaders.length;
            float temp = 0;
            if(shaders.length>2) {
                for(int i = 0; i<shaders.length; i++) {
                    position[i] = temp;
                    temp += v;
                }
            }else {
                position[0] = 0;
                position[1] = 1;
            }
            paint.setShader(new LinearGradient(x0, y0, x1, y1, shaders, position, Shader.TileMode.CLAMP));
        }else if(paint.getShader() != null) {
            paint.setShader(null);
        }
    }


    protected DashPathEffect pathDashEffect(float[] intervals){                     //线，段，线，段
        DashPathEffect dashEffect = new DashPathEffect(intervals, mPhase);
        return dashEffect;
    }


    public void aniShowChar(float start, float end){
        aniShowChar(start, end, mInterpolator, ANIDURATION);
    }

    public void aniShowChar(float start, float end, TimeInterpolator interpolator){
        aniShowChar(start, end, interpolator, 1000);
    }

    public void aniShowChar(float start, float end, TimeInterpolator interpolator, long duration){
        aniShowChar(start, end, interpolator, duration, false);
    }

    public void aniShowChar(float start, float end, TimeInterpolator interpolator, long duration, boolean intvalue){
        mValueAnimator.cancel();
        if(intvalue) {
            mValueAnimator = ValueAnimator.ofInt(( (int)start ), ( (int)end ));
        }else {
            mValueAnimator = ValueAnimator.ofFloat(start, end);
        }
        mValueAnimator.setDuration(duration);
        mValueAnimator.setInterpolator(interpolator);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation){
                onAnimationUpdating(animation);
            }
        });
        mValueAnimator.start();
    }

    protected void onAnimationUpdating(ValueAnimator animation){
    }

    /**
     * 传入 数据
     */
    public void feedData(@NonNull Jchart... jcharts){
        feedData(new ArrayList<Jchart>(Arrays.asList(jcharts)));
    }

    /**
     * 传入 数据
     */
    public void feedData(@NonNull List<Jchart> jchartList){
        mSelected = -1;
        mJcharts.clear();
        if(jchartList.size()>0) {
            mJcharts.addAll(jchartList);
            mAllLastPoints = new ArrayList<>(jchartList.size());
            for(int i = 0; i<mJcharts.size(); i++) {
                Jchart jchart = mJcharts.get(i);
                jchart.setIndex(i);
                mAllLastPoints.add(new PointF(jchart.getMidX(), -1));
            }

            if(mWidth>0) {
                //已经显示在界面上了 重新设置数据
                refreshChartSetData();
            }

        }else {
            if(BuildConfig.DEBUG) {//in lib DEBUG always false
                Log.e(TAG, "数据异常 ");
            }
        }
    }

    protected void findTheBestChart(){
        mFirstJchart = mJcharts.get(0);
        mLastJchart = mJcharts.get(mJcharts.size()-1);
        mHeightestChart = Collections.max(mJcharts, new Comparator<Jchart>() {
            @Override
            public int compare(Jchart lhs, Jchart rhs){
                return (int)( lhs.getTopest()-rhs.getTopest() );
            }
        });
        mMinestChart = Collections.min(mJcharts, new Comparator<Jchart>() {
            @Override
            public int compare(Jchart lhs, Jchart rhs){
                return (int)( lhs.getTopest()-rhs.getTopest() );
            }
        });
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "最大值："+mHeightestChart.getTopest());
        }
        if(mYaxis_msg == null || mYaxis_msg.size() == 0) {
            mYaxis_Max = MathHelper.getCeil10(mHeightestChart.getTopest());
            //默认 y轴显示两段三个刻度
            refreshYaxisValues(3);
        }else {
            if(mYaxis_Max<mHeightestChart.getTopest() || mYaxis_min>mMinestChart.getTopest()) {
                //纵轴的 最大值 要比数据最大还大
                mYaxis_Max = mYaxis_Max<mHeightestChart.getTopest() ? MathHelper
                        .getCeil10(mHeightestChart.getTopest()) : mYaxis_Max;
                if(mYaxis_min>mMinestChart.getTopest()) {
                    mYaxis_min = MathHelper.getCast10(mMinestChart.getTopest());
                }
                //纵轴的 最小值 要比数据最小还小
                refreshYaxisValues(mYaxis_msg.size());
            }
        }
    }

    public void setOnGraphItemListener(OnGraphItemListener listener){
        mListener = listener;
    }

    public void setInterpolator(TimeInterpolator interpolator){
        mInterpolator = interpolator;
    }

    public int getNormalColor(){
        return mNormalColor;
    }

    /**
     * 默认颜色
     */
    public void setNormalColor(int normalColor){
        mNormalColor = normalColor;
    }

    public int getActivationColor(){
        return mActivationColor;
    }

    /**
     * 设置 柱状图 被选中的颜色
     */
    public void setActivationColor(int activationColor){
        mActivationColor = activationColor;
    }

    public int getGraphStyle(){
        return mGraphStyle;
    }

    public void setNeedY_abscissMasg(boolean needY_abscissMasg){
        mNeedY_abscissMasg = needY_abscissMasg;
    }

    /**
     * 设置y轴 刻度 信息
     * <p>所有数据设置为0 （showYnum为0）则会根据数据自动计算y轴需要显示的最大值最小值为0
     *
     * @param min
     *         y轴 显示的最小值
     * @param max
     *         y轴显示的最大值
     * @param showYnum
     *         y轴显示 刻度数量 建议为奇数
     */
    public void setYaxisValues(int min, int max, int showYnum){
        mYaxis_Max = max;
        mYaxismax = new DecimalFormat("##.#").format(mYaxis_Max);
        mYaxis_min = min;
        mYaxis_msg = mYaxis_msg == null ? new ArrayList<String>(showYnum) : mYaxis_msg;
        refreshExcels();
    }

    private void refreshYaxisValues(int showYnum){
        mYaxis_msg = new ArrayList<>(showYnum);
        mYaxismax = new DecimalFormat("##.#").format(mYaxis_Max);
        float diffLevel = ( mYaxis_Max-mYaxis_min )/( (float)showYnum-1 );
        for(int i = 0; i<showYnum; i++) {
            mYaxis_msg.add(new DecimalFormat("#").format(mYaxis_min+diffLevel*i));
        }
    }

    /**
     * 设置y轴 刻度 信息
     *
     * @param showMsg
     *         y轴显示 内容
     */
    public void setYaxisValues(@NonNull List<String> showMsg){
        mYaxis_msg = new ArrayList<>(showMsg.size());
        mYaxismax = showMsg.get(0);
        for(int i = 0; i<showMsg.size(); i++) {
            if(mYaxismax.length()<showMsg.get(i).length()) {
                mYaxismax = showMsg.get(i);
            }
            mYaxis_msg.add(showMsg.get(i));
        }
    }

    /**
     * @param xNums
     *         几段
     * @param xinterval
     *         每段多少
     */
    public void setXnums(int xNums, int xinterval){
        mXNums = xNums;
        mXinterval = xinterval;
    }

    /**
     * 设置y轴 刻度 信息
     *
     * @param showMsg
     *         y轴显示 内容
     */
    public void setYaxisValues(@NonNull String... showMsg){
        setYaxisValues(Arrays.asList(showMsg));

    }

    /**
     * 设置y轴 刻度 信息  0开始
     *
     * @param max
     *         y轴显示的最大值
     * @param showYnum
     *         y轴显示 刻度数量
     */
    public void setYaxisValues(int max, int showYnum){
        setYaxisValues(0, max, showYnum);
    }

    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        if(mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
        mChartArea = null;
        mAllLastPoints.clear();
        mAllPoints.clear();
        mJcharts.clear();
    }

    /**
     * 坐标 信息 文字大小
     *
     * @param abscissaMsgSize
     */
    public void setAbscissaMsgSize(int abscissaMsgSize){
        mAbscissaMsgSize = abscissaMsgSize;
    }

    /**
     * 设置 图表类型  柱状 折线  折线+柱状
     */
    public void setGraphStyle(@GraphStyle int graphStyle){
        mGraphStyle = graphStyle;
        if(mWidth>0) {
            refreshChartSetData();
        }
    }

    /**
     * 设置 滑动距离
     *
     * @param sliding
     */
    public void setSliding(float sliding){
        mSliding = sliding;
    }

    /**
     * 图表 距离 横轴的距离
     *
     * @param above
     */
    public void setAbove(int above){
        mAbove = above;
        refreshExcels();
    }

    public boolean isScrollAble(){
        return mScrollAble;
    }

    /**
     * 设置可滚动的时候 建议同时可见个数{@link #setVisibleNums(int)}
     *
     * @param scrollAble
     */
    public void setScrollAble(boolean scrollAble){
        mScrollAble = scrollAble;
        mSliding = 0;//可滚动转不可滚动时移动距离置为0
        if(mWidth>0) {
            refreshChartSetData();
        }
    }

    public float getInterval(){
        return mInterval;
    }

    public void setInterval(float interval){
        mInterval = interval;
    }

    public int getSelected(){
        return mSelected;
    }

    /**
     * 设置 选中的
     *
     * @param selected
     */
    public void setSelected(int selected){
        mSelected = selected;
    }

    public int getVisibleNums(){
        return mVisibleNums;
    }

    public void setSelectedTextSize(float textSize){
        mSelectedTextPaint.setTextSize(textSize);
    }

    /**
     * 设置 可见的柱子个数 点个数
     * 可滚动的情况下 默认可见5个
     *
     * @param visibleNums
     */
    public void setVisibleNums(int visibleNums){
        mVisibleNums = visibleNums;
        if(mWidth>0) {
            refreshChartSetData();
        }
    }

    public void setSelectedMode(@SelectedMode int selectedMode){
        mSelectedMode = selectedMode;
    }

    public void aniChangeData(List<Jchart> jchartList){
    }

    public Paint getPaintAbsicssa(){
        return mAbscissaPaint;
    }

    public Paint getPaintCoordinate(){
        return mCoordinatePaint;
    }

    public Paint getPaintAbscisDash(){
        return mAbscisDashPaint;
    }

    public Paint getSelectedTextBg(){
        return mSelectedTextBgPaint;
    }

    public Paint getSelectedTextPaint(){
        return mSelectedTextPaint;
    }

}
