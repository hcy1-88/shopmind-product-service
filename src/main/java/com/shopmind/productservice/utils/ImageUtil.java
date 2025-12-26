package com.shopmind.productservice.utils;

import com.shopmind.productservice.exception.ProductServiceException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 图片处理
 * Author: huangcy
 * Date: 2025-12-26
 */
public class ImageUtil {

    public static boolean isBase64Image(String str) {
        Pattern pattern = Pattern.compile("^data:image/(png|jpeg|jpg|gif);base64,[A-Za-z0-9+/=]+$");
        return str != null && pattern.matcher(str).matches();
    }

    /**
     * 前端上传图片时，字符串是"data:image/png;base64,iVBxxxxx"  , 需提取纯 Base64 图片编码
     * @param base64Str 图片编码
     * @return 纯 base64
     */
    public static String extractBase64Data(String base64Str) {
        return base64Str.substring(base64Str.indexOf(",") + 1);
    }

    /**
     * 获取图片的 mineType 类型
     * @param base64Str 形如 "data:image/png;base64,iVBxxxxx"
     * @return 获取图片的 mineType 类型
     */
    public static String getMimeTypeFromBase64(String base64Str) {
        Pattern pattern = Pattern.compile("^data:(.+?);base64,");
        Matcher matcher = pattern.matcher(base64Str);

        String mimeType = null;
        if (matcher.find()) {
            mimeType = matcher.group(1); // 直接返回完整的MIME类型
        }

        if (mimeType != null && mimeType.startsWith("image/")){
            return mimeType;
        } else {
            throw new ProductServiceException("PRODUCT0002");
        }

    }
}
