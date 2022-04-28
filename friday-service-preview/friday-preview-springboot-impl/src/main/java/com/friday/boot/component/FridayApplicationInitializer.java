package com.friday.boot.component;

import com.friday.boot.config.SpringContextConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class FridayApplicationInitializer implements WebApplicationInitializer {

    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println("===== SpringContext初始化... =====");
        //初始化Spring上下文环境
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        //把配置类注册到spring环境
        context.register(SpringContextConfig.class);
        //解析spring环境中的配置类
        //context.refresh();
        System.out.println("===== SpringContext初始化完成 =====");

        //注册DispatcherServlet
        DispatcherServlet servlet = new DispatcherServlet(context);
        //把一个servlet注册给容器（tomcat）
        ServletRegistration.Dynamic registration = servletContext.addServlet("app", servlet);
        //设置DispatcherServlet的init在web容器启动时执行init方法
        registration.setLoadOnStartup(1);
        //配置DispatcherServlet拦截所有请求
        registration.addMapping("/");
    }
}
