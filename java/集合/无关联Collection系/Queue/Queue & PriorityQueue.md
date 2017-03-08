# Queue & PriorityQueue
> 1. Queue是所有FIFO结构的根接口.
>   - 其实现类有PriorityQueue（堆，优先队列）、Deque（双端队列）系（ArrayDeque、LinkedList，分别用数组和链表实现）.
> 2. PriorityQueue时刻按照元素大小关系维护堆序.

<br><br>

## 目录

1. [Queue的特有方法]()
2. [PriorityQueue]()

<br><br>

### 一、Queue的特有方法：[·](#目录)
> 1. **直接继承自Collection，是所有FIFO结构的根接口.**
> 2. 实现类有PriorityQueue（堆，优先队列）、Deque（双端队列）系（ArrayDeque、LinkedList，分别用数组和链表实现）.
>   - Java**没有实现Queue的单端队列**，**直接用Deque**（双端队列）来完成单端队列功能即可.

<br>

- 围绕着FIFO，Queue提供了3种方法（增、删、查），其余没有对Collection做出任何扩展.
  - 但每种方法都有2个版本，一个是**异常安全的（不抛出异常）**，**另一种是异常不安全的（遇到异常直接抛出）**.

<br>

**1.&nbsp; 从队尾部添一个元素：**

```Java
/** 1. 异常不安全
 *  - 如果队列的空间是受限的，则满插时会
 *    1. 抛出异常.
 *    2. 并返回false.
 */
boolean add(E e);

// 2. 异常安全，满插时返回false，不抛出异常
boolean offer(E e); // 空间受限满插时不抛出异常，只是返回false
```

<br>

**2.&nbsp; 从队头取出一个元素（同时删除）：**

```Java
// 1. 异常不安全，空取时抛出异常
E remove();

// 2. 异常安全，空取时返回null
E poll();
```

<br>

**3.&nbsp; 偷看一下队头元素（不删除）：**

```Java
// 1. 异常不安全，空看抛出异常
E element();  

// 2. 异常安全，空看返回null
E peek();
```

<br><br>

### 二、PriorityQueue：[·](#目录)
> 即优先队列（堆）.
>
> - 需要根据元素的大小关系时刻堆序.
>   1. 底层用数组实现的，因此和ArrayList一样，存在capacity物理容量上限这一概念.
>   2. 队首肯定是堆顶.
> - 由于元素需要比较大小，而null无法比较大小，因此不能存放null，否则抛出**[NullPointerException]异常**.

<br>

1. **没有对Queue扩展任何方法**，方法使用上就当纯Queue使用.
2. **排序规则设定：**
  1. 自然排序：元素自身实现Comparable接口的compareTo方法.
  2. 定制排序：PriorityQueue的指定Comparator的构造器

<br>

- 构造器：

```Java
// 1. 空，物理容量默认
PriorityQueue();

// 2. 空，指定物理容量
  // 预估的话最好要自信、精确
PriorityQueue(int initialCapacity);

// 3. 空，默认容量，定制排序
PriorityQueue(Comparator<? super E> comparator);

// 4. 空，指定容量 & 定制排序
PriorityQueue(int initialCapacity, Comparator<? super E> comparator);

/** 5. 用另一个集合构造一个堆
 *  - 如果c是PriorityQueue或者是SortedSet，那么：
 *    1. 将c.cmp赋给this.cmp
 *    2. 保持c原有的顺序构造出this
 *  - 如果c是其它集合，那么：
 *    1. 将c中元素的compareTo赋给this.cmp
 *    2. 再按照这个cmp逐个插入c中的元素整理出新的堆序
 *
 *  $$ 总之，c中的元素必须可比，如果不可比将抛出异常！
 */
PriorityQueue(Collection<? extends E> c);
```
