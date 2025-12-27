package com.shopmind.productservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 生成标签请求 DTO
 * Author: huangcy
 * Date: 2025-12-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateTagsRequestDto {
    /**
     * 商品 ID
     */
    private Long productId;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品分类 ID
     */
    private Long categoryId;

    /**
     * 图片 URL 列表（可选，用于图像识别）
     */
    private List<String> imageUrls;
}

