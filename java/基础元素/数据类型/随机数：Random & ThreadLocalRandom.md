# 随机数：Random & ThreadLocalRandom
> 学名叫做**随机序列生成器**.
>
> - Random是单线程的，ThreadLocalRandom是多线程的（线程安全的）.
>
>> 任何语言的随机数都是用**伪随机序列**构造的，**种子一样得到的随机序列必定一样**.

<br><br>

## 目录

1. [Random]()
2. [ThreadLocalRandom]()

<br><br>

### 一、Random：[·](#目录)
> 线程不安全版本（单线程）.

<br>

**1.&nbsp; 构造器：**

```Java
// 1. 默认以当前JVM的[微秒时间]作为种子
Random();

// 2. 显式设定种子
Random(long seed);
```

<br>

**2.&nbsp; 获取序列中下一个随机数：**

```Java
// 1. [0, bound)
int nextInt(int bound);

// 2. 各自类型范围[-最大, +最大]
int nextInt();
long nextLong();

// 3. (0.0, 1.0)
float nextFloat();
double nextDouble();

// 4. 均值为0.0，方差为1.0的高斯分布
double nextGaussian();

// 5. 随机布尔值
boolean nextBoolean();

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
static ThreadLocalRandom ThreadLocalRandom.current();
```
