# 实验平台后端
## 功能简介
实验平台的后端分为主机和评测机集群，主机负责接收用户的评测请求（以及待评测的代码）、调度分布式评测集群、汇总评测结果、向用户返回评测成绩结果、统计学生的实验成绩，主机又称「成绩系统」；评测机（集群）负责接受评测请求并向主机拉取待评测代码和评测程序，完成评测后将评测结果返回至主机。后端主机和评测机集群之间通过 Apache-Kafka 消息队列进行通信，发布评测任务和发布评测结果各一个topic。前端和后端之间通过 HTTP 协议进行 JSON RPC 通讯，以此来完成数据的交互。关于后端的概念定义详见文档「评测程序对接成绩系统（主机）」
## 构建指南
### 评测机
1. 安装openjdk11
2. kafka和mysql在配置过程在主机端的部署文档中已有说明，此处不再赘述
3. 进入主机代码的目录，执行编译与打包指令：
```bash
mvn package
```
8. 将打包得到的.jar文件（位于target目录）复制到要部署的目录中
9. 在jar部署到的目录中，创建文件application.properties，按照如下定义进行配置
```properties
score.worker.host = <主机地址>
score.worker.web-root = http://${score.worker.host}:2333/
spring.kafka.bootstrap-servers=<kafka消息队列主机地址>
score.worker.python-path = <python解释器路径>
score.worker.name = <此评测机名称>
logging.file.path=<日志存储路径>
score.file-root-path = <临时文件存储地址>
score.worker-key = <评测机密钥，评测机和主机需在此字段上填写相同的内容，否则将无法完成代码同步>


```
10. 输入指令
`java -jar ./worker-0.0.1-SNAPSHOT.jar`
启动主机服务器




















