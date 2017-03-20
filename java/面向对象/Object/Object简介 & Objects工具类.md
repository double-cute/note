# Object简介 & Objects工具类
> 是所有Java类型（包括自定义类型）的父类.
>
> - 即**根类**，其中定义的方法会被所有Java类继到.
>    - 这些方法由于太基础和必须，基本上所有Java类都会使用到.
>    - 因此这些方法是**根方法**.
>
>> 由于Object太基础，其类核大多是由C语言编写（大多都是本地方法）.
>>
>>> 1. clone会在深拷贝一节中详细讲解.
>>> 2. finalize会在垃圾回收一章中详细讲解.
>>> 3. 线程同步方法会在多线程专题中详细讲解.
>>>
> Objects工具类主要用于对Java对象进行防null操作.
>
> - 在需要大量防null操作的场景中应用.

<br><br>

## 目录

1. [Object根方法一览]()
2. [获取运行时类型信息]()
3. [获取对象的hash值：hashCode]()
4. [转换成String信息：toString]()
5. [equals]()
6. [Object工具类]()

<br><br>

### 一、Object根方法一览：[·](#目录)

<br>

| 方法 | 说明 | 使用频率 |
| --- | --- | --- |
| toString() | 转换成String信息 | **最常用** |
| equals(Object obj) | 内容相等比较 | **特别常用** |
| hashCode() | 获得对象的hash值 | **较为常用** |
| getClass() | 获得对象的运行时类型 | **较为常用** |
| clone() | 深拷贝 | **依据场景，较为常用** |
| finalize() | 垃圾回收（在对象被回收前一刻调用）| **使用较少** |

<br>

- 线程同步方法：
   1. notify()
   2. notifyAll()
   3. wait([long timeout[, int nanos]])

<br>

- 小结：
   - 除了 **线程同步方法** 和 **getClass** 不能重写.
      - **其余方法都能** 被子类重写.

<br><br>

### 二、获取运行时类型信息：getClass()  [·](#目录)
> 由于获取的是**引用**的**运行时**类型信息.
>
> - 因此需要调用非常底层的JVM内核函数，因此是native本地方法（C语言）实现的.
> - 同时，由于获取运行时信息这一操作对于所有Java类型都一样，因此final不可重写.

<br>

```Java
public final native Class<?> getClass();

// 示例
Class clazz = "abc".getClass();  // 返回值时Class<?>通配泛型，因此只能使用Class接受
                                 // 不能使用Class<String>接受，否则编译错误
```

<br>

- 通过Class返回对象获取类型信息：都是**Class的对象方法**

```Java
// 1. 无包路径的类名
String getSimpleName();

// 2. 全限定类名（包含完整的包路径）
String getName();

// 3. "class/interface" + 全限定类名
String toString();
```

<br><br>

### 三、获取对象的hash值：hashCode  [·](#目录)
> 简单粗暴，直接返回JVM中对象的**虚拟内存地址**.
>
> - 并非物理地址.
>
>> - 同时获取虚拟地址这种操作非常底层，因此也是一个本地native调用.
>>
>>> 子类**可以重写**，用于**作为hash结构容器元素**时计算**桶的索引**.

<br>

```Java
public native int hashCode(); // Object单纯返回对象的JVM虚拟内存地址
```

<br><br>

### 四、转换成String信息：toString  [·](#目录)
> 单纯的返回 "全限定类名"" + "hashCode".
>
>> 可以重写用于描述对象本身的信息.

<br>

```Java
// Object的默认实现是 "全限定类名" + "hashCode的十六进制"
public String toString() {
    return getClass().getName() + "@" + Integer.toHexString(hashCode());
}
```

<br>

- 注意：
   1. 如果没有重写toString和hashCode的话，那么toString默认就是Object的：
      - "全限定类名" + "**虚拟内存地址** 的 十六进制"
   2. 如果重写了toString那就不用说了，完全按照自己的设计来.
   3. 如果之重写了hashCode而没有重写toString的话，那么：
      - 由于**自发性多态**的存在，使用toString输出时显示的是：
         - "全限定类名" + "**自己重写的** hash值 的 十六进制"

<br><br>

### 五、equals：[·](#目录)
> Object的equals比较的就是纯粹的虚拟内存地址.
>
>> 子类重写equals的原则是 **数据成员的内容一一对应相等**.

<br>

```Java
/**  
 *  - 不用担心==两边引用的类型没有is-a继承关系
 *    - 因为equals（不管是Object的版本还是重写的版本）
 *    - 其形参类型始终为Object！
 *      - 而任何类型都和Object具有is-a继承关系.
 */
public boolean equals(Object obj) {
  return (this == obj);
}
```

<br><br>

### 六、Object工具类：[·](#目录)
> 包含了Object中的大部分方法，但其主要目的就是**防null**.
>
> - 如果当一个引用为null时再调用方法会**抛出空指针异常**.
>
>> 里面提供的静态工具方法大都与防null有关.

<br>

- 方法一览：

| 方法 | 说明 |
| --- | --- |
| isNull(obj)、nonNull(obj) | 空/非空判断 |
| requireNonNull(obj[, message]) | 非空检验（异常）|
| toString(obj[, nullDefault]) | 防null的toString |
| hashCode(obj) | 防null的hashCode |
| equals(o1, o2)、compare(o1, o2, cmp) | 防null的比较 |

<br>

**1.&nbsp; 判空/非空：**

- 虽然和==、!= null等价，但是在代码逻辑上更加强调空和非空的判断问题.

```Java
// 1. 判断是否为空
public static boolean isNull(Object obj) {
  return obj == null;
}

// 2. 判断是否为非空
public static boolean nonNull(Object obj) {
  return obj != null;
}
```

<br>

**2.&nbsp; 非空检验：**

```Java
/**  检测一个引用是否为null，如果为null则强行抛出异常，否则什么都不做返回引用
 *   - 可以选择性定制一个异常信息来显示
 *   - 等价于 assert obj != null: [message]
 */
public static <T> T requireNonNull(T obj[, String message]) {
  if (obj == null)
    throw new NullPointerException([message]);
  return obj;
}
```

<br>

**3.&nbsp; 防null的toString：**

```Java
// 1. null返回"null"
public static String toString(Object o) {
  return String.valueOf(o);
}

// 2. null返回指定的默认String
public static String toString(Object o, String nullDefault) {
  return (o != null) ? o.toString() : nullDefault;
}
```

<br>

**4.&nbsp; 防null的hashCode：**

```Java
// null返回0
public static int hashCode(Object o) {
  return o != null ? o.hashCode() : 0;
}
```

<br>

**5.&nbsp; 防null的equals：**

- 避免Object主调的equals在null的情况下抛出空指针异常.

```Java
public static boolean equals(Object a, Object b) {
  return (a == b) || (a != null && a.equals(b));
}
```

<br>

**6.&nbsp; 泛型防null的定制compare：** 只有定制版本

```Java
public static <T> int compare(T a, T b, Comparator<? super T> c) {
  return (a == b) ? 0 :  c.compare(a, b);
}
```
