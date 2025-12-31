package com.shopmind.productservice.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 生成摘要响应 DTO
 * Author: huangcy
 * Date: 2025-12-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateSummaryResponseDto {
    /**
     * AI 生成的商品摘要
     */
    private String summary;

}

