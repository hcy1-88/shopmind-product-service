package com.shopmind.productservice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.shopmind.productservice.client.dto.response.ProductAuditResponseDto;
import com.shopmind.productservice.enums.AuditType;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 商品审核记录表
 * @TableName sm_product_audits
 */
@Data
@TableName(value ="sm_product_audits", autoResultMap = true)
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
    private AuditType auditType;

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
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ProductAuditResponseDto.CheckResult titleCheckResult;

    /**
     * 图片审核结果数组
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<ProductAuditResponseDto.ImageCheckResult> imageCheckResults;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 修改建议
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> suggestions;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;


}