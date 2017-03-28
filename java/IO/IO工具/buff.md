
1. 将字节流转换成字符流：
    1) Java只提供了将字节流转换成字符流的方法，但是并没有提供字符流转换成字节流的方法。这是因为字符流要比字节流操作更加方便、直观，毕竟字符是人直接能看得懂的；
！！因此，如果一个流已经是字符流了，那就没有必要再转换成字节流了，毕竟字符流操作比字节流方便啊！而将字节流转换成字符流的目的就是为了操作更方便，所以Java不提供字符流到字节流的转换；
    2) 完成这个工作只需要InputStreamReader类就行了，其实该类名的全称应该是InputStream to Reader，即把InputStream转化成Reader的意思；
    3) InputStreamReader的API源码：
[java] view plain copy 在CODE上查看代码片派生到我的代码片
public class InputStreamReader extends Reader {  

    private final StreamDecoder sd;  

    /**
     * Creates an InputStreamReader that uses the default charset.
     *
     * @param  in   An InputStream
     */  
    public InputStreamReader(InputStream in) {  
！！可以看到，它继承的就是Reader，而构造器所包装的参数是一个InputStream，因此直接用构造器就能返回一个Reader对象，因此转换直接用构造器来实现；
    4) 示例：Reader r = InputStreamReader(System.in);   // System.in原本是InputStream的字节流，现在被转换成字符流了
    5) 同样OutputStreamWriter可以将输出字节流转换成输出字符流，其构造器：OutputStreamWriter(OutputStream out);  // 同样使用构造器完成转换
    6) 示例：从标准输入读取并处理
[java] view plain copy 在CODE上查看代码片派生到我的代码片
public class Test {  

    public static void main(String[] args) throws IOException {  
        try (  
            InputStreamReader reader = new InputStreamReader(System.in);  
            BufferedReader br = new BufferedReader(reader);  
        ) {  
            String line = null;  
            while ((line = br.readLine()) != null) {  
                if (line.equals("exit")) {  
                    System.exit(1);  
                }  
                System.out.println("Input: " + line);  
            }  
        }  
    }  
}  
！这里我们使用了BufferedReader对字符输入流reader进行了一层包装，以为BufferedReader（处理流）具有缓冲功能，可以实现整行读取，读取效率也很高；



[疯狂Java]I/O：标准流重定向、JVM读写其它进程数据
标签： 疯狂Java标准流重定向JVM读写其它进程的数据
2016-04-24 14:24 467人阅读 评论(0) 收藏 编辑 删除
分类： 疯狂Java笔记（124）  
1. 标准输入输出流：
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
