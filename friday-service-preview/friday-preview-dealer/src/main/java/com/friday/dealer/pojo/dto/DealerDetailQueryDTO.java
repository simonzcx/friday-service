package com.friday.dealer.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author chengxu.zheng
 * @Create 2022/7/18 10:59
 * @Description 经销商详情检索实体类
 */
@Data
@ApiModel(value = "DealerDetailQueryDTO", description = "经销商详情检索实体类")
public class DealerDetailQueryDTO implements Serializable {

    /**
     * 经销商编码
     */
    @ApiModelProperty(value = "经销商编码", required = true)
    private String code;

}
