package com.friday.swagger.util;

import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.List;

/**
 * @Author chengxu.zheng
 * @Create 2022/6/24 18:22
 * @Description Swagger工具类
 */
public class SwaggerUtils {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    public static final List<String> URLS = Arrays.asList(
            "/doc.html",
            "/webjars/**",
            "/swagger-resources",
            "/v2/api-docs",
            "/v3/api-docs",
            "/error",
            "/favicon.ico"
    );

    /**
     * 校验请求路径
     *
     * @param reqUri 请求路径
     * @return 是否匹配
     */
    public static Boolean verify(String reqUri) {
        for (String url : URLS) {
            boolean match = PATH_MATCHER.match(url, reqUri);
            if (match) {
                return true;
            }
        }
        return false;
    }

}
