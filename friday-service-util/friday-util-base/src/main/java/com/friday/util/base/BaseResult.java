package com.friday.util.base;


import com.friday.util.enums.MessageLevelEnum;

/**
 * 基础结果类
 *
 * @author Simon.z
 * @since 2021/12/16
 */
public class BaseResult<T> {

    public static final BaseResult SUCCESS = new BaseResult();

    /**
     * 是否成功
     */
    private boolean success = true;

    /**
     * 消息等级：900致命、800错误、700警告、500通知、300调试、100追踪、0无
     */
    private int level;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 业务数据
     */
    private T data;

    /**
     * 当前页
     */
    private Integer pageNum = 1;

    /**
     * 页大小
     */
    private Integer pageSize = 10;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 总数据量
     */
    private Integer totalSize;


    private static BaseResult build(boolean success) {
        BaseResult result = new BaseResult();
        result.setSuccess(success);
        if (success) {
            result.setLevel(MessageLevelEnum.TRACE.status);
        } else {
            result.setLevel(MessageLevelEnum.ERROR.status);
        }
        return result;
    }

    public static <T> BaseResult<T> success(T data) {
        BaseResult result = build(true);
        result.setData(data);
        return result;
    }

    public static BaseResult fail() {
        BaseResult result = build(false);
        return result;
    }

    public static BaseResult fail(String message) {
        BaseResult result = build(false);
        result.setMessage(message);
        return result;
    }

    public BaseResult ofSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public BaseResult ofLevel(MessageLevelEnum level) {
        this.level = level.status;
        return this;
    }

    public BaseResult ofMessage(String message) {
        this.message = message;
        return this;
    }

    public BaseResult ofData(T data) {
        this.data = data;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }
}
