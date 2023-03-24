package com.friday.mq.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author chengxu.zheng
 * @Create 2022/9/2 11:08
 * @Description MQ消息透传实体类
 */
@Setter
@Getter
@ToString
public class NotificationDataDTO<H, S, A> implements Serializable {

    /**
     * hu的data
     */
    private H hu;

    /**
     * 短信的data
     */
    private S sms;

    /**
     * app的data
     */
    private A app;
}
