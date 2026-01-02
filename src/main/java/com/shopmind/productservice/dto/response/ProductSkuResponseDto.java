package com.shopmind.productservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class ProductSkuResponseDto {

    @JsonFormat(shape =  JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 规格属性，如 {"颜色": "星光色", "存储": "512GB"}
     */
    private Map<String, String> attributes;

    private BigDecimal price;
    private Integer stock;
    /**
     * 图片 url
     */
    private String image;
}