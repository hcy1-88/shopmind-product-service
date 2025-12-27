package com.shopmind.productservice.client;

import com.shopmind.productservice.dto.request.GenerateSummaryRequestDto;
import com.shopmind.productservice.dto.request.GenerateTagsRequestDto;
import com.shopmind.productservice.dto.request.ProductAuditRequestDto;
import com.shopmind.productservice.dto.request.VectorizeProductRequestDto;
import com.shopmind.productservice.dto.response.GenerateSummaryResponseDto;
import com.shopmind.productservice.dto.response.GenerateTagsResponseDto;
import com.shopmind.productservice.dto.response.ProductAuditResponseDto;
import com.shopmind.productservice.dto.response.VectorizeProductResponseDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * AI 服务客户端 - 使用 HTTP Exchange 声明式调用
 * Author: huangcy
 * Date: 2025-12-26
 */
@HttpExchange("/ai")
public interface AIServiceClient {

    /**
     * 审核商品内容（标题、描述、图片）
     * @param requestDto 审核请求
     * @return 审核结果
     */
    @PostExchange("/audit/product")
    ProductAuditResponseDto auditProduct(@RequestBody ProductAuditRequestDto requestDto);

    /**
     * 生成商品标签
     * @param requestDto 标签生成请求
     * @return 生成的标签列表
     */
    @PostExchange("/generate/tags")
    GenerateTagsResponseDto generateTags(@RequestBody GenerateTagsRequestDto requestDto);

    /**
     * 生成商品摘要
     * @param requestDto 摘要生成请求
     * @return AI 生成的摘要
     */
    @PostExchange("/generate/summary")
    GenerateSummaryResponseDto generateSummary(@RequestBody GenerateSummaryRequestDto requestDto);

    /**
     * 商品向量化（用于相似商品推荐和搜索）
     * @param requestDto 向量化请求
     * @return 向量化结果
     */
    @PostExchange("/vectorize/product")
    VectorizeProductResponseDto vectorizeProduct(@RequestBody VectorizeProductRequestDto requestDto);
}
