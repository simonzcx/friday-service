package com.friday.boot.component;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class TomcatStarter {

    private static final Integer TOMCAT_PORT = 8080;

    private static final String WEBAPP_PATH = "friday-service-preview/friday-preview-springboot-impl/src/main";

    /**
     * A context path must either be an empty string or start with a '/' and do not end with a '/'.
     * The path [/] does not meet these criteria and has been changed to []
     */
    private static final String CONTEXT_PATH = "/app";

    public static void run() {
        String path = System.getProperty("user.dir") + File.separator + WEBAPP_PATH;
        System.out.println("路径：" + path);
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(TOMCAT_PORT);

        //创建上下文
        tomcat.addWebapp(CONTEXT_PATH, path);
        //StandardContext context = (StandardContext) tomcat.addWebapp(CONTEXT_PATH, path);
        try {
            tomcat.start();

            //监听关闭端口，阻塞式。没有这一句，方法执行完成后会直接结束
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }
}
