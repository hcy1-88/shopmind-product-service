package com.shopmind.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 生成标签响应 DTO
 * Author: huangcy
 * Date: 2025-12-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateTagsResponseDto {
    /**
     * 生成的标签列表
     */
    private List<TagInfo> tags;

    /**
     * 标签信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagInfo {
        /**
         * 标签名称
         */
        private String name;

        /**
         * 推荐的颜色（UI 显示用）
         */
        private String color;

        /**
         * 相关性评分 0-1
         */
        private Double relevanceScore;
    }
}

