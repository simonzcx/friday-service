package com.friday.swagger.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @Author chengxu.zheng
 * @Create 2022/6/10 16:00
 * @Description Swagger属性类
 */
@Component
public class SwaggerProperties implements Serializable {

    /**
     * 是否开启swagger，生产环境一般关闭，所以这里定义一个变量
     */
    @Value(value = "${swagger.enable:false}")
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

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTermsOfServiceUrl() {
        return termsOfServiceUrl;
    }

    public void setTermsOfServiceUrl(String termsOfServiceUrl) {
        this.termsOfServiceUrl = termsOfServiceUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}