# EnumSet & 各类Set性能比较
> 1. EnumSet专门用于存放枚举类型的元素，由于枚举类型值是有限的，因此使用二进制位向量来保存.
>   - 因此其维护的不是插入顺序，而是**二进制位顺序**.
>   - 由于null无法表示成二进制位，因此**不能添加null（否则抛出空指针异常）** .
> 2. 从实现角度来看，效率从高到低为：EnumSet > HashSet > LinkedHashSet > TreeSet

<br><br>

## 目录

1. [EnumSet简介](#一enumset简介)
2. [构造EnumSet](#二构造enumset)
3. [Set各实现类的性能比较](#三set各实现类的性能比较)

<br><br>

### 一、EnumSet简介：[·](#目录)
> 除了构造方式比较特殊之外EnumSet没有对Set做任何拓展，也就是说除了构造方法外没有添加任何额外的方法（相较于Set）.

<br>

**1.&nbsp;**  EnumSet只能存放**一种**枚举类型的元素，一旦元素的枚举类型确定了，那么集合类型就确定了.

- 即**只能存放该种枚举类型的元素**，如果强行add其它枚举类型的元素会**直接编译报错**！
  - 例如：EnumSet\<Season\> es = ...; es.add(Color.BLUE);  // 就直接编译报错，虽然底层是二进制位，但还是会严格检查类型

<br>

**2.&nbsp;**  底层是用**二进制位向量**来保存枚举元素的：

1. 枚举值按照其在枚举类型的定义顺序编号.
  - 例如：enum Alpha { A, B, C, D } // 按照顺序，ABCD分别对应二进制位的0123四个位置
2. **EnumSet维护的是二进制位顺序.**
  - 例如，按照DCA的顺序add到es里，那么底层二进制位保存的结果就是1101.
    1. 1表示该二进制位所代表的枚举值存在，0表示不存在.
    2. 1号位0，表示其所代表的B不存在于集合es中，最低位1表示A存在于集合中.
  - out.print输出时的顺序也是ACD，**按照枚举值的定义顺序维护**（这是显然的，底层就是用二进制位向量存放的）.

<br>

**3.&nbsp;**  注意事项：

1. 二进制位向量本身就保证了元素不可能重复.
2. 二进制操作保证了containsAll、retainAll等各类**集合操作非常高效**.
3. 由于null无法用二进制位表示，因此**不能往EnumSet中添加null**，如果**强行add会抛出[NullPointerException]异常**.

<br><br>

### 二、构造EnumSet：[·](#目录)
> EnumSet并没有提供公开的构造器来构造对象，而是提供了很多静态工具方法来构造.
>
>> 会在编译时检查加入的元素的编译时候类型是否为 **同一**、**枚举类型**.
>>
>>   - 如果不是枚举类型，或者是枚举类型但元素不是同一种枚举类型都会直接编译时报错.

<br>

**1.&nbsp; 用枚举类型来构造：**

- 传入参数必须是枚举类型的.class，否则直接编译报错！

```Java
// 1. 将指定枚举类型的所有值添加进去
static EnumSet<E> allOf(Class<E> elementType);

// 2. 构造一个空的、存放elementType的EnumSet
static EnumSet<E> noneOf(Class<E> elementType);

// 示例
EnumSet<Season> es = EnumSet.allOf(Season.class);
```

<br>

**2.&nbsp; 利用另一个EnumSet构造：**

```Java
// 1. 简单复制（当然是深拷贝）
static EnumSet<E> copyOf(EnumSet<E> s);

// 2. 补集：构造一个s的补集
  // 例如：s = [A, B]; s的枚举类型为[A, B, C, D]; 那么complementOf(s) = [C, D]
static EnumSet<E> complementOf(EnumSet<E> s);
```

<br>

**3.&nbsp; 直接利用枚举值构造：**

```Java
// 1. 直接用若干枚举值构建一个枚举集合，例如EnumSet es = EnumSet.of(Season.SPRING, Season.WINNTER);
static EnumSet<E> of(E first, E... rest);
// 示例
EnumSet<Season> es = EnumSet.of(Season.SPRING, Season.SUMMER, Season.WINNTER);

/** 2. 直接用枚举值区间构建一个集合，包括[from, to]的所有枚举值
 *  from -> to的大小顺序为枚举值在枚举类型中的定义顺序
 *  [from, to]是闭区间！比较特殊！不是左闭右开了！因为枚举类型无法表示最后一个值的后一个值！
 */
static EnumSet<E> range(E from, E to);
```

<br>

**4.&nbsp; 利用只存放同一枚举类型元素的集合构造：**

- 被利用的集合必须满足一下条件：只要违背下述原则，必定会在编译时报错！
  1. 可以是Set、List等.
  2. 里面存放元素的运行时类型必须是：
    1. 枚举类型.
    2. 同一种枚举类型.
- 虽然c中的元素可能可以重复，但完全不必担心最终构造出的EnumSet的重复问题，二进制位向量自动避免（重复add返回false而已）.

```Java
static EnumSet<E> copyOf(Collection<E> c);

// 示例：
ArrayList<Season> li = new ArrayList<>();
li.add(Season.WINNTER);
li.add(Season.WINNTER);
li.add(Season.SPRING);

EnumSet<Season> es = EnumSet.copyOf(li);
System.out.println(es); // [Spring, WINNTER]
```

<br><br>

### 三、Set各实现类的性能比较：[·](#目录)

<br>

1. 就效率而言EnumSet毋庸置疑是最高的，毕竟是用二进制向量保存的，其次HashSet性能好于LinkedHashSet（仅仅多了一个维护插入顺序的链表），而TreeSet性能排最后，因为需要时刻维护红黑树的结构已达到有序状态（大小顺序）；
2. 至于碰到一个问题该选择何种Set的实现类就很简单了，关键看你是什么需求，枚举就用EnumSet，仅仅是一个无序集合就用HashSet，需要维护插入顺序就用LinkedHashSet，需要维护大小顺序的就只能用TreeSet了！
