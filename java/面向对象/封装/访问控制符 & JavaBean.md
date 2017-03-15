# 访问控制符 & JavaBean


<br><br>

## 目录

1. [访问控制符](#一访问控制符)
2. [JavaBean](#二javabean)

<br><br>

### 一、访问控制符：[·](#目录)
> 是封装的关键.
>
>> - Java有4种访问控制级别，范围从大到小（从宽到严）依次为：
>>    - public > protected > defualt(包) > private

<br>

**1.&nbsp; 4种控制符的限定范围：**

| 控制符 | 限定范围 |
| --- | --- |
| public | 上下左右、里外一切都可以访问 |
| protected | 包内和包外的子类public，包外其它类不可见 |
| default（不写默认就是它）| 包内public，包外不可见 |
| private | 类内public，类外不可见 |

<br>

**2.&nbsp; 如何选择合适的访问控制符：**

1. public：希望暴露给外界自由使用的接口式方法.
2. private：
   1. 一般**数据成员**都应该定义成private以限制外界自由访问
   2. 类**内部的工具方法**应该定义成private的.
      - 因为这些工具方法只会在类内部用到，外界用不到，或者外界乱用会导致错误.
   3. 用private修饰的成员**子类也不可见**.
      - 数据成员不用说了，子类直接不可见.
      - 方法也别想覆盖，只会产生一个新方法而已.
3. protected：
   - 由于protected修饰的成员对子类课件，因此一般都是为了继承而使用.
   - 特别是希望被子类覆盖的方法应该用protected修饰.
4. default：
   - 用于包中的各类间协同工作，但又不希望包外使用它.

<br>

**3.&nbsp; 注意事项：**

1. 4种访问控制符都可以修饰成员.
   - 但**只有public和default可以修饰外部类**.
      - 外部类不能修饰为private和protected的.
2. 一个.java源文件中**最多只能有一个public的外部类**：
   1. 如果一个public的外部类都没有，那么该.java文件的名称可以任意取.
   2. 如果有一个public的外部类，那么该.java文件的名称就必须跟该类的名称一样（大小写敏感！）.
      - 如果超过一个public外部类，或者.java文件名和public类名不一样就**直接编译报错**！

- javac和java命令的规则：
   1. 里面有几个类就javac出来多少个.class文件.
   2. 但是只能java里面有main入口的那个类才能运行程序.
      - 例如：D.java中定义了A、B、C、D、E这5个类，只有D是public的（这没问题），其中只有B有main入口.
         1. javac D.java后产生A.class ... E.class五个.class文件.
         2. 但是只能java B才能运行程序.

<br><br>

### 二、JavaBean：[·](#目录)
> 是一种非常科学而严谨的封装规范，在网络信息传递以及开源框架中被大量运用.

<br>

- JavaBean满足的规范：缺一不可
   1. 每一个个数据成员都必须是private的.
   2. 必须为每个数据成员提供public的setter和getter.
      - 这两点是的数据对外界被良好的隐藏.

```Java
public 成员类型 get成员名(); // getter
public void set成员名(成员类型 arg);  // setter
```
