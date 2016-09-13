# bean的创建方式
> *有构造器创建、静态工厂创建、实例工厂创建、Spring的FactoryBean接口四种*

<br><br>

## 目录
1. [构造器创建]()
2. [静态工厂创建]()
3. [实例工厂创建]()
4. [Spring的FactoryBean接口]()

<br><br>

### 一、构造器创建：[·](#目录)
- 就是之前一直介绍的创建bean的方式.
- 这里强调一下底层调用顺序：
  - 设值注入：
    1. 调用默认构造器（无参构造器）.
    2. 调用依赖bean的构造器构造依赖bean.
    3. 调用setter设值.
    4. 返回bean实例.

<br><br>

### 二、静态工厂创建：[·](#目录)
- 不由Spring容器来创建bean，而是让Spring容器调用一个自己设计的静态工厂类来创建bean，创建后的bean还是由Spring容器管理.

```java
package org.lirx.app.animal;

public interface Being {
	public void print();
}

package org.lirx.app.animal;

public class Dog implements Being {
	private String msg;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public void print() {
		System.out.println(msg + " : " + "i'm a dog!");
	}
}

package org.lirx.app.animal;

public class Cat implements Being {
	private String msg;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public void print() {
		System.out.println(msg + " : " + "i'm a cat!");
	}
}
```

```html
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns="http://www.springframework.org/schema/beans"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans-4.0.xsd">

	<!-- 普通的构造器创建中，bean标签的class属性实际上指定是创建bean的构造器！ -->
	<!-- 只不过现在是由静态工厂类来创建，因此现在的“构造器”就变成了静态工厂类的静态方法了！ -->
		<!-- 所以现在的class变成了class+factory-method了，class当然指定的是工厂类咯！ -->
        <!-- factory-method指定的方法必须是静态的！！！ -->
	<bean id="dog" class="org.lirx.app.animal.BeingFactory" factory-method="getBeing">
		<!-- 由于现在静态方法等价于原来的默认构造器（无参构造器），但考虑到一般静态工厂方法会有参数 -->
		<!-- 而现在静态工厂方法的功能等同于构造器，因此还是使用constructor-arg来传参 -->
		<constructor-arg value="dog"/>

		<!-- 剩下的步骤就和普通的构造器创建一样了，通过静态方法获取到了初始的bean实例后再用setter设值 -->
		<property name="msg" value="狗"/>
	</bean>
	<bean id="cat" class="org.lirx.app.animal.BeingFactory" factory-method="getBeing">
		<constructor-arg value="cat"/>
		<property name="msg" value="猫"/>
	</bean>

</beans>
```

```java
package org.lirx.app;

import org.lirx.app.animal.Being;
import org.lirx.app.animal.Dog;
import org.lirx.app.user.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringTest {

	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("bean.xml");

		// 完全的面向接口编程，业务逻辑中最好不要出现具体实现类！！
		// getBean的第二个参数决定getBean返回值类型！否则就是Object类型，需要强制转换返回值
			// 这样写更加简洁和漂亮
		Being b1 = ctx.getBean("dog", Being.class);
		b1.print();
		Being b2 = ctx.getBean("cat", Being.class);
		b2.print();
	}

}
```

<br><br>

### 三、实例工厂创建：[·](#目录)
- 和静态工厂创建相对应，现在创建bean的工厂方法不是静态的了，而是对象方法，这就意味着工厂类可以产生工厂对象了.
- 所以实现起来也相当简单：
  1. 需要在配置文件中配置一个单独的bean来包装工厂对象.
  2. 然后在*利用工厂bean创建*的bean的bean标签中，利用factory-bean指定工厂bean的id，factory-method指定创建bean的工厂方法（这回是对象方法了）.
  3. 剩余的步骤和静态工厂创建完全一样（其实也和普通的构造器创建一模一样了）.

```java
package org.lirx.app.animal;

public class BeingFactory {
	public Being getBeing(String arg) { // 去掉static修饰
		if (arg.equalsIgnoreCase("dog")) {
			return new Dog();
		}
		return new Cat();
	}
}
```

```html
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns="http://www.springframework.org/schema/beans"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans-4.0.xsd">

	<!-- 工厂bean -->
	<bean id="beingFactory" class="org.lirx.app.animal.BeingFactory"/>

	<!-- 现在的默认构造器相当于是工厂bean+工厂方法 -->
	<!-- 所以原来的class就变成了factory-bean+factory-method -->
	<bean id="dog" factory-bean="beingFactory" factory-method="getBeing">

		<constructor-arg value="dog"/>

		<property name="msg" value="狗"/>

	</bean>

	<bean id="cat" factory-bean="beingFactory" factory-method="getBeing">
		<constructor-arg value="cat"/>
		<property name="msg" value="猫"/>
	</bean>

</beans>
```

<br><br>

### 四、Spring的FactoryBean接口：[·](#目录)
- 这是Spring提供的一种专业化工厂模式工具.
- 所有实现了FactoryBean的bean就叫spring factory bean.
  - 该接口中最重要的方法就是getObject，即生产方法.
  - 通过getBean获取工厂bean得到的是**getObject方法生产的产品**！

```java
getBean("spring-factory-bean的id");  // 得到的是产品！
getBean("&spring-factory-bean的id");  // 加了&以后得到的才是spring factory bean本身！
```

- FactoryBean接口中的所有方法：

```java
public interface FactoryBean<T> {
    public T getObject(); // 生产方法
    public Class<?> getObjectType(); // 返回产品的实现类
    public boolean isSingleton(); // 返回是否是单例模式，要根据getObject的实现逻辑来确定
}
```

**示例：**

```java
package org.lirx.app.animal;

public class Dog implements Being {
	@Override
	public void print() {
		System.out.println("i'm a dog!");
	}
}

package org.lirx.app.animal;

public class Cat implements Being {
	@Override
	public void print() {
		System.out.println("i'm a cat!");
	}
}

package org.lirx.app.animal;

import org.springframework.beans.factory.FactoryBean;

public class BeingFactory implements FactoryBean<Being> {
	private Being being;
	private String msg;

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public Being getObject() throws Exception {
		if (being == null) {
			if (msg.equalsIgnoreCase("dog")) {
				being = new Dog();
			}
			else {
				being = new Cat();
			}
		}

		return being;
	}

	@Override
	public Class<?> getObjectType() {
		if (msg.equalsIgnoreCase("dog")) {
			return Dog.class;
		}
		return Cat.class;
	}

	@Override
	public boolean isSingleton() { // 属于单例模式
		return true;
	}

}

```

```html
<bean id="dog" class="org.lirx.app.animal.BeingFactory">
	<property name="msg" value="dog"/>
</bean>
```

```java
Being b = ctx.getBean("dog", Being.class);
b.print();
```

- Spring其实提供了很多实用的FactoryBean接口的实例，例如TransactionProxyFactoryBean等，用于为目标bean创建事务代理.
