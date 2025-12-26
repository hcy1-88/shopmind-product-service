package com.shopmind.productservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.shopmind.productservice.dto.business.PriceRange;
import com.shopmind.productservice.enums.ProductStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Description:
 * Author: huangcy
 * Date: 2025-12-26
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 仅序列化非 null 字段
public class ProductResponseDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    private String name;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private PriceRange priceRange;

    /**
     * 预览图，封面
     */
    private String image;

    /**
     * 详情图
     */
    private List<String> images;
    private String aiSummary;
    private String description;
    private String merchantName;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long merchantId;
    private String location;
    /**
     * 分类 id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long category;
    private ProductStatus status;
    private List<ProductSkuResponseDto> skus;
    private List<ReviewDto> reviews;
}
