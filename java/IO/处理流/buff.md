


[疯狂Java]I/O：处理流、PrintStream简介、I/O体系、字符串作为流节点
标签： 疯狂JavaIO处理流StringReaderStringWriter
2016-04-22 11:20 354人阅读 评论(0) 收藏 编辑 删除
分类： 疯狂Java笔记（124）  
1. 处理流PrintStream的简单使用：
   1) 处理流最大的特点就是对节点流的包装，因此只要构造器的参数是节点流的一定就是处理流（PrintStream、BufferedStream等），而构造器参数是节点本身的就是节点流（FileInputStream等）；
   2) 处理流的两大好处：操作简单（对象方法使用很简单，基本源自各编程语言的模型，通用性强）、执行效率更高；
   3) PrintStream简介：
        i. System.out就是PrintStream，只不过底层包装的节点流是标准输出流（显示屏）；
        ii. 虽然它是字节流，但是它可以方便输出字符，里面提供了print、println等方法；
！！print系列最强大的地方就是可以输出Java对象（前提是Java对象要实现串行化，否则就默认只能输出类的名字，以及对象哈希码）；
        iii. 其输出字符的效率要比用PrintWriter输出字符的效率还要高（因为它处理直接就是二进制字节），而字符在处理的时候还要经过一层编解码环节；
        iv. 因此输出字符都应该优先考虑使用字节处理流！！
   4) 示例：使用PrintStream写文件
[java] view plain copy 在CODE上查看代码片派生到我的代码片
public class Test {  

   public static void main(String[] args) throws IOException {  
       // TODO Auto-generated method stub  
       try (  
           FileOutputStream fis = new FileOutputStream("test");  
           PrintStream ps = new PrintStream(fis)  
       ) {  
           ps.println("lalala"); // 输出文本  
           ps.println(new Test()); // 输出对象  
       }  
       catch (IOException e) {  
           e.printStackTrace();  
       }  
   }  

}  
   5) 处理流关闭后底层包装的节点流会自动关闭（其实API中处理流的close就是直接调用包装的节点流的close方法的）：void PrintStream.close();

2. Java的I/O体系：
   1) Java的I/O体系非常庞大，主要分为两大部分：
        i. java.io包下的标准I/O流：InputStream、Reader、OutputStream、Writer、ByteArrayInputStream、PrintStream、BufferedReader等等常见的
        ii. JDK其它包下的特殊I/O流：AudioInputStream、ZipInputStream等处理多媒体、加解密、压缩/解压的I/O流；
   2) 内容、类型繁多，光是标准I/O流就有40多种，这是因为Java为了实现更好的设计，按照I/O流的功能进行细分了；
   3) 虽然东西多，但是非常规律，就按照字节流、字符流分，而每种字节流和字符流又可以分为输入流和输出流，因此你真正需要记忆的大约就是（总数量÷4）那么多就行了，也就是10种作用，用的多了就记住了；
