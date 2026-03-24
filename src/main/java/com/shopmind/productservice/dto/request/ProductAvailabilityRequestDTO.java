package com.shopmind.productservice.dto.request;

import lombok.Data;

import java.util.List;

/**
 * 批量检测商品可用性请求DTO
 */
@Data
public class ProductAvailabilityRequestDTO {
    private List<Long> productIds;
}