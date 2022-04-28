https://docs.spring.io/spring-framework/docs/current/reference/html/web.html

web项目的启动流程： <br />
Tomcat启动时会解析web.xml文件  <br />
→ 通过web.xml文件初始化listener和servlet  <br />
→ 执行listener(ContextLoaderListener) 加载spring <br />
→ 执行servlet(DispatchServlet)加载spring-mvc <br />

- web.xml          加载配置spring容器，配置拦截器
- application.xml  配置扫描包，扫描业务类
- springmvc.xml    扫描controller，视图解析器等


在没有springboot之前 <br />
1）在web.xml手动配置Spring配置文件和DispatcherServlet <br />
```xml
<!-- Spring MVC配置 -->
<servlet>
    <servlet-name>spring</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
    <servlet-name>DispatcherServlet</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>

<!-- Spring配置 -->
<listener>
   <listenerclass>
     org.springframework.web.context.ContextLoaderListener
   </listener-class>
</listener>
  
<!-- 指定Spring Bean的配置文件 -->
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:config/applicationContext.xml</param-value>
</context-param>
```
2）将项目打包到tomcat容器中运行 <br />
3）Tomcat启动时，解析web.xml，Spring通过ContextLoaderListener来完成上下文的加载 <br />
4）Tomcat将web.xml中配置的前端控制器DispatcherServlet注册到Servlet容器中 <br />

综上总结，启动服务需要完成以下几步
1）启动一个Tomcat服务 <br />
2）初始化Spring上下文环境 <br />
3）注册DispatcherServlet <br />
SpringBoot要想达到零配置，需要完成以上几个步骤 <br />

解决方案：
启动Tomcat -> 内嵌解决 <br />
初始化Spring上下文 -> 利用SPI机制 <br />
动态注册DispatcherServlet -> 初始化时一并实现 <br />

实现步骤
1）添加spring、mvc和tomcat依赖 <br />
2）创建Spring初始化上下文环境的配置类SpringContextConfig.java <br />
3）创建一个内嵌的Tomcat的启动类TomcatStarter.java <br />
4）创建应用程序的启动类FridayBootApplication.java（实际上就是启动一个Tomcat），到此未结束，还有Spring的上下文没有初始化，DispatcherServlet没有注册 <br />
5）创建应用程序初始化类FridayApplicationInitializer.java（Spring的上下文初始化，注册DispatcherServlet） <br />
6）在resources/META-INF/services目录下创建文件<br />
`javax.servlet.ServletContainerInitializer`
```
com.friday.boot.component.FridayApplicationInitializer
``` 
6.1）创建了6）中的文件，Tomcat启动时会自自动执行FridayApplicationInitializer的onStartup()方法，Spring上下文环境初始化完毕，DispatcherServlet也成功注册 <br />