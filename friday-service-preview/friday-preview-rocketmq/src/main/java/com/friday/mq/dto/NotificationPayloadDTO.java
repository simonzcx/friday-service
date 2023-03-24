package com.friday.mq.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author chengxu.zheng
 * @Create 2022/9/2 11:08
 * @Description MQ消息替换模版内容实体类
 */
@Setter
@Getter
@ToString
public class NotificationPayloadDTO<H, S, A> implements Serializable {

    /**
     * hu的payload
     */
    private H hu;

    /**
     * 短信的payload
     */
    private S sms;

    /**
     * app的payload
     */
    private A app;
}
