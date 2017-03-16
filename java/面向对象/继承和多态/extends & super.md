# extends & super
> extends是继承的关键字，super用于访问父类成员.

<br><br>

## 目录

1. [extends](#一extends)
2. [从this和super的区别看继承的存储结构](#二从this和super的区别看继承的存储结构)
3. [继承的简洁式总结](#三继承的简洁式总结)

<br><br>

### 一、extends：[·](#目录)
> 即类和接口继承的关键字.

<br>

- **类只支持单继承，接口支持多重继承**，那是因为：
   1. 接口中不能定义数据，类中可以定义数据.
   2. 方法只能保存一份，但一旦数据多重继承起来了，保存起来非常复杂.
      - 数据的继承与存储非常容易出bug.

<br>

- 例如：

```Java
public class Son extends Father { ... }
public interface SonInt extends FatherInt1, FatherInt2, ... { ... }
```

<br>

- 子类将**继承得到**父类的全部**可见成员**.
   - 可见成员是指：首先**成员是指数据和和方法（包括静态和非静态）**
      1. public和protected成员，这个毋庸置疑.
      2. default成员**只有在子类和父类定义在同一个包中才能继承得到**！
         - 其实就是4中访问控制符的基本概念而已，可以回忆一下.

<br><br>

### 二、从this和super的区别看继承的存储结构：[·](#目录)
> 和this相似的是super也是引用，用来表示子类对象中父类的部分（指向父类部分）.

<br>

- 先看一个例子：

```Java
class A {
	public int a = 10;
	public String f() {
		return "A";
	}
  private void g() {}
}

class AA extends A {
	public int a = 20;
	public String f() {
		return "AA";
	}
	public void show() {
		out.println("this.a = " + this.a); // 20
		out.println("super.a = " + super.a);  // 10
		out.println("f() = " + f());  // AA，无前缀默认为this.
		out.println("this.f() = " + this.f());  // AA
		out.println("super.f() = " + super.f());  // A

    u(); // u
		this.u(); // u
		super.u();  // u
    // g(); this.g(); super.g();   // 不可见，直接编译报错！
	}
}

public class Test {

	public static void main(String[] args) {
		AA r = new AA();
		out.println("r.a = " + r.a);  // 20
		r.show(); // 20 10 AA AA A u u u
    r.u(); // u
	}

}
```

- **结论：**
   1. 父类的不可见成员对子类隐藏（无法访问）.
      - 父类的private成员（所有不可见的成员（包括其它包中的default成员））无法访问.
   2. 子类会继承**所有**父类中可见的成员（数据和方法）.
      - 在AA的show中A、10都可以输出就证明了这点.
   3. 子类对象其实是两个对象合二为一：
      1. 明显是父类对象和子类本身拓展部分（想象成去掉extends的一个普通类）的合二为一.
      2. 就相当于：
         - class Father {...}  & class Son extends Father { ... }
         - super指向Father
         - this指向**去掉extends的Son**
            - 把Son想象成没有extends关键字的普通class Son {...}即可.
   4. 因此：
      1. **super的编译时类型是class Father {...}**
      2. **this的编译时类型class Son {...}   // 注意！！！没有extends的！！！！**
         - 因此this.一个和父类同名的变量或者重写的方法，那调用的一定是：
            - **编译器肉眼看到的** class Son {...}中 写出来的变量和方法！绝不考虑父类的.
   5. 子类引用的编译时类型**和this一样**是：class Son {...}  /// 没有extends！！！
      - 所以子类引用就跟this一样，只能调用class Son {...}**编译器肉眼成员**
      - 因此上面的r.a显示的是10而不是父类的20，r就跟this的效果一样了.
         - 通过编译时类型的原理可以将子类引用强转成父类引用然后轻松访问父类内容：
            1. Son son = new Son...;
            2. ((Father)son).a;   // (Father)son的编译时类型为class Father {...}，因此a一定是父类的a
               - 因此对于(Father)son可见的只有class Father {...}的**编译器肉眼**部分了.
               - 但要小心多态：((Father)son).f();  // 调用的是子类的f()，输出AA了
                  - 多态就是：**父类编译时类型+子类运行时类型+调用重写的方法** -> 最终会调用子类版本的方法
   6. 除非this和子类引用访问的成员在class Son {...// 没extends}中找不到，才会再回溯到父类中找是否还有可见的.
      - 子类中调用u()、this.u()，以及子类引用调用r.u()都能成功调用就是证明.
      - 但毕竟是子类中没有的，也就是说是class Son {...}中编译器肉眼看不到的.
         - 因此要回溯到class Father {...}编译器肉眼能看到的地方去找.
         - 因此**在向上回溯过程中会默认将前缀隐式修改为super**.
            - 即上面的u()在向上回溯过程中默认是是super.u()
            - this.u()向上回溯时会被替换成super.u()
            - r.u()在向上回溯过程中也会被替换成super.u()
      - 这个问题很重要，会在 **[自发性多态]()** 中讲到！

<br><br>

### 三、继承的简洁式总结：[·](#目录)
> 只要心中要编译时类型和运行时类型的概念，一切问题都可以轻松思考解决.
>
>> 绝不死记硬背.

<br>

1. 可见的才能继承.
2. 数据方法通通继承（子类都可获得）.
3. 子类对象包含两种编译时类型.
   1. super对应于class Father {...}
   2. this对应于class Son {...}   // 没有extends
   3. 子类引用对应于this
      - 向上强转的一定是编译时类型.
4. 只有当this和子类引用访问class Son {...}中不存在的成员时才会向上回溯寻找.
5. 回溯时默认隐式替换成super访问前缀.

<br>

- **注意：**
   - 由于super和this一样都是对象引用，因此super也可以访问父类静态成员.
   - 但还是那句话，静态成员最好使用类名访问，更加严谨和健壮.
