# Hibernate概述、部署、使用

<br><br>

## 目录：
1. [Hibernate概述](#一hibernate概述)
2. [部署Hibernate](#二部署hibernate)
3. [使用Hibernate](#三使用hibernate)


<br><br>

### 一、Hibernate概述：[·](#目录)
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

### 二、部署Hibernate：[·](#目录)
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

- Hibernate应用需要用到的库：
  1. 核心类库：hibernate3.jar
  2. 运行时依赖库：[dir]lib/required/和[dir]lib/jpa/
  3. c3p0数据源：[dir]lib/optional/c3p0/
  4. jdbc连接器（在MySql项目中找，connector目录下）：mysql-connector-java-x.x.xx-bin.jar
  5. Hibernate默认的日志工具SLF4J：slf4j-nop-1.6.1.jar

> [dir]required/目录中只提供了SLF4J的标准接口slf4j-api-1.6.1.jar，但是Hibernate项目本身并**没有**提供实现，实现下载[SLF4J-1.6.1](http://www.slf4j.org/dist/slf4j-1.6.1.zip)，解压后直接在根目录中找到slf4j-nop-1.6.1.jar的实现文件.

- 所有类库都应该包含在应用程序的lib/目录中.
- 对于Eclipse工程，则可以创建一个全局的User Library，名称就是"hibernate-3.6.0-Final".
  - 然后将以上所有类库都add进去.
  - 创建应用程序后别忘了将该User Library加入Build Path中.
    - 如果是普通应用程序则无需修改Deployment选项，因为运行测试是自动加载到lib/目录中.
    - 单如果是Web应用程序，别忘了改一下Deployment选项，因为Eclipse不会自动将User Library加载进WEB-INF/lib/中.

<br><br>

### 三、使用Hibernate：[·](#目录)
- Hibernate编程步骤：
  1. 设计持久化对象PO.
  2. 将PO对象映射到一张数据表（映射之后，一个PO对象就代表表中的一行数据了）.
  3. 将映射信息记录到Hibernate配置文件中（所有映射都需要记录才能被Hibernate识别）.
  4. 在应用中使用PO进行数据库操作（持久化操作）.

**步骤一：** 设计PO [·](#目录)

1. PO在Hibernate中是一种超级低侵设计，完全允许是一个POJO，无需继承或实现任何Hibernate类和接口.
2. 但它至少是一个类JavaBean，表中记录的各个列在PO中体现为数据域.
  - 即每一列属性都必须要有一个PO的数据成员相对应.
  - 这样，对数据成员的操作就相当于对列的操作.
  - 因此，至少要为这些数据域提供getter和setter.

```Java
package org.crazyit.app.domain;

public class News
{
	private Integer id;
	private String title;
	private String content;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return this.content;
	}
}
```

**步骤二：** PO到表的映射 [·](#目录)

1. 还要指定PO映射到哪张数据表，否则PO没有任何用处.
  - 所以：**PO = POJO + 映射**
2. PO -> 表的的映射由Hibernate映射文件指定，是标准XML文件，后缀约定为.hbm.xml
  - hbm即Hibernate Mapping的缩写.
3. 一个PO必须要有一个hbm文件，因此PO和hbm之间是一一对应的关系，所以PO的hbm应该就放在和PO的相同路径下.
  - 即hbm文件应该和PO编译生成的.class文件放在同一个位置.
  - 并且hbm文件的名称也必须和PO的类名相同，例如：News.class -- News.hbm.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- Hibernate DTD信息，模板，所有都一样，包括Hibernate5.0也和这个一样 -->
<!DOCTYPE hibernate-mapping PUBLIC
	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
<hibernate-mapping package="org.crazyit.app.domain"> <!-- 指定PO所在的Java包路径 -->
	<class name="News" table="news_table"> <!-- 最重要的就是这句，指明了PO到表的映射 -->
		<id name="id">
			<generator class="identity"/>
		</id>
	</class>
</hibernate-mapping>
``` [+](#三使用hibernate)

**步骤三：** 将映射信息记录到Hibernate配置文件中 [·](#目录)

1. 虽然PO有了，映射也有了，但是Hibernate还是不知道谁是PO，必须要在Hibernate的配置文件中告诉它哪些是PO，在哪里能找到他们.
2. 方便的是hbm映射文件中不仅指明了PO的类（Java包路径和类名）也指明了映射关系，所以只需要把所有的hbm文件罗列在Hibernate配置文件中即可.
  - 例如：\<mapping resource="org/crazyit/app/domain/News.hbm.xml"/>
  - 最重要的就是要指出hbm文件的位置.
3. 配置文件中不仅需要罗列hbm文件，同时也需要配置一些全局性的、重要的、基础的内容，特别是数据库连接信息等.
4. Hibernate配置文件默认名是hibernate.cfg.xml，也是一个标准的XML文件，并且应该放在classes/目录下.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- Hibernate DTD信息，模板，所有都一样，包括Hibernate5.0也和这个一样 -->
<!DOCTYPE hibernate-configuration PUBLIC
	"-//Hibernate/Hibernate Configuration DTD 3.0//EN"
	"http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
	<session-factory>

    <!-- 数据库连接信息 -->
		<!-- 指定连接数据库所用的驱动 -->
		<property name="connection.driver_class">com.mysql.jdbc.Driver</property>
		<!-- 指定连接数据库的url，其中hibernate是本应用连接的数据库名 -->
		<property name="connection.url">jdbc:mysql://localhost:3306/hibernate</property>
		<!-- 指定连接数据库的用户名 -->
		<property name="connection.username">root</property>
		<!-- 指定连接数据库的密码 -->
		<property name="connection.password">1234</property>

    <!-- 数据源连接池配置信息 -->
		<!-- 指定连接池里最大连接数 -->
		<property name="hibernate.c3p0.max_size">20</property>
		<!-- 指定连接池里最小连接数 -->
		<property name="hibernate.c3p0.min_size">1</property>
		<!-- 指定连接池里连接的超时时长 -->
		<property name="hibernate.c3p0.timeout">5000</property>
		<!-- 指定连接池里最大缓存多少个Statement对象 -->
		<property name="hibernate.c3p0.max_statements">100</property>
		<property name="hibernate.c3p0.idle_test_period">3000</property>
		<property name="hibernate.c3p0.acquire_increment">2</property>
		<property name="hibernate.c3p0.validate">true</property>

    <!-- Hibernate框架配置 -->
		<!-- 指定数据库方言 -->
		<property name="dialect">org.hibernate.dialect.MySQL5Dialect</property>
		<!-- 根据需要自动创建数据表 -->
		<property name="hbm2ddl.auto">update</property><!--①-->
		<!-- 显示Hibernate持久化操作所生成的SQL -->
		<property name="show_sql">true</property>
		<!-- 将SQL脚本进行格式化后再输出 -->
		<property name="hibernate.format_sql">true</property>


	<!-- 重要！！！罗列所有持久化类的类名 -->
		<mapping resource="org/crazyit/app/domain/News.hbm.xml"/>

	</session-factory>
</hibernate-configuration>
``` [+](#三使用hibernate)

**步骤四：** 在应用中使用PO进行持久化操作 [·](#目录)

操作步骤：

1. 加载hibernate.cfg.xml配置.
2. 获取SessionFactory标签.
3. 创建Session.
4. 利用Session打开一个事务.
5. 在事务中使用PO进行持久化操作（但这些操作都是逻辑操作）.
6. 保存操作（其实就是将操作编译成数据库可执行的代码）.
7. 提交事务（将所有操作，都保存在缓存中，一次性提交给数据库执行）.
8. 先关闭事务，再关闭Session，最后关闭SessionFactory.

- 回顾hibernate.cfg.xml配置，所有的配置信息都放在session-factor标签下，可以看出来Hibernate的运行靠的是**"session"** .
- session：就是一次会话，会话和Web应用的session不同，这里session的会话双方是应用程序和数据库，session就表示一次和数据库之间的连接.
  - 连接是需要大量信息的，比如数据库登陆信息、连接池配置信息、此次连接包含哪些PO等.
  - session-factory就是生产session的工厂，工厂生产产品是要有详细的制造配方，而session-factory标签中的配置信息就是制造session的配方.
- 所以第1到3步的含义就是，1.（加载获得配方表），2.（将配方表交给工厂），3.（工厂根据配方表生产session）.

```java
package lee;

import org.crazyit.app.domain.News;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class NewsManager
{
	public static void main(String[] args)
		throws Exception
	{
		// 加载获取配方
		Configuration conf = new Configuration().configure();
		// 把配方交给工厂
		SessionFactory sf = conf.buildSessionFactory();
		// 工厂根据配方生产出一个session
		Session sess = sf.openSession();

		// 开始事务
		Transaction tx = sess.beginTransaction();
		// 创建PO
		News n = new News();
		// 操作PO
		n.setTitle("疯狂Java联盟成立了");
		n.setContent("疯狂Java联盟成立了，" + "网站地址http://www.crazyit.org");
		// 保存（编译操作）
		sess.save(n);
		// 提交事务
		tx.commit();

		// session和工厂
		sess.close();
		sf.close();
	}
}
```

- 可以看到Hibernate程序**无需编写SQL语句！** [+](#三使用hibernate)
