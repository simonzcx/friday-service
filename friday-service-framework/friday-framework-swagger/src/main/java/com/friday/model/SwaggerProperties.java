package com.friday.model;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * swagger属性
 *
 * @author Simon.z
 * @since 2021/12/17
 */
@Component
public class SwaggerProperties {

    /**
     * 是否开启swagger，生产环境一般关闭，所以这里定义一个变量
     */
    @Value("${swagger.enable:false}")
    private Boolean enable;

    /**
     * 项目应用名
     */
    @Value("${swagger.application-name:swagger}")
    private String applicationName;

    /**
     * 项目版本信息
     */
    @Value("${swagger.application-version:V1.0.0}")
    private String applicationVersion;

    /**
     * 项目描述信息
     */
    @Value("${swagger.application-description:swagger}")
    private String applicationDescription;

    /**
     * 接口调试地址
     */
    @Value("${swagger.try-host:http://127.0.0.1}")
    private String tryHost;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    public String getApplicationDescription() {
        return applicationDescription;
    }

    public void setApplicationDescription(String applicationDescription) {
        this.applicationDescription = applicationDescription;
    }

    public String getTryHost() {
        return tryHost;
    }

    public void setTryHost(String tryHost) {
        this.tryHost = tryHost;
    }
}
