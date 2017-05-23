# hash、list、set、zset的编码方案简介

<br><br>

## 目录

1. [hash]()
2. [list]()
3. [set]()
4. [zset]()

<br><br>

### 一、hash：[·](#目录)

<br>

- 配置：
   - 同时满足以下条件使用ziplist编码，否则使用hashtable编码.
   - 两者不宜设置太大，因为ziplist是紧凑型数组，各种操作时间复杂度很高.

```Shell
# 字段数 < 该值
hash-max-ziplist-entries 512
# 字段值大小（字节）< 该值
hash-max-ziplist-value 64
```

<br><br>

### 二、list：[·](#目录)

<br>

- 配置：
   - 同时满足以下条件使用ziplist编码，否则使用linkedlist编码.
   - 两者不宜设置太大，因为ziplist是紧凑型数组，各种操作时间复杂度很高.

```Shell
# 元素数 < 该值
list-max-ziplist-entries 512
# 元素大小（字节）< 该值
list-max-ziplist-value 64
```

<br>

- 关于linkedlist编码方案的简单介绍：
   1. 双向链表，每个元素都用一个redisObject保存.
   2. 由于每个元素都是string类型的，因此可以用优化string的方式优化list的每个元素.

<br>

- 展望：quicklist
   - 最新版本的Redis正在开发REDIS_ENCODING_QUICKLIST编码方案. （新的list编码方案）
      1. 介于ziplist和linkedlist之间.
      2. 用双向链表的方式连缀多个ziplist.
      3. 是一种折中方案，空间比linkedlist省，速度比ziplist快.

<br><br>

### 三、set：[·](#目录)

<br>

- 配置：
   - 集合中全部为long内的整数，且元素数量 < 该值就是用intset编码.
      - 否则使用hashtable编码.

```Shell
set-max-intset-entries 512
```

<br><br>

### 四、zset：[·](#目录)

<br>

- 配置：
   - 满足以下条件用ziplist编码，否则用skiplist编码.

```Shell
# 元素数 < 该值
zset-max-ziplist-entries 512
# 元素大小（字节）< 该值
zset-max-ziplist-value 64
```

- 关于Redis的跳跃表skiplist的若干实现特例：
   1. Redis的skiplist由两部分组成：
      1. hash表：存储元素值到分数的映射. （**使得zScore等操作的复杂度为O(1)**）
      2. skiplist：存储分数到元素值的映射，以实现排序功能.
   2. Redis对skiplist的若干修改：
      1. 允许分数相同.
      2. 每个skipnode都增加了一个前向指针用以实现倒查询.
   3. 每个元素都用redisObject包装，因此可以用string的优化方式去优化skiplist中的每个元素.
