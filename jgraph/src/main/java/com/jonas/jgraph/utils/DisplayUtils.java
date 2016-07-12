package com.jonas.jgraph.utils;

import android.content.Context;

/**
 * Created by jiangzuyun on 2015/9/2.
 */
public class DisplayUtils {

    private static float sScale;

    public static int dip2px(Context context, float dipValue) {
        sScale = sScale == 0 ? context.getResources().getDisplayMetrics().density : sScale;
        return (int) (dipValue * sScale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        sScale = sScale == 0 ? context.getResources().getDisplayMetrics().density : sScale;
        return (int) (pxValue / sScale + 0.5f);
    }
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
