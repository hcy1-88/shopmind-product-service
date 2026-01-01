package com.shopmind.productservice.dto.request;

import lombok.Data;

import java.util.List;

/**
 * Description:
 * Author: huangcy
 * Date: 2026-01-01
 */
@Data
public class ProductGettingRequestDTO {
    private List<Long> ids;
}
