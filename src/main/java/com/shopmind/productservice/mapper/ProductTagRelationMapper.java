package com.shopmind.productservice.mapper;

import com.shopmind.productservice.dto.business.ProductTagDTO;
import com.shopmind.productservice.entity.ProductTagRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
* @author hcy18
* @description 针对表【sm_product_tags(商品标签关联表)】的数据库操作Mapper
* @createDate 2025-12-21 15:40:09
* @Entity com.shopmind.productservice.entity.ProductTag
*/
public interface ProductTagRelationMapper extends BaseMapper<ProductTagRelation> {

    /**
     * 商品 id 和对应的 tag name 和 color
     */
    List<ProductTagDTO> selectTagsByProductIds(@Param("productIds") Collection<Long> productIds);
}




