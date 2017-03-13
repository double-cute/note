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

1. [Stream构造]()
2. [流变换（修改）]()
3. [聚集操作]()
4. [聚集流的使用流程]()

<br><br>

### 一、Stream构造：[·](#目录)
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

### 二、流变换（修改）：[·](#目录)
> 将流经过一定的处理（排序、过滤等），这类操作的特点是：
>
> 1. 原来的流会被消耗掉，不得再使用了.
>   - 强行使用会抛出**[IllegalStateException]异常**.
>   - 即流已经不在状态了，不能再使用了.
>     - 示例：
>       - IntStream is = ...;
>       - is.sorted().limit();  // 错误！第一次sorted过后消耗掉了，不能再继续执行其它操作了
>       - is = is.sorted(); is.limit();  // 正确！返回的是新流，可以继续使用
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

**4.&nbsp; 去重：**

- 使用该方法**无需先进行排序**，因此非常牛逼.

```Java
// 1. XxxStream的方法
XxxStream distinct();

// 2. Stream<T>的方法
Stream<T> distinct();
```

<br>

**5.&nbsp; 限制最大长度：**

- 如果最大长度小于当前元素个数那么发生截断（只取前maxSize个）.

```Java
// 1. XxxStream
XxxStream limit(long maxSize);

// 2. Stream<T>
Stream<T> limit(long maxSize);
```

<br>

**6.&nbsp; 观察：**

- 不作出任何改变（返回的仍然是原来的流），仅仅就是逐个值迭代.
  - 常用于调试.
  - **不要在里面修改元素的内容.**

```Java
// 1. XxxStream
XxxStream peek(XxxConsumer action);

// Stream<T>
Stream<T> peek(Consumer<? super T> action);

// 示例：调试输出各个值是什么
s.peek(ele -> System.out.println("value = " + ele));
```

<br><br>

### 三、聚集操作：[·](#目录)
> 最大的特点就是：
>
>> 1. 会消耗流.
>> 2. 不会返回新的流，只会返回：
>>   - void
>>   - 一个统计计算结果（int、double等）.
>>   - 一个boolean表示是否查询成功等.
>>
>>> 一旦聚集操作过了，再享用刚刚的流做其它操作就只能重新构造了.

<br>

**1.&nbsp; 遍历：**

```Java
// 1. XxxStream
void forEach(XxxConsumer action);

// 2. Stream<T>
void forEach(Consumer<? super T> action);

// 超简洁遍历
s.forEach(System.out::println);
```

<br>

**2.&nbsp; 转化成数组：**

```Java
// 1. XxxStream
xxx[] toArray();

// 2. Stream<T>
Object[] toArray();
```

<br>

**3.&nbsp; 统计个数：**

```Java
// XxxStream、Stream<T>两者都适用
long count();
```

<br>

**4.&nbsp; 找最大/最小值：**

- 返回的是Optional可选类型.
  - 这种类型专门用于防null.
  - 想要得到对应类型数据get一下就行了.
    - 例如：
      - Optional<String> os = ...;
      - String s = os.get();

```Java
// 1. XxxStream版本
  // int、long、double本身就是有自然大小顺序的
OptionalXxx max|min();

// 2. Stream<T>版本
  // 强制定制排序，没有自然排序的版本
Optional<T> max|min(Comparator<? super T> comparator);
```

<br>

**5.&nbsp; 是否包含满足谓词条件的元素：**

```Java
// 1. XxxStream版本
boolean anyMatch(XxxPredicate predicate); // 至少存在一个满足
boolean allMatch(XxxPredicate predicate); // 全部都满足
boolean noneMatch(XxxPredicate predicate); // 全部都不满足

// 2. Stream<T>
boolean anyMatch(Predicate<? super T> predicate); // 至少存在一个满足
boolean allMatch(Predicate<? super T> predicate); // 全部都满足
boolean noneMatch(Predicate<? super T> predicate); // 全部都不满足
```

<br>

**6.&nbsp; 获取第一个元素：**

- 如果流为空则返回空的Optional（不能get）.
  - 如果不为空就返回实的Optional（可以get）.

```Java
// 1. XxxStream
XxxOptional findFirst();

// 2. Stream<T>
Optional<T> findFirst();
```

<br><br>

### 四、聚集流的使用流程：[·](#目录)

<br>

- 大致步骤：
  1. 构造.
  2. 处理（修改、变换）.
  3. 一次性统计.

<br>

- 示例：

```Java
double max = IntStream.
	builder().	// 1. 构造
	add(7).
	add(3).
	add(7).
	add(10).
	add(3).
	build().
		sorted().	// 2. 处理和变换
		limit(9).
		mapToDouble(e -> e * 3.14).
			max().	// 3. 最后一步一次性统计
			get();
```
