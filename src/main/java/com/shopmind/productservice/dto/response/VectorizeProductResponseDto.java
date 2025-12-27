package com.shopmind.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 向量化响应 DTO
 * Author: huangcy
 * Date: 2025-12-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VectorizeProductResponseDto {
    /**
     * 商品 ID
     */
    private Long productId;

    /**
     * 向量化是否成功
     */
    private Boolean success;

    /**
     * 向量 ID（存储在向量数据库中的 ID）
     */
    private String vectorId;

    /**
     * 错误信息（如果失败）
     */
    private String errorMessage;
}

