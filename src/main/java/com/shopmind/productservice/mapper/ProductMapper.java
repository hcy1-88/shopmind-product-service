package com.shopmind.productservice.mapper;

import com.shopmind.productservice.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author hcy18
* @description 针对表【sm_products(商品表)】的数据库操作Mapper
* @createDate 2025-12-21 15:40:09
* @Entity com.shopmind.productservice.entity.Product
*/
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 增加销量
     * @param productId 商品 id
     * @param quantity 数量
     */
    void incrementProductSalesCount(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     *
     * @param tsQueryStr tsquery 全文搜索
     * @param originalKeyword 搜索词
     * @param limit top k
     * @return product id , 按词频排名
     */
    List<Long> searchProductIdsByEnhancedKeywords(@Param("tsQueryStr") String tsQueryStr,
                                                  @Param("originalKeyword") String originalKeyword,
                                                  @Param("limit") int limit);
}




