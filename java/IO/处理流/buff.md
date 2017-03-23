


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




   2. BufferedReader简介：
       1) 是一种高级处理流，具有缓冲功能，可以整行读取流中的数据，读取效率更高（因为可以缓冲）；
       2) 构造器：BufferedReader(Reader in); // 用一个低级节点流来构造
       3) 整行读取：String readLine(); // 以行结束符\n（Unix）、\r\n（Win）为一行读取数据，返回的字符串将去掉行结束符
   ！！如果读不到行结束符程序就会阻塞，这可以从上面程序可以看出，输完一行会停在那里等待下一行的输入（回车表示一行的输入）；


   2. Scanner：
    1) 是一种基于正则表达式的文本扫描器，可以扫描文本中的字符串（模式）、数字等；
    2) 它是一种高级处理流，可以包装文件、键盘输入流、字符串等，对这些节点中的文本流数据进行解析；
    3) 构造器：
         i. 通用版本：Scanner(InputStream | File | Path source[, Charset cs]);
         ii. 分别重载了InputStream（键盘输入流就用这种）、File、Path，并且可以选择性指定字符集（毕竟像文件这种使用的字符集可以多种多样，容易产生乱码问题）；
         iii. 还有一个String版本的：Scanner(String source);  // String就没有字符集这个选项，因为String肯定是Java程序内的数据，Java程序范围之内肯定都是Java自己的Unicode编码，因此没有Charset这个参数重载版本；
    4) 解析普通字符串：
         i. 解析的时候默认使用空白符（空格、制表、换行）作为字符串的分隔符（如果输入的是"abc def"，那么也会被分成两个字符串"abc和def"！！一定要注意了；
         ii. 解析方法：
             a. boolean hasNext();  // 是否有下一个字符串，基于分隔符的
             b. String next();  // 获取下一个字符串并移动位置指针
！！所有的解析方法肯定都是先用hasNext判断是否能继续读取，然后再调用next读取下一个；
    5) 自定义分隔符：
         i. Scanner默认使用空白符分隔字符串，但是可以自定义这个分隔符；
         ii. Scanner useDelimiter(String pattern);  // 用一个正则表达式来作为分隔符
         iii. 简单的例子比如：scan.useDelimiter("\n"); 就是只以换行符作为分隔符，那么hasNext和next读取的单位就是行了而不是普通字符串了；
    6) 当然Scanner为了方便也提供了正行读取的解析法：
         i. boolean hasNextLine();  // 是否有下一行
         ii. String nextLine(); // 直接读取下一行
！！Scanner的所有读取都会抛弃分隔符，也就是说上面的方法读取之后都不包含空白符、换行符；
    7) 解析数字：
         i. boolean hasNextType(); // 是否有下一个type类型的数据
         ii. type nextType(); // 读取下一个type类型的字符串并解析成type类型返回
！！这里type支持Java的所有基础类型（int、double、long等）；
    8) 使用：肯定是用一个while循环判断是否有下一个可读取的内容（hasNext），然后在循环体中循环调用next读取内容
[java] view plain copy 在CODE上查看代码片派生到我的代码片
while (scn.hasNext()) {  
    type val = next();  
    处理代码...  
}  
