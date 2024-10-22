# SteamGamesSearcher后端项目

## 下载并配置elasticsearch
从官网下载，然后解压，进入bin目录，运行elasticsearch.bat，然后运行kibana.bat，访问localhost:5601，进入kibana，创建index，然后在dev-tools中运行以下代码：
按常理来说这样是没问题的，但elasticsearch可能会和你的jdk版本不匹配。这里需要你删除环境变量中的classpath，然后就可以点击elasticsearch.bat启动了
启动之后，你会访问localhost:9200，但发现无法打开，elasticsearch中也会提示你无法访问，这是因为权限管理的问题。你需要修改config/elasticsearch.yml文件，将相关的安全认证都设置为false，然后就没问题了。
