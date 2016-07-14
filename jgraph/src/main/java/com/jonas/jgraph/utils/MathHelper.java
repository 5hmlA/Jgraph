package com.jonas.jgraph.utils;

/**
 * @author jiangzuyun.
 * @date 2016/7/14
 * @des [一句话描述]
 * @since [产品/模版版本]
 */


public class MathHelper {


    /**
     * @param num
     * @return 最接近num的数 这个数同时是5的倍数
     */
    public static int getRound5(float num) {
        return ((int) (num + 2.5)) / 5 * 5;
    }

    /**
     * @param num
     * @return 根据num向上取数 这个数同时是5的倍数
     */
    public static int getCeil5(float num) {
        return ((int) (num + 4.9999999)) / 5 * 5;
    }

    /**
     * 向上取数
     * @param num
     * @return
     */
    public static int getCeil10(float num) {
        return ((int) (num + 9.9999999)) / 10 * 10;
    }

    /**
     * 向下取数
     * @param num
     * @return
     */
    public static int getRound10(float num) {
        return ((int) (num + 5)) / 10 * 10;
    }

    public static int getCast10(float num) {
        return ((int) (num)) / 10 * 10;
    }
}
