# Hibernate配置

<br><br>

## 目录

<br><br>

### 一、Configuration对象：就是Hibernate配置本身 [·](#目录)
- org.hibernate.cfg.Configuration：
  - 该类的对象就代表Hibernate框架的配置.
  - Configuration的作用：
    1. 它的唯一作用就是为Session工厂提供产品配方.
    2. 所以它被设计成启动期间对象，一旦调用buildSessionFactory创建了一个工厂实例后它就被**丢弃**了，不能继续使用了！
      - 该过程其实就是*找*（让Hibernate框架创建）了一家工厂，然后把配方交给工厂.

**Hibernate的核心是Session，Session由SessionFactory根据Configuration提供的配方（即配置）生产出来，所以Hibernate编程的第一步就是加载配置信息得到第一个Configuration对象**

- 获取加载后的Configuration实例的方法：第一步肯定是先new出来一个空的Configuration对象咯！
  1. 从hibernate.cfg.xml配置文件中加载：
    - 调用configure方法专门从cfg.xml中加载配置信息：Configuration conf = new Configuration().configure();
    - 最推荐使用该方式，无需其它任何操作，所有配置信息统统写入cfg.xml中，全部信息都通过configure()方法读入Configuration对象中，方便管理，只需要维护cfg.xml文件即可，无需多余的工作.
  2. 从hibernate.properties文件中加载配置：
    - 该配置文件和cfg.xml意义完全相同（存放位置也和cfg.xml一样，通常两种方法二选一，不要同时用），只不过是properties格式的.
    - 默认情况下直接一个new Configuration();就是从properties配置文件中加载信息，乍一看要比上一中方法简单.
    - 但.properties配置文件最大问题就是，不允许在里面添加hbm映射信息.
    - 这意味着只能通过Java方法显式手动添加：<br>
    new Configuration().addResource("Item.hbm.xml").addClass("lirx.Item.Class").addResource("Bid.hbm.xml");
      - addResource和addClass都行，是等价的.
    - 如果hbm映射数量特别多，那么这种方式显然不可行.
    - **在Hibernate项目的/project/etc/目录中有一个hibernate.properties模板文件，里面罗列了所有的配置属性以及默认值，可以通过该模板文件了解Hibernate的所有配置属性.**
  3. 无配置文件，纯手工添加：
    - 直接通过Configuration的对象方法addResource、setProperties、setProperty方法将所有配置信息（数据库连接信息、框架信息、hbm映射信息）一句句添加.

> 由于后两种方法都属于**硬编码**方式将配置信息写在源代码中，严重**污染代码**，不利于调试（修改配置信息还需重新编译）和维护，通常建议使用第一种方法加载配置信息.

<br><br>

### 二、常用配置属性：[·](#目录)
- **JDBC连接属性**：见上一章范例：[\[步骤三： 将映射信息记录到Hibernate配置文件中\]](https://github.com/double-cute/note/blob/master/hibernate/Hibernate概述、部署、使用.md#三使用hibernate)
  - 这里需要特别强调的就是hibernate.dialect属性，即数据库方言.
    - 各大商业数据库都对标准SQL进行了不同程度的富有特色的扩展，扩展功能各不相同，使得在操作上具有一定差异.
    - Hibernate底层对这些数据库之间的差异的细节进行了隐藏，使得你在使用Hibernate的时候根本感觉不到数据库之间的差异，但前提是你必须先告诉Hibernate你使用的是哪个数据库.
    - 精确地讲Hibernate的解决方案是为每种数据库都提供了一个方言库，每个方言库都为该数据库的特别功能提供一个Hibernate统一的标准映射（统一的接口），这样就实现了数据库细节差异的隐藏.
    - 这里就只列出MySQL的方言库：

| 数据库 | 方言库 |
| --- | --- |
| MySQL | org.hibernate.dialect.MySQLDialect |
| MySQL with InnoDB | org.hibernate.dialect.MySQLInnoDBDialect |
| MySQL with MyISAM | org.hibernate.dialect.MySQLMyISAMDialect |

- **数据库连接池属性**：
  - Hibernate自带有连接池，并且提供了一个属性hibernate.connection.pool_size来设置连接池最大并发数，但该连接池仅有测试价值，实际中还是要使用一些成熟的连接池，如c3p0，因此hibernate.connection.pool_size该属性设置通常会被替换为c3p0连接池配置.
  - 配置示例键上一章：[\[步骤三： 将映射信息记录到Hibernate配置文件中\]](https://github.com/double-cute/note/blob/master/hibernate/Hibernate概述、部署、使用.md#三使用hibernate)

- **其它非常实用的配置**：
  1. hibernate.show_sql：[true/false]，是否在控制台输出Hibernate生成的SQL语句.
  2. hibernate.format_sql：[true/false]，是否将SQL语句（要在控制台输出的）转化成良好的SQL格式.
  3. hibernate.use_sql_comments：[true/false]，是否在生成的SQL语句中添加有助于调试的注释信息.
  4. hibernate.connection.autocommit：[true/false]，是否开启事务的自动提交，不建议开启，开启后将失去事务管理功能.
  5. hibernate.hbm2ddl.auto：[update/create/create-drop]，在创建SessionFactory时是否根据hbm映射自动建立数据表，udpate（如果表已经存在则不作任何处理）、create（如果表已存在则删除它重新创建）、create-drop（在create的基础上，显示关闭SessionFactory时自动将刚建的表删除）.
    - **通常后两者用于调试不用于发布版.**
