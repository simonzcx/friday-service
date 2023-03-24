//package com.friday.chatgpt.bak;
//
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.http.HttpUtil;
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import cn.hutool.http.HttpResponse;
//import lombok.Data;
//import lombok.SneakyThrows;
//
//import okhttp3.Response;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
///**
// * @Author chengxu.zheng
// * @Create 2022/12/12 17:04
// * @Description
// */
//@Data
//public class Chatbot {
//    private String conversationId = null;
//
//    private String sessionToken;
//
//    private String authorization = "";
//    private String cfClearance;
//    private String userAgent;
//    private String parentId;
//    private Map<String, String> headers = new HashMap<>();
//
//    private String conversationIdPrev;
//    private String parentIdPrev;
//
//
//    public Chatbot(Config config) {
//        this(config, null);
//    }
//
//    public Chatbot(Config config, String conversationId) {
//        this.sessionToken = config.getSession_token();
//        this.cfClearance = config.getCfClearance();
//        this.userAgent = config.getUserAgent();
//        this.conversationId = conversationId;
//        this.parentId = UUID.randomUUID().toString();
//        if (StrUtil.isNotEmpty(sessionToken)) {
//            refreshSession();
//        }
//    }
//
//    /**
//     * 初始化
//     *
//     * @param sessionToken
//     * @param cfClearance
//     * @param userAgent
//     */
//    public Chatbot(String sessionToken, String cfClearance, String userAgent) {
//        this.userAgent = userAgent;
//        this.cfClearance = cfClearance;
//        this.sessionToken = sessionToken;
//        this.parentId = UUID.randomUUID().toString();
//        refreshSession();
//    }
//
//    // Resets the conversation ID and parent ID
//    public void resetChat() {
//        this.conversationId = null;
//        this.parentId = UUID.randomUUID().toString();
//    }
//
//
//    // Refreshes the headers -- Internal use only
//    public void refreshHeaders() {
//        if (StrUtil.isEmpty(authorization)) {
//            authorization = "";
//        }
//        this.headers = new HashMap<String, String>() {{
//            put("Host", "chat.openai.com");
//            put("Accept", "text/event-stream");
//            put("Authorization", "Bearer " + authorization);
//            put("Content-Type", "application/json");
//            put("User-Agent", userAgent);
//            put("X-Openai-Assistant-App-Id", "");
//            put("Connection", "close");
//            put("Accept-Language", "en-US,en;q=0.9");
//            put("Referer", "https://chat.openai.com/chat");
//        }};
//
//    }
//
//
//    Map<String, Object> getChatStream(Map<String, Object> data) {
//        String url = "https://chat.openai.com/backend-api/conversation";
//
//        String body = HttpUtil.createPost(url)
//                .headerMap(headers, true)
//                .body(JSON.toJSONString(data), "application/json")
//                .execute()
//                .body();
//
//        String message;
//        Map<String, Object> chatData = new HashMap<>(16);
//        for (String s : body.split("\n")) {
//            if ((s == null) || "".equals(s)) {
//                continue;
//            }
//            if (s.contains("data: [DONE]")) {
//                continue;
//            }
//
//            String part = s.substring(5);
//            JSONObject lineData = JSON.parseObject(part);
//            try {
//
//                JSONArray jsonArray = lineData.getJSONObject("message")
//                        .getJSONObject("content")
//                        .getJSONArray("parts");
//
//                if (jsonArray.size() == 0) {
//                    continue;
//                }
//                message = jsonArray.getString(0);
//
//                conversationId = lineData.getString("conversation_id");
//                parentId = (lineData.getJSONObject("message")).getString("id");
//
//                chatData.put("message", message);
//                chatData.put("conversation_id", conversationId);
//                chatData.put("parent_id", parentId);
//            } catch (Exception e) {
//                System.out.println("getChatStream Exception: " + part);
//                //  e.printStackTrace();
//            }
//
//        }
//        return chatData;
//
//    }
//
//    // Gets the chat response as text -- Internal use only
//    public Map<String, Object> getChatText(Map<String, Object> data) {
//
//        // Create request session
//        Session session = new Session();
//
//        // set headers
//        session.setHeaders(this.headers);
//
//        // Set multiple cookies
//        session.getCookies().put("__Secure-next-auth.session-token", sessionToken);
//        session.getCookies().put("__Secure-next-auth.callback-url", "https://chat.openai.com/");
//        session.getCookies().put("cf_clearance", cfClearance);
//
//        // Set proxies
//        setupProxy(session);
//
//        HttpResponse response = session.postRequest("https://chat.openai.com/backend-api/conversation",
//                data);
//        String body = response.body();
//
//        String errorDesc = "";
//
//
//        String message = "";
//        Map<String, Object> chatData = new HashMap<>();
//        for (String s : body.split("\n")) {
//            if ((s == null) || "".equals(s)) {
//                continue;
//            }
//            if (s.contains("data: [DONE]")) {
//                continue;
//            }
//
//            String part = s.substring(5);
//
//            try {
//                JSONObject lineData = JSON.parseObject(part);
//
//                JSONArray jsonArray = lineData.getJSONObject("message")
//                        .getJSONObject("content")
//                        .getJSONArray("parts");
//
//                if (jsonArray.size() == 0) {
//                    continue;
//                }
//                message = jsonArray.getString(0);
//
//                conversationId = lineData.getString("conversation_id");
//                parentId = (lineData.getJSONObject("message")).getString("id");
//
//                chatData.put("message", message);
//                chatData.put("conversation_id", conversationId);
//                chatData.put("parent_id", parentId);
//            } catch (Exception e) {
//                System.out.println("getChatStream Exception: " + part);
//                //  e.printStackTrace();
//                continue;
//            }
//
//        }
//        return chatData;
//    }
//
//    private void setupProxy(Session session) {
////        if (config.get("proxy") != null && !config.get("proxy").equals("")) {
////            Map<String, String> proxies = new HashMap<>();
////            proxies.put("http", config.get("proxy"));
////            proxies.put("https", config.get("proxy"));
////            session.setProxies(proxies);
////        }
//    }
//
//    public Map<String, Object> getChatResponse(String prompt, String output) {
//        Map<String, Object> data = new HashMap<>();
//        data.put("action", "next");
//        data.put("conversation_id", this.conversationId);
//        data.put("parent_message_id", this.parentId);
//        data.put("model", "text-davinci-002-render");
//
//        Map<String, Object> message = new HashMap<>();
//        message.put("id", UUID.randomUUID().toString());
//        message.put("role", "user");
//        Map<String, Object> content = new HashMap<>();
//        content.put("content_type", "text");
//        content.put("parts", Collections.singletonList(prompt));
//        message.put("content", content);
//        data.put("messages", Collections.singletonList(message));
//
//        this.conversationIdPrev = this.conversationId;
//        this.parentIdPrev = this.parentId;
//
//        if ("text".equals(output)) {
//            return this.getChatText(data);
//        } else if ("stream".equals(output)) {
//            return this.getChatStream(data);
//        } else {
//            throw new RuntimeException("Output must be either 'text' or 'stream'");
//        }
//    }
//
//    public Map<String, Object> getChatResponse(String prompt) {
//        return this.getChatResponse(prompt, "text");
//    }
//
//
//    @SneakyThrows
//    public void refreshSession() {
//
//        if (sessionToken == null || sessionToken.equals("")) {
//            throw new RuntimeException("No tokens provided");
//        }
//        Session session = new Session();
//
//        // Set proxies
//        setupProxy(session);
//
//        // Set cookies
//        session.getCookies().put("__Secure-next-auth.session-token", sessionToken);
//        session.getCookies().put("cf_clearance", cfClearance);
//        String cookiesString = session.getCookiesString();
//        Map<String, String> map = new HashMap<>();
//        map.put("User-Agent", userAgent);
//        map.put("cookie", cookiesString);
//        map.put("Cookie", cookiesString);
//        session.setHeaders(map);
//        String urlSession = "https://chat.openai.com/api/auth/session";
//        HttpResponse response = session.getRequest(urlSession);
//
//        if (response.getStatus() != 200) {
//            System.err.println("err code: " + response.getStatus());
//            System.err.println("cf_clearance: " + cfClearance);
//            System.err.println("token: " + sessionToken);
//            System.err.println("userAgent: " + userAgent);
//
//            System.err.println("请检查以上参数是否正确，是否过期。并且获取以上参数的浏览器要和本程序在同一IP地址" );
//            System.err.println("Please check whether the above parameters are correct or expired. And the browser that obtains the above parameters must be at the same IP address as this program" );
//
//            return;
//        }
//
//        try {
//            String name = "__Secure-next-auth.session-token";
//            String cookieValue = response.getCookieValue(name);
//            sessionToken = cookieValue;
//
//            String body = response.body();
//            System.out.println("session_token: " + cookieValue);
//            JSONObject responseObject = JSON.parseObject(body);
//
//            String accessToken = responseObject.getString("accessToken");
//            System.out.println("accessToken: " + accessToken);
//
//            authorization = accessToken;
////                config.put("Authorization", accessToken);
//
//            this.refreshHeaders();
//        } catch (Exception e) {
//            System.out.println("Error refreshing session");
//            throw new Exception("Error refreshing session", e);
//        }
//    }
//
//    public void rollbackConversation() {
//        this.conversationId = this.conversationIdPrev;
//        this.parentId = this.parentIdPrev;
//    }
//
//    @SneakyThrows
//    public static JSONObject resJson(Response response) {
//        JSONObject responseObject = null;
//        String text = response.body().string();
//        try {
//            response.body().close();
//            responseObject = JSON.parseObject(text);
//        } catch (Exception e) {
//            System.out.println("json err, body: " + text);
//            throw new RuntimeException(e);
//        }
//
//        return responseObject;
//    }
//
//}
