//package com.friday.chatgpt.bak;
//
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.io.resource.ResourceUtil;
//import com.alibaba.fastjson2.JSON;
//import lombok.SneakyThrows;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * @Author chengxu.zheng
// * @Create 2022/12/12 17:03
// * @Description
// */
//public class ChatGPT {
//
//    @SneakyThrows
//    public static String getInput(String prompt) {
//        System.out.print(prompt);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        List<String> lines = new ArrayList<>();
//        String line;
//        try {
//            while ((line = reader.readLine()) != null && !line.isEmpty()) {
//                lines.add(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return lines.stream().collect(Collectors.joining("\n"));
//    }
//
//
//    @SneakyThrows
//    public static void main(String[] args) {
//
//        System.out.println("ChatGPT - A command-line interface to OpenAI's ChatGPT (https://chat" +
//                ".openai.com/chat)");
//        System.out.println("Repo: github.com/acheong08/ChatGPT");
//        System.out.println("Type '!help' to show commands");
//        System.out.println("Press enter twice to submit your question.\n");
//
//        final String path = "config.json";
//        if (FileUtil.exist(path)) {
//            String configString = ResourceUtil.readUtf8Str(path);
//
//            Config config = JSON.parseObject(configString, Config.class);
//
//            Chatbot chatbot = new Chatbot(config, null);
//            String prompt;
//            while (true) {
//                prompt = getInput("\nYou:\n");
//                if (prompt.startsWith("!")) {
//                    if ("!help".equals(prompt)) {
//                        System.out.println("\n!help - Show this message");
//                        System.out.println("!reset - Forget the current conversation");
//                        System.out.println("!refresh - Refresh the session authentication");
//                        System.out.println("!rollback - Rollback the conversation by 1 message");
//                        System.out.println("!config - Show the current configuration");
//                        System.out.println("!exit - Exit the program");
//                        continue;
//                    } else if ("!reset".equals(prompt)) {
//                        chatbot.resetChat();
//                        System.out.println("Chat session reset.");
//                        continue;
//                    } else if ("!refresh".equals(prompt)) {
//                        chatbot.refreshSession();
//                        System.out.println("Session refreshed.\n");
//                        continue;
//                    } else if ("!rollback".equals(prompt)) {
//                        chatbot.rollbackConversation();
//                        System.out.println("Chat session rolled back.");
//                        continue;
//                    } else if ("!exit".equals(prompt)) {
//                        break;
//                    }
//                }
//
//                if (Arrays.asList(args).contains("--text")) {
//                    try {
//                        System.out.println("Chatbot: ");
//                        String out = "text";
//                        Map<String, Object> message = chatbot.getChatResponse(prompt, out);
//                        System.out.println(message.get("message"));
//                    } catch (Exception e) {
//                        System.out.println("Something went wrong!");
//                        e.printStackTrace();
//                        System.out.println(e);
//                    }
//                } else {
//                    try {
//                        System.out.println("Chatbot: ");
//                        Map<String, Object> message = chatbot.getChatResponse(prompt, "stream");
//                        // Split the message by newlines
//                        String[] messageParts = message.get("message").toString().split("\n");
//
//                        for (String part : messageParts) {
//                            String[] wrappedParts = part.split("\n");
//                            for (String wrappedPart : wrappedParts) {
//                                System.out.println(wrappedPart);
//                            }
//                        }
//
//                    } catch (Exception e) {
//                        System.out.println("Something went wrong!");
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        } else {
//            System.out.println("Please create and populate config.json to continue");
//            if (!FileUtil.exist(path)) {
//                FileUtil.writeUtf8String("", path);
//            }
//        }
//
//    }
//}
