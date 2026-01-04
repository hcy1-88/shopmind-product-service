package com.shopmind.productservice.client;

import com.shopmind.framework.context.ResultContext;
import com.shopmind.productservice.client.dto.request.SearchAndRankWithSemanticsRequestDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

/**
 * Description: 推荐服务
 * Author: huangcy
 * Date: 2026-01-04
 */
@HttpExchange("/recommend")
public interface RecommendationClient {

    @PostExchange("/search/semantic")
    ResultContext<List<Long>> searchAndRankWithSemantics(@RequestBody SearchAndRankWithSemanticsRequestDTO requestDTO);
}
