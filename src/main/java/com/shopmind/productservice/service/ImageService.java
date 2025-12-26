package com.shopmind.productservice.service;

public interface ImageService {
    /**
     * 获取图片 url
     * @param base64 图片编码
     * @return url
     */
    String getImageUrlFromBase64(String base64);
}
