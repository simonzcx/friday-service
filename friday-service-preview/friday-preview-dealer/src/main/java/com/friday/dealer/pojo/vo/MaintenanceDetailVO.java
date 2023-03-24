package com.friday.dealer.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author chengxu.zheng
 * @Create 2022/7/18 10:58
 * @Description 经销商维修保养详情实体类
 */
@Data
@ApiModel(value = "MaintenanceDetailVO", description = "经销商维护详情实体类")
public class MaintenanceDetailVO implements Serializable {

    /**
     * 预约时间表
     */
    @ApiModelProperty("预约时间表")
    private List<ReserveDateVO> appointments;

    /**
     * 维修项目列表
     */
    @ApiModelProperty("维修项目列表")
    private List<MaintenanceItemVO> repairs;

    /**
     * 保养项目列表
     */
    @ApiModelProperty("保养项目列表")
    private List<MaintenanceItemVO> maintenances;

}
