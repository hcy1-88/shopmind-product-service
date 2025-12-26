package com.shopmind.productservice.dto.response;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * Description:
 * Author: huangcy
 * Date: 2025-12-26
 */
@Data
public class CategoryResponseDto {
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
}
