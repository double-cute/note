# 深拷贝：clone
> 等价于复制构造函数.

<br><br>

## 目录

1. []()
2. []()

<br><br>

### 一、Object的clone的底层实现：
> 实际是浅克隆（值克隆），但不过**重新开辟**一段**对象内存空间**.
>
> - 只不过拷贝的仅仅是**基本类型变量的值**和**引用类型变量的值**（即**指针值**，即**指向**）.
>    - **并不拷贝指向的对象的内容**.
>    - 由于拷贝的仅仅是指向，因此拷贝出的副本中的引用和本体中的引用指向同一个对象成员的内存空间.
>       - 因此是浅拷贝.
>    - 其实现就是**C语言的memcpy**，简单粗暴地将对象**内存拷贝一遍**.
>
>> - 换句话说，Object的clone的默认实现是 **深拷贝变量的值（包括指针的指向）**.
>>    - **但浅拷贝指针指向的对象成员.**

<br>

- 由于底层是系统调用的memcpy，因此Object的clone实现是native本地调用.
   - 这里先介绍它的具体实现细节.

```Java
protected native Object clone() throws CloneNotSupportedException {
  1. 先 [this.getClass] 看一下自己的运行时类型.
	2. 和C语言一样，类型决定了内存空间的大小，因此就得到 [memcpy的size] 了.
  3. malloc一段 [新的] size大小的空间.
  4. 将this内存空间memcpy到新空间中去.
  5. 最后返回新空间的指针.

  // C语言描述
  int size = sizeof(this.getClass());
  Object obj = (Object)malloc(size);
  memcpy(obj, this, size);
  return obj;
}
```

- 很明显的看出 **Object的默认是clone实现** 仅仅是 **值的深拷贝 & 指向内容的浅拷贝**.

<br><br>

### 二、如何重写实现自己的深拷贝？
> 毕竟自己去调用系统函数memcpy这些太复杂太麻烦，但可以再好好体会一下Object的默认clone实现吗？
>
>> 虽然是 **指向内容的浅拷贝** 但确实 **值的深拷贝** 啊！
>>
>> - 也就是说还是有深拷贝的咯！

<br>

**1.&nbsp; 大致思路：** 最底层调用Object的clone

1. 不管一个对象的类型有多么复杂（对象嵌套对象、嵌套集合数组等，而且是多层嵌套）.
2. 但一直分解到**最底层**，一定还是**由基本类型数据构成**的！
   - 示例：Family 包含 Father 包含 Name 包含 String lastName，而String底层是由char构成的.
      - 最底层还是char这种基本类型数据.
3. 而Object默认的clone实现就是对 **值（基本类型数据）** 的深拷贝.

- 总结：最底层的那一环（**只包含基本类型成员**），直接调用Object的clone就能保证是深拷贝.

<br>

**2.&nbsp; 深拷贝向上传递：**

1. 上一小节已经保证了最底层重写的clone是深拷贝了.
2. 那么接下来只要将深拷贝一层层向上传递就行了.
   - 在上一层重写的clone中调用这一层的clone（已经保证了这一层的clone是深拷贝了）.
   - 一直传递到最高层为止.

<br>

**3.&nbsp; 注意重写clone时的 访问控制符 以及 CloneNotSupportedException异常：**

> 总结成一句话就是 **重写clone一定要改成public**.

1. 访问控制符：**重写改成public**
   - 由于Object的默认实现中定义clone的是 **protected** 的.
      - 这就意味着，只能在 **java.lang包** 以及 **包外的子类内部** 是可见的.
      - 而平时应用clone方法的范围都在以上这两个范围之外.
         - 因此**重写时一定要将访问控制符改为public**.
         - 否则**外部不可见**.
2. CloneNotSupportedException异常 其实是由 重写的访问控制符引起的：
   - Object的clone默认实现中的 **第一句其实是**：
      - 检查 clone的重写版本控制符 **是否≥** 当前this引用的访问范围.
      - 如果结果是false，就抛出 **[CloneNotSupportedException异常]**.
         - 其实是 **clone对外界不可见** 所造成的，只不过这里强制抛出异常来强调这个问题罢了.
   - 示例：MyClass重写了clone，且控制符仍然是protected，然后在java.lang包外new MyClass().clone()
      1. new出来的MyClass引用this的范围是public的，但clone的重写版本控制符是protected的.
      2. protected ≥ public返回的是false，因此clone不可见，所以这里会抛出[CloneNotSupportedException异常].

- 重新审视Object的clone的实现：

```Java
protected native Object clone() throws CloneNotSupportedException {
  // C语言描述

  if (this的范围 > clone重写版本的访问控制范围) {
    throw new CloneNotSupportedException();
  }

  int size = sizeof(this.getClass());
  Object obj = (Object)malloc(size);
  memcpy(obj, this, size);
  return obj;
}
```

<br>

**4.&nbsp; Cloneable标记接口：**

- Java提供了一个**空的**Cloneable接口，里面什么东西都没有.
   - 因此**只有标记的作用**，没有实际的强制实现作用.
- Java规范要求，所有**实现了深拷贝的类**都应该implements一下Cloneable接口**以示人**.
   - 也就是说标记接口是给人看的，不是给编译器看的.
      - 毕竟实现Cloneable接口并不强制要求要重写clone方法，不重写编译器也不会报错.
         - 但这不是一个良好的习惯.

<br>

**5.&nbsp; 深拷贝实现总结：**

1. implements Cloneable标记接口以示人. （良好的编程规范）
2. 重写clone方法，将访问控制符改为public. （避免外界不可见而无法调用）
3. 实现深拷贝算法：对于引用类型，将最底层（只有基本类型成员）的深拷贝向上传递.
   - 基本类型的深拷贝直接调用Object的clone即可.

<br>

**6.&nbsp; Java核心类库中已经实现深拷贝的类 以及 没有实现深拷贝的类：**

> 记住！Java核心类库中那些没有实现深拷贝的类的clone方法都是不可见的！（仍然是protected的）
>
>> 原则是：**没实现深拷贝就不能暴露给外界去害人.**
>>
>> - 设想，如果没实现深拷贝（只是浅拷贝）还能暴露给外界的话，外界可能会误认为是深拷贝，结果无用导致错误.
>> - 因此，自定义类型时，如果不想提供深拷贝功能，那就不要重写，保持原来的protected控制符.

<br>

- 实现了深拷贝的Java核心类：**数组** 等
   - 自定义类包含它们时，直接调用它们的clone方法就可以完成深拷贝.
- 没有实现深拷贝的Java核心类：**所有包装器类型**、**String** 等.
   - **记住！** 只要是Java核心类，如果没有提供深拷贝clone，那一定会提供**复制构造函数**.
   - 如果自定义类包含它们时，必须**调用复制构造函数以完成深拷贝**.

<br>

**7.&nbsp; 示例：**

```Java
class Bar implements Cloneable { // 别忘了标记Cloneable
	// 最底层只包含基本类型
	int a = 10;
	double d = 1.1;

	@Override  // 别忘了改成public
	public Object clone() throws CloneNotSupportedException {
		// 只包含基本类型的最底层成员直接调用Object的clone就可以完成深拷贝
		return super.clone();
	}
}

class Foo implements Cloneable { // 别忘了标记Cloneable
	// 1. 基本类型：直接依靠Object的clone就能完成深拷贝
	int fa = 20;  
	// 2. 实现了深拷贝的Java核心类：直接调用其重写的深拷贝clone以完成深拷贝
	int[] arr = new int[]{ 1, 2, 3, 4, 5 };
	// 3. 没有暴露给外界深拷贝的Java核心类：需要调用复制构造函数以完成深拷贝
	Integer i = new Integer(11);
	String s = new String("abc");
	// 4. 自定义的引用类型：必须保证其实现（重写）了深拷贝clone，然后调用其重写的clone以完成深拷贝.
	Bar bar = new Bar();

	@Override  // 别忘了改成public
	public Object clone() throws CloneNotSupportedException {

		// 先深拷贝基本类型（fa），浅拷贝引用类型（arr、i、s、bar）
		Foo cp = (Foo)super.clone();

		// 接着深拷贝引用类型
		cp.arr = (int[])this.arr.clone();  // 实现了深拷贝的Java核心类
		cp.i = new Integer(this.i);  // 没有实现深拷贝的Java核心类（复制构造函数）
		cp.s = new String(this.s);
			// 如果自定义引用类型没有暴露clone会抛出CloneNotSupportedException异常
		cp.bar = (Bar)this.bar.clone(); // clone没有暴露给外界的话会抛出异常

		return cp;
	}
}
```

<br><br>

### 三、复制构造函数：
> 复制构造函数这种东西毕竟是C++传承过来的，Java沿用这个概念也是为了照顾C++开发者.
>
>> Java不建议设计复制构造函数这种东西，Java建议复制构造函数和clone之间只保留一个，不要同时都暴露给外界.
>>
>>> 如果非要实现复制构造函数的话最好是：
>>>
>>> 1. 先重写深拷贝clone.
>>> 2. 然后在复制构造函数中调用clone（并在内部处理异常）.
>>>    - 因为一般构造器没有抛出异常的习惯.
>>> 3. 隐藏clone（protected），暴露复制构造函数（public）.

<br>

- 接上例：

```Java
// 暴露复制构造函数（public），同时clone最好是对外界隐藏
public Foo(Foo obj) {
	Foo cp = null;
	try {
    // this = (Foo)obj.clone();  // 错误！this不可修改！
		cp = (Foo)obj.clone();
	}
	catch (CloneNotSupportedException e) {
		e.printStackTrace();
	}

	this.fa = cp.fa;
	this.arr = cp.arr;
	this.i = cp.i;
	this.s = cp.s;
	this.bar = cp.bar;
}
```
