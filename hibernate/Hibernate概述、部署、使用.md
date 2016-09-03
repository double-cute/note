# Hibernate概述、部署、使用

<br><br>

## 目录：


<br><br>

### 一、Hibernate概述：
1. Hibernate目标：减去持久化编程任务的95%，让开发者专注于商务逻辑.
  - 尽量避免直接使用SQL语句，完全使用面向对象的方式操作数据库.
2. Hibernate的功能：
  1. 最基本的当然还是O/R映射了.
  2. 除此之外还提供了大量的数据查询和数据获取的简便方法，大幅度减少开发人员工作量.
3. Hibernate的优点：
  - 免费开源.
  - 轻量级封装，调试容易.
  - 扩展性强.
  - 开发者活跃，发展稳定.

<br><br>

### 二、部署Hibernate：
1. 下载[Hibernate-3.6.0-Final](https://sourceforge.net/projects/hibernate/files/hibernate3/3.6.0.Final/hibernate-distribution-3.6.0.Final-dist.zip/download).
2. 解压得到项目文件hibernate-distribution-3.6.0.Final：只列出需要用到的条目
        - hibernate-distribution-3.6.0.Final
          - hibernate3.jar # hibernate核心类库
          - [dir]project # 项目源码
          - [dir]documentation # API文档
          - [dir]lib # hibernate编译环境以及运行环境
            - [dir]required # hibernate运行时环境
            - [dir]jpa # jpa规范接口
            - [dir]optional # 可选插件（数据源c3p0库就包含在其中）
3. Hibernate应用需要用到的库：
        * 核心类库：hibernate3.jar
        * 运行时依赖库：[dir]lib/required/和[dir]lib/jpa/
        * c3p0数据源：[dir]lib/optional/c3p0/
        * jdbc连接器（在MySql项目中找，connector目录下）：mysql-connector-java-x.x.xx-bin.jar
        * Hibernate默认的日志工具SLF4J：slf4j-nop-1.6.1.jar
> - [dir]required目录中只提供了SLF4J的标准接口：slf4j-api-1.6.1.jar
> - 但是Hibernate项目本身并**没有**提供实现，实现下载：[SLF4J-1.6.1](http://www.slf4j.org/dist/slf4j-1.6.1.zip).
> - 解压后直接在根目录中找到slf4j-nop-1.6.1.jar的实现文件.

- 所有类库都应该包含在应用程序的lib/目录中.
- 对于Eclipse工程，则可以创建一个全局的User Library，名称就是"hibernate-3.6.0-Final".
  - 然后将以上所有类库都add进去.
  - 创建应用程序后别忘了将该User Library加入Build Path中.
    - 如果是普通应用程序则无需修改Deployment选项，因为运行测试是自动加载到lib/目录中.
    - 单如果是Web应用程序，别忘了改一下Deployment选项，因为Eclipse不会自动将User Library加载进WEB-INF/lib/中.

<br><br>

### 三、使用Hibernate：
