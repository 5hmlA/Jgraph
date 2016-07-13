package com.jonas.jgraph.utils;

import android.graphics.Path;
import android.graphics.PointF;

/**
 * @author yun.
 * @date 2016/7/13
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public class DrawHelper {
    /**
     *  由 lsPointF到lePointF的曲线 转换到 sPoint到ePointF的曲线
     * @param path
     * @param sPoint
     * @param ePointF
     * @param lsPointF
     * @param lePointF
     * @param aniRatio
     */
    public static void AnipathCubicFromLast(Path path, PointF sPoint, PointF ePointF, PointF lsPointF, PointF
            lePointF, float aniRatio){
        float con_x = ( sPoint.x+ePointF.x )/2;
        path.cubicTo(con_x, lsPointF.y+( sPoint.y-lsPointF.y )*aniRatio, con_x,
                lePointF.y+( ePointF.y-lePointF.y )*aniRatio, ePointF.x,
                lePointF.y+( ePointF.y-lePointF.y )*aniRatio);
    }
    public static void AnipathLinetoFromLast(Path path, PointF toPoint, PointF ltoPointF, float aniRatio){
        path.lineTo(toPoint.x, ltoPointF.y+( toPoint.y-ltoPointF.y )*aniRatio);
    }

    /**
     * prePoint到nextPointF的曲线
     * @param pathline
     * @param prePoint
     * @param nextPointF
     */
    public static void pathCubicTo(Path pathline, PointF prePoint, PointF nextPointF){
        float c_x = ( prePoint.x+nextPointF.x )/2;
        pathline.cubicTo(c_x, prePoint.y, c_x, nextPointF.y, nextPointF.x, nextPointF.y);
    }
}
