# PrintStream & PrintWriter
> 和Scanner类似，属于 **处理流** 中 **特别高级的一类**，提供各种花式输出手段.
>
>> - **除了print、println、format/printf之外，这两者可以完全当做OutputStream和Writer使用**.
>>    - 其余没有扩展任何方法.
>>
>>> - 但两者的输出格式 **都是文本字符**.
>>> - 再加上，两者的方法基本相同，
>>>    - 因此 **前者运用较多**，因为前者是以字节的形式处理输出，比后者字符形式要高效.

<br><br>

## 目录

1. [构造器](#一构造器)
2. [print & println](#二print--println)
3. [格式化输出：format](#三格式化输出format--)
4. [追加：append](#四追加append--)

<br><br>

### 一、构造器：[·](#目录)
> 不仅可以包装 **任意节点流**，还可以直接包装 **文件节点**.
>
>> 由于 **文件输出特别常用**，因此专门提供了文件节点包装.

<br>

**1.&nbsp; 直接包装文件节点：**

```Java
// PrintStream
PrintStream|PrintWriter(File file | String fileName[, String charset]);
```

<br>

**2.&nbsp; 包装任意输出节点流：**

1. PrintStream可以额外指定encoding编码.
   - PrintWriter则木有，字符流必然是Java的Unicode编码.
2. PrintWriter还额外支持**字节节点流**的包装.
3. autoFlush默认是关闭的，如果**开启（true）会降低输出效率，一般发布版不开启**.
   - 开启后，默认会在一下三种情况自动调用flush冲马桶.
      1. 输出byte[].
      2. 使用println.
      3. 遇到\\n.

```Java
// PrintStream
PrintStream(OutputStream out[, boolean autoFlush[, String encoding]]);
// PrintWriter
PrintWriter(OutputStream out | Writer out[, boolean autoFlush]);
```

<br><br>

### 二、print & println：[·](#目录)
> 以 **字符的格式** 输出.

<br>

- print不换行，println换行（就是print的最后一个字符是\\n罢了）.

<br>

**1.&nbsp; 输出单个基本类型：**

- 支持所有基本类型，type重载了 **所有基本类型（除了byte和short）**.
   - 如果传单个byte或short则会调用int版本的向上兼容.

```Java
// type可以是boolean、char、int、long、float、double
void print(type x);
void println(type x);
```

<br>

**2.&nbsp; 输出单个字符串（String和char[]）：**

- 特别是String s这个版本特别重要，**它使得输出内容可以用+连缀**！

```Java
// 对于char[] s会将其连缀成一个String输出！
void print(String s | char[] s);
void println(String s | char[] s);
```

<br>

**3.&nbsp; 放空多态输出单个Object：** 多态**调用运行时类型的toString**方法！

- null输出"null"字符串

```Java
void print(Object obj);
void println(Object obj);
```

<br>

**4.&nbsp; println额外支持输出空行：**

```Java
void println(); // 等价于print('\n');
```

<br><br>

### 三、格式化输出：format  [·](#目录)
> 即C语言的printf，它有两种名字：
>
>> 1. printf：直接沿用C语言的printf，照顾C语言改行过来的开发者.
>> 2. format：听上去更加Java.
>
>>> - 两者完全一样，只不过就是两种不同的命名罢了，作用都是 **往输出流中write一个格式化字符串**.
>>> - 推荐使用format，因为听起来更Java.
>>>
>> 因此，这里就那format来介绍了.

<br>

- 方法原型：

```Java
// args虽然是Object类型的，但实际是根据其 **运行时类型** 决定如何格式化
PrintStream|PrintWriter format([Locale l, ]String format, Object ... args)
```

- Java format的语法格式总结：

<br>

**1.&nbsp; 字符和字符串：**

| 格式化参数 | 说明 |
| --- | --- |
| %c | 单个char |
| %s | 单个String |

<br>

**2.&nbsp; 整型：** 统一表示所有基本类型的整型（从byte到long），不分d和lld之类的

| 格式化参数 | 说明 |
| --- | --- |
| %d | 十进制 |
| %o %#o | 八进制 前缀0 |
| %x %#x | 十六进制 前缀0x|

<br>

**3.&nbsp; 浮点型：** 统一表示所有浮点型（float、double），部分f和lf之类的

| 格式化参数 | 说明 |
| --- | --- |
| %f | 普通形式 |
| %e | 科学计数法 |
| %g | 自然表示法：%f和%e中取短的那个 |
| %.5f | 指定小数点位数（5）|

<br>

**4.&nbsp; 特殊字符：**

| 格式化参数 | 说明 |
| --- | --- |
| %n | 换行 |
| **\\t** | 制表，不是%t |
| %b | 单个boolean类型 |
| %% | 百分比符号% |
| %tc | 完整信息的Date：Friday Sep 27 22:08:33 CST 2017 |

<br>

**5.&nbsp; 特殊标志：**

| 格式化参数 | 说明 |
| --- | --- |
| %8d %10f | 指定字宽（8个字符和10个字符）|
| %+9.3f | +表示正数加上+前缀（还带一个字宽9保留3位的浮点数）|
| %-10f | -表示左对齐（在字宽10中左对齐），默认是右对齐 |
| %0+8d | 前缀不0表示字宽不足补前缀0 |
| %,10d | ,表示逗号分组（还带一个右对齐10字宽）|

<br><br>

### 四、追加：append  [·](#目录)

<br>

**1.&nbsp; 追加单个字符：**

```Java
PrintStream append(char c);
```

<br>

**2.&nbsp; 追加一个字符序列：**

```Java
PrintStream|PrintWriter append(CharSequence cs[, int start, int end]);
```
