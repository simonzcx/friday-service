package com.friday.dealer.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author chengxu.zheng
 * @Create 2022/7/18 10:44
 * @Description 经销商检索实体类
 */
@Data
@ApiModel(value = "DealerQueryDTO", description = "经销商检索实体类")
public class DealerQueryDTO implements Serializable {

    /**
     * 经度，两位小数点
     */
    @ApiModelProperty(value = "经度，两位小数点", required = true)
    private Double longitude;

    /**
     * 纬度，两位小数点
     */
    @ApiModelProperty(value = "纬度，两位小数点", required = true)
    private Double latitude;

    /**
     * 搜索关键字
     */
    @ApiModelProperty("搜索关键字")
    private String keyword;

    /**
     * 返回结果中的最大条目数量，默认为20
     */
    @ApiModelProperty(value = "返回结果中的最大条目数量，默认为20")
    private Integer limit = 20;

}
