package com.friday.chatgpt.utils;

import com.alibaba.fastjson2.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @Author chengxu.zheng
 * @Create 2023/3/14 11:47
 * @Description
 */
public class HttpClientUtils {

    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

    public static String getResultByProxy(String url, String request, String proxyHost, int proxyPort) throws Exception {
        String response = null;
        HttpPost httpPost = null;
        try {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            HttpClient httpClient = getHttpClient(url, proxy);
            //设置请求配置类  重点就是在这里添加setProxy 设置代理
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(15000)
                    .setConnectTimeout(15000)
                    .setConnectionRequestTimeout(15000)
                    .setProxy(proxy)
                    .build();
            httpPost = new HttpPost(url);

            httpPost.setConfig(requestConfig);
            httpPost.addHeader("Content-Type", CONTENT_TYPE);
            httpPost.setHeader("Authorization", "Bearer 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");
            httpPost.setEntity(new StringEntity(request, "utf-8"));

            response = getHttpClientResponse(httpPost, httpClient);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != httpPost) {
                httpPost.releaseConnection();
            }
        }
        return response;
    }

    private static String getHttpClientResponse(HttpPost httpPost, HttpClient httpClient) throws Exception {
        String result = null;
        HttpResponse httpResponse = httpClient.execute(httpPost);
        System.out.printf("请求响应：" + JSON.toJSONString(httpResponse));
        HttpEntity entity = httpResponse.getEntity();


        if (null != entity) {
            try (InputStream inputStream = entity.getContent()) {
                byte[] bytes = read(inputStream, 1024);
                result = new String(bytes, StandardCharsets.UTF_8);
            }
        }

        return result;
    }

    private static HttpClient getHttpClient(String url, HttpHost proxy) throws Exception {
        //设置认证
        CredentialsProvider provider = new BasicCredentialsProvider();
        //第一个参数对应代理httpHost，第二个参数设置代理的用户名和密码，如果代理不需要用户名和密码，填空
        provider.setCredentials(new AuthScope(proxy), new UsernamePasswordCredentials("", ""));

        HttpClient httpClient;
        String lowerURL = url.toLowerCase();
        if (lowerURL.startsWith("https")) {
            httpClient = createSSLClientDefault(provider);
        } else {
            httpClient = HttpClients.custom().setDefaultCredentialsProvider(provider).build();
        }
        return httpClient;
    }

    private static CloseableHttpClient createSSLClientDefault(CredentialsProvider provider) throws Exception {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, (s, sslSession) -> true);
        return HttpClients.custom().setSSLSocketFactory(socketFactory).setDefaultCredentialsProvider(provider).build();
    }

    public static byte[] read(InputStream inputStream, int bufferSize) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[bufferSize];

        for (int num = inputStream.read(buffer); num != -1; num = inputStream.read(buffer)) {
            outputStream.write(buffer, 0, num);
        }

        outputStream.flush();
        return outputStream.toByteArray();
    }
}

