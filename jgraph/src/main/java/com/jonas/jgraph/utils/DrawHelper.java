package com.jonas.jgraph.utils;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Size;

import java.security.SecureRandom;

/**
 * @author yun.
 * @date 2016/7/13
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class DrawHelper {
    /**
     * 由 lsPointF到lePointF的曲线 转换到 sPoint到ePointF的曲线
     *
     * @param path
     * @param sPoint
     * @param ePointF
     * @param lsPointF
     * @param lePointF
     * @param aniRatio
     */
    public static void AnipathCubicFromLast(Path path, PointF sPoint, PointF ePointF, PointF lsPointF, PointF lePointF, float aniRatio){
        float con_x = ( sPoint.x+ePointF.x )/2;
        path.cubicTo(con_x, lsPointF.y+( sPoint.y-lsPointF.y )*aniRatio, con_x,
                lePointF.y+( ePointF.y-lePointF.y )*aniRatio, ePointF.x, lePointF.y+( ePointF.y-lePointF.y )*aniRatio);
    }

    public static void AnipathLinetoFromLast(Path path, PointF toPoint, PointF ltoPointF, float aniRatio){
        path.lineTo(toPoint.x, ltoPointF.y+( toPoint.y-ltoPointF.y )*aniRatio);
    }

    /**
     * prePoint到nextPointF的曲线
     *
     * @param pathline
     * @param prePoint
     * @param nextPointF
     */
    public static void pathCubicTo(Path pathline, PointF prePoint, PointF nextPointF){
        float c_x = ( prePoint.x+nextPointF.x )/2;
        pathline.cubicTo(c_x, prePoint.y, c_x, nextPointF.y, nextPointF.x, nextPointF.y);
    }

    /**
     * 得到一个随机颜色
     *
     * @return 随机颜色
     */
    public int randomColor(){
        SecureRandom random = new SecureRandom();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return Color.rgb(red, green, blue);
    }

    /**
     * 得到单个字的高度
     *
     * @param paint
     *         画笔
     * @return 高度
     * thanks: http://blog.csdn.net/tianjf0514/article/details/7642656
     */
    public float getFontHeight(Paint paint){
        return paint.ascent()+paint.descent();
//        Paint.FontMetrics fm = paint.getFontMetrics();
//        return (float)Math.ceil(fm.descent-fm.ascent);

    }

    /**
     * 字符串的宽度
     *
     * @param paint
     *         画笔
     * @param str
     *         字符串
     * @return
     */
    public float getTextWidth(Paint paint, @Size(min = 1) String str){
        return paint.measureText(str, 0, str.length());
    }
}
