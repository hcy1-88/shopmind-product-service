package com.shopmind.productservice.service;

import com.shopmind.productservice.client.dto.response.ProductAuditResponseDto;
import com.shopmind.productservice.entity.ProductAudit;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author hcy18
* @description 针对表【sm_product_audits(商品审核记录表)】的数据库操作Service
* @createDate 2025-12-21 15:40:09
*/
public interface ProductAuditService extends IService<ProductAudit> {
    /**
     * 批量查询 商品的最后一条审核记录
     * @param productIds 商品 id 数组
     * @return 结果 key 是 product id，value 是审核结果
     */
    Map<Long, ProductAuditResponseDto> getLatestAuditBatchByProductIds(List<Long> productIds);
}
