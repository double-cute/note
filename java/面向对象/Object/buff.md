2. equals方法的重写模板：
@Override
public boolean equals(Object obj) {
  if (this == obj) { // 先比较地址
    return true;
  }

  if (obj != null && obj.getClass() == ThisType.class) { // other不为空，且类型严格相同再比较值
    ThisType other = (ThisType)obj;
    if (值相等逻辑成立) {
      return true;
    }
  }

  return false;
}
！！equals的正规定义就是：自反性、对称性、传递性，在值满足这三个性质的同时，类型也要满足这三个性质，因此这里使用了obj.getClass() == ThisType.class的严格类型相等，而不是使用obj instanceof ThisType！

### 五、compare的实现原则：



    4) 示例：
class R implements Comparable {
	int val;

	public R(int val) {
		this.val = val;
	}

	@Override
	public String toString() {
		return "R[val:" + val + "]";
	}

	@Override
	public boolean equals(Object obj) { // equals的标准写法
		if (this == obj) { // 先比较地址
			return true;
		}

		if (obj != null && obj.getClass() == R.class) { // 再比较类型（前提是obj不能为空）
			R r = (R)obj; // 先把类型调整一致（当然可以直接return this.val == ((R)obj).val
						  // 但如果在return之前还要用obj进行一些其它复杂操作那用临时的类型转换太麻烦了
			              // 因此先协调类型才是最标准最合理的做法
			return this.val == r.val;
		}

		return false; // 地址不同 || obj为空 || 类型不一致，那肯定不一样了！
	}

	@Override
	public int compareTo(Object o) { // compareTo的标准写法
		if (this == o) { // 第一步和equals一样，还是先比较地址，这是最基本也是最容易想到的
			return 0;
		}

		if (o == null) { // 由于compareTo比较的是大小，而null是没法比较大小的，因此null就得抛异常
			             // 这里只是个演示，就不实现这一块了
			// 抛出异常
		}

		R r = (R)o; // 不判断类型是否一致，不一致直接抛出异常！就是要暴露异常方便调试（集合中元素都应该保证类型相同）

		return this.val - r.val; // 正常的操作和返回
	}
}

public class Test {

	public static void main(String[] args) {
		TreeMap map = new TreeMap();

		map.put(new R(9), "aaa");
		map.put(new R(5), "bbb");
		map.put(new R(-3), "ccc");
		System.out.println(map); // key: -3, 5, 9

		System.out.println(map.firstEntry()); // key: -3
		System.out.println(map.lastKey()); // key: 9
		System.out.println(map.higherKey(new R(2))); // key: 5
		System.out.println(map.lowerEntry(new R(1))); // key: -3
		System.out.println(map.subMap(new R(-1), new R(10))); // key: 5, 9
	}
}

1. Object类简介：
    1) 是Java所有类型的基类，如果一个自定义的类没有extends显示指定其父类则它默认继承Object类；
    2) 常用方法（通常需要根据需求覆盖，Object本身对它们的定义极其简单）：
         i. 相等判断：
public boolean equals(Object obj) {
    return (this == obj);
}
！！可以看到就是简单的判断是否地址相同；
         ii. 哈希值：public native int hashCode();  // 默认用地址值计算，和地址一一对应
         iii. 返回运行时类：public final native Class<?> getClass();
         iv. 返回字符串表示：
public String toString() {
    return getClass().getName() + "@" + Integer.toHexString(hashCode());
}
！！可以看到原始的字符串信息就是"类名@哈希值的16进制表示"；
    3) 上面这些方法除了getClass不能覆盖之外其余都可以覆盖！！
    4) 其它几个常用方法（之前用过）：
         i. finalize：回收之前调用；
         ii. wait、notify、notifyAll：用于线程通信；

2. 自我克隆简介：clone
    1) 是Object中提供的一种最基础的工具，可以实现对象的自我克隆，和C++的拷贝构造函数是一个道理；
    2) 原型：protected native Object Object.clone() throws CloneNotSupportedException;
    3) 该方法的目的是克隆对象的整个内存空间（克隆出的对象和原对象在内存中是相互隔离的），和C++一样同样存在浅拷贝和深拷贝的问题；
    4) 如果一个类想要实现克隆的功能就必须实现该方法，因为它在Object中是protected，即对外界是隐藏的，只能在子类中使用；
    5) 如何实现克隆呢？需要以下的步骤完成：
         i. 实现类要implements一个Cloneable接口，该接口是一个标记接口，里面没东西；
！！标记接口是给人看的，而不是给编译器看的，仅仅就是用来提醒类库的使用者，该类已经实现了自我克隆的功能，可以放心使用了！仅此而已！只是增强可读性而已；
         ii. 自己重写clone方法，一般clone是要对外开放的，因此重新的时候要把protected改成public！
         iii. 在clone的实现中不用像C++那样非常麻烦地将每个成员都复制给克隆对象一个，只需要调用Object基类的clone方法即可，即：
@Override
public Object clone() throws CloneNotSupportedException {
	// TODO Auto-generated method stub
	return super.clone();
}
！就可以实现克隆了！
    6) 为什么？？为什么Object的clone方法那么神奇？都不需要自己手写派生部分的克隆吗？这里我们要讲解以下Object的clone的原理，其实它是这样实现的：
protected native Object clone() throws CloneNotSupportedException {
	getClass一下得到当前的运行时类！  // 即使你是子类也是可以辨别出来的！
	由于在编程语言中类型决定了对象内存空间的大小，因此就决定了要拷贝的空间的大小len了；
	然后在获得this的地址；
	然后将this开始的len大小的内存空间赋值到一个新的len大小的内存空间中；
	最后返回新内存空间的指针（引用）即可；
}
！！也就是说它根据类型确定空间的大小，然后就赋值这么大的内存空间即可（即对象的纯数据区），因此还是非常智能的；
！！由于它是根据内存空间总大小一次性复制，因此其效率特别高（一次性复制类似于C语言的memcpy）；
    7) 但是Object的这种clone就有一个非常大的隐患，那就是浅复制：如果对象中还包含其它对象，那么拷贝的时候仅仅拷贝的就是一个指针值，而这两个对象的该指针指向的还是同一段内存，其指向的区域并没有隔离，因此需要自己解决深复制的问题；

3. 深拷贝：
    1) 深拷贝必须由开发者自行实现，而实现的方法就是“递归”克隆；
    2) 递归克隆的要领：总的来说有两点
         i. 对象成员必须也实现了Cloneable接口（这里的实现必须是实现clone方法）；
         ii. 在外层对象的clone方法中显示调用该对象成员的clone方法；
    3) 模板示例：
class MemObj implements Cloneable {
	// 数据定义...

	@Override
	public Object clone() throws CloneNotSupportedException {
		// 具体实现，保证该类本身就是深拷贝
	}

}

class Obj implements Cloneable {
	// 基本类型数据定义
	MemObj memObj; // 引用类型数据定义

	@Override
	public Object clone() throws CloneNotSupportedException {
		Obj obj = null;

		try {
			obj = (Obj)super.clone(); // 先暂时获取一个浅拷贝的对象
		}
		catch (CloneNotSupportedException e) { // 拷贝过程中可能出现异常
			e.printStackTrace();
		}

		// 如果没有引用类型成员该句就不用，其它的所有代码都是模板代码
		obj.memObj = (MemObj)memObj.clone(); // 再利用成员对象已经实现好的深拷贝clone单独拷贝该成员

		return obj; // 最终返回
	}


}
！！可以看到上面的定义是一种递归定义，一个对象要能深拷贝就必须保证其成员也能深拷贝；

    4) Java类库中有些类（引用类型的）可以像int等基本类型一样可以直接用Object的clone进行深拷贝！！
         i. String就是一个典型的例子，如果包含它的类想要实现深拷贝就直接super.clone就行了，无需递归显式调用String成员对象的clone！
         ii. 这种类在底层其实已被Java转化成了基本数据类型；
         iii. 那应该怎样辨别Java类库中哪些类可以自动深拷贝（像String一样）哪些由跟普通用户自定义类一样需要递归深拷贝呢？
         iv. 方法很简单：只要该类的clone方法是可见的，那就必须显式递归深拷贝，如果clone方法是不可见的那就可以自动深拷贝（super.clone就行了）；
         v. 示例：
class A implements Cloneable {
	public String name; // clone可见，因此可以自动深拷贝
	public int[] arr = {1, 2, 3, 4, 5}; // 数组类型的clone不可见，必须手动递归深拷贝

	public A (String name) {
		this.name = name;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		A a = (A)super.clone();
		a.arr = arr.clone(); // arr必须手动调用clone深拷贝！！
		return a;
	}

}

class B implements Cloneable { // 再嵌套一层
	public A a;
	public B(A a) {
		this.a = a;
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		B b = (B)super.clone();
		b.a = (A)a.clone();
		return b;
	}

}

public class Test {

	public static void main(String[] args) throws FileNotFoundException, IOException, CloneNotSupportedException {
		B b1 = new B(new A("lala"));
		B b2 = (B)b1.clone();
		b1.a.name = "xxx";
		b1.a.arr[0] = 999;
		System.out.println(b2.a.name); // lala
		System.out.println(Arrays.toString(b2.a.arr)); // 1, 2, 3, 4, 5 第一个并不是999，说明内存隔离
		System.out.println(b1.a == b2.a); // false，引用成员a的内存也是隔离的
	}
}


4. Objects工具类：
    1) 里面包含了和Object一样的方法，只不过都是静态的工具方法，并且参数是一个Object对象；
    2) 其目的就是防止空指针异常，设想你要调用一个对象的toString方法，如果该对象引用还是一个null那岂不是会抛出空指针异常吗？
    3) Objects就是为了解决当对象调用Object基础方法时发生的空指针异常问题！！
    4) Object类中对应的几个：如果参数就是null的，那么将返回null
         i. static String toString(Object o);  // 内部调用o的toString方法，只不过空指针安全
         ii. static int hashCode(Object o);  // 内部调用o的hashCode方法，只不过空指针时返回0
    5) 其它几个常用的工具方法：
         i. 非空赋值：static <T> T requireNonNull(T obj);  // 要求obj不为空，否则抛出异常，如果不为空就返回obj本身
！！！类似于C++的assert先判断一个东西不为空，只有不为空的时候才能拿它赋值：
Obj obj = Objects.requireNonNull(otherObj);

// 相当于
assert(otherObj);
obj = otherObj;
         ii. 标准非空判断：static boolean isNull(Object obj);  // 比obj == null看上去更加正规！
！！其实其实现就是：
public static boolean isNull(Object obj) {
    return obj == null;
}
