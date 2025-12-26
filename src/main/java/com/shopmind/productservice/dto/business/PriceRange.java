package com.shopmind.productservice.dto.business;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description: 价格范围
 * Author: huangcy
 * Date: 2025-12-26
 */
@Data
public class PriceRange {
    @DecimalMin(value = "0.01", message = "最小价格不能小于0.01")
    private BigDecimal min;
    private BigDecimal max;
}
