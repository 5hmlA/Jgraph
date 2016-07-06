package com.jonas.jdiagram.inter;

import com.jonas.jdiagram.models.Jchart;

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
    public void cmdFill(Jchart... jcharts);


    /**
     * 传入 数据
     */
    public void cmdFill(List<Jchart> jchartList);

}
