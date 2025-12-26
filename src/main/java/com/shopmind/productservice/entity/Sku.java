package com.shopmind.productservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * SKU表
 * @TableName sm_skus
 */
@TableName(value ="sm_skus")
@Data
public class Sku {
    /**
     * SKU ID
     */
    @TableId
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * SKU规格组合 {"颜色": "红色", "内存": "128GB"}
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> skuSpecs;

    /**
     * SKU价格
     */
    private BigDecimal price;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * SKU图片URL
     */
    private String image;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

}