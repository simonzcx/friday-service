package com.friday.tool.pojo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @Author chengxu.zheng
 * @Create 2022/8/16 13:39
 * @Description ApiPathInfo
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ApiPathInfo implements Serializable {

    private List<String> tags;

    private String summary;

    private String operationId;

    private List<Parameter> parameters;

    private JSONObject requestBody;

    private JSONObject responses;


    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Parameter implements Serializable {

        private String name;

        private String in;

        private String description;

        private Boolean required;

        private Boolean allowReserved;

        private JSONObject schema;

    }

}
