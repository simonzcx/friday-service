package com.friday.tts.controller;

import com.friday.tts.pojo.dto.VoiceDTO;
import com.friday.tts.pojo.vo.BasePageVO;
import com.friday.tts.pojo.vo.VoiceVO;
import com.friday.util.base.BaseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Author chengxu.zheng
 * @Create 2022/11/10 18:26
 * @Description 语音管理模块
 */
@RestController
@RequestMapping("/voice")
@Api(tags = "语音管理模块")
public class VoiceController {

    @PostMapping("/list")
    @ApiOperation(value = "获取语音列表", response = BaseResult.class)
    public BasePageVO<VoiceVO> list(@RequestBody @Valid VoiceDTO record) {
        return new BasePageVO<>();
    }
}
