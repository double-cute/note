# Spring框架提供的资源访问工具
> *Spring容器时常要访问XML配置文件，除了XML文件之外还会访问各种类型的文件、二进制流等，这些被统称为**资源***.<br>
> *Resource接口提供了通用访问资源的规范，而各种实现类则提供了访问各种不同类型资源的手段.*

<br><br>

## 目录
1. [Resource接口]()
2. [创建Resource对象]()
3. [ResourceLoader接口：资源加载器]()
4. [Spring容器加载bean.xml配置的5种location写法]()
5. [ResourceLoaderAware：使bean通过IoC方式自动持有一个ResourceLoader]()
6. [往bean中注入Resource]()

<br><br>

### 一、Resource接口：[·](#目录)
- 是Spring提供的一种比JRE更强大的访问资源的工具，但它只是一个抽象接口，抽象除了访问资源的各种通用的接口方法.
- 而Spring也提供了若干Resource的实现类，以完成对不同类型资源的访问：

        1. UrlResource：访问网络资源
        2. ClassPathResource：访问CLASSPATH中的资源
        3. FileSystemResource：访问OS文件系统中的资源
        4. ServletContextResource：访问ServletContext中的资源
        5. InputStreamResource：访问输入流中的数据
        6. ByteArrayResource：访问字节buffer中的数据

- Spring对资源访问的设计也是一种典型的面向接口变成的设计，使业务逻辑和实现分离：
  1. 在业务逻辑中指使用Resource接口.
  2. 然后将具体的资源访问实现类包装成bean.
  3. 访问资源时用Resource引用来从容器中获取具体的Resource实现类以完成对资源的访问.
  4. 在业务逻辑的代码中完全看不出具体访问什么资源，以及访问资源的具体方法是什么.


- Resource中定义的通用资源访问方法：

| 方法 | 说明 |
| --- | --- |
| boolean exists(); | 指向的资源是否存在 |
| String getFilename(); | 返回文件名 |
| String getDescription(); | 返回资源描述，通常在异常时输出该信息 |
| boolean isOpen(); | 资源是否处于被打开状态，不允许对打开的资源反复多次读取（文件指针混乱），读完之后应该显示关闭资源 |
| long contentLength(); | 返回资源的大小（字节） |
| InputStream getInputStream(); | 获取资源的输入流 |
| URL getURL(); | 如果资源是网络资源则返回URL |
| File getFile(); | 如果资源师文件则返回文件句柄 |

- **Resource不仅可以在Spring项目中使用，也单独使用，只不过单独使用会使代码于Spring框架相耦合.**

<br><br>

### 二、创建Resource对象：[·](#目录)

| 实现类 | 创建 | 说明 |
| --- | --- | --- |
| UrlResource | Resource r = new UrlResource("http://xxx/data.xml"); | URL必须制定协议前缀（http:、ftp:、file:(本地文件系统)） |
| ClassPathResource | Resource r = new ClassPathResource("bean.xml"); | 自动搜索CLASSPATH（如果是Web应用的话，自动搜索WEB-INF/classes） |
| FileSystemResource | Resource r = new FileSystemResource("bean.xml"); | OS本地文件系统内的路径 |
| ServletcContextResource | Resource r = new ServletContextResource(**application**, "WEB-INF/xxx.xml"); | 第一个参数是ServletContext对象，路径必须是相对WebContent根目录，通过该方法可以让JSP访问到WEB-INF中的内容 |
| ByteArrayResource | ByteArrayResource(**byte[] ba**); | 直接利用一个字节数组建立，由于访问的资源是内存中的数组，因此getFilename方法无效 |


- **关于InputStreamResource和ByteArrayResource的讨论：**
  1. InputStreamResource直接从输入流中读取，限制非常多，并且Spring对其的实现**效率不高**.
  2. 还是因为InputStreamResource利用的是输入流，因此**无法多次读取**（isOpen一直为true）.
    1. 但ByteArrayResource由于是数组因此**效率非常高**.
    2. 同时也不存在多次读取的问题，**可以随意读**.
    3. 字节数组是非常**通用且常用**的数据传输方法，Socket通信、线程通信基本都使用字节数组交换信息.
- 因此通常的做法是：
  1. 尽量**少用InputStreamResource**.
  2. 如果刚好碰到使用InputStreamResource的情况，那么也应该将其**转换为ByteArrayResource后再进行处理**.

<br><br>

### 三、ResourceLoader接口：资源加载器  [·](#目录)
- 实现了ResourceLoader的类都可以作为**资源加载器**，因为接口中规定了必须实现：Resource getResource(String location);
  - 即通过getResource来加载资源.

- **ApplicationContext就实现了该接口**，因此Spring容器本身就是一个资源加载器：
  - 因此完全不需要自己new出来Resource对象来加载资源，而是直接通过ApplicationContext的getResource来加载资源.
- **getResource的实现逻辑：**

| 参数String location的形式 | 加载规则 |
| --- | --- |
| 以"classpath:"作为前缀 | 用ClassPathResource加载资源 |
| 以"file:"作为前缀 | 以UrlResource访问本地文件系统资源（注意**不是**FileSystemResource） |
| 没有任何前缀 | 以ApplicationContext的实现类为依据决定使用哪种Resource实现类 |

- 在没有任何前缀的情况下：

| ApplicationContext的实现类 | 采用的Resource的实现类 |
| --- | --- |
| FileSystemXMLApplicationContext | FileSystemResource |
| ClassPathXmlApplicationContext | ClassPathResource |
| XmlWebApplicationContext | ServletContextResource |

**因此，在初始化ApplicationContext本身时也可以利用上述规则，即，用ApplicationContext的实现类new出Spring容器时，构造器的参数（bean.xml）本身就是资源的location.**

<br><br>

### 四、Spring容器加载bean.xml配置的5种location写法：[·](#目录)

1. [三、]所介绍的普通规则.
2. "classpath*:"前缀：
  - **只能用于ApplicationContext加载bean.xml，不能用于普通的Resource加载资源.**
  - 使用ClassPathResourc加载bean.xml
  - 会递归搜索classes/中全部符合条件的配置文件，也就是说可以同时加载**多个**配置文件.
  - "classpath:"前缀默认只会搜索classes/下的第一个符合要求的配置文件.
  - 例如：
    1. classpath:po.xml  --  只会搜索classes/下第一个po.xml
    2. classpath*:bean.xml  --  会递归搜索classes/中所有名为bean.xml的文件，包括其子目录classes/aa/下也会.
3. 使用通配符*：
  - 例如bean*.xml
  - 则会在相应目录下寻找所有以"bean"作为前缀的配置文件.
4. "classpath*:"前缀和*通配符相结合.
5. "file:前缀"
  - FileSystemXMLApplicationContext并不区分相对路径和绝对路径，**全部都当成相对路径处理**.
  - 也就是说：

```java
// 两者都是相对路径bean.xml
ApplicationContext actx = new FileSystemXMLApplicationContext("bean.xml");
ApplicationContext actx = new FileSystemXMLApplicationContext("/bean.xml");
```
- 如果硬要在FileSystemXMLApplicationContext下加载bean.xml指定绝对路径，必须使用file:前缀

```java
// 还是相对路径
ApplicationContext actx = new FileSystemXMLApplicationContext("file:bean.xml");
// 这回是绝对路径了
ApplicationContext actx = new FileSystemXMLApplicationContext("file:/bean.xml");
```

<br><br>

### 五、ResourceLoaderAware：使bean通过IoC方式自动持有一个ResourceLoader  [·](#目录)
- 实现了ResourceLoaderAware接口的bean被Spring检测到时，会自动将一个实现了ResourceLoader的资源加载器引用传给该bean，使其持有.
  - 只不过Spring容器传给bean的是**Spring容器本身**，因为Spring容器本身实现了ResourceLoader接口.
- 实现了aware接口，就必须在Bean类中维护一个ResourceLoader对象引用，并提供setter和getter对象.
  - Spring容器通过setter将自身传给该bean.

<br><br>

### 六、往bean中注入Resource：
- 不管是通过new XxxResource的方式还是直接通过ApplicationContext.getResource的方式都会使资源的location硬编码到业务逻辑代码中，非常不合理.
  - 最好的方式是将Resource作为以来注入到bean中，将资源的location放在配置文件中管理.

- Spring对Resource注入bean的支持：
  - Bean类中维护一个Resource属性（面向接口）

```java
public class XxxBean {
    private Resource res;
    // res's setter & getter
    ...
}
```

- bean.xml中直接给出资源location即可，**无须交代Resource**：

```html
<bean id="xxx" class="org.lirx.app.xxxBean">
    <!-- 因为一旦Spring容器检测到res域的类型是Resource，就会自动将Spring容器本身set给该bean -->
    <!-- 因此无须再ref到一个Resource实现类bean上了，直接给出locatin即可 -->
        <!-- location的书写规则同ApplicationContext这个ResourceLoader（由容器本身实现类决定） -->
    <property name="res" value="classpath:test.xml"/>
    ...
</bean>
```
