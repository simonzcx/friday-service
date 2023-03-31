package com.friday.chatgpt.controller;

import com.alibaba.fastjson2.JSONObject;
import com.friday.chatgpt.utils.HttpClientPoolUtil;
import com.friday.chatgpt.utils.HttpClientUtils;
import com.friday.chatgpt.utils.RestTemplateUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @Author chengxu.zheng
 * @Create 2023/3/14 11:58
 * @Description ChatController
 */
@RestController
public class ChatController {

    @Resource
    private HttpClientPoolUtil httpClientPoolUtil;

    @PostMapping("/chat")
    public String chat(@RequestBody String record) throws Exception {
        return "success";
    }

    @PostMapping("/chat1")
    public String chat1(@RequestBody JSONObject record) throws Exception {
        String url = "https://api.openai.com/v1/chat/completions";
        String proxyHost = "192.168.3.5";
        int proxyPort = 7890;

        return HttpClientUtils.getResultByProxy(url, record.toString(), proxyHost, proxyPort);
    }

    @PostMapping("/chat2")
    public JSONObject chat2(@RequestBody JSONObject record) {
        String url = "https://api.openai.com/v1/chat/completions";
        return httpClientPoolUtil.postForJsonObject(url, record.toJSONString());
    }

    @PostMapping("/chat3")
    public JSONObject chat3(@RequestBody JSONObject record) {
        //RestTemplate restTemplate = new RestTemplateBuilder(new ProxyCustomizer()).build();
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();

        headers.put("Authorization", new ArrayList<String>() {{
            add("Bearer xxxx-token");
        }});
        HttpEntity<String> httpEntity = new HttpEntity<>(record.toString(), headers);

        RestTemplateUtil.setRestTemplate("192.168.3.5", 7890);
        RestTemplate restTemplate = RestTemplateUtil.getRestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, httpEntity, String.class);
        if (HttpStatus.OK.equals(response.getStatusCode())) {
            return JSONObject.parseObject(response.getBody());
        }
        return null;
    }
}
