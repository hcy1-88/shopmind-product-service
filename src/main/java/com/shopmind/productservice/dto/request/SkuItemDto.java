package com.shopmind.productservice.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Description:
 * Author: huangcy
 * Date: 2025-12-26
 */
@Data
public class SkuItemDto {
    // 图片 URL 或 Base64 数据
    private String image;
    // 价格
    private BigDecimal price;
    // 规格，如 {"颜色": "星光色", "存储": "512GB"}
    private Map<String, String> specs;
    // 库存数量
    private Integer stock;
}