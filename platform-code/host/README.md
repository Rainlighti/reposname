# 实验平台后端
## 功能简介
实验平台的后端分为主机和评测机集群，主机负责接收用户的评测请求（以及待评测的代码）、调度分布式评测集群、汇总评测结果、向用户返回评测成绩结果、统计学生的实验成绩，主机又称「成绩系统」；评测机（集群）负责接受评测请求并向主机拉取待评测代码和评测程序，完成评测后将评测结果返回至主机。后端主机和评测机集群之间通过 Apache-Kafka 消息队列进行通信，发布评测任务和发布评测结果各一个topic。前端和后端之间通过 HTTP 协议进行 JSON RPC 通讯，以此来完成数据的交互。关于后端的概念定义详见文档「评测程序对接成绩系统（主机）」
## 构建指南
### 主机
1. 安装openjdk11
2. 安装kafka
3. 切换至kafka所在目录，执行
```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
```
以启动zookeeper和kafka的broker
4. 创建两个topic
```bash
bin/kafka-topics.sh --create --zookeeper localhost:2181 \
    --replication-factor 1 --partitions 6 --topic judging-task
bin/kafka-topics.sh --create --zookeeper localhost:2181 \
    --replication-factor 1 --partitions 1 --topic judging-result
```
注意，这里的judging-task的partitions数量必须大于实际使用的评测机的数量，否则在无法完成将评测任务均衡负载至每台评测机上。
5. 安装mysql8
6. 执行以下sql语句创建数据库并设计sql_mode
```sql
create database os_exp_score;
use os_exp_score;
SET GLOBAL sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''));

```
7. 进入主机代码的目录，执行编译与打包指令：
```bash
mvn package
```
8. 将打包得到的.jar文件（位于target目录）复制到要部署的目录中
9. 在jar部署到的目录中，创建文件application.properties，按照如下定义进行配置
```properties
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://<此处填mysql服务器地址>/os_exp_score?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8&useUnicode=true
spring.datasource.username=<sql服务器用户名>
spring.datasource.password=<sql服务器密码>
server.port=<后端端口>
#spring.jpa.show-sql=true
spring.kafka.bootstrap-servers=<kafka主机地址>
spring.kafka.consumer.group-id=cn.voidnet
logging.file.path=<日志文件地址>
score.file-root-path = <评测临时文件存储目录>
score.worker-key = <评测机密钥，评测机和主机需在此字段上填写相同的内容，否则将无法完成代码同步>


```
10. 输入指令
`java -jar ./score-system-0.0.1-SNAPSHOT.jar`
启动主机服务器




















