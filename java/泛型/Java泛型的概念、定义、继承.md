# Java泛型的概念、定义、继承
> Java设计泛型的初衷：
>
>   - 主要是为了解决Java容器**无法记忆元素类型**的老问题：
>     - 由于Java设计之初并不知道会往容器中存放什么类型的元素，因此**容器元素类型都规定为Object**，这样就什么东西都能放了！
>     - 但是笼统的Object规定方案有明显的缺陷：
>       1. 取出元素后必须进行强制类型转换（使之编译时类型和运行时类型一致，**对象引用能调用什么方法是由其编译时类型决定的**）.
>         - 由于没有类型就需要在很多地方进行强制类型转换，这不仅增加了编程的复杂度同时也不美观（臃肿），加大了代码维护难度.
>         - 因此是个麻烦事.
>       2. 如果不小心往集合里加了不同类型的元素可能会导致类型异常的问题（equals、compare时容易犯错）.
>         - 因此是个容易出错的事.
>
>> 泛型最主要就是为了解决这个问题，但不过现在泛型的应用更加广泛，已经远远超出了记忆容器元素类型的作用.

<br><br>

## 目录

1. [Java泛型的概念]()
2. [创建泛型对象：菱形语法 & 自动类型推断]()
3. [定义泛型类、接口]()
4. [继承泛型：定义和使用泛型的区别]()
5. [继承类型参数]()
6. [继承关系的传递问题]()

<br><br>

### 一、Java泛型的概念：[·](#目录)

- 背景介绍：Java 5的一个重大改变，引入了泛型
  1. 引入了**参数化类型（Parameterized Type）** 的概念，即泛型.
  2. 改造了所有的Java集合，使之都实现泛型.
    - 它允许程序**在创建集合时指定集合元素的类型**，比如List<String>就声明了一个**只能**存放String类型元素的List集合.

<br>

- **泛型**和**类型参数**：
  1. 泛型（Generic）：就是指**参数化类型**.
    - 上面的List<String>就是参数化类型，即泛型.
  2. 类型参数：就是指泛型中的可变参数类型.
    - String就是List<String>泛型的类型参数.
    - 即<>中的类型，即**菱形语法**.

<br>

- 应用泛型的**好处**：
  1. 取（集合可以记忆元素的类型）：取出的元素**无需强制类型转化**就可以用它的**运行时类型引用**接收.
  2. 存（严格检查类型兼容）：一旦指定了泛型的类型参数就不能往里面添加其它冲突类型的元素.
    - 上述两点都是在**编译时**进行检查的，大大**保障了运行时的类型安全**.
    - 具体说，泛型保证了**运行时一定不会发生类型转化异常（ClassCastException）** .
    - Java的泛型**屌就屌在编译时就能搞定一切**.

<br><br>

### 二、创建泛型对象：菱形语法 & 自动类型推断  [·](#目录)

1. **泛型引用** 的概念：即用泛型声明的引用
  - 例如：ArrayList<String> list;   // list就是一个泛型引用
2. **泛型对象** 的概念：即用泛型构造器构造出来的对象
  - 例如：new ArrayList<String>();

- 这两个定义都有一个共同特点：**必定要有<>菱形，不包含<>就跟泛型无关！！**

<br>

- 创建泛型对象时的**自动推断**功能：泛型对象可以根据接受它的泛型引用自动推断出自己的类型参数
  - 例如：ArrayList<String> list = new ArrayList<>();
  - 即**泛型对象的类型参数可以不写**，会自动根据**接受它的泛型引用的类型参数**推断.
    - 当然**写上肯定是没问题的**，如：ArrayList<String> list = new ArrayList<String>();  // 完全正确

<br>

- 但不能用无类型参数的引用去接受有类型参数的泛型对象：**只能正向推断，不能反向推断**
  - 例如：
    1. ArrayList<> list = new ArrayList<String>();  // 不能反向推断
    2. ArrayList list = new ArrayList<String>();  // list根本都不是泛型引用，没有用<>声明

<br>

- 应用示例：

```Java
public class Test {

	public static void main(String[] args) {
		ArrayList<String> list = new ArrayList<>();
		list.add("lala");
		list.add("haha");
		// list.add(5); // 类型不符，直接报错！！
		list.forEach(ele -> System.out.println(ele)); // 可以看到取出的ele无需强制类型转换，直接就是String类型的
		// 说明泛型集合能记住元素的类型，代码简洁了很多

		HashMap<String, Integer> map = new HashMap<>();
		map.put("abc", 15);
		map.put("def", 88);
		map.forEach((key, value) -> System.out.println(key + " : " + value)); // 可以看到key、value同样无需强制类型转化
	}
}
```

<br><br>

### 三、定义泛型类、接口：[·](#目录)
> 不仅Java的集合都定义成了泛型，用户自己也可以定义任意泛型类、接口.
>
>> 只要在定义时用<>来指定类型参数即可.

1. **类型参数名** 可以自己随意取：
  - 例如：public class Fruit<T> { ... }.
  - 其中<T>指定了该泛型的类型参数，T是开发者自行命名**类型参数名**.
2. 实例化泛型：给定一个**具体的Java类型**代替**类型参数名**从而**产生一个实例化的泛型对象**
  - 其实就是**用具体的Java类型**替换定义泛型时的**类型参数名（T）** .
  - 示例：Fruit<String> fruit = new Fruit<>(...);  // fruit就指向了一个实例化的泛型对象
3. 类型参数可以在**整个**类、接口内**当成普通Java类型使用**.
  - **所有可以使用普通Java类型的地方都可以使用类型参数**.
  - 甚至可以**嵌套**使用泛型！！

```Java
public interface MyGneric<E> {
    E add(E val);
    Set<E> makeSet();  // 嵌套使用泛型！
    ...
}
```

- **泛型构造器：**
  1. 定义泛型构造器时**不用菱形语法**，否则会**编译错误**.
  2. 利用泛型构造器new泛型对象时是肯定要使用菱形语法的.

```Java
public class MyGenric<T> {
    MyGeneric(...) { ... }  // 定义泛型构造器不用菱形语法
    MyGeneric<T>(...) { ... }  // 编译错误！
    ...
}
```

<br><br>

### 四、继承泛型：定义和使用泛型的区别  [·](#目录)
> 继承泛型：**泛型或者不是泛型**的子类继承**可变类型参数实例化** 的泛型父类.

- 定义泛型 & 使用泛型：
  1. 定义泛型：即定义泛型类以及接口
    - 最大的特点就是类型参数作为可变参数使用，<T>，T可以代表任何类型.
  2. 使用泛型：创建泛型对象等
    - 特点就是必须用真实的Java类型来代替类型参数.

<br>

- **继承泛型是一种对泛型的使用：**
  - 也就是说**可变类型参数无法被继承**.
    - **原则上，任何编程语言都不允许泛型模板层层继承！！**
  - 也就是说**只有实例化的泛型才能被继承**.
- 示例：

```Java
public class MyType extends MyGeneric<String> { ... }  // 正确，子类不是泛型
public class MyType extends MyGeneric<T> { ... }  // 错误！继承泛型其实是使用泛型作为一个父类，因此类型参数必须实例化
public interface MyType<T> extends MyGeneric<String> { ... }  // 正确，子类是泛型
```

<br>

- 从父亲那里继承来的所有类型参数也被实例化了.
  - 因此在儿子中使用这些继承来的类型参数必须要使用实例化的类型，不能再继续使用泛型的可变类型参数了.
  - 例如：

```Java
class Father<T> {
	T info;
	public Father(T info) {
		this.info = info;
	}
	public T get() {
		return info;
	}
	public T set(T info) {
		T oldInfo = this.info;
		this.info = info;
		return this.info;
	}
}

class Son extends Father<String> { // 从父类继承来的类型参数已经实例化为String了
	// 因此在子类中使用它们的时候全部都要使用实例化的类型！

	public Son(String info) {
		super(info);
	}

	@Override
	public String get() {
		return "haha";
	}

	@Override
	public String set(String info) {
		return "lala";
	}

}

class Daughter<T> extends Father<String> { // 但子类自己还可以继续定义为泛型
    public Daughter(String info) {
		super(info);
	}

	@Override
	public String get() {
		return "haha";
	}

	@Override
	public String set(String info) {
		return "lala";
	}

    // 拥有自己的可变类型参数T
    T girlsInfo;
    public T getGirlsInfo() {
        return girlsInfo;
    }
}
```

<br><br>

### 五、继承类型参数：[·](#目录)
> 继承类型参数：子类和父类**都是非实例化的泛型**，并且子类拥有和父类**相同的可变类型参数**.
>
>> 完全传承父类泛型，最为纯粹的扩展.

- 示例：

```Java
class Father<T> { ... }
class Son<T> extends Father<T> { ... }  // 父类怎么使用T，子类就怎么使用T
```

<br>

- 三种常见的编译时错误：在进行**类型参数继承**时Java编译器**严格记忆**定义**父泛型**时的**类型参数名**.
  - 简单来说：
    1. 父子的类型参数名一定要相同，这个不用解释了，否则怎么叫类型参数的继承呢.
    2. **父泛型在派生子类的时候，类型参数名一定要跟定义时保持一致.**

```Java
class Father<T> { }
class Son<E> extends Father<T> { }  // 错误1

class Father<T> { }
class Son<T> extends Father<E> { }  // 错误2

class Father<T> { }
class Son<E> extends Father<E> { }  // 错误2
```

<br><br>

### 六、继承关系的传递问题：[·](#目录)
> 总结地讲就是：两个**具有继承关系**的**普通类**，它们**的泛型**也**具有**继承关系，但以**它们作为类型实参**的泛型**不具有**继承关系.

- 具体的讲：

```Java
if A 是 B 的父类:
    A<T> 也是 B<T> 的父类
    但是：Generic<A> 不是 Generic<B> 的父类！！
```

<br><br>

```
泛型原生类型讲解
6. 在使用泛型的时候可以不使用菱形语法指定实参，直接裸用类型名：
    1) 例如：
         i. 定义引用（对象）时裸用类名：ArrayList list = new ArrayList(); // 当然也可以写成ArrayList list = new ArrayList<>();
         ii. 实现/继承：public class MyType extends MyGeneric { ... }
！！上面使用的类型或者接口在定义的时候都是泛型！！但是使用它们的时候忽略类型参数（都不用加菱形）；
    2) Java规定，一个泛型无论如何、在任何地方、不管如何使用，它永远都是泛型，因此这里既是你忽略类型实参它底层也是一个泛型，那么它的类型实参会是什么呢？既然我们没有显式指定，那么Java肯定会隐式为其指定一个类型实参吧？
    3) 答案是肯定的，如果使用泛型的时候不指定类型实参，那么Java就会用该泛型的“原生类型“来作为类型实参传入！
！！那么“原生类型“是什么呢？这里先不介绍，会在下一章的”泛型原理“里详细分解；
！！但是我们这里可以先透露一下，Java集合的原生类型基本都是Object，因此像上面的ArrayList list = new ArrayList();写法其实传入的是Object类型实参，即ArrayList<Object>！
```
