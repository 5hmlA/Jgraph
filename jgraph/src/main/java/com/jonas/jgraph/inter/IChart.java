package com.jonas.jgraph.inter;

import com.jonas.jgraph.models.Jchart;

import java.util.List;

/**
 * @author yun.
 * @date 2016/6/8
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
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
