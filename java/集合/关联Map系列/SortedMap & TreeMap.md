# SortedMap & TreeMap
> SortedMap和TreeMap类比于SortedSet和TreeSet：
>
>> 1. SortedMap是接口，TreeMap是其最常用的实现.
>> 2. 都是维护元素大小顺序的Set型结构，**SortedMap系列的大小顺序是根据key来维护的**.
>> 3 同样，由于需要compare比较，因此 **SortedMap系列的key不能为null**.

<br><br>

## 目录

1. [SortedMap](#一sortedmap)
2. [TreeMap](#二treemap)

<br><br>

### 一、SortedMap：[·](#目录)
> 完全和SortedSet相同.

<br>

**1.&nbsp; 获取比较体：** 由于顺序是根据key维护的，实际上返回的是key的比较体

```Java
Comparator<? super K> comparator();
```

<br>

**2.&nbsp; 偷看（异常不安全）：**

```Java
K firstKey();
K lastKey();
```

<br>

**3.&nbsp; 取子集：**

```Java
SortedMap<K,V> subMap(K fromKey, K toKey);
SortedMap<K,V> headMap(K toKey);
SortedMap<K,V> tailMap(K fromKey);
```

<br><br>

### 二、TreeMap：[·](#目录)
> 是SortedMap最常用的实现类，可以和TreeSet做类比.

<br>

1. 同样是用红黑树实现的.
2. entry的大小顺序是根据key的compare来维护的.
   1. 自然排序：key实现Comparable接口的compareTo方法.
   2. 定制排序：TreeMap构造器指定comparator.
      - **TreeMap(Comparator\<? super K\> comparator);**
   3. 注意要保证equals和compare = 0的结果保持一致.
3. 由于null无法进行compare比较，因此只插一个null时没事儿，但如果再插一个就会抛出异常，因此 **不要存key=null的entry**.
4. **比较大小、比较相等（查找的时候）使用的都是compare！**
   - 不是equals！

<br>

**1.&nbsp; 构造器：**

```Java
// 1. 空
TreeMap([Comparator<? super K> cmp]);

// 2. 非空
TreeMap(Map<? extends K, ? extends V>|SortedMap<? extends K, ? extends V> m);
```

<br>

**2.&nbsp; 取头尾：** 异常安全

```Java
Map.Entry<K, V> pollFirstEntry();
Map.Entry<K, V> pollLastEntry();
```

<br>

**3.&nbsp; 找前后的元素：**

```Java
// 1. 小
K lowerKey|floorKey(K key);
Map.Entry<K, V> lowerEntry|FloorEntry(K key);

// 2. 大
K higherKey|ceilingKey(K key);
Map.Entry<K, V> higherEntry|ceilingEntry(K key);
```
