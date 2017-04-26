# String
> Java提供了三种基本的字符串类型：String、StringBuilder、StringBuffer，其中：
>
> 1. String是 **不可变类**（只读不可修改），一旦初始化后内容就固定，最为常用.
> 2. 后两者都是 **可修改** 的字符串类型.
> 3. StringBuilder是 **单线程** 的（线程不安全，但效率极高），StringBuffer是 **线程安全** 的（效率略逊色）.
>    - **两者的方法集合完全一样**，仅仅就是底层单线程和多线程的区别罢了.
>    - 之后以StringBuilder为准进行描述.
>
>> 本章只介绍String的用法.

<br><br>

## 目录

1. [构造器](#一构造器)
2. [长度、判空、大小写转换](#二长度判空大小写转换)
3. [内容比较：相等&大小](#三内容比较相等大小--)
4. [索引随机访问：根据索引获取字符&子串](#四索引随机访问根据索引获取字符子串--)
5. [定位：确定字符&子串的索引位置](#五定位确定字符子串的索引位置-)
6. [匹配：子串、前后缀、模式串](#六匹配子串前后缀模式串-)
7. [处理&改造：脱白、分割、连缀、替换](#七处理改造脱白分割连缀替换--)
8. [字符串连接](#八字符串连接)
9. [编码成byte数组 & 转化成char数组](#九编码成byte数组--转化成char数组)
10. [获取一个格式化字符串：静态工具方法format](#十获取一个格式化字符串静态工具方法format--)
11. [将其它类型转换成String：静态工具方法valueOf](#十一将其它类型转换成string静态工具方法valueof--)
12. [字符串常量池注册：intern](#十二字符串常量池注册intern--)
13. [字符串哈希：hashCode](#十三字符串哈希hashcode--)

<br><br>

### 一、构造器：[·](#目录)

<br>

**1.&nbsp; 无参构造器：** 较为特殊

```Java
String();  // 构造一个长度为0的字符串
// 由于内容不能修改，长度又是0，因此一般用于特殊用途（作为空字符串的特殊用途）
```

<br>

**2.&nbsp; 用另一个字符串构造：** 最为常用

```Java
String(String original | StringBuilder builder | StringBuffer buffer);
// 三种字符串类型都行
// 其中字符串常量（如"abc"等）理所当然属于不可变的String类型
```

<br>

**3.&nbsp; 将二进制字节序列按照指定编码方式转换成字符串：** 较为常用

- 总的来说就是：区间？（整还是段），编码？（是否需要自己指定）

```Java
// 在 整个区间 或 [offset, offset + len)进行转换构造
String(byte[] bytes[, int offset, int len][, Charset charset | String charsetname]);
```

- 如果不指定编码集将使用 **Charset.defaultCharset（即OS默认编码集）** 进行解码.

<br>

**4.&nbsp; 利用字符数组转换：** 很少用，毕竟字符数组很少用

- 只考虑 区间？（整还是段），本身已经是字符了，无须考虑编码.

```Java
String(char[] value[, int offset, int len]);
```

<br><br>

### 二、长度、判空、大小写转换：[·](#目录)

<br>

**1.&nbsp; 返回长度：**

```Java
int length();  // 只有数组的是length属性，字符串的是length()方法！
```

<br>

**2.&nbsp; 判空：** 即检查length()是否为0

- 但如果引用是null的话会引发空指针异常的！

```Java
boolean isEmpty();
```

<br>

**3.&nbsp; 大小写转换：**

```Java
String toLowerCase([Locale locale]);  // 转成小写
String toUpperCase([Locale locale]);  // 转成大写
```

<br><br>

### 三、内容比较：相等&大小  [·](#目录)

<br>

**1.&nbsp; 相等比较：** 比较的是真实的字符内容

```Java
// 1. 覆盖于Object，比较的是真实的字符内容
boolean equals(Object anObject); // 但可以和任意类型比较（自动调用anObject.toString()）

// 2. 无视大小写的真实字符内容比较，只能和String类型比（自己重载的）
boolean equalsIgnoreCase(String anotherString);

// 3. 高效地和CharSequence、StringBuffer单独比较的特供版.
  // 只有大小写敏感的版本
boolean contentEquals(CharSequence cs | StringBuffer buff);
```

<br>

**2.&nbsp; 大小比较：**

```Java
// compareTo来自Comparable接口，而无视大小写的版本来自重载
  // 因此都只能严格地和String类型的比
int compareTo[IgnoreCase](String anotherString);
```

1. 返回值：
  - **第1个不相等的字符差**.
  - 如果其中一个是另一个的**前缀**则返回两者**长度差**.
2. 一般这个差值的大小对于程序来说意义不大，一般只是根据差值的正是负0来判断两者的大小.

<br>

### 四、索引随机访问：根据索引获取字符&子串  [·](#目录)

<br>

**1.&nbsp; 模拟数组的随机访问str[index]：**

```Java
char charAt(int index);  // index越界直接抛出异常[IndexOutOfBoundsException]
```

<br>

**2.&nbsp; 截取子串：**

```Java
// 1. String版本：[beginIndex, ) or [beginIndex, endIndex)
String substring(int beginIndex[, int endIndex]);

// 2. CharSequence版本：就只有[beginIndex, endIndex)一种
CharSequence substring(int beginIndex, int endIndex);
```

<br>

### 五、定位：确定字符&子串的索引位置 [·](#目录)

<br>

**1.&nbsp; 重载总结：**

1. 从前往后还是从后往前？ （indexOf返回第一个匹配上的，lastIndexOf返回最后一个匹配上的）
2. 找的是字符还是字符串？ （第1个参数是int ch还是String str？）
3. 是从头开始找还是区间？ （[fromIndex, )要指定吗？）

<br>

**2.&nbsp; 方法：**

```Java
// 没找到返回-1
int indexOf|lastIndexOf(int ch | String str[, int fromIndex]);
```

<br><br>

### 六、匹配：子串、前后缀、模式串 [·](#目录)

<br>

- 只检测是否存在：就只有contains

<br>

**1.&nbsp; 是否存在指定的子串：**

```Java
// 匹配的是一般化的CharSequence类型
boolean contains(CharSequence cs);
```

<br>

- 检测是否 **完全匹配**：startswith/endsWith、matches/RegionMatches

<br>

**2.&nbsp; 完全匹配指定的前后缀：**

```Java
// 1. 匹配前缀：可以选择在[fromIndex, )中找前缀
boolean startsWith(String prefix[, int fromIndex]);

// 2. 匹配后缀：不能指定区间，只能在整个this主串的范围内匹配
boolean endsWith(String suffix);
```

<br>

**3.&nbsp; 整个this主串匹配一个正则表达式：**

- 注意！是整个主串去匹配，如果只是存在模式串不算！

```Java
boolean matches(String regex);
```

<br>

**4.&nbsp; this和其它String部分完全相等匹配：** 可选择忽略大小写

```Java
// this[toffset, ) 是否和 other[ooffset, ooffset+len) 完全相同
boolean regionMatches(
  [boolean ignoreCase,]
  int toffset,  // this[toffset, )
  String other, int ooffset, int len  // other[ooffset, ooffset+len)
);
```

<br><br>

### 七、处理&改造：脱白、分割、连缀、替换  [·](#目录)

<br>

**1.&nbsp; 前后脱白：**

```Java
// 脱去前后所有连续的空白符（空格、制表、换行）
String trim();
```

<br>

**2.&nbsp; 指定分隔符将this分割成一个String数组：**

```Java
// 可以根据limit选择性分割出指定数量的子串，如果不指定则全局分割
String[] split(String delimiterRegex[, int limit]);
```

- 示例：分割 "boo:and:foo"
   - 可以看到 ≤0 等价于不传limit.
   - 如果 limit≥能分的总个数 也等价于不传limit.

| 分隔符 | limit | 结果 |
| :---: | :---: | --- |
| : | 2 | "boo", "and:foo" |
| :	| 5	| "boo", "and", "foo" |
| :	| -2 | "boo", "and", "foo" |
| o	| 5	| "b", "", ":and:f", "", "" |
| o	| -2 | "b", "", ":and:f", "", "" |
| o	| 0	| "b", "", ":and:f" |

<br>

**3.&nbsp; 将多个字符串连缀成一个String：String的静态工具方法**

- 使用指定的分隔符delimiter连缀.

```Java
// 1. 连缀可变参数字符串（也可以是字符串数组）
static String join(CharSequence delimiter, CharSequence... elements);

// 2. 连缀字符串容器内的元素（特征是可迭代）
static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements);
```

- 示例：

```Java
// 1.
String res = String.join("-", "Hello", "World");  // Hello-World
String res = String.join("-", new String[]{ "Hello", "World" });

// 2.
List<String> strs = new ArrayList<>();
// Set<String> strs = new LinkedHashSet<>(); 两者二选一
strs.add("Hello"); strs.add("World");
String res = String.join("-", strs); // Hello-World
```

<br>

**4.&nbsp; 替换：**

- 全局替换是指**匹配到的每一个都替换**！

```Java
// 1. 全局替换单个字符
String replace(char oldChar, char newChar);

// 2. 全局替换子串：注意"aaa"中用"b"替换"aa"的结果是"ba"！
String replace(CharSequence target, CharSequence replacement);

// 3. 模式替换：All表示全局替换，First表示只替换第一个匹配上的
String relaceAll|replaceFirst(String regex, String replacement);
```

<br><br>

### 八、字符串连接：[·](#目录)

> 共有 **+重载、concat、StringBuffer.append** 3种，效率从低到高相差非常大.

<br>

```Java
// 1.
+、+=

// 2.
String concat(String str);

// 3.
StringBuffer StringBuffer.append(String|StringBuilder|StringBuffer s);
StringBuilder StringBuilder.append(String|StringBuilder|StringBuffer s);
```

<br>

- 测试代码：
  - 可以看到时间消耗为：3931 >> 1950 >> 16
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

**1.&nbsp; +重载：**

- 优点：可以和不同类型数据进行连接，书写美观简洁.
- 缺点：底层有 **和StringBuilder、StringBuffer的混合类型转换**，再加上String本身是不可变类，实现中 **需要大量的new**，因此效率极低.
- 适用场合：System.out.print输出**人眼观察的信息**的时候使用，对效率没要求，只对代码简洁可读性有要求的场合.

<br>

**2.&nbsp; String concat(String str);**

- 实现中 **用new char[]数组作为中间缓存**，没有和StringBuilder和StringBuffer的混合类型转换，因此 **效率高于+重载**.
- 适用场合：**数据量中等**，类型仅仅 **只有String一种**，效率和代码风格兼具的场合.

<br>

**3.&nbsp; StringBuffer.append(同理StringBuilder)：**

- 优点：由于StringBuffer是**可变类**，连接时仅仅就是**在同一片内存中**进行操作，因此效率极高（**没有new开辟空间的消耗**）.
- 适用场合：**超大量** 的数据处理，**追求极致的效率**.

<br><br>

### 九、编码成byte数组 & 转化成char数组：[·](#目录)

<br>

**1.&nbsp; 编码成byte数组：**

```Java
// 1. 无参默认用Charset.defaultCharset编码
byte[] getBytes([Charset charset | String CharsetName]);

// 2. C语言风格，只能用默认编码集：this[srcBegin, srcEnd) -> dst[dstBegin, ...
void getBytes(int srcBegin, int srcEnd, byte[] dst, int dstBegin);
```

<br>

**2.&nbsp; 获取char数组：**

```Java
// 1. 获取整列，最常用
char[] toCharArray();

// 2. C语言风格：this[srcBegin, srcEnd) -> dst[dstBegin, ...
void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin);
```

<br><br>

### 十、获取一个格式化字符串：静态工具方法format  [·](#目录)

<br>

- 格式化占位符大全请参考 [**IO-处理流-PrintStream & PrintWriter-格式化输出format**](../IO/处理流/PrintStream%20%26%20PrintWriter.md#三格式化输出format--)

```Java
static String format([Locale] l, String format, Object... args);
```

<br><br>

### 十一、将其它类型转换成String：静态工具方法valueOf  [·](#目录)

<br>

**1.&nbsp; 单个基本类型转化成String：**

```Java
// type支持：boolean、char、int（提升包容了byte、short）、long、float、double
static String valueOf(type val);
```

<br>

**2.&nbsp; 防空转换单个Object：**

```Java
// o.toString()有空指针异常的风险，但该方法可以防空（o为null时返回"null"字符串）
static String valueOf(Object o);
```

<br>

**3.&nbsp; 将char数组转换成String：**

```Java
// 将 整个data 或 data[offset, offset+len) 转化成String
static String valueOf(char[] data[, int offset, int count]);
```

<br><br>

### 十二、字符串常量池注册：intern  [·](#目录)
> 总得来说就是将this加入常量池并返回常量池String的引用.
>
>> 由于常量池内的常量访问效率高，因此在有些效率需求的场合会应用到.

<br>

- 原型：

```Java
String intern();
```

- 原理：
   - 检查this的内容是否存在于String常量池中. （使用equals和常量池中的String常量比较）
      1. 如果存在就返回常量池的那个String的引用.
      2. 如果不存在就将this的内容加入常量池中，并返回常量池String的引用.
- 因此，不管s1和s2是如何创建（获取）的，只有它俩equals相等，那么s1.intern() == s2.intern()一定为true.
   - 必定地址相同，都来自于常量池的那个String.

<br><br>

### 十三、字符串哈希：hashCode  [·](#目录)

<br>

```Java
// 覆盖于Object的hashCode
int hashCode();
```

<br>

- 算法：s[0] × 31^(n-1) + s[1] × 31^(n-2) ... + s[n-1]
   - ^表示次方.
   - 内部维护这一个private int hash，只有第一次调用该方法时会计算一次.
      - 后面再调用就直接返回hash变量了.
