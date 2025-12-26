package com.shopmind.productservice.utils;

/**
 * Description: 地图坐标系转换
 * Author: huangcy
 * Date: 2025-12-20
 */
public class GeoCodingUtil {
    private static final double X_PI = 3.14159265358979324 * 3000.0 / 180.0;
    private static final double PI = 3.1415926535897932384626;
    private static final double A = 6378245.0;
    private static final double EE = 0.00669342162296594323;


    /**
     * WGS84坐标系转换成腾讯地图坐标系(GCJ02)
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @return double[]
     */
    public static double[] wgs84ToGcj02(double longitude, double latitude) {
        if (outOfChina(longitude, latitude)) {
            return new double[]{longitude, latitude};
        }
        double dLat = transformLat(longitude - 105.0, latitude - 35.0);
        double dLon = transformLng(longitude - 105.0, latitude - 35.0);
        double radLat = latitude / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A / sqrtMagic) * Math.PI);
        dLon = (dLon * 180.0) / ((A / (magic * sqrtMagic)) * Math.PI);
        double mgLat = latitude + dLat;
        double mgLon = longitude + dLon;
        return new double[]{mgLon, mgLat};
    }


    /**
     * WGS84坐标系转百度坐标系(BD09)
     *
     * @param wgs84Lng WGS84 经度
     * @param wgs84Lat WGS84 纬度
     * @return 百度坐标数组
     */
    public static double[] wgs84ToBd09(double wgs84Lng, double wgs84Lat) {
        double[] gcj02 = wgs84ToGcj02(wgs84Lng, wgs84Lat);
        double[] bd09 = gcj02ToBd09(gcj02[0], gcj02[1]);
        return bd09;
    }


    /**
     * 百度坐标系(BD09)转腾讯地图坐标系(GCJ02)
     *
     * @param bd_lon 百度经度
     * @param bd_lat 百度纬度
     * @return 火星坐标数组
     */
    public static double[] bd09ToGcj02(double bd_lon, double bd_lat) {
        double x = bd_lon - 0.0065;
        double y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        double gg_lng = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new double[]{gg_lng, gg_lat};
    }

    /**
     * 百度坐标(BD09)系转换成WGS84坐标系
     *
     * @param bdLng 经度
     * @param bdLat 纬度
     * @return double[] 经度，纬度
     */
    public static double[] bd09ToWgs84(double bdLng, double bdLat) {
        double[] gcj02 = bd09ToGcj02(bdLng, bdLat);
        double[] wgs84 = gcj02ToWgs84(gcj02[0], gcj02[1]);
        return wgs84;
    }

    /**
     * 腾讯地图坐标系(GCJ02)转换成WGS84坐标系
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @return double[]
     */
    public static double[] gcj02ToWgs84(double longitude, double latitude) {
        if (outOfChina(longitude, latitude)) {
            return new double[]{longitude, latitude};
        }
        double dLat = transformLat(longitude - 105.0, latitude - 35.0);
        double dLon = transformLng(longitude - 105.0, latitude - 35.0);
        double radLat = latitude / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A / sqrtMagic) * Math.PI);
        dLon = (dLon * 180.0) / ((A / (magic * sqrtMagic)) * Math.PI);
        double mgLat = latitude + dLat;
        double mgLon = longitude + dLon;
        double wgsLat = latitude + (latitude - mgLat);
        double wgsLon = longitude + (longitude - mgLon);
        return new double[]{wgsLon, wgsLat};
    }


    /**
     * 腾讯地图坐标系(GCJ02)转百度坐标系(BD09)
     *
     * @param gcj_lng 火星坐标系经度
     * @param gcj_lat 火星坐标系纬度
     * @return 百度坐标数组
     */
    public static double[] gcj02ToBd09(double gcj_lng, double gcj_lat) {
        double x = gcj_lng, y = gcj_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * X_PI);
        double bd_lng = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new double[]{bd_lng, bd_lat};
    }


    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLng(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    private static boolean outOfChina(double lng, double lat) {
        return (lng < 72.004 || lng > 137.8347) || (lat < 0.8293 || lat > 55.8271);
    }

}
