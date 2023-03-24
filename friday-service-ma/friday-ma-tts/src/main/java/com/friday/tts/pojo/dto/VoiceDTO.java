package com.friday.tts.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author chengxu.zheng
 * @Create 2022/11/10 18:30
 * @Description 语音检索实体类
 */
@Setter
@Getter
@ToString
@ApiModel(value = "VoiceDTO", description = "语音检索实体类")
public class VoiceDTO implements Serializable {

    /**
     * 开始时间 UNIX毫秒时间戳
     */
    @ApiModelProperty(value = "开始时间 UNIX毫秒时间戳，检索指定时间到当前时间的语音")
    private Long startTime;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private Integer pageNum;

    /**
     * 页大小
     */
    @ApiModelProperty(value = "页大小，最大100", required = true)
    private Integer pageSize;

}
