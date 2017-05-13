# Queue & PriorityQueue
> 1. Queue是所有FIFO结构的根接口.
>    - 其实现类有PriorityQueue（堆，优先队列）、Deque（双端队列）系（ArrayDeque、LinkedList，分别用数组和链表实现）.
> 2. PriorityQueue时刻按照元素大小关系维护堆序.

<br><br>

## 目录

1. [Queue的特有方法](#一queue的特有方法)
2. [PriorityQueue](#二priorityqueue)

<br><br>

### 一、Queue的特有方法：[·](#目录)
> 1. **直接继承自Collection，是所有FIFO结构的根接口.**
> 2. 实现类有PriorityQueue（堆，优先队列）、Deque（双端队列）系（ArrayDeque、LinkedList，分别用数组和链表实现）.
>    - Java**没有实现Queue的单端队列**，**直接用Deque**（双端队列）来完成单端队列功能即可.

<br>

- 围绕着FIFO，Queue提供了3种方法（增、删、查），其余没有对Collection做出任何扩展.
  - 但每种方法都有2个版本，一个是**异常安全的（不抛出异常）**，**另一种是异常不安全的（遇到异常直接抛出）**.

<br>

- 队列处理方法分成两类，一类是异常安全的，一类是异常不安全的：
   1. 异常不安全的方法命名继承自Collection.
   2. 异常不安全：满插、空取、空看 直接抛出异常！
   3. 异常不安全：满插返回false、空取返回null、空看返回null.

| | 尾插 | 头取 | 头看 |
| :---: | :---: | :---: | :---: |
| 异常不安全（命名来自Collection）| `boolean add(E e);` | `E remove();` | `E element();` |
| 异常安全 | `boolean offer(E e);` | `E poll();` | `E peek();` |

<br><br>

### 二、PriorityQueue：[·](#目录)
> 即优先队列（堆）.
>
> - 需要根据元素的大小关系时刻堆序.
>    1. 底层用数组实现的，因此和ArrayList一样，存在capacity物理容量上限这一概念.
>    2. 队首肯定是堆顶.
> - 由于元素需要比较大小，而null无法比较大小，因此不能存放null，否则抛出**[NullPointerException]异常**.

<br>

1. **没有对Queue扩展任何方法**，方法使用上就当纯Queue使用.
2. **排序规则设定：**
   1. 自然排序：元素自身实现Comparable接口的compareTo方法.
   2. 定制排序：PriorityQueue的指定Comparator的构造器

<br>

- 构造器：

```Java
// 1. 空集合，可以指定初始化数组大小（默认是11个单位），也可以选择排序规则
PriorityQueue([int initialCapacity][,][Comparator<? super E> comparator]);

// 2. 非空构造
  // 如果c是无序的，则采用自然排序，否则将c或s或p的比较体赋给this
PriorityQueue(Collection<? extends E> c|SortedSet<? extends E> s|PriorityQueue<? extends E> p);
```
