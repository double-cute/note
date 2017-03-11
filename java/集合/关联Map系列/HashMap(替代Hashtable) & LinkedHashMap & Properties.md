# HashMap(替代Hashtable) & LinkedHashMap & Properties
> 1. HashMap是代替HashTable的新规范，Map的最常用实现类.
> 2. LinkedHashMap维护插入顺序.
> 3. Properties是Hashtable特殊的实现类，用于读写和存放程序配置属性.
>   - Hashtable唯一存在的理由就是给Properties陪跑.

<br><br>

## 目录

1. [HashMap]()
2. [LinkedHashMap]()
3. [Properties]()

<br><br>

### 一、HashMap：[·](#目录)
> Map最最常用的的实现类.
>
>> 还有一个兄弟实现类Hashtable：
>>
>> - Hashtable和Stack一样都是在Java 1.0时就存在的古老实现版本，规范性不好且不好用、问题较大.
>> - 现在已经被新标准HashMap所代替.

<br>

- 显然和HashSet一样也是利用hash保证key不重复的：
  1. 只不过entry的**桶和槽由key的hashCode和equals决定**罢了.
  2. 同样也是无序的.
  3. **允许key和value为null.**
  4. 没有对Map进行任何扩展，方法和Map一模一样.

<br><br>

### 二、LinkedHashMap：[·](#目录)
> 就像LinkedHashSet之于HashSet，LinkedHashMap用一张额外链表维护Entry的插入顺序，并且迭代的时候更加高效.

- 和HashMap一样，没有对Map做任何额外的扩展，完全当做Map使用，只不过迭代顺序和插入顺序一样罢了.

<br><br>

### 三、Properties：[·](#目录)
> 一种比较特殊的**Hashtable的子类**，专门用于**读写和存放程序的配置信息**.
>
>> 由于是Hashtable的子类，因此是线程安全的，里面的很多方法有synchronized同步监控.

<br>

- **构造器：**

```Java
// 1. 空
Properties();

// 2. 用另一个Properties对象构造
Properties(Properties defaults);
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

**2.&nbsp; Properties的良好使用习惯：**

- 虽然它是一种特殊的Hashtable\<String, String\>，但应该也只拿它来处理程序配置属性以及属性文件，不要用来做其他用途（比如当成其它的Map\<String, String\>）：
  1. 首先**它是Hashtable的子类，Hashtable不规范，实现有缺陷**，不能滥用.
  2. 设计的初衷就是处理属性和属性文件，因此**只能在这个用途中能保证Hashtable的实现是安全可靠的**，因此不要滥用.

<br>

**3.&nbsp; 获取属性：**

```Java
// 1. 不存在时返回null
String getProperty(String key);

// 2. 不存爱时返回defaultValue
String getProperty(String key, String defaultValue);
```

<br>

**4.&nbsp; 设值：**

```Java
// 不存在插入，存在就覆盖
synchronized Object setProperty(String key, String value);
```

<br>

**5.&nbsp; 从属性文件中加载属性：**

- 这个加载是合并不是覆盖：
  1. 并不是把原来的删掉然后再加载.
  2. 而是和原有的数据合并（没有的添加，有的覆盖）.

```Java
synchronized void load(InputStream inStream | Reader reader);
```

<br>

**6.&nbsp; 将属性写入属性文件：**

- 这个写入**是覆盖不是追加**（会删除掉原来的同名文件的）.

```Java
// comments是注释信息，在属性文件中以#开头
void store(OutputStream out | Writer writer, String comments);
```
