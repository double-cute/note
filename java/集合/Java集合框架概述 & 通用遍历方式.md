# Java集合框架概述 & 通用遍历方式
> Java集合**都重载了toString**方法，因此可以**直接打印其全部元素**.
>
>> 集合的两种通用遍历方式 **传统forEach** 和 **.forEach(Consumer)** 都是 **并发只读视角的** .

<br><br>

## 目录

1. [Java集合框架：无关联集合Collection & 关联集合Map](#一java集合框架无关联集合collection--关联集合map--)
2. [传统的forEach遍历](#二传统的foreach遍历)
3. [.forEach(Consumer)-lambda表达式遍历](#三foreachconsumer-lambda表达式遍历)

<br><br>

### 一、Java集合框架：无关联集合Collection & 关联集合Map  [·](#目录)
> 和Java数组不同，Java集合是可以存放多个元素的高级数据结构（数据容器），并且：
>
>   - 数组可以保存基本数据类型，而集合**只能存放引用类型数据**.
>     - 否则会编译报错，例如：ArrayList\<int\> list = new ArrayList\<\>();

<br>

**1.&nbsp; Java将集合分成两大类：**

1. 有关联关系的集合（即映射关系）：以**Map**为代表的根接口.
2. 无关联关系的集合：以**Collection**为代表的根接口，像链表、set之类的
  - 也就是说**Collection**和**Map**是所有Java集合的**根接口**，从它们俩派生出了所有Java集合实现类.

<br>

- **2.&nbsp;**  集合其实也是要求 **元素类型一致** 的，只不过它使用 **泛型** 来规定这种类型的一致.
  - 因此，**如果类型参数声明为Object，那么就可以存放任意类型了**.

<br>

- **3.&nbsp; 无关联 & 关联 集合框架一览：**

| Collection | 说明 | Map | 说明 |
| --- | --- | --- | --- |
| Set | 无序、不可重 | HashMap | 键无序 |
| List | 有序、可重复 | SortedMap | 键有序 |
| Queue | 队列（有序、可重复、FIFO）| EnumMap | 枚举Map |

<br><br>

### 二、传统的forEach遍历：[·](#目录)

```Java
for (type ele: 集合对象) {
	利用ele干一些事情...
}
```

<br><br>

### 三、.forEach(Consumer)-lambda表达式遍历：[·](#目录)
> 从Java 8开始，很多“集合类型”（不仅仅指Collection和Map，还有其它可以保存多个元素的数据类型）类提供了.forEach(Consumer)方法.
>
>   - 可以更加简洁地遍历集合.

<br>

- **1.&nbsp; 方法原型：**

```Java
class 集合<T> { // 将集合的迭代元素逐个交给action处理
    default void 集合类型对象.forEach(Consumer<? super T> action);
    // action是函数式接口，可以用lambda表达式定义操作迭代元素的动作
}

public interface Consumer<T> {
    void accept(T t);
}
```

<br>

- **2.&nbsp; lambda表达式简洁遍历集合：**

```Java
Collection c = ...;  
c.forEach(ele -> System.out.println(ele));
```
