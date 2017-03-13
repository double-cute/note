# 精确实数：BigDecimal
> Java的基本数值型（包括其包装器类型）具有很大的局限性：
>
> 1. 存在上下界.
> 2. 特别是浮点数（float和double）本身就存在精度问题.
>
>> BigDecimal底层使用字符串来保存实数：
>>
>> 1. 只表示实数，没有整型和浮点型之分，小数点和没小数点的都一块儿表示.
>> 2. 字符串不限制长度.
>>   - 这就意味表示的实数没有上下界的局限，也没有精度长度的局限，可以无限精确.

<br><br>

## 目录

1. []()
2. []()

<br><br>

### 一、构造器：

<br>

**1.&nbsp; 利用字符串构造：**

```Java
// 1.
BigDecimal(String val);
// 2.
BigDecimal(char[] val[, int offset, int len]);
```

<br>

**2.&nbsp; 利用基本类型构造：**

- 只支持int、long、double三种类型

```Java
// double本身就存在精度问题，因此要慎用！
BigDecimal(int | long | double | BigDecimal val);
```

<br><br>

### 二、精度和小数点位数：
> 1. precision：精度是指当前的数字个数（不包括小数点的全部数字的个数）.
>   - 其实就是底层字符串长度-1（减掉小数点的那个1）.
>   - 精度不允许设值只能查看.
> 2. scale：即保留到小数点后几位.
>   - 可以设值，可以查看.
>   - 但是会影响精度：变少了那么位数（精度）也会随之变少，加大了精度也随之加大.

<br>

**1.&nbsp; 精度只能查看不能修改：**

```Java
int precision();
```

<br>

**2.&nbsp; 小数点保留位数可查看可修改：**

```Java
// 1. 查看
int scale();

/** 2. 设值，返回新的对象，不改变原对象，原对象scale不变！
 *  - scale改变后precision也随之改变（一起大一起小，变化量也是一样的）.
 *  - 最直观的的反应就是out.print的时候可以看出小数位数的区别.
 *  - 默认是取舍策略是UNNECESSARY，但小心异常.
 */
BigDecimal setScale(int newScale[, int roundingMode]);
```

- roundingMode是指取舍方式.
  - 有可能scale设定后需要截断.
  - 这个时候必须指定一种截断策略.
  - 都是BigDecimal的静态常亮，以**BigDecimal.ROUND_**作为前缀.

| 策略 | 说明 | 示例 |
| --- | --- | --- |
| UP | 远离0 | -1.235 -\> -1.2**4** |
| DOWN | 靠近0 | -1.235 -\> -1.2**3** |
| CEILING | 靠近+∞ | -1.235 -\> -1.2**3** |
| FLOOR | 靠近-∞ | -1.235 -\> -1.2**4** |
| HALF_UP | ≥ 5远离0（即UP）<br>否则舍去 | -1.235 -\> -1.2**4** |
| HALF_DOWN | ≤ 5舍去靠近0（即DOWN）<br>否则**进位**| -1.235 -\> -1.2**3**<br>-1.236 -\> -1.2**4** |
| UNNECESSARY | 表示精度刚好没必要取舍 | **但如果真的需要截断（取舍）会抛出异常！**|

<br>

### 三、转换成基本类型：

<br>

**1.&nbsp; 转换成整型：**

- xxx支持所有整型（**除了char**）：byte、short、int、long
  - 通常建议使用安全的版本.

```Java
// 1. 狂野版本，如果有小数点或者超出xxx的范围直接截断丢失精度
xxx xxxValue();

// 2. 安全版本，如果有小数点或者找出xxx的返回会抛出ArithmeticException异常
xxx xxxValueExact();
```

<br>

**2.&nbsp; 转换成浮点型：**

- xxx当然是float和double.

```Java
// 不存在超出范围的问题，只不过浮点型本身就有精度损失，所以要慎重
xxx xxxValue();
```

<br><br>

### 四、算术运算：

<br>

**1.&nbsp; 二元运算：**

```Java
// op支持add、subtract、multiply，分别对应加减乘
  // 除法非常特殊，后面详解
BigDecimal op(BigDecimal val);
```

<br>

**2.&nbsp; 一元运算：**

```Java
// 1. 指数
BigDecimal pow(int n);

// 2. 取反（加一个负号）
BigDecimal negate();

// 3. 单位采样，< 0返回-1，≥ 0返回0
int signum();

// 4. 自己加自己，不是+1
BigDecimal plus();
```

<br><br>

### 四、除法和模除：
> 很多除法或者模除的结果无法精确表示（比如无限循环小数等）.
>
>> 毕竟无限的精确还是无法存储的，因此除法和模除一般需要制定控制精度.

<br>

**1.&nbsp; 除法：**

```Java
/**  
 *  divisor：除数
 *  scale：小数点后保留几位
 *  roundingMode：设值取舍策略，默认是UNNECESSARY，小心异常！
 */
BigDecimal divide(BigDecimal divisor[, [int scale, ]RoundingMode roundingMode]);
```

<br>

**2.&nbsp; 模除：**

```Java

```

<br>


         i. 首先要知道整除（即结果只取除法结果的整数部分）：BigDecimal divideToIntegralValue(BigDecimal divisor); // 定义为a \ b
         ii. 接着就是模除：BigDecimal remainder(BigDecimal divisor); // 理论上就是a % b
！！但是BigDecimal表示的是实数，因此两个操作数都可能有小数点，而这里的模除是广义模除，其运算方法就是 a - (a \ b) × b，因此结果很可能也有小数点，并且也可能是负数！
    7) 还有同时得到除数和余数的方法：BigDecimal[] divideAndRemainder(BigDecimal divisor);  // 把上面两个方法分别放在[0]和[1]中一块儿返回（先\结果，后%结果）
