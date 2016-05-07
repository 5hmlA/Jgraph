package com.jonas.schart;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.jonas.schart.chart.NExcel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created on 2016/1/10.  by Jonas{https://github.com/mychoices} 画线的时候 必须把 画笔的style设置为 Paint.Style.STROKE
 */

public class NChart extends View implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

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
     * 选中的 柱状图
     */
    private int mSelected = -1;
    private float mDownX;
    private boolean moved;
    //文字间隔 上下margin
    private float mTextMarging;
    private float mTextSize = 15;

    private int mBarStanded = 0;
    /**
     * 最高的柱子 高度
     */
    private float mSugHeightest;
    /**
     * 柱子高度 缩放比
     */
    private float mHeightRatio = 1;
    /**
     * 是否 支持滚动
     */
    private boolean mScrollAble = true;

    //----------------画笔-----------
    private Paint mLinePaint;
    private Paint mLinePaint2;
    /**
     * 图表 画笔
     */
    private Paint mExecelPaint;
    private Paint mTextPaint;
    /**
     * 画横坐标信息
     */
    private Paint mAbscissaPaint;
    private Paint mTextBgPaint;
    private Paint mPointPaint;
    private Path pathLine = new Path();

    /**
     * 柱状图 选中的颜色
     */
    private int mActivationColor = Color.RED;

    /**
     * 柱状图 选中的颜色
     */
    private int mNormalColor = Color.DKGRAY;
    /**
     * 横坐标 信息颜色
     */
    private int mAbscissaMsgColor = Color.parseColor("#556A73");
    private int mTextBgColor = Color.parseColor("#556A73");

    private int mPointColor = Color.parseColor("#CC6500");
    /**
     * 渐变色
     */
    private int[] mShaderColors;

    private DecimalFormat mFormat;

    /**
     * 选中 柱子 的模式
     */
    private int selectedModed = SelectedMode.selectedActivated;
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
    private List<NExcel> mExcels = new ArrayList<>();
    /**
     * 柱形图 宽
     */
    private float mBarWidth = 0;
    /**
     * 横坐标 文字 大小
     */
    private float mAbscissaMsgSize;

    private int mFixedNums;//参照的 固定柱状图

    /**
     * 滑动 距离
     */
    private float mSliding = 0;

    //---------动画--------
    private float ratio = 1;
    private long animateTime = 1600;
    private ValueAnimator mVa;
    /**
     * 画横线
     */
    private boolean mCrosses;

    //-------------动画风格
    private Interpolator mInterpolator = new DecelerateInterpolator();//先加速 后减速
    private PathEffect pathEffect = null;
    private float phase = 0;//


    private Rect mBounds;
    private boolean mNeedLineEffict = false;
    /**
     * 折线 圆点 半径
     */
    private float mLinePointRadio = 0;
    private int mTextColor = Color.parseColor("#556A73");
    //    new AccelerateInterpolator() 先减速 后加速
    //    new AnticipateInterpolator()
    //    new BounceInterpolator()
    //    new OvershootInterpolator()

    /**
     * 要画的 图表的 风格
     */
    //    private int mChartStyle = ChartStyle.BAR_LINE;
    private int mChartStyle = ChartStyle.BAR;
    private int mTextAniStyle = TextAniStyle.ANIDOWN;
    /**
     * 柱状图的 动画风格
     */
    private int mBarAniStyle = ChartAniStyle.BAR_DISPERSED;
    /**
     * 折线图的 动画风格
     */
    private int mLineAniStyle = ChartAniStyle.BAR_DOWN;

    private float mBarRadio = 0;

    public interface ChartStyle {
        static final int BAR = 1;
        static final int LINE = 2;
        static final int BAR_LINE = 4;
        static final int CLOSELINE = 5;
    }

    public interface SelectedMode {
        /**
         * 选中的 颜色变  显示所有柱子 文字
         */
        int selectedActivated = 0;
        /**
         * 选中的 显示 柱子 文字 其他不显示
         */
        int selecetdMsgShow = 1;
    }

    public interface TextAniStyle {
        static final int ANIUP = 0;
        static final int ANIDOWN = 1;
    }

    public interface ChartAniStyle {
        static final int BAR_UP = 0;
        static final int BAR_RIGHT = 1;
        static final int BAR_DOWN = 2;
        /**
         * 柱形条 由某个往外扩散
         */
        static final int BAR_DISPERSED = 5;
        static final int LINE_RIPPLE = 3;
        static final int LINE_CONNECT = 4;
    }


    public NChart(Context context) {
        super(context);
        init(context);
    }

    public NChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            //            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MPieView, defStyleAttr, 0);
            //            pieInterWidth = (int)typedArray.getDimension(R.styleable.MPieView_pieInterColor, 0);
            //            backColor = typedArray.getColor(R.styleable.MPieView_piebackground, Color.WHITE);
            //            specialAngle = typedArray.getInt(R.styleable.MPieView_specialAngle, 0);
            //            PieSelector = typedArray.getBoolean(R.styleable.MPieView_PieSelector, true);
            //            typedArray.recycle();
        }
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

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(2);
        mLinePaint.setStyle(Paint.Style.STROKE);//画线的时候 必须把 画笔的style设置为 Paint.Style.STROKE
        mFormat = new DecimalFormat("##.##");

        mTextBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextBgPaint.setColor(mTextBgColor);

        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAbscissaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAbscissaPaint.setTextSize(mAbscissaMsgSize);
        mAbscissaPaint.setColor(mAbscissaMsgColor);
        mAbscissaPaint.setTextAlign(Paint.Align.CENTER);//画笔文字居中

        //画闭合 曲线区域
        mLinePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint2.setPathEffect(new CornerPathEffect(25));//圆角
        mLinePaint2.setShader(new LinearGradient(0, 20, 0, mHeight, Color.RED, Color.GREEN, Shader.TileMode.CLAMP));
        //拿不到  height的值

    }

    /**
     * 初始化 一些默认数据
     */
    private void initData() {
        mBarWidth = dip2px(36);
        mInterval = dip2px(20);
        mTextMarging = dip2px(4);
        mTextSize = sp2px(15);
        mAbscissaMsgSize = sp2px(15);
//        mLinePointRadio = dip2px(4);
        mAbove = dip2px(5);
        mBounds = new Rect();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mHCoordinate = mHeight - Math.abs(2 * mAbscissaMsgSize);
        调整柱子宽度(w);

//        //以最高的柱子为准 确定缩放率
//        mHeightRatio = (mHCoordinate - 2 * mTextSize - mAbove - mTextMarging - mLinePointRadio - 5) / mSugHeightest;
        if (mExcels.size() > 0) {
            高度缩放();
        }
        mWidth = w;
        animateExcels();

    }

    private void 调整柱子宽度(int w) {
        //如果不可滚动的话 将柱子全部显示在屏幕内 平均分屏幕宽度
        if (!mScrollAble) {
            if (mFixedNums != 0 && mChartStyle == ChartStyle.BAR) {
//                mBarWidth = (w - 2 * (mFixedNums - 1)) / mFixedNums;
//                mInterval = (w - mBarWidth * mExcels.size()) / (mExcels.size() - 1);
                mBarWidth = (w - 2 * (mFixedNums + 1)) / mFixedNums;
                mInterval = (w - mBarWidth * mExcels.size()) / (mExcels.size() + 1);
            } else {
                //不可滚动 没有参照数量 柱子平均分屏幕宽 最小间隔2
                mInterval = 2;
                mBarWidth = (w - mInterval * (mExcels.size() + 1)) / mExcels.size();
            }
        }
    }

    private void 高度缩放() {
        if (mHCoordinate > 0) {
            //以最高的柱子为准 确定缩放率
            mHeightRatio = (mHCoordinate - 2 * mTextSize - mAbove - mTextMarging - mLinePointRadio - 5) / mSugHeightest;
            for (int i = 0; i < mExcels.size(); i++) {
                NExcel nExcel = mExcels.get(i);
                nExcel.setHeight(nExcel.getHeight() * mHeightRatio);
                nExcel.setWidth(mBarWidth);
                PointF start = nExcel.getStart();
                start.x = mInterval * (i + 1) + mBarWidth * i;
                start.y = mHCoordinate - mAbove - nExcel.getLower();
                //nExcel.setColor(mNormalColor);
            }
        }
    }

    private void refreshExcels() {
        if (mHCoordinate > 0) {
            for (int i = 0; i < mExcels.size(); i++) {
                NExcel nExcel = mExcels.get(i);
                nExcel.setWidth(mBarWidth);
                PointF start = nExcel.getStart();
                start.x = mInterval * (i + 1) + mBarWidth * i;
                start.y = mHCoordinate - mAbove - nExcel.getLower();
                //nExcel.setColor(mNormalColor);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawSugExcel_BAR(canvas);
//        drawSugExcel_CloseLINE(canvas);
//        drawCoordinateAxes(canvas);
        if (mChartStyle == ChartStyle.BAR) {
            drawSugExcel_BAR(canvas);
        } else if (mChartStyle == ChartStyle.LINE) {
            drawSugExcel_LINE(canvas);
        } else if (mChartStyle == ChartStyle.CLOSELINE) {
            drawSugExcel_CloseLINE(canvas);
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
            NExcel excel = mExcels.get(i);
            PointF start = excel.getStart();

            if (selectedModed == SelectedMode.selectedActivated) {
                //选中模式 一
                if (null == mShaderColors) {
                    //画笔 没有渐变色的时候
                    if (i != mSelected) {
                        mExecelPaint.setColor(mNormalColor);
                    } else {
                        mExecelPaint.setColor(mActivationColor);
                    }
                } else {
                    //画笔 有渐变色的时候
                    if (i != mSelected) {
                        mExecelPaint.setShader(new LinearGradient(start.x, start.y - excel.getHeight(), start.x, start.y, mShaderColors[0], mShaderColors[1], Shader
                                .TileMode.CLAMP));
                    } else {
                        mExecelPaint.setShader(new LinearGradient(start.x, start.y - excel.getHeight(), start.x, start.y, mActivationColor, mShaderColors[1], Shader.TileMode.CLAMP));
                    }
                }
                drawSugExcel_text(canvas, excel, i, true);
            } else {
                //选中模式 二
                if (null != mShaderColors) {
                    mExecelPaint.setShader(new LinearGradient(start.x, start.y - excel.getHeight
                            (), start.x, start.y, mShaderColors[0], mShaderColors[1], Shader
                            .TileMode
                            .CLAMP));
                } else {
                    mExecelPaint.setColor(mNormalColor);
                }
            }
            //      canvas.drawRect(excel.getRectF(), mExecelPaint);
            //      canvas.drawRect(excel.getRectF().left, excel.getRectF().top * ratio, excel.getRectF().right,
            //          excel.getRectF().bottom, mExecelPaint);

            //画柱子 + 动画
            if (mBarAniStyle == ChartAniStyle.BAR_UP) {
                //下往上 增长
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawRoundRect(excel.getRectF().left, excel.getRectF().bottom - (excel.getHeight()) * ratio,
                            excel.getRectF().right, excel.getRectF().bottom, mBarRadio, mBarRadio, mExecelPaint);
                } else {
                    canvas.drawRect(excel.getRectF().left, excel.getRectF().bottom - (excel.getHeight()) * ratio,
                            excel.getRectF().right, excel.getRectF().bottom, mExecelPaint);
                }
            } else if (mBarAniStyle == ChartAniStyle.BAR_RIGHT) {
                //左往右 增大
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawRoundRect(excel.getRectF().left, excel.getRectF().top,
                            excel.getRectF().left + (excel.getWidth()) * ratio, excel.getRectF().bottom, mBarRadio, mBarRadio, mExecelPaint);
                } else {
                    canvas.drawRect(excel.getRectF().left, excel.getRectF().top,
                            excel.getRectF().left + (excel.getWidth()) * ratio, excel.getRectF().bottom, mExecelPaint);
                }

                //                canvas.drawRect(excel.getRectF().left, excel.getRectF().top, excel.getRectF().right*ratio,
                //                        excel.getRectF().bottom, mExecelPaint);
            } else if (mBarAniStyle == ChartAniStyle.BAR_DOWN) {
                //上往下 增长
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawRoundRect(excel.getRectF().left, excel.getRectF().top, excel.getRectF().right,
                            excel.getRectF().top + (excel.getHeight()) * ratio, mBarRadio, mBarRadio, mExecelPaint);
                } else {
                    canvas.drawRect(excel.getRectF().left, excel.getRectF().top, excel.getRectF().right,
                            excel.getRectF().top + (excel.getHeight()) * ratio, mExecelPaint);
                }

            } else {
                if (mBarStanded >= mExcels.size()) {
                    mBarStanded = mExcels.size() - 1;
                }
                NExcel sExcel = mExcels.get(mBarStanded);
                //以mBarStanded为准 左右散开
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawRoundRect(sExcel.getRectF().left + (excel.getRectF().left - sExcel.getRectF().left) * ratio, excel.getRectF().top,
                            sExcel.getRectF().right + (excel.getRectF().right - sExcel.getRectF().right) * ratio,
                            excel.getRectF().bottom, mBarRadio, mBarRadio, mExecelPaint);
                } else {
                    canvas.drawRect(sExcel.getRectF().left + (excel.getRectF().left - sExcel.getRectF().left) * ratio, excel.getRectF().top,
                            sExcel.getRectF().right + (excel.getRectF().right - sExcel.getRectF().right) * ratio,
                            excel.getRectF().bottom, mExecelPaint);
                }

            }
            //柱状图横坐标 信息
            drawAbscissaMsg(canvas, excel);
        }
        if (selectedModed == SelectedMode.selecetdMsgShow && mSelected != -1 && ratio == 1) {
            //选中模式 二
            选中的文字(canvas);
        }
    }


    /**
     * 画选择的 柱状图 数据
     */
    private void 选中的文字(Canvas canvas) {

        mTextPaint.setColor(Color.WHITE);
        NExcel excel = mExcels.get(mSelected);
        PointF midPointF = excel.getMidPointF();
        String msg = mFormat.format(excel.getUpper()) + excel.getUnit();
        mTextPaint.getTextBounds(msg, 0, msg.length(), mBounds);

        //画文字背景
        Path textBg = new Path();

        float bgHeight = dip2px(6);
        float bgWidth = dip2px(8);

        float textMarging = dip2px(3);//文字背景 三角尖处 和 柱子的距离
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
        RectF rectF = new RectF(midPointF.x - mBounds.width() / 2 - bgWidth, midPointF.y - textMarging - bgHeight - mBounds.height() - bgHeight * 2
                , midPointF.x + mBounds.width() / 2 + bgWidth, midPointF.y - textMarging - bgHeight);
        canvas.drawRoundRect(rectF, 3, 3, mTextBgPaint);

        //画文字
        canvas.drawText(msg, midPointF.x, midPointF.y - textMarging - bgHeight * 2, mTextPaint);
    }

    /**
     * @param above 上面 true
     */
    private void drawSugExcel_text(Canvas canvas, NExcel excel, int i, boolean above) {
        PointF midPointF = excel.getMidPointF();
        //SelectedMode.selectedActivated 模式下才走这方法
        if (mSelected == i) {
            mTextPaint.setColor(mActivationColor);
        } else {
            mTextPaint.setColor(mTextColor);
        }

        float textMarging = mTextMarging;
        if (mChartStyle == SugChart.ChartStyle.LINE) {
            textMarging = mTextMarging + mLinePointRadio;
        }

        //        if(excel.getHeight()%2 == 0) {
        //        if(i%2 == 0) {
        图表上下文字(canvas, mFormat.format(excel.getUpper()) + excel.getUnit(), midPointF.x, (above ? (midPointF.y - textMarging) : (midPointF.y + mTextSize + mLinePointRadio)) * ratio, (above ? (excel.getRectF().bottom - (excel.getHeight() + textMarging) * ratio) : (excel
                .getRectF().bottom - (excel.getHeight() - mTextSize - mLinePointRadio) * ratio)));

        //柱子 底部文字
        if (excel.getLower() > 0 && mChartStyle == ChartStyle.BAR) {
            图表上下文字(canvas, mFormat.format(excel.getLower()) + excel.getUnit(), midPointF.x, (excel.getStart().y + textMarging + mTextSize) * ratio, excel.getStart().y + excel.getLower() - (excel.getLower() - mTextSize) * ratio);
        }
    }

    private void 图表上下文字(Canvas canvas, String text, float x, float y, float y2) {
        if (mTextAniStyle == TextAniStyle.ANIDOWN) {
            //            向下飘
            canvas.drawText(text, x,
                    y, mTextPaint);
        } else {
            //文字向上飘
            canvas.drawText(text, x,
                    y2, mTextPaint);
        }
    }


    /**
     * 画 折线
     */
    private void drawSugExcel_LINE(Canvas canvas) {
        mLinePaint.setColor(mNormalColor);
        if (mCrosses) {
            //先画 横线
            PointF lineStart = mExcels.get(0).getMidPointF();
            PointF lineEnd = mExcels.get(mExcels.size() - 1).getMidPointF();
            canvas.drawLine(lineStart.x, mHeight / 2f, lineStart.x + (lineEnd.x - lineStart.x) * ratio, mHeight / 2f,
                    mLinePaint);
        } else {
            pathLine.reset();
            画折线(canvas);

            if (null != mShaderColors && ratio == 1) {
                //画折线下的渐变色
                画折线下的渐变色(canvas);
            }

            if (mLinePointRadio > 0) {
                //画 折线的圆点
                画折线的圆点(canvas);
            }
            //画选中的 item 的文字信息
            if (selectedModed == SelectedMode.selecetdMsgShow && mSelected != -1 && ratio == 1) {
                选中的文字(canvas);
            }
        }
    }

    private void 画折线(Canvas canvas) {
        for (int i = 0; i < mExcels.size(); i++) {
            NExcel excel = mExcels.get(i);
            //                PointF start = excel.getStart();
            //                if(mChartStyle == ChartStyle.LINE) {
            //                    start.x += mSliding;//当 柱状和折线都画的时候 只要其中一个改变起点横坐标即可
            //                }
            PointF midPointF = excel.getMidPointF();
//            canvas.drawPoint(midPointF.x, midPointF.y, mLinePaint);
            if (i == 0) {
                //        pathLine.moveTo(midPointF.x, mHeight/2f);
                pathLine.moveTo(midPointF.x, mHeight / 2f + (midPointF.y - mHeight / 2f) * ratio);
                //                            pathLine.moveTo(midPointF.x, midPointF.y);
            } else {
                pathLine.lineTo(midPointF.x, mHeight / 2f + (midPointF.y - mHeight / 2f) * ratio);
                //                    pathLine.quadTo(mExcels.get(i-1).getMidPointF().x, mExcels.get(i-1).getMidPointF().y, midPointF.x,
                //                            midPointF.y);
            }

            if (selectedModed == SelectedMode.selectedActivated) {
                //选中模式 一
                //画 文字
                if (i < mExcels.size() - 1) {
                    drawSugExcel_text(canvas, excel, i, excel.getMidPointF().y < mExcels.get(i + 1).getMidPointF().y);
                } else {
                    drawSugExcel_text(canvas, excel, i, true);
                }
            }
            //柱状图横坐标 信息
            drawAbscissaMsg(canvas, excel);
        }
        if (mNeedLineEffict) {
            pathEffict();
        }
        canvas.drawPath(pathLine, mLinePaint);
    }

    private void 画折线下的渐变色(Canvas canvas) {
        pathLine.lineTo(mExcels.get(mExcels.size() - 1).getMidX(), mHCoordinate);
        pathLine.lineTo(mExcels.get(0).getMidX(), mHCoordinate);
        pathLine.close();
        mExecelPaint.setShader(new LinearGradient(0, 0, 0, mHCoordinate, mShaderColors[0], mShaderColors[1], Shader.TileMode.CLAMP));
        canvas.drawPath(pathLine, mExecelPaint);
    }

    private void 画折线的圆点(Canvas canvas) {
        for (int i = 0; i < mExcels.size(); i++) {
            NExcel excel = mExcels.get(i);
            PointF midPointF = excel.getMidPointF();
            if (selectedModed == SelectedMode.selectedActivated) {
                if (i == mSelected) {
                    mPointPaint.setColor(mActivationColor);
                } else {
                    mPointPaint.setColor(mNormalColor);
                }
            } else {
                mPointPaint.setColor(mPointColor);
            }
            canvas.drawCircle(midPointF.x, midPointF.y, mLinePointRadio, mPointPaint);
        }
    }

    /**
     * 画 折线
     */
    private void drawSugExcel_CloseLINE(Canvas canvas) {
        mLinePaint.setColor(Color.RED);
        mCrosses = false;
        pathLine.reset();
        pathLine.moveTo(mExcels.get(0).getMidPointF().x, mHCoordinate);
        for (int i = 0; i < mExcels.size(); i++) {
            NExcel excel = mExcels.get(i);
            PointF midPointF = excel.getMidPointF();
            canvas.drawPoint(midPointF.x, midPointF.y, mLinePaint);
            pathLine.lineTo(midPointF.x, mHeight / 2f + (midPointF.y - mHeight / 2f) * ratio);
            //画 文字
            if (i < mExcels.size() - 1) {
                drawSugExcel_text(canvas, excel, i, true);
            } else {
                drawSugExcel_text(canvas, excel, i, true);
            }
            //柱状图横坐标 信息
            drawAbscissaMsg(canvas, excel);
        }
        pathLine.lineTo(mExcels.get(mExcels.size() - 1).getMidPointF().x, mHCoordinate);
        canvas.drawPath(pathLine, mLinePaint2);
    }

    /**
     * 画横坐标 信息
     */
    private void drawAbscissaMsg(Canvas canvas, NExcel excel) {
        mAbscissaPaint.setColor(mAbscissaMsgColor);
        PointF midPointF = excel.getMidPointF();
        //柱状图横坐标 信息
        canvas.drawText(excel.getXmsg(), midPointF.x, mHCoordinate + mTextMarging + mTextSize, mAbscissaPaint);
    }

    private void pathEffict() {
        //  PathEffect子类
        //        ComposePathEffect
        //        CornerPathEffect  圆角
        //        DashPathEffect(float[], phase)  虚线  数组指定虚线(线段长，间隔) 相应位置比 长度 phase 虚线偏移 变化则产生虚线移动动画
        //        DiscretePathEffect 离散
        //        PathDashPathEffect(path,advance,phase,style)path成的形状 advance间隔 phase 虚线偏移
        //        SumPathEffect

        //        pathEffect = new CornerPathEffect(25); //圆角
        //                pathEffect = new DashPathEffect(new float[]{10, 8, 5, 10}, phase); //指定 线段长度，间隔长度
        // (数组中表示线段，间隔，线段，间隔，线段，间隔)
        //        pathEffect = new DiscretePathEffect(3f,6f); //
        //        Path p = new Path();
        //        p.addRect(0, 0, 6, 6, Path.Direction.CCW);
        //        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //            p.addArc(0, 0, 6, 6,0,180);
        //        }
        //        pathEffect = new PathDashPathEffect(p,18,phase,PathDashPathEffect.Style.ROTATE); //圆角
        pathEffect = new DashPathEffect(new float[]{10, 8, 5, 10}, phase); //指定 线段长度，间隔长度
        phase = ++phase % 50;//不断变化 出现 虚线移动效果

        mLinePaint.setPathEffect(pathEffect);
        invalidate();
    }

    /**
     * 画 坐标轴
     */
    private void drawCoordinateAxes(Canvas canvas) {
        mExecelPaint.setColor(Color.BLACK);
        canvas.drawLine(0, mHCoordinate, mWidth, mHCoordinate, mExecelPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (ratio == 1) {//执行动画的时候 不可滑动
                    float moveX = event.getX();
                    mSliding = moveX - mDownX;
                    if (Math.abs(mSliding) > mTouchSlop) {
                        //          pathLine.reset();
                        moved = true;
                        mDownX = moveX;
                        if (mExcels.get(0).getStart().x + mSliding > mInterval || mExcels.get(mExcels.size() - 1)
                                .getStart().x + mBarWidth + mInterval + mSliding < mWidth) {
                            return true;
                        }
                        for (int i = 0; i < mExcels.size(); i++) {
                            NExcel excel = mExcels.get(i);
                            PointF start = excel.getStart();
                            start.x += mSliding;//图表左右移动
                        }
                        if (mScrollAble) {
                            invalidate();
                        }
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

        }
        return true;
    }

    /**
     * 判断 点中哪个柱状图
     */
    private int clickWhere(PointF tup) {
        for (int i = 0; i < mExcels.size(); i++) {
            NExcel excel = mExcels.get(i);
            PointF start = excel.getStart();
            if (start.x > tup.x) {
                return -1;
            } else if (start.x <= tup.x) {
                if (start.x + excel.getWidth() > tup.x && (start.y > tup.y && start.y - excel.getHeight() < tup.y)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 二分法 判断 点中哪个柱状图
     */
    private int clickWhere2(PointF tup) {
        int high = mExcels.size() - 1;
        int low = 0;
        int midle = mExcels.size() / 2;
        while (low < high) {
            midle = (low + high) / 2;
            if (tup.x > mExcels.get(midle).getStart().x && tup.x < mExcels.get(midle).getStart().x + mBarWidth) {
                NExcel nExcel = mExcels.get(midle);
                if (tup.y < nExcel.getStart().y && tup.y > nExcel.getStart().y - nExcel.getHeight()) {
                    return midle;
                }
            } else if (tup.x > mExcels.get(midle).getStart().x) {
                low = midle + 1;
            } else {
                high = midle - 1;
            }
        }
        return -1;
    }

    /**
     * 传入 数据
     */
    public void cmdFill(NExcel... nExcels) {
        cmdFill(Arrays.asList(nExcels));
    }

    /**
     * 传入 数据
     */
    public void cmdFill(List<NExcel> nExcelList) {
        mExcels.clear();
        for (NExcel nExcel : nExcelList) {
            mSugHeightest = mSugHeightest > nExcel.getHeight() ? mSugHeightest : nExcel.getHeight();
        }

        for (int i = 0; i < nExcelList.size(); i++) {
            NExcel nExcel = nExcelList.get(i);
            nExcel.setWidth(mBarWidth);
            PointF start = nExcel.getStart();
            start.x = mInterval * (i + 1) + mBarWidth * i;
            nExcel.setColor(mNormalColor);
            mExcels.add(nExcel);

            PointF midPointF = nExcel.getMidPointF();
            if (i == 0) {
                pathLine.moveTo(midPointF.x, midPointF.y);
            } else {
                pathLine.lineTo(midPointF.x, midPointF.y);
            }
        }
        if (mWidth != 0) {
            调整柱子宽度(mWidth);
            高度缩放();
            postInvalidate();
        }
    }


    private void animateExcels() {
        if (mVa == null) {
            mVa = ValueAnimator.ofFloat(0, 1).setDuration(animateTime);
            mVa.addUpdateListener(this);
            mVa.setInterpolator(mInterpolator);
            mVa.addListener(this);
        }
        mVa.start();
    }


    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        ratio = (float) animation.getAnimatedValue();
        postInvalidate();
    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {
        if (ChartStyle.LINE == mChartStyle) {
            if (mCrosses) {
                mCrosses = false;
                mVa.start();
            }
        }
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }

    public float getInterval() {
        return mInterval;
    }

    /**
     * 设置 两柱状图之间的间隔
     */
    public void setInterval(float interval) {
        this.mInterval = interval;
        refreshExcels();
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
        mCrosses = (mChartStyle == ChartStyle.LINE);
        mVa.cancel();
        mVa.start();
        if (mChartStyle == ChartStyle.LINE) {
            mTextMarging += mLinePointRadio / 2;
        }
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
        refreshExcels();
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

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
        postInvalidate();
    }

    public float getAbove() {
        return mAbove;
    }

    /**
     * 设置 图表 距离x轴的距离
     *
     * @param above
     */
    public void setAbove(float above) {
        mAbove = above;
        refreshExcels();
    }

    public int getBarStanded() {
        return mBarStanded;
    }

    /**
     * 设置 柱形条 扩散 动画风格的 参照柱形条
     *
     * @param barStanded
     */
    public void setBarStanded(int barStanded) {
        mBarStanded = barStanded;
    }

    public int getTextAniStyle() {
        return mTextAniStyle;
    }

    /**
     * 设置文字的 出现动画 上飘 或者 下飘
     *
     * @param textAniStyle TextAniStyle
     */
    public void setTextAniStyle(int textAniStyle) {
        mTextAniStyle = textAniStyle;
    }

    public int getBarAniStyle() {
        return mBarAniStyle;
    }

    /**
     * 设置 柱形条 的展现动画
     *
     * @param barAniStyle ChartAniStyle
     */
    public void setBarAniStyle(int barAniStyle) {
        mBarAniStyle = barAniStyle;
        if (barAniStyle == ChartAniStyle.BAR_DISPERSED) {
            mInterpolator = new DecelerateInterpolator();//先加速 后减速
        } else if (barAniStyle == ChartAniStyle.BAR_DISPERSED) {
            mInterpolator = new AccelerateInterpolator();//先 减速 后加速
        }
    }

    public int getLineAniStyle() {
        return mLineAniStyle;
    }

    /**
     * 设置 折线 的展现动画
     *
     * @param lineAniStyle ChartAniStyle
     */
    public void setLineAniStyle(int lineAniStyle) {
        mLineAniStyle = lineAniStyle;
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    /**
     * x轴 的位置
     *
     * @param HCoordinate 距离底部 多少
     */
    public void setHCoordinate(float HCoordinate) {
        mHCoordinate = mHeight - HCoordinate;
        refreshExcels();
    }

    public int dip2px(float dipValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public void animateShow() {
        mVa.cancel();
        if (mChartStyle == ChartStyle.LINE) {
            mCrosses = true;
        }
        mVa.start();
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

    /**
     * 设置 横坐标 信息文字 颜色
     *
     * @param abscissaMsgColor
     */
    public void setAbscissaMsgColor(int abscissaMsgColor) {
        mAbscissaMsgColor = abscissaMsgColor;
        mAbscissaPaint.setColor(mAbscissaMsgColor);
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

    public void setSelectedModed(int selectedModed) {
        this.selectedModed = selectedModed;
        postInvalidate();
    }

    public int getSelectedModed() {
        return selectedModed;
    }

    public float getLinePointRadio() {
        return mLinePointRadio;
    }

    /**
     * 设置 柱子的圆角 半径
     * @param barRadio
     */
    public void setBarRadio(float barRadio) {
        mBarRadio = barRadio;
    }

    /**
     * 设置 折线中 折点 圆的半径
     *
     * @param linePointRadio
     */
    public void setLinePointRadio(float linePointRadio) {
        mLinePointRadio = linePointRadio;
    }

    public void setNeedLineEffict(boolean needLineEffict) {
        mNeedLineEffict = needLineEffict;
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
