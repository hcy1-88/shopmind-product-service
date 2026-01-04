package com.shopmind.productservice.client.dto.request;

import lombok.Data;

import java.util.List;

/**
 * Description: 语义搜索和精排请求
 * Author: huangcy
 * Date: 2026-01-04
 */
@Data
public class SearchAndRankWithSemanticsRequestDTO {
    private String keyword;
    private Integer limit;
    private List<Long> productIds;
}
