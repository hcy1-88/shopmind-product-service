package com.shopmind.productservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopmind.framework.id.IdGenerator;
import com.shopmind.productservice.dto.request.SkuItemDto;
import com.shopmind.productservice.dto.response.ProductSkuResponseDto;
import com.shopmind.productservice.entity.Sku;
import com.shopmind.productservice.exception.ProductServiceException;
import com.shopmind.productservice.service.ImageService;
import com.shopmind.productservice.service.SkuService;
import com.shopmind.productservice.mapper.SkuMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
* @author hcy18
* @description 针对表【sm_skus(SKU表)】的数据库操作Service实现
* @createDate 2025-12-21 15:40:09
*/
@Slf4j
@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService{

    @Resource
    private IdGenerator idGenerator;

    @Resource
    private ImageService imageService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ProductSkuResponseDto> addBatchSku(Long productId, List<SkuItemDto> skuItemDtos) {
        List<ProductSkuResponseDto> res = new ArrayList<>();
        List<Sku> skus = new ArrayList<>();
        for (SkuItemDto skuItemDto : skuItemDtos) {
            Sku sku = new Sku();
            sku.setId(idGenerator.nextId());
            sku.setProductId(productId);
            sku.setSkuSpecs(skuItemDto.getSpecs());
            sku.setPrice(skuItemDto.getPrice());
            sku.setStock(skuItemDto.getStock());
            sku.setImage(imageService.getImageUrlFromBase64(skuItemDto.getImage()));
            skus.add(sku);
        }
        this.saveBatch(skus);
        return skus.stream().map(sku -> {
            ProductSkuResponseDto resp = new ProductSkuResponseDto();
            BeanUtils.copyProperties(sku, resp);
            return resp;
        }).toList();
    }

    @Override
    public void deleteByProductId(Long productId) {
        this.lambdaUpdate()
                .eq(Sku::getProductId, productId)
                .remove();
    }

    @Override
    public List<ProductSkuResponseDto> getSkusByProductId(Long productId) {
        List<Sku> skus = this.lambdaQuery()
                .eq(Sku::getProductId, productId)
                .list();
        
        return skus.stream().map(sku -> {
            ProductSkuResponseDto resp = new ProductSkuResponseDto();
            resp.setId(sku.getId());
            resp.setStock(sku.getStock());
            resp.setAttributes(sku.getSkuSpecs());
            resp.setPrice(sku.getPrice());
            resp.setImage(sku.getImage());
            return resp;
        }).toList();
    }

    @Override
    public void deductStock(Long productId, Long skuId, Integer quantity) {
        Sku sku = this.lambdaQuery()
                .eq(Sku::getId, skuId)
                .eq(Sku::getProductId, productId)
                .one();
        if (sku == null) {
            log.error("商品 sku 不存在，减库存失败，请检查参数是否正确！product id: {} , sku id: {}", productId, skuId);
            throw new ProductServiceException("PRODUCT0007");
        }

        sku.setStock(sku.getStock() - quantity);
        this.updateById(sku);
        log.info("库存扣减成功！product id: {} , sku id: {}", productId, skuId);
    }
}




