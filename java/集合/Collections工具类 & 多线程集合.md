# Collections工具类 & 多线程集合
> 操作集合的工具类（静态方法集），可以操作Colletion系和Map系的所有实现类对象.
>
>> 主要功能包括：
>>
>> 1. 对元素进行排序、查找、修改.
>> 2. 设置集合的属性（只读、可修改等）.
>> 3. 同步控制（将单线程集合转化成多线程版本）.

<br><br>

## 目录

1. [List排序](#一list排序)
2. [List查找](#二list查找)
3. [List批量填充和替换](#三list批量填充和替换)
4. [Collection查询信息](#四collection查询信息)
5. [生成不可变集合](#五生成不可变集合)
6. [同步控制（生成多线程集合）](#六同步控制生成多线程集合)

<br><br>

### 一、List排序：[·](#目录)
> 排序只能用于线性存储结构，因此Set和Map都不行，只能用于List.

<br>

**1.&nbsp; 反转：**

```Java
static void reverse(List<?> list);
```

<br>

**2.&nbsp; 排序（按照元素的大小顺序）：**

```Java
// 1. 自然排序（元素自身实现Comparable的compareTo方法）
static <T extends Comparable<? super T>> void sort(List<T> list);

// 2. 定制排序
static <T> void sort(List<T> list, Comparator<? super T> c);
```

<br>

**3.&nbsp; 洗牌（随机打乱顺序）：**

```Java
static void shuffle(List<?> list);
```

<br>


**4.&nbsp; 根据索引交换两个元素的顺序：**

```Java
// 注意IndexOutOfBoundsException异常（索引要给的对）
static void swap(List<?> list, int i, int j);
```

<br>

**5.&nbsp; 旋转：**

```Java
/**
 *  如果distance为：
 *  1. 正：将后distance个元素挪到前面.
 *  2. 负：将前distance个元素挪到后面.
 *
 *  - distance可以大于list.size()，因为第一步会先diantance %= list.size()一下的.
 */
static void rotate(List<?> list, int distance);
```

<br><br>

### 二、List查找：[·](#目录)

<br>

**1.&nbsp; 二分查找元素的位置：** 前提条件是**事先已经排好序了**（大小顺序）

- 找不到返回-1.

```Java
// 1. 自然排序下的二分查找
static <T> int binarySearch(List<? extends Comparable<? super T>> list, T key);

// 2. 定制排序下的二分查找
static <T> int binarySearch(List<? extends T> list, T key, Comparator<? super T> c);
```

<br>

**2.&nbsp; 寻找子集的位置：**

- 没找到就返回-1.

```Java
// 1. target在source中第一次出现的位置
static int indexOfSubList(List<?> source, List<?> target);

// 2. target在source中最后一次出现的位置
static int lastIndexOfSubList(List<?> source, List<?> target);
```

<br>

### 三、List批量填充和替换：[·](#目录)

<br>

**1.&nbsp; 批量填充：**

```Java
// 整个list用obj填充
static <T> void fill(List<? super T> list, T obj);
```

<br>

**2.&nbsp; 批量替换：**

```Java
// 把所有oldValue替换成newValue
  // 如果至少有一个值被成功替换就返回true
static <T> boolean replaceAll(List<T> list, T oldVal, T newVal);
```

<br><br>

### 四、Collection查询信息：[·](#目录)
> 只适用于Collection.

<br>

**1.&nbsp; 找最大/最小元素：**

- 各提供了自然排序和定制排序两个版本.

<br>

- 前提要求：违反就会抛出异常！
  1. max/min要求集合中至少要有一个元素.
  2. 如果有大于等于2个元素，那其中不能有null（无法比较大小）.
- 如果只有一个元素并且是null的话，那返回的就是null，但这样通常没有任何意义.

```Java
// 1. 最大
static <T extends Object & Comparable<? super T>> T max(Collection<? extends T> coll);  // 自然排序
static <T> T max(Collection<? extends T> coll, Comparator<? super T> comp); // 定制排序

// 2. 最小
static <T extends Object & Comparable<? super T>> T min(Collection<? extends T> coll);  // 自然排序
static <T> T min(Collection<? extends T> coll, Comparator<? super T> comp); // 定制排序
```

<br>

**2.&nbsp; 统计出现频率：**

- 异常安全：
  1. c为空没关系，返回0.
  2. null的个数也能统计.
- 一般用于允许重复的集合，不可重复的集合没意思.

```Java
// 统计o出现在c中的次数（equals）
static int frequency(Collection<?> c, Object o); // 一般应用于可重复的集合
```

<br><br>

### 五、生成不可变集合：[·](#目录)
> 这里的不可变是指：
>
>> 1. 通过集合本身的对象方法无法改变（比如add、clear、remove等）.
>> 2. 但是通过元素的引用调用元素自身的对象方法还是可以改变元素的内容的.
>>   - 例如：
>>     1. set.add(...);  // 拒绝，集合方法修改元素将失败
>>     2. set.remove(...);  // 同样拒绝，同样也是集合方法修改元素
>>     3. set.forEach(ele -> ele.setValue(22));  // 允许修改，是通过元素本身的方法去修改是允许的
>>
>>> 不可变集合一般用于如特殊用途，比如作为常数池，专门用于存放频繁用到的一些常数之类的.

<br>

**1.&nbsp; 生成空集合：**

```Java
/** Xxx支持Set、SortedSet、List、Map、SortedMap
 *  
 *  - 返回的私有的EmptyXxx内部类对象，都继承自Xxx.
 *    - 不必担心，返回的必定是实现类对象.
 */
static final <T> Xxx<T> emptyXxx();
```

<br>

**2.&nbsp; 生成只包含一个元素的集合：**

- 返回的都是私有内部类对象SingletonXxx.
  - 可以放心使用，一定都是实现类对象，可以放心使用.

```Java
// 1. Set
static <T> Set<T> singleton(T o);  // 只有set没有Xxx后缀，需要特殊记忆一下！

// 2. Map
static <K,V> Map<K,V> singletonMap(K key, V value);

// 3. List
static <T> List<T> singletonList(T o);
```

<br>

**3.&nbsp; 由可变集合生成一个对应的不可变集合：**

```Java
// Xxx支持Collection、Set、SortedSet、List、Map、SortedMap
static <T> Xxx<T> unmodifiableXxx(Xxx<? extends T> c);
```

<br><br>

### 六、同步控制（生成多线程集合）：[·](#目录)
> Java的集合框架（包括所有Collection、Map全部都是单线程的）.
>
> - 毕竟单线程效率更高，应用更广.
> - 但Java集合框架**并没提供多线程版本**的集合.
>
>> 只有老版本的几种集合Vector、Stack等默认是线程安全的（多线程），但是实现较差，已经被新标准替代了.

<br>

```Java
// 将一个单线程集合转换成多线程集合
  // Xxx支持Collection、Set、List、Map
static <T> Xxx<T> synchronizedXxx(Xxx<T> c);

// 例如
static <K,V> Map<K,V> synchronizedMap(Map<K,V> m);
```

<br>

- 可以看到返回都是抽象接口类型的对象：Collection、Set、List、Map
  - 你可能会烦恼：那这样的话不是要在返回后进行强转吗？
    1. 但是，无法强制转换成集合实现类型（如HashSet、ArrayList、HashMap等具体实现类）.
    2. 那是因为返回对象的实际运行时类型其实是Collections的私有内部类Collections.SynchronizedXxx
  - 因此强制类型转换会发生ClassCastException错误，比如：

```Java
HashSet<Integer> hs = new HashSet<>();
hs = (HashSet<Integer>)Collections.synchronizedSet(hs);  // 抛出类型转换异常
// 因为HashSet和Collections.SynchronizedSet之间不兼容.
```

<br>

- **如何解决多线程集合和想要的实现类之间的类型兼容问题呢？**
  - 答案是：**不用解决**，**不用理会**.
  - 因为Java集合框架体系中的所有实现类基本没有多抽象接口中定义的方法有任何的额外扩展！！
    - 也就是说像HashSet、ArrayList等实现类根本没有对其抽象接口Set、List等添加任何额外方法.
    - 唯一额外的东西就是构造器了！
      - 但是构造器都是在转换成多线程集合之前就已经调用了，根本不影响变成多线程之后的使用！
      - 所以，淡定淡定，就正常实用就行了.
