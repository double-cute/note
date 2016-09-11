# bean的基本配置

<br><br>

## 目录
1. [beans标签]()
2. [bean标签的基本用法]()
3. [为bean取别名]()
4. [bean的作用域]()


<br><br>

### 一、beans标签：[·](#目录)
- 一个beans标签下可以定义多个bean标签，开发中应该将一组功能相互关联或者相近的bean包含在一个beans标签下统一管理.
- beans的属性用于指定其下所有bean的共同特征，它的所有属性bean也有，只不过bean的相同属性会覆盖beans的：

| 属性 | 说明 |
| --- | --- |
| defualt-lazy-init | 延迟加载 |
| default-autowire | 是否开启自动装配 |
| defualt-init-method | 指定默认初始化方法 |
| defualt-destroy-method | 指定默认析构方法 |

- Spring容器初始化时默认会加载所有的singleton bean（即单例bean），如果想关闭该功能需要将defualt-lazy-init设为true，其默认值是false.
- bean也有以上属性，只不过名称都是**去掉"default-"前缀**的！！


- **一般采用默认值即可，无需改动.**

<br><br>

### 二、bean标签的基本用法：[·](#目录)
- 属性：
  1. id：Spring容器中对bean的唯一标识，**不要包含/**.
  2. class：bean的实现类，**必须是实现类** 不能是接口，必须是**全限定类名**.

- 关于构造注入的歧义问题：
  - 构造注入之前有过简单的介绍，[[内容回顾](IoC-DI.md#三将初始值注入bean的方式)].
  - Spring类型转换机制在构造器重载之下容易发生歧义：
    - 加入构造器是Test(int a);那么构造器注入可以写成\<constructor-arg value="23"/\>，虽然"23"是字符串值，但由于Test接受的参数是int型的，Spring提供自动类型转换，可以顺利地将"23"转换成int型23.
    - 但如果又重载了一个Test(String a);那么\<constructor-arg value="23"/\>将会调用String版的而非int版的，因为"23"的原生类型毕竟是String，Spring消除歧义的方法就是根据原生类型调用相应的构造器.
  - 为了避免上述歧义，可以使用constructor-arg标签的type属性显式指定类型，该type和Hibernate的type不一样，这里的type需要指定**Java类型**！
    - 如果上面写成\<constructor-arg type="int" arg="23"/\>就会正确将其理解成int型了.

<br><br>

### 三、为bean取别名：[·](#目录)
> *虽然**id**指定了bean在容器中的唯一名称，但某些场景中还是不得不为bean取别名.*

- 使用alias标签即可：

```html
<!-- name指定要取别名的bean的id，alias属性就是别名 -->
<alias name="person" alias="Peter"/>
<!-- 可以为同一个bean取多个别名 -->
<alias name="person" alias="Tom"/>
```

<br><br>

### 四、bean的作用域：[·](#目录)
> *用bean标签的**scope**属性为bean指定作用域.*

| scope属性的合法值 | 说明 |
| --- | --- |
| singleton | 单例bean，由单例公共场所管理，Spring启动时会自动初始化 |
| prototype | 原生bean，每次getBean产生的是一个全新的bean |
| request | 每次HTTP请求都会产生一个全新的bean |
| session | 每次Session都会产生一个全兴的bean |

- *如果不指定scope属性，则默认为singleton的*.
- 单例bean可以重复利用.


- 关于request和session作用域的若干说明：
  1. 只能在Web应用中使用.
  2. 通常都是将Action控制器定义成bean，并将其放入request作用域中，这样Action bean就只在这次Http request中有效.
  3. 想让Spring框架能够在request到来时自动创建并注入request scope的bean，就必须在web.xml中使用Spring提供的监听器：

```html
<listener>
    <listener-class>org.springframework.web.context.request.RequestContextListener</listener-class>
</listener>
```
