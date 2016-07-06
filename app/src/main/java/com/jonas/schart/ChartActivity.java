package com.jonas.schart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jonas.jdiagram.graph.JcoolGraph;
import com.jonas.jdiagram.graph.PieGraph;
import com.jonas.jdiagram.models.Jchart;
import com.jonas.jdiagram.inter.SuperGraph;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    private JcoolGraph mLineChar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        PieGraph pieChart = (PieGraph) findViewById(R.id.sug_record_pie);
        Jchart hotbody = new Jchart(50, Color.parseColor("#73c0fd"));
        Jchart burn = new Jchart(50, Color.parseColor("#b8e986"));
        Jchart anaerobic = new Jchart(50, Color.parseColor("#f7eb57"));
        Jchart aerobic = new Jchart(50, Color.parseColor("#ffbf55"));
        Jchart limit = new Jchart(50, Color.RED);
        if (pieChart != null) {
            pieChart.setPieWidth(35);
//            pieChart.setInterval(10);
            pieChart.cmdFill(hotbody, burn, anaerobic, aerobic, limit);
        }
        mLineChar = (JcoolGraph) findViewById(R.id.sug_recode_line);

        List<Jchart> lines = new ArrayList<>();
        for (int i = 0; i < 20; i++) {

            lines.add(new Jchart(new SecureRandom().nextInt(50)+15, Color.parseColor("#b8e986")));
        }
//        lines.get(3).setUpper(100);
//        mLineChar.setYaxisValues(0, 100, 4);
//        mLineChar.setScrollAble(true);
        mLineChar.setVisibleNums(10);
//        mLineChar.setSelectedMode(SuperChart.SelectedMode.selecetdMsgShow_Top);
        mLineChar.setLineShowStyle(SuperGraph.LineShowStyle.LINESHOW_FROMLINE);
        mLineChar.setLineStyle(JcoolGraph.LineStyle.LINE_CURVE);
        mLineChar.setShaderAreaColors(Color.GRAY,Color.TRANSPARENT);
//        mLineChar.setLineShaderColors(Color.RED, Color.parseColor("#ffbf55"), Color.parseColor("#f7eb57"), Color.parseColor("#b8e986"), Color.parseColor("#73c0fd"));
        mLineChar.setShowFromMode(JcoolGraph.ShowFromMode.SHOWFROMBUTTOM);
        mLineChar.cmdFill(lines);
    }

    public void clicked(View v){
        mLineChar.aniShowChar_growing();
    }

    public void changedata(View v){
        List<Jchart> lines = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            lines.add(new Jchart(new SecureRandom().nextInt(50)+15, Color.parseColor("#b8e986")));
        }
        mLineChar.aniChangeData(lines);

    }
}
