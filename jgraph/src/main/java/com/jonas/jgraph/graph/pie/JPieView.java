package com.jonas.jgraph.graph.pie;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import com.jonas.jgraph.R;
import com.jonas.jgraph.models.Apiece;
import com.jonas.jgraph.utils.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 在控件显示前 设置变量 代码执行顺序（构函---设置变量的方法(set)----onSizeChange-----onDraw）
 * 在空间显示之后  设置变量值 那么代码值执行设置变量的那个方法
 * 控件的onSizeChange方法只执行一次  构函(一次) （是否有设置变量(一次)）onSizeChange(一次)之后执行onDraw(多次)
 * 当onSizeChange执行完之后  在（控件的非onDraw方法中）可以认为该控件已显示
 * Created by jiangzuyun on 2015/8/31.
 * 无论控件是否是正方形 都在中间部分画图
 * 提示线 分为 斜线部分和 横线部分 背景是透明的
 * showLine 是否显示 提示线的总开关（默认true 默认显示）
 * TstartAtTouch 设置提示线的起点(true表示从点击的地方开始画提示线(此时nRadius设置效果失效) 默认false)
 * nRadius 设置 提示线的起点(TstartAtTouch为true时无效)(1:表示从外辅助圆(饼图)的一半开始，0表示从圆心开始)原理：该值其实是内辅助圆的半径即提示线的起点
 * AniLine 设置是否展示提示线的显示动画（默认为true显示）
 * ANIDURINGTIME 设置 提示线显示动画的时间
 * movingShowTX 旋转的时候是否显示 提示线(默认为false，当为true的时候AniLine将失效，失去提示线的显示动画)
 * TshLong 设定提示线横线部分的长度 默认一直画到控件两边
 * cleanWire 清除所有的提示线条（外部调用需要调用postInvalidate刷新）
 * lpading 设置外辅助圆的大小 (该值可动态调整饼图的大小)（该值是外圆与控件周边的距离(默认控件较短边的1/4)，其与外圆半径成反比，越小则提示线的转折点越靠外，不建议修改 一般该值要比提示线上文字的长度的最长的那个大一点）
 * TsWidth 提示线的宽度
 * TsColor 提示线的颜色 默认黑色
 * 饼图（在控件的中心部分显示 只点击饼图内部有效）
 * PieSelector 设置是否显示 点击效果(默认true 显示 当其为true 那么特殊角的突出效果就失效)
 * specialAngle 设置特殊角度 当饼图的某个扇形包含该特殊角度的时候 就突出显示（当值为0的时候无效 同时当PieSelector为true的时候无效）
 * pieRotateable 设置当前饼图是否支持旋转 默认false 不可旋转
 * setOnItemPieClickListener 饼图点击监听器  点中了哪个扇形
 * setOnRotatingPieListener 旋转监听器 获取旋转角度
 * setOnSpecialPieListener  特殊角度监听器 获取特殊角度所在的扇形位置
 * pieInterWidth 饼图间隔线的宽度
 * backColor 饼图背景色 布局中设置的背景色无效（ondraw无法拿到布局中的背景色？？）默认白色
 * interColor 饼图间隔线的颜色 默认白色
 * getPieRadius 获取饼图半径（饼图的半径可以通过lpading 设置）
 * getClickPosition 可以获取点击的位置
 * getSpecialPosition  获取特殊角度对于的扇形位置
 * pointPieOut 设置突出扇形的 突出大小 setPointPieOut提供set方法设置  内部有处理 该值过大的情况（一般不修改）
 * TsOut 设置提示线突出饼图的部分长度（一般不修改）最好先于setPointPieOut调用
 * 2015/8/31.
 */
public class JPieView extends WarmLine implements Animator.AnimatorListener {
    /**
     * 点中pie的效果  默认true 开启效果
     */
    private boolean PieSelector = true;
    private Paint mPaint;
    private Paint paintInter;
    private int width;
    private int height;
    /**
     * 控件的背景色
     */
    private int backColor = Color.WHITE;
    /**
     * 饼图 间隔线的景色
     */
    public int pieInterColor = Color.WHITE;
    /**
     * 扇形之间的间隔宽度
     */
    public int pieInterWidth = 0;
    /**
     * 饼图数据
     */
    private List<Apiece> pieData = new ArrayList<Apiece>();
    /**
     * 所有数据(数字)总和
     */
    private int totalPieNum;
    /**
     * 旋转的角度
     */
    private float degrees;

    private float centX;
    private float centY;
    private float down_x;
    private float down_y;
    private ViewGroup[] clashView;
    /**
     * 点击中的 那个扇形的 位置
     */
    private int clickPosition = Integer.MAX_VALUE;
    /**
     * 起开关作用 当旋转之后不会触发点击 获取被点击扇形位置
     * move执行了 就变为true
     */
    private boolean rotate = false;
    /**
     * 特殊角度 位于此角度的扇形变大
     */
    public float specialAngle = 0;

    /**
     * 制定的某个 扇形 突出的大小 默认13 必须小于 提示线多出饼图的大小TsOut 如果超过则 设置为默认13
     */
    private float pointPieOut;

    /**
     * 当前饼图是否可旋转 默认不可
     */
    private boolean pieRotateable = true;
    private float sweep4 = 360;
    private float start4;
    /**
     * 饼图点击 监听器
     */
    private OnItemPieClickListener itemclicklistener;
    /**
     * 特殊角度 对应位置 监听器
     */
    private OnSpecialPieListener specialPielistener;
    /**
     * 旋转 监听器
     */
    private OnRotatingPieListener rotatingPielistener;
    /**
     * 较小的边
     */
    private float just;
    /**
     * 记录事件动作
     */
    private String action;

    /**
     * 饼图的 半径
     */
    private float pieRadius;
    /**
     * 特殊角度 所对的扇形位置
     */
    private int specialPosition;
    /**
     * down到up之间旋转了多少度
     */
    private float eachDegrees;
    /**
     * 是否允许 滚动之后 出发选中事件 （点击事件）默认false
     * 滚动后不触发点击扇形事件
     */
    public boolean selectAfterMove;
    /**
     * 用于控制 当饼图展开动画执行完后 去掉那个执行动画的 扇形
     */
    protected boolean hideAniCircle;
    /**
     * 扇形从内往外 变大 的速度
     */
    private float fillouting;
    /**
     * 饼图 展现的 动画时间
     */
    private long PIEANITIME = 3000;
    /**
     * 内往外扩展所用的 矩阵
     */
    private RectF fillOutRectF;
    /**
     * 画扇形用的矩阵
     */
    private RectF arcRectF;
    /**
     * 突出扇形的矩阵
     */
    private RectF outRectF;
    /**
     * 饼图 展现动画的 加速器
     */
    private TimeInterpolator pieInterpolator = new BounceInterpolator();
    /**
     * 设置 是否展示  饼图展现动画
     * 默认为true  默认：饼图有展现动画
     */
    private boolean showPieAnimation = true;

    public PieShowAnimation pieshowani = PieShowAnimation.FILLOUTING;
    /**
     * 较长的边
     */
    private int maxb;
    private Paint titleP;
    private float showProgress = 1;
    private boolean outMoving;
    private ValueAnimator mAnimator = new ValueAnimator();

    @Override
    public void onAnimationStart(Animator animation){
        showLine = false;
    }

    @Override
    public void onAnimationEnd(Animator animation){
        showLine = showLineTemp;
    }

    @Override
    public void onAnimationCancel(Animator animation){
    }

    @Override
    public void onAnimationRepeat(Animator animation){

    }

    /**
     * 饼图的展现动画动画  默认 扩展动画
     *
     * @author Refuse
     */
    public enum PieShowAnimation {
        /**
         * fillOut为true 内往外扩展动画
         */
        FILLOUTING,
        /**
         * 扫描动画
         */
        SCANNING,
        /**
         * 每个扇形慢慢变大
         */
        GROWING,
        /**
         * 无展现动画
         */
        NONE
    }

    public JPieView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        if(attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MPieView, defStyleAttr, 0);
            pieInterColor = typedArray.getColor(R.styleable.MPieView_pieInterColor, Color.WHITE);
            pieInterWidth = (int)typedArray.getDimension(R.styleable.MPieView_pieInterColor, 0);
            pointPieOut = typedArray.getDimension(R.styleable.MPieView_pointPieOut, 0);
            backColor = typedArray.getColor(R.styleable.MPieView_piebackground, Color.WHITE);
            specialAngle = typedArray.getInt(R.styleable.MPieView_specialAngle, 0);
            outMoving = typedArray.getBoolean(R.styleable.MPieView_outMoving, false);
            PieSelector = typedArray.getBoolean(R.styleable.MPieView_PieSelector, true);
            typedArray.recycle();
        }
        //        paintInter.setColor(pieInterColor); 出错 why？？
        init();
    }

    public JPieView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public JPieView(Context context){
        this(context, null);
    }

    public void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titleP = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintInter = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 初始化一些画笔设置 可以外部改变的变量的初始化不要放在构造函数中
        // 设置间隔画笔风格
        paintInter.setStyle(Paint.Style.STROKE);
        paintInter.setColor(pieInterColor);
        // 画笔的宽度怎么画 与画笔宽度为0 相比 就是在画笔宽度为0的基础上左右各加粗宽度的一半
        paintInter.setStrokeWidth(pieInterWidth);
    }

    /**
     * 只有在 控件大小被改变的时候 才会被调用
     * 此时 获取控件的 宽高
     * onSizeChanged 在ondraw之前调用 一般 onSizeChanged调用之后可以认为 (在非onDraw中)控件显示了
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i("显示控件前设置变量代码执行顺序", "onSizeChanged(int w, int h, int oldw, int oldh)");

        width = getWidth();
        height = getHeight();
        just = width>height ? height : width;
        maxb = width<height ? height : width;
        centX = width*1f/2;
        centY = height*1f/2;

        // 防止 突出扇形突出过大
        pointPieOut = pointPieOut>TsOut ? TsOut/2 : pointPieOut == 0 ? TsOut/2 : pointPieOut;
        pieRadius = pieRadius == 0 ? wRadius-TsOut : pieRadius>wRadius-TsOut ? wRadius-TsOut : pieRadius;
        pieInterWidth = pieInterWidth>TsOut-pointPieOut ? (int)( TsOut-pointPieOut-10 ) : pieInterWidth;

        fillOutRectF = new RectF(centX-fillouting, centY-fillouting, centX+fillouting, centY+fillouting);
        arcRectF = new RectF(centX-pieRadius, centY-pieRadius, centX+pieRadius, centY+pieRadius);
        outRectF = new RectF(centX-pieRadius-pointPieOut, centY-pieRadius-pointPieOut, centX+pieRadius+pointPieOut,
                centY+pieRadius+pointPieOut);


        // 当背景为透明的时候 获取布局的背景色
        if(Integer.MAX_VALUE == backColor) {
            backColor = getBackColor();// ondraw也无法拿到布局中设置的背景色
            // backColor = Color.RED;
        }

        //==================================饼图的展现动画=从这里触发====外界无论设置啥变量最后都会走到这======================================
        //内往外 的展现动画 需要在此处出发，，因为这个动画 需要先获取到just
        if(pieData.size() != 0) {
            //=================设置完数据后 显示到界面==========================
            if(!showPieAnimation) {
                postInvalidate();
            }else {
                aniShowPie();
            }
        }
    }

    //动画需要的属性
    public float getShowProgress(){
        return showProgress;
    }

    //动画需要的属性
    public void setShowProgress(float showProgress){
        this.showProgress = showProgress;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas){

        // 无论控件 那边大 都在中间的正方形画饼图

        // 画背景
        mPaint.setColor(backColor);
        RectF back = new RectF(0, 0, width, height);
        canvas.drawRect(back, mPaint);

//        // 画标题
        //        float measureText = titleP.measureText(pieTiele);
        //        Rect bounds = new Rect();
        //        titleP.getTextBounds(pieTiele, 0, pieTiele.length(), bounds);
        //        canvas.drawText(pieTiele, width / 2 - measureText / 2, (height / 2) * 1f / 2 - bounds.height(), titleP);

        RectF oval;
        if(pieshowani == PieShowAnimation.FILLOUTING) {
            // 画扇形所需要的 矩形======扇形==从内往外 变大==所用=同时也是最终扇形所用的矩形============
            fillOutRectF.set(centX-fillouting, centY-fillouting, centX+fillouting, centY+fillouting);
            oval = fillOutRectF;
        }else {
            // =========无动画 画扇形所需要的 矩形===========
            oval = arcRectF;
        }

        // 画上辅助矩形
        // Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // paint.setStyle(Style.STROKE);
        // paint.setColor(Color.RED);
        // canvas.drawRect(oval, paint);

        // 将 提示线的起点 设定到 饼图半径的一半处
        //        if(nRadius == 1) {
        //            nRadius = pieRadius/2;
        //            Log.d("提示线起点", "将 提示线的起点 设定到 饼图半径的一半处:"+nRadius);
        //        }

        Apiece pie = null;
        for(int i = 0; i<pieData.size(); i++) {
            pie = pieData.get(i);
            mPaint.setColor(pie.getPieColor());

            // 把每个扇形的起止角度 限定在360之内 方便
            float startPie = pie.getStartAngle()+degrees;
            startPie = startPie>360 ? startPie-360 : startPie;
            startPie = startPie<0 ? startPie+360 : startPie;
            float end = startPie+pie.getSweepAngle();
            end = end>360 ? end-360 : end;
            end = end<0 ? end+360 : end;


            //当 up的时候 才画出 突出的选中扇形
            if(PieSelector && clickPosition == i && ( outMoving ? true : "up".equals(action) )) {
                // 提起时 选中的扇形变大
                canvas.drawArc(outRectF, startPie*showProgress, pie.getSweepAngle()*showProgress, true, mPaint);

                if(pieInterWidth != 0) {
                    canvas.drawArc(outRectF, startPie*showProgress, pie.getSweepAngle()*showProgress, true, paintInter);
                }

            }else {//没选中--(特殊角度？普通扇形)
                // = 画 =====特殊角度处===== 突出的扇形 ======当需要的时候new出新的RectF
                if(specialAngle>0 && ( ( end>specialAngle && startPie<specialAngle ) || ( ( end>specialAngle || startPie<specialAngle ) && ( end<startPie ) ) )) {
                    // 设置监听
                    if(specialPielistener != null) {
                        specialPielistener.onSpecialPie(i, pieData.get(i));
                    }
                    specialPosition = i;
                    PieSelector = false;
                    // 画突出扇形
                    canvas.drawArc(outRectF, startPie*showProgress, pie.getSweepAngle()*showProgress, true, mPaint);
                    if(pieInterWidth != 0) {
                        canvas.drawArc(outRectF, startPie*showProgress, pie.getSweepAngle()*showProgress, true, paintInter);
                    }
                }else {
                    // =============== 画 饼图的扇形=====================
                    canvas.drawArc(oval, startPie*showProgress, pie.getSweepAngle()*showProgress, true, mPaint);
                    if(pieInterWidth != 0) {
                        canvas.drawArc(oval, startPie*showProgress, pie.getSweepAngle()*showProgress, true, paintInter);
                    }
                }
            }
        }
        //        super.onDraw(canvas);//先画父类提示线条   //或者放最后 通过修改画提示线的颜色 来让提示线在展示动画之后出现
        //==================================饼图的展现动画===========================================
        if(pieshowani == PieShowAnimation.SCANNING && showPieAnimation) { // 不执行fillOut动画 同时 允许出现饼图展现动画
            // 则执行扇形扫描动画
            if(!hideAniCircle) {// 扇形动画完后会 变成条线 所以执行完 要去掉这个扇形
                // 画最顶层的 白色扇形 360度的 用于展现饼图慢慢展开的效果
                mPaint.setColor(backColor);
                // 画扇形所需要的 矩形
                RectF oval4 = new RectF(centX-just/2, centY-just/2, centX+just/2, centY+just/2);
                canvas.drawArc(oval4, start4, sweep4, true, mPaint);
            }
        }
        super.onDraw(canvas);// 通过修改画提示线的颜色 来让提示线在展示动画之后出现
    }

    //    public void drawSpecialPie(Canvas canvas, float mWidth, float mHeight, Apiece pie, float startPie, float outORin){
    //        RectF oval2 = new RectF(padings+mWidth/2-outORin, padings+mHeight/2-outORin, width-padings-mWidth/2+outORin,
    //                height-padings-mHeight/2+outORin);
    //        canvas.drawArc(oval2, pie.getStartAngle()+degrees, pie.getSweepAngle(), true, mPaint);
    //
    //        // 当饼图的间隔线宽度不为0 的时候 才画剑阁线
    //        if(pieInterWidth != 0) {
    //            // 画突出扇形的间隔
    //            RectF ovalInter2 = new RectF(padings-outORin-pieInterWidth/2+mWidth/2, padings-outORin-pieInterWidth/2+mHeight/2,
    //                    width-padings+pieInterWidth/2-mWidth/2+outORin, height-padings+pieInterWidth/2-mHeight/2+outORin);
    //            canvas.drawArc(ovalInter2, startPie, pie.getSweepAngle(), true, paintInter);
    //        }
    //    }

    // view 没有拦截事件 所以只能选择dispatchTouchEvent或者onTouchevent来处理时间
    // 但是 view默认是不可点击的 所以事件不会被传递到onTouchevent,所以view的事件处理建议写在dispatchTouchEvent内
    // 每次事件都会 重新调用该方法
    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        //请求 会产生事件冲突的外部ViewGroup不要拦截事件
        if(clashView != null && clashView.length>0) {
            for(int i = 0; i<clashView.length; i++) {
                clashView[i].requestDisallowInterceptTouchEvent(true);
            }
        }
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                action = "down";
                rotate = false;// 清除 旋转状态
                down_x = event.getX();
                down_y = event.getY();
                // 当 该点 不再饼图内的画 说明没点中饼图 （应该在 down处处理 后续就不需要处理）
                double c = Math.sqrt(( Math.pow(( down_x-centX ), 2) )+Math.pow(( down_y-centY ), 2));
                if(c>pieRadius) {
                    return false;// 父类的 down事件处理逻辑就不会执行 因为super在下面
                }

                // 根据 cos函数 获取当前up时的点 与水平右之间的角度 判断点击的是哪个扇形
                selectedPosition = clickPosition = checkClickWhere(down_x, down_y);
                if(PieSelector) {
                    //TODO 传值给父类
                    //                    invalidate();
                }

                // return true; // 当事件被消费之后 后续事件才会被该控件处理 原生view没处理 //父类已经
                // 返回true了这里不需要 否则会阻断父类的down 当父类是view的时候需要打开
                break;
            case MotionEvent.ACTION_MOVE:
                if(!pieRotateable) {
                    break;
                }
                float move_x = event.getX();
                float move_y = event.getY();

                // 当移动的距离超过0.001就算滑动
                if(Math.abs(move_x-down_x)>0.0001 || Math.abs(move_y-down_y)>0.0001) {
                    rotate = true;// 处于旋转状态
                    action = "move";
                }
                // rotate = true;// 处于旋转状态

                //TODO 给父类旋转角度的值    旋转饼图逻辑，返回旋转角度
                rotedAngle = rotatePie(move_x, move_y);

                // 当 该点 不再饼图内的画 说明没点中饼图 （应该在 down处处理 后续就不需要处理）
                double c3 = Math.sqrt(( Math.pow(( down_x-centX ), 2) )+Math.pow(( down_y-centY ), 2));
                if(c3>pieRadius) {
                    // TODO
                    cleanWire = true;// 清除 所you提示线条
                    return true;// 父类的 down事件处理逻辑就不会执行 因为super在下面
                }
                // clianWire = false;

                // return true; // 当事件被消费之后 后续事件才会被该控件处理 //父类已经 返回true了这里不需要
                // 当父类是view的时候需要打开
                // 否则会阻断父类的down
                break;
            case MotionEvent.ACTION_UP:
                eachDegrees = 0;//清0 下次旋转重新计算

                float up_x = event.getX();
                float up_y = event.getY();

                // 当 该点 不再饼图内的画 说明没点中饼图 （应该在 down处处理 后续就不需要处理）
                double c1 = Math.sqrt(( Math.pow(( up_x-centX ), 2) )+Math.pow(( up_y-centY ), 2));
                if(c1>pieRadius) {
                    return true;// 直接return
                }else {
                    action = "up";
                }

                //==============================move之后是否可执行之后操作==========================================
                if(!selectAfterMove && rotate) {// rotate在move的时候 设置为true ---- down的时候设置为false
                    // 如过up之前有move过的画 up的时候就不判断点击哪了
                    // move 之后 后续代码不执行 父类执行
                    break;
                }
                selectedPosition = clickPosition = checkClickWhere(up_x, up_y);// 会触发选中

                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 旋转 饼图
     *
     * @param move_x
     * @param move_y
     */
    private float rotatePie(float move_x, float move_y){

        // 方法2 move的时候获取角度 - move之前的角度 = 旋转的角度
        double moveAngle = getPointAngle(move_x, move_y);
        double downAngle = getPointAngle(down_x, down_y);
        degrees += (float)( moveAngle-downAngle );
        eachDegrees += (float)( moveAngle-downAngle );

        //限定角度 不超过 360 -360
        degrees = degrees>360 ? degrees-360 : degrees;
        degrees = degrees<-360 ? degrees+360 : degrees;
        eachDegrees = eachDegrees>360 ? eachDegrees-360 : eachDegrees;
        eachDegrees = eachDegrees<-360 ? eachDegrees+360 : eachDegrees;

        if(rotatingPielistener != null) {
            rotatingPielistener.onRotatingPie(eachDegrees, degrees);
        }
        down_x = move_x;
        down_y = move_y;
        postInvalidate();

        return degrees;
    }

    /**
     * 根据 cos函数 获取当前up时的点 与水平右之间的角度 判断点击的是哪个扇形
     *
     * @param up_y
     * @param up_x
     */
    private int checkClickWhere(float up_x, float up_y){
        double clickAngle = getPointAngle(up_x, up_y);
        // 根据 点击的角度 判断点中了那个扇形
        for(int i = 0; i<pieData.size(); i++) {
            Apiece pie = pieData.get(i);

            // 把每个扇形的起止角度 限定在360之内 方便
            float startPie = pie.getStartAngle()+degrees;
            startPie = startPie>360 ? startPie-360 : startPie;
            startPie = startPie<0 ? startPie+360 : startPie;
            float start = startPie;
            float end = startPie+pie.getSweepAngle();
            end = end>360 ? end-360 : end;
            end = end<0 ? end+360 : end;

            if(( end>clickAngle && start<clickAngle ) || ( end>clickAngle || start<clickAngle ) && ( end<start )) {
                // 只有在 up的时候才触发选中动作
                if("up".equals(action)) {
                    if(itemclicklistener != null) {
                        itemclicklistener.onPieItemClick(i, pieData.get(i));
                    }
                    // down的时候也有调用该方法
                    Logger.e("RotatablePie----当前点击位置", i+"====起点："+startPie+"====终点："+end+"===旋转角："+degrees);
                }
                return i;
            }
        }
        return Integer.MAX_VALUE;
    }

    /**
     * 获取当前点 与 水平右的夹角
     *
     * @param up_x
     * @param up_y
     * @return
     */
    public double getPointAngle(float up_x, float up_y){
        float a = up_x-centX;
        float b = up_y-centY;
        // 斜边
        double c = Math.sqrt(Math.pow(a, 2)+Math.pow(b, 2));
        // 获取 弧度
        double acos = Math.acos(a/c);
        // 获取 角度 角度=弧度/PI * 180
        double clickAngle = acos/Math.PI*180;// 注意 获取的只是0-180 还需要判断
        if(up_y>centY) {
            // 点击位于 下半圆
        }else {
            // 点击位于 上半圆
            clickAngle = 2*180-clickAngle;
        }
        return clickAngle;
    }

    /**
     * 设置数据
     * Apiece创建 颜色必备
     *
     * @param pieData
     */
    public void feedData(List<Apiece> pieData){
        if(pieData.size() == 0) {
            throw new RuntimeException("设置的数据错误");
        }
        this.pieData.clear();
        float totalData = 0;
        for(Apiece pie : pieData) {
            totalData += pie.getNum();
        }
        float totalAngle = 360-0*pieData.size();
        float startAngle = 0;
        //计算角度
        for(Apiece pie : pieData) {
            float sweepAngle = pie.getNum()/totalData*totalAngle;
            pie.setStartAngle(startAngle);
            pie.setSweepAngle(sweepAngle);
            startAngle += sweepAngle;
        }
        this.pieData = pieData;
        if(pieData.get(0).getPieColor() == Integer.MAX_VALUE) {
            setEachPieColor();
        }
        extraDataInit();
        postInvalidate();
    }

    /**
     * 设置数据
     *
     * @param pieData
     */
    public void feedData(Float[] pieData){
        List<Float> floats = Arrays.asList(pieData);
        feedData2(floats);
    }

    /**
     * 设置 饼图数据 解析出角度颜色数据
     *
     * @param pieData
     */
    public void feedData2(List<Float> pieData){
        Logger.i("显示控件前设置变量代码执行顺序", "设置数据入口--feedData(List<Float> pieData)");
        this.pieData.clear();// 清空原有 数据
        // pieData.addAll(pieData);
        for(Float each : pieData) {
            totalPieNum += each;
        }
        float startAngle = 0;
        for(Float each : pieData) {
            float sweepAngle = each/totalPieNum*360;
            // startAngle = ;
            Apiece pie = new Apiece("", each, 0, startAngle, sweepAngle);
            startAngle += sweepAngle;
            this.pieData.add(pie);
        }
        extraDataInit();
        // 为每个扇形设置不同颜色
        setEachPieColor();
        postInvalidate();
        //        // =================设置完数据后 显示到界面==========================
    }

    /**
     * 获取随机的颜色
     *
     * @return
     */
    private int getRanColor(){
        Random random = new Random();
        return 0xff000000|random.nextInt(0x00ffffff);
    }

    /**
     * 设置饼图各扇形的描述
     * 之前必须设置饼图的 folat数据 feedData(List<Float> pieData2)
     *
     * @param pieDescData2
     */
    public void setDescPiedata(List<String> pieDescData2){
        if(pieData.size() == 0 || pieDescData2.size() != pieData.size()) {
            throw new RuntimeException(
                    "设置的数据不对，或者请先设置 饼图数据setPiedata(List<Float> pieData2)/或者AnalyticData(List<Apiece> pieData2)"+"，饼图描述后设置");
        }
        for(int i = 0; i<pieData.size(); i++) {
            Apiece eachPie = pieData.get(i);
            String describe = pieDescData2.get(i);
            eachPie.setDescribe(describe);
        }
        extraDataInit();
    }

    /**
     * 将饼图中扇形的 对角线 描述信息describe传到 画线的类
     */
    public void extraDataInit(){
        DecimalFormat format = new DecimalFormat("##.##");
        //无论是否对角线显示都 传数据到画线
        List<Map<String,Object>> showDatas = new ArrayList<>();
        for(Apiece epie : pieData) {
            Map<String,Object> pieMap = new HashMap<>();
            float startAngle2 = epie.getStartAngle();
            float sweepAngle = epie.getSweepAngle();
            float cenAngle = startAngle2+sweepAngle/2;
            pieMap.put("angle", cenAngle);//扇形的对角线
            String show = TextUtils.isEmpty(epie.getDescribe()) ? format.format(epie.getNum()) : epie.getDescribe();
            pieMap.put("show", show);//提示内容 desc
            showDatas.add(pieMap);
        }
        //将数据传递到父类 line
        setPieAngles(showDatas);
    }

    /**
     * 第一次展现 饼图的动画
     */
    public void aniShowPie(){
        mAnimator.cancel();
        mAnimator.setDuration(PIEANITIME);
        mAnimator.setInterpolator(pieInterpolator);
        mAnimator.addListener(this);
        switch(pieshowani) {
            case SCANNING:
                //扫描展现动画
                // ---够函--feedData---到aniShowPie ---onsizechange----ondraw
                mAnimator.setFloatValues(360, 0);
                mAnimator.start();
                mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation){
                        sweep4 = (float)animation.getAnimatedValue();
                        postInvalidate();
                    }
                });
                break;
            case FILLOUTING:
                //内往外扩张动画
                // --feedData---到aniShowPie 此时just还没拿到数据(just在onsizechange里面获取值得)
                mAnimator.setFloatValues(0, pieRadius);
                mAnimator.start();
                mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation){
                        fillouting = (float)animation.getAnimatedValue();
                        postInvalidate();
                    }
                });
                break;
            case GROWING:
                ObjectAnimator oa = ObjectAnimator.ofFloat(this, "showProgress", 0, 1);
                oa.setDuration(PIEANITIME);
                oa.addListener(this);
                oa.setInterpolator(pieInterpolator);
                oa.cancel();
                oa.start();
                //                mAnimator.setFloatValues(0, 1);
                //                mAnimator.start();
                //                mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                //                    @Override
                //                    public void onAnimationUpdate(ValueAnimator animation){
                //                        showProgress = (float)animation.getAnimatedValue();
                //                        postInvalidate();
                //                    }
                //                });
                break;
        }
    }

    /**
     * 改变 每个扇形的颜色 使用后 需要刷新 postinvalidate
     */
    public void setEachPieColor(){
        for(Apiece eachP : pieData) {
            // 随机颜色
            Random random = new Random();
            int ranColor = 0xff000000|random.nextInt(0x00ffffff);
            // int nextInt = random.nextInt(16777216) + 1;
            // String hexString = Integer.toHexString(-nextInt);
            // int ranColor = Color.parseColor("#" + hexString);
            eachP.setPieColor(ranColor);
        }
    }

    /**
     * 控件的背景颜色
     */
    public int getBackColor(){
        return backColor;
    }

    /**
     * 控件的背景颜色
     *
     * @param backColor
     */
    public void setBackColor(int backColor){
        this.backColor = backColor;
    }

    /**
     * 扇形之间的间隔
     */
    public int getPieInterWidth(){
        return pieInterWidth;
    }

    /**
     * 扇形之间的间隔 是个角度 默认2
     */
    public void setPieInterWidth(int pieInterWidth){
        pieInterWidth = pieInterWidth>TsOut-pointPieOut ? (int)( TsOut-pointPieOut-10 ) : pieInterWidth;
        paintInter.setStrokeWidth(pieInterWidth);
    }

    /**
     * 饼图 所有数据(数字)总和
     *
     * @return
     */
    public int getTotalPieNum(){
        return totalPieNum;
    }

    /**
     * 饼图 旋转的角度
     */
    public float getDegrees(){
        return degrees;
    }

    /**
     * 饼图 旋转的角度
     * 建议在初始化的时候 使用
     *
     * @param degrees
     */
    public void setDegrees(float degrees){
        degrees = degrees>360 ? degrees-360*(int)( ( degrees/360 ) ) : degrees;
        this.degrees = degrees;
        rotedAngle = degrees;
        //刷新提示线
        if(( selectedPosition != -1 || down_x != -1 || down_y != -1 ) && selectedPosition<pieData.size()) {
            if(showInCenAngle) {
                showCenterGetPoint();
            }else {
                showAtTouch(down_x, down_y);
            }
            if(AniLine) {
                drawAniLine(Tstarty, turnY);
            }
        }else {
            //重置提示状态 饼图的选中状态 因为数据变了 选中的位置可能不存在
            selectedPosition = -1;
            down_x = down_y = -1;
        }
        postInvalidate();
    }


    /**
     * 指定的特殊角度 当某个扇形位于该角度是 该扇形变大 该值 为0 的时候 不变大 默认为0
     */
    public float getSpecialAngle(){
        return specialAngle;
    }

    /**
     * 指定的特殊角度 当某个扇形位于该角度是 该扇形变大 该值 为0 的时候 不变大 默认为0
     */
    public void setSpecialAngle(float specialAngle){
        this.specialAngle = specialAngle;
    }

    /**
     * 指定的某个 扇形 突出的大小
     */
    public float getPointPieOut(){
        return pointPieOut;
    }

    /**
     * 指定的某个 扇形 突出的大小 内部有处理 该值过大的情况（一般不修改）
     */
    public void setPointPieOut(float pointPieOut){
        //        this.pointPieOut = pointPieOut;
    }

    /**
     * 设定 扇形是否可 旋转 默认不可以
     */
    public boolean isPieRotateable(){
        return pieRotateable;
    }

    /**
     * 设定 扇形是否可 旋转 默认不可以
     */
    public void setPieRotateable(boolean pieRotateable){
        this.pieRotateable = pieRotateable;
    }

    /**
     * 返回 解析好的 饼图的数据 可以修改饼图某个扇形的颜色 描述 刷新下即可 不建议修改 角度 会出现为题 使用需注意
     *
     * @return
     */
    public List<Apiece> getPieData(){
        return pieData;
    }


    /**
     * 饼图点击监听
     *
     * @author Refuse
     */
    public interface OnItemPieClickListener {
        /**
         * 饼图点击 到某个扇形触发
         *
         * @param position
         *         被点击扇形的位置
         * @param itemPie
         *         被点击的扇形
         */
        void onPieItemClick(int position, Apiece itemPie);
    }

    /**
     * 点中了哪个扇形
     *
     * @param itemclicklistener
     */
    public void setOnItemPieClickListener(OnItemPieClickListener itemclicklistener){
        this.itemclicklistener = itemclicklistener;
    }

    /**
     * 特殊角 监听
     *
     * @author Refuse
     */
    public interface OnSpecialPieListener {
        /**
         * 特殊角度 所对应的 扇形 监听
         *
         * @param position
         *         被点击扇形的位置
         * @param itemPie
         *         被点击的扇形
         */
        void onSpecialPie(int position, Apiece itemPie);
    }

    /**
     * 旋转监听
     */
    public interface OnRotatingPieListener {
        /**
         * @param rotateDegrees
         *         一次旋转时 旋转了几度（0-360）
         * @param totalRotateDegrees
         *         一共旋转了几度（0-360）
         */
        void onRotatingPie(float rotateDegrees, float totalRotateDegrees);
    }

    /**
     * 设置 旋转监听
     * 旋转监听器 获取旋转角度
     *
     * @param rotatingPielistener
     */
    public void setOnRotatingPieListener(OnRotatingPieListener rotatingPielistener){
        this.rotatingPielistener = rotatingPielistener;
    }

    /**
     * 设置 特殊角度 对应的 扇形监听
     * 获取特殊角度所在的扇形位置
     *
     * @param specialPielistener
     */
    public void setOnSpecialPieListener(OnSpecialPieListener specialPielistener){
        this.specialPielistener = specialPielistener;
    }

    /**
     * 获取饼图的半径
     *
     * @return
     */
    public float getPieRadius(){
        return pieRadius;
    }

    /**
     * 获取 点中的扇形位置 或者说 非点击的时候 获取的是上次选择的扇形位置
     *
     * @return
     */
    public int getClickPosition(){
        return clickPosition;
    }

    /**
     * 设置点击的位置 可以 代码中 让指定位置的扇形 突出显示
     *
     * @param clickPosition
     */
    public void setClickPosition(int clickPosition){
        this.clickPosition = clickPosition;
        action = "up";
        cleanWire = true;
        postInvalidate();
        //		cleanWire = false;
    }

    public int getSpecialPosition(){
        return specialPosition;
    }

    /**
     * 饼图展现动画 默认为 fillOut 内往外扩展动画
     *
     * @param ani
     */
    public void setPieShowAnimation(PieShowAnimation ani){
        showPieAnimation = true;
        pieshowani = ani;
        switch(ani) {
            case NONE:
                showPieAnimation = false;
                break;
            case SCANNING:
                postInvalidate();
                break;
            case FILLOUTING:
                break;

        }
    }

    /**
     * 设置 会阻碍扇形滑动的外部ViewGroup  不要拦截事件
     *
     * @param clashView
     */
    public void setClashOuterViewGroup(ViewGroup... clashView){
        this.clashView = clashView;
    }


    /**
     * 控件的背景颜色
     */
    public int getPieBackColor(){
        return backColor;
    }

    /**
     * 控件的背景颜色
     *
     * @param backColor
     */
    public void setPieBackColor(int backColor){
        this.backColor = backColor;
    }

    //    /**
    //     * 控件局四周的距离 默认20 布局里面设置
    //     */
    //    public void setPadings(int padings) {
    //        // TODO
    //        this.padings = padings;
    //    }

    /**
     * 旋转的时候 允许 选中效果
     * 默认情况下只有up才有选中效果
     *
     * @param outMoving
     */
    public void setOutMoving(boolean outMoving){
        this.outMoving = outMoving;
    }

    /**
     * 设置间隔线颜色
     *
     * @param pieInterColor
     */
    public void setPieInterColor(int pieInterColor){
        paintInter.setColor(pieInterColor);
    }

    /**
     * 设置 饼图的是否出现展现动画
     *
     * @param showPieAnimation
     */
    public void setShowPieAnimation(boolean showPieAnimation){
        this.showPieAnimation = showPieAnimation;
    }

    /**
     * 设置动画插入器
     *
     * @param pieInterpolator
     */
    public void setPiePieInterpolator(TimeInterpolator pieInterpolator){
        this.pieInterpolator = pieInterpolator;
    }

    /**
     * 设置 允许扇形的点中效果
     *
     * @param pieSelector
     */
    public void setPieSelector(boolean pieSelector){
        PieSelector = pieSelector;
        clickPosition = Integer.MAX_VALUE;
        postInvalidate();
    }
}
