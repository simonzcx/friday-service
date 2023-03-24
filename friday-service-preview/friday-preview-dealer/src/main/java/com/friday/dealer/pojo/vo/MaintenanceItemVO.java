package com.friday.dealer.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author chengxu.zheng
 * @Create 2022/7/18 11:30
 * @Description 经销商维修保养项目实体类
 */
@Data
@ApiModel(value = "MaintenanceItemVO", description = "经销商维修保养项目实体类")
public class MaintenanceItemVO implements Serializable {

    /**
     * 项目编码
     */
    @ApiModelProperty("项目编码")
    private String code;

    /**
     * 项目名称
     */
    @ApiModelProperty("项目名称")
    private String item;

    /**
     * 状态
     */
    @ApiModelProperty("状态（1-可预约， 2-不可预约）")
    private Integer status;
}
