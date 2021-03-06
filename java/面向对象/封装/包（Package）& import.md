# 包（Package）& import
> 为了**解决命名冲突**，和C++使用命名空间不一样的是，Java使用包来管理类库.
>
>> 1. Java要求所有类都必须放在包中.
>> 2. 在代码中使用任何类都需要加上包名前缀.
>>    - 除非**事先用import语句导入包**才可以省略前缀使用一个类.
>> 3. 包就是Java中类的**命名空间**.
>>
>>> 除了基本类型没有包（命名空间）外，其它所有Java类（引用类型）都必须放在包中管理.
>>>
>>> - **包中存放的单位是类.**

<br><br>

## 目录

1. [用package关键字指定代码中类所属的包](#一用package关键字指定代码中类所属的包)
2. [在没有import时包路径的省略规则](#二在没有import时包路径的省略规则)
3. [用包来组织和管理项目：包路径命名规范](#三用包来组织和管理项目包路径命名规范--)
4. [包路径到文件系统的映射](#四包路径到文件系统的映射)
5. [import](#五import)

<br><br>

### 一、用package关键字指定代码中类所属的包：[·](#目录)
> 只要在.java源文件的**第一个非注释行上**放置语句：**package 包路径;**
>
>> - 编译器就会把 **该源文件中** 定义的 **所有类** 都 **归到该包中**.
>>    - 编译出来的Xxx.class类在使用时必须加上包路径前缀，例如：
>>    - java com.example.Xxx  // 要在com/目录的上一级目录中运行该命令
>> - 源文件中**最多只能有一句**package.

<br>

**1.&nbsp; 包路径的命名规范：**

1. 全部小写.
2. 多个有意义的单词用"."连缀而成.
3. 中间不要有其它分隔符.

- 例如：package com.lirx;

<br>

**2.&nbsp; 完整的类名要有包路径：** 全限定类名

> 完整的类名称为**全限定类名**.

- 例如：类**Person**位于包**com.lirx**下，那么Person的完整类名应该是**com.lirx.Person**
- **在任何地方都可以使用全限定类名.**
   - 不管有没有import过，使用全限定类名一定不会错！
   - 只不过import后可以省略包路径而已.

<br><br>

### 二、在没有import时包路径的省略规则：[·](#目录)
> 包路径一般都很长，为了不使代码臃肿应该能省略就省略.

<br>

- 省略规则描述：在**同一个包中可以省略**包路径，在不同包中必须使用完整的包路径：
  - 同一个层级的包中，各类相互使用可以省略包路径而直接使用类名.
  - **但不包括父子关系的包！**
     - 例如包A包含包B（目录结构是A/B/...），但包A和包B还是两个不同的包.
     - 这就意味着，**包A中除了包B之外的其它一切** 要使用包B中的类都必须使用全限定类名.

<br><br>

### 三、用包来组织和管理项目：包路径命名规范  [·](#目录)
> Java的包路径除了可以防止命名冲突外还可以有效地行使项目组织和管理的功能.
>
>> 包路径本身就对应了一种目录结构.

<br>

- 包路径命名的国际规范：**公司域名的倒写.项目名.模块名.组件名**
   - 毕竟软件庞大起来完全有可能导致项目名、模块名、组件名同时冲突.
   - 因此就想出了将域名倒写放在包路径里的方法.
      - 毕竟域名在全世界范围内是唯一的，这就从源头上解决了明明冲突的问题.
   - 其次是这个规范的思路是，**从大到小分解一个项目**，最小到达组件的级别.

<br>

- 示例：com.company.ebay.submit.hint.Toolbar
   1. com.company是公司域名.
   2. ebay是项目名，表示一个购物网站.
   3. submit是一个提交系统.
   4. hint是一个组件，起到提示作用.
   5. Toolbar.class是该组件中的一个类.

<br><br>

### 四、包路径到文件系统的映射：[·](#目录)
> Java规定.class文件在文件系统中的目录结构必须和其所在的包路径完全相同.
>
>> 例如：com.example.A 必须对应 com/example/A.class路径.

<br>

**1.&nbsp; 原因很简单：**

1. 如果直接以"包路径+类名.class"作为.class的文件名的话会导致文件名过长.
   - 最致命的是，各种.class文件平行地放在一起根本没有层次感，不好管理，容易混乱.
2. 因此最简单的做法就是强制要求包路径和文件系统中的路径完全对应！

<br>

**2.&nbsp; 用java命令运行带包路径的类：**

> 必须在**包路径的根目录下**使用命令"**java 全限定类名**"才行.
>
>> 否则会执行失败，提示找不到该类.

- 示例：
   1. 全限定类名是com.example.A，对应的目录结构是dir/com/example/A.class
   2. 那么必须**在dir/目录下**执行：**java com.example.A**
      - 也可以执行：java com/example/A
         - 不管是Unix还是Windows，都是用/作为分隔符（统一采用Unix标准），即使在Windows中也用/作为包路径分隔符.
         - 在Windows中如果执行java com\\example\\A是错的！
   3. 还是推荐在使用JDK命令时使用"."作为包路径分隔符，就跟源代码中一样，清晰易懂！

<br>

- 其实java com.example.A命令的真正含义是：
   - 去CLASSPATH目录下寻找有没有com/example/A.class文件.
   - 如果有就加载运行，否则就报错！

<br>

**3.&nbsp; javac -d会自动根据package语句建立相应的目录结构：**

<br>

- javac的**-d选项**不仅具有**指定.class文件的输出目录**的功能，
   - 还会**根据package语句自动生成包路径对应的目录结构**！
   - 如果不加-d选项，就只能在当前目录下编译出没有目录结构的.class文件.
      - 示例：A.java -> package com.example;
         1. javac A.java  ->  ./A.class
         2. javac **-d .** A.java  ->  **./com/example/A.class**

<br>

- 如果.class文件没有package对应的目录结构的话是无法运行的！
   - 上面的javac A.java  ->  ./A.class，即使在./中java com.example.A也无法运行.
   - **必须自己手动建立目录**com/example/A.class后才能运行，**很麻烦**.
      - 因此**编译时加上-d选项是一个良好的习惯**.

<br>

**4.&nbsp; 定义类的包路径的是package不是文件系统的路径！**

- 不要误以为把A.class的目录结构构造成com/example/A.class后A的包路径就是com.example了！
   - 大错特错，**定义包路径的永远是package语句**！
   - 如果没有package语句的定义，不管你怎么构造文件路径都是没用的！
- **package语句经过编译后会将包路径写入.class文件作为标识！**

<br><br>

### 五、import：[·](#目录)
> 想在**包外**使用该包中的类并**省略其包路径前缀**就必须使用import导入目标包了.
>
>> import语句必须位于**package语句**之后和**第一个类/接口定义**之前.

<br>

**1.&nbsp; 两种导入语法：**

1. 导入单个类：import 目标类的全限定类名
   - 例如：import java.util.Date   // 这样就只导入了1个Date类
2. 通配符导入一个报下的所有类：import 包路径.*
   - 例如：import java.util.*   // 这样就导入了java.util报下的所有类
   - 但**不能导入子包**，即上面并不能导入util包下的子包中的类！

<br>

**2.&nbsp; 导入之后仍有冲突：** 只能使用全限定类名来消除歧义

- 比如同时导入：
   1. import java.util.*
   2. import java.sql.*
      - 两个包中都有Date类，此时就**只能使用全限定类**名访问Date类.
      - 否则存在歧义，编译器无法判断这个Date类到底是util包中的还是sql包中的.

<br>

**3.&nbsp; 静态导入：**

> 在JDK 1.5后引入的新特性.
>
>> 允许使用import static语句导入类的静态成员.
>>
>>> 导入后，在使用这些静态成员时，不仅可以**省略包路径**还可以**省略类名前缀**.

<br>

- 不加static的import只能导入非静态成员：
   - 例如：import com.example.\*; 其中有一个类A，它中有一个public static int val;
      - 但你不能在源码中省略**类名前缀**A引用val，即val = 10是错的，只能A.val = 10;

<br>

- 也分为单独导入和通配符导入.
   - 通配符导入同样不包括子包中的内容！

<br>

- 示例：
   1. import static java.lang.System.out;  // 单独导入一个out静态对象
      - 直接out.println(...);
   2. import static java.lang.Math.PI;   // 单独导入一个PI数据成员
      - 直接double d = PI + 1.5;
   3. import static java.lang.Math.\*;  // 导入Math中的所有成员
      - 直接double res = sqrt(PI);
