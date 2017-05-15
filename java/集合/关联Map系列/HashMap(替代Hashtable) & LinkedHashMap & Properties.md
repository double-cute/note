# HashMap(替代Hashtable) & LinkedHashMap & Properties
> 1. HashMap是代替HashTable的新规范，Map的最常用实现类.
> 2. LinkedHashMap维护插入顺序.
> 3. Properties是Hashtable特殊的实现类，用于读写和存放程序配置属性.
>    - **Hashtable唯一存在的理由就是给Properties陪跑.**

<br><br>

## 目录

1. [HashMap & LinkedHashMap](#一hashmap)
2. [Properties](#三properties)

<br><br>

### 一、HashMap & LinkedHashMap：[·](#目录)
> Map最最常用的的实现类.
>
>> 还有一个兄弟实现类Hashtable：
>>
>> - Hashtable和Stack一样都是在Java 1.0时就存在的古老实现版本，规范性不好且不好用、问题较大.
>> - 现在已经被新标准HashMap所代替.
>
> 就像LinkedHashSet之于HashSet，LinkedHashMap用一张额外链表维护Entry的插入顺序，并且迭代的时候更加高效.
>
> - 和HashMap一样，没有对Map做任何额外的扩展，完全当做Map使用，只不过迭代顺序和插入顺序一样罢了.

<br><br>

<br>

- 显然和HashSet一样也是利用hash保证key不重复的：
   1. 只不过entry的 **桶和槽由key的hashCode和equals决定** 罢了.
   2. 同样也是无序的.
   3. **允许key和value为null.**
   4. 没有对Map进行任何扩展，方法和Map一模一样.

<br>

- HashMap中的术语：size、capacity、load-factor、threshold
   1. size：map中，**当前** 实际存放的key-value对数.
   2. capacity：map**当前**最大物理容量，单位是**桶**，即最大能容纳的桶的数量.
   3. load-factor：装载因子，等于size/capacity，但它是一个阈值，用来定义threshold. （默认是0.75f，即75%）
   4. threshold：resize阈值，等于load-facotor × capacity，当size > threshold时，map的capacity需要自动扩容.

<br>

- 构造器：

```Java
// 1. 空，只有定义initialCapacity的同时才能选择性定义loadFactor
   // 默认下：初始容量是16个桶，装载因子是75%
[Linked]HashMap([int initialCapacity[, float loadFacotr]]);

// 2. 非空
[Linked]HashMap(Map<? extends K, ? extends V> m);
```

<br><br>

### 二、Properties：[·](#目录)
> 一种比较特殊的 **Hashtable的子类**，专门用于 **读写和存放程序的配置信息**.
>
>> 由于是Hashtable的子类，因此是线程安全的，里面的很多方法有synchronized同步监控.

<br>

- **构造器：**

```Java
// 空的或者用另一个Properties构造
Properties([Properties defaults]);
```

<br>

**1.&nbsp; 属性文件：**

- 例如Windows中的ini文件，一行一个"key=value".
   - 特点：
      1. =两边不要有多余的空格.
      2. 属性文件中的key和value都是纯字符串.
         - 这就意味着Properties的key-value也只能存放字符串（String）.
         - **Properties其实是一种特殊的Hashtable\<String, String\>.**

<br>

**2.&nbsp; Properties的良好使用习惯：取属性名**

- 虽然它是一种特殊的Hashtable\<String, String\>，但应该也只拿它来处理程序配置属性以及属性文件，不要用来做其他用途（比如当成其它的Map\<String, String\>）：
   1. 首先**它是Hashtable的子类，Hashtable不规范，实现有缺陷**，不能滥用.
   2. 设计的初衷就是处理属性和属性文件，因此**只能在这个用途中能保证Hashtable的实现是安全可靠的**，还是不要滥用为好.
- 举例：获取属性名

```Java
// 1. 获取全部的key
Enumeration<?>	propertyNames();

// 2. 获取全部的类型为String的key
Set<String>	stringPropertyNames();
```

- 由于Properties要求所有的属性和值必须都是String类型的.
   - 并且，其提供的设值方法setProperity也只能设值key-value都为String.
   - 但是！别忘了，Properties也是Hashtable，也能用put方法将任意类型的key-value插入.
      - 例如：props.put(new Object(), "123"); props.put("123", new Object());
         1. propertyNames()把全部key都返回.
         2. stringPropertyNames只取类型为String的key.
   - 所以，不要乱用Properites！
      - 正常用途下（只存放String类型的key-value）propertyNames和stringPropertyNames相同.
      - 因此，规范是：
         1. Properties只用于配置文件处理.
         2. 只用stringPropertyNames. （并且，**Enumeration是旧标准，实现不合理**）
   - 所以，Properties继承Hashtable实在是很愚蠢.

<br>

**3.&nbsp; 获取属性&设值：**

```Java
// 1. 强行取值（key没有关联就返回null或者默认值）
String getProperty(String key[, String defaultValue]);

// 2. 强插强设，返回就value
Object setProperty(String key, String value);
```

<br>

**4.&nbsp; IO：**

- 读取配置到Properties：
   - **是合并不是覆盖（没有的添加，有的覆盖）.**

```Java
// 1. 从输入流中读取
void load(InputStream|Reader in);
// 2. 从XML文档中读取
void loadFromXML(InputStream in);
```

- 将Properties的内容输出到其它地方：**必须要加注释信息**
   - **是覆盖不是合并（会删除掉原来的同名文件的）.**

```Java
// 1. 写到输出流（store和list效果相同）
void store|list(OutputStream|Writer out, String comments);
// 2. 写入XML文档
void storeToXML(OutputStream os, String comment[, String encoding]);
```
