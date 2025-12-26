package com.shopmind.productservice.dto.business;

/**
 * 百度坐标系经纬度
 */
public class BaiduCoordinate {
    private final double latitude;   // 纬度 (BD-09)
    private final double longitude;  // 经度 (BD-09)

    public BaiduCoordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    @Override
    public String toString() {
        return String.format("BaiduCoordinate{lng=%.6f, lat=%.6f}", longitude, latitude);
    }
}