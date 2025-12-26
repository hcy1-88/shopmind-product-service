package com.shopmind.productservice.dto.request;

import com.shopmind.productservice.dto.business.SkuSpecValue;
import lombok.Data;

import java.util.List;

/**
 * Description:
 * Author: huangcy
 * Date: 2025-12-26
 */
@Data
public class SkuSpecDto {
    /**
     * 规格名称（如：颜色、内存）
     */
    private String name;

    /**
     * 规格值数组 [{"value": "红色", "image": "url"}]
     */
    private List<SkuSpecValue> values;
}
