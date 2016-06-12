package com.jonas.schart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jonas.schart.chart.AniLineChar;
import com.jonas.schart.chart.PieChart;
import com.jonas.schart.chartbean.JExcel;
import com.jonas.schart.superi.SuperChart;

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
        JExcel hotbody = new JExcel(50, Color.parseColor("#73c0fd"));
        JExcel burn = new JExcel(50, Color.parseColor("#b8e986"));
        JExcel anaerobic = new JExcel(50, Color.parseColor("#f7eb57"));
        JExcel aerobic = new JExcel(50, Color.parseColor("#ffbf55"));
        JExcel limit = new JExcel(50, Color.RED);
        if (pieChart != null) {
            pieChart.setPieWidth(35);
//            pieChart.setInterval(10);
            pieChart.cmdFill(hotbody, burn, anaerobic, aerobic, limit);
        }
        mLineChar = (AniLineChar) findViewById(R.id.sug_recode_line);

        List<JExcel> lines = new ArrayList<>();
        for (int i = 0; i < 20; i++) {

            lines.add(new JExcel(new SecureRandom().nextInt(50)+15, Color.parseColor("#b8e986")));
        }
        lines.get(3).setUpper(100);
        mLineChar.setYaxisValues(0, 100, 4);
//        mLineChar.setScrollAble(true);
        mLineChar.setVisibleNums(10);
//        mLineChar.setSelectedMode(SuperChart.SelectedMode.selecetdMsgShow_Top);
        mLineChar.setLineShowStyle(SuperChart.LineShowStyle.LINESHOW_SECTION);
        mLineChar.setLineStyle(AniLineChar.LineStyle.LINE_BROKEN);
        mLineChar.setShaderColors(Color.RED, Color.parseColor("#ffbf55"), Color.parseColor("#f7eb57"), Color.parseColor("#b8e986"), Color.parseColor("#73c0fd"));
        mLineChar.cmdFill(lines);
    }

    public void clicked(View v){
        mLineChar.aniShowChar_growing();
    }

}
