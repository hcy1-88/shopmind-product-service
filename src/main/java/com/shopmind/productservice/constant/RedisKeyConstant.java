package com.shopmind.productservice.constant;

/**
 * Redis Key 常量
 * 
 * @author hcy18
 * @date 2025-12-31
 */
public class RedisKeyConstant {
    
    /**
     * 商品新鲜度 ZSet - member: productId, score: timestamp
     */
    public static final String PRODUCT_NEW = "product:new";
    
    /**
     * 商品销量 Hash - field: productId, value: 销量
     */
    public static final String PRODUCT_ORDER_PREFIX = "product:order:";
    
    /**
     * 商品浏览量 Hash - field: productId, value: 浏览量
     */
    public static final String PRODUCT_VIEW_PREFIX = "product:view:";
    
    /**
     * 热门商品列表（游客） - List<Long>（商品ID列表）
     */
    public static final String HOT_PRODUCTS_GUEST = "hot_products:guest";
    
    /**
     * 热门商品分数 ZSet - member: productId, score: 热度分数
     * 用于临时存储计算出的热度分数
     */
    public static final String HOT_PRODUCTS_SCORE = "hot_products:score";
}

