package com.friday.chatgpt.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @Author chengxu.zheng
 * @Create 2023/3/14 12:27
 * @Description
 */
@Service
@Slf4j
public class HttpClientPoolUtil implements InitializingBean {

    private PoolingHttpClientConnectionManager poolConnManager;

    private RequestConfig requestConfig;

    private CloseableHttpClient httpClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        initPool();
    }

    private void initPool() {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(builder.build());
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", socketFactory).build();
            poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            poolConnManager.setMaxTotal(500);
            poolConnManager.setDefaultMaxPerRoute(500);

            int socketTimeout = 1200000;
            int connectTimeout = 100000;
            int connectionRequestTimeout = 100000;
            HttpHost proxy = getProxy();
            requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionRequestTimeout)
                    .setSocketTimeout(socketTimeout)
                    .setConnectTimeout(connectTimeout)
                    .setProxy(proxy)
                    .build();
            httpClient = getConnection();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private static HttpHost getProxy() {
        return new HttpHost("192.168.3.5", 7890);
    }

    private CloseableHttpClient getConnection() {
        return HttpClients.custom()
                .setConnectionManager(poolConnManager)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(2, false))
                .build();
    }

    private static void config(HttpRequestBase httpRequestBase) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(20000)
                .setConnectTimeout(20000)
                .setSocketTimeout(20000)
                .build();
        httpRequestBase.addHeader("Content-Type", "application/json;charset=UTF-8");
        httpRequestBase.setConfig(requestConfig);
    }

    public String get(String url) {
        HttpGet httpGet = new HttpGet(url);
        config(httpGet);
        return getResponse(httpGet);
    }

    public JSONArray post(String url, String jsonStr) {
        log.debug("url = " + url + " ,  json = " + jsonStr);
        long start = System.currentTimeMillis();
        CloseableHttpResponse response = null;
        JSONArray result = new JSONArray();
        try {
            // 创建httpPost
            HttpPost httpPost = new HttpPost(url);
            config(httpPost);
            httpPost.setHeader("Accept", "application/json");

            StringEntity entity = new StringEntity(jsonStr, StandardCharsets.UTF_8.toString());
            entity.setContentType("application/json");
            entity.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(entity);
            //发送post请求
            response = httpClient.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = JSON.parseArray(EntityUtils.toString(response.getEntity()));
                log.debug("request success cost = {}， result = {}", System.currentTimeMillis() - start, result);
                return result;
            } else {
                log.error("bad response, result: {}", EntityUtils.toString(response.getEntity()));
            }
        } catch (Exception e) {
            log.error("=============[\"异常\"]======================, e: {}", e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public String post(String url, Map<String, Object> requestData) {
        HttpPost httpPost = new HttpPost(url);
        config(httpPost);
        StringEntity stringEntity = new StringEntity(JSON.toJSONString(requestData), "UTF-8");
        stringEntity.setContentEncoding(StandardCharsets.UTF_8.toString());
        httpPost.setEntity(stringEntity);
        return getResponse(httpPost);
    }

    public JSONObject postForJsonObject(String url, String jsonStr) {
        log.debug("url = " + url + " ,  json = " + jsonStr);
        long start = System.currentTimeMillis();
        CloseableHttpResponse response = null;
        try {
            // 创建httpPost
            HttpPost httpPost = new HttpPost(url);
            config(httpPost);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Authorization", "Bearer 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");

            StringEntity entity = new StringEntity(jsonStr, "UTF-8");
            entity.setContentType("application/json");
            entity.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(entity);
            //发送post请求
            response = httpClient.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                JSONObject result = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
                log.debug("request success cost = {}， result = {}", System.currentTimeMillis() - start, result);
                return result;
            } else {
                log.error("bad response: {}", JSONObject.parseObject(EntityUtils.toString(response.getEntity())));
            }
        } catch (Exception e) {
            log.error("=============[\"异常\"]======================, e: {}", e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private String getResponse(HttpRequestBase request) {
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(request, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8.toString());
            EntityUtils.consume(entity);

            return result;
        } catch (IOException e) {
            log.error("send http error", e);
            return "";
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
