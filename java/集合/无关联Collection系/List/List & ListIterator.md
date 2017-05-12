# List & ListIterator
> - List：
>   1. 继承自Collection（可使用Collection的全部方法），属于**无关联集合**.
>   2. 逻辑上看成类数组结构，**可索引（从0计）、可重复、维护插入顺序**.
>   3. 元素的equals用于查找、删除等操作.
>   4. 两个最常用的实现类：ArrayList（数组实现）、LinkedList（链表实现，**同时也是双端队列**）
>   5. 由于维护的是插入顺序，而插入顺序和元素本身无关，因此 **可以存放null（多个null也行）** ，毕竟不依赖compare、equals等.
>
>> 由于其特色是利用索引操作，因此必然要注意索引越界的问题.
>>
>>   - 一旦发生越界就会抛出**[IndexOutOfBoundsException]异常**.
>
> <br>
>
> - ListIterator：
>   1. List独有的迭代器.
>   2. 比Collection的基础的Iterator多了**前向迭代**和**插入元素**的功能.

<br><br>

## 目录

1. [构造List：利用Collections工具类](#一构造list利用collections工具类--)
2. [equals & hashCode](#二equals--hashcode)
3. [修改List](#三修改list)
4. [ListIterator：假设类型参数为E](#四listiterator假设类型参数为e--)

<br>

- **所有static开头的静态工具方法都是Collections中的！**

<br><br>

### 一、构造List：利用Collections工具类  [·](#目录)
> 得到的都是immutable列表，即不可修改的，不能add、remove，否则运行时异常！

<br>

```Java
// 1. 得到一个空的list
static <T> List<T>	emptyList();
// 2. 得到只有一个元素的list
static <T> List<T>	singletonList(T o);
// 3. 得到有n个o的list
static <T> List<T>	nCopies(int n, T o);
```

<br><br>

### 二、equals & hashCode：[·](#目录)

<br>

**1.&nbsp; equals：**

```Java
// 要求两个list的每个元素对应equals才行！！
boolean	equals(Object o);
```

<br>

**2.&nbsp; hashCode：**

```Java
// 根据每个元素的hashCode计算
int	hashCode();
// 算法如下
{
    int hashCode = 1;
    for (E e : list)
        hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
}
```

<br><br>

### 三、修改List：[·](#目录)

<br>

**1.&nbsp; 插入（添加）元素：**

- 是在指定的index和index - 1之间插入，可以理解为**在index前插入**.
  - 这个插入不会覆盖[index, )的元素，而是将其往后挪动.

```Java
// 1. 单插
void add(int index, E element);

// 示例
ArrayList<Integer> li = ...;  // 0, 1, 2
li.add(1, 50); // 0, 50, 1, 2

// 2. 群插
  // 越界抛出异常，而非返回false
boolean addAll(int index, Collection<? extends E> c);
```

<br>

**2.&nbsp; 删除元素（指定索引处）：**

```Java
E remove(int index);
```

<br>

**3.&nbsp; 全局替换元素：**

```Java
// 1. 传统的new替换old
static <T> boolean replaceAll(List<T> list, T oldVal, T newVal);

// 2. 根据一元操作批量修改 每一个元素，因为要返回计算结果并插入原容器，因此是E而不是? super E
default void replaceAll(UnaryOperator<E> operator);
// 示例
li.replaceAll(ele -> ele + 2);  // 更新每个元素+2
```

<br>

**4.&nbsp; 批量 复制&填充：**

```Java
// 1. 全部元素改为obj
static <T> void	fill(List<? super T> list, T obj);

// 2. dest的前src.lengt()个元素全部拷贝成和src一样
  // 如果dest.length() < src.length()就异常！
static <T> void	copy(List<? super T> dest, List<? extends T> src)
```

<br>

**5.&nbsp; 索引随机访问：取值 & 设值 & 截取区间  （i2th）**

```Java
// 1. 取值：arr[i]
E get(int index);

// 2. 设值：arr[i] = ele，并返回修改前的元素
E set(int index, E element);

// 3. 截取区间：this[fromIndex, toIndex)
List<E> subList(int fromIndex, int toIndex);
```

- 注意两者的区别：引用是值传递！

```Java
ArrayList<R> li = ...;  // [1, null, 3, 4]

R r = li.get(1);
r = new R(1000);  // [1, null, 3, 4]没有改变
// 那是因为r和li[1]是两个不同的引用变量，改变r的指向并不能改变li[1]的指向

li.set(1, new R(1000));  // [1, 1000, 3, 4]
// set直接改变li[1]的指向
```

<br>

**6.&nbsp; 定位查找元素：th2i**

- 找不到返回-1.
- 查找依据是equals.

```Java
// 1. 顺着找和逆着找第1个匹配的 单个元素
int indexOf|lastIndexOf(Object o);

// 2. 找到第1个匹配的 子序列
static int indexOfSubList|lastIndexOfSubList(List<?> source, List<?> target);

// 3. 二分查找单个元素（要求事先排序）
static <T> int binarySearch(List<? extends Comparable<? super T>> list, T key); // 默认升序规则
static <T> int binarySearch(List<? extends T> list, T key, Comparator<? super T> c); // 定制排序版本
```

<br>

**7.&nbsp; 顺序：**

```Java
// 1. 交换两个元素
static void	swap(List<?> list, int i, int j);

// 2. 反转
static void	reverse(List<?> list);

// 3. 随机打乱（可以指定自己的随机过程）
static void	shuffle(List<?> list[, Random rnd]);

// 4. 顺时针循环位移 distance%(list.size()-1) 个位置
  // 如果distance为负，则逆时针位移
static void	rotate(List<?> list, int distance);

// 5. 排序
  // 自然排序
static <T extends Comparable<? super T>> void sort(List<T> list);
  // 定制排序
default void sort(Comparator<? super E> c);
static <T> void	sort(List<T> list, Comparator<? super T> c);
```

<br><br>

### 四、ListIterator：假设类型参数为E  [·](#目录)
> List自己专有的迭代器，多了 **向前迭代 & 获取索引值** 的功能.

<br>

**1.&nbsp; 获取迭代器：**

```Java
// 获取从index开始的迭代器（默认无参从0开始）
ListIterator<E> listIterator([int index]);
```

<br>

**2.&nbsp; 前向迭代：**

- 返回index的都是peek，就是偷看一下一下个元素的索引，并不移动位置指针！

```Java
// 1. 前向探测
boolean hasPrevious();
E previous();
int previousIndex();

// 2. 正常地向后迭代
boolean hasNext();
E next();
int nextIndex();
```

<br>

**3.&nbsp; 修改：**

- 即当前位置前的一个空缺处插入.

```Java
// 1. 在当前位置指针 前 插入一个元素
void add(E e);

// 2. 删除当前位置指针处的元素
void remove();

// 3. 设值：同样是当前位置指针处的元素
void set(E e);

// 示例
ArrayList<Integer> li = ...;  // 0, 1, 2
ListIterator<Integer> it = li.listIterator(1);
it.add(99);  // 0, 99, 1, 2
```
