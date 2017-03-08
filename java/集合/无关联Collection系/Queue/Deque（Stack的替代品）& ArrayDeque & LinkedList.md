# Deque（Stack的替代品）& ArrayDeque & LinkedList
> 由于Queue中的单端队列、双端队列使用都超级频繁，因此Java干脆合二为一，就只提供一个双端队列根接口Deque.
>
> - 并木有单独提供单端队列根接口，因为双端队列可以当做单端队列使用.
>
> <br>
>
> - Deque: 一个Deque三种用法，6不6？
>   1. 不使用任何对Queue的扩展（就相当于**只当成Queue使用，即只用Queue的方法**），那就是普通的**单端队列**.
>   2. **使用Deque特有的操作双端的方法**，那就是典型的**双端队列**.
>   3. 可以当做**栈**使用：
>     - 由于双端队列可以在同一个口进出，因此可以模拟栈.
>     - Java把对一个口的操作方法包装了**push、pop等操作**，用来模拟栈.
>   4. 由于维护的是插入顺序（不用维护大小顺序），因此只有**equals用来做相等比较**.
> - Stack：
>   1. 旧标准，实现不好，已经**被摒弃**.
>   2. **Deque现在是栈的新标准.**
> - ArrayDeque：
>   1. 数组实现.
>   2. 只能在构造时确定capacity.
>   3. 非常特殊！**不允许保存null**（否则就抛出 **[NullPointerException]** ）.
>     - 虽然只是维护插入顺序（不用比较大小），但由于数组实现本身的限制以及数据结构的特性.
>     - 导致必须不能存放null.
> - LinkedList:
>   1. 用双链表实现的Deque.
>   2. 同时实现了List和Deque接口，因此完全可以当做List使用.
>   3. 由于链表的特性，**允许存放null**.

<br><br>

## 目录

1. [Deque的双端操作方法](#一deque的双端操作方法)
2. [Deque特有的扩展方法](#二deque特有的扩展方法)
3. [Deque对栈操作的支持](#三deque对栈操作的支持)
4. [ArrayDeque](#四arraydeque)
5. [LinkedList](#五linkedlist)
6. [各类线性表的性能比较](#六各类线性表的性能比较)

<br><br>

### 一、Deque的双端操作方法：[·](#目录)
> 其实完全就是和Queue的三组方法相对应（插入、弹出、偷看），只不过分别支持两个端口.

<br>

**1.&nbsp; 插入：**

```Java
/* == 异常不安全 == */

// 1. 从队首加
void addFirst(E ele);
// 2. 从队尾加
void addLast(E ele); // 和Queue的add等价

/* == 异常安全 == */

// 3. 从队首加
boolean offerFirst(E ele);
// 4. 从队尾加
boolean offerLast(E ele); // 和Queue的offer等价
```

<br>

**2.&nbsp; 弹出：**

```Java
/* == 异常不安全 == */

// 1. 从队首弹
E removeFirst();  // 和Queue的remove等价
// 2. 从队尾弹
E removeLast();

/* == 异常安全 == */

// 3. 从队首弹
E pollFirst();  // 等价于Queue的poll
// 4. 从队尾弹
E pollLast();
```

<br>

**3.&nbsp; 偷看：**

```Java
/* == 异常不安全 == */

// 1. 偷看队首
E getFirst()  // 和Queue的element等价
// 2. 偷看队尾
E getLast();

/* == 异常安全 == */

// 3. 偷看队首
E peekFirst();  // 等价于Queue的peek
// 4. 偷看队尾
E peekLast();
```

<br><br>

### 二、Deque特有的扩展方法：[·](#目录)

<br>

**1.&nbsp; 删除指定元素：**

- 依据是equals.
  - 没找到指定元素则返回false.
  - 该方法并不返回被删除的元素.

```Java
// 1. 删除第一个出现的e
boolean removeFirstOccurrence(E e);

// 2. 删除最后一个出现的e
boolean removeLastOccurrence(E e);
```

<br>

**2.&nbsp; 反向迭代器：**

```Java
// 1. 正常的从Collection继承而来的正向迭代器
Iterator<E> iterator();  // 起点在队首

/** 2. 特有的反向迭代器，起点在队尾
 *  - 其hasNext和next是朝反向迭代的.
 */
Iterator<E> descendingIterator();  // 起点在队尾
```

<br><br>

### 三、Deque对栈操作的支持：[·](#目录)
> JDK 1.0就提供Stack类，是Vector的子类，使用数组实现的FILO栈结构.
>
> - 但是和Vector一样，Stack过于古老，并且实现地非常不好，问题多多，因此现在已经被摒弃了.
>
>> 由于**双端队列可以轻松实现栈的功能**（限制在一个端口进出元素），因此**目前Deque成了栈的新标准**.
>>
>> - **Deque对一些双端操作进行了包装，完美支持了栈的功能.**

<br>

- Java只是将Deque的addFirst和removeFirst两个异常不安全的单端进出方法包装成了push和pop.
  - 默认队首作为栈口.

```Java
// 1. 压栈
void push(E e);  // 等价于addFirst

// 2. 弹栈
E pop();  // 等价于removeFirst
```

- 如果有其它需求，比如：
  1. 用队尾作为栈口.
  2. 希望压弹操作是异常安全的.
- 那么就自己利用其它双端操作方法来代替push和pop了.

<br><br>

### 四、ArrayDeque：[·](#目录)
> 顾名思义，就是用数组实现的Deque.
>
>> 和ArrayList一样，也是动态开辟内存（满了就再自动开辟），也有capacity.
>>
>>> 除了比Deque多了一个指定capacity的构造器之外，**没有对Deque接口做任何拓展，没有多出任何额外的方法**.

<br>

- 只不过由于Deque本身数据结构特点的限制：
  1. 只能在构造初始化时指定capacity.
    - **初始化确定了capacity之后就无法再修改了.**
    - 不像ArrayList那样可以通过ensureCapacity随时修改.
  2. 并且没有trimToSize的瘦身方法.

```Java
// 1. 默认capacity是16个元素
ArrayDeque();

// 2. 显式指定初始capacity大小
ArrayDeque(int numElements);
```

<br><br>

### 五、LinkedList：[·](#目录)
> 用双向链表实现的Deque.
>
>> 由于链表的特性，允许LinkedList存放null.

<br>

1. 多重继承：同时实现了List和Deque接口.
  - 因此可以同时使用List和Deque的方法.
2. 由于不是用数组实现的，它**有且仅有一个默认的无参构造器**.

<br>

- 由于其既可以当做List使也可以当做Deque使，因此功能上是最强大的.

<br><br>

### 六、各类线性表的性能比较：[·](#目录)
> 性能优劣依赖于底层实现和应用场景.

<br>

1. 数组随机访问、批量处理性能优越.
2. 链表在插入、删除、迭代器迭代方面性能优越.
  - 数组用迭代器迭代反而效率比普通迭代要低.
