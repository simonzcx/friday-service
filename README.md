# friday-service
### 项目简介
friday-service由多个大的模块组成
| 模块  | 描述 |
| :----- | :----- |
| friday-service-parent |  friday-service的父工程，管理项目的Maven版本依赖  |
| friday-service-util |  friday-service的工具模块，包含了常用的常量、枚举、工具类 |
| friday-service-framework |  friday-service的框架模块，包含了常用框架的集成，例如redis、elasticsearch、mq等  |
***

### friday-service-util
| 模块  | 描述 |
| :----- | :----- |
| friday-util-base |  返回结果类、字符串操作工具类、集合操作工具类等常用的基础工具类库  |
| friday-util-constant |  正则表达式、标点符号常量、日期常量等常用的基础常量类库 |
| friday-util-freemarker |  friday-service的框架模块，包含了常用框架的集成，例如redis、elasticsearch、mq等  |
***

### friday-service-framework
| 模块  | 描述 |
| :----- | :----- |
| friday-framework-elasticsearch | spring-boot集成elasticsearch |
| friday-framework-logstash |  spring-boot集成logstash实现应用日志采集 |
| friday-framework-swagger |  spring-boot集成swagger3.0实现web页面API接口的管理  |

