package com.shopmind.productservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 向量化请求 DTO
 * Author: huangcy
 * Date: 2025-12-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VectorizeProductRequestDto {
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
     * AI 摘要
     */
    private String aiSummary;

    /**
     * 商品标签列表
     */
    private List<String> tags;

    /**
     * 商品分类 ID
     */
    private Long categoryId;
}

