package com.jonas.jgraph.utils;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.jonas.jgraph.models.Jchart;

/**
 * @author jiangzuyun.
 * @date 2016/10/19
 * @des [一句话描述]
 * @since [产品/模版版本]
 */


public class CalloutHelper {


//    private static Paint mCalloutBgPaint;
//    private static float mLineWidth;
//    private static Paint mCalloutPaint;
//    private static float mWidth;
//
//    public static CalloutHelper createHelper(float lineWidth, float width, Paint calloutBgPaint,Paint calloutPaint) {
//        mLineWidth = lineWidth;
//        mWidth = width;
//        mCalloutBgPaint = calloutBgPaint;
//        mCalloutPaint = calloutPaint;
//        return new CalloutHelper();
//    }

    /**
     * 根据传入的数据 画出图表标注
     *
     * @param canvas
     * @param excel  标注数据
     * @param top    最大值，上部
     */
    public static void drawCalloutActual(Canvas canvas, @NonNull Jchart excel, boolean top, float lineWidth, float width,
                                         Paint calloutBgPaint, Paint calloutPaint,boolean scrollAble) {
//    public static void drawCalloutActual(Canvas canvas, @NonNull SugChart.SugExcel excel, boolean top) {
        float toPoint = dip2px(2) + lineWidth / 2;//标注距离点的间距
        if (excel.getHeight() <= 0) {
            return;
        }

        PointF midPointF = excel.getMidPointF();
//        String msg = excel.getUpper() + excel.getUnit();
        String msg = String.valueOf(excel.getShowMsg());

        Path triangleBg = new Path();

        float calloutHeight = dip2px(21);//胶囊的高
        float bgPading = dip2px(6);//间距
        float triangleBgWidth = dip2px(8);
        float triangleBgHeight = dip2px(4);

        //三角
        if (top) {
            triangleBg.moveTo(midPointF.x, midPointF.y - toPoint);
            triangleBg.lineTo(midPointF.x - triangleBgWidth / 2, midPointF.y - toPoint - triangleBgHeight - 1);
            triangleBg.lineTo(midPointF.x + triangleBgWidth / 2, midPointF.y - toPoint - triangleBgHeight - 1);
            triangleBg.close();
        } else {
            triangleBg.moveTo(midPointF.x, midPointF.y + toPoint);
            triangleBg.lineTo(midPointF.x - triangleBgWidth / 2, midPointF.y + toPoint + triangleBgHeight + 1);
            triangleBg.lineTo(midPointF.x + triangleBgWidth / 2, midPointF.y + toPoint + triangleBgHeight + 1);
            triangleBg.close();
        }

        canvas.drawPath(triangleBg, calloutBgPaint);
        float msgWidth = calloutPaint.measureText(msg);


        RectF rectF = new RectF(midPointF.x - msgWidth / 2f - bgPading, midPointF.y - toPoint - triangleBgHeight - calloutHeight
                , midPointF.x + msgWidth / 2f + bgPading, midPointF.y - toPoint - triangleBgHeight);

        if (!top) {
            rectF.offset(0, lineWidth + getCalloutHeight() + dip2px(4) + dip2px(2));
        }
        float dffw = 0;
        if(!scrollAble) {
            //防止 画出到屏幕外
            dffw = rectF.right-width;
        }
        float msgX = midPointF.x;
        float magin = 1;
        if (dffw > 0) {
            rectF.right = rectF.right - dffw - magin;
            rectF.left = rectF.left - dffw - magin;
            msgX = midPointF.x - dffw - magin;
        } else if (rectF.left < 0) {
            rectF.right = rectF.right - rectF.left + magin;
            msgX = midPointF.x - rectF.left + magin;
            rectF.left = magin;
        }
        canvas.drawRoundRect(rectF, rectF.height() / 2f, rectF.height() / 2f, calloutBgPaint);

        canvas.drawText(msg, msgX, rectF.bottom - rectF.height() / 2f + getTextHeight(calloutPaint) / 2, calloutPaint);
    }

    public static int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static float getTextHeight(Paint textPaint) {
        return -textPaint.ascent() - textPaint.descent();
    }

    /**
     * 标注 高度
     * @return
     */
    public static float getCalloutHeight() {
        // 胶囊高 + 三角距离点的间距 + 三角高
        return dip2px(21) + dip2px(2) + dip2px(4);
    }
}
