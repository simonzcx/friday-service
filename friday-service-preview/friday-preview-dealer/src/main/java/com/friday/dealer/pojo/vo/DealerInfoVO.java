package com.friday.dealer.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author chengxu.zheng
 * @Create 2022/7/18 10:48
 * @Description 经销商信息实体类
 */
@Data
@ApiModel(value = "DealerInfoVO", description = "经销商信息实体类")
public class DealerInfoVO implements Serializable {

    /**
     * 经销商编码
     */
    @ApiModelProperty("经销商编码")
    private String code;

    /**
     * 经销商名称
     */
    @ApiModelProperty("经销商名称")
    private String name;

    /**
     * 经销商位置经度，两位小数点
     */
    @ApiModelProperty("经销商位置经度，两位小数点")
    private Double longitude;

    /**
     * 经销商位置纬度，两位小数点
     */
    @ApiModelProperty("经销商位置纬度，两位小数点")
    private Double latitude;
}
