package com.shopmind.productservice.exception;

import com.shopmind.framework.exception.ShopmindException;
import com.shopmind.framework.util.MessageSourceHelper;

/**
 * Description:
 * Author: huangcy
 * Date: 2025-12-26
 */
public class ProductServiceException extends ShopmindException {
    private static final String RESOURCE_BASE_NAME = "product_message";

    public ProductServiceException(String code) {
        super(MessageSourceHelper.getMessage(RESOURCE_BASE_NAME, code));
    }

    public ProductServiceException(String code, Object... args) {
        super(MessageSourceHelper.getMessage(RESOURCE_BASE_NAME, code, args));
    }

    /**
     * 构造方法：直接指定错误码和消息（不使用国际化）
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public ProductServiceException(String code, String message) {
        super(code, message);
    }
}
