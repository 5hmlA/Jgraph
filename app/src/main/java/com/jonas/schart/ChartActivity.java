package com.jonas.schart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jonas.schart.chart.AniLineChar;
import com.jonas.schart.chart.PieChart;
import com.jonas.schart.chartbean.SugExcel;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    private AniLineChar mLineChar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        PieChart pieChart = (PieChart) findViewById(R.id.sug_record_pie);
//        SugExcel hotbody = new SugExcel(new SecureRandom().nextInt(50), 0x73c0fd);
        SugExcel hotbody = new SugExcel(50, Color.parseColor("#73c0fd"));
        SugExcel burn = new SugExcel(50, Color.parseColor("#b8e986"));
        SugExcel anaerobic = new SugExcel(50, Color.parseColor("#f7eb57"));
        SugExcel aerobic = new SugExcel(50, Color.parseColor("#ffbf55"));
        SugExcel limit = new SugExcel(50, Color.RED);
        if (pieChart != null) {
            pieChart.setPieWidth(35);
//            pieChart.setInterval(10);
            pieChart.cmdFill(hotbody, burn, anaerobic, aerobic, limit);
        }
        mLineChar = (AniLineChar) findViewById(R.id.sug_recode_line);

        List<SugExcel> lines = new ArrayList<>();
        for (int i = 0; i < 21; i++) {

            lines.add(new SugExcel(new SecureRandom().nextInt(100), Color.parseColor("#b8e986")));
        }
        mLineChar.setYaxisValues(0, 100, 4);
//        mLineChar.setScrollAble(true);
        mLineChar.setShaderColors(Color.RED, Color.parseColor("#ffbf55"), Color.parseColor("#f7eb57"), Color.parseColor("#b8e986"), Color.parseColor("#73c0fd"));
        mLineChar.cmdFill(lines);
    }

    public void clicked(View v){
        mLineChar.aniShowChar_growing();
    }

}
