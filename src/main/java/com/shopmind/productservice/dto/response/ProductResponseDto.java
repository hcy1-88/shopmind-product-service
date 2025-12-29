package com.shopmind.productservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.shopmind.productservice.dto.business.PriceRange;
import com.shopmind.productservice.enums.ProductStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
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

    /**
     * 商品摘要（ai 生成）
     */
    private String aiSummary;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商家
     */
    private String merchantName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long merchantId;

    /**
     * 位置
     */
    private String location;

    /**
     * 分类 id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long category;

    /**
     * 商品的审核状态
     */
    private ProductStatus status;
    /**
     * 规格组合
     */
    private List<ProductSkuResponseDto> skus;

    /**
     * 评论
     */
    private List<ReviewDto> reviews;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 修改建议
     */
    private List<String> suggestions;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 商品标签
     */
    private List<GenerateTagsResponseDto.TagInfo> tagInfo;
}
