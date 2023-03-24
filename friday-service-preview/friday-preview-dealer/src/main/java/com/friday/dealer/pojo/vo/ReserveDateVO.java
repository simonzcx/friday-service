package com.friday.dealer.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author chengxu.zheng
 * @Create 2022/7/18 11:09
 * @Description 经销商预约日期实体类
 */
@Data
@ApiModel(value = "ReserveDateVO", description = "经销商预约日期实体类")
public class ReserveDateVO implements Serializable {

    /**
     * 预约日期
     */
    @ApiModelProperty(value = "预约日期", example = "2022-07-18")
    private String date;

    /**
     * 星期几（Mon-星期一，Tues-星期二，Wed-星期三，Thur-星期四，Fri-星期五，Sat-星期六，Sun-星期天）
     */
    @ApiModelProperty(value = "星期几（Mon-星期一，Tues-星期二，Wed-星期三，Thur-星期四，Fri-星期五，Sat-星期六，Sun-星期天）", example = "Mon")
    private String weekday;

    /**
     * 预约时间列表
     */
    @ApiModelProperty("预约时间列表")
    private List<ReserveTimeVO> times;

    @Data
    @ApiModel(value = "ReserveTimeVO", description = "经销商预约时间实体类")
    public static class ReserveTimeVO implements Serializable {

        /**
         * 预约时间
         */
        @ApiModelProperty(value = "预约时间", example = "10:00")
        private String time;

        /**
         * 状态
         */
        @ApiModelProperty("状态（1-可预约/空闲， 2-不可预约/繁忙）")
        private Integer status;
    }

}
