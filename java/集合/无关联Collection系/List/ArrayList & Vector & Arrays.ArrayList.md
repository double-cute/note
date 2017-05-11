# ArrayList & Vector & Arrays.ArrayList
> List的实现类有 ArrayList & Vector & Arrays.ArrayList（数组实现）、LinkedList（链表实现）
>
> 1. LinkedList有双端队列的功能，因此放在后面的章节讲解.
> 2. Vector多用于Java早期版本，**属于旧标准，很多方面实现的不好，已经被摒弃了**.
>    - 现在 **数组List的新标准是ArrayList**.
> 3. Arrays.ArrayList是被限制的ArrayList，只读、定长，初始化后永远不能改变.

<br><br>

## 目录

1. [ArrayList特有的方法](#一arraylist特有的方法)
2. [只读定长的Arrays.ArrayList](#二只读定长的arraysarraylist)

<br><br>

### 一、ArrayList特有的方法：[·](#目录)
> 由于ArrayList是用数组实现的，因此内部维护一个buffer数组.
>
> 1. buffer的大小就是capacity.
> 2. 即可以存放的 **元素个数** 的 **上限**，即ArrayList的 **物理容量**.
> 3. 如果存放的元素的个数超过该上限就会自动增大capacity（**动态开辟更多空间**）.

<br>

- 构造器：

```Java
// 1. 空集合，默认10个元素
ArrayList([int initialCapacity]);

// 2. 非空构造
ArrayList(Collection<? extends E> c);
```

<br>

**1.&nbsp; 手动调整capacity：**

```Java
/** 建议capacity调整到 ≥ minCapacity
 *  - 即调整到至少能存放minCapacity的大小
 *  - 通常程序员无需关心capacity大小，毕竟可以动态调整
 *     1. 但如果存放元素特别多，而且存放次数特别多，那么频繁地开辟内存会大大降低效率.
 *     2. 该方法的目的就是在上述这种情况下提前一次性开辟足够内存以减少开辟内存的次数
 *     3. 因此使用该方法的前提就是能做精确的预估（估少了降低效率，估多了浪费内存）.
 **
 *  特殊情况：当minCapacity小于当前存储的元素个数时会强制capacity = 当前元素个数，不会发生异常.
 */
void ensureCapacity(int minCapacity);
```

<br>

**2.&nbsp; 瘦身：**

```Java
/** 将capacity设为当前元素个数.
 *  - 即100%瘦身
 *     1. 一般只有在List已经确定不再改变后再瘦身.
 *     2. 这样可以最大程度节省内存空间.
 *     3. 但在处于长度可变的情况下不建议调用该方法（避免开辟空间造成性能下降）.
 */
void trimToSize();
```

<br><br>

### 二、只读定长的Arrays.ArrayList：[·](#目录)
> Arrays工具类提供了一个asList方法，可以生成一种只读、定长的List.
>
> - 其类型比较特殊，是Arrays中定义的内部类，而且是private的，即**private class Arrays.ArrayList\<T\>;**
>    - 也就是说外部无法访问这种特殊的类型.
>    - 但asList返回的是List\<T\>，即Arrays.ArrayList\<T\>的父类，因此可以正常使用.
>       - 毕竟Arrays.ArrayList是只读、定长、不能修改，因此用List操作它足够了（反正**就只能进行查看元素的操作**）.
>
>> 任何修改它内容的行为都将抛出**[UnsupportedOperationException]异常**.
>>
>> - 任何修改内容的方法在Arrays.ArrayList中都是不支持的.

<br>

```Java
// 直接枚举（可变参数列表）初始化要添加的元素
static <T> List<T> Arrays.asList(T... a);

// 示例
List<Integer> constant_li = Arrays.asList(1, 4, 2, 5, 7);

ArrayList<Integer> li = ...;  // 1, 2, 3
List constant_li = Arrays.asList(li.toArray());  // 正确！可变参数列表实质上是Java数组类型
List<Integer> constant_li = Arrays.asList(li.toArray());  // 错误！toArray返回的是Object[]，因此List<Object>和List<Integer>类型冲突
List<Integer> constant_li = Arrays.asList((Integer[])li.toArray());  // 错误！Object[]无法强转成Integer[]！
```
