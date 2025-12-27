package com.shopmind.productservice.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopmind.productservice.dto.response.ProductAuditResponseDto;
import com.shopmind.productservice.entity.ProductAudit;
import com.shopmind.productservice.service.ProductAuditService;
import com.shopmind.productservice.mapper.ProductAuditMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author hcy18
* @description 针对表【sm_product_audits(商品审核记录表)】的数据库操作Service实现
* @createDate 2025-12-21 15:40:09
*/
@Service
public class ProductAuditServiceImpl extends ServiceImpl<ProductAuditMapper, ProductAudit> implements ProductAuditService{
    @Override
    public Map<Long, ProductAuditResponseDto> getLatestAuditBatchByProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Map.of();
        }

        List<ProductAudit> latestList = baseMapper.selectLatestByProductIds(productIds);

        return latestList.stream()
                .collect(Collectors.toMap(
                        ProductAudit::getProductId,
                        this::convertToResponseDto,
                        (v1, v2) -> v1 // 冲突时保留第一个（理论上不会有重复 product_id）
                ));
    }

    private ProductAuditResponseDto convertToResponseDto(ProductAudit audit) {
        if (audit == null) {
            return null;
        }

        return ProductAuditResponseDto.builder()
                .auditStatus(audit.getAuditStatus())
                .titleCheckResult(audit.getTitleCheckResult())
                .imageCheckResults(audit.getImageCheckResults())
                .rejectReason(audit.getRejectReason())
                .suggestions(audit.getSuggestions())
                .auditTime(audit.getCreatedAt())
                .build();
    }
}




