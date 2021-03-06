# 过期自动删除：expire
> 应用于 **有实效的数据**，比如 **限时优惠、缓存、验证码** 等.
>
>> - Redis提供能 **expire系列命令** 为键设定一个时效，过期后自动删除.
>>    - 同时提供了 **persist命令** 让键成为永久的（永远都不会删除）.
>>       - **默认情况下，创建的普通间都是永久的.**
>>
>> <br>
>>
>>> - 相比于SQL数据库，SQL需要一个额外的字段记录到期时间并不停地检测它，非常麻烦且耗费资源.
>>>
>>> <br>
>>>
> **过期时间键（expire键）的自动删除不会被watch认为该键发生改变.**

<br><br>

## 目录

1. [设定过期时间：expire系列命令](#一设定过期时间expire系列--)
2. [查看键的剩余时间：ttl系列](#二查看键的剩余时间ttl系列--)
3. [取消键的过期时间：persist](#三取消键的过期时间persist--)

<br><br>

### 一、设定过期时间：expire系列  [·](#目录)

<br>

1. 前缀p表示精确时间，单位是毫秒，没有p前缀则默认为单位是秒.
2. expire系列命令 **强制要求** 时间参数为 **整数字符串**，否则报错.
3. 设值成功返回1，否则返回0.
   - 失败通常有两种情况：
      1. 键不存在.
      2. 其它未知情况.

<br>

**1.&nbsp; 设定过期时长：**

```Shell
# 1. 单位是 秒
expire 键名 过期时长（秒）
pexpire 键名 过期时长（毫秒）
```

<br>

**2.&nbsp; 设定过期时间点：**

- 时间点采用的是Unix时间戳，例如（秒）：13151988711
   - 毫秒情况下则是再加3个0：13151988711000

```Shell
expireat 键名 过期时间点（秒）
pexpireat 键名 过期时间点（毫秒）
```

<br><br>

### 二、查看键的剩余时间：ttl系列  [·](#目录)

<br>

- 返回值：
   1. 键存在时：
      1. 正数：剩余存活时间.
      2. -1：该键是普通永久键，永远不会被删除.
   2. 键不存在时返回-2.

```Shell
# 单位是秒
ttl 键名
# 单位是毫秒
pttl 键名
```

<br><br>

### 三、取消键的过期时间：persist  [·](#目录)
> 由于所有新创建的键默认都是普通永久的.
>
>> 并且，只有string类型是直接可以通过set覆盖键.
>>
>> - 因此对于string类型的过期时间键可以通过set覆盖来删除过期时间.
>>    - **除了string类型的set命令之外，其余所有对键值进行操作的命令（incr、lpush、hset等）均不会影响过期时间.**

<br>

1. 返回过期时间是否被成功清除（1/0）
2. 失败的情况：
   1. 键本身就是永久的.
   2. 键不存在.

```Shell
persist 键名
```
