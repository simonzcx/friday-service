package com.friday.util.enums;

/**
 * 消息等级类
 *
 * @author Simon.z
 * @since 2021/12/16
 */
public enum MessageLevelEnum {

    /**
     * 致命
     */
    FATAL(900),

    /**
     * 错误
     */
    ERROR(800),

    /**
     * 警告
     */
    WARN(700),

    /**
     * 通知
     */
    INFO(500),

    /**
     * 调试
     */
    DEBUG(300),

    /**
     * 追踪
     */
    TRACE(100),

    /**
     * 无
     */
    NONE(0);

    public final int status;

    private MessageLevelEnum(int status) {
        this.status = status;
    }
}
