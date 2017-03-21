# File（文件节点）
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

1. [File构造器：文件-目录通用](#一file构造器文件-目录通用--)
2. [获取文件名：文件-目录通用](#二获取文件名文件-目录通用--)
3. [属性检测：文件-目录通用](#三属性检测文件-目录通用--)
4. [文件操作：只适用于文件](#四文件操作只适用于文件--)
5. [目录操作：只适用于目录](#五目录操作只适用于目录--)

<br><br>

### 一、File构造器：文件-目录通用  [·](#目录)
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

### 二、获取文件名：文件-目录通用  [·](#目录)
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

### 三、属性检测：文件-目录通用  [·](#目录)

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

### 四、文件操作：只适用于文件  [·](#目录)

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
/**  是File的静态工具方法
 *   
 *    - 创建一个this.path = ${directory.path}\${prefix}${19位随机数}${suffix}的File对象
 *
 *      1. 由于该方法真的会创建一个文件，因此directory指定的路径必须存在，否则将抛出异常.
 *        - 如果directory省略，则direcotry隐式默认为（Windows）：C:\Users\ioi\AppData\Local\Temp
 *          - Linux默认为：
 *        - 如果directory为空""，则direcotry默认为**当前运行路径的根目录**.
 *          - 例如：当前程序是在D:\xxx\dir\app中运行，那么directory默认为D:
 *        - directory可以使用相对路径：.（当前目录）、..（上级目录）
 *
 *      2. prefix必须≥3个字符，否则将抛出异常.
 *        - prefix中不能包含文件分隔符！否则会出现未知异常！
 *        - 只能把prefix当成simple name的前缀，文件分隔符只能在directory中指定.
 *
 *      3. suffix可以为null，字符数不限.
 *        - 同样不能包含文件分隔符，只能当做simple name的后缀.
 *
 *    - 创建出来的文件和普通文件没有任何区别！这个方法相当于两个方法的合体：
 *      1. File f = new File(${directory.path}\${prefix}${7位随机数}${suffix});
 *      2. f.createNewFile();
 */
static File createTempFile(String prefix, String suffix[, File directory]) throws IOException;

// 示例：当前路径中必须有temp/目录，否则抛出异常
File tmpFile = File.createTempFile("test", ".tmp", new File("temp"));
// test1978568321310049023.tmp
```

<br>

**5.&nbsp; 删除钩：**

- 向JVM注册一个删除钩，在程序退出时自动删除该文件.
   - **任何File对象都可以使用.**
   - 但**多用于临时文件**，一般临时文件只在程序运行期间临时使用.

```Java
void deleteOnExit();

// 示例
File f = new File("a.txt");
f.createNewFile();
f.deleteOnExit();

File tmpFile = File.createTempFile(...);
tmpFile.deleteOnExit();
```

<br><br>

### 五、目录操作：只适用于目录  [·](#目录)

<br>

**1.&nbsp; 创建单级目录：**

```Java
/**  只能创建单级目录.
 *  
 *   1. 已存在返回false.
 *   2. 如果传入的this.path是多级目录.
 *     - 中间只要有一级（最后一级之前）不存在都不会创建，直接返回false
 *     - 例如：dir1\dir2\dir3\dir4
 *       - 除非dir1\dir2\dir3已存在才会尝试创建dir4
 *       - dir1、dir2、dir3中只要有任意一级不存在都将宣告失败.
 */
boolean mkdir();
```

<br>

**2.&nbsp; 创建多级目录：**

```Java
// 除非已存在，否则一定能成功创建.
boolean mkdirs();
```

<br>

**3.&nbsp; 列出this.path下的所有文件和目录的simple name（getName()）：**

```Java
String[] list([FilenameFilter filter]);
```

<br>

**4.&nbsp; 列出this.path下的所有文件：**

```Java
// 返回的每个File对象的file.path = ${this.path}\${getName()}
File[] listFiles([FilenameFilter filter]);

// 示例：
new File(".").listFiles();  // getPath: [.\xxx, .\xxx, .\xxx]
```

<br>

**5.&nbsp; 文件过滤器FilenameFilter：**

```Java
/**  用于list()和listFiles时筛选文件
 *  
 *   - dir所在的目录：dir.path = this.path
 *   - name是当前迭代对象的getName().
 *
 *   - 满足accept = true的将保留，false被筛掉.
 */
public interface FilenameFilter {
    boolean accept(File dir, String name);
}

// 示例
File dir = new File(".");
dir.list(new FilenameFilter() {
	@Override
	public boolean accept(File dir, String name) {
		out.println(dir.getPath() + " - " + name);
		return true;
	}
});
/* 输出
. - .classpath
. - .project
. - .settings
. - bin
. - dir
. - a.txt
. - p.txt
. - src
*/

// 由于是函数式接口，可以使用lambda表达式
dir.list((dir, name) -> name.endsWith(".txt") && dir.getName().startsWith("nano"));
```

<br>

**6.&nbsp; 查看所有根目录：** File的静态工具方法

- Linux下返回的是设备挂载点.

```Java
static File[] listRoots();
// 返回的getPath: [C:\, D:\, ...]
```
