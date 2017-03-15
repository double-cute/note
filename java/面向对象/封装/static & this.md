# static & this
> - 什么是成员？
>   1. 成员包括4个部分：数据成员、默认值初始化的位置、初始化块、方法
>   2. 成员分两类，一类是对象成员，一类是类成员（静态成员）.
>     - 它俩都包含这4个部分.
>
>> 1. this指向对象自身，可以在内部通过this引用访问自己.
>> 2. 用static修饰的成员**属于类而不属于对象**.
>>   - 因此访问时应该**使用类名而不是对象名**.

<br><br>

## 目录

1. [this]()
2. [static]()
3. [省略this和类名访问成员]()
4. [类成员和对象成员的访问规范]()

<br><br>

### 一、this：[·](#目录)
> 和C++的this指针一样（只不过Java的this是引用），用来**表示当前的对象本身**.
>
>> 可以方便在**对象内部**（方法、块）中通过this来**访问自身**，例如：this.func(a, b);
>>
>> - this和其它普通的引用一样，只不过它指向当前对象本身.
>>
>>> 接下来介绍this的常见用法.

<br>

**1.&nbsp; 形参名和数据成员名重名：**

```Java
Person(String name, int age) {
    this.name = name;
    this.age = age;
}
```

<br>

**2.&nbsp; 返回this（对象本身）：**

```Java
public MyType func() {
    return this;
}
```

<br><br>

### 二、static：[·](#目录)
> 用static修饰的成员（数据 、方法）**属于类而不属于对象**，称为**类成员**或**静态成员**.
> - 而没有static修饰的成员都属于对象.

<br>

**1.&nbsp; 访问类成员（静态成员）应该使用类名：**

```Java
class MyType { ... }
MyType.static_func("test");
MyType.INT_FLOOR = 10;
```

<br>

**2.&nbsp; 对象方法和类方法的本质区别：** 涉及到**对象的存储模型**

1. 对象的内存空间里**只保存成员数据不保存成员方法**！
  - 对象有几个，成员数据就有几份.
  - 和类成员（静态成员）一样，所有的对象方法只有一份，所有对象都共享它.
2. 如何共享？
  - 对象方法有一个隐藏参数this（隐式地作为第一个参数传入），调用时用主调的对象作为实参隐式传入.
  - 例如：
    - 定义：class A { public void f(int x); }  -\>  public void f(A this, int x);
    - 调用：A a = ...; a.f(10);  -\>  f(a, 10);
  - 也就是说，this确定了调用该方法的对象是谁.
3. 为什么类方法不能调用对象方法？
  - **类方法没有隐式的this引用参数**（是直接通过类名调用的）.
  - 而对象方法必须要有this参数.
  - 两则矛盾，**静态方法并不能为对象方法提供this实参**，因此不能调用（否则编译报错！）.

<br><br>

### 三、省略this和类名访问成员：[·](#目录)
> Java允许在类的代码块和方法（静态和非静态都行）中访问成员（静态和非静态都行）不加this或者类名前缀.

<br>

**1.&nbsp; 在静态方法和静态块（静态初始化块和静态默认值处）中省略前缀：**

- 编译器会默认在访问的成员之前隐式加上 **类型.** 的前缀.

```Java
class MyType {
    private static int a = f();  ->  private int a = MyType.f();
    private static int b;

    static {
        b = f();  -> MyType.b = MyType.f();
    }

    public static void func() {
        f();  ->  MyType.f();
    }
}
```

<br>

**2.&nbsp; 在对象方法和对象块（初始化块和默认值处）中省略前缀：**

- 编译器会默认在访问的成员之前隐式加上 **this.** 的前缀.

```Java
class MyType {
    private int a = f();  ->  private int a = this.f();
    private int b;

    {
        b = f();  -> b = this.f();
    }

    public void func() {
        f();  ->  this.f();
    }
}
```

<br>

**3.&nbsp; 正是由于2.的原因，导致允许使用对象引用来访问静态成员：**

- 毕竟，在对象成员中是可以访问静态成员的（静态属于大家的，大家都能用是理所当然的）.
  - 而在对象中省略前缀默认是补上**this.**
    - 而this是对象引用.
      - 因此，如果访问的是静态成员，那么就必须让这样的语法能自圆其说，所以就开了这个不太好的后门了.

```Java
class MyType {
    private static int a;
    public void set(int val) {
        a = val;    
            ->  this.a = val;  
                ->  a是静态成员  
                    ->  那就必须允许通过对象引用访问静态成员的语法成立才行！
    }
}
```

- 因此，**可以随意使用对象引用来访问静态成员**.
  - 即使对象成员为null也行！

```Java
class MyType {
    public static int a;
    public static void f() {}
}

MyType.a = 10; MyType.f();  // 通过类名访问本身就是正确的，不用说了

MyType obj = new MyType();
obj.a = 10; obj.f();  // 通过对象引用访问也正确

// 甚至对象引用为null也行
MyType obj = null;
obj.a = 10; obj.f();  // 正确！
```

<br><br>

### 四、类成员和对象成员的访问规范：[·](#目录)

<br>

1. 对象成员可以访问类成员，但是类成员不能访问对象成员.
  - 这是一定的，否则编译报错.
2. 访问类成员一定要通过 **类名.** 的前缀访问，不要用对象引用来访问.
  - 在对象中（对象方法、对象块）也最好使用类名前缀来访问类成员.
    1. 不是强制的，但这是一个良好的习惯，可以使代码更健壮.
    2. 用类名前缀可以一眼看出这是个类成员.
    3. 也看到了，当对象引用为null时都可以正常访问类成员，更加容易造成误解.
