# Spring框架提供的资源访问工具
> *Spring容器时常要访问XML配置文件，除了XML文件之外还会访问各种类型的文件、二进制流等，这些被统称为**资源***.<br>
> *Resource接口提供了通用访问资源的规范，而各种实现类则提供了访问各种不同类型资源的手段.*

<br><br>

## 目录
1.

<br><br>

### 一、Resource接口：
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

### 二、创建Resource对象：

| 实现类 | 创建 | 说明 |
| --- | --- | --- |
| UrlResource | Resource r = new UrlResource("file:data.xml"); | URL必须制定协议前缀（http:、ftp:、file:(本地文件系统)） |
| ClassPathResource | Resource r = new ClassPathResource("bean.xml"); | 自动搜索CLASSPATH（如果是Web应用的话，自动搜索WEB-INF/classes） |
| FileSystemResource | Resource r = new FileSystemResource("bean.xml"); | OS本地文件系统内的路径 |
| ServletcContextResource | Resource r = new ServletContextResource(application, "WEB-INF/xxx.xml"); | 第一个参数是ServletContext对象，路径必须是相对WebContent根目录，通过该方法可以让JSP访问到WEB-INF中的内容 |
| ByteArrayResource | ByteArrayResource(byte[] ba); | 直接利用一个字节数组建立，由于访问的资源是内存中的数组，因此getFilename方法无效 |


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

### 三、
