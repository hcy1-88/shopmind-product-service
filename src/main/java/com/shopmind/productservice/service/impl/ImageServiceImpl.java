package com.shopmind.productservice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.shopmind.framework.service.StorageService;
import com.shopmind.productservice.contants.StorageConstants;
import com.shopmind.productservice.exception.ProductServiceException;
import com.shopmind.productservice.service.ImageService;
import com.shopmind.productservice.utils.ImageUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Base64;

/**
 * Description:
 * Author: huangcy
 * Date: 2025-12-26
 */
@Service
public class ImageServiceImpl implements ImageService {
    @Resource
    private StorageService storageService;

    @Override
    public String getImageUrlFromBase64(String base64) {
        if (StrUtil.isEmpty(base64)) {
            return null;
        }
        if (base64.startsWith("http:") || base64.startsWith("https:")){
            return base64;
        } else if (ImageUtil.isBase64Image(base64)) {
            String base64Data = ImageUtil.extractBase64Data(base64);
            byte[] decode = Base64.getDecoder().decode(base64Data);
            String contentType = ImageUtil.getMimeTypeFromBase64(base64);
            return storageService.uploadFile(decode, contentType, StorageConstants.PRODUCT_DIR);
        } else {
            throw new ProductServiceException("PRODUCT0002");
        }
    }
}
