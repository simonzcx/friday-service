package com.friday.tts.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author chengxu.zheng
 * @Create 2022/11/10 18:02
 * @Description 语音实体类
 */
@Setter
@Getter
@ToString
@ApiModel(value = "VoiceVO", description = "语音实体类")
public class VoiceVO implements Serializable {

    /**
     * 语音唯一标识
     */
    @ApiModelProperty(value = "语音唯一标识")
    private String code;

    /**
     * 语音名称
     */
    @ApiModelProperty(value = "语音名称")
    private String title;

    /**
     * 语音艺术家
     */
    @ApiModelProperty(value = "语音艺术家")
    private String artist;

    /**
     * 语音风格
     */
    @ApiModelProperty(value = "语音风格")
    private String style;

    /**
     * 语音封面URL
     */
    @ApiModelProperty(value = "语音封面URL")
    private String coverUrl;

    /**
     * 语音URL
     */
    @ApiModelProperty(value = "语音URL")
    private String voiceUrl;

    /**
     * 语音小样URL
     */
    @ApiModelProperty(value = "语音小样URL")
    private String demoUrl;

    /**
     * 语音发布时间 UNIX毫秒时间戳
     */
    @ApiModelProperty(value = "语音发布时间 UNIX毫秒时间戳")
    private Long releaseTime;

    /**
     * 语音详情
     */
    @ApiModelProperty(value = "语音详情")
    private String remark;

}
