# 聚集操作流Stream
> Java 8新增的工具，并非I/O流，而是一种特殊的集合，但又不在Collection/Map体系当中，Stream自成一派.
>
> - 是一种维护插入顺序的可重复集合.
>
> <br>
>
>> 1. 用途：对集合中的内容进行聚集操作（特指统计操作，如求平均值、最大/最小值等）.
>> 2. 优点：效率极高.
>>   - 代价：**具有寿命**，很多聚集操作结束之后流就被消耗光了.
>>     - 想要再统计其它信息的话就得重新创建流（以及添加元素）.
>>
>>> 应用场景：**数据规模大、临时性、一次性的统计**，如果需要频繁统计则频繁消耗和创建流的代价又太大了.
>>>
>>>> 目前**只提供了int、long、double三种基本类型的Stream**，毕竟是统计专用，统计都是数值型元素.

<br><br>

## 目录

1. []()
2. []()

<br><br>

### 一、Stream构造：
> Stream是**聚集流**（聚集操作流的简称）的**根接口**，是**泛型**，**Stream\<T\>**.
>
>> 但目前只提供了IntStream、LongStream、DoubleStream三种**数值型派生接口**，分别对应三种基本类型聚集流.
>>
>> - Java隐藏了各种Stream的实现（都是private，不公开），原因是：
>>   1. 毕竟这是Java 8的新特性，实现可能还存在一定缺陷.
>>   2. 未来提供成熟的实现时再公开也不迟.
>> - 因此虽然返回的是Stream、XxxStream接口引用，但底层实现类并没有对相应的接口方法做任何扩展.
>>   - 因此完全可以当做实现类的引用来使用.

<br>

- **生产线模式：**
  - 利用stream生产者builder构造相应的stream.
    - Builder是Stream的内部接口：Stream$Builder
    - 构造步骤：
      1. 获取builder.
      2. 利用builder构造stream：
        1. builder.add()添加元素.
        2. builder.build()正式构造出来一个stream.

<br>

**1.&nbsp; 获取生产者builder：** Stream的静态工具方法

```Java
// 获取生产者
static XxxStream.Builder builder();

static Stream.Builder<T> builder();

// 示例
IntStream.Builder builder = IntStream.builder();

Stream.Builder<String> builder = Stream.builder();
```

<br>

**2.&nbsp; 添加元素：** Stream$Builder的对象方法

- 可以看到参数的首字母小写，这就意味着XxxStream是针对基本数据类型的.
- 返回的还是Builder对象本身，因此可以像C++的cin、cout一样连续插入.

```Java
XxxStream.Builder add(xxx t);
Stream.Builder<T> add(T t);

// 示例
IntStream.Builder builder = IntStream.builider().add(5).add(3);

/**
 *  连续调用必须要在builder()这个位置就能推断出类型参数的真实值.
 *  - 因此必须在这个位置显式声明类型实参才行
 */
Stream.Builder<String> builder = Stream.<String>builder().add("111").add("222"); // 正确
Stream.Builder<String> builder = Stream.builder().add("111").add("222"); // 错误！builder()必须要声明类型实参
```

<br>

**3.&nbsp; 生产完毕交付产品：** Stream&Buidler的对象方法

- builder最终返回生产出来的产品Stream对象.

```Java
XxxStream build();
Stream<T> build();

// 示例
IntStream is = IntStream.builder().add(5).add(3).build();

Stream<String> s = Stream.<String>builder().add("11").add("22").build();
// 或者
Stream.Builder<String> builder = Stream.builder();
Stream<String> s = builder.add("111").add("222").build();
```

<br>

**4.&nbsp; 利用Collection构造出一个Stream：**

- 假设Collection的类型参数是E.

```Java
Stream<E> stream(); // 也就是说所有的Collection系都有这个方法可以构造Stream

// 示例
TreeSet<String> ts = ...;
Stream<String> ss = ts.stream();
```

<br><br>

### 二、流变换（修改）：
> 将流经过一定的处理（排序、过滤等），这类操作的特点是：
>
> 1. 原来的流会被消耗掉，不得再使用了.
>   - 强行使用会抛出**[IllegalStateException]异常**.
>   - 即流已经不在状态了，不能再使用了.
> 2. 但是，**一定会生成一个处理后的新流**，可供继续使用.

<br>

**1.&nbsp; 映射成另一种类型的流：**

```Java
/* === 映射成int、long、double(即xxx)类型的stream === */
  // 1. XxxStream的方法
XxxStream mapToXxx(ToXxxFunction mapper);
  // 2. Stream<T>的方法
XxxStream mapToXxx(ToXxxFunction<? super T> mapper);
  // lambda表达式的函数式接口
public interface ToXxxFunction<T> {
    xxx applyAsXxx(T value);
}
// 示例
Stream<String> ss = ...;
IntStream is = ss.mapToIntStream(ele -> ele.length());

/* === Stream<T>独有的，转换成任意类型的stream === */
<R> Stream<R> map(Function<? super T, ? extends R> mapper);
public interface Function<T, R> {
    R apply(T t);
}
// 示例
Stream<MyClass> sm = ...;
Stream<String> ss = sm.map(ele -> ele.toString());
```

- 有时候通过Collection得到的stream需要转化，例如：

```Java
TreeSet<Integer> ts = ...;
Stream<Integer> si = ts.stream();  // stream()返回类型为Stream<T>
IntStream is = si.mapToIntStream(ele -> ele);  // 转换成XxxStream需要额外一步转化
```

<br>

**2.&nbsp; 排序：**

```Java
// 1. XxxStream只有自然排序一种版本
XxxStream sorted();

// 2. Stream<T>支持自然排序和定制排序两种版本
Stream<T> sorted([Comparator<? super T> comparator]);
```

<br>

**3.&nbsp; 过滤：** 把满足谓词条件的**留下**

```Java
// 1. XxxStream的过滤
XxxStream filter(XxxPredicate predicate);

// 2. Stream<T>的过滤
Stream<T> filter(Predicate<? super T> predicate);

// 示例
IntStream is = ...;  // 1, 2, 3, 4, 5
is.filter(ele -> ele > 3);  // 4, 5
```

<br>



    1) 聚集操作有两类，一类是中间方法，另一类是末端方法：
        i. 中间方法：进行一定的操作得到（返回）一个新的流（比如每个元素+1得到一个新的流），而这个新的流还可以继续进行其他聚集操作（没有被消耗）；
        ii. 末端方法：通常都是会计算出一个统计值的方法（比如求平均值、统计总共有多少个元素、求最大/最小值等），这些方法会消耗流，消耗完后流就不能再使用了，想要统计其它东西就必须要重新构造流了！！
    2) 典型的中间方法：

         iii. Stream peek(Consumer action);  // 观察流的内容，返回原来的流保持不变，action用来观察流中的每个元素，该方法主要用来调试，并不产生实际作用（比如输出流中的每个值）
         iv. Stream distinct(); // 去重后得到一个新的流，去重使用equals方法做相等比较，该方法不要求先对流进行排序，可以直接乱序去重（而且其算法也不是先对流进行排序的），因此非常牛逼！


         vi. Stream limit(long maxSize); // 规定最多允许访问多少个元素，比如一个流长度为5，而现在你limit(3)，那么count计算数量时返回的就是3，即规定了操作的范围
    3) 典型的末端方法：末端方法会消耗完整个流，调用完之后流宣布死亡，需要统计其它信息就得重新创建流了
         i. void forEach(Consumer action); // 遍历
         ii. Object[] toArray(); // 转换成相应的数组，如果是TypeStream那返回的就是Type[]了！
常规的统计方法：
         i. find最小/最大值：
            a. Stream版的：Optional Stream.min | max(Comparator comparator); // Optional<T>代表任意类型，任意类型比较大小必须要规定比较接口Comparator
            b. TypeStream版的：type TypeStream.min | max(); // 简单很多
         ii. 统计元素个数：long count();
         iii. 检查是否包含符合谓词条件的元素：
            a. boolean anyMatch(Predicate predicate); // 是否存在一个满足谓词条件的元素
            b. boolean allMatch(Predicate predicate); // 是否全部元素都满足谓词条件
            c. boolean noneMatch(Predicate predicate); // 是否全都不满足谓词条件
         iv. 获取第一个元素：Optional findFirst();

4. 示例：
public class Test {
	public static void main(String[] args) {

		IntStream is = IntStream.builder().add(50).add(30).add(60).add(30).add(70).add(70).add(10).build();
		is.filter(ele -> ele > 10).distinct().sorted().forEach(System.out::println);
	}
}
！！Lambda表达式打印一个元素可以直接System.out::println，表示打印那个ele迭代元素，最最变态的简洁！！
