package com.shopmind.productservice.client.dto.request;

import lombok.Data;

import java.util.List;

/**
 * Description:
 * Author: huangcy
 * Date: 2026-02-01
 */
@Data
public class ProductIdsRequestDTO {
    private List<Long> productIds;
}
