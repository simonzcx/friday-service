package com.friday.mq.producer;

import com.friday.mq.dto.NotificationMsgDTO;
import lombok.extern.log4j.Log4j2;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author chengxu.zheng
 * @Create 2022/9/2 11:08
 * @Description VIP到期通知信息生产者
 */
@Log4j2
@Component
public class ReserveNoticeProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private NotificationSendCallBack notificationSendCallBack;

    @Value("${rocketmq.producer.topic-notification}")
    private String topicNotification;

    /**
     * 通知消息
     *
     * @param record 业务数据
     * @param codes  消息唯一标识列表
     */
    public void notice(NotificationMsgDTO record, List<String> codes) {
        Message<?> message = MessageBuilder
                .withPayload(record)
                .setHeader(RocketMQHeaders.KEYS, "1_3_" + record.getRequestId())
                .build();

        notificationSendCallBack.setMsgKey(record.getRequestId());
        notificationSendCallBack.setNoticeList(codes);
        rocketMQTemplate.asyncSend(topicNotification, message, notificationSendCallBack);
    }
}
