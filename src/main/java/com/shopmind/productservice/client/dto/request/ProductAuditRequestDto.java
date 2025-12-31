package com.shopmind.productservice.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 商品审核请求 DTO
 * Author: huangcy
 * Date: 2025-12-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAuditRequestDto {
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
     * 封面图片 URL
     */
    private String coverImage;

    /**
     * 详情图片 URL 列表
     */
    private List<String> detailImages;

}

