package com.jonas.schart;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.jonas.schart.chart.JChart;
import com.jonas.schart.chartbean.JExcel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created on 2016/5/6.  by Jonas{https://github.com/mychoices}
 */

public class SugTestActivity extends Activity {

    private JChart mSchart;
    private List<JExcel> mJExcels;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugtest);
        mSchart = (JChart) findViewById(R.id.sug_recode_schar);

        mJExcels = new ArrayList<>();
        for (int i = 0; i < 11; i++) {

            JExcel jExcel = new JExcel(new Random().nextInt(100)+20,"km", "测试");
//            JExcel jExcel = new JExcel(100, "测试");
//            if (i == 1) {
//                jExcel = new JExcel(200, "测试");
//            }
            mJExcels.add(jExcel);
        }
//        mSchart.setChartStyle(ChartStyle.LINE);
        mSchart.setChartStyle(JChart.ChartStyle.BAR);
        mSchart.setExecelPaintShaderColors(new int[]{Color.parseColor("#089900"), Color.parseColor("#9FC700")});
//        mSchart.setExecelPaintShaderColors(new int[]{Color.parseColor("#4df1dbd4"), Color.TRANSPARENT});
//        mSchart.setScrollAble(false);
        mSchart.setFixedWidth(31);
        mSchart.cmdFill(mJExcels);
    }

    public void changestyle(View v) {
        if (mSchart.getChartStyle() == JChart.ChartStyle.BAR) {
            mSchart.setChartStyle(JChart.ChartStyle.LINE);
            mSchart.setExecelPaintShaderColors(new int[]{Color.parseColor("#4df1dbd4"), Color.TRANSPARENT});
            mSchart.cmdFill(mJExcels);
        } else {
            mSchart.setChartStyle(JChart.ChartStyle.BAR);
            mSchart.setExecelPaintShaderColors(new int[]{Color.parseColor("#089900"), Color.parseColor("#9FC700")});
            mSchart.cmdFill(mJExcels);
        }
        mSchart.postInvalidate();
    }
}

