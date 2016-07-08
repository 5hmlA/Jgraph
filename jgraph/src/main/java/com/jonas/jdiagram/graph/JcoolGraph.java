package com.jonas.jdiagram.graph;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

import com.jonas.jdiagram.BuildConfig;
import com.jonas.jdiagram.inter.SuperGraph;
import com.jonas.jdiagram.models.Jchart;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author jiangzuyun.
 * @date 2016/6/8
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 */
public class JcoolGraph extends SuperGraph {

    private static final long ANIDURATION = 1100;
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
    private ValueAnimator mValueAnimator = ValueAnimator.ofFloat(0, 1);
    ;
    private Paint mSelectedTextBgPaint;
    private Paint mSelectedTextPaint;
    /**
     * 选中文字背景 三角尖处 距离图表的距离
     */
    private float mSelectedTextMarging = 4;
    /**
     * 顶部选中文字 三角尖的高度
     */
    private float mBgTriangleHeight;
    private ArrayList<PointF> mAllPoints = new ArrayList<>();
    /**
     * 允许范围内的误差
     */
    private float mAllowError = 3;
    private float mBgTextSize = 4;
    private ArrayList<PointF> mAllLastPoints;
    //    private float FROM_LINE_Y = 0;
    private float mAbscisDashLineWidth = 0.2f;
    /**
     * 线条 和横轴之间的 渐变区域
     */
    private Paint mShaderAreaPaint;
    private int[] mShaderAreaColors;
    private Path mShadeAreaPath = new Path();
    private TimeInterpolator mInterpolator = new BounceInterpolator();
    private static final String TAG = JcoolGraph.class.getSimpleName();
    /**
     * 存放 纵轴信息
     */
    private ArrayList<String> mYaxis_msg;
    /**
     * y轴最长的文字
     */
    private String mYaxismax = "100";

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

    public static interface LineMode {
        /**
         * 连接每一个点
         */
        int LINE_EVERYPOINT = 1;
        /**
         * 跳过0  断开
         */
        int LINE_JUMP0 = 2;

        /**
         * 跳过0 用虚线链接
         */
        int LINE_DASH_0 = 2;
    }

    private int mLineMode = LineMode.LINE_EVERYPOINT;

    public static interface ShowFromMode {
        int SHOWFROMTOP = 0;
        int SHOWFROMBUTTOM = 1;
        int SHOWFROMMIDDLE = 2;
    }

    private int mShowFromMode = ShowFromMode.SHOWFROMBUTTOM;

    private boolean moved;
    private float mDownX;

    private float mBarWidth = -1;
    /**
     * 最高的点
     */
    private Jchart mHeightestChart;
    /**
     * 图表显示的区域 x轴起点  左边为刻度
     */
    private RectF mChartArea;
    private Paint mLinePaint;
    private float mLineWidth = 5;
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
    private float mYaxis_Max = 0;
    private float mYaxis_min;
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
    private float mAniRatio = 1;
    /**
     * 保存的原始路径数据
     */
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

    public JcoolGraph(Context context) {
        super(context);
    }

    public JcoolGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JcoolGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mChartStyle = ChartStyle.LINE;
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAbscissaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAbscisDashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectedTextBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectedTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShaderAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        initializeData();
    }

    private void initializeData() {
        mBarWidth = dip2px(16);//默认的柱子宽度
        mInterval = dip2px(4);//默认的间隔大小
        mAbscissaMsgSize = sp2px(12);//坐标轴信息
        mAbscissaMsgColor = Color.parseColor("#556A73");
        mLineColor = Color.RED;
        mLineWidth = dip2px(1.2f);

        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(mLineWidth);

        mAbscissaPaint.setTextAlign(Paint.Align.CENTER);
        mAbscissaPaint.setTextSize(mAbscissaMsgSize);
        mAbscissaPaint.setColor(mAbscissaMsgColor);

        mAbscisDashPaint.setStrokeWidth(mAbscisDashLineWidth);
        mAbscisDashPaint.setStyle(Paint.Style.STROKE);
        mAbscisDashPaint.setColor(mAbscissaMsgColor);

        //显示在顶部 选中的文字背景
        mSelectedTextBgPaint.setColor(Color.GRAY);
        //顶部选中文字 颜色
        mSelectedTextPaint.setTextAlign(Paint.Align.CENTER);
        mSelectedTextPaint.setColor(Color.WHITE);
        mBgTriangleHeight = dip2px(6);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mJcharts == null || mJcharts.size() <= 0) {
            return;
        }
        if (mNeedY_abscissMasg) {
            drawYabscissaMsg(canvas);
        }
        canvas.save();
        canvas.rotate(mAniRotateRatio);
        canvas.translate(mSliding, 0);//大于0 往右移动 小于0往左移动
        super.onDraw(canvas);
        canvas.restore();

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
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!moved) {
                        PointF tup = new PointF(event.getX(), event.getY());
                        mSelected = clickWhere(tup);
                        System.out.println(mSelected);
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
    private void drawSelectedText(Canvas canvas, Jchart excel) {
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
        canvas.drawRoundRect(rectF, 3, 3, mSelectedTextBgPaint);
        canvas.drawText(msg, msgX, midPointF.y - mSelectedTextMarging - mBgTriangleHeight * 2, mSelectedTextPaint);
    }

    @Override
    protected void drawSugExcel_LINE(Canvas canvas) {
        if (mLineMode == LineMode.LINE_EVERYPOINT) {
            //不跳过为0的点
            lineWithEvetyPoint(canvas);
        } else {
            //跳过为0的点（断开，虚线链接）
            lineSkip0Point(canvas);
        }

    }

    /**
     * 为画笔 设置 渲染器
     *
     * @param paint
     */
    protected void paintSetShader(Paint paint, int[] shaders) {
        paintSetShader(paint, shaders, mChartArea.left, mChartArea.top, mChartArea.left, mChartArea.bottom);
    }


    private void lineWithEvetyPoint(Canvas canvas) {
        mShadeAreaPath.reset();
        mAniLinePath.reset();
        mLinePaint.setColor(mNormalColor);
        paintSetShader(mLinePaint, mShaderColors);
        for (Jchart jchart : mJcharts) {
            jchart.draw(canvas, mLinePaint, true);
        }
        if (mState == State.aniChange && mAniRatio < 1) {
            drawLineAllpointFromLineMode(canvas);
            return;
        } else {
            mState = -1;
        }
        if (mLineShowStyle != LineShowStyle.LINESHOW_ASWAVE && (mLineShowStyle == -1 || !mValueAnimator.isRunning())) {
            canvas.drawPath(mLinePath, mLinePaint);
        } else if (mLineShowStyle == LineShowStyle.LINESHOW_DRAWING) {
            drawLineAllpointDrawing(canvas);
        } else if (mLineShowStyle == LineShowStyle.LINESHOW_SECTION) {
            drawLineAllpointSectionMode(canvas);
        } else if (mLineShowStyle == LineShowStyle.LINESHOW_FROMLINE) {
            drawLineAllpointFromLineMode(canvas);
        } else if (mLineShowStyle == LineShowStyle.LINESHOW_FROMCORNER) {
            drawLineAllpointFromCornerMode(canvas);
        } else if (mLineShowStyle == LineShowStyle.LINESHOW_ASWAVE) {
            drawLineAsWave(canvas);
        }
        //渐变区域
        if (mShaderAreaColors != null) {
            mShadeAreaPath.lineTo(mChartRithtest_x, mChartArea.bottom);
            mShadeAreaPath.lineTo(mChartArea.left, mChartArea.bottom);
            mShadeAreaPath.close();
            paintSetShader(mShaderAreaPaint, mShaderAreaColors);
            canvas.drawPath(mShadeAreaPath, mShaderAreaPaint);
        }
    }

    private void drawLineAllpointFromCornerMode(Canvas canvas) {
        if (mLineStyle == LineStyle.LINE_CURVE) {
            for (int i = 0; i < mAllPoints.size() - 1; i++) {
                PointF midPointF = mAllPoints.get(i);
                PointF endPointF = mAllPoints.get(i + 1);
                if (mAniLinePath.isEmpty()) {
                    mAniLinePath.moveTo(midPointF.x * mAniRatio, midPointF.y * mAniRatio);
                    mShadeAreaPath.moveTo(midPointF.x * mAniRatio, midPointF.y * mAniRatio);
                }
                float con_x = (midPointF.x + endPointF.x) / 2;
                mAniLinePath.cubicTo(con_x * mAniRatio, midPointF.y * mAniRatio, con_x * mAniRatio, endPointF.y * mAniRatio,
                        endPointF.x * mAniRatio, endPointF.y * mAniRatio);
                mShadeAreaPath.cubicTo(con_x * mAniRatio, midPointF.y * mAniRatio, con_x * mAniRatio, endPointF.y * mAniRatio,
                        endPointF.x * mAniRatio, endPointF.y * mAniRatio);

            }
        } else {
            for (PointF midPointF : mAllPoints) {
                if (mAniLinePath.isEmpty()) {
                    mAniLinePath.moveTo(midPointF.x * mAniRatio, midPointF.y * mAniRatio);
                    mShadeAreaPath.moveTo(midPointF.x * mAniRatio, midPointF.y * mAniRatio);
                } else {
                    mAniLinePath.lineTo(midPointF.x * mAniRatio, midPointF.y * mAniRatio);
                    mShadeAreaPath.lineTo(midPointF.x * mAniRatio, midPointF.y * mAniRatio);
                }
            }
        }
        canvas.drawPath(mAniLinePath, mLinePaint);
    }

    private void drawLineAllpointFromLineMode(Canvas canvas) {
        if (BuildConfig.DEBUG) {
            canvas.drawLine(0, mChartArea.bottom, mWidth, mChartArea.bottom, mLinePaint);
            canvas.drawLine(0, mChartArea.top, mWidth, mChartArea.top, mLinePaint);
        }
        if (mAllPoints.size() != mJcharts.size()) {
            throw new RuntimeException("mAllPoints.size() == mJcharts.size()");
        }
        if (mLineStyle == LineStyle.LINE_CURVE) {
            for (int i = 0; i < mAllPoints.size() - 1; i++) {
                PointF midPointF = mAllPoints.get(i);
                PointF endPointF = mAllPoints.get(i + 1);
                PointF midLastPointF = mAllLastPoints.get(i);
                PointF endLastPointF = mAllLastPoints.get(i + 1);
                if (mAniLinePath.isEmpty()) {
                    mAniLinePath.moveTo(midPointF.x, midLastPointF.y + (midPointF.y - midLastPointF.y) * mAniRatio);
                    mShadeAreaPath.moveTo(midPointF.x, midLastPointF.y + (midPointF.y - midLastPointF.y) * mAniRatio);
                }
                float con_x = (midPointF.x + endPointF.x) / 2;
                mAniLinePath.cubicTo(con_x, midLastPointF.y + (midPointF.y - midLastPointF.y) * mAniRatio, con_x,
                        endLastPointF.y + (endPointF.y - endLastPointF.y) * mAniRatio, endPointF.x,
                        endLastPointF.y + (endPointF.y - endLastPointF.y) * mAniRatio);
                mShadeAreaPath.cubicTo(con_x, midLastPointF.y + (midPointF.y - midLastPointF.y) * mAniRatio, con_x,
                        endLastPointF.y + (endPointF.y - endLastPointF.y) * mAniRatio, endPointF.x,
                        endLastPointF.y + (endPointF.y - endLastPointF.y) * mAniRatio);

            }
        } else {
            for (int i = 0; i < mAllPoints.size(); i++) {
                PointF midPointF = mAllPoints.get(i);
                PointF lastPointF = mAllLastPoints.get(i);
                if (mAniLinePath.isEmpty()) {
                    mAniLinePath.moveTo(midPointF.x, lastPointF.y + (midPointF.y - lastPointF.y) * mAniRatio);
                    mAniLinePath.moveTo(midPointF.x, lastPointF.y + (midPointF.y - lastPointF.y) * mAniRatio);
                } else {
                    mAniLinePath.lineTo(midPointF.x, lastPointF.y + (midPointF.y - lastPointF.y) * mAniRatio);
                    mShadeAreaPath.lineTo(midPointF.x, lastPointF.y + (midPointF.y - lastPointF.y) * mAniRatio);
                }
            }
        }
        canvas.drawPath(mAniLinePath, mLinePaint);
    }

    private void drawLineAsWave(Canvas canvas) {
        for (Jchart jchart : mJcharts) {
            mJcharts.get((int) mAniRatio).aniHeight(this);
            jchart.draw(canvas, mLinePaint, true);
//            jchart.draw(canvas, mLinePaint, false);
//            jchart.aniHeight(this).draw(canvas, mLinePaint, true);
        }
        if (mLineStyle == LineStyle.LINE_CURVE) {
            setUpCurveLinePath();
        } else {
            setUpBrokenLinePath();
        }
        canvas.drawPath(mAniLinePath, mLinePaint);
    }

    private void setUpCurveLinePath() {
        for (int i = 0; i < mJcharts.size() - 1; i++) {
            PointF startPoint = mJcharts.get(i).getMidPointF();
            PointF endPoint = mJcharts.get(i + 1).getMidPointF();//下一个点
            if (mAniLinePath.isEmpty()) {
                mAniLinePath.moveTo(startPoint.x, startPoint.y);
                mShadeAreaPath.moveTo(startPoint.x, startPoint.y);
            }
            float contr = (startPoint.x + endPoint.x) / 2;
            mAniLinePath.cubicTo(contr, startPoint.y, contr, endPoint.y, endPoint.x, endPoint.y);
            mShadeAreaPath.cubicTo(contr, startPoint.y, contr, endPoint.y, endPoint.x, endPoint.y);
        }
    }

    private void setUpBrokenLinePath() {
        for (Jchart chart : mJcharts) {
            if (mAniLinePath.isEmpty()) {
                mAniLinePath.moveTo(chart.getMidPointF().x, chart.getMidPointF().y);
                mShadeAreaPath.moveTo(chart.getMidPointF().x, chart.getMidPointF().y);
            } else {
                mAniLinePath.lineTo(chart.getMidPointF().x, chart.getMidPointF().y);
                mShadeAreaPath.lineTo(chart.getMidPointF().x, chart.getMidPointF().y);
            }
        }
    }

    /**
     * 无到有 画出完整线条动画
     *
     * @param canvas
     */
    private void drawLineAllpointDrawing(Canvas canvas) {
        if (mCurPosition == null) {
            return;
        }
        //动画
        if (mCurPosition[0] <= mChartLeftest_x) {
            mPrePoint = mJcharts.get(0).getMidPointF();
            mAniLinePath.moveTo(mPrePoint.x, mPrePoint.y);
        } else {
            if (mPrePoint == null) {
                mPrePoint = mJcharts.get(0).getMidPointF();
            }
            if (mLineStyle == LineStyle.LINE_CURVE) {
                float controllA_X = (mPrePoint.x + mCurPosition[0]) / 2;
                float controllA_Y = mPrePoint.y;
                float controllB_X = (mPrePoint.x + mCurPosition[0]) / 2;
                float controllB_Y = mCurPosition[1];
                mAniLinePath
                        .cubicTo(controllA_X, controllA_Y, controllB_X, controllB_Y, mCurPosition[0], mCurPosition[1]);
            } else {
                mAniLinePath.lineTo(mCurPosition[0], mCurPosition[1]);
            }
            mPrePoint.x = mCurPosition[0];
            mPrePoint.y = mCurPosition[1];
        }
        canvas.drawPath(mAniLinePath, mLinePaint);
        Jchart jchart = mJcharts.get((int) ((mCurPosition[0] - mChartArea.left) / mBetween2Excel));
        drawAbscissaMsg(canvas, jchart);
    }

    /**
     * 一段一段 画出线条
     *
     * @param canvas
     */
    private void drawLineAllpointSectionMode(Canvas canvas) {
        int currPosition = (int) mAniRatio;
        if (mLineStyle == LineStyle.LINE_BROKEN) {
            Jchart jchart = mJcharts.get(currPosition);
            if (currPosition == 0) {
                mAniLinePath.moveTo(jchart.getMidPointF().x, jchart.getMidPointF().y);
            } else {
                mAniLinePath.lineTo(jchart.getMidPointF().x, jchart.getMidPointF().y);
            }
            canvas.drawPath(mAniLinePath, mLinePaint);
            drawAbscissaMsg(canvas, jchart);
        } else {
            PointF currPointf = mJcharts.get(currPosition).getMidPointF();
            if (mPrePoint == null) {
                mPrePoint = mJcharts.get(0).getMidPointF();
            }
            if (currPosition == 0) {
                mAniLinePath.moveTo(mPrePoint.x, mPrePoint.y);
            }
            float controllA_X = (mPrePoint.x + currPointf.x) / 2;
            float controllA_Y = mPrePoint.y;
            float controllB_X = (mPrePoint.x + currPointf.x) / 2;
            float controllB_Y = currPointf.y;
            mAniLinePath.cubicTo(controllA_X, controllA_Y, controllB_X, controllB_Y, currPointf.x, currPointf.y);
            mPrePoint = currPointf;
            canvas.drawPath(mAniLinePath, mLinePaint);
            Jchart jchart = mJcharts.get(((int) mAniRatio));
            drawAbscissaMsg(canvas, jchart);
        }

    }

    private void lineSkip0Point(Canvas canvas) {
        if (mLineMode == LineMode.LINE_DASH_0) {
            //虚线连接

            for (int i = 0; i < mJcharts.size(); i++) {
                Jchart jchart = mJcharts.get(i);
                PointF midPointF = jchart.getMidPointF();
                if (i == 0) {
                    mLinePath.moveTo(midPointF.x, midPointF.y);
                } else {
                    mLinePath.lineTo(midPointF.x, midPointF.y);
                }
            }
        } else {
            //跳过0 断开


        }
    }

    /**
     * 画纵坐标信息
     *
     * @param canvas
     */
    private void drawYabscissaMsg(Canvas canvas) {
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

    protected void drawCoordinateAxes(Canvas canvas) {
        mCoordinatePaint.setStrokeWidth(0.4f);
        if (mJcharts != null && mJcharts.size() > 0) {
            canvas.drawLine(mChartArea.left, mChartArea.bottom, mJcharts.get(mJcharts.size() - 1).getMidPointF().x,
                    mChartArea.bottom, mCoordinatePaint);
        } else {
            canvas.drawLine(mChartArea.left, mChartArea.bottom, mChartArea.right, mChartArea.bottom, mCoordinatePaint);
        }
    }

    /**
     * 传入 数据
     */
    public void cmdFill(List<Jchart> jchartList) {
        mSelected = -1;
        mJcharts.clear();
        if (jchartList != null && jchartList.size() > 0) {
            mAllLastPoints = new ArrayList<>(jchartList.size());
            mHeightestChart = jchartList.get(0);
            for (int i = 0; i < jchartList.size(); i++) {
                Jchart jchart = jchartList.get(i);
                jchart.setIndex(i);//第几个
                if (jchart.getUpper() > mHeightestChart.getUpper()) {
                    mHeightestChart = jchart;
                    if (mYaxis_msg == null || mYaxis_msg.size() == 0) {
                        //默认 y轴显示两段三个刻度
                        setYaxisValues(getCeil10(mHeightestChart.getUpper()), 3);
                    } else {
                        if (mYaxis_Max < mHeightestChart.getUpper()) {
                            setYaxisValues((int) mYaxis_min, getCeil10(mHeightestChart.getUpper()), mYaxis_msg.size());
                        }
                    }
                }
                mAllLastPoints.add(new PointF(jchart.getMidX(), -1));
            }
            mJcharts.addAll(jchartList);
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refreshChartArea();
        refreshChartSetData();
    }

    /**
     * 刷新 画图表的区域
     */
    private void refreshChartArea() {
        float yMsgLength = 0;
        float yMsgHeight = 0;

        if (mNeedY_abscissMasg) {
            //如果 需要 纵轴坐标的时候
            yMsgLength = mAbscissaPaint.measureText(mYaxismax, 0, mYaxismax.length());
            Rect bounds = new Rect();
            mAbscissaPaint.getTextBounds(mYaxismax, 0, mYaxismax.length(), bounds);
            yMsgLength = bounds.width() < yMsgLength ? bounds.width() : yMsgLength;
            if (mSelectedMode == SelectedMode.selecetdMsgShow_Top) {
                yMsgHeight = bounds.height() + 2.5f * mBgTriangleHeight;
            } else {
                yMsgHeight = bounds.height();
            }
        }
        if (allowInterval_left_right) {
            //如过 允许 图表左右两边留有 间隔的时候
            mChartArea = new RectF(yMsgLength + getPaddingLeft() + mInterval, getPaddingTop() + yMsgHeight,
                    mWidth + getPaddingLeft() - mInterval, getPaddingTop() + mHeight - 2 * mAbscissaMsgSize);
        } else {
            mChartArea = new RectF(yMsgLength + getPaddingLeft(), getPaddingTop() + yMsgHeight, mWidth + getPaddingLeft(),
                    getPaddingTop() + mHeight - 2 * mAbscissaMsgSize);
        }
    }

    /**
     * 以动画方式 切换数据
     *
     * @param jchartList
     */
    @Override
    public void aniChangeData(List<Jchart> jchartList) {
        mState = State.aniChange;
        if (mLineMode != LineMode.LINE_EVERYPOINT) {
            throw new RuntimeException("use aniChangeData lineMode must be LineMode.LINE_EVERYPOINT");
        }
        if (jchartList != null && jchartList.size() == mAllLastPoints.size()) {
            mAllLastPoints.clear();
            mSelected = -1;
            mJcharts.clear();
            mHeightestChart = jchartList.get(0);
            for (int i = 0; i < jchartList.size(); i++) {
                //保存上一次的数据
                PointF allPoint = mAllPoints.get(i);
                mAllLastPoints.add(new PointF(allPoint.x, allPoint.y));

                Jchart jchart = jchartList.get(i);
                jchart.setIndex(i);
                if (jchart.getUpper() > mHeightestChart.getUpper()) {
                    mHeightestChart = jchart;
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "最大值：" + mHeightestChart.getUpper());
                    }
                    if (mYaxis_msg == null || mYaxis_msg.size() == 0) {
                        //默认 y轴显示两段三个刻度
                        setYaxisValues(getCeil10(mHeightestChart.getUpper()), 2);
                    } else {
                        if (mYaxis_Max < mHeightestChart.getUpper()) {
                            setYaxisValues((int) mYaxis_min, getCeil10(mHeightestChart.getUpper()), mYaxis_msg.size());
                        }
                    }
                }
            }
            mJcharts.addAll(jchartList);

            if (!mScrollAble && mForceFixNums && mJcharts.size() > mVisibleNums) {
                //如果不可滚动的话 同时要显示固定个数 那么为防止显示不全 将可见个数设置为柱子数量
                mVisibleNums = mJcharts.size();
            }

            if (mWidth > 0) {
                //已经显示在界面上了 重新设置数据
                refreshChartSetData();
            }
            if (mChartStyle == ChartStyle.LINE) {
                aniShowChar(0, 1, new LinearInterpolator());
            } else {
                postInvalidate();
            }
        } else {
            throw new RuntimeException("aniChangeData的数据必须和第一次传递cmddata的数据量相同");
        }
    }

    /**
     * 获取屏幕 宽高 后 更新 图表区域 矩阵数据 柱子宽 间隔宽度
     */
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
    private void refreshExcels() {
        if (mJcharts == null) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "数据为空 ");
            }
            return;
        }
        if (mYaxis_msg != null && (mYaxis_Max <= 0 || mYaxis_Max < mHeightestChart.getUpper())) {
            setYaxisValues((int) mYaxis_min, getRound10(mHeightestChart.getUpper()), mYaxis_msg.size());
        }
        mHeightRatio = (mChartArea.bottom - mChartArea.top) / (mYaxis_Max - mYaxis_min);
        //填充 path
        mLinePath.reset();
        mAllPoints.clear();
        for (int i = 0; i < mJcharts.size(); i++) {
            Jchart jchart = mJcharts.get(i);
            jchart.setHeight((jchart.getHeight() - mYaxis_min) * mHeightRatio);//刷新在画布中的高度
            jchart.setWidth(mBarWidth);
            PointF start = jchart.getStart();
            //刷新 每个柱子矩阵左下角坐标
            start.x = mChartArea.left + mBarWidth * i + mInterval * i;
            start.y = mChartArea.bottom - mAbove - jchart.getLower();

            jchart.setColor(mNormalColor);
            if (mChartStyle == ChartStyle.LINE) {
                if (mLineMode == LineMode.LINE_EVERYPOINT) {
                    mAllPoints.add(jchart.getMidPointF());
                } else {
                    if (jchart.getHeight() > 0) {
                        mAllPoints.add(jchart.getMidPointF());
                    }
                }
                //aniChangeData之后会刷新数据 mAllLastPoints的y不等于0 mAllLastPoints.get(i).y == 0
                if (mLineShowStyle == LineShowStyle.LINESHOW_FROMLINE && mAllLastPoints.get(i).y == 0) {
                    if (mShowFromMode == ShowFromMode.SHOWFROMBUTTOM) {
                        mAllLastPoints.get(i).y = mChartArea.bottom;//0转为横轴纵坐标
                    } else if (mShowFromMode == ShowFromMode.SHOWFROMTOP) {
                        mAllLastPoints.get(i).y = mChartArea.top;//0转为横轴纵坐标
                    } else if (mShowFromMode == ShowFromMode.SHOWFROMMIDDLE) {
                        mAllLastPoints.get(i).y = (mChartArea.bottom + mChartArea.top) / 2;//0转为横轴纵坐标
                    }
                }
            } else {
                if (BuildConfig.DEBUG) Log.e(TAG, "当前图形 是柱状图");
                //continue; //没必要continue
            }
        }

        //曲线
        if (mLineStyle == LineStyle.LINE_CURVE) {
            for (int i = 0; i < mAllPoints.size() - 1; i++) {
                PointF startPoint = mAllPoints.get(i);
                PointF endPoint = mAllPoints.get(i + 1);//下一个点
                if (mLinePath.isEmpty()) {
                    mLinePath.moveTo(startPoint.x, startPoint.y);
                }
                float controllA_X = (startPoint.x + endPoint.x) / 2;
                float controllA_Y = startPoint.y;
                float controllB_X = (startPoint.x + endPoint.x) / 2;
                float controllB_Y = endPoint.y;
                mLinePath.cubicTo(controllA_X, controllA_Y, controllB_X, controllB_Y, endPoint.x, endPoint.y);
            }
        } else {
            for (PointF allPoint : mAllPoints) {
                if (mLinePath.isEmpty()) {
                    mLinePath.moveTo(allPoint.x, allPoint.y);
                } else {
                    mLinePath.lineTo(allPoint.x, allPoint.y);
                }
            }
        }
        mChartRithtest_x = mJcharts.get(mJcharts.size() - 1).getMidPointF().x;
        mChartLeftest_x = mJcharts.get(0).getMidPointF().x;

        if (mShaderAreaColors != null && mChartStyle == ChartStyle.LINE) {
            mShadeAreaPath = new Path(mLinePath);//起点为第一个点
            //            mShadeAreaPath.addPath(mLinePath);//起点为第一个点
            mShadeAreaPath.setLastPoint(mChartLeftest_x, mJcharts.get(0).getMidPointF().y);
            mShadeAreaPath.lineTo(mChartLeftest_x, mChartArea.bottom);
            mShadeAreaPath.lineTo(mChartRithtest_x, mChartArea.bottom);
            //            mShadeAreaPath.lineTo(mChartRithtest_x,mJcharts.get(mJcharts.size()-1).getMidPointF().y);
        }
        if (mJcharts.size() > 1) {
            mBetween2Excel = mJcharts.get(1).getMidPointF().x - mJcharts.get(0).getMidPointF().x;
        }
        //        aniShow_growing();
    }

    public void aniShow_growing() {
        if (mLineShowStyle == LineShowStyle.LINESHOW_DRAWING) {
            mPathMeasure = new PathMeasure(mLinePath, false);
            mPathMeasure.getPosTan(0, mCurPosition, null);
            aniShowChar(0, mPathMeasure.getLength());
        } else if (mLineShowStyle == LineShowStyle.LINESHOW_SECTION) {
            aniShowChar(0, mJcharts.size());
        } else if (mLineShowStyle == LineShowStyle.LINESHOW_FROMLINE || mLineShowStyle == LineShowStyle.LINESHOW_FROMCORNER) {
            aniShowChar(0, 1);
        } else if (mLineShowStyle == LineShowStyle.LINESHOW_ASWAVE) {
            if (mJcharts.size() > 0) {
                aniShowChar(0, mJcharts.size() - 1, new LinearInterpolator(), (mJcharts.size() - 1) * 300);
                for (Jchart jchart : mJcharts) {
                    jchart.setAniratio(0);
                }
            }
        }
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
        mAniLinePath.reset();
        //        if(mLineStyle == LineStyle.LINE_CURVE) {
        //            mLinePath.rewind();//倒序
        //        }
        //        mValueAnimator.isRunning()
        mValueAnimator.setDuration(duration);
        mValueAnimator.setInterpolator(interpolator);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAniRatio = (float) animation.getAnimatedValue();
                if (mChartStyle == ChartStyle.LINE) {
                    if (mLineShowStyle == LineShowStyle.LINESHOW_DRAWING) {
                        //mCurPosition必须要初始化mCurPosition = new float[2];
                        mPathMeasure.getPosTan(mAniRatio, mCurPosition, null);
                    }
                }
                invalidate();
            }
        });
        mValueAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAllLastPoints.clear();
        mAllPoints.clear();
        mJcharts.clear();
        mShadeAreaPath = null;
        mLinePath = null;
        mShaderColors = null;
        mChartArea = null;
        mPathMeasure = null;
        mCurPosition = null;
        mAllPoints = null;
        mAllLastPoints = null;
        mAniLinePath = null;
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
    public void setLineShaderColors(int... colors) {
        mShaderColors = colors;
    }

    public void setShaderAreaColors(int... colors) {
        mShaderAreaColors = colors;
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
        refreshExcels();
    }

    public void setNeedY_abscissMasg(boolean needY_abscissMasg) {
        mNeedY_abscissMasg = needY_abscissMasg;
    }

    public float getAniRatio() {
        return mAniRatio;
    }

    public void setAniRatio(float aniRatio) {
        this.mAniRatio = aniRatio;
    }

    public float getAniRotateRatio() {
        return mAniRotateRatio;
    }

    public void setAniRotateRatio(float aniRotateRatio) {
        mAniRotateRatio = aniRotateRatio;
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "onAttachedToWindow ");
        }
    }

    @Override
    public void setSelectedMode(int selectedMode) {
        if (mSelectedMode != SelectedMode.selecetdMsgShow_Top) {
            mSelectedMode = selectedMode;
            if (mWidth > 0) {
                refreshChartArea();
            }
        }
    }

    public void setSelectedTextMarging(float selectedTextMarging) {
        mSelectedTextMarging = selectedTextMarging;
    }

    public void setLineStyle(int lineStyle) {
        mLineStyle = lineStyle;
    }

    //    @Override
    //    public void setChartStyle(int chartStyle){
    //        mChartStyle = ChartStyle.LINE;
    //    }

    /**
     * 设置fromline动画的最初起点
     *
     * @param showFromMode
     */
    public void setShowFromMode(int showFromMode) {
        mShowFromMode = showFromMode;
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        mInterpolator = interpolator;
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

}
//可滚动   根据可见个数 计算barwidth
//
//不可滚动
//   1 无视mVisibleNums 所有柱子平均显示
//   2 固定最多个数mVisibleNums 大于等于mExecels的数量
//