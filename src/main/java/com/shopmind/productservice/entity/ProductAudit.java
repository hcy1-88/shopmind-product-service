package com.shopmind.productservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

/**
 * 商品审核记录表
 * @TableName sm_product_audits
 */
@TableName(value ="sm_product_audits")
public class ProductAudit {
    /**
     * 审核ID
     */
    @TableId
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 审核类型：ai/manual
     */
    private String auditType;

    /**
     * 审核状态：pending/approved/rejected
     */
    private String auditStatus;

    /**
     * 审核员ID（人工审核）
     */
    private Long auditorId;

    /**
     * 标题审核结果
     */
    private Object titleCheckResult;

    /**
     * 图片审核结果数组
     */
    private Object imageCheckResults;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 修改建议
     */
    private Object suggestions;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;


}