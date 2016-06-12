package com.jonas.schart.superi;

import com.jonas.schart.chartbean.JExcel;

import java.util.List;

/**
 * @Author jwx338756
 * @Date: 2016
 * @Description: 折线+虚线
 * @Others: {https://github.com/mychoices}
 */
public interface IChart {

    /**
     * 传入 数据
     */
    public void cmdFill(JExcel... jExcels);


    /**
     * 传入 数据
     */
    public void cmdFill(List<JExcel> jExcelList);

}
