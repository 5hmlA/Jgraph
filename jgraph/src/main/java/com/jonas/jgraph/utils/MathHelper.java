package com.jonas.jgraph.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
     * @param spValue
     *         （DisplayMetrics类中属性scaledDensity）
     */
    public static int sp2px(Context context, float spValue){
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)( spValue*fontScale+0.5f );
    }

    /**
     * 根据时间 判断显示多少段,
     */
    private void analyzeStep(List data, int allow){
        ArrayList<Float> remainds = new ArrayList<>(); //5,4,3
        float total = data.size();
        if(total> allow) {
            float _5 = total/5;
            if(total%5 == 0) {
                arrangeData(data,5, (int)_5,allow);
                return;
            }
            float _4 = total/4;
            if(total%5 == 0) {
                arrangeData(data,4, (int)_4,allow);
                return;
            }
            float _3 = total/3;
            if(total%5 == 0) {
                arrangeData(data,3, (int)_3,allow);
                return;
            }
            remainds.add(Math.abs(_5-getRound5(_5)));
            remainds.add(Math.abs(_4-getRound5(_4)));
            remainds.add(Math.abs(_3-getRound5(_3)));

            Float min = Collections.min(remainds);
            for(int i = 0; i<remainds.size(); i++) {
                if(min == remainds.get(i)) {
                    arrangeData(data,5-i, getRound5(total/( 5-i )),allow);
                }
            }
        }
    }

    /**
     * @param graph
     *         多少段
     * @param interval
     *         每段多上时间 分钟
     */
    private void arrangeData(List data,int graph, int interval, float allow){
        interval = interval>0 ? interval : 1;
        //曲数据的间隔
        float step = data.size()/allow;
        int showInterval = 0;
        System.out.println("取数间隔："+step+"======分几组："+graph+"---每组时间"+interval);

//        int pointInterval = allow/graph;
//
//
//        mHeartSugExcels = new ArrayList<>();
//        for(int i = 0; i<mTotaldate.size(); i++) {
//            if(i%step == 0 && mHeartSugExcels.size()<allow) {
//                SugChart.SugExcel sugExcel = new SugChart.SugExcel(mTotaldate.get(i), "");
//                mHeartSugExcels.add(sugExcel);
//            }
//        }
//
//        int lastSec = 1;
//        for(int i = 0; i<mHeartSugExcels.size(); i++) {
//            int sec = i*step*60%( interval*60 );
//            if(lastSec>sec) {
//                mHeartSugExcels.get(i).setXmsg(showInterval+"");
//                showInterval += interval;
//            }
//            lastSec = sec;
//        }
//
//        mSugScJHeart.cmdFill(mHeartSugExcels);

    }
}
