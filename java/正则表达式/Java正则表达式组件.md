# Java正则表达式组件
> 组件包括：
>   1. 正则表达式**载体String**：这就不用说了，它是正则表达式的载体，所有操作都依赖String本身.
>     - 最重要的就是存放正则表达式命令，命令就是一组普通的字符串，必须编译成可执行体才能真正运作.
>     - 命令在编译之前只是普通的静态字符串.
>   2. 正则表达式**可执行体Pattern**：由命令**编译而成**的**可执行的**正则表达式.
>   3. 正则表达式**引擎Matcher**：相当于一台机器，可以执行编译好的正则表达式.
>     - **编译好的可执行的正则表达式仍然是静态的，只有放到Matcher引擎中执行时才是动态的.**

<br><br>

## 目录

1. [可执行体Pattern](#一可执行体pattern)
2. [引擎Matcher](#二引擎matcher)
3. [完整的正则表达式应用流程](#三完整的正则表达式应用流程)
4. [支持正则表达式的一些String对象方法](#四支持正则表达式的一些string对象方法)

<br><br>

### 一、可执行体Pattern：[·](#目录)
> 即**编译好的**正则表达式，即**命令编译后的产物**（一组**只有引擎才能识别的动作指令**）.

<br>

- **编译正则表达式** 的概念：
  1. 编译的概念：普通的命令**字符串**经过编译后变成一组**可执行的指令**.
  2. 正则表达式命令中包含很多特殊字符，如*、^等，它们不是普通字符，而是代表动作的代码，可以看成编程语言中的函数.
    - 例如，\*编译后变成：function_operator\*() { 指令集：匹配上一个表达式0次或多次; }

<br>

- **Pattern是什么？**
  1. 具有**编译**命令的功能.
    - 功能**类似flex/bison&**
  2. 同时也用来**存放编译结果**（可执行代码）.
    - 总结地说就是：将命令根据**一定语法规则解析后**生成的**可执行代码（动作函数组）** 就是Pattern对象.

<br>

- **创建Pattern：**
  - 不提供构造器，**必须手动显式**调用**static Pattern.compile方法** 编译生成.
    - 目的是为了**强调编译这个过程以及严谨的步骤**！

```Java
// 第一步：自己先设计正则表达式命令
String regex = "自己设计编写的表达式命令";

// 第二步：用Pattern的静态工具方法（编译器）compile对命令进行编译获取Pattern对象（可执行体）
Pattern pattern = Pattern.compile(regex);

// 总结：就是一个  regex(命令字符串)  ->  pattern(可执行代码)  的过程.
```

<br>

- Pattern的一些**常用对象方法**：
  - Pattern对象用一个**String this.pattern**来保存**被编译的命令字符串**.

```Java
// 以下两者一样
String pattern() { return this.pattern; }
String toString() { return this.pattern; }  // 只不过toString覆盖于Object，用途更广方便out.print输出
```

<br>

- Pattern的一些**代码特点**：
  1. 和String一样是**不可变类，一旦被编译，无法再改变其内容，必定线程安全**（无法修改正则表达式命令）.
  2. 先编译，后用来执行，**一次编译可以多次执行**，效率高.

<br><br>

### 二、引擎Matcher：[·](#目录)
> 它就是一台匹配机器，把 **主串（String）** 和 **模式串的匹配指令Pattern** 塞进去就可以进行匹配了.

<br>

- **如何生成Matcher引擎对象？**
  - 很显然，已经说过了，须要两要素：主串String和模式串匹配指令Pattern.
  - 可惜Matcher并没有提供Matcher(String mainText, Pattern mode)的构造器.
    - 目的同样是为了强调正则表达式严谨的步骤，强制你显示调用Pattern的matcher对象方法生成一个Matcher引擎对象：

```Java
Matcher matcher = pattern.matcher(CharSequence mainText);  // 主串作为输入，pattern本身就是模式串匹配指令
// 意义就是，让一个pattern去主动匹配一个主串
// 当然，一个pattern可以去匹配很多主串，生成不同的引擎对象
```

- 只有pattern.matcher没有new Matcher的**另一个主要原因**：
  - pattern.matcher具有很好的**面向对象意义**，而单单一个new Matcher(mainText, pattern)的**面向对象意义不浓，反而更接近晦涩的面向过程**.

<br>

- **查看信息：**
ii. 获取Matcher中嵌入的模式串：Pattern pattern();   // 返回嵌入的Pattern对象

<br>

#### 匹配：

> 匹配的过程就是一个**边扫描主串边检查是否匹配**的过程，因此必须要维护一个**位置指针**来指示**当前已经扫描到了主串的什么位置**.

<br>

**1. 清除状态重新来过：**

```Java
Matcher reset();  // 清除所有状态（位置指针归0），回到开始匹配之前，准备一轮全新的匹配

// 很可惜，只能重置主串，不能重置Pattern
Matcher reset(CharSequence text);  // 清除状态的同时将主串重设主串为text
```

<br>

**2. 接着往下匹配：** 会往下推进位置指针

```Java
/**
 *  从当前位置往下继续匹配.
 *    - 如果匹配到了一个子串就暂停匹配返回true，并保存匹配到的子串.
 *    - 如果一直扫描到结尾还没有符合要求的子串就返回false.
 *  - 所以一旦返回false就一定代表主串已经全部扫描完了，位置指针已经到END了！
 *  - 常以find作为循环控制条件：while (matcher.find()) { ... }
 */
boolean find();  

/**
 *  1. 先reset()一下清除所有状态.
 *  2. 再将位置指针放到start处.
 *  3. 调用一下find()往下匹配一个，就只有一个！
 */
boolean find(int start); // 调整位置到start处，重新进行一次匹配
// 关键字是：start、重新、一次（就只进行一次匹配）.
```

<br>

**3. 如果find返回true，那么就可以查看保存好的匹配结果了：**

<br>

- 结果的**内容**：结果用**捕获组**表示
  - **捕获组的概念：**
    1. 什么是**捕获**：其实就是匹配的意思，即**捕获**主串中能匹配上模式串的的子串.
    2. 什么是捕获组的**组**：
      - 引擎会根据正则表达式的特征把表达式划分成若干组.
      - 例如："(a)(b)(c)"被划分为3组，分别为"a"、"b"、"c".
      - 引擎还对组进行编号，依次为1、2、3（**从1开始计**）.
        - 从Java 7开始还支持对捕获组命名（String类型的命名）.
        - 可以通过编号或者命名访问捕获组内容.

```Java
String group();  // 返回所有组的连缀，即整个匹配到的子串
String group(int group);   // 根据编号访问捕获组
String group(String name);  // 根据命名访问捕获组

// 总结就是
String group([int num | String name]);
```

<br>

- 结果的**位置**：也可以根据捕获组查询

```Java
// [start, end)就是结果的范围区间
int start([int num | String name]);  // 返回本次匹配到的子串在主串中的起始位置
int end([int num | String name]);  // 结束位置
```

<br>

- 特殊匹配方法：**全局匹配**

```Java
boolean matches();   // 必须整个主串匹配才返回true
boolean lookingAt();  // 只要主串的开头匹配就返回true，比如用"abc"匹配主串"abcxxx"就行，因为主串前缀能匹配上"abc"
```

<br><br>

### 三、完整的正则表达式应用流程：[·](#目录)

```Java
String text = ...;  // 准备好主串

// 命令、编译、生成引擎
String regex = ...;  // 设计命令
Pattern pattern = Pattern.compile(regex);  // 编译命令
Matcher matcher = pattern.matcher(text);  // 指定要匹配的主串，从而生成一个匹配引擎

// 进行匹配
while (m.find()) {  // 一个个往下匹配
    // 利用匹配到的结果做一些想做的事情
	String s = m.group();
    String s1 = m.group(1);
    String sName = m.group(name);
    int start1 = m.start(1);
	...
}
```

<br><br>

### 四、支持正则表达式的一些String对象方法：[·](#目录)

```Java
// 1. split：以regex作为分隔符split，最最常用！
String[] split(String regex);

// 2. 全局匹配：this是否完全匹配regex
boolean matches(String regex);

// 3. 全局替换：将所有可以匹配regex的子串都替换成replacement
String replaceAll(String regex, String replacement);

// 4. 只替换第一个匹配到的
String replaceFirst(String regex, String replacement);
```
