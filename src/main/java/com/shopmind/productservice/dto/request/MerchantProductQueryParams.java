package com.shopmind.productservice.dto.request;

import lombok.Data;

/**
 * Description: 商家查询商品
 * Author: huangcy
 * Date: 2025-12-31
 */
@Data
public class MerchantProductQueryParams {
    private Integer pageNumber;
    private Integer pageSize;
    // 商品状态：draft/pending_review/approved/rejected
    private String status;
    private String keyword;
}
