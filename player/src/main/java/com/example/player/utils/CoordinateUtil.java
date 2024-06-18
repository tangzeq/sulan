package com.example.player.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 坐标工具
 */
public class CoordinateUtil {

    static class Point {
        float x;
        float y;
        float z;
    }

    /**
     * 计算两个点的距离
     * @param point1
     * @param point2
     * @return
     */
    public static double distance(Point point1,Point point2){
        return Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2) + Math.pow(point1.z - point2.z, 2));
    }

    /**
     * 从一堆坐标点中生成由 num 个点 组成的 平面几何
     * 采集的坐标点时 平面等距时结果最优
     * @param points
     * @param num 平面顶点数
     * @return
     */
    public static List<List<Point>> pointToSurface(List<Point> points,int num) {
        Map<Point,Map<Point,Double>> pointMap = new HashMap<>();
        for (Point i : points) {
            pointMap.put(i,new HashMap<>());
            for (Point j : points) {
                pointMap.get(i).put(j,distance(i,j));
            }
        }
        List<List<Point>> face = new ArrayList<>();
        return face;
    }
}
