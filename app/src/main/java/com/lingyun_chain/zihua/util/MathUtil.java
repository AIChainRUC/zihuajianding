package com.lingyun_chain.zihua.util;

/**
 * author: anapodoton
 * created on: 2018/4/14 17:54
 * description:
 */
public class MathUtil {
    /**
     * 两个向量可以为任意维度，但必须保持维度相同，表示n维度中的两点
     * 欧式距离
     * @param vector1
     * @param vector2
     * @return 两点间距离
     */
    public static double sim_distance(double vector1, double vector2) {
        double distance = 0;

        double temp = Math.pow((vector1 - vector2), 2);
        distance += temp;
        distance = Math.sqrt(distance);
        return distance;
    }
}
