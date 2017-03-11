# SortedMap & TreeMap
> SortedMap和TreeMap类比于SortedSet和TreeSet：
>
>> 1. SortedMap是接口，TreeMap是其最常用的实现.
>> 2. 都是维护元素大小顺序的Set型结构，**SortedMap系列的大小顺序是根据key来维护的**.
>> 3 同样，由于需要compare比较，因此**SortedMap系列的key不能为null**.

<br><br>

## 目录

1. [TreeMap简介](#一treemap简介)
2. [TreeMap的特有方法](#二treemap的特有方法)

<br><br>

### 一、TreeMap简介：[·](#目录)
> 是SortedMap最常用的实现类，可以和TreeSet做类比.

<br>

1. 同样是用红黑树实现的.
2. entry的大小顺序是根据key的compare来维护的.
  1. 自然排序：key实现Comparable接口的compareTo方法.
  2. 定制排序：TreeMap构造器指定comparator.
    - **TreeMap(Comparator\<? super K\> comparator);**
  3. 注意要保证equals和compare = 0的结果保持一致.
3. 由于null无法进行compare比较，因此只插一个null时没事儿，但如果再插一个就会抛出异常，因此**不要存key=null的entry**.
4. **比较大小、比较相等（查找的时候）使用的都是compare！**
  - 不是equals！

<br><br>

### 二、TreeMap的特有方法：[·](#目录)
> 基本和TreeSet的形式一样的，只不过方法名的后缀变成了**Key、Entry、Map**.

<br>

**1.&nbsp; 寻找首尾：**

```Java
// 1. 2. 返回首尾的key （异常不安全，比较古老）
  // 如果map为空则抛出NoSuchElementException异常
K firstKey();
K lastKey();

// 3. 4. 返回首尾entry（异常安全）
  // 如果map为空，不抛出异常，只是返回null而已
Map.Entry<K, V> firstEntry()
Map.Entry<K, V> lastEntry();
```

<br>

**2.&nbsp; 返回大于/小于指定元素的第一个元素：**  都是异常安全的！

- lowerEntry、higherEntry比较的也是key.
  - 没有任何value参会比较.
- 都是异常安全的，不存在或者为空之类的都是返回null.

```Java
// 1. 2. 返回的是key
K lowerKey(K key);
K higherKey(K key);

// 3. 4. 返回的是entry
Map.Entry<K, V> lowerEntry(K key);
Map.Entry<K, V> higherEntry(K key);
```

<br>

**3.&nbsp; 获取区间子集：** 同样都是异常安全的

1. 参数异常会抛出**[IllegalArgumentException]异常**：
  - 比如：为负数、右大于左等.
2. 如果不存在（或者原map本身就是空的），那么返回的是一个空的Map而不是null.

```Java
SortedMap<K,V> headMap(K toKey);
SortedMap<K,V> tailMap(K fromKey);
SortedMap<K,V> subMap(K fromKey, K toKey);
```
