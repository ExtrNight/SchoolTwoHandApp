package com.school.twohand.utils;

import java.util.HashMap;
import java.util.Map;

/** 根据两点的经纬度计算出两点间距离
 * Created by yang on 2016/10/20 0020.
 */
public class MapDistance {
    private static double EARTH_RADIUS = 6371.393; //地球平均半径

    /**
     * 根据给定的两个经纬度计算两地之间的距离，单位km
     * @param lon1  经度1
     * @param lat1  纬度1
     * @param lon2  经度2
     * @param lat2  纬度2
     * @return  两地距离
     */
    public static double getDistance(double lon1, double lat1, double lon2, double lat2) {
        double x = changeToRad(lon1);
        double y = changeToRad(lat1);
        double a = changeToRad(lon2);
        double b = changeToRad(lat2);
        double rad = Math.acos(Math.cos(y) * Math.cos(b) * Math.cos(x - a) + Math.sin(y) * Math.sin(b));
        if (rad > Math.PI)
            rad = Math.PI * 2 - rad;
        return EARTH_RADIUS * rad;
    }

    /**
     * 将角度转化为弧度
     * @param angle 角度
     * @return  弧度
     */
    public static double changeToRad(double angle) {
        return angle / 180 * Math.PI;
    }

//    public static void main(String[] args) {
//        System.out.println(getDistance(151, -33, 120, 30));
//    }
}
