package com.jonas.jdiagram.progress;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

public class RoundBollProgress extends View {

	private Paint mPaint;
	private int height;
	private int width;
	/**
	 * 圆环的 角度
	 */
	private float sweepAngle;
	/**
	 * 圆环的其实角度
	 */
	private float startAngle = 0;
	/**
	 * 圆环的颜色
	 */
	private int ringColor = Color.BLUE;
	/**
	 * 圆环的宽度
	 */
	private int ringWidth = 6;
	/**
	 * 圆环 据周边的距离
	 */
	private int ringPading = 10;
	/**
	 * 圆环上 滚动小球的半径
	 * 当 小球的半径等于 圆环 环的半径的时候？？
	 */
	private int boxRadius = ringWidth / 2 + ringPading / 2+4;
	/**
	 * 圆环上 滚动小球的颜色
	 */
	private int boxColor = Color.RED;
	/**
	 * 圆环 内圆的颜色
	 */
	private int roundColor = Color.YELLOW;
	// private int roundColor = Color.WHITE;
	/**
	 * 圆环 内字 的颜色
	 */
	private int textColor = Color.GREEN;
	/**
	 * 圆环 内字 的内容
	 */
	private String centerText;
	/**
	 * 圆环 内字 的大小
	 */
	private int centerTextSize = 35;

	public RoundBollProgress(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public RoundBollProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		width = getWidth();
		height = getHeight();
		// 以小的边为主 设定为正方形
		if (width > height) {
			width = height;
		}
		height = height > width ? width : height;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 画圆环（扇形） = 外圆（扇形） - 内圆（扇形）
		// 最底部外扇形
		RectF oval = new RectF(ringPading, ringPading, width - ringPading,
				height - ringPading);
		mPaint.setColor(ringColor);
		canvas.drawArc(oval, startAngle, sweepAngle, true, mPaint);
		// 画上面的 小外圆
		mPaint.setColor(roundColor);
		canvas.drawCircle(width / 2, height / 2, width / 2 - ringWidth
				- ringPading, mPaint);

		// 在圆环末端 画小球 角度=弧度/PI * 180 弧度＝(角度/180) *PI
		// 在圆环末端 的小球 的轨迹是圆 半径为 内圆半径+圆环环宽度的一半
		float radius = width * 1f / 2 - ringWidth - ringPading + ringWidth * 1f
				/ 2;
		double sweepHudu = sweepAngle / 180 * Math.PI;
		float cenx = (float) (radius * Math.cos(sweepHudu)) + width / 2;
		float ceny = (float) (radius * Math.sin(sweepHudu)) + height / 2;
		mPaint.setColor(boxColor);
		canvas.drawCircle(cenx, ceny, boxRadius, mPaint);

		// 画两条 对称轴
//		mPaint.setColor(Color.BLACK);
//		canvas.drawLine(0, height / 2, width, height / 2, mPaint);
//		canvas.drawLine(width / 2, 0, width / 2, height, mPaint);

		if (TextUtils.isEmpty(centerText)) {
			//如过 文字为空的话 就不画
			return;
		}
		// 圆环中心 写字
		mPaint.setColor(Color.BLACK);
		mPaint.setTextSize(centerTextSize);

		// mpaint.setTextSize(55);
		// C 语言 的思想
		Rect bounds = new Rect();
		mPaint.getTextBounds(centerText, 0, centerText.length(), bounds);
		// 获取 所画的字的宽和高
		// mpaint.measureText(text);//返回的是字的宽度
		int textWidth = bounds.width();
		int textHeight = bounds.height();

		canvas.drawText(centerText, width/2 - textWidth / 2, height/2 + textHeight / 2, mPaint);
	}

	public void setAniSweepAngle(float sweepAngle) {
		ObjectAnimator.ofFloat(this, "sweepAngle", 0, sweepAngle)
				.setDuration(1000).start();

	}

	public void setAniSweepAngle(float startAngle, float sweepAngle) {
		this.startAngle = startAngle;
		ObjectAnimator.ofFloat(this, "sweepAngle", 0, sweepAngle)
				.setDuration(1000).start();
	}

	public float getSweepAngle() {
		return sweepAngle;
	}

	public void setSweepAngle(float sweepAngle) {
		this.sweepAngle = sweepAngle;
		postInvalidate();
	}

	/**
	 * @return	圆环的颜色
	 */
	public int getRingColor() {
		return ringColor;
	}

	/**
	 * 
	 * @param ringColor	圆环的颜色
	 */
	public void setRingColor(int ringColor) {
		this.ringColor = ringColor;
	}

	/**
	 * @return 圆环的 环半径
	 */
	public int getRingWidth() {
		return ringWidth;
	}

	/**
	 * @return 圆环的 环半径
	 */
	public void setRingWidth(int ringWidth) {
		this.ringWidth = ringWidth;
	}

	/**
	 * @return 圆环 局周围的 边距
	 */
	public int getRingPading() {
		return ringPading;
	}

	/**
	 * @return 圆环 局周围的 边距
	 */
	public void setRingPading(int ringPading) {
		this.ringPading = ringPading;
	}

	/**
	 * @return	滚动小球的半径
	 */
	public int getBoxRadius() {
		return boxRadius;
	}

	/**
	 * @return	滚动小球的半径
	 */
	public void setBoxRadius(int boxRadius) {
		this.boxRadius = boxRadius;
	}

	/**
	 * @return	滚动小球的颜色
	 */
	public int getBoxColor() {
		return boxColor;
	}

	/**
	 * @return	滚动小球的颜色
	 */
	public void setBoxColor(int boxColor) {
		this.boxColor = boxColor;
	}

	/**
	 * @return	内圆的颜色 圆环背景色
	 */
	public int getRoundColor() {
		return roundColor;
	}

	/**
	 * @return	内圆的颜色 圆环背景色
	 */
	public void setRoundColor(int roundColor) {
		this.roundColor = roundColor;
	}

	/**
	 * @return	文字的颜色
	 */
	public int getTextColor() {
		return textColor;
	}

	/**
	 * @return	文字的颜色
	 */
	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	/**
	 * @return	文字的内容
	 */
	public String getCenterText() {
		return centerText;
	}

	/**
	 * @return	文字的内容，当内容为空时 不显示 默认不显示
	 */
	public void setCenterText(String centerText) {
		this.centerText = centerText;
	}

	/**
	 * @return	文字的大小
	 */
	public int getCenterTextSize() {
		return centerTextSize;
	}

	/**
	 * @return	文字的大小
	 */
	public void setCenterTextSize(int centerTextSize) {
		this.centerTextSize = centerTextSize;
	}

}
