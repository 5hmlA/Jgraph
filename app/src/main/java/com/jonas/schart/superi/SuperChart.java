package com.jonas.schart.superi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;

import com.jonas.schart.chartbean.JExcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author jwx338756
 * @Date: 2016
 * @Description: 折线+虚线
 * @Others: {https://github.com/mychoices}
 */
public class SuperChart extends View {

    /**
     * 选中的 柱状图
     */
    protected int mSelected = -1;
    protected int mHeight;
    protected int mWidth;
    protected Paint mCoordinatePaint;
    /**
     * 间隔
     */
    protected float mInterval = 0;

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
     */
    protected int mVisibleNums = -1;

    /**
     * 在不可滚动时 最多显示可见个数
     * mVisibleNums>=mExecels.size 否则部分不可见
     */
    protected boolean mForceFixNums;

    /**
     * 选中模式 为-1 表示不处理点击选中状态
     */
    protected int mSelectedMode = -1;

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

    public interface LineShowStyle {
        /**
         * 线条从无到有 慢慢出现
         */
        int LINESHOW_DRAWING = 1;
        /**
         * 线条 一段一段显示
         */
        int LINESHOW_SECTION = 2;
        /**
         * 线条 一从直线慢慢变成折线/曲线
         */
        int LINESHOW_FROMLINE = 3;
    }

    /**
     * 线条展示的动画风格
     */
    protected int mLineShowStyle = -1;

    public interface ChartStyle {
        /**
         * 心率柱状图
         */
        int BAR = 1;
        int LINE = 2;
        int BAR_LINE = 3;
        int PIE = 4;
    }

    protected Context mContext;
    /**
     * 系统认为发生滑动的最小距离
     */
    protected int mTouchSlop;

    /**
     * 图表 数据集合
     */
    protected List<JExcel> mExcels = new ArrayList<>();

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
    protected int mChartStyle = ChartStyle.LINE;

    /**
     * 滑动 距离
     */
    protected float mSliding = 0;

    public SuperChart(Context context) {
        super(context);
        init(context);
    }


    public SuperChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public SuperChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    protected void init(Context context) {
        mContext = context;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mCoordinatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCoordinatePaint.setStyle(Paint.Style.STROKE);

        mCoordinatePaint.setColor(Color.parseColor("#AFAFB0"));
        mCoordinatePaint.setStrokeWidth(2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h - getPaddingBottom() - getPaddingTop();
        mWidth = w - getPaddingLeft() - getPaddingRight();
        mCenterPoint = new PointF(w / 2f, h / 2f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null != mExcels && mExcels.size() > 0) {
            if (mChartStyle == ChartStyle.BAR) {
                drawSugExcel_BAR(canvas);
            } else if (mChartStyle == ChartStyle.LINE) {
                drawSugExcel_LINE(canvas);
            } else if (mChartStyle == ChartStyle.PIE) {
                drawSugExcel_PIE(canvas);
            } else {
                drawSugExcel_BAR(canvas);
                drawSugExcel_LINE(canvas);
            }

        }
        drawCoordinateAxes(canvas);
    }

    protected void drawSugExcel_PIE(Canvas canvas) {

    }

    /**
     * 画柱状图
     *
     * @param canvas
     */
    protected void drawSugExcel_BAR(Canvas canvas) {
    }

    /**
     * 画 横轴 横坐标 信息
     *
     * @param canvas
     * @param msg
     */
    protected void drawAbscissaMsg(Canvas canvas, String msg) {
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

        if (mExcels != null && mExcels.size() > 0) {
            canvas.drawLine(0, mHeight, mExcels.get(mExcels.size() - 1).getMidPointF().x, mHeight, mCoordinatePaint);
        } else {
            canvas.drawLine(0, mHeight, mWidth, mHeight, mCoordinatePaint);
        }
    }


    /**
     * 传入 数据
     */
    public void cmdFill(@NonNull JExcel... jExcels) {
        cmdFill(new ArrayList<JExcel>(Arrays.asList(jExcels)));
    }


    /**
     * 传入 数据
     */
    public void cmdFill(@NonNull List<JExcel> jExcelList) {
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
     * 设置 滑动距离
     *
     * @param sliding
     */
    public void setSliding(float sliding) {
        mSliding = sliding;
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

    /**
     * 设置 可见的柱子个数 点个数
     * 可滚动的情况下 默认可见5个
     *
     * @param visibleNums
     */
    public void setVisibleNums(int visibleNums) {
        mVisibleNums = visibleNums;
        //防止 不可滚动时visibleNums设置太小
        if (!mScrollAble && mForceFixNums && mExcels.size() > mVisibleNums) {
            //如果不可滚动的话 同时要显示固定个数 那么为防止显示不全 将可见个数设置为柱子数量
            mVisibleNums = mExcels.size();
        }
    }

    public void setForceFixNums(boolean forceFixNums) {
        mForceFixNums = forceFixNums;
    }

    public void setSelectedMode(int selectedMode){
        mSelectedMode = selectedMode;
    }

    public void setLineShowStyle(int lineShowStyle){
        mLineShowStyle = lineShowStyle;
    }

    public void aniChangeData(List<JExcel> jExcelList){};
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
