[疯狂Java]I/O：文件随机读写专用类——RandomAccessFile
标签： 疯狂JavaIORandomAccessFile
2016-04-24 16:29 618人阅读 评论(0) 收藏 举报
分类： 疯狂Java笔记（124）  
1. 随机读写文件类RandomAccessFile简介：
   1) Java提供了一种功能特别强大的文件读写类：RandomAccessFile
   2) 我们都知道File只能对文件进行创建、删除、改名等操作，但要读写文件的内容必须要经过流的包装；
   3) 而RandomAccessFile虽然名字是以File作为结尾，但其并不和File是一个系列的，RandomAccessFile其实是一个文件I/O流，用文件名构造后可以直接对文件进行读写不需要用流进行包装；
   4) 但是RandomAccessFile并不是严格意义上的I/O流！
        a. 因为任何一个Java的I/O都至少可以分为节点流或者处理流，但是该类即和文件相关联（节点流的特点），又具有处理流的设计结构（很多处理流才会有的高级方法）；
        b. 并且它也并不需继承自InputStream、OutputStream、Reader、Writer中的任何一个；
        c. 因此它只是一个专门用于文件I/O的输入输出工具罢了！因此它还是属于输入输出范畴的；
        d. 其最大的一个特点就是可以自由访问文件的任意一个位置（即随意移动位置指针），这对于正常的I/O流是不可思议的，首先输出流不允许移动位置指针，而输入流最多只能向前略过几个位置而已；
        e. 因此该类只是一个读写文件，但又使用I/O流的方式进行读写的特殊工具类，就是为随机读写文件专业打造的！！

2. 创建（打开文件）对象：
   1) 除了需要指定文件名以外还需要指定读写模式：RandomAccessFile(File file | String name, String mode);
   2) 支持的模式：
        i. "r"：只读，文件必须存在，否则抛出异常；
        ii. "rw"：读写（没有只写的方式），如果文件不存在则会自动创建一个空的；
        iii. “rws”："rw"的基础上，对文件内容以及元数据的所有更新都会立即同步到磁盘上；
        iv. “rwd"："rw"的基础上，对文件内容（不包括元数据）的更新会立即同步到磁盘上；

3. 只支持二进制字节读写：
   1) RandomAccessFile只支持二进制读写，即底层都是用二进制字节流来读写数据的；
   2) 但这并不代表它等价于OutputStream之类的字节流用起来那么麻烦；
   3) 它实际上支持很多非二进制字节的处理方式，比如它有读取/写入字符、double、int、Date之类的方法，但这是软件（编程）层面的，底层还是会全部转化为二进制字节流进行读写；
   4) 也就是说操作它很方便（有很多非二进制的操作方法），只不过底层都将转化成二进制字节，用起来很高级！

4. 移动记录指针：记录指针指向的单元是字节（8bit），因为RandomAccessFile底层用二进制字节进行处理
   1) 初始化时（刚打开文件时）记录指针在0处（文件的第一个字节，索引从0计），记录指针的位置值是long型的，以为考虑到文件可能会非常大，超出int的范围；
   2) 获取当前指针位置：long getFilePointer();  // 返回指针的绝对位置
   3) 绝对定位：void seek(long pos); // 直接将指针定位到pos处，这个方法在很多编程语言中都支持
   4) 略过n个字节：int skipBytes(int n); // 单向向前越过n个字节，返回实际略过的字节数，因为可能跳跃的距离会超过文件结尾，此时会强行停留在EOF处

5. 读写文件：
   1) RandomAccessFile的读写方法和普通的I/O流几乎一模一样；
   2) 用read和write进行读写，方法原型和InputStream和OutputStream一模一样，只支持byte字节的；

6. 追加和插入：
   1) 追加内容必须先把位置指针定位到最后：raf.seek(raf.length());
   2) 插入：
        i. RandomAccessFile依然不支持直接在指定位置上插入内容（这是必须靠算法来实现的！）；
        ii. 如果直接在中间某个位置写入内容，则会直接覆盖掉该位置之后的内容！！
        iii. 因此必须要先将插入点之后的全部内容先备份（一般是使用createTempFile的方式用临时文件备份，并且该临时文件是退出自动删除的）起来，待插入完毕后再将备份内容追加到插入内容之后才行；

7. 示例：
   1) 只读：
[java] view plain copy 在CODE上查看代码片派生到我的代码片
public class Test {  

   public static void print(String s) {  
       System.out.println(s);  
   }  

   public static void main(String[] args) throws IOException {  
       try (RandomAccessFile raf = new RandomAccessFile("src\\com\\lirx\\Test.java", "r")) {  
           print("Initial position: " + raf.getFilePointer());  // 初始位置  

           raf.seek(300); // 定位到300  
           byte[] b = new byte[1024];  
           int hasRead = 0;  
           while ((hasRead = raf.read(b)) > 0) { // 正常读，和InputStream没有任何区别  
               print(new String(b, 0, hasRead));  
           }  
       }  
   }  
}  
   2) 追加：
[java] view plain copy 在CODE上查看代码片派生到我的代码片
public class Test {  

   public static void print(String s) {  
       System.out.println(s);  
   }  

   public static void main(String[] args) throws IOException {  
       try (RandomAccessFile raf = new RandomAccessFile("test.out", "rw")) {  
           raf.seek(raf.length());  
           raf.write("lalala\r\n".getBytes());  
       }  
   }  
}  
   3) 中间插入内容：
[java] view plain copy 在CODE上查看代码片派生到我的代码片
public class Test {  

   public static void print(String s) {  
       System.out.println(s);  
   }  

   public static void main(String[] args) throws IOException {  
       File tmp = File.createTempFile("tmp", null);  
       tmp.deleteOnExit(); // 创建退出自动删除的临时备份文件  
       try (  
           RandomAccessFile raf = new RandomAccessFile("test.out", "rw");  
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
           raf.write("hahahahahaha".getBytes());  
           while ((hasRead = tmpIn.read(b)) > 0) { // 在将备份出去的内容追加到插入的内容之后  
               raf.write(b, 0, hasRead);  
           }  
       }  
   }  
}  

8. 多线程下载工具的设计理念：
   1) 可以使用RandomAccessFile来实现；
   2) 下载开始时会建立两个文件，一个是和原文件大小相同的空文件，一个是记录位置指针的位置文件；
   3) 下载时启动多个线程，每个线程中都new一个RandomAccessFile打开那个空文件（即多个RandomAccessFile同时指向该空文件，RandomAccessFile不是线程安全的，目的就是这个）；
   4) 每当网络断开或者发生什么异常都会立即将各个线程RandomAccessFile的位置指针记录到位置文件中，待下次连网后根据位置指针的位置继续下载；
