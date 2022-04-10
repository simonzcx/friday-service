<a name="nHzht"></a>
# 一、Helm与Helm Chart
参考链接：[https://zhuanlan.zhihu.com/p/80821849](https://zhuanlan.zhihu.com/p/80821849)
<a name="7mute"></a>
## 前言
我们平时在日常生活中会经常在不同的平台上与各种各样的应用打交道，比如从苹果的 App Store 里下载的淘宝、高德、支付宝等应用，或者是在 PC 端安装的 Word、Photoshop、Steam。这些各类平台上的应用程序，对用户而言，大多只需要点击安装就可使用。<br />然而，在云 (Kubernetes)上，部署一个应用往往却不是那么简单。如果想要部署一个应用程序到云上，首先要准备好它所需要的环境，打包成 Docker 镜像，进而把镜像放在部署文件 (Deployment) 中、配置服务 (Service)、应用所需的账户 (ServiceAccount) 及权限 (Role)、命名空间 (Namespace)、密钥信息 (Secret)、可持久化存储 (PersistentVolumes) 等资源。也就是编写一系列互相相关的 YAML 配置文件，将它们部署在Kubernetes 集群上。<br />但是即便应用的开发者可以把这些 Docker 镜像存放在公共仓库中，并且将所需的 YAML 资源文件提供给用户，用户仍然需要自己去寻找这些资源文件，并把它们一一部署。倘若用户希望修改开发者提供的默认资源，比如使用更多的副本 (Replicas) 或是修改服务端口 (Port)，他还需要自己去查需要在这些资源文件的哪些地方修改，更不用提版本变更与维护会给开发者和用户造成多少麻烦了。

可见最原始的 Kubernetes 应用形态并不便利。
<a name="xa3E2"></a>
## 简介
<a name="DG8Mc"></a>
### Helm
在这样的大环境下，有一系列基于 Kubernetes 的应用包管理工具横空出世。而Helm，就是这其中最受欢迎的选择之一。包管理器类似于我们在 Ubuntu 中使用的apt、Centos中使用的yum 或者Python中的 pip 一样，能快速查找、下载和安装软件包。
<a name="1nvm3"></a>
### Helm Chart
Helm使用的包格式称为chart。 chart就是一个描述Kubernetes相关资源的文件集合。文件集合目录名称就是chart名称（没有版本信息）。
```bash
nginx/
  charts/                    # 包含chart依赖的其他chart
  templates/                 # 模板目录，当和values结合时，可生成有效的Kubernetes manifest文件存放了各类应用部署所需要使用的YAML文件
  templates/deployment.yaml  # 部署相关配置信息
  templates/service.yaml     # 服务端口相关配置信息
  templates/ingress.yaml     # 容器通过ingress暴露服务配置
  Chart.yaml                 # 包含了chart信息的YAML文件 例如当前 Chart 的名称、版本等基本信息
  values.yaml                # chart 默认的配置值 应用在安装时的默认参数
```
基于helm chart在K8S上部署应用，各应用在接入时，需要创建自己的chart文件。 

# 二、K8S部署服务
以流程引擎为例，流程引擎需要部署的服务有：后端、前端

## 1、后端部署

### 1.1 打包上传镜像

#### 1.1.1 打包JAR
IDEA打开Terminal，进入pom文件所在路径，执行编译打包命令：
```shell
mvn clean install -Dmaven.test.skip=true
```

#### 1.1.2 打包镜像
Docker根据Dockerfile将编译jar包打包成镜像包
```shell
FROM fridayz/jre:8
COPY /target/flow-center-be.jar /home/citybrain/flow-center/flow-center-be.jar

# 设置运行参数
ENV JAVA_OPTS=""
ENV SPRINGBOOT_ARGS="--spring.profiles.active=dev"

#定义时区参数
ENV TZ=Asia/Shanghai

## 解决中文乱码
ENV LANG en_US.utf8

#设置时区
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo '$TZ' > /etc/timezone

WORKDIR /home/citybrain/flow-center

ENTRYPOINT java ${JAVA_OPTS} -jar flow-center-be.jar ${SPRINGBOOT_ARGS}
```
```shell
docker build -t 192.168.2.158:9443/citybrain/flow-center-be:0.0.1 .
注： 192.168.2.158:9443    -- Harbor仓库Host
     citybrain            -- Harbor仓库项目namespace(命名空间)
     flow-center-be       -- 镜像名称
     0.0.1                -- 镜像版本
     .                    -- 当前目录默认名称为Dokcerfile的文件
```

#### 1.1.3 登录
登录Harbor仓库
```shell
docker login --username=admin --password=Harbor12345 192.168.2.158:9443
```

#### 1.1.4 推送
推送本地镜像包到Harbor仓库
```shell
docker push 192.168.2.158:9443/citybrain/flow-center-be:0.0.1
```

### 1.2 Helm Chart配置
注：需要先安装helm本地系统或者Linux子系统

#### 1.2.1 创建Chart配置文件集合
创建流程引擎后端Chart配置文件集合
```shell
helm create flow-center-be
```
![image.png](https://cdn.nlark.com/yuque/0/2021/png/1264491/1625209975960-6c7a42fd-c9b0-4893-8b18-8ae179185133.png#crop=0&crop=0&crop=1&crop=1&height=215&id=QgYjb&margin=%5Bobject%20Object%5D&name=image.png&originHeight=430&originWidth=1215&originalType=binary&ratio=1&rotation=0&showTitle=false&size=67234&status=done&style=none&title=&width=607.5)

#### 1.2.2 Chart.yaml
Chart.yaml 申明Chart文件结合及应用的配置信息。
```yaml
apiVersion: v2                                      -- 忽略，Helm chart版本
name: flow-center-be                                -- 忽略，Chart文件集合的名称，创建时便有
description: A Helm chart for Kubernetes            -- 忽略，描述
type: application                                   -- 忽略，默认，文件类型
version: 0.0.1                                      -- 当前Chart文件集合版本，类似代码的版本管理
appVersion: "1.2.0"                                 -- 当前部署应用的版本，默认为nginx模板1.16.0
```

#### 1.2.3 value.yaml
value.yaml 申明配置信息，给deploy.yaml/service.yaml/ingress.yaml赋值。
```yaml
#######################################################################模板
# Default values for flow-center-be.
# This is a YAML-formatted file.
# Declare variables to be passed into your templates.

replicaCount: 1

image:
  repository: nginx
  pullPolicy: IfNotPresent
  # Overrides the image tag whose default is the chart appVersion.
  tag: ""

imagePullSecrets: []
nameOverride: ""
fullnameOverride: ""

serviceAccount:
  # Specifies whether a service account should be created
  create: true
  # Annotations to add to the service account
  annotations: {}
  # The name of the service account to use.
  # If not set and create is true, a name is generated using the fullname template
  name: ""

podAnnotations: {}

podSecurityContext: {}
  # fsGroup: 2000

securityContext: {}
  # capabilities:
  #   drop:
  #   - ALL
  # readOnlyRootFilesystem: true
  # runAsNonRoot: true
  # runAsUser: 1000

service:
  type: ClusterIP
  port: 80

ingress:
  enabled: false
  className: ""
  annotations: {}
    # kubernetes.io/ingress.class: nginx
    # kubernetes.io/tls-acme: "true"
  hosts:
    - host: chart-example.local
      paths:
        - path: /
          pathType: ImplementationSpecific
  tls: []
  #  - secretName: chart-example-tls
  #    hosts:
  #      - chart-example.local

resources: {}
  # We usually recommend not to specify default resources and to leave this as a conscious
  # choice for the user. This also increases chances charts run on environments with little
  # resources, such as Minikube. If you do want to specify resources, uncomment the following
  # lines, adjust them as necessary, and remove the curly braces after 'resources:'.
  # limits:
  #   cpu: 100m
  #   memory: 128Mi
  # requests:
  #   cpu: 100m
  #   memory: 128Mi

autoscaling:
  enabled: false
  minReplicas: 1
  maxReplicas: 100
  targetCPUUtilizationPercentage: 80
  # targetMemoryUtilizationPercentage: 80

nodeSelector: {}

tolerations: []

affinity: {}
```
------------------------>
```yaml
#######################################################################配置后
image:                                                               
  repository: 192.168.2.158:9443/citybrain/flow-center-be              -- 部署时拉取的镜像
  tag: 0.0.1                                                           -- 部署时拉取的镜像版本
  # Alawys：下载镜像 IfnotPresent：优先使用本地镜像，否则下载镜像，Nerver：仅使用本地镜像
  pullPolicy: Always                                                   -- 拉取策略 
service:
	#service的类型：
	#ClusterIP：默认值，k8s系统给service自动分配的虚拟IP，只能在集群内部访问。
	#NodePort：将Service通过指定的Node上的端口暴露给外部，访问任意一个NodeIP:nodePort都将路由到ClusterIP。默认端口范围30000-32700
	#LoadBalancer：在NodePort的基础上，借助cloud provider创建一个外部的负载均衡器，并将请求转发到:NodePort，此模式只能在云服务器上使用。
	#ExternalName：将服务通过 DNS CNAME 记录方式转发到指定的域名（通过 spec.externlName 设定）。
  type: ClusterIP                                                      -- Service的类型，指定Service的访问方式，默认为ClusterIp
  protocol: TCP                                                        -- 协议，默认TCP
  port: 8080                                                           -- 内部服务访问端口
  targetPort: 8080                                                     -- 容器端口
  nodePort:                                                            -- 映射宿主机的端口，type为NodePort时指定，不指定则默认分配
  
app:
  name: "flow-center-be"                                               -- 自定义变量：应用名称
  version: "1.2.0"                                                     -- 自定义变量：应用版本，会覆盖Chart.yaml中的appVersion
  replicaCount: 1
  config:
    datasource:                                                        -- 自定义变量：数据库配置相关
      host: "dev-mysql-service.citybrain"
      port: "3306"
      database: "flow-dev"
      username: "root"
      password: "citybrain@2020"
    redis:                                                             -- 自定义变量：Redis配置相关
      host: "dev-redis-flow-center-master.citybrain"
      port: "6379"
      password: "citybrain@2020"
      database: "1"

env:
  springBootArgs:                                                     -- 自定义变量：环境变量，覆盖jar包中对应配置文件的配置
  - spring.profiles.active=dizuo
  - sso.open=true
  - sso.domain=citybrain.com
  - sso.login.page=http://xxx.xxxxx.xxx:8080/ums/login
  #ums后端服务地址
  - sso.server=http://security-center-be-service.citybrain:8080
  #后台应用服务地址 无用
  - sso.service=http://127.0.0.1:8080
  - sso.logout.path=http://xxx.xxxxx.xxx:8080/ums/sso/logout
```

#### 1.2.4 deployment.yaml
deployment.yaml 申明部署应用相关的信息。<br />参考地址：[https://www.cnblogs.com/wzs5800/p/13534942.html](https://www.cnblogs.com/wzs5800/p/13534942.html)
```yaml
#######################################################################模板
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ include "flow-center-be.fullname" . }}
  labels:
    {{- include "flow-center-be.labels" . | nindent 4 }}
spec:
  {{- if not .Values.autoscaling.enabled }}
  replicas: {{ .Values.replicaCount }}
  {{- end }}
  selector:
    matchLabels:
      {{- include "flow-center-be.selectorLabels" . | nindent 6 }}
  template:
    metadata:
      {{- with .Values.podAnnotations }}
      annotations:
        {{- toYaml . | nindent 8 }}
      {{- end }}
      labels:
        {{- include "flow-center-be.selectorLabels" . | nindent 8 }}
    spec:
      {{- with .Values.imagePullSecrets }}
      imagePullSecrets:
        {{- toYaml . | nindent 8 }}
      {{- end }}
      serviceAccountName: {{ include "flow-center-be.serviceAccountName" . }}
      securityContext:
        {{- toYaml .Values.podSecurityContext | nindent 8 }}
      containers:
        - name: {{ .Chart.Name }}
          securityContext:
            {{- toYaml .Values.securityContext | nindent 12 }}
          image: "{{ .Values.image.repository }}:{{ .Values.image.tag | default .Chart.AppVersion }}"
          imagePullPolicy: {{ .Values.image.pullPolicy }}
          ports:
            - name: http
              containerPort: 80
              protocol: TCP
          livenessProbe:
            httpGet:
              path: /
              port: http
          readinessProbe:
            httpGet:
              path: /
              port: http
          resources:
            {{- toYaml .Values.resources | nindent 12 }}
      {{- with .Values.nodeSelector }}
      nodeSelector:
        {{- toYaml . | nindent 8 }}
      {{- end }}
      {{- with .Values.affinity }}
      affinity:
        {{- toYaml . | nindent 8 }}
      {{- end }}
      {{- with .Values.tolerations }}
      tolerations:
        {{- toYaml . | nindent 8 }}
      {{- end }}
```
------------------------>
```yaml
#######################################################################配置后
apiVersion: apps/v1                                        
kind: Deployment
metadata:                                                              -- 元数据
  name: {{ .Values.app.name }}-deployment                              -- deployment应用名，{{ .Values.app.name }}指向values.yaml文件中app.name的值，以下类同
  labels:                                                              -- 自定义标签属性列表
    app: {{ .Values.app.name }}                                        -- 自定义标签，deployment应用标签定义
    
spec:                                                                  -- 必选，Pod中容器的详细定义
  selector:                                                            -- 选择器
    matchLabels:
      app: {{ .Values.app.name }}                                      -- Pod选择器标签，通过此标签匹配对应pod
      
  replicas: {{ .Values.app.replicaCount }}                             -- Pod副本数量
  
  template:                                                            -- 应用容器模版定义
    metadata:
      annotations:                                                     -- 自定义注解属性列表
      labels:
        app: {{ .Values.app.name }}                                    -- 与上面matchLabels的标签相同
    spec:
      containers:
        # 容器名称
        - name: {{ .Values.app.name }}                                 -- 容器名称
          # 应用的镜像地址
          image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"  -- 镜像地址
          imagePullPolicy: {{ .Values.image.repository }}              -- 拉取策略
          
          ports:                                                       -- 容器端口
          - name: server                                               -- 端口名称
            containerPort: {{ .Values.service.targetPort }}            -- 端口
            protocol: TCP                                              -- 端口协议，支持TCP和UDP，默认TCP
          
          # 应用容器
          env:                                                         -- 环境变量
          {{- if .Values.env.springBootArgs }}  
          - name: SPRINGBOOT_ARGS                                      -- 指向Dockerfile文件中JAR包启动命令参数
            value: "--{{ .Values.env.springBootArgs | join " --" }}"
          {{- end }}
          {{- if .Values.env.javaOptions }}  
          - name: JAVA_OPTS                                            -- 指向Dockerfile文件中JAR包启动命令参数
            value: "{{ .Values.env.javaOptions | join " " }}"
          {{- end }}
  
          - name: SERVER_PORT                                          -- 指向JAR对应配置文件的变量，以下类同
            value: "{{ .Values.service.targetPort}}"                   -- 指向JAR对应配置文件变量的值，以下类同
          - name: REDIS_HOST
            value: "{{ .Values.app.config.redis.host}}"
          - name: REDIS_PORT
            value: "{{ .Values.app.config.redis.port}}"
          - name: REDIS_PASSWORD
            value: "{{ .Values.app.config.redis.password}}"
          - name: REDIS_DATABASE
            value: "{{ .Values.app.config.redis.database}}"

          - name: DB_HOST
            value: {{ .Values.app.config.datasource.host}}
          - name: DB_PORT
            value: "{{ .Values.app.config.datasource.port}}"
          - name: DB_NAME
            value: {{ .Values.app.config.datasource.database }}
          - name: DB_USERNAME
            value: {{ .Values.app.config.datasource.username }}
          - name: DB_PASSWORD
            value: {{ .Values.app.config.datasource.password }}
          
          volumeMounts:                                              -- 挂载卷
            # 应用日志目录
          - name: app-logs                                           -- 挂载卷名称
            mountPath: /home/citybrain/flow-center/logs              -- 容器类挂载文件路径
          
      volumes:                                                       -- 卷，目录（宿主机或其他）
        - name: app-logs                                             -- 挂载卷名称
          hostPath:
            path: /k8s_storage/docker/logs/{{ .Values.app.name }}/logs  -- 宿主机路径
            type: DirectoryOrCreate                                  -- 如果指定的路径不存在，那么将根据需要创建空目录
```
<a name="4Li3h"></a>
#### 1.2.5 service.yaml
service.yaml 申明容器服务端口配置等信息。<br />参考链接：[https://blog.csdn.net/yucaifu1989/article/details/106680995](https://blog.csdn.net/yucaifu1989/article/details/106680995)<br />[https://www.cnblogs.com/cwsheng/p/14940571.html](https://www.cnblogs.com/cwsheng/p/14940571.html)
```yaml
#######################################################################模板
apiVersion: v1
kind: Service
metadata:
  name: {{ include "flow-center-be.fullname" . }}
  labels:
    {{- include "flow-center-be.labels" . | nindent 4 }}
spec:
  type: {{ .Values.service.type }}
  ports:
    - port: {{ .Values.service.port }}
      targetPort: http
      protocol: TCP
      name: http
  selector:
    {{- include "flow-center-be.selectorLabels" . | nindent 4 }}
```
------------------------>
```yaml
#######################################################################配置后
apiVersion: v1
kind: Service
metadata:                                                              -- 资源对象的元数据定义
  name: {{ .Values.app.name }}-service                                 -- Service名称
  labels:                                                              -- 自定义标签属性列表
    app: {{ .Values.app.name }}                                        -- Service标签定义
spec:
  type: {{ .Values.service.type }}                                     -- Service的类型，指定Service的访问方式，默认为ClusterIp
  ports:                                                               -- Service需要暴露的端口列表
  - name: server                                                       -- 端口名称
    protocol: {{ .Values.service.protocol }}                           -- 端口协议，支持TCP和UDP，默认TCP
    # 这里的端口和clusterIP对应，例ip:8080,供内部访问。
    port: {{ .Values.service.port }}                                   -- 内部服务访问端口
    # 端口一定要和container暴露出来的端口对应
    targetPort: {{ .Values.service.targetPort }}                       -- 容器端口
    # 所有的节点都会开放此端口，此端口供外部调用
    nodePort: {{ .Values.service.nodePort }}                           -- 映射宿主机的端口，type为NodePort时指定，不指定则默认分配
  selector:                                                            -- 将选择具有label标签的Pod作为管理范围                      
    # 这里选择器一定要选择容器的标签
    app: {{ .Values.app.name }}
```

#### 1.2.6 测试
配置完成可以使用命令测试Chart的语法是否正确，类似nginx的命令：nginx -t
```yaml
helm lint flow-center-be
```
![image.png](https://cdn.nlark.com/yuque/0/2021/png/1264491/1625215425091-82969daf-7a42-44e4-bf2c-df445624605e.png#crop=0&crop=0&crop=1&crop=1&height=82&id=yu7ei&margin=%5Bobject%20Object%5D&name=image.png&originHeight=163&originWidth=1466&originalType=binary&ratio=1&rotation=0&showTitle=false&size=32238&status=done&style=none&title=&width=733)<br />之所以报错是因为values.yaml默认的配置被删掉了，导致校验时部分YAML校验不通过，因为这部分文件再部署中使用不到，所以可以删除，精简一下。、<br />精简前文件结构：

```yaml
.
└── flow-center-be
    ├── Chart.yaml
    ├── charts
    ├── templates
    │ ├── NOTES.txt
    │ ├── _helpers.tpl
    │ ├── deployment.yaml
    │ ├── hpa.yaml
    │ ├── ingress.yaml
    │ ├── service.yaml
    │ ├── serviceaccount.yaml
    │ └── tests
    │     └── test-connection.yaml
    └── values.yaml
```
精简后文件结构：
```yaml
.
└── flow-center-be
    ├── Chart.yaml
    ├── templates
    │ ├── deployment.yaml
    │ └── service.yaml
    └── values.yaml
```

#### 1.2.7 打包上传
Chart配置文件包校验通过后即可打包上传。
```yaml
helm package flow-center-be
```
![image.png](https://cdn.nlark.com/yuque/0/2021/png/1264491/1625215811387-8eed461e-e539-4e55-948a-32f7cb31dcd9.png#crop=0&crop=0&crop=1&crop=1&height=40&id=X80GI&margin=%5Bobject%20Object%5D&name=image.png&originHeight=80&originWidth=1325&originalType=binary&ratio=1&rotation=0&showTitle=false&size=23043&status=done&style=none&title=&width=662.5) <br />
上传时可以通过Harbor页面上传，登录Harbor页面，进入对应相应项目命名空间页面，即可上传打包生成的tgz文件。<br />
![image.png](https://cdn.nlark.com/yuque/0/2021/png/1264491/1625216303528-f73eb50d-314d-4620-a993-452d9497f7ba.png#crop=0&crop=0&crop=1&crop=1&height=302&id=ylHkQ&margin=%5Bobject%20Object%5D&name=image.png&originHeight=604&originWidth=1914&originalType=binary&ratio=1&rotation=0&showTitle=false&size=93006&status=done&style=none&title=&width=957)


### 1.3 部署

#### 1.3.1 配置仓库
使用kubeapps进行部署，登录kubeapps页面，选择命名空间citybrain。<br />
![image.png](https://cdn.nlark.com/yuque/0/2021/png/1264491/1625216586351-ae6d1397-74e2-4f54-b816-d0a9ba25b6c1.png#crop=0&crop=0&crop=1&crop=1&height=213&id=Nd7Uv&margin=%5Bobject%20Object%5D&name=image.png&originHeight=426&originWidth=1895&originalType=binary&ratio=1&rotation=0&showTitle=false&size=96399&status=done&style=none&title=&width=947.5) <br />
先配置仓库<br />
![image.png](https://cdn.nlark.com/yuque/0/2021/png/1264491/1625216629641-053263c8-2bfd-4b20-9b59-f252f7ecbbd2.png#crop=0&crop=0&crop=1&crop=1&height=104&id=dKQYi&margin=%5Bobject%20Object%5D&name=image.png&originHeight=208&originWidth=1907&originalType=binary&ratio=1&rotation=0&showTitle=false&size=36936&status=done&style=none&title=&width=953.5) <br />
![image.png](https://cdn.nlark.com/yuque/0/2021/png/1264491/1625216710263-eb48f044-eefd-4a09-820f-25c340303ecd.png#crop=0&crop=0&crop=1&crop=1&height=324&id=QnXAA&margin=%5Bobject%20Object%5D&name=image.png&originHeight=648&originWidth=1917&originalType=binary&ratio=1&rotation=0&showTitle=false&size=118962&status=done&style=none&title=&width=958.5)
<a name="PCFin"></a>
#### 1.3.2 部署
配置完仓库后，既可看到仓库中的所有Chart文件。<br />
![image.png](https://cdn.nlark.com/yuque/0/2021/png/1264491/1625216796274-493d169b-d163-444f-8a8d-3aefa55a6a8c.png#crop=0&crop=0&crop=1&crop=1&height=419&id=ujdzE&margin=%5Bobject%20Object%5D&name=image.png&originHeight=838&originWidth=1888&originalType=binary&ratio=1&rotation=0&showTitle=false&size=183437&status=done&style=none&title=&width=944) <br />
选择对应Chart文件，点击Deployment。<br />
![image.png](https://cdn.nlark.com/yuque/0/2021/png/1264491/1625216846765-0c254e17-c151-4972-8277-fa58c12bc8be.png#crop=0&crop=0&crop=1&crop=1&height=167&id=zFCJf&margin=%5Bobject%20Object%5D&name=image.png&originHeight=333&originWidth=1917&originalType=binary&ratio=1&rotation=0&showTitle=false&size=45575&status=done&style=none&title=&width=958.5) <br />
设置名称，当前页面也可以修改value.yaml的值。最后点击DEPLOY 0.0.1即开始部署。<br />
![image.png](https://cdn.nlark.com/yuque/0/2021/png/1264491/1625216906302-f8d6f49c-dbba-4f8d-a42c-d6e6824ef154.png#crop=0&crop=0&crop=1&crop=1&height=413&id=CWDWr&margin=%5Bobject%20Object%5D&name=image.png&originHeight=826&originWidth=1898&originalType=binary&ratio=1&rotation=0&showTitle=false&size=111129&status=done&style=none&title=&width=949)

## 2、前端部署
参考链接：[https://cloud.tencent.com/developer/article/1706705](https://cloud.tencent.com/developer/article/1706705) <br />
前端的部署雷同1（后端部署）中的部署步骤，在前端Chart配置文件中会涉及到ingress.yaml配置文件，ingress可用于暴露服务供外部访问，类似nginx。<br />
要理解ingress,需要区分两个概念，ingress和ingress-controller：<br />
**Ingress对象:**<br />
<p style=”text-indent:2em”> 指的是k8s中的一个api对象，一般用yaml配置，作用是定义请求如何转发到service的规则，可以理解为配置模板；</p>
<p style=”text-indent:2em”> Ingress规则是很灵活的，可以根据不同域名，不同path转发请求到不同的service，并且支持https/http；</p>

**Ingress-controller：**<br />
<p style=”text-indent:2em”> 具体实现反向代理及负载均衡的程序，对ingress定义的规则进行解析，根据配置的规则来实现请求转发;  简单来说，Ingress-controller才是负责转发的组件，通过各种方式将他暴露在集群入口，外部对集群的请求流量会先到Ingress-controller,而Ingress对象是用来告诉Ingress-controller该如何转发请求，比如那些域名那些path要转发到那些服务等。</p>
<p style=”text-indent:2em”> Ingress-controller并不是k8s自带的组件，实际上ingress-controller只是一个统称，用户可以选择不同的Ingress-controller实现，目前，由K8S维护的Ingress-controller只有google云的GCE与Ingress-nginx两个，其他还有第三方维护的ingress-controller,但不管采用哪种Ingress-controller,实现的机制都大同小异，只是在具体配置上有差异，一般来说，Ingress-controller的形式都是一个Pod，里面跑着daemon程序和反向代理程序，daemon负责不断监控集群的变化，根据Ingress对象生成配置并应用新配置到反向代理，比如nginx-ingress就是动态生成nginx配置，动态更新upstream，并在需要视乎reload程序应用新配置。</p>
  ingress-nginx：k8s官方维护<br />
  nginx-ingress：nginx官方维护

要通过ingress向外部暴露服务，首先得部署一个Ingress-controller，目前选用的是k8s官方维护的ingress-nginx，且已经完成部署。<br />文件结构：
```yaml
.
├── flow-center-fe
│ ├── Chart.yaml
│ ├── templates
│ │ ├── deployment.yaml
│ │ ├── ingress.yaml
│ │ └── service.yaml
│ └── values.yaml
```

### 2.1 value.yaml
```yaml
image:
  repository: "192.168.2.158:9443/citybrain/flow-center-fe"
  tag: 0.0.1
  pullPolicy: Always

service:
  name: flow-center-fe
  type: NodePort
  protocol: TCP
  port: 80
  targetPort: 80
  nodePort: 31001
  
app:
  name: flow-center-fe
  replicaCount: 1
  
port: 
  - name: server
    value: 80

env:
  - name: machine_host
    value: "flow-center-be-service.citybrain"
  - name: be_port
    value: "8080"
  - name: DOLLER
    value: $

ingress:
  enabled: true                                                         -- 是否开启ingress服务
  annotations: 
    nginx.ingress.kubernetes.io/proxy-body-size: 1024m                  -- 自定义注解属性
  hosts:                                                                -- 域名列表
    - host: xxx.xxxxx.xxx
      paths:
        - path: /workflow                                               -- 匹配路径
  tls: []
```

### 2.2 ingress.yaml
```yaml
{{- if .Values.ingress.enabled -}}                                                 -- 判断是否启用ingress，Chart语法
{{- $fullName := .Values.app.name -}}                                              -- 全局变量，Chart语法
{{- $svcPort := .Values.service.port -}}
apiVersion: networking.k8s.io/v1beta1                                              -- 这里的版本根据部署的k8s版本确认
kind: Ingress
metadata:
  name: {{ $fullName }}
  labels:
    app: {{ .Values.app.name }}
  {{- with .Values.ingress.annotations }}
  annotations:
    {{- toYaml . | nindent 4 }}
  {{- end }}
spec:
  {{- if and .Values.ingress.className (semverCompare ">=1.18-0" .Capabilities.KubeVersion.GitVersion) }}
  ingressClassName: {{ .Values.ingress.className }}
  {{- end }}
  {{- if .Values.ingress.tls }}
  tls:
    {{- range .Values.ingress.tls }}
    - hosts:
        {{- range .hosts }}
        - {{ . | quote }}
        {{- end }}
      secretName: {{ .secretName }}
    {{- end }}
  {{- end }}
  rules:
    {{- range .Values.ingress.hosts }}                                            -- 遍历value.yaml中ingress的hosts列表，Chart语法
    - host: {{ .host | quote }}
      http:
        paths:
          {{- range .paths }}
          - path: {{ .path }}
            {{- if and .pathType (semverCompare ">=1.18-0" $.Capabilities.KubeVersion.GitVersion) }}
            pathType: {{ .pathType }}
            {{- end }}
            backend:
              {{- if semverCompare ">=1.19-0" $.Capabilities.KubeVersion.GitVersion }}
              service:                                                           -- 
                name: {{ $fullName }}
                port:
                  number: {{ $svcPort }}
              {{- else }}
              serviceName: {{ $fullName }}-service                                 -- 暴露服务的名称，与service.yaml中service.name相同
              servicePort: {{ $svcPort }}                                          -- 暴露服务的端口，此处使用的是内部服务的端口port，并非targetPort
              {{- end }}
          {{- end }}
    {{- end }}
{{- end }}


##################简洁的示例：

apiVersion: networking.k8s.io/v1beta1
kind: Ingress
metadata:
  name: "{{ .Values.service.name }}-ingress"
  namespace: {{ .Values.k8s_namespace }}
  labels:
    app: "{{ .Values.service.name }}-ingress"
  annotations:
    nginx.ingress.kubernetes.io/proxy-body-size: 1024M
spec:
  tls:
  - hosts: 
    - '{{ .Values.dns.domain }}'
    secretName: '{{ .Values.dns.secretName }}'
  rules:
  - host: {{ .Values.dns.domain }}
    http:
      paths:
      - path: {{ .Values.dns.path }}
        backend:
          serviceName: {{ .Values.service.name }}
          # 这里指定service的port，不是targetport
          servicePort: {{ .Values.service.port }}
```

# 3、遇到的问题
1）deployment.ymal文件中env的变量值如果为数字，部署会报错：
```bash
Deployment in version “v1” cannot be handled as a Deployment: v1.Deployment.Spec: v1.DeploymentSpec.Template: v1.PodTemplateSpec.Spec: v1.PodSpec.Containers: []v1.Container:
```
解决方法，给为数字的变量加上双引号""<br />
![image.png](https://cdn.nlark.com/yuque/0/2021/png/1264491/1624500966657-c8ea34db-4823-40be-91c0-168cc5ca6f2e.png#crop=0&crop=0&crop=1&crop=1&height=290&id=y0Cdc&margin=%5Bobject%20Object%5D&name=image.png&originHeight=580&originWidth=899&originalType=binary&ratio=1&rotation=0&showTitle=false&size=59727&status=done&style=none&title=&width=449.5)



