package com.shopmind.productservice.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 商品标签关联表
 * @TableName sm_product_tag_relations
 */
@Data
@TableName(value ="sm_product_tag_relations")
public class ProductTagRelation {
    /**
     * 关联ID
     */
    @TableId
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 创建时间
     */
    private Date createdAt;

}