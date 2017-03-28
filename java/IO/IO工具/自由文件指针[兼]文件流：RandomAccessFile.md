# 自由文件指针[兼]文件流：RandomAccessFile
> - 既不属于节点流也不属于处理流：**两大核心特点**
>   1. **位置指针可以自由访问和操作**：节点流和处理流都不允许！
>   2. 只能包装文件节点（**只能进行文件IO**）：处理流允许包装任何节点流的！
>
>> 因此，**只能算作IO工具类**，没有继承和实现任何节点流抽象基类，而是一种特殊重构的IO类.
>
>> - 底层 **把一切文件都当成二进制文件看待**，不管是文本文件还是其它什么格式，就当成纯粹的二进制文件进行读写.
>>    - 读写格式也是 **纯二进制格式的**.
>>
>>> 最大的优点就是 **在可写模式下，文件的大小可以随意伸缩**.

<br><br>

## 目录

1. [构造器：只能包装文件节点]()
2. [自由访问和操纵位置指针]()
3. [文件大小]()
4. [基础read & write：4大节点流抽象基类的版本]()
5. [单个基本类型的read & write：支持所有Java基本类型]()
6. [read无符号整型 & 行]()
7. [write字符串：字节方式 & 字符方式]()
8. [应用技巧：追加和插入]()
9. [示例]()
10. [多线程断点下载工具的设计建议]()

<br><br>

### 一、构造器：只能包装文件节点  [·](#目录)

<br>

**1.&nbsp; 构造器原型：**

```Java
RandomAccessFile(File file | String fileName, String mode);
```

<br>

**2.&nbsp; 读写模式mode参数：**

- 注意：不像C语言那样有append模式！
   - 因为**指针已经可以自由移动了，可以轻易移动末尾进行append**.

| mode | 说明 |
| --- | --- |
| "r" | 只读，文件必须存在，否则抛出异常 |
| "rw" | 读写（**没有只写的方式**）<br>如果文件不存在则会自动创建一个空的 |
| "rws" | "rw"的基础上<br>对文件内容以及元数据的所有更新都会**立即同步**到磁盘上<br>即自动flush |
| "rwd" | "rws"的基础上，去掉对元数据的立即同步 |

<br><br>

### 二、自由访问和操纵位置指针：[·](#目录)
> 由于RandomAccessFile底层是面向二进制的，因此 **位置指针的移动步长（单位）是字节（8bit）**.
>
>> - **位置指针的类型是long型**.
>>    - 考虑到文件可能会非常大，字节数超出int的范围.

<br>

- 刚打开文件时，位置指针为0（文件的**第1个字节处**，**索引从0计**）.

<br>

**1.&nbsp; 获取当前位置：** 底层是C语言本地调用

```Java
native long getFilePointer();
```

<br>

**2.&nbsp; 绝对定位：**

- 没有提供相对定位，毕竟细节太多，方法越少越好，相对可以用绝对来实现.

```Java
/**  将指针定位到绝对位置pos处.
 *
 *   - 如果pos ≥ EOF的位置（即超出length()当前文件大小）.
 *      1. 仍然生效：getFilePointer()的结果就是pos处.
 *      2. 但文件大小length()不变.
 *         - length()想变大必须要在 ≥ EOF处写入数据才行！
 *         - 新length = pos + 写入数据的大小 (前提是 pos ≥ EOF)
 */
void seek(long pos);

// 示例：
RandomAccessFile raf = new RandomAccessFile("empty", "rw");
raf.seek(128); // raf.length() = 0 && raf.getFilePointer() = 128
raf.writeInt(1000);  // raf.length() = 128 + 4 = 132 && raf.getFilePointer() = 132
```

<br>

**3.&nbsp; 跳过：** skipBytes

```Java
/**  向后略过n个字节
 *  
 *   - 返回实际跳过的字节数.
 *      - 可能小于n，因为该方法不允许跳得超过EOF！
 *      - 超出就强行停在EOF处，此时跳跃长度小于n.
 */
int skipBytes(int n) throws IOException;
```

<br><br>

### 三、文件大小：[·](#目录)
> 文件大小可以 **在可写的模式下随意设置**！

<br>

```Java
/**  1. 文件的当前大小
 *  
 *    - 只有在：
 *      1. setLength()强行设值后：等于设的值.
 *      2. 在EOF处或之后写数据：等于新pos的值 + 1
 */
native long length() throws IOException;

/**  2. 强行设置文件大小
 *  
 *    - 如果newLength ≥ 当前大小，那没问题，大小会更新的.
 *    - 如果newLength < 当前大小，除了大小会正常更新以外.
 *      - pos会更新到newLength处，即：pos = newLength
 *      - 所以一定要记得setLength后重新获取一下更新后的pos（getFilePointer一下）
 *        - 老的pos也许是错误的！
 */
native void setLength(long newLength) throws IOException;
```

<br><br>

### 四、基础read & write：4大节点流抽象基类的版本  [·](#目录)

<br>

```Java
int read([byte[] b[, int off, int len]]);

void write(int b | byte[] b[, int off, int len]) throws IOException
```

- 其中read的变相：等待式完整读取

```Java
// 不返回实际读取数量，必须、强制要求读满b.length或者len个，不足就阻塞线程继续等待输入
   // 一般用于多线程交互同一个文件时使用.
final void readFully(byte[] b[, int off, int len]) throws IOException;
```

<br><br>

### 五、单个基本类型的read & write：支持所有Java基本类型  [·](#目录)

<br>

- type支持**所有**Java基础类型：
   - boolean、char、short、int、long、float、double

```Java
final type readType() throws IOException;

final void writeType(type v) throws IOException;
```

<br><br>

### 六、read无符号整型 & 行：[·](#目录)

<br>

**1.&nbsp; read无符号整型：** 目前只支持byte和short类型的

- 即，接下来的1个或2个字节按照无符号格式解析.

```Java
final int readUnsignedByte() throws IOException;
final int readUnsignedShort() throws IOException;
```

<br>

**2.&nbsp; 读取一行：**

```Java
// 到了EOF还readLine就返回null
final String readLine() throws IOException;
```

<br><br>

### 七、write字符串：字节方式 & 字符方式  [·](#目录)

<br>

```Java
// 1. 以字节形式写，就和正常的print等一样（去掉char的高字节，只保留低字节）
final void writeBytes(String s) throws IOException;

// 2. 以字符形式写（内部调用writeChar，2字节为单位写入）
final void writeChars(String s) throws IOException;
```

- 通常建议使用writeBytes，**一般OS中的文本文件中的字符都是单字节的**！

<br><br>

### 八、应用技巧：追加和插入  [·](#目录)

<br>

**1.&nbsp; 追加内容必须先把位置指针定位到最后：** 没有"a"的读写模式

```Java
raf.seek(raf.length());
```

<br>

**2.&nbsp; 插入：**

1. RandomAccessFile依然不支持直接在指定位置上插入内容，因此**必须自己用算法实现**.
   - 如果直接在中间某个位置写入内容，则会直接覆盖掉该位置之后的内容！！
2. 因此必须要先**将插入点之后的全部内容先备份**.
   - 如果备份内容很小则可以在内存中buffer.
   - 如果备份内容巨大，则要使用createTempFile创建临时文件进行备份（注意退出时自动删除（删除钩））.

<br>

<br><br>

### 九、示例：[·](#目录)

<br>

**1.&nbsp; 只读：**

```Java
public class Test {  

   public static void print(String s) {  
       System.out.println(s);  
   }  

   public static void main(String[] args) throws IOException {  
       try (RandomAccessFile raf = new RandomAccessFile("Test.java", "r")) {  // 读自己
           print("Initial position: " + raf.getFilePointer());  // 初始位置  

           raf.seek(300); // 从第300个字节位置处开始读  
           byte[] b = new byte[1024];  
           int hasRead = 0;  
           while ((hasRead = raf.read(b)) > 0) { // 正常读，和InputStream没有任何区别  
               print(new String(b, 0, hasRead));  
           }  
       }  
   }  
}  
```

<br>

**2.&nbsp; 追加：**

```Java
public class Test {  

   public static void print(String s) {  
       System.out.println(s);  
   }  

   public static void main(String[] args) throws IOException {  
       try (RandomAccessFile raf = new RandomAccessFile("a.txt", "rw")) {  
           raf.seek(raf.length());  
           raf.writeBytes("lalala\n");  // 追加一个lala
       }  
   }  
}  
```

<br>

**3.&nbsp; 插入：**

```Java
public class Test {  

   public static void print(String s) {  
       System.out.println(s);  
   }  

   public static void main(String[] args) throws IOException {  
       // 用临时文件备份
       File tmp = File.createTempFile("tmp", null);  
       tmp.deleteOnExit(); // 创建退出自动删除的临时备份文件  
       try (          
           // 本体
           RandomAccessFile raf = new RandomAccessFile("a.txt", "rw");

           // 备份体
             // 虽然两个流关联的是同一个文件节点，但两个流有自己不同的指针
             // 这是必然的，指针指的是缓冲区，两个流必定有各自不同的缓冲区
             // 因此两个指针相互独立，互不影响
           FileOutputStream tmpOut = new FileOutputStream(tmp);  
           FileInputStream tmpIn = new FileInputStream(tmp)  
       ) {  
           raf.seek(30); // 从30开始插入  
           byte[] b = new byte[64];  
           int hasRead = 0;  
           while ((hasRead = raf.read(b)) > 0) { // 先将30后的内容备份出去  
               tmpOut.write(b, 0, hasRead);  
           }  
           raf.seek(30); // 然后再回到30写入要插入的内容  
           raf.writeBytes("$$ I'm here! $$");  
           while ((hasRead = tmpIn.read(b)) > 0) { // 在将备份出去的内容追加到插入的内容之后  
               raf.write(b, 0, hasRead);  
           }  
       }  
   }  
}  
```

<br><br>

### 十、多线程断点下载工具的设计建议：[·](#目录)

<br>

1. 当然是用RandomAccessFile实现.
2. 准备两个文件：
   1. 和原文件大小相同的空文件：用来接收下载的数据.
   2. 记录各线程当前位置指针的日志文件.
3. 下载时启动多个线程，按照文件大小均匀分配每个线程中指针的起始位置，然后开始下载.
4. 每当任务中断（断网、断电、自己关闭等）都会立即将各个线程RandomAccessFile的位置指针记录到日志文件中.
   - 待下次开启后可以根据日志信息继续从上次的位置下载.
