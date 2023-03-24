package com.friday.mq.producer;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Author chengxu.zheng
 * @Create 2022/9/26 14:42
 * @Description
 */
@Setter
@Slf4j
@Component
public class NotificationSendCallBack implements SendCallback {

    /**
     * msgKey
     */
    private String msgKey;

    /**
     * 通知的用户UID-过期时间列表
     */
    private List<String> noticeList;

    @Override
    public void onSuccess(SendResult sendResult) {
        log.info("消息推送成功，msgId: {}, msgKey: {}", sendResult.getMsgId(), msgKey);
        if (CollectionUtils.isEmpty(noticeList)) {
        }
    }

    @Override
    public void onException(Throwable throwable) {
        log.error("消息发送失败，msgKey: {}, e: {}", msgKey, throwable.getMessage());
    }

}
