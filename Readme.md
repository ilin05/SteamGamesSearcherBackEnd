# SteamGamesSearcher后端项目



## 基础配置

* 连接本地MySQL数据库

* 修改`src/main/resources/application.properties`中的密码，改成自己MySQL数据库的密码

* 运行一遍`src/main/resources/schema.sql`，即可在本地创建数据库



## 将源数据导入

下载`games.json`文件(本来想用git lfs一同搞到github上的，结果上传过去发现里面的东西没了，保险起见还是自己操作吧)，放在`src/main/resources/static`文件夹下

然后运行测试类`SteamGamesSearcherBackEndApplicationTests`中的`storeGamesIntoMySQL`方法，即可将9万多条游戏数据存储到MySQL数据库中。



## 下载并配置elasticsearch

在官网[Elasticsearch：官方分布式搜索和分析引擎 | Elastic](https://www.elastic.co/cn/elasticsearch)上下载Elastic search压缩包并解压，然后执行bin目录下的`elasticsearch.bat`即可启动elastic search。按常理来说这样是没问题的，但elasticsearch可能会和你的jdk版本不匹配。这里需要你删除环境变量中的classpath（网上说好像jdk17以上的版本其实不需要配置这个classpath，所以删掉后是没关系的），然后重启电脑。之后就可以点击elasticsearch.bat启动了。

启动之后，你会访问localhost:9200，但发现无法打开，elasticsearch中也会提示你无法访问，这是因为权限管理的问题。你需要修改config/elasticsearch.yml文件，将相关的安全认证都设置为false，然后就没问题了。具体修改如下所示：

```shell
xpack.security.enabled: false

xpack.security.http.ssl:
	enabled: false
```

不出意外的话，现在就能执行`elasticsearch.bat`了，然后打开`localhost:9200`，可以看到如下类似的信息：

```shell
{
    "name": "LEFTPOUND",
    "cluster_name": "elasticsearch",
    "cluster_uuid": "i7cCJEtKQK-5YGh0n6iYeQ",
    "version": {
        "number": "8.15.3",
        "build_flavor": "default",
        "build_type": "zip",
        "build_hash": "f97532e680b555c3a05e73a74c28afb666923018",
        "build_date": "2024-10-09T22:08:00.328917561Z",
        "build_snapshot": false,
        "lucene_version": "9.11.1",
        "minimum_wire_compatibility_version": "7.17.0",
        "minimum_index_compatibility_version": "7.0.0"
    },
    "tagline": "You Know, for Search"
}
```



## 下载并配置ik分词器

**默认的中文分词是将每个字看成一个词**（不使用用IK分词器的情况下），所以我们需要安装中文分词器ik来解决这个问题。

下载地址：https://github.com/medcl/elasticsearch-analysis-ik/releases

需要下载**和Elastic Search对应版本**的ik分词器。下载压缩包然后解压到`elastic search`文件夹下的`plugins`文件夹下，并将解压出的文件夹命名为`ik`。



## 下载并配置Kibana

从[Install Kibana on Windows | Kibana Guide [8.15\] | Elastic](https://www.elastic.co/guide/en/kibana/current/windows.html)下载Kibana。这个是可视化执行查询的一个配套应用。

下载**和Elastic Search对应的版本**之后，同样地，在bin目录下执行`kibana.bat`，你可能同样会发现无法执行。

可以对症下药在网上搜索解决方法。我遇到的问题应该是没有权限，需要如下修改`config/kibana.yml`中的配置：

```shell
server.port: 5601  
server.hosts: ["http://localhost:9200"] 
elasticsearch.hosts: ["http://localhost:9200"] 
```

然后就可以启动`kibana.bat`了。之后访问[Console - Dev Tools - Elastic](http://localhost:5601/app/dev_tools#/console)，看看能不能用。
