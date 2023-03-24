package com.friday.dealer.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author chengxu.zheng
 * @Create 2022/6/10 16:00
 * @Description Swagger属性类
 */
@Data
@Component
public class SwaggerProperties {

    /**
     * 是否开启swagger，生产环境一般关闭，所以这里定义一个变量
     */
    @Value(value = "${swagger.enable}")
    private Boolean enable;

    /**
     * 项目名称
     */
    @Value(value = "${swagger.apiInfo.title}")
    private String title;

    /**
     * 项目版本
     */
    @Value(value = "${swagger.apiInfo.version}")
    private String version;

    /**
     * 项目描述信息
     */
    @Value(value = "${swagger.apiInfo.description}")
    private String description;

    /**
     * 服务条款网址
     */
    @Value(value = "${swagger.apiInfo.termsOfServiceUrl}")
    private String termsOfServiceUrl;

    /**
     * 联系人名称
     */
    @Value(value = "${swagger.apiInfo.contact.name}")
    private String name;

    /**
     * 联系人URL
     */
    @Value(value = "${swagger.apiInfo.contact.url}")
    private String url;

    /**
     * 联系人Email
     */
    @Value(value = "${swagger.apiInfo.contact.email}")
    private String email;

    /**
     * 分组名称
     */
    @Value(value = "${swagger.docket.groupName}")
    private String groupName;

}