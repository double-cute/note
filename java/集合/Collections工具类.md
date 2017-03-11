# Collections工具类
> 操作集合的工具类（静态方法集），可以操作Colletion系和Map系的所有实现类对象.
>
>> 主要功能包括：
>>
>> 1. 对元素进行排序、查找、修改.
>> 2. 设置集合的属性（只读、可修改等）.
>> 3. 同步控制（将单线程集合转化成多线程版本）.

<br><br>

## 目录

1. []()
2. []()

<br><br>

### 一、List排序：
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

### 二、List查找：

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

### 三、List批量填充和替换：

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

### 四、Collection查询信息：
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

### 五、
5. 设置只读集合：以下方法产生的集合都是不可变的只读集合（任何修改集合的行为都是导致运行时异常
    1) 产生一个空的不可变集合：static final <T> Xxx<T> emptyXxx();  // Xxx支持Set、List、Map、SortedSet、SortedMap
！！泛型类型根据返回值的接受引用推断，例如：Set<String> set = Collections.emptySet();  // 泛型参数由Set<String>推断出来
    2) 产生一个只包含一个元素的不可变集合：
        i. static <T> Set<T> singleton(T o);  // 产生只包含一个元素o的Set不可变集合
        ii. static <K,V> Map<K,V> singletonMap(K key, V value);  // 产生只包含一对key-value的Map不可变集合
        iii. static <T> List<T> singletonList(T o);
！！注意！只有Set不符合singletonXxx的命名规范，需要记忆一下！
    3) 返回指定集合的不可变版本（只读版本）：static <T> Xxx<T> unmodifiableXxx(Xxx<? extends T> c); // Xxx支持Collection、Set、List、Map、SortedSet、SortedMap

4. 同步控制：
    1) Java集合框架的所有集合Collection、Map全部都是线程不安全的，因为线程不安全的效率更高，应用更广泛，然而并没有为它们提供相应的线程安全的版本；
    2) 只有老版本的几个集合Vector、Stack等是线程安全的（默认就是线程安全，连线程不安全的版本都没有），但是实现较差，已经没人用了；
    3) 还好Collections工具类提供了synchronizedXxx(Xxx<T> c)方法可以将一个线程不安全的集合包装成线程安全的集合并返回：Xxx支持Collection、Set、List、Map
         i. static <T> Collection<T> synchronizedCollection(Collection<T> c);
         ii. static <T> Set<T> synchronizedSet(Set<T> s);
         iii. static <T> List<T> synchronizedList(List<T> list);
         iv. static <K,V> Map<K,V> synchronizedMap(Map<K,V> m);
！！可以看到包装后的返回类型并不是什么新类型（没有synchronized之类的前缀），还是原有的类型，只不过底层多支持了线程安全的功能，用法和线程不安全的普通版本一模一样！！非常方便，就像变魔术一样！
！！注意：返回类型都是Collection、Set、List、Map这样的上层接口，如果需要使用具体的实现类则需要强制类型转换一下；
    3) 示例：ArrayList<String> list = (ArrayList<String>)Collections.synchronizedCollection(new ArrayList<String>());  // 返回值一定要(ArrayList<String>)转换一下
！！虽然List是ArrayList的父类，但List<T>并不是ArrayList<T>的父类！这个问题会在泛型中具体讲到！！
