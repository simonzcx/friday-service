package com.friday.chatgpt.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;

/**
 * @Author chengxu.zheng
 * @Create 2023/3/14 18:08
 * @Description RestTemplateUtil
 */
@Slf4j
public class RestTemplateUtil {

    /**
     * RestAPI 调用器
     */
    private final static RestTemplate REST_TEMPLATE;

    static {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(300000);
        requestFactory.setReadTimeout(300000);
        REST_TEMPLATE = new RestTemplate(requestFactory);
        // 解决乱码问题
        REST_TEMPLATE.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public static RestTemplate getRestTemplate() {
        return REST_TEMPLATE;
    }

    public static void setRestTemplate(String ip, Integer port) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(50000);//ms
        factory.setConnectTimeout(150000);//ms
        factory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port)));
        //factory.setProxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(ip, port)));
        REST_TEMPLATE.setRequestFactory(factory);

        // 解决乱码问题
        REST_TEMPLATE.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }



}
