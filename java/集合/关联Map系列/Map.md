# Map
> Map和Collection处于同一个集成层次上，是有关联关系集合的根接口，通过**key来找value**.
>
>> - key是键值对唯一性的标识，因此key不能重复.
>> - **Set底层是用Map实现的，即value为null的Map：**
>>   1. 因此Set就相当于只保存key（value等于null）的Map.
>>   2. Set的所有实现Map也有相对应的实现：HashMap、LinkedHashMap、SortedMap、TreeMap、EnumMap.
>>     - 可以看到**完全和Set的实现一一对应.**
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

### 一、Map接口的通用方法：都是Map的对象方法
    1) 和Collection一样，Map也提供了很多基础的字典操作通用方法；
    2) 插入元素（Map的元素就是键值对）：
         i. V put(K key, V value);  // 插入一对键值，如果之前已经存在该键了，那么就覆盖，并返回原来键值对的值！如果之前不存在那就是新添加了，返回的是null（之前的值不存在所以返回null）
         ii. void putAll(Map m);  // 将另一个字典中的内容拷贝到本字典中
    3) 删除/清空：
         i. V remove(Object key);  // 删掉指定key对应的Entry，返回删除之前的value（失败返回null）
         ii. default boolean remove(Object key, Object value);  // 精确删除key-value对（key和value必须都能匹配上），成功返回true
         iii. void clear();  // 清空集合，这个方法比较通用
    4) 常用方法：
         i. int size(); // 存放了多少个键值对
         ii. boolean isEmpty(); // 判空
         iii. V get(K key);  // 返回指定键所对应的值
    5) 获取键/值的集合：
         i. Set<K> keySet();  // 返回键组成的Set集合
         ii. Collection<V> values(); // 返回无结构的value集合，有多少key就有多少value，因此该集合中的value肯定是可以重复的
！！这两个方法返回的集合和Map中的key和value是一一映射的（返回的是Map中的key和value的镜像），在任意一方对内容进行改动都会直接更新到另一方！！使用时要小心
         iii. Set<Map.Entry<K, V>> entrySet();  // 直接返回键值对的包装对象Entry所组成的集合，该Entry的类型是Map.Entry，是Map中定义的内部类
！！Map.Entry的用法：都是Map.Entry的对象方法
              a. K getKey(); // 得到该条目的key
              b. V getValue(); // 得到该条目的value
              c. V setValue(V value); // 重设该条目的value
！同样，返回的Entry的Set也是原Map中内容的镜像，通过该返回的Set修改会影响到原Map（同样原Map也能反过来影响该镜像），例如：
public class Test {

	public static void main(String[] args) {

		Map mp = new HashMap();
		mp.put(1, "haha");
		mp.put(2, "haha");
		mp.put(3, "haha");
		Iterator it = mp.entrySet().iterator(); // 得到镜像的迭代器
		while (it.hasNext()) {
			((Map.Entry)it.next()).setValue("papa"); // 用镜像修改
		}

		System.out.println(mp.values()); // 查看原像，结果全是"papa"

	}
}
          iv. 通常可以通过keySet（结合get方法）用forEach遍历Map：
for (Key key: map.keySet()) {
	System.out.println(key + " --> " + map.get(key));
}
    6) 查看是否包含某个key或者value：
         i. boolean containsKey(K key); // 是否包含某个key
         ii. boolean containsValue(Object value);  // 是否包含某个value
    7) Map重写了toString方法，以[key=value, key=value...]的形式返回字符串，因此key和value必须要自己实现toString才行；

3. Java 8为Map新增的若干默认方法：
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
