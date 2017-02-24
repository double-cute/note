# 数组工具类Arrays
> 专门用来操作数组的工具类（静态算法库），就像Collections工具类专门用来操作集一样.
>
>> 算法包括两大类，一类用于处理单线程，另一类用于处理多线程.
>>
>>> **注意 ：** 所有的[from, **to)**区间都是**左闭右开**.

<br><br>

## 目录：

1. [单线程算法](#一单线程算法)
2. [多线程算法：方法名以parallel作为前缀](#二多线程算法方法名以parallel作为前缀--)

<br><br>

## 所有的算法都是Arrays的public static方法，这里只介绍一些使用频率较高的算法.
## 所有的查找、排序、比较算法都需要数组元素类型实现compareTo方法才行.

<br><br>

### 一、单线程算法：[·](#目录)

<br>

**1. 二分查找：** 返回带查找元素key的索引（不存在返回-1），**前提是数组已经升序排列**.

```Java
int binarySearch(type[] a, type key);
int binarySearch(type[] a, int fromIndex, int toIndex, type key); // 在指定范围中查找
```

<br>

**2. 升序排列：** Java**没提供倒排sort**，可以自己定义type的比较方法或者升序排列后倒序输出

```Java
void sort(type[] a);
void sort(type[] a, int fromIndex, int toIndex);  // 在指定范围升序排列
```

<br>

**3. 相等比较：** 必须长度、元素完全对应相等才返回true

```Java
boolean equals(type[] a, type[] a2);
```

<br>

**4. 拷贝：** 根据提供的数组，拷贝出一个**新的数组（新的内存空间）** 并返回

```Java
type[] copyOf(type[] original, int length); // 拷贝前length个，超出部分赋为二进制0
type[] copyOfRange(type[] original, int from, int to);  // 拷贝指定区间，超出部分赋为二进制0
```

- 如果需要大规模拷贝内存空间，则系统调用更加高效：

```Java
// src -> dest，拷贝length个元素，超出部分赋为二进制0
static native void System.arraycopy(Object src, int srcPos,
                                    Object dest, int destPos,
                                    int length);
```

<br>

**5. 填充：**

```Java
void fill(type[] a, type val);  // 整个数组填充为val
void fill(type[] a, int fromIndex, int toIndex, type val); // 区间填充
```

<br>

**6. 连缀：** 分别调用各个元素的toString，用", "（逗号+空格分隔）连缀成一个完整的字符串并返回

```Java
String toString(type[] a);
```

<br><br>

### 二、多线程算法：方法名以parallel作为前缀  [·](#目录)

<br>

**1. 排序：**

```Java
void parallelSort(type[] a);
void parallelSort(type[] a, int fromIndex, int toIndex);
```

<br>

**2. 相邻迭代更新：** 即从头到尾逐个按照left op right的方式更新

```Java
void parallelPrefix(type[] a, int from, int to, typeBinaryOperator op) {
    if from == 0: a[from] = a[from] * a[from]; from += 1;
    for i in [from, to): a[i] = a[i] op a[i - 1];
}

// 重载版本
void parallelPrefix(type[] a, typeBinaryOperator op);  // [0, length)全区间

// 其中typeBinaryOperator是个函数式接口，需要自己定义二元操作
public interface TypeBinaryOperator {
    type applyAsInt(type left, type right);
}


// 示例：[1, 2, 3, 4, 5]连乘迭代，结果为[1, 2, 6, 24, 120]
Arrays.parallelPrefix(arr, 1, 5, (left, right) -> left * right);
```

<br>

**3. 利用索引生成元素：**

```Java
void parallelSetAll(type[] a, TypeUnaryOperator op) {  // 利用每个元素的索引生成一个值
    for (int i = 0; i < a.length; i += 1) {
        a[i] = op a[i];
    }
}

// UnaryOperator是函数式接口，需要自己定义的一元操作
public interface TypeUnaryOperator {
    type applyAsInt(type operand); // 利用索引生成一个值，如何生成自己定义
}

// 示例
int[] a = new int[5];
Arrays.parallelSetAll(a, index -> index * 5); // 结果是[0, 5, 10, 15, 20]
```
