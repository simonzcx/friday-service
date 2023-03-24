package com.friday.mq.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author chengxu.zheng
 * @Create 2022/9/2 11:08
 * @Description MQ消息编码实体类
 */
@Setter
@Getter
@ToString
public class NotificationMsgCodeDTO implements Serializable {

    /**
     * hu的messageCode
     */
    private String hu;

    /**
     * 短信的messageCode
     */
    private String sms;

    /**
     * app的messageCode
     */
    private String app;
}
