package com.friday.sheep.util;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Spring工具类
 *
 * @author Simon.z
 * @since 2022/01/25
 */
@Component
public class SpringUtils implements ApplicationContextAware, DisposableBean {

    /**
     * applicationContext
     */
    private static ApplicationContext applicationContext;

    /**
     * 不可实例化
     */
    private SpringUtils() {
    }

    /**
     * 获取applicationContext
     *
     * @return applicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringUtils.applicationContext = applicationContext;
    }

    /**
     * 获取实例
     *
     * @param name Bean名称
     * @return 实例
     */
    public static Object getBean(String name) {
        Assert.hasText(name, "name不能为空");
        return applicationContext.getBean(name);
    }

    /**
     * 获取实例
     *
     * @param name Bean名称
     * @param type Bean类型
     * @return 实例
     */
    public static <T> T getBean(String name, Class<T> type) {
        Assert.hasText(name, "name不能为空");
        Assert.notNull(type, "类型不能为空");
        return applicationContext.getBean(name, type);
    }

    /**
     * 获取实例
     *
     * @param type Bean类型
     * @return 实例
     */
    public static <T> T getBean(Class<T> type) {
        Assert.notNull(type, "类型不能为空");
        return applicationContext.getBean(type);
    }

    @Override
    public void destroy() {
        applicationContext = null;
    }
}
