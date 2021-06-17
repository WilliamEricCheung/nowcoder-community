# 开始项目 

### 项目部署的环境配置
1. 安装过程
  * 尽量使用yum去安装
  * yum没有的包用wget -i -c (下载路径)去下载
  * 普通下载范围： 
    1. maven
    2. tomcat
    3. elasticsearch-6.4.3
    4. elasticsearch-ik-6.4.3 
    5. init-sql.zip
    6. kafka
    7. mysql-8.0(用yum安装rpm)
  * yum安装范围：
    1. yum install -y unzip.x86_64
    2. nginx 
    3. java jdk
  * 解压缩安装：tar -zxvf (安装包) -C /opt
2. 配置过程：
  * vim /etc/profile
  * 尾部新增：export PATH=$PATH:/opt/apache-maven-3.6.1/bin
  * 尾部新增：export PATH=$PATH:/opt/apache-tomcat-xxx.x.x/bin
  * 刷新环境变量：source /etc/profile
  * 检查环境变量：echo $PATH
  maven: 
    在config目录下修改settings.xml
    <mirror>
      <id>alimaven</id>
      <mirrorOf>central</mirrorOf>
      <name>aliyun maven</name>
      <url>https://maven.aliyun.com/repository/central</url>
    </mirror>
  mysql:
    1. 启动服务：systemctl start mysqld
    2. 查找初始密码：grep 'password' /var/log/mysqld.log
    3. 登录并修改密码：mysql -u root -p; alter user root@localhost identified by "新密码";
  elasticsearch:
    1. 解压缩
    2. unzip -d /opt/elasticsearch-6.4.3/plugins/ik elasticsearch-analysis-ik-6.4.3.zip
    3. 在config目录的elasticsearch.yml配置
       * cluster.name: nowcoder
       * path.data: /tmp/elastic/data
       * path.logs: /tmp/elastic/log
    4. 修改jvm.options: -Xms256m -Xmx512m
    5. ES不能root用户启动，需要创建普通用户并授权访问：
       * groupadd users
       * useradd user1 -p 123456 -g users
       * cd /opt
       * chown -R user1:user *
       * cd /tmp
       * chown -R user1:user *
       * su - user1
       * 后台启动：bin/elasticsearch -d
  wkhtmltopdf:
    1. yum install -y wkhtmltopdf.x86_64
    2. yum install -y xorg-xll-server-Xvfb.x86_64
    3. 测试xvfb-run --server-args="-screen 0, 1024x768x24" wkhtmltoimage https://www.baidu.com 1.png
    4. cd /opt
    5. vim wkhtmltoimage.sh: xvfb-run --server-args="-screen 0, 1024x768x24" wkhtmltoimage "$@"
    6. chmod +x wkhtmltoimage.sh
  Tomcat:
    bin目录下执行startup.sh启动服务，在webapps中存放项目  
  Nginx:
    1. vim /etc/nginx/nginx.conf   
    2. 注释掉server块配置文件
    3. 插入：
       upstream myserver{
          server 127.0.0.1:8080 max_fails=3 fail_timeout=30s;
       }
       server{
          listen 80;
          server_name ip地址;
          location / {
              proxy_pass http://myserver;
          }
       }

### 部署项目
mvn clean package -Dmaven.test.skip=true

### 启动工程前以下服务先要启动

MySQL：
* 本地用根目录下的sql初始化数据库与原始模拟数据

Redis:
* [Windows端下载链接](https://github.com/MicrosoftArchive/redis/releases)

Kafka：

* 解压缩kafka到合适的位置，并手动[启动](https://www.orchome.com/6) zookeeper与kafka的服务
* windows:
    1. .\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
    2. .\bin\windows\kafka-server-start.bat .\config\server.properties
* linux:
    1. bin/zookeeper-server-start.sh -daemon config/zookeeper.properties
    2. nohup bin/kafka-server-start.sh config/server.properties 1>/dev/null 2>&1 &
  
ElasticSearch：
  
* [ElasticSearch安装配置](https://www.elastic.co/cn/downloads/past-releases/elasticsearch-6-4-3) (yml配置文件在project-init文件夹中)
* [ElasticSearch中文分词插件](https://github.com/medcl/elasticsearch-analysis-ik/releases) (需与ES版本一致，v6.4.3，到ES路径的plugins下新建ik目录并解压缩)

WKHtmlToPdf:

* [下载链接](https://wkhtmltopdf.org/downloads.html)