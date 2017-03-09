# Map
> Map和Collection处于同一个集成层次上，是有关联关系集合的根接口，通过**key来找value**.
>
>> - key是键值对唯一性的标识，因此key不能重复.
>>
>> <br>
>>
>> - **Set底层是用Map实现的，即value为null的Map：**
>>   1. 因此Set就相当于只保存key（value等于null）的Map.
>>   2. Set的所有实现Map也有相对应的实现：HashMap、LinkedHashMap、SortedMap、TreeMap、EnumMap.
>>     - 可以看到**完全和Set的实现一一对应.**
>>
>> <br>
>>
>> - 但Map没有线性表的存储方式（List、Queue），即没有ListMap、QueueMap这种存储方式，那是因为：
>>   - Map这种关联结构**强调的是根据key来找value**.
>>     1. 即相当于List根据索引找value一样，list[1]是根据索引1取出value，而map["hello"]则是根据key("hello")来找value.
>>     2. Map的索引就是key，key可以是任何引用类型，比简单的线性关系更加复杂.
>>       - **根据key的特点来决定key-value对的存储位置.**
>>     3. 因此Map没有线性表存储结构的实现.

<br><br>

## 目录

1. []()
2. []()
3. []()

<br><br>

- 接下来介绍的都是Map根接口中定义的**基础对象方法**，所有Map实现类都可以使用的方法.
  - 可以看到凡是须要在Map中查看参数是否存在的方法，被检查的参数一定是Object类型的.
    - 就是为了多态调用equals方法进行相等比较，像：
      1. remove(Ojbect key...);
      2. contains(Object key \| Object value);
      3. get
    - 但那些主动插入（设值等）方法传入的参数就必须是精确匹配的类型K或V了，例如：
      - put(K key, V value);
      - Java的设计理念默认**查看是被动的**，**其余操作是主动的**：
        1. 主动操作默认是**提前知道类型**的，因此要用精确的类型.
        2. 被动操作默认是**不知道类型**的，因此使用Object.

<br><br>

### 一、大小、判空、插入、删除：
> **覆盖了toString方法**，因此可以方便输出全部key-value对.

<br>

**1.&nbsp; 大小 & 判空：**

```Java
// 1. 返回当前存放的entry的个数
int size();

// 2. 判空
boolean isEmpty();
```

<br>

**2.&nbsp; 插入：**

```Java
/** 1. 插入一对key-value
 *  - 如果：
 *    1. key之前已经存在了，那么覆盖value并返回旧的value
 *    2. 否则添加并返回null
 */
V put(K key, V value);

// 2. 将另一个字典插入this（key重复会覆盖value）
void putAll(Map m);  // 将另一个字典中的内容拷贝到本字典中
```

<br>

**3.&nbsp; 删除 & 清空：**

```Java
// 1. 根据key精确删除对应的一整个entry，返回之前的value
V remove(Object key);

/** 2. 严格匹配key-value删除对应的entry
 *  - 因为key是不重复的，因此只根据key删entry足够了
 *    - 严格匹配key-value才能删的应用场景比较少
 */
default boolean remove(Object key, Object value);

// 3. 清空
void clear();
```

<br><br>

### 二、查看、修改：
> 查看获得全部都是Map中内容的引用，可以**通过这些返回的引用直接修改原数据**.
>
> - 不管是key还是value还是entry，都可以修改.

<br>

**1.&nbsp; 是否包含指定key&value：**

```Java
// 1. 是否包含指定key
boolean containsKey(Object key);

// 2. 是否包含指定value
boolean containsValue(Object value);
```

<br>

**1.&nbsp; 根据key获取对应的value：** 最为常用

```Java
// 根据equals查找key，并返回相应的value
  // 如果没找到则返回null
V get(Object key);
```

<br>

**2.&nbsp; 获取key&value的结合：**

```Java
// 1. 获取key组成的集合
Set<K> keySet();  // 返回键组成的Set集合

/** 2. 返回的是一种可重复的Set.
 *
 *  - 返回的集合是一种特殊的Map内部类：private Map.values
 *    1. 有多少个key，则返回的集合中就有多少个value
 *    2. 因此里面的values是可以重复的
 *    3. 由于Map.Values类型无法访问，因此只能当做Collection实例使用
 *
 *  - 返回的集合中value的顺序和keySet中key的顺序保持一致的！
 *    - 为了达到这个目的，返回集合的类型也就必须要用Map了
 */
Collection<V> values();
```

<br>

**3.&nbsp; 获取entry集合：**

```Java
/** 1. 直接返回所有entry所组成的集合
 *
 *  - entry的类型是Map的内部接口：interface Map.Entry
 *    1. Map.Entry对外部开放，因此可以随意使用.
 *    2. 但每种Map实现类中的Entry实现类都是对外隐藏的.
 *      - 在外部想操作entry实例，不得不使用Map.Entry引用.
 *
 *  
 *  - 返回的集合其实是一种特殊类型.
 *    - 是Map内部定义的：private class EntrySet;
 *      1. 专门用于存放entry，并且保持和原Map中key相同的顺序.
 *        - 因此底层肯定还是一个Map结构.
 *      2. 但由于是对外隐藏的，因此只能当做普通的Set来使用.
 */
Set<Map.Entry<K, V>> entrySet();
```

- 操作Map.Entry：**Map.Entry的对象方法**

```Java
// 1. 获取key
K getKey();

// 2. 获取value
V getValue();

// 3. 重置value
  // 返回旧的value
V setValue(V value);
```

<br><br>

### 三、遍历：
> 所有的遍历方法都是**记忆类型**的，因此**无需任何强转**.
>
>> 随意遍历，随意浪.

<br>

```Java
HashMap<K, V> mp = ...;

/* === 1. key-value打包在一起遍历 === */

// 直接Map.forEach，最新Java才支持的
mp.forEach((K key, V value) -> ...);
// 但没有对应的forEach和Iterator遍历

// entrySet遍历
mp.entrySet.forEach(Map.Entry<K, V> entry -> ...);
for (Map.Entry<K, V> entry: mp.entrySet()) { ... }
Iterator<Map.Entry<K, V>> it = mp.entrySet().iterator();

/* === 2. key、value分开，通过遍历key来遍历value === */
mp.keySet().forEach(K key -> V value = mp.get(K) ... );
for (K key: mp.keySet()) { V value = mp.get(key); ... }
Iterator<K> it = mp.keySet().iterator();

/* === 3. 只遍历value === */
mp.values().forEach(V value -> ...);
for (V value: mp.values()) { ... }
Iterator<V> it = mp.values().iterator();
```

<br><br>

### 、
    1) 除了remove(key, value)之外Java还新增了很多方便Map使用的默认方法；
    2) default V compute(K key, BiFunction remappingFunction);
         i. BiFunction是一个二元运算接口（函数式接口）：
public interface BiFunction<T, U, R> {
    R apply(T t, U u);
}
         ii. 在这里是指根据key所指定的key-value对，用key和value这两个值计算一个新的value；
         iii. 如果新value不为空就覆盖原key-value对，如果新value为空就删除原key-value对，如果新旧value都为空则保持不变；
         iv. 返回的是计算出的新value（可能为空）；
         v. 例如：map.compute(key, (k, v) -> ((K).k).toString + ((V)v).toString());  // 用key和value的字符串连接作为新的value

    3) V computeIfAbsent(K key, Function mappingFunction);
！！ 如果key不存在（两种情况，一种就是真的不存在，另一种是key存在，但是其对应value为空），那么就用后面指定的函数计算出一个新的value覆盖（或者添加）；
    4) default V computeIfPresent(K key, BiFunction remappingFunction);
！！如果key存在（有value，且value不为空）就用函数计算一个新值覆盖原键值对
    5) default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction);
！！如果key不存在（不存在或值为空）就赋成value，如果key存在（其value不为空）则用函数（通过旧value和参数中给出的新value）计算出一个新的value覆盖，如果函数计算出的新value为null则删除该键

！！3) 4)两个方法当计算出来的新value为null时都会删除原键值对！！

！！接下来介绍几个非常常用且实用的默认方法！
      1) 如果一个键不存在（不存在或者对应的value为空）就添加/覆盖一对键值：default V putIfAbsent(K key, V value);
      2) 直接精确删除一对键值（之前讲过）：default boolean remove(K key, V value);
      3) 简洁遍历：default void forEach(BiConsumer<? super K, ? super V> action);  // 例如典型的打印遍历：map.forEach((k, v) -> System.out.println(k + " ---> " + v));
      4) 防无获取值：default V getOrDefault(Object key, V defaultValue);  // 如果不包含key（是不包含，key-null的情况不算，只是不包含）就用defaultValue返回，否则返回正常的value
      5) 重设：相当于set一个key的value为新的value，只不过Map没有提供set方法
           i. default V replace(K key, V value);  // 将key的值重设为新的value，返回老的value
           ii. default boolean replace(K key, V oldValue, V newValue);  // 精确找到key-oldvalue对，然后将oldValue替换成新的newValue，替换成功返回true
！！注意不要和put搞混了，如果key不存在不会添加，只能重设已存在的key所对应的value
           iii. 全部批量重设：default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function);  // 根据函数（通过key-value计算）重设每一个键值对的value
