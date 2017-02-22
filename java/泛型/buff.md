

### 一、Java数组类型的多态bug引发对泛型多态的思考：
> Java数组类型有一个小bug，那就是：如果A是B的父类，那么A[]也是B[]的父类.

- 这个bug虽然可以在编程时避免（不出现），然而一旦出现了就很容易（几乎是90%可能性）出错，例如：

```Java
String[] sarr = new String[10];
Object[] oarr = sarr;  // 编译正确！Java允许A是B的父类，那么A[]就是B[]的父类！
oarr[0] = 1.5;  // 运行时异常，因为oarr的运行时类型是String[]，String[]无法接受double类型的元素！
```

- 上面这样的错误很显然是不会在编译时检查的（毕竟Java允许这样），但头疼的问题是：
  - **运行时异常抛出的错误信息是ArrayStoreException，而不是类型冲突的ClassCastException！！**
  - 也就是说明明是类型冲突的错误，但报的却是数组存储错误！！
  - 如果开发者不明白这一点的话，肯定会耗费大量的时间被真正的错误原因所折磨！！
    - 简直就是坑爹啊，Java竟然纵容这么容易出错的漏洞设计！！

<br>

- 这种数组多态设计漏洞的背景：
  1. 数组设计得太早，是作为Java语言的一部分诞生的.
  2. Java数组实在太原始太底层了，很多JVM的内核都是基于数组的.
  3. 因此只能将就一下了，所以使用数组的时候一定要格外小心多态bug！

<br>

- 数组多态bug引发对泛型的思考：
  - 数组多态bug中映射出泛型多态的模型：

```Java
1. 可以把数组类型想成一种泛型Array<T>，那么String[]就是Array<String>，Double[]就是Array<Double>.
2. 数组多态bug用泛型来描述就是：如果**类型实参有继承关系**，那么它们**相同泛型如果也允许有继承关系的话**就容易发生运行时的类型冲突异常！

* 具体一点，接着上面的例子就是说：Object是String的父类，但如果也允许Array<Object>是Array<String>的父类的话就容易发生运行时的类型冲突异常！
```

- 这是道理是显然的，所以泛型**最为一个晚辈**，在Java好多个版本之后才出现，因此**有责任要避免这个bug**，**不能重蹈Java数组类型的覆辙**.
- 因此，**Java泛型编译时检查的一条重要规则**：[具有继承关系的类型实参]的[相同泛型]**不具有继承关系**
  - 具体的说就是，如果A是B的父类，那么Generic<A>不是Generic<B>的父类！！！两者直接不能发生多态！！
    - 绝对不能用Generic<A>的引用指向Generic<B>的对象！
  - **但A<T>一定是B<T>的父类，这是显然的.**

<br>

- Java泛型的**暖心而霸气的承诺**：**只要在编译时没有发生警告或错误，那么在运行时绝对不会发生类型转化异常！**
  - 即人性又牛逼.

<br><br>

### 二、任意类型参数情况下的泛型传参问题：类型参数通配符?

- 首先，实例化类型参数的泛型传参肯定是没有问题的，例如：void func(List\<String\> list);  // 可以传进来ArrayList<String>实参，只要Generic是List的子类或本身即可

<br>

- 但如果传参时类型参数不确定，可以是任何类型实参该怎么办呢？
  - 比如说：void func(List\<?\> list)中，?表示类型实参不确定，可以是任意的，既可以是String，也可以是Integer等.
    - 在实际生产开发中必定会有这样的需求的，那该怎么办呢？
  - 能想到的最直接的方案就是用Object来替换?，即void func(List\<Object\> list)，可这样是否就可以接受任意类型实参呢？
    - **大错特错！正因为A是B的父类，但Generic<A>不是Generic<B>的父类，因此如果这样写的话运行时必定会发生类型冲突！**

<br>

- Java专门提供了类型参数通配符?来解决上述问题，就直接写成void func(List\<?\> list)就能解决问题了！啊哈哈哈！
  1. 当?出现在**泛型的菱形**\<\>中时就表示**类型参数通配符**.
  2. ?可以匹配**一个范围**中的任意类型.
    - 什么叫一个范围呢？
      1. ?可匹配的范围是：≤ Object
      2. ? super Xxx可匹配的范围是：String ≤  ≤ Object
      3. ? extends Xxx可匹配的范围是：≤ Xxx
- 但是这里要**注意一个概念**：Generic\<? ...\>可以匹配自己范围的所有Generic\<T\>，并不是指Generic\<? ...\>是Generic\<T\>的父类！！！
  - 这个是指**编译器语法上的匹配**，并不指**实际上真正存在继承关系**！！！
  - 只是**语法概念**，而**非继承概念**.

<br>

- 那问题来了，对于类型参数是?通配符的泛型形参在方法中应该怎么使用呢？
  - 即：

```Java
void func(List<?> list) {
    // ???该怎么使用这个list呢？应该注意什么呢？
}
```

1. 首先肯定要慎重，不能乱修改，比如：
  - 如果你直接list.add("abc");就会埋下bug的种子.
    - 设想，如果你传入的实参是List<Integer>类型的，那你添加了一个String类型的不就发生运行时类型冲突了吗！
  - **但Java承诺过，使用泛型一定不会发生运行时类型异常，因此Java编译器会强制关闭出现上述歧义的方法调用，如果出现就会发生编译错误！**
2. 因此，最安全稳妥的方法就是**把?当做自己的类型上限**即可：即把?当做自己匹配范围的上限最安全最稳妥，一定不可能犯错.
  1. 把?当做Object.
  2. 把? super Xxx当做Object.
  3. 把? extends Xxx当做Xxx.

- 例如：

```Java
void func(List<? extends String> list) {
    list.get(0).charAt(5);  // 把?当做上限String即可
}
```

<br>

- 实际上，Java本来就是**将?的编译时类型默认为?的类型上限**：
  - 验证方法：多态

```Java
class A {
	public void print() { System.out.println("A"); }
}

class B extends A {
	@Override
	public void print() {
		System.out.println("B");
	}
}

public class Test {
	public static void proc(List<? extends A> list) {
		list.get(0).print();  // ?的编译时类型为A
	}

	public static void main(String[] args) throws NullPointerException  {
		ArrayList<A> list = new ArrayList<>();  // 不管是A还是B都一样，ArrayList<B>结果也一样
		list.add(new B());
		proc(list);  // B，调用的是子类的B的print方法，发生了多态！！
                    // 所以推断出?的编译时类型为上限A
	}
}
```

<br><br>

3. 设定?的上下限：
    1) 单单一个?可以表示任意类型，但有时候需要限定?的范围，这种需求是普遍存在的，就举一个最简单的例子：
         i. Shape是Circle、Rectangle、Triangle的父类；
         ii. Shape中定义了一个抽象方法draw表示绘制图形，Circle、Rectangle、Triangle都有相应的draw的覆盖版本，用于绘制各自的图形；
         iii. 这是一个多态的典型例子，可以用Shape的引用和任意具体图形挂钩，然后调用Circle的draw来绘制不同的图形；
         iv. 考虑到一个方法drawShape(List<?> list); 想把list中的所有图形画出来，显然，这里希望?只代表Shape、Circle、Rectangle、Triangle这几种图形，其它的不要，但是现在一个?可以代表任意类型，可以是String，也可以是Integer，这显然不符合要求，但是也不能写成drawShape(List<Shape> list)，因为List<Shape>并不是List<Circle>等的父类！！
    2) 而受限的的?（通配符）就可以解决这个问题：不管受不受限，?仍然表示未知类型！！
         i. 设定?的上限：<? extends Xxx>表示?只能代表Xxx或者Xxx的子类；
         ii. 设定?的下限：<? super Xxx>表示?只能代表Xxx或者Xxx的父类；
！！按照继承的上下关系，父在子上，子在父下，因此extends划定了?的上限，而super划定了?的下限，对于super限定，其上限是无限的，而所有类的父类都可以追溯到Object，因此super限定的?的上限就是Object！！
    3) 在上面的问题中就可以使用?的上限来解决：drawShape(List<? extends Shape> list); // 这里的?就只能是Shape、Circle、Rectangle、Triangle之一了，如果传其它类型就会直接编译报错！
！！这就比单用一个<?>要好很多，因为单用一个<?>需要强制类型转换：
public void drawShape(List<?> list) {
	for (Object obj: list) {
		Shape s = (Shape)obj; // 由于传进来的可能是任何乱七八糟的类型（比如String等，这些就是错误的），因此需要强制类型转换一下
		s.draw();
	}
}

public void drawShape(List<? extends Shape> list) {
	for (Shape s: list) { // 由于已经保证了传进来的类型最高就是Shape，因此这里就无需强转，直接使用Shape类型多态就行了
		s.draw();
	}
}
！！可以看到，泛型的目的就是为了避免各种臃肿的代码（强制类型转换），这里使用?的上限就可以是代码简化很多！！
    4) 不管受不受限，?仍然表示未知类型，仍然是只读的，不能修改！！
        i. 虽然<? extends Shape>已经使?受限了，但是这个?泛型对象仍然是只读的，不能修改；
        ii. 这个问题很好考虑，还是之前的分析方法一样，?现在可能是Shape，可能是Circle，可能是Rectangle，也可能是Triangle，具体是哪个没人知道，因为?可以表示这4个中的任意一个；
        iii. 现在倒过来想，你现在想往该?泛型对象中加入一个Rectangle，那如果?代表的是Triangle，那岂不是类型冲突了吗？
        iv. 所以，不管?受限不受限，只要出现了?，那么?泛型对象就是只读的，想都别想改它了！！！！


4. 类型参数的上限：
    1) 除了在参数传递中，类型通配符?可以设定上下限，普通的泛型定义中，类型参数也可以设定上限，例如：public class A<T extends B>，这就表示类型参数T只能是B或者B的子类，在使用该泛型时如果用其它类型实例化就会编译报错！！
    2) 可惜的，类型参数只能设定上限，不能设定下限，即没有：class A<T super B>这样的语法！！！Java目前不支持！！
    3) 类型参数不仅可以指定上限，也可以指定多个必须实现的接口：
         i. 类型参数的上限设定和接口限定的一般写法是：class 泛型名<T extends 上限类 & 接口1 & 接口2...>
         ii. 其中最多只有一个上限类，不能同时继承多个类，这是必然的，因为Java本身就不支持多重继承；
         iii. 但是Java在接口层面支持“多重继承”，因此可以实现多个接口，接口之间用&连接；
         iv. 这里要求接口必须写在上限类之后，否则会编译报错的！！
         v. 这里的意思就是使用该泛型的时候，实例化的具体类型必须是指定上限类或者其子类，并且必须实现指定的所有接口，不满足上述要求的实例化类型都会导致编译错误！
