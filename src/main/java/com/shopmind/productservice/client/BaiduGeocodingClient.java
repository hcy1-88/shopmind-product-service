package com.shopmind.productservice.client;

import cn.hutool.core.util.StrUtil;
import com.shopmind.productservice.dto.business.BaiduCoordinate;
import com.shopmind.productservice.exception.ProductServiceException;
import com.shopmind.productservice.properties.BaiduProperties;
import com.shopmind.productservice.utils.GeoCodingUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 百度地理编码 HTTP 客户端（仅返回原始 BD-09 坐标）
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "shopmind.baidu.open-map", name = "enabled")
public class BaiduGeocodingClient {

    @Resource
    private BaiduProperties bdProperties;

    /**
     * 调用百度地理编码 API，返回 BD-09 坐标
     *
     * @param address 详细地址
     * @param city    城市（可选）
     * @return BaiduCoordinate 或 null（失败时）
     */
    public BaiduCoordinate geocode(String address, String city) {
        if (address == null || address.trim().isEmpty()) {
            return null;
        }
        String url = null;
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("address", address.trim());
            if (city != null && !city.trim().isEmpty()) {
                params.add("city", city.trim());
            }
            params.add("ak", bdProperties.getAk());
            params.add("output", "json");

            url = UriComponentsBuilder.fromHttpUrl(bdProperties.getOpenMap().getGeoCodingUrl())
                    .queryParams(params)
                    .build()
                    .toUriString();

            String response = sendGet(url);
            return parseBaiduResponse(response);

        } catch (Exception e) {
            log.error("地理位置编码请求异常，url:{}, address:{}, city:{}", url, address, city, e);
            throw new ProductServiceException("PRODUCT0001");
        }
    }

    /**
     * 根据 省名、市名、地区名、详细地址 ，生成 Point 经纬度坐标点
     * @return Point
     */
    public Point createAddressLocation(String provinceName,String cityName, String districtName, String detailAddress) {
        // 城市名是获取经纬度的兜底保障，详细地址未必会包含城市名
        if (StrUtil.isEmpty(cityName)){
            throw  new ProductServiceException("PRODUCT0003");
        }
        // 1. 拼接地址
        String fullAddr = provinceName + cityName +
                districtName + detailAddress;

        // 2. 调用百度获取原始坐标（BD-09）
        BaiduCoordinate bd09 = this.geocode(fullAddr, cityName);

        // 3. 显式转换为 WGS84
        double[] wgs84 = GeoCodingUtil.bd09ToWgs84(bd09.getLongitude(), bd09.getLongitude());

        // 4. 存入 PostGIS
        return new GeometryFactory()
                .createPoint(new Coordinate(wgs84[0], wgs84[1]));
    }


    private String sendGet(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } finally {
            conn.disconnect();
        }
    }

    // 使用正则解析 JSON（避免引入 Jackson/Gson）
    private BaiduCoordinate parseBaiduResponse(String json) {
        if (json == null) return null;

        // 检查 status
        Pattern statusPattern = Pattern.compile("\"status\"\\s*:\\s*(\\d+)");
        Matcher statusMatcher = statusPattern.matcher(json);
        if (!statusMatcher.find() || !"0".equals(statusMatcher.group(1))) {
            throw new IllegalStateException("百度API返回非成功状态: " + json);
        }

        // 提取 lng/lat
        Pattern lngPattern = Pattern.compile("\"lng\"\\s*:\\s*([+-]?\\d*\\.?\\d+)");
        Pattern latPattern = Pattern.compile("\"lat\"\\s*:\\s*([+-]?\\d*\\.?\\d+)");

        Matcher lngMatcher = lngPattern.matcher(json);
        Matcher latMatcher = latPattern.matcher(json);

        if (lngMatcher.find() && latMatcher.find()) {
            double lng = Double.parseDouble(lngMatcher.group(1));
            double lat = Double.parseDouble(latMatcher.group(1));
            return new BaiduCoordinate(lat, lng); // 注意：构造函数参数顺序是 (lat, lng)
        }

        return null;
    }
}