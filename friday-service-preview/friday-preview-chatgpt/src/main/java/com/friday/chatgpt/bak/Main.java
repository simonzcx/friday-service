//package com.friday.chatgpt.bak;
//
//import cn.hutool.core.io.resource.ResourceUtil;
//import com.alibaba.fastjson2.JSON;
//
//import java.util.Map;
//
///**
// * @Author chengxu.zheng
// * @Create ${DATE} ${TIME}
// * @Description
// */
//public class Main {
//
//    public static void main(String[] args) {
//        final String path = "config.json";
//        String configString = ResourceUtil.readUtf8Str(path);
//        Config config = JSON.parseObject(configString, Config.class);
//        Chatbot chatbot = new Chatbot(config, null);
//        Map<String, Object> chatResponse = chatbot.getChatResponse("");
//        System.out.println("----------------------------------");
//        System.out.println(chatResponse.get("message"));
//    }
//}