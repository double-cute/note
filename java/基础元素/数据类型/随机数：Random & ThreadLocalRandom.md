# 随机数：Random & ThreadLocalRandom
> 学名叫做**随机序列生成器**.
>
> - Random是单线程的，ThreadLocalRandom是多线程的（线程安全的）.
>
>> 任何语言的随机数都是用**伪随机序列**构造的，**种子一样得到的随机序列必定一样**.
>>
>>> - **需要定义复杂的随机序列则可以继承Random并进行拓展.**

<br><br>

## 目录

1. [Random](#一random)
2. [ThreadLocalRandom](#二threadlocalrandom)

<br><br>

### 一、Random：[·](#目录)
> 线程不安全版本（单线程）.

<br>

**1.&nbsp; 构造器：**

```Java
// 默认以当前JVM的[微秒时间]作为种子，或者显式指定一个long种子
Random([long seed]);
```

<br>

**2.&nbsp; 显式手动设定一个新的种子：**

- 可以在程序运行中途设定，不影响之前的结果，只影响之后的结果！

```Java
void setSeed(long seed);
```

<br>

**3.&nbsp; 获取序列中下一个随机数：**

```Java
// 1. [true, false]
boolean nextBoolean();

// 2. 无参默认为[-最大, +最大] or 显式指定为[0, bound)
int nextInt([int bound]);

// 3. [-最大, +最大]
long nextLong();

// 4. (0.0, 1.0)
float nextFloat();
double nextDouble();

// 5. 均值为0.0，方差为1.0的高斯分布
double nextGaussian();

// 6. 随机二进制序列填满整个数组
void nextBytes(byte[] bytes);     
```

<br><br>

### 二、ThreadLocalRandom：[·](#目录)
> 线程安全的随机序列生成器.
>
>> 除了构造之外，其它方法和Random一模一样，没有任何扩展.

<br>

- 通过ThreadLocalRandom的静态工具方法获取：

```Java
// 以当前所在线程的JVM当前时间作为种子
static ThreadLocalRandom current();
```
