# Map
> Map和Collection处于同一个继承层次上，是有关联关系集合的根接口，通过 **key来找value**.
>
>> - key是键值对唯一性的标识，因此 **key不能重复**.
>>
>> <br>
>>
>> - **Set底层是用Map实现的，即value为null的Map：**
>>    1. 因此Set就相当于只保存key（value等于null）的Map.
>>    2. Set的所有实现Map也有相对应的实现：HashMap、LinkedHashMap、SortedMap、TreeMap、EnumMap.
>>       - 可以看到 **完全和Set的实现一一对应.**
>>
>> <br>
>>
>> - 但Map没有线性表的存储方式（List、Queue），即没有ListMap、QueueMap这种存储方式，那是因为：
>>    - Map这种关联结构 **强调的是根据key来找value**.
>>       1. 即相当于List根据索引找value一样，list[1]是根据索引1取出value，而map["hello"]则是根据key("hello")来找value.
>>       2. Map的索引就是key，key可以是任何引用类型，比简单的线性关系更加复杂.
>>         - **根据key的特点来决定key-value对的存储位置.**
>>       3. 因此Map没有线性表存储结构的实现.

<br><br>

## 目录

1. [构造：Collections不可变构造方法](#一构造collections不可变构造方法--)
2. [大小、判空、equals、hashCode](#二大小判空equalshashcode)
3. [插入](#三插入)
4. [删除](#四删除)
5. [替换（覆盖）](#五替换覆盖)
6. [查找](#六查找)
7. [获取key、value、entry集合](#七获取keyvalueentry集合)
8. [遍历](#八遍历)

<br><br>

- 接下来介绍的都是Map根接口中定义的 **对象方法**，所有Map实现类都可以使用这些方法.
   - 可以看到凡是须要在Map中查看参数是否存在的方法，被检查的参数一定是Object类型的.
      - 就是为了多态调用equals方法进行相等比较，像：
         1. remove(Ojbect key...);
         2. contains(Object key \| Object value);
         3. get(Object ...);
      - 但那些主动插入（设值等）方法传入的参数就必须是精确匹配的类型K或V了，例如：
         - put(K key, V value);
         - Java的设计理念默认 **查看是被动的**，**其余操作是主动的**：
            1. 主动操作默认是 **提前知道类型** 的，因此要用精确的类型.
            2. 被动操作默认是 **不知道类型** 的，因此使用Object.

<br>

- 术语解释：

<br>

> 1. 关联：
>    - 即 **key关联**，即 **key关联了一个非null的value**.
>
> 2. 没关联：
>    - 即 **key没关联**，即 **key不在map中，或者key关联了一个null**.
>
> <br>
>
> - 关于返回值：**必定**、**无论如何** 都会返回 **旧value**.
>    - 只要看到方法有返回值（V类型的），那必定会返回一个旧value.
>    - 旧的value是啥就返回啥：
>       1. 旧的value如果是null那就返回null.
>       2. 旧的value不存在（参数中指定的key没有出现在map中）也返回null.
>          - 旧相当于旧值是null.        

<br><br>

### 一、构造：Collections不可变构造方法  [·](#目录)

<br>

```Java
// 1. 空
static <K,V> Map<K,V> emptyMap();
// 2. 空SortedMap
static <K,V> SortedMap<K,V> emptySortedMap();
// 3. 单例
static <K,V> Map<K,V> singletonMap(K key, V value);
```

<br><br>

### 二、大小、判空、equals、hashCode：[·](#目录)
> **覆盖了toString方法**，因此可以方便输出全部key-value对.

<br>

```Java
// 1. 返回当前存放的entry的个数
int size();

// 2. 判空
boolean isEmpty();

// 3. 实现了每对key-value对应相等，可以放心使用
   // Map.equals 依赖 Set<Map.EntrySet>.equals 依赖 Map.EntrySet.equals 依赖 Map.Entry.equals（key-value对应相等）
boolean	equals(Object o);

// 4. hash = sum: entry.hash; entry.hash = key.hash ^ value.hash
int	hashCode();
```

<br><br>

### 三、插入：[·](#目录)

<br>

**1.&nbsp; 强行插入（不管key关联了没）：**

- 返回旧value（之前没关联就相当于旧value是null）.

```Java
V put(K key, V value);
```

<br>

**2.&nbsp; key没关联就插，否则不插，全都返回旧value：**

```Java
default V putIfAbsent(K key, V value);
```

<br>

**3.&nbsp; key没关联就插入新值：**

- key没关联就根据key计算出一个新值.
   - 新值不为null就插入.
   - 返回的是计算出来的新值（不管是不是null）.
- key关联了就什么都不做，返回旧value.

```Java
default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction);
interface Function<T, R> { R apply(T t); }

// 示例
m.computeIfAbsent(15, key -> key + 1);  // 如果15没出现在keySet，那么就将15-15+1插入m中
// 如果原来有15-null也会插入（覆盖）15-16
// 即使lambda表达式为key -> null，也行，新的key-null也能被插入
```

<br>

**4.&nbsp; 强行插入另一个map（不管受体里的key关联了没）：**

```Java
void putAll(Map<? extends K,? extends V> m);  // 将另一个字典中的内容拷贝到本字典中
```

<br><br>

### 四、删除：[·](#目录)

<br>

**1.&nbsp; 强删entry（不管key关联没），一定返回旧value：**

```Java
V remove(Object key);
```

<br>

**2.&nbsp; 强删entry（不管key关联没），但oldValue必须匹配（即使是null）：**

```Java
// 既然已经知道旧value了，也就不必返回了
default boolean remove(Object key, Object oldValue);
```

**3.&nbsp; 清空：**

```Java
void clear();
```

<br><br>

### 五、替换（覆盖）：[·](#目录)

<br>

**1.&nbsp; 有key才替换（关联了null也行），一定返回旧value：**

```Java
default V replace(K key, V value);
```

<br>

**2.&nbsp; 有key就替换（关联null也行），但必须精确匹配oldValue，否则什么都不做：**

- 替换成功返回去true.
   - 由于事先知道oldValue，因此不返回oldValue，而返回是否成功.

```Java
default boolean replace(K key, V oldValue, V newValue);
```

<br>

**3.&nbsp; key关联就根据key-value计算新值并插入：** （先看有无关联再计算）

- key如果有关联：
   1. 根据key-value计算新值.
   2. 新值非null就插入.
   3. 新值为null就删除该entry.
      - 最终返回新值（不管是否为null）.
- 如果key没关联：
   - 什么都不做，返回null.

```Java
V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction);

interface BiFunction<T, U, R> { R apply(T t, U u); } // 这里是 (K key, V value) -> V returnValue

// 示例
m.computeIfPresent(111, (key, value) -> key + value); // 将key为111的oldValue替换成key + oldValue
```

<br><br>

**4.&nbsp; 同上：** （先计算，再看有无关联）

- 先计算出newValue.
   1. 如果oldValue非null（有关联），那么和computeIfPresent一样.
      - newValue不为空就插入并返回，为空就删除并返回null.
   2. 如果oldValue为空（无关联）.
      - newValue不为空就插入，否则什么都不做返回null.
- 总结：**强插，newValue为空就删，不为空就插，返回newValue.**

```Java
V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction);

// 示例
HashMap<Integer, Integer> m = ...; // 0-0, 1-null, 2-2

m.compute(2, (key, value) -> key * value);  // 2-4
m.compute(2, (key, value) -> null);  // 删除2-2
m.compute(1, (key, value) -> null);  // 删除1-null
m.compute(1, (key, value) -> key * value); // key、value空指针异常
```

<br>

**5.&nbsp; 新旧value合并重设：**

- 强插：
   1. 先计算newValue = oldValue为空（没关联）就为value，否则根据old和value计算出.
   2. newValue为空就删除entry，否则覆盖.
   3. 最终返回newValue的值（不管是不是null）.

```Java
default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction);
```

<br>

**6.&nbsp; 批量替换：**

1. 根据旧的key-value计算出一个新value替换.
2. 这里要求必须所有key必须关联，如果有一个关联null就抛出 **[NullPointerException]异常**.

```Java
default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function);
// 示例
HashMap<Integer, Integer> m = ...;  // 0, 1, 2
m.replaceAll((key, value) -> key + value * 2); // 0+0*2, 1+1*2, 2+2*2 = 0, 3, 6
```

<br><br>

### 六、查找：[·](#目录)
> 查看获得全部都是Map中内容的引用，可以**通过这些返回的引用直接修改原数据**.
>
> - 不管是key还是value还是entry，都可以修改.

<br>

**1.&nbsp; 是否包含指定key&value：**

```Java
// 1. 出现就返回true
boolean containsKey(Object key);

// 2. 出现就返回true，包括null出现也返回true
boolean containsValue(Object value);
```

<br>

**2.&nbsp; 根据key获取对应的value：** 最为常用

```Java
// 1. 直取，出不出现都取
V get(Object key);

// 2. 非空取：存在就取，不存在（包括浮现）就返回defaultValue
default V getOrDefault(Object key, V defaultValue);
```

<br><br>

### 七、获取key、value、entry集合：[·](#目录)

<br>

**1.&nbsp; key集合：**

```Java
// 获取key组成的集合，和Map中key的顺序保持一致
Set<K> keySet();  // 返回键组成的Set集合
```

<br>

**2.&nbsp; value集合：**

```Java
/** 返回的是一种可重复的Set. （其实是用Map实现的）
 *
 *  - 返回的集合是一种特殊的Map内部类：private Map.values
 *     1. 有多少个key，则返回的集合中就有多少个value
 *     2. 因此里面的values是可以重复的
 *     3. 由于Map.Values类型无法访问，因此只能当做Collection实例使用
 *
 *  - 返回的集合中value的顺序和keySet中key的顺序保持一致的！
 *     - 为了达到这个目的，返回集合的类型也就必须要用Map了
 */
Collection<V> values();
```

<br>

**3.&nbsp; 获取entry集合：**

```Java
/** 直接返回所有entry所组成的集合
 *
 *  - entry的类型是Map的内部接口：interface Map.Entry
 *     1. Map.Entry对外部开放，因此可以随意使用.
 *     2. 但每种Map实现类中的Entry实现类都是对外隐藏的.
 *        - 在外部想操作entry实例，不得不使用Map.Entry引用.
 *
 *  
 *  - 返回的集合其实是一种特殊类型.
 *     - 是Map内部定义的：private class EntrySet;
 *        1. 专门用于存放entry，并且保持和原Map中key相同的顺序.
 *           - 因此底层肯定还是一个Map结构.
 *        2. 但由于是对外隐藏的，因此只能当做普通的Set来使用.
 */
Set<Map.Entry<K, V>> entrySet();
```

<br>

**4.&nbsp; Map.Entry类型：**

- 操作key-value

```Java
// 1. 获取key
K getKey();

// 2. 获取value
V getValue();

// 3. 重置value
  // 返回旧的value
V setValue(V value);
```

- 比较Map.Entry：Comparrtor
   - **Java本身没有让Map.Entry实现Comparable**，这是因为entry的比较是有选择性的.
      1. 既可以根据key来比较entry.
      2. 也可以根据value来比较entry.
         - Java把这个选择权交给开发者.
   - Java只提供了获取比较体的方法，开发者获取后可以用来干好多事.

```Java
// 1. 获取根据key比较entry的比较体
static <K extends Comparable<? super K>,V> Comparator<Map.Entry<K,V>> comparingByKey();  // 取key的自然排序
static <K,V> Comparator<Map.Entry<K,V>>	comparingByKey(Comparator<? super K> cmp);  // 自己定制

// 2. 获取根据value比较entry的比较体
static <K,V extends Comparable<? super V>> Comparator<Map.Entry<K,V>> comparingByValue();  // 自然
static <K,V> Comparator<Map.Entry<K,V>> comparingByValue(Comparator<? super V> cmp);  // 定制
```

- 示例：

```Java
// 按照value从大大小给entrySet排序
HashMap<Integer, Integer> m = new HashMap<>();
for (int i = 0; i < 10; ++i) m.put(i, i);
Comparator<Map.Entry<Integer, Integer>> cmp = Map.Entry.comparingByValue((v1, v2) -> v2 - v1);
SortedSet<Map.Entry<Integer, Integer>> ts = new TreeSet<>(cmp);
ts.addAll(m.entrySet());
```

<br><br>

### 八、遍历：[·](#目录)
> 所有的遍历方法都是 **记忆类型** 的，因此 **无需任何强转**.
>
>> 随意遍历，随意浪.

<br>

```Java
HashMap<K, V> mp = ...;
```

<br>

**1.&nbsp; key-value打包在一起遍历：**

```Java
// 1. Map层级上的简洁遍历，直接Map.forEach（最新Java才支持的）
default void forEach(BiConsumer<? super K, ? super V> action);
// 示例：
mp.forEach((K key, V value) -> ...);
// 但没有对应的forEach和Iterator遍历


// 2. entrySet遍历
mp.entrySet.forEach(Map.Entry<K, V> entry -> ...);
for (Map.Entry<K, V> entry: mp.entrySet()) { ... }
Iterator<Map.Entry<K, V>> it = mp.entrySet().iterator();
```

<br>

**2.&nbsp; key、value分开，通过遍历key来遍历value：**

```Java
// 1.
mp.keySet().forEach(K key -> V value = mp.get(K) ... );

// 2.
for (K key: mp.keySet()) { V value = mp.get(key); ... }

// 3.
Iterator<K> it = mp.keySet().iterator();
```

<br>

**3.&nbsp; 只遍历value：**

```Java
// 1.
mp.values().forEach(V value -> ...);

// 2.
for (V value: mp.values()) { ... }

// 3.
Iterator<V> it = mp.values().iterator();
```
