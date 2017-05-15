# 数组工具类：Arrays
> 专门用来操作数组的工具类（静态算法库），就像Collections工具类专门用来操作集一样.
>
>> 算法包括两大类，一类用于处理单线程，另一类用于处理多线程.
>>
>>> **注意 ：** 所有的[from, **to)**区间都是**左闭右开**.

<br><br>

## 目录：

1. [转成str](#一转成str)
2. [equals](#二equals)
3. [二分查找](#三二分查找)
4. [排序](#四排序)
5. [填充](#五填充)
6. [拷贝](#六拷贝)

<br><br>

### 一、转成str：[·](#目录)
> 格式是"[ele1, ele2, ...]"

<br>

- type支持所有类型.

```Java
static String toString(type[] a);
```

<br><br>

### 二、equals：[·](#目录)
> type支持所有类型：boolean、byte、short、char、int、long、float、double、Object

<br>

```Java
// 防空，每个元素对应equals相等
static boolean equals(type[] a1, type a2[]);
```

<br><br>

### 三、二分查找：[·](#目录)
> 返回key的索引（不存在 **返回<0**，而非一定是-1）.
>
>> **前提是数组已经升序排列**.

<br>

- type支持：byte、short、char、int、long、float、double、Object. （**没有boolean**）

```Java
static int binarySearch(type[] a[, int fromIndex, int toIndex] ,type key);
static <T> int binarySearch(T[] a[, int fromIndex, int toIndex] ,T key);
```

<br><br>

### 四、排序：[·](#目录)
> type支持：byte、short、char、int、long、float、double、Object.  （**没有boolean**）
>
>> - 非模板类型数组排序 **全部为升序**.
>> - **模板类型必须自己定制.**

<br>

```Java
// 1. 非模板类型为升序排列，全排或者[from, to)排
static void sort|parallelSort(type[] a[, int from, int to]);

// 2. 模板类型必须定制
static void sort|parallelSort(T[] a[, int from, int to], Comparator<? super T> cmp);
static void parallelSort(T[] a, int from, int to); // 并行排序有个不用定制的版本
```

<br><br>

### 五、填充：[·](#目录)
> 用val填充a.
>
>> - type支持：boolean、char、short、int、long、float、double、Object.  （**全部**）
>> - 不含模板类型T.

<br>

```Java
// 1. 全填充或者只填充区间[from, to)
static void fill(type[] a[, int from, int to], type val);

// 2. 全填充，利用索引计算出一个值进行填充，只支持int、long、double
static void setAll|parallelSetAll(int[] array, IntToTypeFunction generator);
static <T> void setAll|parallelSetAll(T[] array, IntFunction<? extends T> generator);

// 3. 全填充或区间填充，累计填充，只支持int、long、double，只有并行的版本
static void	parallelPrefix(type[] array[, int fromIndex, int toIndex], TypeBinaryOperator op);
static <T> void	parallelPrefix(T[] array[, int fromIndex, int toIndex], BinaryOperator<T> op);
// 等价于：
for (int i = from; i < to; ++i) {
    a[i+1] = a[i] op a[i];
}
```

<br><br>

### 六、拷贝：[·](#目录)
> type支持：boolean、byte、short、char、int、long、float、double. （**没有Object**）
>
>> - 拷贝出一个新数组：不够original.length长就截断，太长补二进制0.

<br>

```Java
// 1. 拷贝[0, len)
static type[] copyOf(type[] original, int len);
static T[] copyOf(T[] original, int len);

// 2. 拷贝[from, to)
static type[] copyOf(type[] original, int from, int to);
static T[] copyOf(T[] original, int from, int to);
```

- 如果需要 **大规模拷贝内存空间**，则系统调用更加高效：

```Java
// src -> dest，拷贝length个元素，超出部分赋为二进制0
static native void System.arraycopy(
    Object src, int srcPos,
    Object dest, int destPos,
    int length
);
```
