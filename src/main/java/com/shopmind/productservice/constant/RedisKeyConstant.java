package com.shopmind.productservice.constant;

/**
 * Redis Key 常量
 * 
 * @author hcy18
 * @date 2025-12-31
 */
public class RedisKeyConstant {
    
    /**
     * 新品。是一组 List<Long> 商品 id
     */
    public static final String PRODUCT_NEW = "product:new";

    
    /**
     * 商品浏览量 Hash - field: productId, value: 浏览量
     */
    public static final String PRODUCT_VIEW_PREFIX = "product:view:";
    
    /**
     * 热门商品列表（游客） - List<Long>（商品ID列表）
     */
    public static final String HOT_PRODUCTS_GUEST = "hot_products:guest";
    

}

