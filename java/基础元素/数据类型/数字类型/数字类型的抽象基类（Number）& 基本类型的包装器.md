# 数字类型的抽象基类（Number）& 基本类型的包装器
> Java的不完全面向对象就是由于基本类型造成，为了弥补这个不足之处，Java为所有基本类型都做了面相对象包装，设计了各自的包装器类型.
>
>> Java之所以还提供8种基本类型主要是为了照顾C开发者的习惯以推广这门语言.
>>
>>> 8种基本类型对应的包装器类型分别是：**Boolean、Byte、Short、Character、Integer、Long、Float、Double**.
>
> Number是 **包装器类、精确整数/实数** 的共同抽象基类. （除了位向量BitSet之外）

<br><br>

## 目录

1. [Number抽象基类]()
1. [自动装箱](#一自动装箱)
2. [自动拆箱](#二自动拆箱)
3. [有了包装器类型就可以应用instanceof了](#三有了包装器类型就可以应用instanceof了)
4. [大小比较](#四大小比较)
5. [相等比较：认准equals](#五相等比较认准equals--)
6. [构造器](#六构造器)
7. [数值和字符串相互转化汇总](#七数值和字符串相互转化汇总)

<br><br>

### 一、Number抽象基类：
> 只定义了一些转换成基本类型的方法.

<br>

```Java
// type支持byte、short、int、long、float、double
  // 子类实现它们的时候可能会发生截断（精度损失）
abstract type typeValue();
```

### 一、自动装箱：[·](#目录)
> JDK 1.5之前，**基本类型数据** 和 **包装器类型数据** 之间相互转换、赋值非常麻烦，需要各种麻烦的步骤.
>
>> 但现在Java提供了自动装箱/拆箱功能，大大简化了这个过程.

<br>

- 自动装箱的概念：
   - 可以直接将基本类型数据的变量或常量赋值给对应的包装器类型数据.
      - 底层会自动隐式地包装成包装器类型数据，例如，Integer a = 5;

<br>

- **自动装箱时的编译时类型检查非常严格！**
   1. 常量 赋值给 包装器类型变量：
      1. 窄化兼容仍然有效.
      2. 越过窄化兼容层次后（**大于char之后**）必须严格遵循 **=两边编译时类型完全一样** 的准则.
         - **完全没有向上兼容的概念.**
         - 例如：
            1. Long l = 5L; // 正确
            2. Long l = 5; // 错误，右边包装后编译时类型为Integer，和左边不符.
   2. 基本类型变量 赋值给 包装器类型变量：
      - 完全严格遵循**=两边编译时类型完全一样**的准则，
         - **完全没有向上兼容的概念.**
   3. 其本质是：**引用类型** 赋值给 **引用类型**，因此类型检查严格
      - 会先将**=右边**提升为**其编译时类型对应的包装器类型**再赋值给右边.

<br>

- 测试代码：

```Java
byte bb = 1;
short ss = 1;
char cc = 1;
int ii = 1;
long ll = 1L;
float ff = 1.1f;
double dd = 1.1;

Byte b = 5;  // 常量赋值支持窄化兼容
	b = bb;
Short s = 11;  // 常量赋值支持窄化兼容
	s = ss;
	s = b;  // 错误！变量赋值不支持向上兼容！
	s = bb; // 错误！
Character c = 6;
	c = cc;
	c = bb;  // 错误！

// 从兼容转化开始，所有的一切都必须严格遵从编译时类型完全相同的原则
// 不支持向上兼容
  // 精确地讲是，面向对象的数值类型没有向上兼容这一说

	c = b;  // 错误！
	c = s;  // 错误！
Integer i = 20;
	i = b;  // 错误！
Long l = 30L;
	l = 20; // 错误！
	l = i;  // 错误！
	l = b;  // 错误！

Float f = 5.5f;
	f = 5;  // 错误！
	f = 5.5;  // 错误！
	f = b;  // 错误！
Double d = 5.5;
	d = 5;  // 错误！
	d = 5.5f;  // 错误！
	d = f;  // 错误！
```

<br>

- **自动装箱的原理（步骤）：**
   1. **先检查**=右边到左边是否满足**窄化兼容**.  （窄化兼容的条件是，左边为Byte、Short、Character，右边是int常量）
      - 如果满足，先将左边的常量（窄化转换右边一定是常量）自动隐式转换为左边的包装器类型.
      - **即满足就顺利完成赋值.**
   2. 如果不满足，则先检查右边常量或变量的编译时类型，然后将其转化为编译时类型对应的包装器类型.
      - 即**右边自动提升为自己本身对应的包装器类型**.
   3. 接着赋值，如果类型不一致则发生类型冲突报错（编译时错误）.
      - 例如：Byte b = 5;
         1. 5 -> byte符合窄化兼容条件.
         2. 5 -> Byte(5) -> b
      - 例如：Byte b = i;
         1. i -> b不符合窄化兼容条件.
         2. i(int) -> Integer(i).
         3. Integer <-> Byte类型不相同，编译报错！
      - 例如：Double d = 5.5f;
         1. 5.5f -> double不符合窄化兼容条件.
         2. 5.5f(float) -> Float(5.5f)
         3. Float <-> Double类型不相同，编译报错！
      - 例如：**Object o = 5;**
         1. 5(int) -> Integer(5)
         2. **Integer -> Object兼容！正确！**

<br><br>

### 二、自动拆箱：[·](#目录)
> 就是将包装器类型变量赋值给基本类型变量.

- 由于其底层实质是：**基本类型** 赋值给 **基本类型**
   1. 先将=右边的包装器类型变量自动隐式转化成其对应的基本类型.
   2. 然后再将其赋给左边的基本类型变量.

<br>

- 由于是最终转化成了基本类型对基本类型的赋值，因此**支持向上兼容**.
   - 例如：float f = new Integer(5);  // 正确！右边先转化为int(5)，而int -> float是向上兼容的

<br>

- 但注意：**检查的是编译时类型，而不是运行是类型！！！！**
   - 拆箱是根据=右边内容 **自己本身的编译时类型** 拆箱成对应的基本类型.
   - 并没有强大到根据自己本身的运行时类型拆箱成对应的基本类型，这必须要由开发者自己决定.

```Java
Object o = 5;
int i = o;  // o的编译时类型是Object，由于编译器笨，不知道o的运行是类型，因此无法拆箱成int
```

<br>

- 只要是**发生赋值的地方**都可以发生**自动装箱和拆箱**，特别是在方法传参时：

```Java
void func(int a);   // obj.func(new Integer(5));  自动拆箱
void func(Integer a);  // obj.func(5);  自动装箱
```

<br>

### 三、有了包装器类型就可以应用instanceof了：[·](#目录)

```Java
Object o = 5;
if (o instanceof Integer) {
	int a = (Integer)o;  // 根据运行时类型强转是需要开发者自己手动决定的
}
```

- 再次强调，不允许直接int a = o;
   - 因为：**重要的事情再重复一遍！**
      - 拆箱是根据=右边内容 **自己本身的编译时类型** 拆箱成对应的基本类型.
      - 并没有强大到根据自己本身的运行时类型拆箱成对应的基本类型，这必须要由开发者自己决定.
         - 即使其运行时类型到=左边的赋值是满足条件的，也不行.
   - 报错的理由是：
      1. o的编译时类型是Object，而a的编译时类型是int，Object无法自动拆箱成Integer，因为无法判断o的运行时类型是否为Integer.
      2. 因此最终的结论是编译时类型冲突，Object -> int转化失败.

<br><br>

### 四、大小比较：[·](#目录)
> Java对含有包装器类型的大小比较有一个 **设计原则上的要求**：必须先全部拆箱成基本类型，再用基本类型做最终的比较.
>
>> 也就是说：Java中，数值的大小比较，永远、最终都是**基本类型数值之间的比较**！！！

- 含有包装器类型的比较方法总结：
   1. 比较运算符：\>、\<、\>=、\<=这些的.  （**风骚型**）
      - 支持两边 **只有一边是包装器** 或者 **两边都是包装器**.
      - 比较运算符在运算前的第一步就是将所有的包装器类型拆箱成对应的基本类型，然后再进行正常的基本类型比较.
   2. 统一类型比较工具：**包装器类的静态工具方法compare**  （**内敛严谨型**）
      - 由于比较运算符允许混合类型比较，因此如果骚起来的话代码看上去很乱，不让人放心.
         - 很难一眼看出最后的结果是统一在什么类型下比较的.
      - 因此Java专门为所有包装器类设计了一个可以**统一在该类型下的大小比较器**.

```Java
// Type是包装器类，type是Type对应的基本类型
  // x < y return -1
  // x == y return 0
  // x > y return 1
public static int Type.compare(type x, type y);

// 例如：double型
public static int Double.compare(double x, double y);
// 这就使得，不管x和y传什么实参，最终都是统一在double下进行比较
// 例如：
Double.compare(new Integer(5), 3.12f);  // 不用担心怎么隐式转换，最终比的都是double下的大小
```

<br><br>

### 五、相等比较：认准equals  [·](#目录)

<br>

**1. 直接用Object覆盖的equals方法是没有任何问题：**

```Java
public equals(Object obj);  // 覆盖后比较的就是值（内容）的相等，没有任何问题
```

- 最安全，**不可能会有任何错误**！！

<br>

**2. 使用==比较：**

1. 如果一边是基本类型，一边是包装器类型，那么：
   - 包装器类型会**自动拆箱**成自己的基本类型.
   - 接着就是**基本类型的==比较**了.
      - 例如：6.0 == new Integer(6);  // 正确！基本类型的==比较允许混合类型出现
2. 如果两边都是包装器类型，那么：
   1. 就是引用类型的==比较了.
   2. 此时==比较的是引用指向的对象的内存地址是否相同！
      - 但也不能乱比较，和instanceof一样，**必须是两个有继承关系的对象才能用==比较地址是否相同**.
         - 像new String() == new Date()这种就直接编译报错了.
      - 例如：**new Integer(5) == new Double(5.0);  // 直接编译报错，因为Integer和Double之间并不是直系关系！**

<br>

**3. 自动装箱的实现算法：**

1. 由于-128-127（char范围）的整数太常用了（根据经验）.
2. 因此Integer在静态初始化代码中创建了一块缓存，将-128-127包装成Integer存在这个缓存中（就是一个Integer[]数组，依次存放-128-127）.
   - 缓存中的这些对象都是只读的，不可写.
3. 如果采用自动装箱赋值，则会直接让那个引用指向缓存中对应的只读Integer对象.
4. 单如果自动装箱的数值超出127（>127）则会重新new出来一个对象了.

- 因此会有如下的梗：

```Java
new Integer(5) == new Integer(5);  // false，很正常，比较的是地址

Integer a = 5; // 自动装箱，直接指向Integer静态缓存
Integer b = 5;
out.println(a == b); // true，5∈[-128, 128)，因此指向的是Integer静态缓存中的同一个对象

Integer x = 128; // 自动装箱，但其实是new出来的
Integer y = 128;
out.println(x == y); // false，128超出了[-128, 128)范围，因此都是重新new出来的
// 等价于
Integer x = new Integer(128);
Integer y = new Integer(128);
out.println(x == y); // 理所当然是false
```

<br>

**4. 一句话总结：** 不要浪！不要骚！**老老实实用equals比较吧！**

<br><br>

### 六、构造器：[·](#目录)

- 所有包装器的构造器**只有两种**：

```Java
// Type是包装器类型，type是对应的基本类型
Type(type val);   // 通过基本类型构造
Type(String val);  // 通过字符串构造

// 例如：
Double d = new Double(5.5);
Float f = new Float("5.5");

// 由于通过基本类型构造的参数是基本类型，因此还可以自动拆箱
Double d = new Double(new Double(5.5)); // Double(5.5)拆箱成5.5(double)
Double d = new Double(new Integer(5)); // Integer(5)拆箱成5(int)，再向上兼容成5.0(double)
```

<br><br>

### 七、数值和字符串相互转化汇总：[·](#目录)
> 所有 **[字符串 -> 数值]** 的方法都会都会抛出 **解析异常[NumberFormatException]**.

<br>

| 转换方向 | 方法描述 | 具体方法 | 注意事项 |
| --- | --- | --- | --- |
| 字符串 -\> 数值（包装器） | 包装器的构造器 | Type(String val); | 返回类型是**包装器类型** |
| 字符串 -\> 数值（基本类型） | 包装器的parseType静态工具方法 | static type **Type.parseType**(String val); | 返回类型是**基本类型** |
| 数值（基本类型） -\> 字符串 | String的valueOf静态工具方法 | static String **String.valueOf**(type val); | 传入的是基本类型（当然可以自动拆箱（传包装器））|
| 数值（基本、包装器）-\> 字符串 | 和空串连接 | "" + val | val如果是包装器类型则默认调用覆盖好的**toString**方法 |

<br><br>

### 八、Integer的valueOf缓存机制：

-128~127缓存在Integer的private static Integer[] cache中，

Integer.valueOf(6) == Integer.valueOf(6)  // true
Integer.valueOf(200) == Integer.valueOf(200)  // false，超出范围直接new
