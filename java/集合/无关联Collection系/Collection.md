# Collection概述 & Iterator和Predicate筛

<br><br>

## 目录

1. []()
2. []()
3. []()

<br><br>

### 一、Collection接口：假设其类型参数是E
> 所有无关联集合的根接口，**其方法在所有派生类中都存在（也就是在Set、List、Queue以及其实现类中都可以使用）**.

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

### 二、只有Collection有的Iterator：
> Java的迭代器Iterator**只在Collection中有，Map中木有**！
>
> - 和C++中迭代器的概念一样，二要素：
>
>   1. 迭代器必定**从属于某个容器**，其作用就是用来遍历所属容器中的元素的.
>     - 具体就是**每种Collection实现类都有自己的Iterator内部类**.
>   2. 迭代器是在容器的只读数据视图之上进行迭代.
>     - 因此不能在迭代过程中修改容器中的数据，否则会抛出异常！
>     - 除非用迭代器的**专用修改方法**对数据进行修改，其余自说三话的修改都将引发异常.

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

### 三、Iterator的常用方法：假设类型参数是E

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
```


！！由于Java容器都是泛型类模板，因此容器可以记忆元素的具体类型，因此可以放心使用，只不过取出元素后要进行类型转换后才能正常使用！
！！！使用迭代器next获得的元素是一个集合中对应元素的深拷贝（即数据视图），如果对迭代变量进行修改是不会修改集合中的原数据的！！
！！同样，也不能直接在迭代过程中使用c.remove等方法对集合进行修改，因为迭代器已经锁定住集合了，强行修改会抛出异常！只能用Iterator的专用修改集合元素的方法修改才是正确的，就像上面的Iterator.remove方法；
    4) 模板：
public class Test {

	public static void main(String[] args) {
		Collection c = new ArrayList(); // ArrayList是Collection的一个实现类，默认元素类型为Object

		Iterator it = c.iterator();
		while (it.hasNext()) {
			Type var = it.next(); // 迭代值（数据视图）
			对var进行操作;
			c.remove(); // 错误！！在迭代过程中使用非迭代器方法对集合进行修改会直接抛出异常！！
		}
	}
}
！！可以看到，实际上，Iterator迭代的“集合”是真正集合的视图，视图和真实数据之间是一一映射的关系，如果此时使用非迭代器方法对真实数据进行修改就会导致真实数据和映像之间不一致，因此会抛出异常，而迭代器的修改方法可以保证这种映射的一致性，即迭代器先对视图进行修改，然后将视图的修改更新到真实数据，但是反向就是无效的，因为映像自己是知道关联的是哪个真实数据，但是真实数据本身不知道有哪些映像和我关联的，即真实数据永远是被动的，而映像是主动的！

3. Lambda表达式结合迭代器进行遍历——forEachRemaining方法：
    1) Iterator专门提供了一个forEachRemaining方法可以专门使用Lambda表达式进行遍历，以完成一些强大的功能（同时保证代码的高度简洁）；
    2) 原型：default void Iterator.forEachRemaining(Consumer<? super E> action);  // 里面又是一个函数闭包Consumer action，用来定义对当前迭代变量进行何种操作
    3) 里面的action同样是接受每次迭代的变量，相当于上面代码"Type var = it.next()“中的var，同样也是数据视图，不要在action中用尝试修改集合中的元素
    4) 示例：it.forEachRemaining(ele -> System.out.println(ele)); // 打印每个元素

4. Java 8新增的Predicate集合过滤方法：
    1) Predicate是一种谓词动作接口：是一个函数式接口，即表示一个动作
public interface Predicate<T> {
    boolean test(T t);
}
！！即在test中给出一种关于变量t的测试条件；
    2) 该接口主要用来筛选满足该判断条件的集合元素；
    3) Java 8为Collection新增了一个支持根据Predicate条件判断来筛选集合元素的方法：
         i. 该方法是：default boolean Collection.removeIf(Predicate<? super E> filter);  // 可以批量删除满足Predicate filter条件的所有元素
         ii. 示例：c.removeIf(ele -> ele.length() < 10); // 批量删除所有长度小于10的元素
         iii. 目前只增加了一个removeIf，未来可能会增加更多Predicate筛选方法；

5. 灵活运用Predicate：
    1) 毕竟Predicate就是一个函数式接口，你就可以把它当做C语言函数指针使用；
    2) 示例：
public class Test {

	public static void operate(Collection c, Predicate p) { // 满足谓词条件p的元素都打印出来
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






### 二、Collection接口：
> 所有无关联集合的根接口，**其方法在所有派生类中都存在（也就是在Set、List、Queue以及其实现类中都可以使用）**.

    2) 接下来介绍的都是Collection的对象方法；
    3) 添加元素：成功添加返回true
         i. boolean add(Object o); // 添加一个元素
         ii. boolean addAll(Collection c);  // 将c中的所有元素加入
    4) 删除元素：成功删除返回true
         i. boolean remove(Object o);  // 删除指定元素，只删除第一个找到的
         ii. boolean removeAll(Collection c);  // 删除c中所有的元素，相当于集合相减
         iii. boolean retainAll(Collection c);  // 删除c中不包含的元素，相当于集合相交
         iv. void clear(); // 清空集合
    5) 检查是否包含某个元素：
         i. boolean contains(Object o);  // 是否包含指定元元素o
         ii. boolean contains(Collection c);  // 是否包含集合c中的所有元素
    6) 功能方法：
         i. 查看集合中元素的个数：int size();
         ii. 判空：boolean isEmpty();
         iii. 把集合转换成一个数组：Object[] toArray();
    7) 示例：
public class Test {

	public static void main(String[] args) {
		Collection c = new ArrayList(); // ArrayList是Collection的一个实现类，默认元素类型为Object

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
！！这里我们先忽略泛型，因此编译警告先忽略；
