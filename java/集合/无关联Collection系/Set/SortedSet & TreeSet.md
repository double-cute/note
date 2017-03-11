# SortedSet & TreeSet
> SortedSet是有序集合的根接口（抽象接口）.
>
>   - 这里的有序维护的是**元素的大小顺序**.
>
>> TreeSet是SortedSet最常用的实现类，由于用红黑树实现而得名TreeSet.
>>
>>> 所有SortedSet（包括TreeSet）**都不允许存放null**，只要add一个null就**抛出NullPointerException**，因为：
>>>
>>>   - null无法compare，SortedSet仅仅由compare决定位置.

<br><br>

## 目录

1. [SortedSet规定的有序集合的实现标准：时刻维护大小顺序](#一sortedset规定的有序集合的实现标准时刻维护大小顺序--)
2. [TreeSet常用方法：假设类型参数为E](#二treeset常用方法假设类型参数为e--)
3. [如何设定排序规则？](#三如何设定排序规则-)
4. [SortedSet系列（囊括了TreeSet）判断元素是否重复的标准：compare = 0](#四sortedset系列囊括了treeset判断元素是否重复的标准compare--0--)

<br><br>

### 一、SortedSet规定的有序集合的实现标准：时刻维护大小顺序  [·](#目录)
> 顾名思义就是有序的Set，但是它的有序和LinkedHashSet不一样，LinkedHashSet维护的是插入时的顺序，而SortedSet维护的是**元素之间大小顺序**（升序/降序）.

<br>

**实现标准：**

- 大小顺序**时刻维护**.
  1. 插入元素时会安排在合适的位置.
  2. 删除元素之后也会即时调整剩余元素的位置.

<br>

- SortedSet的实现类不多，基本只会用TreeSet这个实现类，之所以叫TreeSet，是因为它使用红黑树实现的.
  - 红黑树是SortedSet最通用、最高效的实现，还有其它很多编程语言使用红黑树作为Set的通用实现（如STL）.

<br><br>

### 二、TreeSet常用方法：假设类型参数为E  [·](#目录)

<br>

**1.&nbsp; 返回首尾元素：**

- 不存在直接抛出异常 **[NoSuchElementException]**.
  - 如果**集合是空的情况下**调用就会抛出该异常.

```Java
// 1. 返回首
E first();
// 2. 返回尾
E last();
```

<br>

**2.&nbsp; 返回小于/大于指定元素的第一个元素：**

- **不存在直接返回null**，不会抛出异常.

```Java
// 1. 返回小于e的第一个元素
E lower(E e);
// 2. 返回大于e的第一个元素
E higher(E e);
```

- 注意：
  1. 参数可以不属于集合，只要能和集合中的元素比较大小即可.
    - 例如：[1, 2, 4, 5]，那么lower(3) = 2
  2. 如果是升序排列那么lower/higher表示小于/大于，如果是**降序排列则lower/higher表示大于/小于**.

<br>

**3.&nbsp; 获取区间：**

- 参数所代表的端点不需要属于集合，只要和集合中的元素可比就行.

```Java
// 1. 两端区间：[fromElement, toElement)
SortedSet<E> subSet(E fromElement, E toElement);

// 2. 头区间：[ , toElement)
SortedSet<E> headSet(E toElement);

// 3. 尾区间：[fromElement, )
SortedSet<E> tailSet(E fromElement);
```

<br><br>

### 三、如何设定排序规则？ [·](#目录)
> 共有两种方式：
>
> 1. 自然排序：元素自己实现Comparable接口的compareTo方法.
>   - 自然排序的意义是指元素所在**值域中的自然大小**，比如自然数中2就是大于1.
> 2. 定制排序：TreeSet(Comparator)构造器中指定一个自己的Comparator接口实现类.
>   - 定制排序的意义表示**业务逻辑需求的排序，可以违背自然排序的原则**，比如业务要求从大大小排序，那么Comparator逻辑中1就是大于2的.
>
>> **两者中必须至少实现一种**，如果两个都不实现，则在插入第一个元素时安然无恙，但在插入第二个元素时由于需要和第一个元素比较大小（但木有实现大小比较的规则）而抛出异常！

<br>

**1.&nbsp; 自然排序：**

```Java
public interface Comparable<T> { // 集合中的元素必须自己实现此接口
    public int compareTo(T o);
}
```

- 很多Java类库中的类如**String、Date、BigDecimal、Integer等包装器类**都已经实现了Comparable的自然排序.
  - 特别的**Boolean也实现了大小比较**，规定true大于false.

<br>

**2.&nbsp; 定制排序：**

```Java
// TreeSet有一个指定Comparator参数的构造器
TreeSet(Comparator comparator);

// 其中
public interface Comparator<T> { // 是一个函数式接口，可以用lambda表达式
    int compare(T o1, T o2);
}

// 示例
TreeSet<Integer> ts = new TreeSet<>((i1, i2) -> i2 - i1); // 定制了一个降序排列的Integer-TreeSet
```

<br>

**3.&nbsp; TreeSet排序规则的设定是如何实现的？**

- TreeSet中维护着一个comparator成员.
  1. 自然排序时默认将元素的compareTo赋给它.
  2. 定制排序时将参数comparator赋给它.

<br>

**4.&nbsp; 升序降序如何控制？**

1. 升序就是return v1 - v2; （**前减后**）
2. 降序就是return v2 - v1; （**后减前**）


<br><br>

### 四、SortedSet系列（囊括了TreeSet）判断元素是否重复的标准：compare = 0  [·](#目录)
> 由于SortedSet也是Set的一种，因此也不允许元素重复！

- 判断标准**不是equals而是compare**！
  - 自然排序下是compareTo，定制排序下是comparator.
  - **相等条件是compare是否返回0.**

<br>

**注意：**

- 虽然不是由equals决定的，但为了**避免元素位置信息的错位**，一定要**让equals和comapre = 0两者保持一致**！

<br><br>

### 五、compare的实现原则：



    4) 示例：
class R implements Comparable {
	int val;

	public R(int val) {
		this.val = val;
	}

	@Override
	public String toString() {
		return "R[val:" + val + "]";
	}

	@Override
	public boolean equals(Object obj) { // equals的标准写法
		if (this == obj) { // 先比较地址
			return true;
		}

		if (obj != null && obj.getClass() == R.class) { // 再比较类型（前提是obj不能为空）
			R r = (R)obj; // 先把类型调整一致（当然可以直接return this.val == ((R)obj).val
						  // 但如果在return之前还要用obj进行一些其它复杂操作那用临时的类型转换太麻烦了
			              // 因此先协调类型才是最标准最合理的做法
			return this.val == r.val;
		}

		return false; // 地址不同 || obj为空 || 类型不一致，那肯定不一样了！
	}

	@Override
	public int compareTo(Object o) { // compareTo的标准写法
		if (this == o) { // 第一步和equals一样，还是先比较地址，这是最基本也是最容易想到的
			return 0;
		}

		if (o == null) { // 由于compareTo比较的是大小，而null是没法比较大小的，因此null就得抛异常
			             // 这里只是个演示，就不实现这一块了
			// 抛出异常
		}

		R r = (R)o; // 不判断类型是否一致，不一致直接抛出异常！就是要暴露异常方便调试（集合中元素都应该保证类型相同）

		return this.val - r.val; // 正常的操作和返回
	}
}

public class Test {

	public static void main(String[] args) {
		TreeMap map = new TreeMap();

		map.put(new R(9), "aaa");
		map.put(new R(5), "bbb");
		map.put(new R(-3), "ccc");
		System.out.println(map); // key: -3, 5, 9

		System.out.println(map.firstEntry()); // key: -3
		System.out.println(map.lastKey()); // key: 9
		System.out.println(map.higherKey(new R(2))); // key: 5
		System.out.println(map.lowerEntry(new R(1))); // key: -3
		System.out.println(map.subMap(new R(-1), new R(10))); // key: 5, 9
	}
}
