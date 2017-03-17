# 多态 & 重写（Override）& instanceof
> Java中触发多态的亘古不变的条件：
>
> 1. （引用的）**编译时类型** 是 **运行时类型** 的 **父类**.
> 2. 并且调用了被子类重写的方法.
>
>> 那么多态将被触发，这个 **编译时类型为父类的引用** 最终调用的是 **子类重写版本** .
>>
>>> - 子类重写（覆盖）父类方法的 **三同两小一大** 原则：小/大 是 ≤和≥ （**包括相等**）
>>>    1. 方法名、方法签名相同，都是静态或者都是非静态.
>>>    2. 返回类型、抛出异常类型比父类的要小.（返回类型小就是**返回类型协变**）
>>>    3. 访问控制符比父类的大.（但private不可见，不能覆盖）
>>>
>>>> - 如果不放心是否成功重写，可以在重写方法上一行**加上@Override的Annotation**.
>>>> - 这样编译器会强制帮你检查是否满足重写规则，如果不满足就直接编译报错！
>>>>    - 编译器会认为这是一个重写方法，因此会检查是否满足重写的三同两小一大原则.

<br><br>

## 目录

1. [多态的各种形式：假设重写方法是f()](#一多态的各种形式假设重写方法是f--)
2. [自发式多态：一定要避免！](#二自发式多态一定要避免--)
3. [多态的应用场合：接口/实现分离的思想](#三多态的应用场合接口实现分离的思想--)
4. [还原运行时类型：向下反转](#四还原运行时类型向下反转--)
5. [instanceof运算符](#五instanceof运算符)

<br><br>

### 一、多态的各种形式：假设重写方法是f()  [·](#目录)

<br>

**1.&nbsp; 正常形式：**

```Java
Father father = new Son();
father.f();

R r1 = new R();
Object r2 = new R();
boolean b = r2.equals(r1); // 调用的是R覆盖的equals
```

<br>

**2.&nbsp; 自放大：** 通过强转（放大类型）

```Java
((Father)new Son()).f();
```

<br>

**3.&nbsp; 隐式：**

```Java
// 假设R覆盖Object的toString返回的是"R"
Object obj = new R();
System.out.println(obj);  // 输出R
System.out.println((Object)new R()); // 同样输出R
```

<br>

**4.&nbsp; 自发式：**  危险！很容易隐藏bug！不要用！

<br>

- 就是在**父类方法中**调用**会被子类重写的方法**！
   - 在下一节详细讲述.

<br><br>

### 二、自发式多态：一定要避免！  [·](#目录)
> 在**父类方法中**调用**会被子类重写的方法**会导致自发性多态.
>
>> 这是一种隐藏特别深的缺陷，一定要避免.

<br>

- 将子类引用赋给父类引用**无需强制类型转换**.
   - 这是多态规则.
   - 其实编译器底层还是隐式进行了强转.
      - 这样可以减少代码的臃肿度.

<br>

**1.&nbsp; 正常形式：** this引用的层层传入

```Java
class Father {
    // 该方法将被子类重写
	void id() {
		out.println("Father");
	}

    // 在父类方法中调用一个将会被子类重写的方法
	void aFatherFunc() {
		id();  // 或者this.id()也一样，本来就是隐式默认添加this前缀的.
	}
}

class Son extends Father {
    @Override
	void id() { // 子类重写
		out.println("Son");
	}
}

public class Test {
	public static void main(String[] args) {
        Father father = new Son();
        father.aFatherFunc();  // 输出Son
	}
}
```

- 分析：
   1. 首先Father father = new Son()符合多态条件（引用编译时类型是运行时类型的父类）.
   2. father调用aFatherFunc()时传入的this引用就是father（仍然是编译时类型为父类）.
   3. 接着再传入aFatherFunc()里面调用的id()将this引用继续带入.
      - 因此最后this.id()的调用同样符合多态条件（this的编译时类型是运行时类型的父类，并且id被重写）.
   4. 因此最后输出的是Son.

<br>

- 总结起来就是：**在父类的方法中层层调用将会被重写的方法时，由于this引用必然会被层层带入，因此必然会发生自发式多态.**

<br>

**2.&nbsp; 向上回溯式（super强转）：**

- 容易导致逻辑bug.

```Java
// 把上面main方法里的两行代码改成
new Son().aFatherFunc();  // 输出Son
```

- 容易出逻辑bug的原因：
   1. 由于aFatherFunc在class Son中找不到，因此会到父类中去中.
   2. 然后你会想当然的认为父类中的aFatherFunc调用的肯定是父类的id().
      - 那你就大错特错了，实际调用的是子类的id().

<br>

- 分析原理：向上回溯的super替换
   1. 如果子类引用调用了只有父类定义的方法（子类中没有），就会发生向上回溯.
   2. 而想要看见（**编译器肉眼可见**）父类中定义的方法就必须将**类型为子类的this引用强转成super**.
      - 其实就是个强转而已：((Father)this).aFatherFunc()，转换后等价于super.aFatherFunc()
   3. 而super的编译时类型为父类，转换后指向对象的运行时类型还是子类，因此必然触发多态！
      - 也就是说层层传入的this引用实际上是super！
         1. this.aFatherFunc() ->
         2. ((Father)this).aFatherFunc() 等价于 super.aFatherFunc() ->
         3. super.id() ->
         4. 触发多态

<br>

**3.&nbsp; 构造器回溯式（也是super强转导致的）：** 最为隐蔽！一定要避免！

- 就是在父类构造器中调用会被子类重写的方法！
   - **太隐蔽了，一定要避免！！**

```Java
class Father {
	void id() { // 将被子类重写
		out.println("Father");
	}

	// 在父类构造器中调用会被子类重写的方法
	Father() {
		id();
	}
}

class Son extends Father {
	String info = "hello";

	@Override
	void id() { // 覆盖，输出自己info的信息
		out.println("Son with info: " + info); // 输出null
		// out.println("info length = " + info.length()); // 空指针异常！
	}

}

public class Test {
	public static void main(String[] args) {
		new Son(); // Son with info: null
	}
}
```

- 分析：
   1. Son()首先会调用Father()，因而向上回溯.
   2. 回溯后变成调用super()，而super编译时类型为Father，运行时类型为Son，因此触发多态.
   3. 将super引用带入Father()中变成了，super.id()，多态发生，变成调用子类的id().
   4. 而此时子类还没有开始构造，因此info为null，此时输出信息必然是null.
      - 如果再访问info.length()那必然发生空指针异常！

<br><br>

### 三、多态的应用场合：接口/实现分离的思想  [·](#目录)
> 子类是父类的**is-a抽象**.

<br>

- 多态的精髓在于让父类引用调用相同的方法可以表现出不同子类的行为：
   1. 设想让一个Father轮流指向其不同的子类Son1、Son2...
   2. 所有Son都覆盖了Father的f()方法，并且每种Son的覆盖逻辑都不同（行为都不一样）.
   3. 多态发生时，Father每次调用的f()都表现出不同Son的行为.

<br>

- 最常见的例子就是Java集合：
   - 经常用Set等抽象集合的引用指向TreeSet、HashSet等具体类型的对象.
   - 但你完全不用理会底层指向的对象具体是什么类型的，只要放心的当做Set使用就行.
   - 因为多态保证了你在使用Set的add等操作时完全感受不到底层操作的其实是TreeSet等的add.

<br>

- 多态最典型的应用：接口/实现分离的思想
   - 标准协会只给出最上层的抽象类、接口、父类.
      - 里面定义了所有需要实现的方法（**规范**）.
   - 然后不同开发商更具自己的业务需求，按照各自的逻辑实现里面的规则（产生自己的派生类）.
      - 但**不对外添加任何规范以外的方法**（辅助方法之类定义成private）.
      - **对外看上去实现类和上层类/接口没有任何区别.**
   - 最后发布软件后，只需要用户用标准协会提供的上层类/接口的引用指向供应商的实现类对象.
      - 使用时完全感受不出供应商的底层复杂实现.
      - **仿佛那些上层类/接口都是实现类.**
- **总结：父类/接口是规范，子类是实现.**

<br><br>

### 四、还原运行时类型：向下反转  [·](#目录)
> 在引用相互赋值时编译器只检查引用的编译时类型，不检查运行时类型.
>
> <br>
>
> - 引用赋值时的编译器检查：
>
>> 1. 向上兼容：子类引用赋给父类引用是合法的. （**符合is-a逻辑**）
>> 2. 向下不兼容：父类引用赋给子类引用是不合法的. （**不符合is-a逻辑，直接编译错误**）.
>> 3. 相等：两个引用的编译时类型完全相同，那相互赋值是完全没有问题的.
>>
>>> - 那么也就是说，非要把一个父类引用逆天地赋给子类引用，就不得不强制类型转换了：
>>>    - Son son = (Son)fahter;
>>>    - 原理是类型相等原则，即强转后右边的编译时类型和左边相同（都是子类）.
>>>       - 这就是**向下反转**.

<br>

- 还原运行是类型是指：
   - 把指向子类的父类引用还原成子类引用.
   - 也就是将编译时类型为父类的引用还原成和运行时类型相同（子类）.
- 方法：向下反转
   - Father father = new Son(); Son son = (Son)father; // 即可

<br>

- 但再次强调：强制类型转换运算符不能乱用的
   - **必须赋值运算符两边的编译时类型存在父子继承关系时才行，否则编译报错！**
      - Java是一个强类型语言，严格检查编译时类型的匹配！
   - 例如：Integer i = (Integer)"abc";  // Integer和String完全扯不上关系（没有is-a的继承关系），因此报错！

<br><br>

### 五、instanceof运算符：[·](#目录)
> 和+、-、\*等一样都属于Java的基本运算符.
>
>> 该运算符的作用是检查一个**引用**的**运行时类型**.

<br>

- 用法：引用 instanceof 类名/接口名
   - 具体作用是：判断该引用的**运行时类型**是否为指定类/接口的**子类或者相等**.
- 返回的是boolean.

<br>

- 由于Java的运算符是一定要检查编译时类型是否匹配，因此instanceof也不例外.
   - 只有当instanceof两边的东西（编译时类型）满足is-a的父子继承关系时才行，否则编译错误！！

```Java
Object o = "lala";
if (o instanceof String) { // true
	String s = (String)o;
}
out.println(o instanceof Math); // false


String s = "lala";
s instanceof Math; // 编译报错，s的编译时类型String和Math没有继承关系
```
