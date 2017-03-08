# Deque（栈的替代品）& ArrayDeque & LinkedList
> 由于Queue中的单端队列、双端队列使用都超级频繁，因此Java干脆合二为一，就只提供一个双端队列根接口Deque.
>
> - 并木有单独提供单端队列根接口，因为双端队列可以当做单端队列使用.
>
> <br>
>
> - Deque: 一个Deque三种用法，6不6？
>   1. 不使用任何对Queue的扩展（就相当于只当成Queue使用），那就是普通的单端队列.
>   2. 使用Deque特有的操作双端的方法，那就是典型的双端队列.
>   3. 可以当做栈使用：
>     - 由于双端队列可以在同一个口进出，因此可以模拟栈.
>     - Java把对一个口的操作方法包装了push、pop等操作，用来模拟栈.
> - ArrayDeque：
>   1.
> - LinkedList:
>   1.

<br><br>

！！从上面可以推断出Queue鉴别元素相同的依据和List一样也是equals方法！因为LinkedList属于Queue，而LinkedList也属于List，而List鉴别元素相同的依据是equals，因此Queue鉴别元素相同的依据肯定也是equals！不信可以试一下哦！！！
方法：
     i. 类别A（包装了一个int a）的equals返回true；
     ii. 连续插入1、2、3、4；
     iii. 然后删除一个7，结果发现序列变成了2、3、4；
     iv. 因为它的依据是equals方法，删除7的是否发现第一个1和7equals返回true，因此删除了第一个1！
！！所以equals一定要实现地合理！

1. Deque：
    1) 是Queue的子接口，表示双端队列，即两端（队尾和队首）都能插入和删除的特殊队列；
    2) 当然，Deque可以使用Queue的全部方法，但是自己也扩展了很多方法，主要用于操作两个端口的：特点就是Queue的每种方法都多了一个操作队尾的版本（同时也提供了一个名称上呼应的操作队首的版本（first（队首）、last（队尾）），和Queue的完全相对应（就是Queue的方法多了两个First和Last的后缀而已）：
         i. 插入：add/offer
异常不安全：
            a. void add(E ele);  // Queue
            b. void addFirst(E ele); // Deque，从队首加，和a.等价
            c. void addLast(E ele);  // Deque，从队尾加
异常安全：
            a. boolean offer(E ele);  // Queue
            b. boolean offerFirst | offerLast(E ele); // Deque
         ii. 取出并删除：remove/poll
异常不安全：E remove | removeFirst | removeLast();
异常安全：E poll | pollFirst | pollLast();
         iii. 查看但不删除：element（Queue）/get（Deque）/peek，有点儿特殊，不安全的查看没有直接沿用Queue的名称（element），而是get！
异常不安全：
            a. E element(); // Queue，查看队首
            b. E getFirst | getLast();  // Deque
异常安全：E peek | peekFirst | peekLast();
！！其中，Queue的版本和Deque的First版本完全等价，只是为了命名上和Last对称罢了！例如poll和pollFirst完全等价；
    3) 特殊的，Deque也有自己特有的一些常用方法：
         i. 找到并删除指定元素：boolean removeFirstOccurrence | removeLastOccurrence(E e);  // 删除队列中第一个出现的/最后一个出现的e，如果不存在或者空或其它返回false，成功删除（即改变了队列）则返回true
         ii. Deque不仅有普通的正向迭代器dq.iterator，也有反向迭代器：Iterator<E> descendingIterator();  // 得到的同样是Iterator类型的迭代器，只不过是反向的
！！即起始位置在队尾，其hasNext、next是朝队首迭代的！

2. 用Deque实现栈：
    1) Java1.0的时候就提供了一个Stack类，是Vector的子类，用于实现栈这种数据结构（FILO），即一种只能在一端进出元素的数据结构；
    2) 但是和Vector一样，Stack过于古老，并且实现地非常不好，问题多多，因此现在基本已经不用了；
    3) Deque是双端队列，两端都可以进出，因此可以直接用Deque来代替栈了（只操作一端就是栈了，只有First方法或者只用Last方法就变成栈了）；
    4) 但是为了迎合Stack的风格，Deque还是提供了push/pop方法，让它用起来感觉是栈：只不过这两个方法默认的端口是队首
         i. void push(E e);  // 等价于void addFirst(E e);
         ii. E pop();  // 等价于E removeFirst();
！！可以看到提供的两个方法都是一场不安全的版本，如果有异常安全的需求那就自行使用offer/poll(First/Last)方法

3. ArrayDeque：
    1) 顾名思义，就是用数组实现的Deque；
    2) 既然是底层是数组那肯定也可以指定其capacity，但不过由于双端队列本身数据结构的限制，要求只能在初始化的时候指定capacity，因此没有像ArrayList那样随时都可以调整的ensureCapacity方法了，只能在构造器中指定其初始的capacity：ArrayDeque(int numElements);   // 初始元素个数，默认长度是16
！！同样由于本身数据结构的限制，ArrayDeque没有trimToSize方法可以为自己瘦身！！
    3) ArrayDeque的使用方法就是上面的Deque的使用方法，基本没有对Deque拓展什么方法；

4. LinkedList：
    1) 是一种双向链表数据结构，由于双向链表的特性，它既具有线性表的特性也具有双端队列的特性；
    2) 在Java的集合中，LinkedList同时（直接）继承了List和Deque；
    3) 由于它是用链表实现的，因此不像数组那样可以指定初始长度，它只有一个空的构造器；
    4) 其余就是它可以使用List和Deque的全部方法，从功能的广度上（覆盖面）来将它是最强大的！
    5) 具体方法就不介绍了，既可以当成List使，也可以当成Deque使；

5. 各类线性表的性能比较以及选择：
    1) 性能主要是看底层是如何实现，不过就是数组和链表两种，因此性能也是围绕这两种数据结构展开的；
    2) 链表遍历、插入、删除高效（相对的数组不行），而数组随机访问、批量处理高效（相对的链表不行），链表使用迭代器迭代高效（数组不行，数组直接随机访问遍历更快）；
    3) 如何选择还是那句老话，根据具体需求决定（编程方便还是质量高/效率高）；
