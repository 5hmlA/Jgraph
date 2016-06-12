package com.jonas.schart.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.jonas.schart.chartbean.SugExcel;
import com.jonas.schart.superi.SuperChart;

import java.util.List;

/**
 * @author jiangzuyun.
 * @date 2016/6/8
 * @des [一句话描述]
 * @since [产品/模版版本]
 */

public class PieChart extends SuperChart {

    private float dataFloatTotal;
    private Paint mPiePaint;
    private float pieWidth = 20;
    /**
     * 画环形 用的正方形的宽度高度
     */
    private float mSquare;
    /**
     * 画环形用的 矩阵
     */
    private RectF mAecRect;

    /**
     * 环形 距离边距的距离
     */
    private float mOutPading = 1;
    private float mStartAnger;
    private Paint mIntervalPaint;
    private int mIntervalColor;
    /**
     * 环形所在扇形的半径
     */
    private float mArcRadio;
    private float mIntervalWidth;

    public PieChart(Context context) {
        super(context);
    }

    public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PieChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mSquare = mWidth > mHeight ? mHeight : mWidth;
        refreshPieSet();
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mChartStyle = ChartStyle.PIE;
        mPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPiePaint.setStyle(Paint.Style.STROKE);
        mIntervalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        initData();
    }

    /**
     * 初始化 默认数据
     */
    protected void initData() {
        mInterval = 10;
        mPiePaint.setStrokeWidth(pieWidth);
        mAecRect = new RectF(0, 0, mWidth, mHeight);
        mIntervalPaint.setStrokeWidth(dip2px(1));
        Drawable background = getBackground();
        if (background instanceof ColorDrawable) {
            mIntervalColor = ((ColorDrawable) background).getColor();
        } else {
            mIntervalColor = Color.WHITE;
        }
    }

    @Override
    protected void drawCoordinateAxes(Canvas canvas) {
    }

    @Override
    protected void drawSugExcel_PIE(Canvas canvas) {

        mStartAnger = 0;
        for (SugExcel excel : mExcels) {
            mPiePaint.setColor(excel.getColor());
            canvas.drawArc(mAecRect, mStartAnger, excel.getHeight(), false, mPiePaint);
            mStartAnger += excel.getHeight();
        }

        mIntervalPaint.setStrokeWidth(mIntervalWidth);
        mIntervalPaint.setColor(mIntervalColor);
        canvas.save();
        for (SugExcel excel : mExcels) {
            canvas.drawLine(mCenterPoint.x + mArcRadio - pieWidth / 2 - 1f, mCenterPoint.y, mWidth, mHeight / 2, mIntervalPaint);
            canvas.rotate(excel.getHeight(), mCenterPoint.x, mCenterPoint.y);
        }
        canvas.restore();
    }

    @Override
    public void cmdFill(List<SugExcel> sugExcelList) {
        mExcels.clear();
        //sugexcel的lower为0 upper就是数据 height可以用来填充角度信息
        for (SugExcel sugExcel : sugExcelList) {
            dataFloatTotal += sugExcel.getUpper();
        }
        mExcels.addAll(sugExcelList);
        // 平分的角度
        for (SugExcel excel : mExcels) {
            excel.setHeight(excel.getUpper() / dataFloatTotal * 360);
        }
        refreshPieSet();

    }

    /**
     * 刷新 环形依赖的矩阵，间距宽度的约束
     */
    private void refreshPieSet() {
        if (mHeight > 0) {
            //环形的宽度 不可以大于矩阵的一半
            pieWidth = pieWidth > (mSquare / 2 - mOutPading)  ? (mSquare / 2 - mOutPading) : pieWidth;
            mPiePaint.setStrokeWidth(pieWidth);
            mIntervalWidth = mIntervalWidth < pieWidth / 10f ? pieWidth / 10f : mIntervalWidth;
            mArcRadio = mSquare / 2 - mOutPading - pieWidth / 2;
            mAecRect.set(mCenterPoint.x - mArcRadio, mCenterPoint.y - mArcRadio, mCenterPoint.x + mArcRadio, mCenterPoint.y + mArcRadio);
            postInvalidate();
        }
    }

    public float getPieWidth() {
        return pieWidth;
    }

    public void setPieWidth(float pieWidth) {
        this.pieWidth = pieWidth;
        refreshPieSet();
    }

    @Override
    public void setInterval(float interval) {
        super.setInterval(interval);
        mIntervalWidth = interval;
        refreshPieSet();
    }

    @Deprecated
    @Override
    public void setChartStyle(int chartStyle) {
        mChartStyle = ChartStyle.PIE;
    }
}
