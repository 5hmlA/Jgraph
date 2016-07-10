package com.jonas.jdiagram.graph;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.jonas.jdiagram.BuildConfig;
import com.jonas.jdiagram.R;
import com.jonas.jdiagram.inter.BaseGraph;
import com.jonas.jdiagram.models.Jchart;

import java.util.List;

/**
 * @author jiangzuyun.
 * @date 2016/6/8
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 */
public class JcoolGraph extends BaseGraph {
    private static final String TAG = JcoolGraph.class.getSimpleName();
    private Paint mBarPaint;
    private int mBarStanded = 0;

    public interface LineStyle {
        /**
         * 折线
         */
        int LINE_BROKEN = 0;
        /**
         * 曲线
         */
        int LINE_CURVE = 1;

    }

    public interface LineShowStyle {
        /**
         * 线条从无到有 慢慢出现
         */
        int LINESHOW_DRAWING = 0;
        /**
         * 线条 一段一段显示
         */
        int LINESHOW_SECTION = 1;
        /**
         * 线条 一从直线慢慢变成折线/曲线
         */
        int LINESHOW_FROMLINE = 2;

        /**
         * 从左上角 放大
         */
        int LINESHOW_FROMCORNER = 3;
        /**
         * 水波 方式展开
         */
        int LINESHOW_ASWAVE = 4;
    }

    public interface BarShowStyle {

        /**
         * 水波 方式展开
         */
        int BARSHOW_ASWAVE = 0;
        /**
         * 线条 一从直线慢慢变成折线/曲线
         */
        int BARSHOW_FROMLINE = 1;
        /**
         * 柱形条 由某个往外扩散
         */
        int BARSHOW_EXPAND = 2;

        /**
         * 线条 一段一段显示
         */
        int BARSHOW_SECTION = 3;
    }

    private int mBarShowStyle = BarShowStyle.BARSHOW_ASWAVE;

    public static interface ShowFromMode {
        int SHOWFROMTOP = 0;
        int SHOWFROMBUTTOM = 1;
        int SHOWFROMMIDDLE = 2;
    }

    public interface LineMode {
        /**
         * 连接每一个点
         */
        int LINE_EVERYPOINT = 0;
        /**
         * 跳过0  断开
         */
        int LINE_JUMP0 = 1;

        /**
         * 跳过0 用虚线链接
         */
        int LINE_DASH_0 = 2;
    }

    private int mLineMode = LineMode.LINE_EVERYPOINT;
    private int mShowFromMode = ShowFromMode.SHOWFROMMIDDLE;
    private int mLineStyle = LineStyle.LINE_CURVE;
    /**
     * linepath动画存储点
     */
    private float[] mCurPosition = new float[2];
    private PathMeasure mPathMeasure;
    private PointF mPrePoint;
    /**
     * 折线两点的间隔
     */
    private float mBetween2Excel;

    /**
     * 线条 和横轴之间的 渐变区域
     */
    private Paint mShaderAreaPaint;
    private int[] mShaderAreaColors;
    private Path mAniShadeAreaPath = new Path();
    private Path mShadeAreaPath = new Path();

    /**
     * 线条展示的动画风格
     */
    protected int mLineShowStyle = LineShowStyle.LINESHOW_ASWAVE;


    private Paint mLinePaint;
    private float mLineWidth = -1;

    /**
     * 渐变色
     */
    private int[] mShaderColors;

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
     * 图表出现动画旋转角度
     */
    private float mAniRotateRatio = 0;

    public JcoolGraph(Context context) {
        super(context);
    }

    public JcoolGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AndroidJgraph);
        mLineStyle = a.getInteger(R.styleable.AndroidJgraph_linestyle, LineStyle.LINE_CURVE);
        mLineMode = a.getInteger(R.styleable.AndroidJgraph_linemode, LineMode.LINE_EVERYPOINT);
        mLineWidth = a.getDimension(R.styleable.AndroidJgraph_linewidth, dip2px(1.2f));
        mLineShowStyle = a.getInt(R.styleable.AndroidJgraph_lineshowstyle, LineShowStyle.LINESHOW_ASWAVE);
        a.recycle();
        initializeData();
    }

    public JcoolGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShaderAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private void initializeData() {
        mLineWidth = mLineWidth == -1 ? dip2px(1.2f) : mLineWidth;
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(mNormalColor);
        mBarPaint.setColor(mNormalColor);
        mLinePaint.setStrokeWidth(mLineWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //这里可以用画布做一些动画
        canvas.save();
        canvas.rotate(mAniRotateRatio);
        canvas.translate(mSliding, 0);//大于0 往右移动 小于0往左移动
        super.onDraw(canvas);
        canvas.restore();
        //纵轴信息固定
        if (mNeedY_abscissMasg && mYaxis_msg != null) {
            drawYabscissaMsg(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        paintSetShader(mShaderAreaPaint, mShaderAreaColors);
        paintSetShader(mLinePaint, mShaderColors);
        paintSetShader(mBarPaint, mShaderColors);
    }

    @Override
    protected void drawSugExcel_BAR(Canvas canvas) {

        if (mState == State.aniChange && mAniRatio < 1) {
            barAniChanging(canvas);
            for (Jchart jchart : mJcharts) {
//                    jchart.draw(canvas, mBarPaint, false);
                jchart.draw(canvas, mCoordinatePaint, false);
            }
        } else {
            mState = -1;
            if (mLastJchart.getAniratio() >= 1 && !mValueAnimator.isRunning()) {
                for (Jchart jchart : mJcharts) {
                    jchart.draw(canvas, mBarPaint, false);
//                    jchart.draw(canvas, mCoordinatePaint, false);
                }
            } else if (mBarShowStyle == BarShowStyle.BARSHOW_ASWAVE) {
                for (Jchart jchart : mJcharts) {
                    mJcharts.get((int) mAniRatio).aniHeight(this, 0, new AccelerateInterpolator());
                    jchart.draw(canvas, mBarPaint, false);
                }
            } else if (mBarShowStyle == BarShowStyle.BARSHOW_FROMLINE) {
                barAniChanging(canvas);
            } else if (mBarShowStyle == BarShowStyle.BARSHOW_EXPAND) {
                drawBarExpand(canvas);
            } else if (mBarShowStyle == BarShowStyle.BARSHOW_SECTION) {
                drawBarSection(canvas);
            }
        }
    }

    private void drawBarSection(Canvas canvas) {
        for (int i = 0; i < ((int) mAniRatio); i++) {
            Jchart jchart = mJcharts.get(i);
            jchart.draw(canvas, mBarPaint, false);
        }
    }

    private void drawBarExpand(Canvas canvas) {
        for (Jchart jchart : mJcharts) {
            if (mBarStanded >= mJcharts.size()) {
                mBarStanded = mJcharts.size() - 1;
            }
            Jchart sjchart = mJcharts.get(mBarStanded);
            //以mBarStanded为准 左右散开
            canvas.drawRect(sjchart.getRectF().left + (jchart.getRectF().left - sjchart.getRectF().left) * mAniRatio, jchart.getRectF().top,
                    sjchart.getRectF().right + (jchart.getRectF().right - sjchart.getRectF().right) * mAniRatio,
                    jchart.getRectF().bottom, mBarPaint);
        }
    }

    private void barAniChanging(Canvas canvas) {
        for (int i = 0; i < mJcharts.size(); i++) {
            Jchart jchart = mJcharts.get(i);
            float lasty = mAllLastPoints.get(i).y;
            RectF rectF = jchart.getRectF();
            RectF drectf = new RectF(rectF.left, lasty + (rectF.top - lasty) * mAniRatio, rectF.right, rectF.bottom);
            canvas.drawRect(drectf, mBarPaint);
        }
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

        mLinePaint.setColor(mNormalColor);
        if (mState == State.aniChange && mAniRatio < 1) {
            drawLineAllpointFromLineMode(canvas);
            for (Jchart jchart : mJcharts) {
                jchart.draw(canvas, mLinePaint, true);
            }
            LeftShaderArea(canvas);
            return;
        } else {
            mState = -1;
            if ((mLineShowStyle == LineShowStyle.LINESHOW_FROMCORNER || mLineShowStyle == LineShowStyle.LINESHOW_DRAWING ||
                    mLineShowStyle == LineShowStyle.LINESHOW_SECTION) && !mValueAnimator.isRunning()) {
                mAniShadeAreaPath.reset();
                mAniLinePath.reset();
                canvas.drawPath(mLinePath, mLinePaint);
                if (mShaderAreaColors != null) {
                    canvas.drawPath(mShadeAreaPath, mShaderAreaPaint);
                }
                return;
            } else if (mLineShowStyle == LineShowStyle.LINESHOW_DRAWING) {
                drawLineAllpointDrawing(canvas);
//                canvas.drawPath(mShadeAreaPath, mShaderAreaPaint);
            } else if (mLineShowStyle == LineShowStyle.LINESHOW_SECTION) {
                drawLineAllpointSectionMode(canvas);
            } else if (mLineShowStyle == LineShowStyle.LINESHOW_FROMLINE) {
                drawLineAllpointFromLineMode(canvas);
                LeftShaderArea(canvas);
            } else if (mLineShowStyle == LineShowStyle.LINESHOW_FROMCORNER) {
                drawLineAllpointFromCornerMode(canvas);
                LeftShaderArea(canvas);
            } else if (mLineShowStyle == LineShowStyle.LINESHOW_ASWAVE) {
                drawLineAsWave(canvas);
                LeftShaderArea(canvas);
            }
        }
    }

    private void LeftShaderArea(Canvas canvas) {
        //渐变区域
        if (mShaderAreaColors != null) {
            mAniShadeAreaPath.lineTo(mChartRithtest_x, mChartArea.bottom);
            mAniShadeAreaPath.lineTo(mChartLeftest_x, mChartArea.bottom);
            mAniShadeAreaPath.close();
            canvas.drawPath(mAniShadeAreaPath, mShaderAreaPaint);
        }
    }

    private void drawLineAllpointFromCornerMode(Canvas canvas) {
        mAniShadeAreaPath.reset();
        mAniLinePath.reset();
        if (mLineStyle == LineStyle.LINE_CURVE) {
            for (int i = 0; i < mAllPoints.size() - 1; i++) {
                PointF midPointF = mAllPoints.get(i);
                PointF endPointF = mAllPoints.get(i + 1);
                if (mAniLinePath.isEmpty()) {
                    mAniLinePath.moveTo(midPointF.x * mAniRatio, midPointF.y * mAniRatio);
                    mAniShadeAreaPath.moveTo(midPointF.x * mAniRatio, midPointF.y * mAniRatio);
                }
                float con_x = (midPointF.x + endPointF.x) / 2;
                mAniLinePath.cubicTo(con_x * mAniRatio, midPointF.y * mAniRatio, con_x * mAniRatio, endPointF.y * mAniRatio,
                        endPointF.x * mAniRatio, endPointF.y * mAniRatio);
                mAniShadeAreaPath.cubicTo(con_x * mAniRatio, midPointF.y * mAniRatio, con_x * mAniRatio, endPointF.y * mAniRatio,
                        endPointF.x * mAniRatio, endPointF.y * mAniRatio);

            }
        } else {
            for (PointF midPointF : mAllPoints) {
                if (mAniLinePath.isEmpty()) {
                    mAniLinePath.moveTo(midPointF.x * mAniRatio, midPointF.y * mAniRatio);
                    mAniShadeAreaPath.moveTo(midPointF.x * mAniRatio, midPointF.y * mAniRatio);
                } else {
                    mAniLinePath.lineTo(midPointF.x * mAniRatio, midPointF.y * mAniRatio);
                    mAniShadeAreaPath.lineTo(midPointF.x * mAniRatio, midPointF.y * mAniRatio);
                }
            }
        }
        canvas.drawPath(mAniLinePath, mLinePaint);
    }

    private void drawLineAllpointFromLineMode(Canvas canvas) {
        mAniShadeAreaPath.reset();
        mAniLinePath.reset();
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
                    mAniShadeAreaPath.moveTo(midPointF.x, midLastPointF.y + (midPointF.y - midLastPointF.y) * mAniRatio);
                }
                float con_x = (midPointF.x + endPointF.x) / 2;
                mAniLinePath.cubicTo(con_x, midLastPointF.y + (midPointF.y - midLastPointF.y) * mAniRatio, con_x,
                        endLastPointF.y + (endPointF.y - endLastPointF.y) * mAniRatio, endPointF.x,
                        endLastPointF.y + (endPointF.y - endLastPointF.y) * mAniRatio);
                mAniShadeAreaPath.cubicTo(con_x, midLastPointF.y + (midPointF.y - midLastPointF.y) * mAniRatio, con_x,
                        endLastPointF.y + (endPointF.y - endLastPointF.y) * mAniRatio, endPointF.x,
                        endLastPointF.y + (endPointF.y - endLastPointF.y) * mAniRatio);

            }
        } else {
            for (int i = 0; i < mAllPoints.size(); i++) {
                PointF midPointF = mAllPoints.get(i);
                PointF lastPointF = mAllLastPoints.get(i);
                if (mAniLinePath.isEmpty()) {
                    mAniLinePath.moveTo(midPointF.x, lastPointF.y + (midPointF.y - lastPointF.y) * mAniRatio);
                    mAniShadeAreaPath.moveTo(midPointF.x, lastPointF.y + (midPointF.y - lastPointF.y) * mAniRatio);
                } else {
                    mAniLinePath.lineTo(midPointF.x, lastPointF.y + (midPointF.y - lastPointF.y) * mAniRatio);
                    mAniShadeAreaPath.lineTo(midPointF.x, lastPointF.y + (midPointF.y - lastPointF.y) * mAniRatio);
                }
            }
        }
        canvas.drawPath(mAniLinePath, mLinePaint);
    }

    private void drawLineAsWave(Canvas canvas) {
        mAniShadeAreaPath.reset();
        mAniLinePath.reset();
        for (Jchart jchart : mJcharts) {
            mJcharts.get((int) mAniRatio).aniHeight(this);
//            jchart.draw(canvas, mLinePaint, true);
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
                mAniShadeAreaPath.moveTo(startPoint.x, startPoint.y);
            }
            float contr = (startPoint.x + endPoint.x) / 2;
            mAniLinePath.cubicTo(contr, startPoint.y, contr, endPoint.y, endPoint.x, endPoint.y);
            mAniShadeAreaPath.cubicTo(contr, startPoint.y, contr, endPoint.y, endPoint.x, endPoint.y);
        }
    }

    private void setUpBrokenLinePath() {
        for (Jchart chart : mJcharts) {
            if (mAniLinePath.isEmpty()) {
                mAniLinePath.moveTo(chart.getMidPointF().x, chart.getMidPointF().y);
                mAniShadeAreaPath.moveTo(chart.getMidPointF().x, chart.getMidPointF().y);
            } else {
                mAniLinePath.lineTo(chart.getMidPointF().x, chart.getMidPointF().y);
                mAniShadeAreaPath.lineTo(chart.getMidPointF().x, chart.getMidPointF().y);
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
        if (mAniLinePath.isEmpty() || mCurPosition[0] <= mChartLeftest_x) {
            mPrePoint = mJcharts.get(0).getMidPointF();
            mAniLinePath.moveTo(mPrePoint.x, mPrePoint.y);
            mAniShadeAreaPath.moveTo(mPrePoint.x, mPrePoint.y);
        } else {
            if (mPrePoint == null) {
                mPrePoint = mJcharts.get(0).getMidPointF();
            }
            if (mLineStyle == LineStyle.LINE_CURVE) {
                float controllA_X = (mPrePoint.x + mCurPosition[0]) / 2;
                float controllA_Y = mPrePoint.y;
                float controllB_X = (mPrePoint.x + mCurPosition[0]) / 2;
                float controllB_Y = mCurPosition[1];
                mAniLinePath.cubicTo(controllA_X, controllA_Y, controllB_X, controllB_Y, mCurPosition[0], mCurPosition[1]);
                mAniShadeAreaPath.cubicTo(controllA_X, controllA_Y, controllB_X, controllB_Y, mCurPosition[0], mCurPosition[1]);
            } else {
                mAniLinePath.lineTo(mCurPosition[0], mCurPosition[1]);
                mAniShadeAreaPath.lineTo(mCurPosition[0], mCurPosition[1]);
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
        if (currPosition == 0) {
            mAniLinePath.reset();
            mAniShadeAreaPath.reset();
        }
        Jchart jchart = mJcharts.get(currPosition);
        if (mLineStyle == LineStyle.LINE_BROKEN) {
            if (currPosition == 0) {
                mAniLinePath.moveTo(jchart.getMidPointF().x, jchart.getMidPointF().y);
            } else {
                mAniLinePath.lineTo(jchart.getMidPointF().x, jchart.getMidPointF().y);
            }
            canvas.drawPath(mAniLinePath, mLinePaint);
            drawAbscissaMsg(canvas, jchart);
        } else {
            PointF currPointf = jchart.getMidPointF();
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
            drawAbscissaMsg(canvas, jchart);
        }
        //渐变区域
        if (mShaderAreaColors != null) {
            mAniShadeAreaPath.reset();
            for (int i = 0; i < currPosition + 1; i++) {
                PointF currPoint = mAllPoints.get(i);
                if (i == 0) {
                    mAniShadeAreaPath.moveTo(currPoint.x, currPoint.y);
                } else {
                    if (mLineStyle == LineStyle.LINE_BROKEN) {
                        mAniShadeAreaPath.lineTo(currPoint.x, currPoint.y);
                    } else {

                        float controllA_X = (mAllPoints.get(i - 1).x + currPoint.x) / 2;
                        float controllA_Y = mAllPoints.get(i - 1).y;
                        float controllB_X = (mAllPoints.get(i - 1).x + currPoint.x) / 2;
                        float controllB_Y = currPoint.y;
                        mAniShadeAreaPath.cubicTo(controllA_X, controllA_Y, controllB_X, controllB_Y, currPoint.x, currPoint.y);
                    }
                }
            }
            mAniShadeAreaPath.lineTo(jchart.getMidX(), mChartArea.bottom);
            mAniShadeAreaPath.lineTo(mChartLeftest_x, mChartArea.bottom);
            mAniShadeAreaPath.close();
            canvas.drawPath(mAniShadeAreaPath, mShaderAreaPaint);
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
     * 以动画方式 切换数据
     *
     * @param jchartList
     */
    @Override
    public void aniChangeData(List<Jchart> jchartList) {
        if (mWidth <= 0) {
            throw new RuntimeException("请使用cmdFill填充数据");
        }
        mSliding = 0;
        mState = State.aniChange;
        if (mGraphStyle == GraphStyle.LINE && mLineMode != LineMode.LINE_EVERYPOINT) {
            throw new RuntimeException("use aniChangeData lineMode must be LineMode.LINE_EVERYPOINT");
        }
        if (jchartList != null && jchartList.size() == mAllLastPoints.size()) {
            mAllLastPoints.clear();
            mSelected = -1;
            mJcharts.clear();
            mJcharts.addAll(jchartList);
            for (int i = 0; i < mJcharts.size(); i++) {
                Jchart jchart = mJcharts.get(i);
                jchart.setIndex(i);
                PointF allPoint = mAllPoints.get(i);
                //保存上一次的数据
                mAllLastPoints.add(new PointF(allPoint.x, allPoint.y));
            }
            refreshExcels();
            aniShowChar(0, 1, new LinearInterpolator());
        } else {
            throw new RuntimeException("aniChangeData的数据必须和第一次传递cmddata的数据量相同");
        }
    }

    @Override
    protected void refreshOthersWithEveryChart(int i, Jchart jchart) {

        if (mGraphStyle == GraphStyle.LINE) {
            if (mLineMode == JcoolGraph.LineMode.LINE_EVERYPOINT) {
                mAllPoints.add(jchart.getMidPointF());
            } else {
                if (jchart.getHeight() > 0) {
                    mAllPoints.add(jchart.getMidPointF());
                }
            }
            //aniChangeData之后会刷新数据 mAllLastPoints的y不等于0 mAllLastPoints.get(i).y == 0
            if (mAllLastPoints.get(i).y == -1) {
                if (mShowFromMode == JcoolGraph.ShowFromMode.SHOWFROMBUTTOM) {
                    mAllLastPoints.get(i).y = mChartArea.bottom;//0转为横轴纵坐标
                } else if (mShowFromMode == JcoolGraph.ShowFromMode.SHOWFROMTOP) {
                    mAllLastPoints.get(i).y = mChartArea.top;//0转为横轴纵坐标
                } else if (mShowFromMode == JcoolGraph.ShowFromMode.SHOWFROMMIDDLE) {
                    mAllLastPoints.get(i).y = (mChartArea.bottom + mChartArea.top) / 2;//0转为横轴纵坐标
                }
            }
        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "当前图形 是柱状图");
            //continue; //没必要continue
            mAllPoints.add(jchart.getMidPointF());
            if (mAllLastPoints.get(i).y == -1) {
                mAllLastPoints.get(i).y = mChartArea.bottom;//0转为横轴纵坐标
            }
        }
    }

    /**
     * 主要 刷新高度
     */
    protected void refreshExcels() {

        mAllPoints.clear();
        super.refreshExcels();
        refreshLinepath();

        mChartRithtest_x = mJcharts.get(mJcharts.size() - 1).getMidPointF().x;
        mChartLeftest_x = mJcharts.get(0).getMidPointF().x;

        if (mJcharts.size() > 1) {
            mBetween2Excel = mJcharts.get(1).getMidPointF().x - mJcharts.get(0).getMidPointF().x;
        }
    }

    private void refreshLinepath() {
        //填充 path
        mLinePath.reset();
        mShadeAreaPath.reset();
        //曲线
        if (mLineStyle == LineStyle.LINE_CURVE) {
            for (int i = 0; i < mAllPoints.size() - 1; i++) {
                PointF startPoint = mAllPoints.get(i);
                PointF endPoint = mAllPoints.get(i + 1);//下一个点
                if (mLinePath.isEmpty()) {
                    mLinePath.moveTo(startPoint.x, startPoint.y);
                    mShadeAreaPath.moveTo(startPoint.x, startPoint.y);
                }
                float controllA_X = (startPoint.x + endPoint.x) / 2;
                float controllA_Y = startPoint.y;
                float controllB_X = (startPoint.x + endPoint.x) / 2;
                float controllB_Y = endPoint.y;
                mLinePath.cubicTo(controllA_X, controllA_Y, controllB_X, controllB_Y, endPoint.x, endPoint.y);
                mShadeAreaPath.cubicTo(controllA_X, controllA_Y, controllB_X, controllB_Y, endPoint.x, endPoint.y);
            }
        } else {
            for (PointF allPoint : mAllPoints) {
                if (mLinePath.isEmpty()) {
                    mLinePath.moveTo(allPoint.x, allPoint.y);
                    mShadeAreaPath.moveTo(allPoint.x, allPoint.y);
                } else {
                    mLinePath.lineTo(allPoint.x, allPoint.y);
                    mShadeAreaPath.lineTo(allPoint.x, allPoint.y);
                }
            }
        }

        mShadeAreaPath.lineTo(mChartRithtest_x, mChartArea.bottom);
        mShadeAreaPath.lineTo(mChartLeftest_x, mChartArea.bottom);
        mShadeAreaPath.close();
    }

    @Override
    public void cmdFill(@NonNull List<Jchart> jchartList) {
        super.cmdFill(jchartList);
        if (mLineShowStyle != -1) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    aniShow_growing();
                }
            }, 1000);
        }
    }

    public void aniShow_growing() {
        if (mGraphStyle == GraphStyle.LINE) {
            if (mLineShowStyle == LineShowStyle.LINESHOW_DRAWING) {
                mAniLinePath.reset();
                mAniShadeAreaPath.reset();
                mPathMeasure = new PathMeasure(mLinePath, false);
                mPathMeasure.getPosTan(0, mCurPosition, null);
                aniShowChar(0, mPathMeasure.getLength(), new LinearInterpolator(), 3000);
            } else if (mLineShowStyle == LineShowStyle.LINESHOW_SECTION) {
                mAniLinePath.reset();
                mAniShadeAreaPath.reset();
                aniShowChar(0, mJcharts.size(), new LinearInterpolator(), 3000);
            } else if (mLineShowStyle == LineShowStyle.LINESHOW_FROMLINE || mLineShowStyle == LineShowStyle.LINESHOW_FROMCORNER) {
                if (mShowFromMode == ShowFromMode.SHOWFROMMIDDLE) {
                    aniShowChar(0, 1, new AccelerateInterpolator());
                } else {
                    aniShowChar(0, 1);
                }
            } else if (mLineShowStyle == LineShowStyle.LINESHOW_ASWAVE) {
                if (mJcharts.size() > 0) {
                    aniShowChar(0, mJcharts.size() - 1, new LinearInterpolator(), (mJcharts.size() - 1) * 200);
                    for (Jchart jchart : mJcharts) {
                        jchart.setAniratio(0);
                    }
                }
            }
        } else {
            if (mJcharts.size() > 0) {
                if (mBarShowStyle == BarShowStyle.BARSHOW_ASWAVE) {
                    for (Jchart jchart : mJcharts) {
                        jchart.setAniratio(0);
                    }
                    aniShowChar(0, mJcharts.size() - 1, new LinearInterpolator(), (mJcharts.size() - 1) * 200);
                } else if(mBarShowStyle == BarShowStyle.BARSHOW_SECTION){
                    aniShowChar(0, mJcharts.size() - 1, new LinearInterpolator(), (mJcharts.size() - 1) * 200);
                }else {
                    aniShowChar(0, 1, new LinearInterpolator());
                }

            }
        }
    }

    @Override
    protected void onAnimationUpdating(ValueAnimator animation) {
        mAniRatio = (float) animation.getAnimatedValue();
        if (mGraphStyle == GraphStyle.LINE) {
            if (mLineShowStyle == JcoolGraph.LineShowStyle.LINESHOW_DRAWING && mState != State.aniChange) {
                //mCurPosition必须要初始化mCurPosition = new float[2];
                mPathMeasure.getPosTan(mAniRatio, mCurPosition, null);
            }
        }
        postInvalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAllLastPoints.clear();
        mAllPoints.clear();
        mJcharts.clear();
        mAniShadeAreaPath = null;
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
    public void setPaintShaderColors(int... colors) {
        mShaderColors = colors;
//        mLinePaint.setStrokeWidth(dip2px(3));
        if (mWidth > 0) {
            paintSetShader(mLinePaint, mShaderColors);
            paintSetShader(mBarPaint, mShaderColors);
        }
    }

    public void setShaderAreaColors(int... colors) {
        mShaderAreaColors = colors;
        if (mWidth > 0) {
            paintSetShader(mShaderAreaPaint, mShaderAreaColors);
        }
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
        if (mWidth > 0) {
            refreshLinepath();
        }
    }

    /**
     * 设置-1 没动画
     *
     * @param lineShowStyle one of {@link LineShowStyle}
     */
    public void setLineShowStyle(int lineShowStyle) {
        mLineShowStyle = lineShowStyle;
    }

    public void setBarShowStyle(int barShowStyle) {
        mBarShowStyle = barShowStyle;
    }

    /**
     * 设置fromline动画的最初起点
     *
     * @param showFromMode one of {@link ShowFromMode}
     */
    public void setShowFromMode(int showFromMode) {
        mShowFromMode = showFromMode;
    }

}
//可滚动   根据可见个数 计算barwidth
//
//不可滚动
//   1 无视mVisibleNums 所有柱子平均显示
//   2 固定最多个数mVisibleNums 大于等于mExecels的数量
//