package com.jonas.jdiagram.inter;

import android.graphics.Paint;

/**
 * @author yun.
 * @date 2016/6/8
 * @des [进度控件的基类 封装了一些通用的方法]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public interface IProgress {

    IProgress setAniDuration(long duration);
    float getProgress();
    Paint getProgPaint();
    Paint getTextPaint();

    /**
     * 当前进度
     *
     * @param progress
     */
    void setProgress(float progress);

    /**
     * 当前进度
     *
     * @param progress
     */
    IProgress setAniProgress(float progress);

    /**
     * 进度条的宽度
     *
     * @param progWidth
     */
    IProgress setProgressWidth(float progWidth);

    /**
     * 总进度
     *
     * @param max
     */
    IProgress setMax(float max);

    IProgress setProgressBackground(int bgColor);

    /**
     * 进度颜色
     *
     * @param progressColor
     */
    IProgress setProgressColor(int progressColor);
}
