package com.shopmind.productservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 评论功能暂时不做 TODO
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewDto {
    private String id;
    private String userId;
    private String userName;
    private String userAvatar;
    private Integer rating;                 // 星级（1~5）
    private String content;
    private Date createdAt;
    private List<String> aiTags;            // AI 提取的标签，如 ["屏幕清晰", "续航强"]
}