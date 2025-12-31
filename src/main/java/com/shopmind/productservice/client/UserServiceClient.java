package com.shopmind.productservice.client;

import com.shopmind.framework.context.ResultContext;
import com.shopmind.productservice.client.dto.request.UserBehaviorRequest;
import com.shopmind.productservice.client.dto.response.UserBehaviorResponseDTO;
import com.shopmind.productservice.client.dto.response.UserInterestsResponseDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

/**
 * Description:
 * Author: huangcy
 * Date: 2026-01-01
 */
@HttpExchange
public interface UserServiceClient {

    /**
     * 获取用户的兴趣
     */
    @GetExchange("/user/interests")
    ResultContext<UserInterestsResponseDTO> getUserInterestsByUserId(@RequestParam("userId") Long userId);


    @PostExchange("/behavior")
    ResultContext<List<UserBehaviorResponseDTO>> getsUserHistoryBehavior(@Valid @RequestBody UserBehaviorRequest userBehaviorRequest);
}
