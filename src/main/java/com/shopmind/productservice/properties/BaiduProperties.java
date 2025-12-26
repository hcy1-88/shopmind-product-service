package com.shopmind.productservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Description: 百度地图客户端配置
 * Author: huangcy
 * Date: 2025-12-20
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "shopmind.baidu")
public class BaiduProperties {
    private String ak;
    private BaiduMap openMap;

    @Data
    public static class BaiduMap {
        private boolean enabled = false;
        private String geoCodingUrl;
    }
}
