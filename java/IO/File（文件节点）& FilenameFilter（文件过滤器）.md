# File（文件节点）& FilenameFilter（文件过滤器）
> File是**java.io包**下的代表**与平台无关**的**文件和目录**.
>
> - 通过File对象只能操作文件本身，并不能操作文件的内容：
>    - 只支持文件和目录的新建、删除、重命名等操作.
>    - 修改文件内容只能使用流.
>       - 它只是**文件流的流节点**而已，仅仅是一个**存储节点**.
>
>> File的renameTo修改文件名的方法各平台之间差异较大，支持的并不好，因此摒弃不要使用.

<br><br>

## 目录

1. []()
2. []()

<br><br>

### 一、File构造器：文件-目录通用
> 使用String类型的文件路径构造.

<br>

```Java
/**  
 *  - 绝对路径、相对路径都行.
 *  - Windows平台中的路径分隔符'\'须要转义"\\".
 *  - 其内部有一个String path成员用于接受pathname.
 */
File(String pathname) {
  this.path = pathname;
}
```

<br><br>

### 二、获取文件名：文件-目录通用
> 都是**纯字符串操作**，**不会深入到文件系统中检查** 文件或目录是否存在.

- 假设：
   - File("dir\\test");
   - 当前Java程序的环境路径是D:\\doc\\eclipse\\Test\\
      - 就是运行当前程序的路径，用${ENV_PATH}代替.

<br>

```Java
// 1. 获取末端simple name
String getName();  // test

// 2. 获取this.path
String getPath() {  // dir\test
  return this.path;  // 如果构造File对象时传入的@#$sdlf
                     // 那么这里返回的就是@#$sdlf
  // 一切都是纯字符串操作
}

// 3. ${ENV_PATH} + this.path
String getAbsolutePath();  // D:\doc\eclipse\Test\dir\test

// 4. this.path去掉后缀simple name
  // 如果为空则返回null
String getParent(); // dir
// 如果this.path = xxx\yyy\dir\test，那么返回xxx\yyy\dir
// 如果this.path = test，那么返回null

// 5. 将this.path = getAbsoluePath()
File getAbsoluteFile(); // 不改变原来的File对象，生成一个新对象返回
```

<br><br>

### 三、属性检测：文件-目录通用

<br>

**1.&nbsp; 是否存在：**

```Java
boolean exists();
```

<br>

**2.&nbsp; 可读/可写：**

```Java
boolean canRead();
boolean canWrite();
```

<br>

**3.&nbsp; 是否是文件或目录：**

```Java
boolean isFile();
boolean isDirectory();
```

<br>

**4.&nbsp; 检查this.path是否是绝对路径：**

```Java
boolean isAbsolute();
```

<br><br>

### 四、文件操作：只适用于文件

<br>

**1.&nbsp; 常规属性查看：**

```Java
// 查看最新修改时间
long lastModified();

// 查看文件大小（单位是字节）
long length();
```

<br>

**2.&nbsp; 根据this.path创建文件：**

```Java
/**  根据this.path新建一个文件.
 *
 *    - 如果文件已存在则创建失败返回false.
 *      - 并不会覆盖已存在的文件.
 */
boolean createNewFile();
```

<br>

**3.&nbsp; 删除this.path指定的文件：**

```Java
// 删除失败返回false
  // 失败的原因包括文件不存在、多线程竞争等等.
boolean delete();
```

<br>

**4.&nbsp; 临时文件：**

```Java

```

<br>


    3) 创建临时文件：static File createTempFile(String prefix, String suffix, File directory);
         i. 以directory+prefix+随机数+suffix作为文件名（装载到path成员变量中）在指定目录directory下创建一个文件；
         ii. 正因为是临时，因此中间用随机数作文件名；
         iii. 例如：File tmp = File.createTempFile("mail", ".tmp", new File("dir"));  // 得到的文件tmp的path变量值为dir\mailXXXXXXX.tmp，并且在dir目录下创建了一个该文件
！！prefix要求至少三个字符，而suffix可以为null；
！！这是File类的静态方法，也就是工具方法！
    4) 一般临时文件都是在程序中临时使用的，都希望能在程序退出时就自动删除这些临时文件：void deleteOnExit();  // 注册了一个删除钩，表示在程序结束时自动删除该文件
！！该方法只要是File对象都能调用，不一定是临时文件；
！！其实临时文件也是普通文件，只不过其名字中有随机生成的数字而已；

6. 目录操作：只适用于目录
    1) 创建单个目录：boolean mkdir();  // 以path指定的名字在当前目录下创建一个目录，必须是原来不存在的，否则会false失败返回
！！该方法不能嵌套创建，比如dir1\dir2这样是不能创建，只能创建单个目录，比如dir1
    2) 嵌套创建：boolean mkdirs(); // 以path指定的名字嵌套创建多级目录，同样必须是原来不存在的，否则会false失败返回
！！只要path路径中有一级不存在就可以成功创建，比如dir1\dir2\dir3，dir1已经存在了，但是dir2不存在，那么同样可以成功创建出dir1\dir2\dir3多级目录
    3) 返回目录下所有文件（包括子目录）的名称：String[] list();
    4) 返回File对象：File[] listFiles();
    5) 利用静态方法listRoots获得操作系统的所有根目录：static File[] listRoots();
！！返回的各个File对象的path值例如：C:\、D:\等，因此调用getName方法得到的是空，之后调用getPath或者getAbsolutePath才能得到真正的路径名，其实就是一个盘符；
！！在Windows中是这样的，如果在Unix/Linux中，返回的是所有挂载到操作系统上的磁盘名称了！！

7. 文件过滤器：
    1) 可以看到在使用list和listFiles方法列出目录中的文件（和目录）时还有第二个版本的重载，即需要一个FilenameFilter的过滤器；
    2) 重载版本：
         i. String[] list(FilenameFilter filter);
         ii. File[] listFiles(FilenameFilter filter);
！！即可以通过过滤器来过滤出你想要的文件；
    3) FilenameFilter是一个函数式接口，里面只有一个方法需要实现，就是：boolean accept(File dir, String name);
         i. name是待检查的文件的名字，dir是该文件所在的目录；
         ii. 在调用list和listFiles时会将目标目录下的所有文件和目录用accept试一下，如果能接受就过滤出来并返回；
         iii. 因此需要实现accept方法来自定义过滤哪些文件；
    4) 示例：
dir.list((dir, name) -> name.endsWith(".txt") || new File(name).isDirectory());
！即我们想要的是.txt文件或者子目录都行；
！！由于是函数式接口，可以使用Lambda表达式；
