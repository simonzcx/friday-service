package com.friday.tool.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Author chengxu.zheng
 * @Create 2022/8/15 22:47
 * @Description ApiDTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ApiDTO implements Serializable {

    @ExcelProperty("接口序号")
    @ColumnWidth(30)
    private String no;

    @ExcelProperty("接口名称")
    @ColumnWidth(40)
    private String name;

    @ExcelProperty("接口路径")
    @ColumnWidth(40)
    private String path;

    @ExcelProperty("接口类型")
    @ColumnWidth(10)
    private String type;

    @ExcelProperty("主要入参")
    @ColumnWidth(10)
    private String inParam;

    @ExcelProperty("主要出参")
    @ColumnWidth(10)
    private String outParam;

    @ExcelProperty("调用方")
    @ColumnWidth(10)
    private String invoker;

    @ExcelProperty("接收方")
    @ColumnWidth(10)
    private String receiver;

}
