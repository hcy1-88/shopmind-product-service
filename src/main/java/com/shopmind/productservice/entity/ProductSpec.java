package com.shopmind.productservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.shopmind.productservice.dto.business.SkuSpecValue;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * SKU规格定义表
 * @TableName sm_product_specs
 */
@Data
@TableName(value ="sm_product_specs")
public class ProductSpec {
    /**
     * 规格ID
     */
    @TableId
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 规格名称（如：颜色、内存）
     */
    private String specName;

    /**
     * 规格值数组 [{"value": "红色", "image": "url"}]
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<SkuSpecValue> specValues;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;


}