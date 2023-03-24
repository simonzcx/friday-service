package com.friday.tool.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author chengxu.zheng
 * @Create 2022/8/15 22:47
 * @Description ApiQueryDTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ApiQueryDTO implements Serializable {

    @NotBlank
    @ExcelProperty("文件路径")
    private String path;

    @NotBlank
    @ExcelProperty("调用方")
    private String invoker;

    @NotBlank
    @ExcelProperty("接收方")
    private String receiver;

    @NotBlank
    @ExcelProperty("文件名")
    private String fileName;

}
