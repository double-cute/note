# Spring容器的接口和实现

<br><br>

## 目录：
1. [Spring容器的基础接口](#一spring容器的基础接口)
2. [容器实现类](#二容器实现类)
3. [BeanFactory规范为Spring容器定下的常用方法](#三beanfactory规范为spring容器定下的常用方法)


<br><br>

### 一、Spring容器的基础接口：[·](#目录)
- Spring容器最基础的接口（也就是最上层的接口）是BeanFactory，但通常用的最多的是其子接口ApplicationContext，后者功能更强大的，前者更抽象.
  - 可以看到，从"BeanFactory"这个名字看出，Spring容器其实就是巨大的Bean的工厂，非常形象！
  - ApplicationContext称为Spring上下文，是BeanFactory更加具体的描述，它才是最常用的Spring容器规范.

<br><br>

### 二、容器实现类：[·](#目录)
**BeanFactory实现类：XmlBeanFactory**

- 看上去好像很别扭，其实意思是从XML文件加载配置的BeanFactory.
- 其最常用的就是构造器了，从构造器角度看就是从XML中读取配置并初始化容器.

初始化：需要用Spring提供的Resource工具读取XML配置进行初始化
```java
// 一下两种读取XML配置的方法二选一
InputStreamResource isr = new FileSystemResource("bean.xml"); // 从文件系统中读取
ClassPathResource cpr = new ClassPathResource("bean.xml"); // 从CLASSPATH中读取

XmlBeanFactory factory = new XmlBeanFactory(isr | cpr);
```

**ApplicationContext实现类：FileSystemXMLApplicationContext、ClassPathXmlApplicationContext**

- 使用起来更加方便，无需Resource加载配置，可以直接通过构造器加载.

```java
ApplicationContext actx = new FileSystemXMLApplicationContext("bean.xml"); // 从文件系统加载
ApplicationContext actx = new ClassPathXmlApplicationContext("bean.xml"); // 从CLASSPATH中加载

// 还有同时加载多个配置文件的重载版本，ClassPathXmlApplicationContext也一样
ApplicationContext actx = new FileSystemXMLApplicationContext(new String[] {"bean.xml", "service.xml"});
```

<br><br>

### 三、BeanFactory规范为Spring容器定下的常用方法：[·](#目录)
> 因此可以在BeanFactory以及ApplicationContext的所有实现类中使用

1. 获取bean：根据id和type获取，name就是id，requiredType就是type
  1. Object getBean(String name); // 根据id获取bean
  2. <T> T getBean(Class<T> requiredType); // 获取指定type的唯一的那个bean
  3. <T> T getBean(String name, Class requiredType); // 双条件精确获取
2. boolean containsBean(String name); // 是否包含id为name的bean
3. Class<?> getType(String name); // 获取id为name的bean的类型
