package com.friday.tool.util;

import cn.hutool.core.io.resource.ResourceUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.friday.constant.base.StringConstants;
import com.friday.tool.pojo.ApiDTO;
import com.friday.tool.pojo.ApiInfo;
import com.friday.tool.pojo.ApiPathInfo;
import com.friday.tool.pojo.ApiQueryDTO;
import com.friday.util.utils.CollectionUtils;
import com.friday.util.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author chengxu.zheng
 * @Create 2022/8/16 11:25
 * @Description
 */
public class ExcelUtil {

    private static String METHOD_POST = "post";

    private static String METHOD_GET = "get";

    private static String TYPE = "http/https";

    public static List<ApiDTO> apiList(ApiQueryDTO record) {
        List<ApiDTO> list = new ArrayList<>();

        String str = ResourceUtil.readUtf8Str(record.getPath());
        JSONObject jsonObject = JSON.parseObject(str, JSONObject.class);
        ApiInfo apiInfo = jsonObject.toJavaObject(ApiInfo.class);
        JSONObject paths = apiInfo.getPaths();

        ApiInfo.Component components = apiInfo.getComponents();
        JSONObject schemas = components.getSchemas();

        for (Map.Entry<String, Object> entry : paths.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            JSONObject valueJson = JSON.parseObject(JSON.toJSONString(value), JSONObject.class);
            String method = valueJson.containsKey(METHOD_POST) ? METHOD_POST : METHOD_GET;
            JSONObject methodJson = valueJson.getJSONObject(method);
            ApiPathInfo apiPathInfo = methodJson.toJavaObject(ApiPathInfo.class);
            String summary = apiPathInfo.getSummary();

            JSONObject requestBody = apiPathInfo.getRequestBody();
            JSONObject response = apiPathInfo.getResponses();
            ApiDTO dto = new ApiDTO();
            key = key.contains("{version}") ? key.replace("{version}", "v1") : key;
            dto.setPath(key);
            if (summary.contains(StringConstants.CHAR_OPEN_CHINESE_BRACKET)) {
                summary = summary.replaceAll(StringConstants.CHAR_OPEN_CHINESE_BRACKET, "");
                String[] split = summary.split(StringConstants.CHAR_CLOSE_CHINESE_BRACKET);
                dto.setNo(split[0]);
                dto.setName(split[1]);
                dto.setType(TYPE);
                initParamsByOpenApi3(dto, requestBody, response, schemas);
                dto.setInvoker(record.getInvoker());
                dto.setReceiver(record.getReceiver());
                list.add(dto);
            }
        }

        return list;
    }

    public static ApiDTO initParams(ApiDTO dto, JSONObject body, JSONObject response, JSONObject schemas) {
        List<String> inParams = new ArrayList<String>() {{
            add("vin");
            add("userId");
        }};
        if (Objects.nonNull(body) && !body.isEmpty()) {
            String ref = body.getJSONObject("content")
                    .getJSONObject("application/json")
                    .getJSONObject("schema")
                    .getString("$ref");
            if (StringUtils.isNotBlank(ref) && ref.contains(StringConstants.CHAR_SLASH)) {
                String[] split = ref.split(StringConstants.CHAR_SLASH);
                JSONObject object = schemas.getJSONObject(split[split.length - 1]);
                JSONArray array = object.getJSONArray("required");
                if (Objects.nonNull(array) && !array.isEmpty()) {
                    for (int i = 0; i < array.size(); i++) {
                        inParams.add(array.getString(i));
                    }
                }
            }
        }
        String inParamStr = String.join("\r\n", inParams);
        dto.setInParam(inParamStr);

        List<String> outParams = new ArrayList<>();
        if (Objects.nonNull(response) && !response.isEmpty()) {
            String ref = response.getJSONObject("200")
                    .getJSONObject("content")
                    .getJSONObject("*/*")
                    .getJSONObject("schema")
                    .getString("$ref");
            if (StringUtils.isNotBlank(ref) && ref.contains(StringConstants.CHAR_SLASH)) {
                String[] split = ref.split(StringConstants.CHAR_SLASH);
                JSONObject object = schemas.getJSONObject(split[split.length - 1]);
                JSONObject data = object.getJSONObject("properties")
                        .getJSONObject("data");

                String dataRef = data.getString("$ref");
                if (StringUtils.isNotBlank(dataRef) && dataRef.contains(StringConstants.CHAR_SLASH)) {
                    String[] dataSplit = ref.split(StringConstants.CHAR_SLASH);
                    JSONObject dataObj = schemas.getJSONObject(dataSplit[dataSplit.length - 1]);
                    JSONObject dataParam = dataObj.getJSONObject("properties");
                    if (Objects.nonNull(dataParam) && !dataParam.isEmpty()) {
                        outParams.addAll(dataParam.keySet());
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(outParams)) {
            String outParamStr = String.join("\r\n", outParams);
            dto.setOutParam(outParamStr);
        }
        return dto;
    }

    public static ApiDTO initParamsByOpenApi3(ApiDTO dto, JSONObject body, JSONObject response, JSONObject schemas) {
        final String keyType = "type";
        final String keyData = "data";
        final String keyRef = "$ref";
        final String keySchema = "schema";
        final String keyProperties = "properties";

        final String valueObject = "object";
        final String valueBoolean = "boolean";

        List<String> inParams = new ArrayList<String>() {{
            add("vin");
            add("userId");
        }};
        if (Objects.nonNull(body) && !body.isEmpty()) {
            JSONObject schema = body.getJSONObject("content")
                    .getJSONObject("application/json")
                    .getJSONObject(keySchema);
            if (Objects.nonNull(schema) && !schema.isEmpty()) {
                if (valueObject.equals(schema.getString(keyType))) {
                    JSONObject properties = schema.getJSONObject(keyProperties);
                    inParams.addAll(properties.keySet());
                }
            }
        }
        dto.setInParam(String.join(", ", inParams));

        List<String> outParams = new ArrayList<>();
        if (Objects.nonNull(response) && !response.isEmpty()) {
            JSONObject schema = response.getJSONObject("200")
                    .getJSONObject("content")
                    .getJSONObject("application/json")
                    .getJSONObject(keySchema)
                    .getJSONObject(keyProperties);
            JSONObject data = schema.getJSONObject(keyData);
            if (Objects.nonNull(data) && !data.isEmpty() && valueObject.equals(data.getString(keyType))) {
                String ref = data.getString(keyRef);
                if (StringUtils.isNotBlank(ref) && ref.contains(StringConstants.CHAR_SLASH)) {
                    String[] split = ref.split(StringConstants.CHAR_SLASH);
                    JSONObject refObj = schemas.getJSONObject(split[split.length - 1]);
                    if (valueObject.equals(refObj.getString(keyType))) {
                        JSONObject properties = refObj.getJSONObject(keyProperties);
                        outParams.addAll(properties.keySet());
                    }
                }
            } else {
                outParams.addAll(schema.keySet());
            }
            if (CollectionUtils.isNotEmpty(outParams)) {
                String outParamStr = String.join(", ", outParams);
                dto.setOutParam(outParamStr);
            }
        }
        return dto;
    }

}
