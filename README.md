# 开始项目 

### 启动工程前

MySQL：
* 本地用根目录下的sql初始化数据库与原始模拟数据

Kafka：

* 解压缩kafka到合适的位置，并手动[启动](https://www.orchome.com/6) zookeeper与kafka的服务
* windows:
    1. .\bin\windows\kafka-server-start.bat .\config\server.properties
    2. .\bin\windows\kafka-server-start.bat .\config\server.properties
* linux:
    1. bin/zookeeper-server-start.sh config/zookeeper.properties
    2. bin/kafka-server-start.sh config/server.properties
ElasticSearch：
  
* [ElasticSearch安装配置](https://www.elastic.co/cn/downloads/past-releases/elasticsearch-6-4-3) (yml配置文件在project-init文件夹中)
* [ElasticSearch中文分词插件](https://github.com/medcl/elasticsearch-analysis-ik/releases) (需与ES版本一致，v6.4.3，到ES路径的plugins下新建ik目录并解压缩)