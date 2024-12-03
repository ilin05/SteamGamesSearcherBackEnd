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





**游戏的tag共有452个，按数量做如下排序：**

```
Indie, Singleplayer, Action, Casual, Adventure, 2D, Strategy, Simulation, RPG, Puzzle, Atmospheric, 3D, Early Access, Pixel Graphics, Story Rich, Colorful, Exploration, Cute, First-Person, Arcade, Multiplayer, Fantasy, Funny, Shooter, Retro, Horror, Platformer, Family Friendly, Anime, Sci-fi, Action-Adventure, Relaxing, Female Protagonist, Difficult, VR, Third Person, Survival, Top-Down, Open World, Stylized, Controller, Combat, Great Soundtrack, Comedy, Visual Novel, 2D Platformer, FPS, Violent, Mystery, Co-op, Free to Play, Dark, Minimalist, Physics, Realistic, Cartoony, Psychological Horror, Choices Matter, Point & Click, Linear, Gore, Sandbox, PvP, Multiple Endings, Sports, Space, Side Scroller, Rogue-like, Old School, Tactical, Building, PvE, Rogue-lite, Puzzle-Platformer, Character Customization, Management, Hidden Object, Hand-drawn, Action RPG, Sexual Content, Magic, Logic, Racing, Nudity, Local Multiplayer, Shoot 'Em Up, 3D Platformer, Procedural Generation, Turn-Based Strategy, Survival Horror, Cartoon, Futuristic, Bullet Hell, Medieval, Turn-Based Combat, Online Co-Op, Crafting, Turn-Based Tactics, Walking Simulator, Drama, Interactive Fiction, Choose Your Own Adventure, Hack and Slash, Action Roguelike, Zombies, 1990's, Resource Management, Local Co-Op, Score Attack, JRPG, Dungeon Crawler, Replay Value, Education, Dark Fantasy, Surreal, War, Historical, Immersive Sim, Turn-Based, Post-apocalyptic, Top-Down Shooter, Nature, Emotional, Base-Building, Stealth, Isometric, Romance, Text-Based, Fast-Paced, Massively Multiplayer, Short, Card Game, Abstract, Classic, Clicker, RTS, Precision Platformer, Military, 1980s, 2.5D, Third-Person Shooter, Tower Defense, Investigation, Board Game, Memes, Detective, Narration, RPGMaker, Dating Sim, Robots, Aliens, Cyberpunk, Perma Death, Tabletop, Cinematic, Driving, VR Only, Life Sim, Dark Humor, Economy, Arena Shooter, Time Management, Mature, 4 Player Local, Flight, Psychological, Thriller, Strategy RPG, Demons, Real Time Tactics, City Builder, Experimental, Conversation, Beat 'em up, Psychedelic, Runner, Fighting, LGBTQ+, Metroidvania, Music, Wargame, Tactical RPG, Nonlinear, Supernatural, Tutorial, Competitive, Team-Based, Collectathon, Lore-Rich, Level Editor, Artificial Intelligence, Comic Book, Idler, Twin Stick Shooter, Automobile Sim, Loot, Dystopian , Party-Based RPG, Parkour, Modern, Utilities, Destruction, Grid-Based Movement, 2D Fighter, Souls-like, Hentai, Match 3, Rhythm, Cats, Alternate History, Deckbuilding, Design & Illustration, Moddable, CRPG, Inventory Management, Crime, Mythology, Space Sim, Beautiful, Card Battler, Soundtrack, Grand Strategy, World War II, Science, Philosophical, Dark Comedy, Noir, Mystery Dungeon, 3D Fighter, Character Action Game, Lovecraftian, Split Screen, Word Game, NSFW, Swordplay, Farming Sim, Colony Sim, Automation, Mouse only, Creature Collector, e-sports, 6DOF, Voxel, 3D Vision, Dragons, Software, Vehicular Combat, Solitaire, Bullet Time, Hero Shooter, Battle Royale, Mechs, Agriculture, Parody , Combat Racing, Capitalism, Open World Survival Craft, Spectacle fighter, America, Blood, Time Manipulation, Animation & Modeling, Gun Customization, Class-Based, Sokoban, MMORPG, Steampunk, FMV, Addictive, Hex Grid, God Game, Political, Conspiracy, Gothic, Martial Arts, Ninja, Game Development, Co-op Campaign, Pirates, Tanks, Otome, Real-Time, Trading, Auto Battler, Underground, Satire, Quick-Time Events, Dog, Mining, Time Travel, Cooking, Programming, Looter Shooter, 4X, Underwater, Remake, Hacking, Hunting, Dynamic Narration, Cult Classic, Dinosaurs, Fishing, Politics, Escape Room, Real-Time with Pause, Faith, Minigames, Naval, Vampire, Political Sim, Western, Video Production, Trading Card Game, Party Game, Superhero, Narrative, Transportation, Assassin, Immersive, Action RTS, Illuminati, Typing, MOBA, Touch-Friendly, Time Attack, Cozy, Asynchronous Multiplayer, Trivia, Trains, On-Rails Shooter, Cold War, Roguelike Deckbuilder, Party, Audio Production, Snow, Traditional Roguelike, Software Training, Heist, Archery, Offroad, Naval Combat, Diplomacy, Music-Based Procedural Generation, Experience, Football, Kickstarter, Villain Protagonist, Soccer, Sailing, Mars, Wholesome, Chess, Foreign, Nostalgia, GameMaker, Gambling, Horses, Sequel, Sniper, Boxing, Photo Editing, Episodic, World War I, Golf, Spelling, Jet, Unforgiving, Motorbike, Outbreak Sim, Transhumanism, Werewolves, Web Publishing, Rome, Pinball, Farming, Bikes, Silent Protagonist, Epic, Roguevania, Spaceships, Basketball, Asymmetric VR, Medical Sim, Crowdfunded, Submarine, LEGO, Social Deduction, Movie, Games Workshop, 360 Video, Mini Golf, Ambient, Electronic Music, Vikings, Based On A Novel, Baseball, Dungeons & Dragons, Gaming, Wrestling, Mod, Warhammer 40K, Tennis, Pool, Lemmings, Motocross, Intentionally Awkward Controls, Skateboarding, Cycling, Boomer Shooter, Hockey, Instrumental Music, Jump Scare, Skating, Skiing, Bowling, Football (Soccer), TrackIR, Documentary, Boss Rush, Rock Music, 8-bit Music, Snowboarding, Voice Control, Musou, Job Simulator, BMX, Masterpiece, Electronic, ATV, Hardware, Well-Written, Cricket, Football (American), Benchmark, Lara Croft, Reboot, Feature Film, Volleyball, Steam Machine, Shop Keeper, Mahjong, Coding, Extraction Shooter, Birds, Rugby, Fox, Dwarf, Hobby Sim, Tile-Matching, Batman, Snooker, Elf
```

展开高级选项之后，应该显示Singleplayer, Action, Casual, Adventure, 2D, Strategy, Simulation, RPG, Puzzle, Atmospheric, 3D, Early Access, Pixel Graphics这些，其他标签可以搜索得到。



**所有语言有这些：**

```shell
English, Simplified Chinese, German, French, Russian, Spanish - Spain, Japanese, Italian, Korean, Traditional Chinese, Portuguese - Brazil, Polish, Turkish, Spanish - Latin America, Dutch, Portuguese, Czech, Ukrainian, Swedish, Thai, Arabic, Hungarian, Danish, Norwegian, Finnish, Portuguese - Portugal, Romanian, Greek, Vietnamese, Bulgarian, Indonesian, Hindi, Catalan, Malay, Slovak, Serbian, Hebrew, Lithuanian, Croatian, Latvian, Estonian, Basque, Bangla, Filipino, Persian, Belarusian, Azerbaijani, Slovenian, Albanian, Icelandic, Galician, Afrikaans, Bosnian, Urdu, Georgian, Kazakh, Mongolian, Uzbek, Welsh, Irish, Macedonian, Tamil, Swahili, Armenian, Zulu, Luxembourgish, Yoruba, Kinyarwanda, Malayalam, Hausa, Gujarati, Kyrgyz, Punjabi (Gurmukhi), Tajik, Tatar, Turkmen, Igbo, Kannada, Maltese, Nepali, Sotho, Sinhala, Uyghur, Scots, Xhosa, Maori, Marathi, Odia, Telugu, Amharic, Assamese, Khmer, Quechua, Konkani, Punjabi (Shahmukhi), Sindhi, Sorani, Tigrinya, Dari, Cherokee, Valencian, Wolof, Tswana
```

展开高级选项后，应该显示English, Simplified Chinese, German, French, Russian, Spanish - Spain, Japanese这几个，其他语言可以搜索得到。



**指定标签计划：**

* tags: 先展示十个可供选择，用户可以搜索别的标签
* winSupport, linuxSupport, macSupport
* price: 指定范围
* supported_language: English, French, Germany, Chinese

