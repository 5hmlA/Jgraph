package com.jonas.schart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jonas.jdiagram.graph.JcoolGraph;
import com.jonas.jdiagram.models.Jchart;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    private JcoolGraph mLineChar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        mLineChar = (JcoolGraph) findViewById(R.id.sug_recode_line);

        List<Jchart> lines = new ArrayList<>();
        for (int i = 0; i < 18; i++) {

            lines.add(new Jchart(new SecureRandom().nextInt(50)+15, Color.parseColor("#b8e986")));
        }
//        lines.get(3).setUpper(100);
//        mLineChar.setScrollAble(true);
        mLineChar.setVisibleNums(10);
//        mLineChar.setYaxisValues(20, 80, 5);
//        mLineChar.setYaxisValues("test","测试","text");
//        mLineChar.setSelectedMode(BaseGraph.SelectedMode.selecetdMsgShow_Top);
//        mLineChar.setLineShowStyle(SuperGraph.LineShowStyle.LINESHOW_FROMLINE);
        mLineChar.setLineShowStyle(JcoolGraph.LineShowStyle.LINESHOW_FROMLINE);
        mLineChar.setLineStyle(JcoolGraph.LineStyle.LINE_CURVE);
//        mLineChar.setLineStyle(JcoolGraph.LineStyle.LINE_BROKEN);
//        mLineChar.setShaderAreaColors(Color.parseColor("#4B494B"),Color.TRANSPARENT);
        mLineChar.setShaderAreaColors(Color.parseColor("#4B494B"),Color.TRANSPARENT);
//        mLineChar.setLineShaderColors(Color.BLUE,Color.TRANSPARENT);
//        mLineChar.setLineShaderColors(Color.RED, Color.parseColor("#ffbf55"), Color.parseColor("#f7eb57"), Color.parseColor("#b8e986"), Color.parseColor("#73c0fd"));
//        mLineChar.setLineShaderColors(0xff059800, 0xffa0c700, 0xf7eb57, 0xb8e986);
//        mLineChar.setLineShaderColors(Color.parseColor("#80ff3320"), Color.parseColor("#ffbf55"), Color.parseColor("#f7eb57"), Color.parseColor("#b8e986"), Color.parseColor("#73c0fd"));
//        mLineChar.setNormalColor(Color.parseColor("#676567"));
//        mLineChar.setShowFromMode(JcoolGraph.ShowFromMode.SHOWFROMBUTTOM);
        mLineChar.cmdFill(lines);
    }

    public void clicked(View v){
        mLineChar.aniShow_growing();
    }

    public void changedata(View v){
        List<Jchart> lines = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            lines.add(new Jchart(new SecureRandom().nextInt(150)+15, 0xb8e986));
        }
        mLineChar.aniChangeData(lines);

    }
}
