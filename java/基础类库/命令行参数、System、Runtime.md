# System类
> 明显代表Java程序运行的OS平台，System类是**对OS平台功能的面向对象包装**.
>
>   - 开发者明显不能用System类创建对象，它**属于JVM运行的外壳**，**只能由JVM自己创建并在后台维护**.
>> 所以System提供的基本都是**静态工具方法**（**对系统调用的包装**）.

<br><br>

# 目录

1. []()
2. []()
3. []()

<br><br>



<br><br>

### 二、System类：


<br>

**功能一：查看OS平台的信息**

<br>

- 由于System类包装的是OS系统调用，因此通过它查看到的信息都是**OS平台的信息**.
  1. 这些信息显然与当前运行的Java程序无关，属于**背景环境信息**.
  2. 因此其中的信息都属于**静态信息**，**只读不能修改**.
    - **应用程序权限低于OS.**

<br>
```Java

```
    4) 除了查看平台的信息之外还可以利用System使Java程序和OS进行交互，总结一下System的常用功能：
         i. 查看OS的环境变量、系统属性；
         ii. 提供标准输入（键盘）、输出（屏幕）、错误输出（屏幕）的基础IO功能；
         iii. 垃圾回收：System的gc方法可以强制操作系统回收程序中失效的内存控件；
         iv. 获取系统时间：是系统底层的时钟时间（从UTC1970年1月1日0时开始计算的时间）；
         v. 获取Java对象精确的内存地址；

！！以下介绍的都是System的静态（static）工具方法：

2. 查看OS的环境变量和系统属性：
    1) 查看环境变量：
         i. Map<String,String> getenv();  // 返回所有的环境变量信息，以键值对的方式保存在map中
         ii. String getenv(String name);  // 根据指定的环境变量名获得环境变量的值
         iii. 示例：
public class Test {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		Map<String, String> envs = System.getenv();
		for (String name: envs.keySet()) {
			System.out.println(name + " ---> " + envs.get(name));
		}

		System.out.println(System.getenv("JAVA_HOME"));
	}
}
    2) 查看系统属性：
         i. 系统属性不仅包含一些OS的信息（用户名、操作同版本等）也包含一些Java程序要经常用到的信息（JVM虚拟机的路径、JVM运行时库的路径等）；
         ii. 这些系统属性在Java中都被命名为Java类名的风格，比如操作系统名称这一属性的属性名就是os.name，而JVM库路径这一属性名就是java.library.path；
         iii. 它们和环境变量一样都是以键值对的形式保存着，你可以将所有系统属性打印到外部文件，可以看到就是Java配置文件的格式，每条属性都是这样的：属性名=属性值
         iv. 查看系统属性的方式：
              a. 只查看指定的属性：String getProperty(String key);  // 指定属性名返回属性值
              b. 查看所有属性：Properties getProperties();  // 直接将所有属性集合保存在Properties对象中，然后利用该对象进一步查看
         v. 将全部属性导入到外部文件中查看：
              a. 想要达到这个目的必须先获得Properties对象，然后用该对象的store方法保存到外部；
              b. 原型：void Properties.store(OutputStream out, String comments);  // 是Properties的对象方法
              c. out指定了导入目的地，comments表示文件的注释信息（就是说明信息）；
              d. 导出的格式当然是Java配置文件的格式，comments将会出现在整个配置文件的开头（以#打头，表示配置文件的注释信息）；
         vi. 综合示例：
public class Test {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		Properties sysProps = System.getProperties(); // 获得属性集合
		System.out.println(System.getProperty("os.name")); // 直接获取指定的系统属性
		System.out.println(sysProps.getProperty("os.name")); // 在属性集合中获取指定属性

		for (Object key: sysProps.keySet()) { // 程序中遍历
			String name = (String)key;
			System.out.println(sysProps.getProperty(name));
		}

		sysProps.store(new FileOutputStream("props.txt"), "System Properties"); // 存储到外部文件
	}
}

3. 查看系统时间：
    1) 原型：native long currentTimeMillis();  // 可以看到是一个本地C语言调用
    2) 返回的是系统时钟时间，单位是毫秒，从UTC 1970年1月1日0时到当前时间的毫秒数，在很多应用中非常常用；

4. 标准流以及重定向：在I/O中详细讲解 -> 链接

5. 得到精确的对象地址：
    1) 我们都知道类的hashCode方法在没有覆盖以前是根据地址计算的，但是该方法可以自行覆盖，比如String的该方法就是根据本身字符序列计算的，因此并不能正确反映其地址信息（两个String只要内容相同则hashCode的相同）；
    2) 但是System的identityHashCode方法可以精确辨认地址，想要纯地址就用该方法：native int identityHashCode(Object x);  // 调用JVM内核的C代码获取对象内存的32位地址

6. 垃圾回收：
    1)


    1. Runtime类简介：
        1) 代表Java程序的运行时环境，是和Java程序相关联的，每一个运行起来的Java程序都具有一个自己Runtime对象；
        2) 和System一样，Java程序的Runtime对象也是由JVM创建的，用户无法自己创建Runtime对象；
        3) Runtime主要用来查看当前Java程序所在的JVM的信息（如JVM可使用的处理器数量、内存信息等）；
        4) Runtime还有一个重要的功能，就是可以执行操作系统的命令，这也是Java程序和OS交互的重要途径；

    2. 获取程序的Runtime对象：static Runtime Runtime.getRuntime();
    ！！由于每个Java程序只能拥有一个自己的Runtime对象，因此必然是要提供静态方法来获取该对象而禁止使用构造器来随意构造对象的；

    3. 查看运行时信息：
        1) 调用前面获得Runtime对象各种对象方法来获取运行时信息；
        2) 这里只介绍几个非常常用的运行时信息查看方法：都是Runtime的对象方法
             i. native int availableProcessors(); // 当前JVM可用的处理器数量
             ii. native long freeMemory();  // 当前JVM内可用的内存
             iii. native long totalMemory();  // 当前JVM的全部内存
             iv. native long maxMemory();  // 当前JVM的内存上限（由于JVM可以动态分配内存，因此totalMemory可以动态扩展，但是上限是静态的，无法改变）
    ！！以上内存大小的单位都是字节（返回值的单位）；

    4. 执行Shell命令：
        1) Process exec(String shellCommand);  // 执行后返回该执行子进程的句柄
    ！！可以进一步利用这个句柄得到命令执行的结果；
        2) 示例：Runtime.getRuntime().exec("dir");
        3) 当然Java重载了很多exec版本，使Java和OS的交互方式更加丰富；
