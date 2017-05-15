# Collection概述 & Iterator和Predicate筛

<br><br>

## 目录

1. [Collection接口：假设其类型参数是E](#一collection接口假设其类型参数是e--)
2. [迭代遍历：Iterable接口](#二只有collection有的iterator)
3. [Predicate筛](#四predicate筛)

<br><br>

- **所有static开头的静态工具方法都是Collections中的！**

### 一、Collection接口：假设其类型参数是E  [·](#目录)
> 所有无关联集合的根接口，**其方法在所有派生类中都存在（也就是在Set、List、Queue以及其实现类中都可以使用）**.

<br>

**1.&nbsp; 大小 & 判空：**

```Java
int size();  // 元素个数
boolean isEmpty();  // 判空
```

<br>

**2.&nbsp; 添加元素：**

- 成功添加返回true

```Java
// 1. 添加一个元素，添加成功返回true
boolean add(E e); // 添加到末尾

// 2. 添加多个元素
boolean addAll(Collection<? extends E> c);  // 添加到末尾
static <T> boolean addAll(Collection<? super T> c, T... elements);
```

<br>

**3.&nbsp; 删除元素：**

- 成功删除返回true.

```Java
// 1. 只删除1个指定元素
boolean remove(Object o);

// 2. 通过谓词筛批量删除
default boolean removeIf(Predicate<? super E> filter);

// 3.清空集合
void clear();
```

<br>

**4.&nbsp; 集合运算：**

- 由于retainAll、removeAll都是删除，因此是?，而addAll是加入，所以是? extends E.
   - 记住！规律是：如果发生 **加入 或者 赋值**，那么 **被动者的类型一定要 ≥ 另一方**.

```Java
// 1. 交：this && c，c的元素编译时类型为Object
boolean retainAll(Collection<?> c);
// 没有交集返回true，即判断是否不想交
static boolean disjoint(Collection<?> c1, Collection<?> c2);

// 2. 并：this || c，c的元素编译时类型为E
boolean	addAll(Collection<? extends E> c);

// 3. 差：this - c，c的元素编译时类型为Object
boolean removeAll(Collection<?> c);  
```

<br>

**5.&nbsp; 包含关系：**

```Java
// 1. 是否包含指定元元素o
boolean contains(Object o);  

// 2. 是否包含集合c中的所有元素，c的元素编译时类型为Object
boolean containsAll(Collection<?> c);
```

<br>

**6.&nbsp; 转换成数组：**

```Java
// 1. 转换成Object数组，缺点是擦除了运行时类型
Object[] toArray();

// 2. 转换成指定类型的数组，类型和参数的类型一样
   // 优点是可以自己主动保留元素原有的运行时类型
   // 搞笑的是，通过参数对象的运行时类型判断，而不是.class属性判断
<T> T[]	toArray(T[] a);

// 示例：
HashSet<String> set = new HashSet<>();
String[] arr = set.toArray(new String[0]);
// toArray(new Object[0]) 等价于 toArray()
```

<br>

**7.&nbsp; 统计：**

```Java
// 1. 统计o的出现次数
static int frequency(Collection<?> c, Object o);

// 2. 找出最大/最小元素，比较可以以来自然排序也可以定制
static <T extends Object & Comparable<? super T>> T	max|min(Collection<? extends T> coll);
static <T> T max|min(Collection<? extends T> coll, Comparator<? super T> comp);
```

<br>

- **示例：**

```Java
public class Test {

	public static void main(String[] args) {
		Collection c = new ArrayList(); // ArrayList是Collection的一个实现类，擦除类型后是<Object>

		c.add("mama");
		c.add(5);  // 虽然不能装基本类型，但是Java支持自动包装，将基本类型包装秤包装器类型
		System.out.println(c.size()); // 2
		c.remove(5);
		System.out.println(c.size()); // 1
		System.out.println(c.contains("mama")); // true
		c.add("fafa");
		System.out.println(c); // [mama, fafa]

		Collection d = new HashSet(); // HashSet也是Collection的一个实现类
		d.add("fafa");
		d.add("coco");
		System.out.println(c.containsAll(d)); // false
		c.removeAll(d); // c - d
		System.out.println(c); // [mama]
		c.clear();
		System.out.println(c); // []
		d.retainAll(c);
		System.out.println(d); // []
	}
}
```

<br><br>

### 二、迭代遍历：Iterable接口  [·](#目录)
> Collection实现了Iterable接口（**Map没有实现**），使Collection得到了迭代器的支持.
>
> - 迭代器的终极目标：就是用统一的方法来迭代不同类型的集合！
>    1. 不同类型集合的内部数据结构不尽相同.
>       - 因此，如果要自己纯手工迭代的话相 **实现起来差异较大**.
>    2. 迭代器提供统一的方法迭代不同类型集合，隐藏底层的差异.

<br>

- Iterable接口：

```Java
public interface Iterable<T> {
  // 1. 传统的迭代器迭代
  Iterator<T> iterator();
  // 2. 简洁迭代语法糖，底层还是使用了传统迭代器迭代实现（只不过支持了Lambda表达式）
  default void forEach(Consumer<? super T> action);
}
```

<br>

**1.&nbsp; 传统迭代器迭代：**

<br>

> - 和C++中迭代器的概念一样，2要素：
>
>   1. 迭代器必定 **从属于某个容器**，其作用就是用来遍历所属容器中的元素.
>      - 具体就是 **每种Collection实现类都有自己的Iterator内部类**.
>   2. 迭代器是在容器的只读数据视图上进行迭代.
>      - 因此不能在迭代过程中修改容器中的数据，否则会抛出异常！
>      - 除非用迭代器的 **专用修改方法** 对数据进行修改，其余的擅自修改都将引发异常.

<br>

- 迭代器的使用步骤：
   1. 获取集合的迭代器：调用Collection的iterator**对象方法**就能获得
   2. 接着使用Iterator的对象方法hasNext、next迭代元素.

```Java
Iterator<E> iterator();

// 示例
ArrayList<Integer> c = ...;
Iterator<Integer> c.iterator();
```

<br>

- 迭代器的方法：

```Java
// 1. 传统迭代方式，hasNext不移动位置指针，next先移动再取元素
boolean hasNext();
E next();

// 2. 简洁迭代：lambda，内部实现还是传统迭代
default void forEachRemaining(Consumer<? super E> action);
// 示例
it.forEachRemaining(ele -> System.out.println(ele));

/** 3. 删除当前位置指针处的元素（直接修改了集合内容）
 *    - 等价于删除上一个next()返回的那个元素
 *    - 是Iterator唯一的一个可以修改集合本身内容的方法！
 */
void remove();
```

<br>

**2.&nbsp; 简洁迭代forEach语法糖：**

```Java

```

<br>

- forEach语法糖：
   - 由于是Iterable的接口方法，因此Collection可以直接调用该方法进行Lambda简洁迭代.

```Java
default void forEach(Consumer<? super T> action);
```

<br>

**3.&nbsp; 示例：**

```Java
Collection c = new ArrayList(); // ArrayList是Collection的一个实现类，默认元素类型为Object

Iterator it = c.iterator();
while (it.hasNext()) {
	Type var = it.next(); // 迭代值（数据视图）
	// 利用var进行操作
	c.remove(); // 错误！！在迭代过程中使用非迭代器方法对集合进行修改会直接抛出异常！！
    it.remove(); // 正确
}

c.forEach(o -> System.out.println(o));  // 语法糖简洁迭代
```

<br><br>

### 三、Predicate筛：[·](#目录)
> 是Java 8新增的集合过滤功能.
>
>> predicate是谓词的意思，即满足谓词条件的集合元素可以被过滤出来.
>>
>>> 用于**快速大批量**过滤元素.

<br>

**1.&nbsp; Predicate是一种谓词动作接口（函数式接口）：**

```Java
public interface Predicate<T> {
    boolean test(T t);
}
```

<br>

**2.&nbsp; Collection新增的谓词筛删除方法：**

```Java
// 快速大批量删除满足谓词条件的集合元素（直接修改了集合内容）
default boolean Collection.removeIf(Predicate<? super E> filter);

// 示例
c.removeIf(ele -> ele.length() < 10); // 批量删除所有长度小于10的元素
```

- **目前就只增加了一个removeIf（最最常用），未来可能会增加更多谓词筛方法.**

<br>

**3.&nbsp; 灵活运用：**

- 毕竟Predicate就是一个函数必报，完全可以当做函数指针使用：

```Java
public class Test {

    // 满足谓词条件p的元素都打印出来
	public static void operate(Collection c, Predicate p) {
		for (Object ele: c) {
			if (p.test(ele)) {
				System.out.println(ele);
			}
		}
	}

	public static void main(String[] args) {
		Collection c = new ArrayList();
		for (int i = 0; i < 10; i++) { // 加入0 ~ 9的字符串
			c.add(String.valueOf(i));
		}

		operate(c, ele -> Integer.valueOf((String)ele) > 3); // 大于3的打印出来
	}
}
```
