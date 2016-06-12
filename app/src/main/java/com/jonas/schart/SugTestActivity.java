package com.jonas.schart;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.jonas.schart.chart.SugChart;
import com.jonas.schart.chartbean.SugExcel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created on 2016/5/6.  by Jonas{https://github.com/mychoices}
 */

public class SugTestActivity extends Activity {

    private SugChart mSchart;
    private List<SugExcel> mSugExcels;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugtest);
        mSchart = (SugChart) findViewById(R.id.sug_recode_schar);

        mSugExcels = new ArrayList<>();
        for (int i = 0; i < 11; i++) {

            SugExcel sugExcel = new SugExcel(new Random().nextInt(100)+20,"km", "测试");
//            SugExcel sugExcel = new SugExcel(100, "测试");
//            if (i == 1) {
//                sugExcel = new SugExcel(200, "测试");
//            }
            mSugExcels.add(sugExcel);
        }
//        mSchart.setChartStyle(ChartStyle.LINE);
        mSchart.setChartStyle(SugChart.ChartStyle.BAR);
        mSchart.setExecelPaintShaderColors(new int[]{Color.parseColor("#089900"), Color.parseColor("#9FC700")});
//        mSchart.setExecelPaintShaderColors(new int[]{Color.parseColor("#4df1dbd4"), Color.TRANSPARENT});
//        mSchart.setScrollAble(false);
        mSchart.setFixedWidth(31);
        mSchart.cmdFill(mSugExcels);
    }

    public void changestyle(View v) {
        if (mSchart.getChartStyle() == SugChart.ChartStyle.BAR) {
            mSchart.setChartStyle(SugChart.ChartStyle.LINE);
            mSchart.setExecelPaintShaderColors(new int[]{Color.parseColor("#4df1dbd4"), Color.TRANSPARENT});
            mSchart.cmdFill(mSugExcels);
        } else {
            mSchart.setChartStyle(SugChart.ChartStyle.BAR);
            mSchart.setExecelPaintShaderColors(new int[]{Color.parseColor("#089900"), Color.parseColor("#9FC700")});
            mSchart.cmdFill(mSugExcels);
        }
        mSchart.postInvalidate();
    }
}

