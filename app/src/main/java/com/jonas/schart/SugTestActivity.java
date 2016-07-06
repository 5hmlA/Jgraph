package com.jonas.schart;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.jonas.jdiagram.graph.JChart;
import com.jonas.jdiagram.models.Jchart;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created on 2016/5/6.  by Jonas{https://github.com/mychoices}
 */

public class SugTestActivity extends Activity {

    private JChart mSchart;
    private List<Jchart> mJcharts;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugtest);
        mSchart = (JChart) findViewById(R.id.sug_recode_schar);

        mJcharts = new ArrayList<>();
        for (int i = 0; i < 11; i++) {

            Jchart jchart = new Jchart(0,new Random().nextInt(100)+20,"测试");
//            Jchart jchart = new Jchart(100, "测试");
//            if (i == 1) {
//                jchart = new Jchart(200, "测试");
//            }
            mJcharts.add(jchart);
        }
//        mSchart.setChartStyle(ChartStyle.LINE);
        mSchart.setChartStyle(JChart.ChartStyle.BAR);
        mSchart.setExecelPaintShaderColors(new int[]{Color.parseColor("#089900"), Color.parseColor("#9FC700")});
//        mSchart.setExecelPaintShaderColors(new int[]{Color.parseColor("#4df1dbd4"), Color.TRANSPARENT});
//        mSchart.setScrollAble(false);
        mSchart.setFixedWidth(31);
        mSchart.cmdFill(mJcharts);
    }

    public void changestyle(View v) {
        if (mSchart.getChartStyle() == JChart.ChartStyle.BAR) {
            mSchart.setChartStyle(JChart.ChartStyle.LINE);
            mSchart.setExecelPaintShaderColors(new int[]{Color.parseColor("#4df1dbd4"), Color.TRANSPARENT});
            mSchart.cmdFill(mJcharts);
        } else {
            mSchart.setChartStyle(JChart.ChartStyle.BAR);
            mSchart.setExecelPaintShaderColors(new int[]{Color.parseColor("#089900"), Color.parseColor("#9FC700")});
            mSchart.cmdFill(mJcharts);
        }
        mSchart.postInvalidate();
    }
}

