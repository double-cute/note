# InputStreamReader & 标准流重定向 & 和其它进程IO

<br><br>

## 目录

1. []()
2. []()
3. []()

<br><br>

### 一、byte2char流转换器——InputStreamReader：
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

### 二、标准流重定向：

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



   1) 标准I/O流就是指System.in、System.out、System.err这三个，分表表示标准输入流、标准输出来、标准错误流；
   2) 三者默认的流节点分别是键盘、屏幕、屏幕，这也是我们频繁使用的三个I/O流；

2. 标准流重定向：
   1) 即人为改变三个标准流的流节点，比如吧System.out的流节点人为改成某个文件，那么在使用System.out输出的时候就不是输出到屏幕了而是输出到那个文件了；
   2) 简单地说就是改变标准流原来默认的流向而已；
   3) System提供了3个静态方法对标准流重定向：
        i. static void setIn(InputStream in); // 重定向标准输入流
        ii. static void setOut(PrintStream out); // 重定向标准输出流
        iii. static void setErr(PrintStream err);  // 重定向标准错误流
！！可以看到两个输出流必须要用处理流来重定向（当然必须要确定底层的流节点），而输入流一般用一个节点流来重定向就行了；
   4) 示例：分别重定向输出流和输入流
[java] view plain copy 在CODE上查看代码片派生到我的代码片
public class Test {  

   public static void main(String[] args) throws IOException {  
       try (PrintStream ps = new PrintStream(new FileOutputStream("out.txt"))) {  
           PrintStream old = new PrintStream(System.out); // 备份  
           System.setOut(ps);  
           System.out.println("lalala");  
           System.out.println(new Test());  
           System.setOut(old); // 还原  
       }  

       try (FileInputStream fis = new FileInputStream("src\\com\\lirx\\Test.java")) {  
           System.setIn(fis);  
           Scanner sc = new Scanner(System.in);  
           sc.useDelimiter("\n");  
           while (sc.hasNext()) {  
               System.out.println(sc.next());  
           }  
       }  
   }  
}  


3. JVM读写其它进程的数据：
   1) 我们知道可以通过Runtime.getRuntime().exec(String cmd)在当前进程中执行平台上的另一个子进程，该方法返回代表该子进程句柄的Process对象；
   2) 现在我们要做的就是父子两个进程之间进行通信（数据交流）；
   3) Process对象提供了三个方法来达到上述目的：三个方法均以当前进程（即父进程）为视角出发的
        i. InputStream Process.getInputStream();  // 获得子进程的标准输出流（到父进程中就是输入流了，对方输出我接受，那么到这里就变成我的输入流了）
        ii. InputStream Process.getErrorStream();  // 获得子进程的标准错误流（到父进程中就是输入流了）；
        iii. OutputStream Process.getOutputStream(); // 获得子进程的标准输入流（在父进程中就是输出流，父进程朝子进程输出，到了子进程就是子进程的输入流了）
！！规律：
       a. getInputStream和getOutputStream都是字面意义，同时也是站在父进程角度的，即getInputStream得到的就是输入流，getOutputStream得到的就是输出流；
       b. 因此无需死记硬背，输入流就是输入，那必定是对方的输出流，而输出流就是输出，必定到对面就是输入流了，只不过默认绑定的都是对方的标准流；
       c. 只有getErrorStream比较特殊，它得到的是对方的错误流，对方的错误流是输出流，那么到了父进程就是输入流了（从返回值可以看出）
   4) 示例：getErrorStream的应用（getInputStream和getErrorStream其实是一样的，都是获取子进程的输出）
[java] view plain copy 在CODE上查看代码片派生到我的代码片
public class Test {  

   public static void main(String[] args) throws IOException {  
       Process p = Runtime.getRuntime().exec("javac"); // 直接调用javac而不带参数是错误的，因此会触发子进程的错误流  
       try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {  
           String buf = null; // 打印子进程的错误提示信息（注意！是字符形式的）  
           while ((buf = br.readLine()) != null) {  
               System.out.println(buf);  
           }  
       }  
   }  
}  
   5) 示例：getOutputStream的应用
[java] view plain copy 在CODE上查看代码片派生到我的代码片
public class Test { // 父进程开启子进程，然后用自己的输出来取代子进程的标准输入  

   public static void main(String[] args) throws IOException {  
       Process p = Runtime.getRuntime().exec("java ChildTest");  
       try (PrintStream ps = new PrintStream(p.getOutputStream())) {  
           ps.println("123456"); // 子进程所谓的“键盘输入”其实是这两句内容  
           ps.println("lalalala");  
       }  
   }  
}  

class ChildTest { // 子进程从键盘读入然后打印到文件  
   public static void main(String[] args) throws Exception {  
       try (  
           Scanner sc = new Scanner(System.in);  
           PrintStream ps = new PrintStream(new FileOutputStream("test.out"))  
       ) {  
           sc.useDelimiter("\n");  
           while (sc.hasNext()) {  
               ps.println("keyboard input: " + sc.next());  
           }  
       }  
   }  
}  
