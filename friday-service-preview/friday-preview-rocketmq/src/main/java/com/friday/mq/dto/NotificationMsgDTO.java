package com.friday.mq.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @Author chengxu.zheng
 * @Create 2022/9/2 11:08
 * @Description MQ消息实体类
 */
@Setter
@Getter
@ToString
public class NotificationMsgDTO implements Serializable {

    /**
     * 必须，约定的messageCode
     */
    private NotificationMsgCodeDTO messageCode;

    /**
     * 推送车机端的id集合
     */
    private List<String> deviceIdList;

    /**
     * 推送用户消息的id集合
     */
    private List<String> userIdList;

    /**
     * 预计发送时间，时间戳，key的actionType为3有效
     * 可以设置定时任务到未来的某个时间触发。考虑到消息延迟后端会提前1分钟左右发送。
     * 如果设置为-1  则默认为取消之前的定时任务【如果还没有发送的话】
     * 默认为null，代表没有定时任务
     */
    private Long validDuration;

    /**
     * 替换消息模板中的内容。
     * 为空则使用默认模板，不为空，不用输入全部，修改哪个就写那个，部分替换的逻辑
     */
    private NotificationPayloadDTO payload;

    /**
     * 透传数据至HU端
     */
    private NotificationDataDTO data;

    /**
     * 发送时间戳，不传默认消息模块发送时间戳
     */
    private Long timestamp;

    /**
     * 发送失败以后是否重试。默认0 不重试。
     * 每次重试的时间间隔固定50ms。最多3次
     */
    private Integer sendFailAndRetryCount;

    /**
     * 是否开启补偿策略，默认不开启补偿。推送失败就失败。异步记录会继续记录。
     */
    private Boolean needCompensator;

    /**
     * 当needCompensator为true的时候，最多进行多少次补充。默认3次，最多16次。
     * 时间间隔默认1s/5s/10s/30s/1m/2m/3m/4m/5m/6m/7m/8m/9m/10m/20m/30m/1h/2h
     */
    private Integer compensatorCount;

    /**
     * 业务方服务
     */
    private String serviceType;

    /**
     * 业务类型
     */
    private String type;

    /**
     * 请求id
     */
    private String requestId;
}
