package com.jonas.jgraph.progress;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.BounceInterpolator;

import java.util.Random;

/**
 * @author yun.
 * @date 2015/10/7
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public class RoundAniRec extends View {

	private Paint mPaint;
	private int width;
	private int height;
	private int mini;
	/**
	 * 圆角矩形的 颜色
	 */
	private int recColor = Color.TRANSPARENT;
	private float recright;
	private float recbottom;
	/**
	 * rx ry 任意一个为0 就为直角矩形
	 */
	private float rx = Integer.MIN_VALUE;
	private float ry = Integer.MIN_VALUE;
	private float left;
	private float top;
	private long ANI_TIME = 4000;
	/**
	 * 进度 的比例 中的最大值
	 */
	private float progressMax = 100;
	// private TimeInterpolator interpolator = new AccelerateInterpolator(1);
	private TimeInterpolator interpolator = new BounceInterpolator();
	private float currentProgress;
	/**
	 * 控件两边 较长者
	 */
	private int max;
	private boolean progressAni;
	/**
	 * 控件是否显示了 是否执行完了onsizechange
	 */
	private boolean onSized_showfinish;

	public RoundAniRec(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	public RoundAniRec(Context context) {
		super(context);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	/**
	 * 
	 * 界面控件初始化 执行循序， 1，RoundAniRec(Context context, AttributeSet
	 * attrs)控件来自布局（new出来的调用对应的构造函数） 2，（变量初始化）get,set方法 和 公有变量的赋值(外部调用)
	 * //如果调用的方法内部涉及到其他变量(界面显示才能获得的值) 则该方法需要在该控件在界面显示后调用
	 * 3，onSizeChanged（界面显示的时候调用，只调用一次） 4，onDraw（界面显示的时候调用）界面刷新就调用
	 * 
	 * 此时 获取控件的 宽高 设置默认值
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		width = getWidth();
		height = getHeight();
		mini = width > height ? height : width;
		max = width < height ? height : width;
		// 当外部没设置矩形圆角的半径时 设置默认
		if (rx == Integer.MIN_VALUE || ry == Integer.MIN_VALUE) {
			ry = rx = mini / 2;// rx或ry过大的话 会被认为是短边的一半 那么圆角矩形两边就刚好是半圆
		}
		// 初始化 进度条颜色
		if (Color.TRANSPARENT == recColor) {
			// 默认颜色 随机
			recColor = setEachPieColor();
		}
		// TODO 默认显示 注释掉的画 默认不显示任何
		recbottom = recright = mini;
		// recbottom = recright = 0;
		
		if (currentProgress != 0) {
			float ratio = currentProgress / progressMax;
			setAniRecChange(progressAni?0:ratio*max, ratio * max);
//			if (progressAni) {
//				setAniRecChange(0, ratio * max);
//				return;
//			}
//			setAniRecChange(ratio * max, ratio * max);
		}
		onSized_showfinish = true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		//清除float的误差
		if (width>height) {
			left = 0;
			if (recright>mini) {
				top = left = 0;
				recbottom = mini;
			}
		}else{
			top = 0;
			if (recbottom>mini) {
				recright = mini;
				top = left = 0;
			}
		}

		mPaint.setColor(recColor);
		RectF rect = new RectF(left, top, recright, recbottom);
		canvas.drawRoundRect(rect, rx, ry, mPaint);

	}

	public int setEachPieColor() {
		// 随机颜色
		Random random = new Random();
		int ranColor = 0xff000000 | random.nextInt(0x00ffffff);
		// int nextInt = random.nextInt(16777216) + 1;
		// String hexString = Integer.toHexString(-nextInt);
		// int ranColor = Color.parseColor("#" + hexString);
		return ranColor;
	}

	/**
	 * AccelerateDecelerateInterpolator 在动画开始与结束的地方速率改变比较慢，在中间的时候加速
	 * 
	 * AccelerateInterpolator 在动画开始的地方速率改变比较慢，然后开始加速
	 * 
	 * AnticipateInterpolator 开始的时候向后然后向前甩
	 * 
	 * AnticipateOvershootInterpolator 开始的时候向后然后向前甩一定值后返回最后的值
	 * 
	 * BounceInterpolator 动画结束的时候弹起
	 * 
	 * CycleInterpolator 动画循环播放特定的次数，速率改变沿着正弦曲线
	 * 
	 * DecelerateInterpolator 在动画开始的地方快然后慢
	 * 
	 * LinearInterpolator 以常量速率改变
	 * 
	 * OvershootInterpolator 向前甩一定值后再回到原来位置
	 */
	public void setAniRecChange(float start, float change) {
		long time = start == change?0:ANI_TIME;
		if (mini < width) {
			// 宽更大 则向右变长
			// recbottom = mini;
			// ObjectAnimator animator = ObjectAnimator.ofFloat(this,
			// "recright",
			// 0, change).setDuration(5000);
			ValueAnimator animator = ValueAnimator.ofFloat(start, change)
					.setDuration(time);

			animator.setInterpolator(interpolator);
			animator.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					recright = (float) animation.getAnimatedValue();
					if (recright < mini) {
						top = mini * 1f / 2 - recright / 2;
//						top = top < 0 ? 0 : top;
						recbottom = recright / 2 + mini * 1f / 2;
						recbottom = recbottom > mini ? mini : recbottom;
					}
					postInvalidate();
				}
			});
			animator.start();
		} else {
			// 高更大 则向下变长
			// recright = mini;
			ValueAnimator animator = ValueAnimator.ofFloat(start, change)
					.setDuration(time);
			animator.setInterpolator(interpolator);
			animator.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					recbottom = (float) animation.getAnimatedValue();
					if (recbottom < mini) {
						left = mini * 1f / 2 - recbottom / 2;
						left = left < 0 ? 0 : left;
						recright = recbottom / 2 + mini * 1f / 2;
						recright = recright > mini ? mini : recright;
					}
					postInvalidate();
				}
			});
			animator.start();
		}
	}

	public int getRecColor() {
		return recColor;
	}

	/**
	 * 设置进度条的颜色 默认 随机 设置为透明的话 颜色也将变为随机
	 * 
	 * @param recColor 矩形颜色
	 */
	public void setRecColor(int recColor) {
		this.recColor = recColor;
		postInvalidate();
	}

	public long getANI_TIME() {
		return ANI_TIME;
	}

	/**
	 * 设置动画显示的时长
	 * 
	 * @param ANI_TIME 设置动画显示的时长
	 */
	public void setANI_TIME(long ANI_TIME) {
		this.ANI_TIME = ANI_TIME;
		postInvalidate();
	}

	public float getMaxProgress() {
		return progressMax;
	}

	/**
	 * 默认 maxProgress 为100
	 * 
	 * @param progressMax 默认 maxProgress 为100
	 */
	public void setMaxProgress(float progressMax) {
		this.progressMax = progressMax;
	}

	/**
	 * 设置进度条进度 执行动画
	 * 可在控件显示的时候执行动画  可在控件显示之后执行动画
	 * 默认最大进度progressMax = 100
	 * 
	 * @param currentProgress 进度条进度 执行动画
	 */
	public void setAniCurrentProgress(float currentProgress) {
		this.currentProgress = currentProgress;
		if (onSized_showfinish) {
			float ratio = currentProgress / progressMax;
			setAniRecChange(0, ratio * max);
			return;
		}
		progressAni = true;//控制控件显示时执行动画的变量
	}
	
	/**
	 * 设置进度条进度(从某个起点开始) 执行动画
	 * 可在控件显示的时候执行动画  可在控件显示之后执行动画
	 * 默认最大进度progressMax = 100
	 * 
	 * @param lastProgress	进度上次的位置
	 * @param currentProgress
	 */
	public void setAniCurrentProgress(float lastProgress,float currentProgress) {
		this.currentProgress = currentProgress;
		if (onSized_showfinish) {
			float ratio = currentProgress / progressMax;
			setAniRecChange(lastProgress/progressMax*max, ratio * max);
			return;
		}
		progressAni = true;//控制控件显示时执行动画的变量
	}

	/**
	 * 设置进度条进度 无动画
	 * 
	 * @param currentProgress 当前进度值
	 */
	public void setCurrentProgress(float currentProgress) {
		this.currentProgress = currentProgress;
		if (onSized_showfinish) {
			float ratio = currentProgress / progressMax;
			setAniRecChange(ratio * max, ratio * max);
			return;
		}
		progressAni = false;
	}

	public float getCurrentProgress() {
		return currentProgress;
	}

	public float getRx() {
		return rx;
	}

	/**
	 * 设置 圆角矩形 圆角半径 如过设置过大 那么 就默认为短边的一半 rx ry 任意一个为0 就为直角矩形
	 * 
	 * @param rx 圆角矩形 圆角半径
	 */
	public void setRx(float rx) {
		this.rx = rx;
	}

	public float getRy() {
		return ry;
	}

	/**
	 * 设置 圆角矩形 圆角半径 rx ry 任意一个为0 就为直角矩形 任意一个为 负数 则矩形不显示
	 * 
	 * @param ry 圆角半径
	 */
	public void setRy(float ry) {
		this.ry = ry;
	}
}
