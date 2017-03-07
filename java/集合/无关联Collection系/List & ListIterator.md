# List & ListIterator
> - List：
>   1. 继承自Collection（可使用Collection的全部方法），属于**无关联集合**.
>   2. 逻辑上看成类数组结构，**可索引（从0计）、可重复、维护插入顺序**.
>   3. 元素的equals用于查找、删除等操作.
>   4. 两个最常用的实现类：ArrayList（数组实现）、LinkedList（链表实现，**同时也是双端队列**）
>   5. 由于维护的是插入顺序，而插入顺序和元素本身无关，因此 **可以存放null（多个null也行）** ，毕竟不依赖compare、equals等.
>
> <br>
>
> - ListIterator：
>   1. List独有的迭代器.
>   2. 比Collection的基础的Iterator多了**前向迭代**和**插入元素**的功能.

<br><br>

## 目录

1. [List独有的索引操作方法：假设类型参数为E]()
2. [ListIterator：假设类型参数为E]()

<br><br>

### 一、List独有的索引操作方法：假设类型参数为E  [·](#目录)
> 由于其特色是利用索引操作，因此必然要注意索引越界的问题.
>
>   - 一旦发生越界就会抛出**[IndexOutOfBoundsException]异常**.

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

**2.&nbsp; 删除元素：**

```Java
E remove(int index);
```

<br>

**3.&nbsp; 访问元素 & 直接设值：**

```Java
// 1. 返回指定索引处元素的引用值
E get(int index);

// 2. 修改指定索引元素引用的指向
  // 返回修改前的元素
E set(int index, E element);
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

**4.&nbsp; 定位元素：**

- 找不到返回-1.
- 查找依据是equals.

```Java
// 1. 返回第一个查找到的
int indexOf(Object o);

// 2. 返回最后一个查找到的
int lastIndexOf(Object o);
```

<br>

**5.&nbsp; 截取区间：**

```Java
// 返回[fromIndex, toIndex)区间子集
List<E> subList(int fromIndex, int toIndex);
```

<br>

**6.&nbsp; 快速大批量替换：**

```Java
// 根据一元操作修改每一个元素
default void replaceAll(UnaryOperator<E> operator);

// 示例
li.replaceAll(ele -> ele + 2);  // 更新每个元素+2
```

<br>

**7.&nbsp; 定制快排：**

```Java
// 定制排序
default void sort(Comparator<? super E> cmp);

// 示例
li.sort((e1, e2) -> e2 - e1); // 降序排列
```

<br><br>

### 二、ListIterator：假设类型参数为E  [·](#目录)
> List自己专有的迭代器.

<br>

**1.&nbsp; 获取迭代器：**

```Java
// 1. 获取从0开始的迭代器
ListIterator<E> listIterator();

// 2. 获取从index开始的迭代器
ListIterator<E> listIterator(int index)
```

<br>

**2.&nbsp; 前向迭代：**

```Java
// 1. 前向探测
boolean hasPrevious();

// 2. 指针前移并返回前移后指向的新元素
E previous();
```

<br>

**3.&nbsp; 在当前指针位置处插入（添加）一个元素：**

- 即当前位置前的一个空缺处插入.

```Java
void add(E e);

// 示例
ArrayList<Integer> li = ...;  // 0, 1, 2
ListIterator<Integer> it = li.listIterator(1);
it.add(99);  // 0, 99, 1, 2
```
