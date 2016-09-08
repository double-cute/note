# Spring简介和部署

<br><br>

## 目录
1. [Spring简介]()
2. [Spring部署]()
3. [Spring编程步骤]()

<br><br>

### 一、Spring简介：[·](#目录)
- 它是一种非常完备的MVC架构工具：它可以将业务层、表现层、持久层有机结合在一起.
- 那具体是怎么将这些层有机结合的呢？**`反转控制（也称为依赖注入）`**
  1. 首先，Spring第一大杀手锏就是可以轻松管理软件系统的各个组件（JDBC连接、Action控制器等等）.
    - 管理方式是将所有这些组件都包装成Bean（一种比JavaBean要求更加宽松的对象），组件在Spring容器里是一个个Bean，可以完全用面向对象的理念管理它们.
  2. 其次，Spring的第二大杀手锏是用配置文件管理各种组件对象（Bean对象），即直接将Bean的定义和初始值直接在配置文件中配置好，然后Spring容器读取配置文件并根据配置信息初始化（构建）Bean对象.
    - 在Java代码中**无需使用new运算符**来构建这些Bean对象，而是直接从Spring容器中获取这些已经由容器创建好的对象.
    - 如果原来是这样构建的：Person p = new Person("Peter", 15);
    - 但现在这样就行了：Person p = 容器.getBean("personBean", Person.class);<br>
    // 其中personBean是在配置文件中为该Bean取的名字，可以在Spring容器中唯一识别
    - 这种创建对象的方式最明显的好处就是**松耦合**，如果哪天软件升级了，Person又增加了tel属性，那么构造器就需要多加一个参数了，那么在所有源代码中使用new创建对象的地方全部都要改（加一个tel的参数），但如果使用第二种方式维护起来就没那么麻烦了.
    - 这种把创建对象的权利转交给容器的做法就叫做控制反转（原来创建对象的工作是由自己做的），即Inversion of Control，简写IoC.


- 总结：Spring是一种**IoC容器**（超大容器），可以将系统的各个组件（表现层、业务层、持久层）定义成**Bean**并统一放在容器中组织和管理，因此是一种非常完备的**MVC构架工具**.

<br><br>

### 二、Spring部署：[·](#目录)
1. Spring框架的[下载目录](http://repo.spring.io/release/org/springframework/spring/)，这里选择的版本是3.0.5
2. 要下载两个项目，一个是spring-framework-3.0.5-RELEASE（是框架的实现），还有一个是spring-framework-3.0.5-RELEASE-dependencies（框架的依赖库）.
  - 其中实现包解压后有三个目录：dist（即发布包，里面全是Spring核心jar库）、docs（开发文档）、projects（项目示例）、src（源码）.
  - 依赖包解压后，里面直接就是依赖jar库.
3. 开发时需要将dist中的jar和依赖包中的所有jar包含在lib/中（Web应用则是在WEB-INF/lib/中），对于Eclipse工程，可以创建一个spring-framework-3.0.5-RELEASE的User Library（包含上述的jar库）.
4. 以上就已经完成整个Spring框架的部署了，可以尽情地在代码中使用Spring框架了.

<br><br>

### 三、Spring编程步骤：[·](#目录)
1. 设计Bean类来包装各种组件.
2. 在类加载路径下（即classes/目录中，Eclipse直接在src/下）编写bean.xml配置文件，来配置Bean.
3. 在Java代码中通过容器的IoC获取Bean并使用.

**设计Bean**

```java
package org.lirx.app.user;

public class Person { // 可以是POJO，典型的低侵，假设Person是一个组件
	private String name;

	public void setName(String name) { // Spring的Bean不要求是严格的JavaBean
		this.name = name;
	}

	public void info() {
		System.out.println("hello " + name);
	}
}
```

**配置Bean（classes/bean.xml）**

```html
<?xml version="1.0" encoding="UTF-8"?>
<!-- Spring配置文件的根元素，使用spring-beans-4.0.xsd语义约束 -->
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns="http://www.springframework.org/schema/beans"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans-4.0.xsd">
	<!-- 一个bean标签就代表一个bean -->
	<!-- 而是指上bean标签是指示让Spring容器new出来一个Bean对象！ -->
    <!-- id是Spring容器识别bean的标识，Java中获取bean就是依据该名称 -->
	<bean id="person" class="org.lirx.app.user.Person">
		<!-- 可以用property标签为Bean的数据域指定初始值，Spring容器会根据初始值为用户代理初始化Bean对象 -->
		<!-- 这就是典型的反转控制IoC，即new对象以及为对象传参的权利反交给容器来完成 -->
		<property name="name" value="Peter"/>
	</bean>
</beans>
```

**通过容器的IoC获取Bean并加以利用**

```java
package org.lirx.app;

import org.lirx.app.user.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringTest { // Spring可以用于任何Java程序，包括简单的Java Application
// 和Hibernate一样，是一种低侵框架

	public static void main(String[] args) {
		// 获取Spring容器ApplicationContext，容器必须要读取bean.xml配置文件
		ApplicationContext ctx = new ClassPathXmlApplicationContext("bean.xml");
		// 利用IoC方法getBean以松耦合的方式获取对象（容器已经代理创建对象并初始化了）
		Person person = ctx.getBean("person", Person.class);
		// 接下来直接使用该Bean即可
		person.info(); // 打印出"hello Peter"
	}

}
```
