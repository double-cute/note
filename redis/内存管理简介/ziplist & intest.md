# ziplist & intset
> 1. ziplist是一种用可变数组实现的高度紧凑的编码格式.
>    - 应用于hash、list、zset的时间换空间方案.
> 2. intset只应用于set存储 **小&整数**.
>    - 就是8字节数组，高度紧凑，时间换空间方案.

<br><br>

## 目录

1. []()
2. []()

<br><br>

### 一、ziplist内部存储结构：

<br>

- **变量名的前缀zl是ziplist的缩写.**

| 地址（从上往下，从低到高）| 字节数:简名 | 说明 |
| :---: | --- | --- |
| uint**32**_t zlbytes | **4**: total_size | `整个ziplist占用的字节数` |
| uint**32**_t zltail | **4**: last_ele_offset | `1) 到最后一个元素的偏移量`<br>`2) 可以快速操作尾端（list专用）` |
| uint**16**_t zllen | **2**: ele_num | `元素的数量` |
| **元素0** | `1) 大小不固定` | `1) 存放list元素时和list的元素一一对应` |
| **元素1** | `2) 根据存储的数据类型决定` | `2) 存放hash元素时前一个是字段名后一个是字段值` |
| ... | `3) hash、list、zset元素都可以存` | `3) 存放zset元素时前一个是元素值后一个是分数` |
| uint**8**_t | **1**: end_sign | `结束标致，**恒为255**` |

<br><br>

### 二、每个ziplist元素的存储结构：

<br>

| 地址（从上往下，从低到高）| 说明 | 字节数 |
| :---: | --- | --- |
| prev_ele_size | `1) 前一个元素的字节数`<br>`2) 以实现倒序查找` | `prev_ele_size < 254? 1: 5` |
| ele_encoding<br>ele_size | `1) 当前元素的编码方案`<br>`2) 当前元素的字节数` | `case ele_size:`<br>`1) < 64: 1`<br>`2) < 16384: 2`<br>`3) 5` |
| ele_content | `元素的实际内容` | `1) 如果可以转换成数字的话则会采用数字编码压缩空间`<br>`2) 会在ele_encoding&ele_size上体现出来` |

<br><br>

### 三、ziplist编码hash类型的示例图：

<br>

- 假设`hSet hkey foo bar`：

![](assets/ziplist_encodes_hash.png "redis_encoding_ziplist_encoding_hash")
