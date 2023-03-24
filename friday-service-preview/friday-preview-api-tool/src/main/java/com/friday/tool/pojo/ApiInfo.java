package com.friday.tool.pojo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Author chengxu.zheng
 * @Create 2022/8/16 10:08
 * @Description ApiInfo
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ApiInfo implements Serializable {

    private String openapi;

    private JSONObject info;

    private JSONArray servers;

    private JSONArray tags;

    private JSONObject paths;

    private Component components;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Component implements Serializable {

        private JSONObject schemas;

    }
}
