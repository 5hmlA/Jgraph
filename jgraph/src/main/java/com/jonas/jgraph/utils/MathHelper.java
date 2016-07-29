package com.jonas.jgraph.utils;

import android.content.Context;

/**
 * @author jiangzuyun.
 * @date 2016/7/14
 * @des [一句话描述]
 * @since [产品/模版版本]
 */


public class MathHelper {


    /**
     * @param num
     * @return 最接近num的数 这个数同时是5的倍数
     */
    public static int getRound5(float num){
        return ( (int)( num+2.5 ) )/5*5;
    }

    /**
     * @param num
     * @return 根据num向上取数 这个数同时是5的倍数
     */
    public static int getCeil5(float num){
        return ( (int)( num+4.9999999 ) )/5*5;
    }

    /**
     * 向上取数
     *
     * @param num
     * @return
     */
    public static int getCeil10(float num){
        return ( (int)( num+9.9999999 ) )/10*10;
    }

    /**
     * 向下取数
     *
     * @param num
     * @return
     */
    public static int getRound10(float num){
        return ( (int)( num+5 ) )/10*10;
    }

    public static int getCast10(float num){
        return ( (int)( num ) )/10*10;
    }

    /**
     * 获取 (x1,y1)和点(x2,y2)连线与 水平右的夹角
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double getPointAngle(float x1, float y1, float x2, float y2){
        float a = x1-x2;
        float b = y1-y2;
        // 斜边
        double c = Math.sqrt(Math.pow(a, 2)+Math.pow(b, 2));
        // 获取 弧度
        double acos = Math.acos(a/c);
        // 获取 角度 角度=弧度/PI * 180
        double clickAngle = acos/Math.PI*180;// 注意 获取的只是0-180 还需要判断
        if(y1<y2) {
            // 点击位于 上半圆
            clickAngle = 2*180-clickAngle;
        }
        return clickAngle;
    }

    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)( dipValue*scale+0.5f );
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param sp
     *         （DisplayMetrics类中属性scaledDensity）
     */
    public static int sp2px(Context context, float sp){
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)( sp*fontScale+0.5f );
    }

}
