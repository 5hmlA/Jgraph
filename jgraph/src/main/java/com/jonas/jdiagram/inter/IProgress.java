package com.jonas.jdiagram.inter;

import android.graphics.Paint;

/**
 * @author jiangzuyun.
 * @date 2016/7/11
 * @des [一句话描述]
 * @since [产品/模版版本]
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
