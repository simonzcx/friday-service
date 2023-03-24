package com.friday.sheep;


import com.alibaba.fastjson.JSON;
import com.friday.sheep.util.RandomUtil;
import com.friday.sheep.util.SpringUtils;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * @Author chengxu.zheng
 * @Create 2022/9/18 20:33
 * @Description
 */
public class Sheep {

    private final static String TOKEN = "";

    private final static String TOKEN_KEY = "t";

    public static void main(String[] args) {
        try {
            System.out.println("【羊了个羊一键闯关开始启动】");
            final int count = 100;
            for (int i = 0; i < count; i++) {
                System.out.println("...第" + (i + 1) + "开始完成闯关...");
                int time = RandomUtil.getRandom(1, 3600);
                System.out.println("生成随机完成耗时:" + time);
                finishGame(1, time);
                System.out.println("(【羊了个羊一键闯关开始结束】" + time);
                Thread.sleep(3000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void finishGame(int state, int time) {
        try {
            String api = "https://cat-match.easygame2021.com/sheep/v1/game/game_over?rank_score=1&" +
                    "rank_state=" + state +
                    "&rank_time=" + time +
                    "&rank_role=1&skin=1";
            RestTemplate restTemplate = SpringUtils.getBean(RestTemplate.class);

            HttpHeaders headers = new HttpHeaders();
            headers.set(TOKEN_KEY, TOKEN);
            HttpEntity<Object> entity = new HttpEntity<>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(api, HttpMethod.GET, entity, Object.class);
            if (HttpStatus.OK.equals(response.getStatusCode())) {
                System.out.println("执行成功===>" + JSON.toJSON(response.getBody()));
            }
            System.out.println("请求不成功===>" + JSON.toJSON(response.getBody()));

        } catch (Exception e) {
            System.out.println("===>请求超时，服务器太多请求了，偶尔超时很正常");
            e.printStackTrace();
        }
    }


}
