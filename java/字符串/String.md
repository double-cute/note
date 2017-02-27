# String
> Java提供了三种基本的字符串类型：String、StringBuilder、StringBuffer，其中：
>   1. String是**不可变类**（只读不可修改），一旦初始化后内容就固定，最为常用.
>   2. 后两者都是**可修改**的字符串类型.
>   3. StringBuilder是**单线程**的（线程不安全，但效率极高），StringBuffer是**线程安全**的（效率略逊色）.
>     - **两者的方法集合完全一样**，仅仅就是底层单线程和多线程的区别罢了.
>     - 之后以StringBuilder为准进行描述.
>
>> 本章**只介绍String**的用法.

<br><br>

## 目录

1. [构造器](#一构造器)
2. [长度以及大小写转换](#二长度以及大小写转换)
3. [连接](#三连接)
4. [检索](#四检索)
  1. str[index]
  2. index findChar|findSubString
  3. prefix|suffix
  4. getSubstring
  5. replaceAChar
5. [比较](#五比较)
6. [根据指定的编码方法将String转化成二进制字节序列](#六根据指定的编码方法将string转化成二进制字节序列)
7. [String的一些静态工具方法](#七string的一些静态工具方法)

<br><br>

### 一、构造器：[·](#目录)

<br>

**1. 用另一个字符串构造：** 最为常用

```Java
String(String original | StringBuilder builder | StringBuffer buffer);
// 三种字符串类型都行
// 其中字符串常量（如"abc"等）理所当然属于不可变的String类型
```

<br>

**2. 将二进制字节序列按照指定编码方式转换成字符串：** 较为常用

```Java
String(bytes[] bytes[, Charset charset | String charsetName]);

// 只在[offset, offset + len)区间进行转换构造
String(bytes[] bytes, int offset, int len[, Charset charset | String charsetName]);
```

- 如果不指定编码集将使用**Charset.defaultCharset**（即**OS默认编码集**）进行解码.

<br>

**3. 无参构造器：** 较为特殊

```Java
String();  // 构造一个长度为0的字符串
// 由于内容不能修改，长度又是0，因此一般用于特殊用途（作为空字符串的特殊用途）
```

<br>

**4. 利用字符数组转换：** 很少用，毕竟字符数组很少用

```Java
String(char[] value);
String(char[] value, int offset, int len);
```

<br><br>

### 二、长度以及大小写转换：[·](#目录)

<br>

**1. 返回长度：**

```Java
int length();  // 只有数组的是length属性，字符串的是length()方法！
```

<br>

**2. 大小写转换：**

```Java
String toLowerCase();  // 转成小写
String toUpperCase();  // 转成大写
```

<br><br>

### 三、连接：[·](#目录)

> 有+重载、concat、StringBuffer.append三种，效率从低到高相差非常大.

- 测试代码：
  - 可以看到3931 >> 1950 >> 16
  - **三者差距巨大，可见效率值悬殊.**

```Java
String s1 = "";
String s2 = "";
StringBuffer s3 = new StringBuffer("");

// +测试
long a = System.currentTimeMillis();
for(int i = 0 ;i < times ; i ++){
    s1 += "a";
}
long b = System.currentTimeMillis();
long time_s1 = b - a;  // 3931

for(int i = 0 ;i < times ; i ++){
    s2 = s2.concat("a");
}
long c = System.currentTimeMillis();
long time_s2 = c - b; // 1950

for(int i = 0 ;i < times ; i ++){
    s3.append("a");
}
long d = System.currentTimeMillis();
long time_s3 = d - c; // 16
```

<br>

**1. +重载：**

- 优点：可以和不同类型数据进行连接，书写美观简洁.
- 缺点：底层有**和StringBuilder、StringBuffer的混合类型转换**，再加上String本身是不可变类，实现中**需要大量的new**，因此效率极低.
- 适用场合：System.out.print输出**人眼观察的信息**的时候使用，对效率没要求，只对代码简洁可读性有要求的场合.

<br>


**2. String concat(String str);**

- 实现中**用new char[]数组作为中间缓存**，**没有**和StringBuilder和StringBuffer的混合类型转换，因此**效率高于+重载**.
- 适用场合：**数据量中等**的字符串操作，类型仅仅**只有String一种**，效率和代码风格兼具的场合.

<br>

**3. StringBuffer StringBuffer.append(String|StringBuffer|Object another);  // 同理StringBuilder**

- 优点：由于StringBuffer是**可变类**，连接时仅仅就是**在同一片内存中**进行操作，因此效率极高（**没有new开辟空间的消耗**）.
- 适用场合：**超大量**的数据处理，追求**极致的效率**.

<br><br>

### 四、检索：[·](#目录)

<br>

**1. 模拟数组的随机访问str[index]：**

```Java
char charAt(int index);  // index越界直接抛出异常[IndexOutOfBoundsException]
```

<br>

**2. 查找单个字符或子串的位置：** 返回索引值，**没找到返回-1**

```Java
// indexOf返回第一个匹配的，lastIndexOf返回最后一个匹配的，从[fromIndex, )中找
int indexOf|lastIndexOf(int ch[, int fromIndex]);  // 匹配单个字符
int indexOf|lastIndexOf(String str[, int fromIndex]);  // 匹配子串
```

<br>

**3. 是否有匹配的前后缀：**

```Java
boolean startsWith(String prefix[, int fromIndex]);  // 查看[fromIndex, )的前缀
boolean endsWith(String suffix);  // 查看后缀，没有fromIndex的版本，只有整串查看
```

<br>

**4. 截取子串：**

```Java
String substring(int beginIndex[, int endIndex]); // 默认截取[beginIndex, )，否则截取[beginIndex, endIndex)
```

<br>

**5. 替换单个字符：**

```Java
String replace(char oldChar, char newChar);  // 替换第一个匹配上的oldChar
```

<br><br>

### 五、比较：[·](#目录)

<br>

**1. 大小比较：**

```Java
int compareTo[IgnoreCase](String anotherString);  // 后者是无视大小写进行比较
```

1. 返回值：
  - **第一个**不相等的**字符差**.
  - 如果其中一个是另一个的**前缀**则返回两者**长度差**.
2. 一般这个差值的大小对于程序来说意义不大，一般只是根据差值的正是负0来判断两者的大小.

<br>

**2. 相等比较：**

```Java
boolean equals[IngoreCase](Object anObject);  // 覆盖于Object的equals，实际比较的是String类型

// 单独和StringBuffer比较，算是多线程的特殊处理，毕竟比较操作是可以并行化的！
boolean contentEquals(StringBuffer buff);  // 没有忽略大小写的变体，只有大小写敏感的版本！
```

<br><br>

### 六、根据指定的编码方法将String转化成二进制字节序列：[·](#目录)

```Java
byte[] getBytes([Charset charset | String CharsetName]);  // 默认采用Charset.defaultCharset编码

// 特殊的，获取字符序列
char[] toCharArray();
// 更一般的获取字符序列的方式
void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin);
// this[srcBegin, srcEnd) -> dst[dstBegin, )
```

<br><br>

### 七、String的一些静态工具方法：[·](#目录)

<br>

**1. 将其它类型转换成字符串对象：**

```Java
static String valueOf(type val);  // type包括所有的基本类型（boolean、int等）
```

- 不常用，通常引用类型都会覆盖Object的toString方法.
- 因此最常应用于基本类型到String的转化，但通常用+和""连接就能达到这个目的.

<br>

**其余查看String的API手册即可.**
