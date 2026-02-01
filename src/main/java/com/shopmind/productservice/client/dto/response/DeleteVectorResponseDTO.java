package com.shopmind.productservice.client.dto.response;

import lombok.Data;

import java.util.List;

/**
 * Description: 批量删除商品向量-响应体
 * Author: huangcy
 * Date: 2026-02-01
 */
@Data
public class DeleteVectorResponseDTO {
    private Integer successCount;
    private List<Long> successIds;
    private List<Long> failedIds;
    private String errorMessage;
}
