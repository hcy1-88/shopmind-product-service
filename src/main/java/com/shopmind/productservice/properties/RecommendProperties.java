package com.shopmind.productservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Description: 推荐相关的属性
 * Author: huangcy
 * Date: 2026-01-05
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "shopmind.recommend")
public class RecommendProperties {

    /**
     * 从数据库里召回的数量
     */
    private Integer recallFromDB = 100;

    /**
     * 存放到缓存的热门商品数
     */
    private Integer hotLimit = 50;

    /**
     * 热门商品在缓存中的过期时间（天）
     */
    private Integer hotExpire = 30;

    /**
     * 商品新鲜度权重 （用户计算热门程度）
     */
    private Double freshWeight = 0.3;

    /**
     * 商品销量权重 （用户计算热门程度）
     */
    private Double salesWeight = 0.5;

    /**
     * 浏览量权重（用户计算热门程度）
     */
    private Double viewsWeight = 0.2;

    /**
     * 存放到缓存的新品数
     */
    private Integer newLimit = 50;

    /**
     * 新品的时间窗口（天）， 例如 30 天以内创建的才叫新品
     */
    private Integer freshWindow = 30;

    /**
     * 新品的缓存时间 （天）
     */
    private Integer newProductExpire = 30;

    /**
     * 商品浏览量的缓存时间（天）
     */
    private Integer viewExpire = 30;
}
