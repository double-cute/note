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
