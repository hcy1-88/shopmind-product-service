package com.shopmind.productservice.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 商品分类表
 * @TableName sm_products_categories
 */
@Data
@TableName(value ="sm_products_categories")
public class ProductCategory {
    /**
     * 分类ID
     */
    @TableId
    private Long id;

    /**
     * 分类编码
     */
    private String code;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类层级
     */
    private Integer level;

    /**
     * 排序权重
     */
    private Integer sortOrder;

    /**
     * 分类图标URL
     */
    private String icon;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

}