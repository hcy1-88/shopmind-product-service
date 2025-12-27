package com.shopmind.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * AI 商品审核响应 DTO
 * Author: huangcy
 * Date: 2025-12-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAuditResponseDto {
    /**
     * 审核状态：approved/rejected
     */
    private String auditStatus;

    /**
     * 标题审核结果
     */
    private CheckResult titleCheckResult;

    /**
     * 图片审核结果列表
     */
    private List<ImageCheckResult> imageCheckResults;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 修改建议
     */
    private List<String> suggestions;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 内容检查结果
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckResult {
        /**
         * 是否通过
         */
        private Boolean passed;

        /**
         * 检测到的问题类型：如 sensitive_words/illegal_content/spam
         */
        private List<String> issueTypes;

        /**
         * 置信度分数 0-1
         */
        private Double confidence;

        /**
         * 详细说明
         */
        private String detail;
    }

    /**
     * 图片审核结果
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageCheckResult {
        /**
         * 图片 URL
         */
        private String imageUrl;

        /**
         * 是否通过
         */
        private Boolean passed;

        /**
         * 检测到的问题类型
         */
        private List<String> issueTypes;

        /**
         * 置信度分数 0-1
         */
        private Double confidence;

        /**
         * 详细说明
         */
        private String detail;
    }
}

