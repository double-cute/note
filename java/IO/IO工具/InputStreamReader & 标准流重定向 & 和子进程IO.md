# InputStreamReader & 标准流重定向 & 和子进程IO

<br><br>

## 目录

1. [byte2char流转换器——InputStreamReader]()
2. [标准流重定向]()
3. [和子进程IO]()

<br><br>

### 一、byte2char流转换器——InputStreamReader：[·](#目录)
> Java只提供了将字节流转换成字符流的IO工具类InputStreamReader，但是并没有提供字符流转换成字节流的工具.
>
>> 原因很简单，这是因为字符比字节更具体和高级，**低级到高级很正常**，高级降为低级的应用场景几乎不存在.
>>
>>> - 设想：如果一个流已经是字符流了，那就没有必要再转换成字节流了，毕竟字符流操作比字节流方便.
>>>    - 将字节流转换成字符流的目的就是为了 **操作更方便和直观**，所以Java不提供字符流到字节流的转换.

<br>

**1.&nbsp; InputStreamReader的转换原理：** 用构造器包装一个字节流直接变成字符流

- InputStreamReader本来就 **直接继承并实现了Reader**，因此 **其编译时类型就是Reader族**！

```Java
// 将一个字节流包装成InputStreamReader（中间过渡字符流Reader）
   // 可以选择性地用3种方式指定字节到字符的编码方式
InputStreamReader(InputStream in[, Charset charset | String charsetName | CharsetDecoder dec]);
```

<br>

**2.&nbsp; 其余的话，在方法上没有对Reader做任何拓展，完全可以当做Reader来使用：**

- 但更多的情况是：
   1. 在利用InputStreamReader构造器转换出一个Reader后.
   2. 就把结果当做一个普通Reader节点流.
   3. 接着去被另外的高级处理流继续包装.
      - 最常见的例子是：System.in(InputStream) -InputStreamReader-> BufferedReader

```Java
public static void main(String[] args) throws IOException {  
    try (  
        // 转换后的结果作为中间过渡
        InputStreamReader interReader = new InputStreamReader(System.in);
        // 接着继续被其它高级处理流包装
        BufferedReader br = new BufferedReader(interReader);  
    )
    {  
        String line = null;  
        while ((line = br.readLine()) != null) {  
            if (line.equals("exit")) {  
                System.exit(1);  
            }  
            System.out.println("Input: " + line);  
        }  
    }  
}  
```

<br><br>

### 二、标准流重定向：[·](#目录)

<br>

**1.&nbsp; 什么是标准流：**

- 即标准输入输出流（即标准IO流）的简称.
   1. 总共包含3者：传统延承C语言的称呼就是stdin、stdout、stderr.
      - Java中分别对应：System.in、System.out、System.err.
   2. 分别代表标准输入流、标准输出流、标准错误流.
   3. 三者的默认定义：
      1. static final **InputStream** System.in：**键盘输入**，流向程序.
      2. static final **PrintStream** System.out：程序正常输出，流向**屏幕终端**.
      3. static final **PrintStream** System.err：程序的异常以及报错信息输出，流向**屏幕终端**.

<br>

**2.&nbsp; 重定向的内容：** 只能重定向指向，不能改变标准流的作用！

| 标准流 | 指向 | 作用 |
| --- | --- | --- |
| System.in | 指向（键盘，即输入源） | 从输入源读取数据到程序 |
| System.out | 指向（屏幕终端，即输出目的地） | 将程序的正常输出送到输出目的地 |
| System.err | 指向（屏幕终端，即输出目的地） | 将程序的错误信息输出送到输出目的地 |

<br>

**3.&nbsp; 利用System的setXxx静态工具方法进行重定向指向：**

```Java
// 三者调用本地native方法进行重定向，毕竟标准IO是OS的财产！
static void setIn(InputStream in);
static void setOut(PrintStream out);
static void setErr(PrintStream err);

// 作用等价于：虽然三者都是final的，只是一个比喻罢了
System.setIn(newIn); --> System.in = newIn;
System.setOut(newOut); --> System.out = newOut;
System.setErr(newErr);  --> System.err = newErr;
```

<br>

**4.&nbsp; 标准流备份：**

- 一个最现实的问题就是重定向之后又想还原回来，这就需要备份了，其实很简单：

```Java
PrintStream oldOut = System.out; // 备份
PrintStream newOut = new PrintStream("a.txt");
System.setOut(newOut); // 重定向
System.out.println("lalala");  // 向新目标输出
System.setOut(oldOut); // 备份还原  
```

<br><br>

### 三、和子进程IO：[·](#目录)
> 之前讲过可以通过Runtime.getRuntime().exec(String cmd)在当前进程中开一个子进程执行该子进程的句柄（Process对象）.
>
>> - 现在我们要做的就是：
>>    1. 站在当前父进程的角度.
>>    2. 和自己开出来的那个子进程进行IO通信.

<br>

**1.&nbsp; 先回顾exec方法：**

```Java
// 1. Runtime的静态工具方法，获取当前运行时环境的句柄.
static Runtime getRuntime();

// 2. Runtime的对象方法，利用this运行时环境执行一个命令（开一个子进程）
  // 并返回子进程的句柄Process对象（Process是进程，Thread的线程）
Process exec(String command) throws IOException;
```

<br>

**2.&nbsp; 获取子进程的 `3个标准流` 和子进程进行IO通信：**

- 都是**Process的对象方法**.

```Java
// 1. 获取向子进程写入数据的输出流（父进程角度）
  // 站在子进程角度就是子进程的标准输入流
OutputStream getOutputStream();

// 2. 获取从子进程读取数据的输入流（父进程角度）
  // 站在子进程角度就是子进程的标准输出流
InputStream getInputStream();

// 3. 获取从子进程读取错误信息的输入流（父进程角度）
  // 站在子进程角度就是子进程的标准错误流
InputStream getErrorStream();
```

<br>

**3.&nbsp; 示例：**

- 打印javac的错误信息：

```Java
public static void main(String[] args) throws IOException {  
    Process p = Runtime.getRuntime().exec("javac"); // 直接调用javac而不带参数是错误的，因此会触发子进程的错误流  
    try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {  
        String buf = null; // 打印子进程的错误提示信息（注意！是字符形式的）  
        while ((buf = br.readLine()) != null) {  
            System.out.println(buf);  
        }  
    }  
}  
```

<br>

- 父进程给子进程传数据，子进程把这些数据写入a.txt中：
   - 实际只需要运行java FatherProc即可.

```Java
// FatherProc.java
public class FatherProc { // 父进程开启子进程，然后用自己的输出来取代子进程的标准输入  
   public static void main(String[] args) throws IOException {  
       Process p = Runtime.getRuntime().exec("java ChildProc");  
       try (PrintStream ps = new PrintStream(p.getOutputStream())) {  
           ps.println("123456"); // 子进程所谓的“键盘输入”其实是这两句内容  
           ps.println("lalalala");  
       }  
   }  
}
```

```Java
public class ChildProc { // 子进程从键盘读入然后打印到文件  
   public static void main(String[] args) throws Exception {  
       try (  
           Scanner sc = new Scanner(System.in);  
           PrintStream ps = new PrintStream("a.txt")  
       )
       {  
           while (sc.hasNext()) {  
               ps.println("keyboard input: " + sc.next());  
           }  
       }  
   }  
}
```
