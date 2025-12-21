package com.shopmind.productservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 商品标签表
 * @TableName sm_products_tags
 */
@Data
@TableName(value ="sm_products_tags")
public class ProductsTag {
    /**
     * 标签ID
     */
    @TableId
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签类型：ai_generated/manual
     */
    private String type;

    /**
     * 标签 UI 颜色
     */
    private String color;

    /**
     * 创建时间
     */
    private Date createdAt;

}