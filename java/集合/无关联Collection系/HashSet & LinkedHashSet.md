# Set & HashSet & LinkedHashSet
> 1. Set是Collection的逻辑派生（没有提供任何额外方法）.
> 2. HashSet和LinkedHashSet都是Set接口的实现类.

<br><br>

## 目录

1. [Set仅仅是Collection的逻辑派生](#一set仅仅是collection的逻辑派生)
2. [HashSet](#二hashset)
3. [集合的天敌：可变元素](#三集合的天敌可变元素--)
4. [LinkedHashSet](#四linkedhashset)

<br><br>

### 一、Set仅仅是Collection的逻辑派生：[·](#目录)
> 仅仅是对Collection的逻辑扩展，**没有添加任何额外的方法**.
>
>> 因此，**功能上完全和Collection一样，但继承关系上Set是Collection的子接口**.

<br>

- 表示 **不可重复**、**无序** 的集合（但无论如何都**必须是不可重复的**，顺序可以根据不同实现选择性维护）
  1. 不可重复体现在：如果**add两个相同的元素**，则返回false，表示添加失败.
  2. 无序体现在：不维护**插入顺序**，不维护**大小顺序**.
    - 大小顺序和插入顺序两者是矛盾的，只能维护其一，不能同时维护.

<br>

- 由于Set只是接口（不是实现类），因此不能定义Set对象.
  - 它的实现类有：

| Set实现类 | 说明 |
| --- | --- |
| HashSet | 不可重复、无序 |
| LinkedHashSet | 不可重复，但只维护插入顺序<br>只比HashSet多了一个维护插入顺序的链表 |
| TreeSet | 不可重复，维护大小顺序 |
| EnumSet | 不可重复，特殊的、专门存放枚举值的集合 |

<br><br>

### 二、HashSet：[·](#目录)
> Set的最常用、最简单的 **不可重复**、**无序** 实现类.

<br>

**1.&nbsp; 不可重复是如何实现的？**

- 用Hash表实现：
  1. 元素的Hash值作为表头索引，即**桶**，即**链的链头**.
    - 元素的**Hash值决定桶的位置**.
  2. **如果Hash值相同就存放在一个桶里**，接着用equals比较，如果还相同就表明是同一个元素.
    - 如果equals比较不同，则存放在桶里的下一个**槽位**，即**直接挂在链尾处**.

<br>

- 总结：**HashSet插入、访问元素的实现过程**（先后顺序很重要）
  1. **首先** 计算 **ele.hashCode()** 决定桶的位置（链头的位置）.
  2. **最后** 用 **ele.equals()** 和桶中的其它元素比较：
    1. 如果是插入：都不相等则扩展槽位（挂在链尾）.
    2. 如果是访问：相等则命中，取出元素.

<br>

**2.&nbsp; 不可重复是如何实现的？**

- 用Hash表作为存储结构自然是无序的（不维护大小顺序，也不维护插入顺序）.

<br>

**3.&nbsp; hashCode和equals的实现原则：**

- 指导原则就是 **对应相同** ：即hashCode相同则equals应该返回true，hashCode不同则equals应该返回false.
  1. 如果hashCode相同但equals为false，那么就违背了Set不重复的理念了（毕竟equals是内容相等的象征）.
  2. 如果hashCode相同但equals为true，那么会增大链长，这就降低了Hash表的访问效率了.

<br>

- 具体实现原则：
  1. equals就是**标准**的**内容相等**.
    - 什么是**对象的有意义变量**？
      - 就是：要**参与到equals比较的变量**.
      - 有意义的变量就是上述要做equals比较的**内容**.
  2. hashCode的含义和实现原则：
    1. public int hashCode();  // 继承自Object，专门留给子类覆盖
    2. 用处：如果该类的对象要作为HashSet、HashMap等Hash表结构的元素，就必须实现它.
    3. 实现原则：
      1. 使用对象中 **所有** **有意义的变量** 计算.
      2. 计算出的hash值尽量**不重复**（或者使重复的概率非常非常小）.

<br>

**4.&nbsp; hashCode的推荐实现方案：**

> 注意！Java规定了，hash值的类型**必须是int**.

- 各类型数据的hash值计算方法：无非就是基本类型和引用类型两种

| Java数据类型 | 推荐的计算方法（假设基本类型值为val）|
| --- | --- |
| boolean | true? 0: 1 |
| byte、short、char、int | (int)val |
| long | (int)(val ^ (val >>> 32)) |
| float | Float.floatToIntBits(val) |
| double | long lb = Double.doubleToLongBits(val);<br>(int)(lb ^ (lb >>> 32)) |
| 引用类型 | obj.hashCode() |

- 自定义类型的hashCode计算方法推荐：
  - 各个有意义成员的hash值乘以一个质数后相加.
  - 示例：

```Java
class R {
    boolean bool;
    char ch;
    long l;
    double d;
    String s;

    @Override
	public boolean equals(Object obj) {
		if (this == obj) { // 地址相同或者同为null
			return true;
		}

		if (obj != null && obj.getClass() == R.class) { // 必须运行时类型完全相同
			R another = (R)obj;

			return // 有意义的内容完全相同
				this.bool = another.bool &&
				this.ch == another.ch &&
				...
				this.s.equals(another.s);
		}

		return false;
	}

    @Override
	public int hashCode() { // 尽量让equals为false的hash值不同
        int hashBool = (bool? 0: 1) * 11;
        int hashCh = (int)ch * 13;
        int hashL = (int)(l ^ (l >>> 32)) * 19;
            long lb = Double.doubleToLongBits(d);
        int hashD = (int)(lb ^ (lb >> 32)) * 31;
        int hashS = s.hashCode();

		return hashBool + hashCh + hashL + hashD + hashS;
	}
}
```

- 重申一下equals的比较原则：
  1. 关键词是 **完全相同**.
  2. 完全相同的含义是：或的关系
    1. 地址相同（分为同为null，都不同为null）.
    2. **运行时类型完全相同.**
      - 完全相同中最重要最霸气的相同.
  3. 有意义的数据成员 **完全相同**.
    - equals的**递归定义**.

<br><br>

### 三、集合的天敌：可变元素  [·](#目录)
> 如果随意**改变集合中元素的内容**，就可能**导致该元素位置信息的紊乱**.

<br>

**1.&nbsp; 原因很简单：**

- 虽然元素**内容改变**了，但其本身在集合中的**物理位置没有改变**.
- 但由于内容改变了，依赖内容计算的**hashCode、equals等决定物理位置的信息值**也随之**改变**.
- 这就导致实际 **物理位置**和**位置信息** 不匹配而发生 **信息错位**，从而导致致命的错误.

<br>

**2.&nbsp; 以HashSet为例：**

```Java
class R {
	int val;
	public R(int val) {
		this.val = val;
	}
	@Override
	public int hashCode() {
		return val;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj != null && obj.getClass() == R.class) {
			R another = (R)obj;
			return this.val == another.val;
		}

		return false;
	}
	@Override
	public String toString() {
		return String.valueOf(val);
	}

}


HashSet<R> hs = new HashSet<>();

// op1
hs.add(new R(5));
hs.add(new R(-3));
hs.add(new R(9));
hs.add(new R(-2));

// -2 -3 5 9

Iterator<R> it = hs.iterator();

// op2
R first = it.next();
first.val = -3;
// -3 -3 5 9

// op3
hs.remove(new R(-3));
// -3 5 9

// op4
hs.contains(new R(-3));  // false
hs.contains(new R(-2));  // false
```

- op1：4个add

| 桶编号 | 内容 |
| --- | --- |
| -2 | -2 |
| -3 | -3 |
| 5 | 5 |
| 9 | 9 |

- op2：first.val = -3

| 桶编号 | 内容 |
| --- | --- |
| -2 | **-3**（原来是-2）|
| -3 | -3 |
| 5 | 5 |
| 9 | 9 |

- op3：hs.remove(new R(-3));
  - hashCode找到桶(-3)，再找equals(-3)
  - 因此删掉了**第2个**桶.

| 桶编号 | 内容 |
| --- | --- |
| -2 | **-3** |
| ~~-3~~ | ~~-3~~ |
| 5 | 5 |
| 9 | 9 |

- op4：
  1. hs.contains(new R(-3)); // -3的桶找不到，false
  2. hs.contains(new R(-2)); // -2的桶能找到，但里面装的内容是-3，equals返回false

| 桶编号 | 内容 |
| --- | --- |
| -2 | **-3** |
| 5 | 5 |
| 9 | 9 |

- **可以看到混乱至极.**

<br>

**3.&nbsp; 总结：**

- 不仅是HashSet，还有很多需要hashCode、equals以及其它（甚至自定义）方法计算物理位置的集合.
  1. 要么存放只读元素.
  2. 如果存放的是可变元素，那也尽量不去修改（严格地说应该是禁止修改）其中元素的内容.

<br><br>

### 四、LinkedHashSet：[·](#目录)

1. 仅仅就是HashSet的基础上额外使用了一个链表记录了元素的插入顺序（add的顺序）.
  - 因此仍然是不可重复的.
2. **方法和HashSet的完全一样，没有添加任何额外的方法.**
  1. 仅仅就是遍历的时候会按照底层额外链表记录的元素插入顺序遍历.
  2. **仍然是hashCode和equals决定桶和槽**，只不过多了一个链表维护插入顺序而已.
3. 由于需要额外维护一张链表，因此**效率不如HashSet**.
