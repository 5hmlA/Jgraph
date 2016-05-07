package com.jonas.schart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;

import com.jonas.schart.chart.NExcel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private SeekBar mSbBarwidth;
    private SeekBar mSbBarinterval;
    private SeekBar mSbHcoordinate;
    private NChart mChart;
    private boolean style_bar = true;
    private SeekBar mSbmabove;

    private void assignViews(){
        mSbBarwidth = (SeekBar)findViewById(R.id.sb_barwidth);
        mSbBarinterval = (SeekBar)findViewById(R.id.sb_barinterval);
        mSbHcoordinate = (SeekBar)findViewById(R.id.sb_hcoordinate);
        mSbmabove = (SeekBar)findViewById(R.id.sb_mabove);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChart = (NChart)findViewById(R.id.sug_recode_schar);
        List<NExcel> nExcelList = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i<8; i++) {
            nExcelList.add(new NExcel(80+random.nextInt(100), "测试"));
        }
        nExcelList.add(new NExcel(99, 150, "测试"));

//        Color.parseColor("#ffe9d1ba");
//        mChart.setScrollAble(false);
//        mChart.setFixedWidth(30);
        mChart.setBarStanded(7);
//        mChart.setExecelPaintShaderColors(new int[]{Color.parseColor("#089900"), Color.parseColor("#9FC700")});
//        mChart.setExecelPaintShaderColors(new int[]{Color.parseColor("#4df1dbd4"), Color.TRANSPARENT});
        mChart.setNormalColor(Color.parseColor("#089900"));
        mChart.cmdFill(nExcelList);
        assignViews();
        setListeners();
    }

    private void setListeners(){
        mSbBarwidth.setOnSeekBarChangeListener(this);
        mSbBarwidth.setProgress((int)mChart.getBarWidth());
        mSbBarinterval.setOnSeekBarChangeListener(this);
        mSbBarinterval.setProgress((int)mChart.getInterval());
        mSbHcoordinate.setOnSeekBarChangeListener(this);
        mSbHcoordinate.setProgress((int)mChart.getHCoordinate());
        mSbmabove.setOnSeekBarChangeListener(this);
        mSbmabove.setProgress((int)mChart.getAbove());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
        switch(seekBar.getId()) {
            case R.id.sb_barwidth:
                mChart.setBarWidth(progress);
                break;
            case R.id.sb_barinterval:
                mChart.setInterval(progress);
                break;
            case R.id.sb_hcoordinate:
                mChart.setHCoordinate(progress);
                break;
            case R.id.sb_mabove:
                mChart.setAbove(progress);
                break;
        }
        mChart.postInvalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar){

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar){

    }

    public void stylechange(View v){
        mChart.setChartStyle(( style_bar = !style_bar ) ? NChart.ChartStyle.BAR : NChart.ChartStyle.LINE);
        mChart.postInvalidate();
    }

    public void animate(View v){
        mChart.animateShow();
    }

    public void selecterChange(View v){
        if (mChart.getSelectedModed()== NChart.SelectedMode.selecetdMsgShow) {
            mChart.setSelectedModed(NChart.SelectedMode.selectedActivated);
        } else {
            mChart.setSelectedModed(NChart.SelectedMode.selecetdMsgShow);
        }
    }

}
