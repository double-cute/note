# Redis简介
> REmote DIctionary Server，即 **远程字典服务器** 的缩写.
>
>> 是典型的NoSQL内存数据库.

<br><br>

## 目录

1. [历史和背景]()
2. [从Redis的名字看它的基本功能和存储结构]()
3. [Redis相比传统SQL数据库的优势]()

<br><br>

### 一、历史和背景：[·](#目录)

<br>

1. 人物：
   1. 创始人：Salvatore Sanfilippo，意大利
   2. 主要代码贡献者：Pieter Norrdhuis
2. 开发语言：C
3. 背景：
   - 意大利创业公司Merzia退出一款基于MySQL的网站实时统计系统LLOOGG.
      - 结果MySQL性能不行，Salvatore亲自开发了内存数据库Redis应用于LLOOGG.
      - 从开始到现在，一直都在GitHub上开源.
4. 发展：几年间风靡全球，称为最流行的**前端高性能敏捷缓存系统**，目前的用户有
   1. 国内：新浪、知乎、阿里巴巴等等.
   2. 国外：GitHub、Stack Overflow、Flickr、**Billizard**、Instagram等等.
      - 彻底动摇了相似缓存系统产品Memcached的地位.
5. 目前：VMware赞助Redis，Salvatore和Noordhuis也加入VMware全职开发和维护Redis（包括社区环境）.

<br><br>

### 二、从Redis的名字看它的基本功能和存储结构：[·](#目录)
> REmote DIctionary Server

<br>

1. 数据的存储结构：DIctionary
   - 是典型的基于key-value的字典结构.
      - SQL全部基于二维行列表，而Redis**全部基于key-value的字典结构**.
      - 但Redis提供了丰富的数据类型，其key或value可以是以下5种类型：
         - 字符串、散列、列表、集合、有序集合.
2. 作为远程服务器：REmote Server
   - 应用可以通过TCP歇息读写Redis字典中的key对应的value.

<br><br>

### 三、Redis相比传统SQL数据库的优势：[·](#目录)

<br>

1. 从存储结构角度看：
   - 采用高级而复杂的数据结构，因此：
      1. 操作简单、直观.
         - 不像SQL语句那样晦涩和复杂.
         - 特别是SQL连接查询，在Redis中可以非常直观一条字典取值命令完成.
      2. 因此，**和应用程序的交互比SQL简单很多很多**.
2. 从内存存储的角度看：Redis是典型的内存数据库
   - 所有数据保存在内存中，性能远高于SQL数据库.
      - 普通笔记本电脑可以在Redis上1秒读写超过10万个key-value.
   - 即使要将内存数据持久化到硬盘中也提供了 **异步写入** 的功能.
      - 在持久化时不妨碍Redis继续提供缓存服务.
3. 从功能丰富的角度看：
   - Redis可以用作缓存、高性能队列、阻塞时读取、“发布/订阅”模型等.
   - 对类似产品Memcached造成严重威胁和冲击：
      1. Redis性能超级优异，几乎没有瓶颈.
      2. Redis 3.0的推出，使得Memcached几乎成为Redis的功能子集.
      3. 虽然Redis是单线程模型，而Memcached是多线程模型，但是Redis已经支持集群.
         - 其性能优势完全淹没Memcached.
4. 体积超小、简单稳定：
   1. 命令就100多条，常用的就只有 **十几条**.
   2. 源码就3万多行，而且是C语言.
   3. 一个空的Redis实例只占1MB左右，不需要维护特别多的metadata.
   4. 全球开发者100多名，版本迭代快速，发展有保障，稳得不行.
