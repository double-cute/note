# Scanner
> 是一种基于正则表达式的**文本**扫描器.
>
> - 注意：是文本扫描器，也就是说它处理的对象是字符.
>
>> 1. 是不折不扣的高级处理流（输入流）.
>> 2. 可以根据正则表达式解析文本中的各种模式串.
>>
>>> - Scanner的解析原理：
>>>    1. 以delimiter作为分隔符，将整个文本按照顺序划分成一个单词列表.
>>>    2. 接着从头到尾按顺序扫描列表中的单词.
>>>    3. 根据实际需求，将单词解析成：目前只支持以下5种类型
>>>       1. 基本数值类型：byte、short、int、long、float、double（没有char）
>>>       2. boolean
>>>       3. BigDecimal、BigInteger
>>>       4. 单词本身：String，不做任何解析
>>>       5. 模式字符串：String，指定正则表达式
>>>
>> - **注意：**
>>    1. 如果匹配**成功**，那么位置指针移到匹配内容的后一个字符位置.
>>       - 即**分隔符的第一个字符处**！而**不是下一个单词首字符处**！
>>    2. 如果匹配**失败**，那么该返回null的返回null，该抛出异常的抛出异常.
>>       - 并且，**位置指针原地不动**.
>>       - 除了以下之外，其余匹配失败都直接抛出 **[InputMismatchException]匹配失败异常**.
>>          1. **findInLine、findWithinHorizon**：没找到就 **返回null**.
>>          2. **nextLine**：过了EOF还要弄就抛出 **[NoSuchElementException]没找到异常**.

<br><br>

## 目录

1. [构造器：包装的目标基本都是字节流](#一构造器包装的目标基本都是字节流--)
2. [关闭Scanner：close](#二关闭scannerclose--)
3. [分隔符：delimiter](#三分隔符delimiter--)
4. [解析单词：hasNext-next](#四解析单词hasnext-next--)
5. [从当前位置开始跳过一个模式串：skip](#五从当前位置开始跳过一个模式串skip--)
6. [复位：reset](#六复位reset--)
7. [解析行](#七解析行)
8. [忽略demiliter的正则表达式匹配](#八忽略demiliter的正则表达式匹配)
9. [示例](#九示例)

<br><br>

### 一、构造器：包装的目标基本都是字节流  [·](#目录)

<br>

**1.&nbsp; 包装字节流：**

```Java
// 由于是字节流，因此可以指定编码方案（将字节转化成Unicode的方法）
Scanner(InputStream source[, String charsetName]);

// 示例：注明的标准输入
Scanner scan = new Scanner(System.in);
```

<br>

**2.&nbsp; 包装文件节点（其实底层包装的是文本字节输入流）：**

```Java
Scanner(File source[, String charsetName]) throws FileNotFoundException;
{ // 可以看到底层实际包装成了文件字节输入流
  this((ReadableByteChannel)(new FileInputStream(source).getChannel()));
}
```

<br>

**3.&nbsp; 特殊的，直接包装一个String作为输入源：**

- 直接使用源码编码Unicode，无须指定编码方案.

```Java
Scanner(String source);
```

<br><br>

### 二、关闭Scanner：close  [·](#目录)
> Scanner同样属于IO资源，可以显式close关闭.
>
>> 但也实现了AutoCloseable，可以在try语句中释放.

<br>

```Java
void close();
```

<br><br>

### 三、分隔符：delimiter  [·](#目录)
> Scanner使用**正则表达式**来描述分隔符.

<br>

**1.&nbsp; 查看当前使用的分隔符：**

- **默认使用空白符**（空格、制表、换行、回车等）作为分隔符.

```Java
Pattern delimiter();
```

<br>

**2.&nbsp; 设置分隔符：**

```Java
// 既可以用Pattern指定，也可以用String正则表达式源命令来指定
  // 返回新的scanner，原来的scanner不能用了（无效了）
Scanner useDelimiter(Pattern pattern | String pattern);
```

<br><br>

### 四、解析单词：hasNext-next  [·](#目录)

<br>

- 如果 **下一个单词** 匹配失败. （**没hasNext检验就直接next所导致的匹配失败**）
   - 就直接抛出 **[InputMismatchException]匹配失败异常**.

<br>

**1.&nbsp; 不做任何解析，直接吐出下一个单词：**

```Java
boolean hasNext();
String next();
```

<br>

**2.&nbsp; 匹配整型：** 需要指定进制radix

- **默认radix = 10**，即默认是十进制的.
- 基本类型整型只支持（即type所代表的）：**byte、short、long、BigInteger**
   - **没有char**.
- 进制支持**2 - 16**，最好**不要超过16**，因为**≥16的数字没法表示**.

```Java
/* === 无参表示使用默认进制，否则使用指定的进制 === */

// 1. 下一个单词是否是type类型的
boolean hasNextType([int radix]);

// 2. 把下一个单词解析成type类型后吐出
type nextType([int radix]);
```

- 查看当前默认的进制radix：

```Java
int radix();
```

- 重设默认的进制radix：

```Java
// 返回重设后的新的scanner，旧的scanner失效了（无法再使用了）
Scanner useRadix(int radix);
```

<br>

**3.&nbsp; 匹配 boolean & 浮点型 & BigDecimal：**

- type指代：boolean、float、double、BigDecimal
   - 不存在radix进制的问题.

```Java
boolean hasNextType();
type nextType();
```

<br>

**4.&nbsp; 匹配 正则表达式模式串：**

- 推荐使用Pattern pattern的版本.
   - **编译好的要比没编译好的省去现场编译的时间.**

```Java
boolean hasNext(Pattern pattern | String pattern);
String next(Pattern pattern | String pattern);
```

<br><br>

### 五、从当前位置开始跳过一个模式串：skip  [·](#目录)

<br>

```Java
/**  是从当前位置指针处开始跳.
 *  
 *   1. 这个跳是忽略分隔符delimiter的！
 *   2. 如果当前位置开始的字符串匹配不上pattern则会抛出NoSuchElementException.
 *     - 当前位置处必须能匹配上，否则就抛出异常.
 *   3. 只能跳过一个模式串，不能连跳多个.
 */
Scanner skip(Pattern pattern | String pattern);

// 一个
scan = scan.skip(pattern);
// 等价于下面两句
if (scan.hasNext(pattern))
  scan.next(pattern);
else
  throw new NoSuchElementException();
```

<br><br>

### 六、复位：reset  [·](#目录)
> 位置指针不动！
>
>> 复原的仅仅是locale、delimiter、radix这三者而已.
>>
>> - 都复原成默认值（**OS本地语言、空白、10**）.

<br>

```Java
Scanner reset();

// 等价于
scan = scan.reset();
// 等价于
scan = scan
  .useDelimiter("\\p{javaWhitespace}+")
  .useLocale(Locale.getDefault(Locale.Category.FORMAT))
  .useRadix(10);
```

<br><br>

### 七、解析行：[·](#目录)

<br>

- **从当前位置指针处开始**，**临时** 以 **'\n'和EOF** 作为分隔符往下解析.

```Java
boolean hasNextLine();
// 如果已经到了EOF再调用则会抛出NoSuchElementException异常
String nextLine();
```

<br><br>

### 八、忽略demiliter的正则表达式匹配：[·](#目录)
> 下面两个方法都**无视分隔符demiliter**.

<br>

**1.&nbsp; 剩余全文匹配：**

```Java
/**   功能描述：
 *    
 *     1. 从当前位置指针处开始.
 *     2. 从接下来的horizon个字符里（包括当前位置指针处）.
 *       - 如果 horizon = 0 或者 horizon > 剩下所有的字符个数，那么表示扫描剩余全文.
 *     3. 找可以匹配pattern的第一个模式串.
 *
 *     - 如果匹配到了，那么：
 *       1. 返回匹配到的模式串.
 *       2. 位置指针移到匹配串最后一个字符的后面一个字符位置.
 *     - 如果没找到，那么：
 *       1. 返回null.
 *       2. 位置指针原地不动.
 */
String findWithinHorizon(Pattern pattern | String pattern, int horizon);
```

<br>

**2.&nbsp; 匹配紧接下来的一行：**

```Java
/**   功能描述：从当前位置开始的一行中，匹配模式串
 *  
 *    - 等价于：findWithinHorizon中 horizon = 当前位置到下一个\n的字符个数.
 *      - 最后的EOF也当成\n
 */
String findInLine(Pattern pattern | String pattern);
```

<br>

**3.&nbsp; 示例：**

```Java
String s = null;

Scanner sc1 = new Scanner("abc999sldkfjkl999wejf");
while ((s = sc1.findWithinHorizon("999", 0)) != null) {
	out.println(s); // 999, 999
}

Scanner sc2 = new Scanner("abc\nsdf999\nwe\nwe999");
s = sc2.findInLine("999");
out.println(s); // null
sc2.nextLine(); // 跳过abc\n这一行
s = sc2.findInLine("999");
out.println(s); // 999，此时匹配完之后落在第一个999h后面的\n处
sc2.nextLine(); // 所以连跳两行（第2个\n）
sc2.nextLine(); // 跳第3行we\n
s = sc2.findInLine("999");
out.println(s); // 999
```

<br><br>

### 九、示例：[·](#目录)

<br>

- 注意：Scanner**没有任何一个方法**可以**将位置指针直接复原为0的**.
   - 下面的所有方法都是连续操作，一气呵成.

```Java
Scanner scan = new Scanner(
  "1011 10101 1011 ascbx382x9843x1123x10821abcsde\nsefwe\nxclwe\n"
);

while (scan.hasNextInt(2)) { // 逐个解析二进制int
	System.out.println(scan.nextInt(2)); // 1011, 10101, 1011
} // ptr停在1011的后一个空白处

scan.skip("\\D+"); // 略掉一段非数字" ascbx"
// 此时ptr停在382的3处

scan = scan.useDelimiter("x"); // 设置分隔符为'x'

while (scan.hasNextInt()) { // 逐个解析默认的10进制int
	System.out.println(scan.nextInt()); // 382, 9843, 1123
} // ptr停在1123后面的x处

while (scan.hasNextLine()) { // 逐行读取
	System.out.println(scan.nextLine()); // 10821abcsde, sefwe, xclwe
}

/* 输出
11
21
11
382
9843
1123
x10821abcsde
sefwe
xclwe
*/
```
