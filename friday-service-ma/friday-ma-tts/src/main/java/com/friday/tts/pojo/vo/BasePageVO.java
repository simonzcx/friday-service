package com.friday.tts.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chengxu.zheng
 * @create 2022/8/23 11:20
 * @Description 基础分页结果实体类
 **/
@ApiModel(value = "BasePageVO", description = "基础分页结果实体类")
public class BasePageVO<T> implements Serializable {

    /**
     * 总数据量
     */
    @ApiModelProperty("总数据量")
    private Long totalCount;

    /**
     * 总页数
     */
    @ApiModelProperty("总页数")
    private Long totalPage;

    /**
     * 当前页
     */
    @ApiModelProperty("当前页")
    private Long pageIndex;

    /**
     * 页大小
     */
    @ApiModelProperty("页大小")
    private Long pageSize;

    /**
     * 业务数据
     */
    @ApiModelProperty("业务数据")
    private List<T> pageData;

    public BasePageVO() {
        this.totalCount = 0L;
        this.totalPage = 0L;
        this.pageIndex = 0L;
        this.pageSize = 0L;
        this.pageData = new ArrayList<>();
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Long totalPage) {
        this.totalPage = totalPage;
    }

    public Long getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Long pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getPageData() {
        return pageData;
    }

    public void setPageData(List<T> pageData) {
        this.pageData = pageData;
    }

    @Override
    public String toString() {
        return "BasePageVO{" +
                "totalCount=" + totalCount +
                ", totalPage=" + totalPage +
                ", pageIndex=" + pageIndex +
                ", pageSize=" + pageSize +
                ", pageData=" + pageData +
                '}';
    }
}
