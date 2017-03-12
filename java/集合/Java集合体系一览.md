# Java集合体系一览
> 1. null（允许null）、not null（不允许null）、唯一null（只有一个元素时才允许是null）
> 2. 粗体表示实现类，否则表示抽象接口.

<br><br>

  - Collection
    - Set：无扩展
      - **HashSet**：无扩展，null
      - **EnumSet**：只扩展了构造方式，not null
      - **LinkedHashSet**：无扩展，null
      - SortedSet：扩展了顺序维护
        - **TreeSet**：无扩展，**唯一null**
    - List：扩展了插入顺序维护（**索引操作**），null
      - **ArrayList**：只扩展了物理上限的操作
      - **`LinkedList`**：无扩展（同时是Deque）
    - Queue：只扩展了**单端队列**操作
      - **PriorityQueue**：无扩展，**not null**
      - Deque：只扩展了**双端操作**
        - **ArrayDeque**：只扩展了一个设定物理上限的构造器，**not null**
        - **`LinkedList`**：无扩展（同时也是List），**null**
  - Map
    - Hashtable
      - **Properties**：专属用法
    - **HashMap**：无扩展，null
    - **EnumHashMap**：只扩展了两个构造器，not null
    - **IdentityHashMap**：无扩展，null
    - **LinkedHashMap**：无扩展，null
    - SortedMap：只扩展了大小顺序维护的操作
      - **TreeMap**：无扩展，**唯一null**

<br><br>

- **关于equals、compare的总结：**
  1. 所有hash存储结构：
    - 相等比较：hashCode和equals共同决定.
  2. 所有维护大小顺序的存储结构：SortedXxx
    - 相等比较：compare = 0（注意不是equals！）
    - 大小比较：当然还是compare了，即compare ≠ 0
  3. 其余一切存储结构：
    - 相等比较：equals
