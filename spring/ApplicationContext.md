# ApplicationContext
> *本节主要介绍ApplicationContext的功能，主要包裹国际化支持、事件机制和声明方式创建启动Spring容器这三大块*

<br><br>

## 目录
1. [国际化支持]()
2. [事件机制]()
3.

<br><br>

### 一、国际化支持：[·](#目录)
- ApplicationContext继承了MessageSource接口，因此具备国际化功能.


**第一步：** 配置MessageSource的bean：

```html
<!-- id必须是messageSource，actx（即ApplicationContext容器的简称）会默认自动查找名称为messageSource的bean -->
<!-- 类型必须是如下，Spring容器已经为你实现好了 -->
<bean id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource">
    <!-- ResourceBundleMessageSource有一个名为basenames的列表属性 -->
        <!-- 用来存放资源文件列表 -->
    <property name="basenames">
        <!-- 用list标签列出所有要用到的资源文件（的basename） -->
        <list>
            <value>basename1</value>
            <value>basename2</value>
            ...
        </list>
    </property>
</bean>
```

**第二步：** 使用MessageSource接口的getMessage方法实现国际化

1. actx启动后会自动读取messageSource bean，然后在getMessage方法中利用messageSource bean提供的资源文件信息进行国际化.
2. getMessage：

```java
String getMessage(String key, Object[] args, Locale loc);
// key是资源文件中的key
// args是资源文件中的参数
// loc是本地国际化信息
```

假设资源文件为：

```
hello=welcome, {0}
now=now is :{0}
```

国际化：

```java
String hello = actx.getMessage("hello", {"Peter"}, Locale.getDefault());
String now = actx.getMessage("now", {new Date()}, Locale.getDefault());
```

<br><br>

### 二、事件机制：[·](#目录)
> 可以实现Spring容器的事件处理.

- ApplicationContext提供的事件机制框架：
  - 事件源是ApplicationContext容器本身，ApplicationEvent是容器事件，必须有容器自己发布，ApplicationListener是监听器，用来监听并处理容器事件，监听器必须配置成bean.


1. **容器事件：**
  - ApplicationEvent：容器事件的根类，所有类型的容器事件都可以用它表示.
  - 当然还有其它更具体类型的容器事件，比如ContextRefreshedEvent（容器初始化时发布）等.
  - **自定义的容器事件类型必须继承它们.**
2. **监听器：**
  - ApplicationListener是监听器的根接口，容器发布的**所有**事件都可以被它监听到.
  - **自定义的监听器必须实现该接口.**
  - 该接口是函数式接口，必须实现方法onApplicationEvent方法用来处理监听到的容器事件.
3. **用bean来配置监听器：**
  - Spring框架中无需使用addXxxListener方法来注册监听器.
  - 直接在bean.xml中配置监听器，容器读取后会自动完成注册，及其低侵！！
  - 配置很简单，直接一个\<bean class="..."\>即可，容器检测到如果该类是ApplicationListener的实现类会自动注册为监听器！！
4. **发布事件触发监听器：**
  - 有些事件是自动触发的，例如容器初始化、配置加载等.
  - 其它大多数时间需要自己手动显式发布，直接调用ApplicationContext的publishEvent即可.


**实现**

容器事件：

```java
package org.lirx.app.event;

import org.springframework.context.ApplicationEvent;

public class EmailEvent extends ApplicationEvent { // 必须继承一种容器事件类
	private String addr;
	private String text;

// 如果定义构造器的话必须要传一个source参数
	// 它代表事件源，必须作为第一个参数传入
	// 并且必须用父类构造器初始化一下！！

	public EmailEvent(Object source) {
		super(source);
	}

	public EmailEvent(Object source, String addr, String text) {
		super(source);

		this.addr = addr;
		this.text = text;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
```

监听器：

```java
package org.lirx.app.listener;

import org.lirx.app.event.EmailEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class EmailNotifier implements ApplicationListener { // 一定要实现ApplicationListener接口

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof EmailEvent) { // 处理EmailEvent
			EmailEvent evt = (EmailEvent)event;
			System.out.println(evt.getAddr() + " : " + evt.getText());
		}
		else { // 处理其他Event
			System.out.println(event);
		}
	}
}
```

监听器配置：

```html
<bean class="org.lirx.app.listener.EmailNotifier"/>
```

事件发布：在Java主程序中

```java
// 这里事件源简单地用一个字符串来表示
EmailEvent evt = new EmailEvent("hello", "example@hot.mail", "this is a example");
ctx.publishEvent(evt);
```
