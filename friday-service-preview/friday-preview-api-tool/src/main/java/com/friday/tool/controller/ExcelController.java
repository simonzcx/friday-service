package com.friday.tool.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.friday.tool.pojo.ApiDTO;
import com.friday.tool.pojo.ApiQueryDTO;
import com.friday.tool.util.ExcelUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @Author chengxu.zheng
 * @Create 2022/8/16 09:50
 * @Description ExcelController
 */
@RestController
@RequestMapping("/excel")
public class ExcelController {

    @PostMapping("/swagger")
    public void export(@RequestBody ApiQueryDTO record, HttpServletResponse response) throws IOException {
        setExcelRespProp(response, record.getFileName());

        EasyExcel.write(response.getOutputStream())
                .head(ApiDTO.class)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet("API列表")
                .doWrite(ExcelUtil.apiList(record));

    }

    /**
     * 设置excel下载响应头属性
     */
    private void setExcelRespProp(HttpServletResponse response, String rawFileName) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(rawFileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
    }
}
