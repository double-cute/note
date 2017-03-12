# Collection概述 & Iterator和Predicate筛

<br><br>

## 目录

1. [Collection接口：假设其类型参数是E](#一collection接口假设其类型参数是e--)
2. [只有Collection有的Iterator](#二只有collection有的iterator)
3. [Iterator的常用方法：假设类型参数是E](#三iterator的常用方法假设类型参数是e--)
4. [Predicate筛](#四predicate筛)

<br><br>

### 一、Collection接口：假设其类型参数是E  [·](#目录)
> 所有无关联集合的根接口，**其方法在所有派生类中都存在（也就是在Set、List、Queue以及其实现类中都可以使用）**.

<br>

- Collections工具类对Collection的支持：
  - [Collection查询信息](../../Collections工具类%20%26%20多线程集合.md#四collection查询信息)

<br>

**1.&nbsp; 常用功能：**

```Java
int size();  // 元素个数
boolean isEmpty();  // 判空
Object[] toArray();  // 转成数组，返回类型是Object，需要强转
```

<br>

**2.&nbsp; 添加元素：**

- 成功添加返回true

```Java
// 1. 添加一个元素
boolean add(E e);

// 2. 将c的所有元素加入this
  // 注意被加入元素的编译时类型视为E（上限）
boolean addAll(Collection<? extends E> c);
```

<br>

**3.&nbsp; 删除元素：**

- 成功删除返回true.

```Java
/** 1. 删除指定元素
 *  只删第一个找到的
 *  做的是Object相等比较
 */
boolean remove(Object o);

/** 2. 集合减（this - c）
 *  元素编译时类型算Object
 *  因此还是Object相等比较
 */
boolean removeAll(Collection<?> c);  

/** 3. 集合交（this && c）
 *  元素编译时类型算Object
 *  因此还是Object相等比较
 */
boolean retainAll(Collection<?> c);

// 4.清空集合
void clear();
```

<br>

**4.&nbsp; 是否包含某个元素：**

```Java
// 1. 是否包含指定元元素o
boolean contains(Object o);  

// 2. 是否包含集合c中的所有元素
  // 做的是类型上限Object的相等比较
boolean containsAll(Collection<?> c);
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

### 二、只有Collection有的Iterator：[·](#目录)
> Java的迭代器Iterator**只在Collection中有，Map中木有**！
>
> - 和C++中迭代器的概念一样，二要素：
>
>   1. 迭代器必定**从属于某个容器**，其作用就是用来遍历所属容器中的元素的.
>     - 具体就是**每种Collection实现类都有自己的Iterator内部类**.
>   2. 迭代器是在容器的只读数据视图之上进行迭代.
>     - 因此不能在迭代过程中修改容器中的数据，否则会抛出异常！
>     - 除非用迭代器的**专用修改方法**对数据进行修改，其余自说三话的修改都将引发异常.

<br>

**1.&nbsp; 迭代器的终极目标：**

- 就是用统一的方法来迭代不同类型的集合！
  1. 不同类型集合的内部数据结构不尽相同.
    - 因此，如果要自己纯手工迭代的话相**实现起来差异较大**.
  2. 迭代器提供统一的方法迭代不同类型集合，隐藏底层的差异.

<br>

**2.&nbsp; 迭代器的使用步骤：**

1. 获取集合的迭代器：调用Collection的iterator**对象方法**就能获得
2. 接着使用Iterator的对象方法hashNext、next迭代元素.

```Java
Iterator<E> Collection.iterator();

// 实例
ArrayList<Integer> c = ...;
Iterator<Integer> c.iterator();
```

<br><br>

### 三、Iterator的常用方法：假设类型参数是E  [·](#目录)

```Java
// 1. 检查是否还有下一个元素可以迭代
boolean hasNext();

// 2. 先移动一位位置指针，再取出元素（即取出下一个元素）
E next();

/** 3. 删除当前位置指针出的元素（直接修改了集合内容）
 *  等价于删除上一个next()返回的那个元素
 *  是Iterator唯一的一个可以修改集合本身内容的方法！
 */
void remove();

// 简洁迭代
default void forEachRemaining(Consumer<? super E> action);
// 示例
it.forEachRemaining(ele -> System.out.println(ele));
```

<br>

- 示例：

```Java
Collection c = new ArrayList(); // ArrayList是Collection的一个实现类，默认元素类型为Object

Iterator it = c.iterator();
while (it.hasNext()) {
	Type var = it.next(); // 迭代值（数据视图）
	// 利用var进行操作
	c.remove(); // 错误！！在迭代过程中使用非迭代器方法对集合进行修改会直接抛出异常！！
    it.remove(); // 正确
}
```

<br><br>

### 四、Predicate筛：[·](#目录)
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
