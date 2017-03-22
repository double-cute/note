所有低级节点流无外乎就是read()、write()之外额外添加一两个方法而已.
BufferedReaderInputStream、bufferedwriteroutputstream，属于高级处理流，可以把所有节点流包装成 **高效缓存处理流**.



3. 以字符串作为流节点进行I/O：
   1) 前面介绍了，既然可以用数组（字节、字符）作为流节点，那么自然会想到用字符串作为流节点专门用来处理字符流；
   2) 其Java类是StringReader和StringBuffer，既然是专门处理字符的，因此就没有StringInputStream、StringOutputStream的版本了；
   3) 构造器：
        i. StringReader的构造器很正常，直接以一个String作为节点：StringReader(String s);
        ii. StringWriter的构造器不用传字符串，它内部自己维护这一个StringBuffer作为字符串节点：
[java] view plain copy 在CODE上查看代码片派生到我的代码片
public class StringWriter extends Writer {  

   private StringBuffer buf;  

   /**
    * Create a new string writer using the default initial string-buffer
    * size.
    */  
   public StringWriter() {  
       buf = new StringBuffer();  
       lock = buf;  
   }  
...  
...  
！！由于String是不可变的，而输出流又是需要改变缓冲区内容的，因此输出流需要使用StringBuffer充当流节点（可变）；
             a. 默认的无参构造器可以看到开辟的缓存空间是0，即从0开始增长；
             b. 有参构造器：
[java] view plain copy 在CODE上查看代码片派生到我的代码片
public StringWriter(int initialSize) {  
   if (initialSize < 0) {  
       throw new IllegalArgumentException("Negative buffer size");  
   }  
   buf = new StringBuffer(initialSize);  
   lock = buf;  
}  
！！给定一个初始的缓存大小，后期超过了会自动增长；
！！那你可能会问，既然不能自己指定流节点StringBuffer，那有什么意义呢？？我就是想查看输出后的结果，但是现在那个StringBuffer是StringWriter的内部成员，难道我还要隔着一层StringWriter来访问吗？
       a. StringWriter的toString其实就是返回其StringBbuffer中的字符串内容，因此是可以轻松访问到的；
       b. 那上面那个问题还是没有解决，那为什么不可以直接传入一个StringBuffer作为流节点呢？为什么一定要使用内部的StringBuffer呢？
       c. 答案其实很简单，Java就是把StringWriter设计成最最高级的StringBuffer，其实就是StringBuffer的强有力升级，StringBuffer对内容进行修改只能通过像什么setCharAt之类的传统方法，而这些方法在几乎所有编程语言中都有提供（类似的），但是StringWriter可以经过处理流包装（PrintWriter）使用更加高级、强大的方法来修改其中的字符，那不就变成了一个可以使用print、println之类的方法来修改字符的高级StringBuffer了吗？？
   4) 示例：
[java] view plain copy 在CODE上查看代码片派生到我的代码片
public class Test {  

   public static void main(String[] args) throws IOException {  
       String src = "hello!\nOK\nGoodMorning!\n";  

       char[] cbuf = new char[128];  
       int hasRead = 0;  
       try (StringReader sr = new StringReader(src)) {  
           while ((hasRead = sr.read(cbuf)) > 0) {  
               System.out.println(new String(cbuf, 0, hasRead));  
           }  
       }  

       try (StringWriter sw = new StringWriter(20)) {  
           sw.write("lalala\n");  
           sw.write("goodgoodgood\n");  
           System.out.println(sw); // 默认调用了toString方法  
       }  
   }  
}  


3. 回推输入流：
    1) 即用输入流读取数据的时候可以将数据回推到输入流中的输入流；
！！输出流没有回推，以为输出是往外输出，没有回推反复输出的需求；
    2) 是一种高级处理流，可以包装输入节点流（即对各种不同的节点流的缓冲进行回推）；
    3) Java的回推输入流类是PushbackInputStream和PushbackReader；
    4) 回推模型：
         i. 回推输入流具有两个缓冲区，一个是正常的输入流缓冲区，另一个是回推缓冲区；
         ii. 程序正常用read系列读取数据是必须先读取回推缓冲区的数据，只有当回推缓冲区读完后才能继续读取正常输入缓冲区中的数据；
         iii. 从这两个缓冲区中读取数据时都是读取一个少一个，即直接从缓冲区中“取走”数据，取走的数据就不再在缓冲区中了；
         iv. 而往回推缓冲区中回推数据时可以回推任何数据，因此只有将从输入缓冲区中读取的数据回推到回推缓冲区时才能实现对输入数据的反复读取；
！小结：回推缓冲区和输入缓冲区是隔开的，只不过回推缓冲区的数据会先读；
    5) 回推三方法：byte和char分别是字节流版本和字符流版本
         i. void unread(int b/int c);  // 回推一个单位b/c
         ii. void unread(byte[] buf/char[] cbuf);  // 回推一整个数组buf/cbuf
         iii. void unread(byte[] buf/char[] cbuf, int off, int len);  // 将数组从off开始的len个单位回推
！！可以看到和read一模一样，只不过干的是和read完全相反；
    6) 在创建回推输入流时需要指定回推缓冲区的大小（单位）：PushbackReader(Reader in, int size);  // 如果回推超过size个单位会直接抛出异常！
    6) 示例：找出输入流中的字符串"Peter"，并将该字符串之前的全部字符打印出来
[java] view plain copy 在CODE上查看代码片派生到我的代码片
public class Test {  

    public static void main(String[] args) throws IOException {  
        try (PushbackReader pr = new PushbackReader(new InputStreamReader(System.in), 64)) {  
            char[] buf = new char[32]; // 临时读取数据的缓冲  
            String preCon = ""; // 上一次读取的内容  
            int hasRead = 0;  
            int find = 0; // 找到目标字符串的位置  
            while ((hasRead = pr.read(buf)) > 0) {  
                String curCon = new String(buf, 0, hasRead); // 当前读取的内容  
                String con = preCon + curCon; // 由于目标字符串可能加载上一次和当前内容的中间，所以需要合并  
                find = con.indexOf("Peter"); // 检测是否找到目标字符串  
                if (find > 0) { // 如果找到了  
                    pr.unread(con.toCharArray()); // 先将合并内容回推  
                    char[] find_buf = new char[find]; // 目标字符串可能完全在curCon之中，因此find可能大于临时缓冲区大小32  
                    pr.read(find_buf, 0, find); // 然后再从回推缓冲区读取到目标字符串之前的位置  
                    System.out.println(new String(find_buf, 0, find)); // 打印退出  
                    System.exit(0);  
                }  
                else { // 不存在，因此先打印上次内容  
                    System.out.print(preCon); // 但是curCon中也许存在目标字符串的部分内容，因此curCon不能打印  
                    preCon = curCon; // 递推  
                }  
            }  
        }  
    }  
}  
！！可以看到回推输入流最主要是用来对输入流进行分析！！而不是闹着好玩儿瞎回推！！
