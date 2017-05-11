# UUID
> Universally Unique Identifier，主要应用于 **分布式计算环境** (Distributed Computing Environment, DCE).
>
> - 其目的最主要是让分布式系统中的所有元素都能有唯一的标识，从而省去中央控制辨识的步骤.
>
>> UUID应用广泛，主要有：Linux ext2/ext3 档案系统，LUKS 加密分割区等等.
>>
>>> - 大多数编程语言都有提供自动生成UUID的API.

<br><br>

## 目录

1. [UUID的标准组成]()
2. [Java对UUID的基本支持]()

<br><br>

### 一、UUID的标准组成：[·](#目录)
> UUID一个长的数字序列，由多个部分组成.
>
> - UUID的缺点也在这里，序列特别长.

<br>

- 从前往后按照如下顺序连缀起来：
   1. 当前日期和时间.
   2. CPU时钟序列.
   3. IEEE机器识别号（如有网卡则也可以从网卡MAC中获取）.

<br>

- 标准格式：
   1. 128 bits = 32 个16进制数.
   2. 8-4-4-16的十六进制形式：xxxxxxxx-xxxx-xxxx-xxxxxxxxxxxxxxxx

<br><br>

### 二、Java对UUID的基本支持：[·](#目录)

<br>

**1.&nbsp; 随机生成：**

```Java
static UUID	randomUUID();
```

<br>

**2.&nbsp; 转成string：**

```Java
// 形式是：8-4-4-16 xxxxxxxx-xxxx-xxxx-xxxxxxxxxxxxxxxx
String toString();
```

<br>

**3.&nbsp; 实现了equals和compareTo.**
