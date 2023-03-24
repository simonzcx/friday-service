//package com.friday.chatgpt.bak;
//
//import cn.hutool.http.HttpUtil;
//import com.alibaba.fastjson2.JSON;
//import net.dreamlu.mica.http.HttpRequest;
//import cn.hutool.http.HttpResponse;
//import lombok.Data;
//
//import okhttp3.Response;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @Author chengxu.zheng
// * @Create 2022/12/12 17:06
// * @Description
// */
//@Data
//public class Session {
//
//    private Map<String, String> headers = new HashMap<>();
//
//    private Map<String, String> cookies = new HashMap<>();
//
//    private Map<String, String> proxies = new HashMap<>();
//
//    String client;
//
//    public Session() {
//    }
//
//
//    public Response post(String url, Map<String, Object> data) {
//        getCookiesString();
//        return HttpRequest.post(url)
//                .addHeader(headers)
//                .bodyJson(data)
//                .execute()
//                .response();
//    }
//
//    public HttpResponse postRequest(String url, Map<String, Object> data) {
//        getCookiesString();
//
//
//        return HttpUtil.createPost(url)
//                .addHeaders(headers)
//                .cookie(getCookiesString())
//                .body(JSON.toJSONString(data), "application/json")
//                .execute();
//
//    }
//
//    public Response post(String url, Map<String, Object> data, boolean followRedirects) {
//        getCookiesString();
//
//        return HttpRequest.post(url)
//                .addHeader(headers)
//                .followRedirects(followRedirects)
//                .bodyJson(data)
//                .execute()
//                .response();
//    }
//
//    public Response get(String url, Map<String, String> data) {
//
//        getCookiesString();
//        Map<String, Object> map = new HashMap<>(data);
//        Response response = HttpRequest.get(url)
//                .addHeader(headers)
//                .queryMap(map)
//                .execute()
//                .response();
//
//        return response;
//    }
//
//    public HttpResponse getRequest(String url) {
//        getCookiesString();
//
//        return HttpUtil.createGet(url)
//                .addHeaders(headers)
//                .cookie(getCookiesString())
//                .execute();
//    }
//
//
//    public String getString(String url, Map<String, String> data) {
//
//        getCookiesString();
//        Map<String, Object> map = new HashMap<>(data);
//        return HttpRequest.get(url)
//                .addHeader(headers)
//                .queryMap(map)
//                .execute()
//                .asString();
//    }
//
//    public Response get(String url, Map<String, String> data, boolean followRedirects) {
//        getCookiesString();
//        Map<String, Object> map = new HashMap<>(data);
//
//        return HttpRequest.get(url)
//                .addHeader(headers)
//                .followRedirects(followRedirects)
//                .queryMap(map)
//                .execute()
//                .response();
//    }
//
//    public Response post(String url, Map<String, String> headers, String payload) {
//
//        getCookiesString();
//        return HttpRequest.post(url)
//                .addHeader(headers)
//                .bodyJson(payload)
//                .execute()
//                .response();
//    }
//
//    public String getCookiesString() {
//        StringBuilder result = new StringBuilder();
//        for (Map.Entry<String, String> entry : cookies.entrySet()) {
//            String key = entry.getKey();
//            String value = entry.getValue();
//            result.append(key).append("=").append(value).append("; ");
//        }
//        headers.put("cookie", result.toString());
//        return result.toString();
//    }
//}
