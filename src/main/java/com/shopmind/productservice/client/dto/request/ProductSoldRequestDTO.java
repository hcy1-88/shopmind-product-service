package com.shopmind.productservice.client.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Description: 下单时，订单服务调商品服务减库存、加销量
 * Author: huangcy
 * Date: 2026-01-02
 */
@Data
public class ProductSoldRequestDTO {

    /**
     * 商品 id
     */
    @NotNull
    private Long productId;

    /**
     * 商品被售卖时，用户选的 sku id
     */
    @NotNull
    private Long skuId;

    /**
     * 购买数量
     */
    @NotNull
    @Min(1)
    private Integer quantity;

}
