package com.shopmind.productservice.dto.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductTagDTO {
    private Long productId;
    private String tagName;
    private String tagColor;
}