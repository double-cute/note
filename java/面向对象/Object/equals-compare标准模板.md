# equals-compare标准模板
> 两者都必须满足数学上的**自反性**、**对称性**、**传递性**.
>
> 1. equals重写讲求的是内容完全一致.
> 2. compare重写讲求的同样是内容的大小可比.
>
>> 两者都提到了**内容**，而**内容**最为关键的一点就是：
>> - **运行时类型的完全一致**.
>>    - 数学上是由**对称性**导致的.
>>    - 这个要求来源于**Java集合**要求其中的元素类型一致.
>>    - 而**运行时类型一致**是Java集合的**最大夙求**.
>>       - 虽然**编译器只保证Java集合中元素的编译时类型一致**.

<br><br>

## 目录

1. [equals模板]()
2. [compare模板]()

<br><br>

### 一、equals模板：[·](#目录)
> equals在数学上必须满足**自反性、对称性、传递性**.
>
> - 内容相同最重要的一点就是**运行时类型完全**相同.
>   - 这其实是**对称性**决定的.
>   - obj1.equals(obj2) -> 有自反性推出 -> obj1 instanceof obj2 && obj2 instanceof obj1
>   - 最终得出 obj1 RuntimeClassEquals obj2
>
>> 这是一种逻辑上的严谨，爸爸和爸爸之间可以比较，但爸爸和儿子之间不能比较.
>>
>> - 很显然，爸爸和儿子在内容上相差很大.

<br>

- 比较策略（包括步骤）：
   1. 先看地址.
   2. 再看运行时类型（需要检查null）.
   3. 最后看感兴趣的数据成员（内容）.
      - 比较之前需要向下反转一发.

<br>

```Java
@Override
public boolean equals(Object obj) {
    // 1. 起手看地址
  if (this == obj) { // 由于equals的形参是obj因此不必单行this和obj有无is-a继承关系
    return true;
  }

  // 2. 接着看运行时类型（只有在obj不为null时才能调用getClass()
  if (obj != null && obj.getClass() == ThisType.class) {
    ThisType other = (ThisType)obj;  // 注意向下反转一发

    // 3. 最后看感兴趣的内容
    if (值相等逻辑成立) {
      return true;
    }
  }

  return false;
}
```

<br><br>

### 二、compare模板：[·](#目录)
> 注意**对称性**要求运行时类型相等才能比较.
>
>> 其次，**运行时类型不同** 的两种对象在逻辑上 **无法** 进行 **大小比较**.
>
>> - 设想，爸爸和儿子能比较大小吗？（即使两者有is-a继承关系）.
>> - 因此compare中遇到运行时类型不一致的情况应该抛出异常！
>>    - 不能返回特殊值，因为这是不可比（大小）的！这是一个严重的异常问题.

<br>

- 比较策略（包括步骤）：
   1. 先看地址.
   2. 首先检查null，因为null是没有大小这一概念的，因此是不可比的，必须抛出异常.
   3. 接着在比较运行时类型是否一致，不一致抛出异常.
   4. 最后看感兴趣的数据成员（内容）.

<br>

**1.&nbsp; 自然排序：**

```Java
@Override
public int compareTo(Object o) {
    // 1. 起手看地址
    if (this == o) { // compare的形参永远是Object因此不用担心this和o不是is-a继承关系
        return 0;
    }

    // 2. 检查null
    if (o != null && o.getClass() == ThisType.class) {
        ThisType other = (ThisType)obj;  // 注意向下反转一发

        // 比较感兴趣的内容
        return this.val - other.val;
    }
    else {
        抛出异常（空指针异常或者运行时类型不一致异常）
    }
}
```

- **精简版：用的最多**

```Java
public int compareTo(Object o) {
    if (this == o) {
        return 0;
    }

    // 运行时类型不一致会直接抛出强转异常的！
    ThisType other = (ThisType)obj;

    return this.val - other.val; // 如果other为null也会直接抛出空指针异常！
}
```

<br>

**2.&nbsp; 自然排序：**

```Java
// 定制排序都是模板方法，因此本身已经保证了运行时类型的一致
<T> int compare(T t1, T t2) {
    if (t1 == t2) {
        return 0;
    }

    // 可以抛出空指针异常
    return t1.val - t2.val;
}
```
