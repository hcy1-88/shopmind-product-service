package com.shopmind.productservice.mapper;

import com.shopmind.productservice.entity.ProductAudit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author hcy18
* @description 针对表【sm_product_audits(商品审核记录表)】的数据库操作Mapper
* @createDate 2025-12-21 15:40:09
* @Entity com.shopmind.productservice.entity.ProductAudit
*/
public interface ProductAuditMapper extends BaseMapper<ProductAudit> {
    /**
     * 批量查询每个商品的最新审核记录（PostgreSQL 专用）
     */
    List<ProductAudit> selectLatestByProductIds(@Param("productIds") List<Long> productIds);
}




