# 排序：sort
> 相当于C++的Algorithm和Java的Collections的工具算法.
>
>> 只不过sort命令 **只能用于** 对 **列表、集合、有序集合** 的排序.
>>
>> - 在对有序集合排序时，**将忽略元素的分数，只考虑元素自身的值**.
>
> <br>
>
>>> - sort的功能类似于SQL的select：
>>>    1. 不仅可以对容器中的元素进行排序.
>>>    2. 还可以 **连接** **2个** 容器键.
>>>       - 比如对key1容器中元素的排序以来key2容器中元素的值.
>>>    3. 还可以选择输出的内容.
>>>       - 比如可以输出被连接的容器键中的元素和当前容器中元素的组合等.
>>>       - 类似于SQL中select和from之间的部分.
>>>    4. 当然了，还可以选择 **升序/降序、根据字典序排/数字值大小排、限制输出结果** 的数量这3个功能.

<br><br>

## 目录

1. [sort命令的完整格式](#一sort命令的完整格式)
2. [BY other_key_pattern](#二by-other_key_pattern)
3. [GET pattern](#三get-pattern)
4. [示例](#四示例)
5. [sort命令的性能分析](#五sort命令的性能分析)

<br><br>

### 一、sort命令的完整格式：[·](#目录)
> **如果参考值相同，那就按照当前容器中元素的自身值排序.**
>
>> - 例如：当前容器中元素为1 2，参考链接值对应的是3 3，按照降序排列.
>>    - 由于参考值都为3，因此只能按照元素自身值排列，由于是降序，因此最后结果是2 1.

<br>

```Shell
sort 目标待排序的容器键键名sort_key [LIMIT offset count] # 只输出结果中的[offset, offset+count]区间，从0计
    # 1. 将结果存储（覆盖）到dstKey中，类型是list
      # 有该选项的话就返回结果列表的长度，否则就正常输出结果中的各元素
    [STORE dstKey]  
    # 2. 依据另一个string键的值或者hash键的字段值进行排序
      # 会将当前容器中的元素值替换成pattern中的*占位符带入
    [BY otherKey_pattern]
        # 2.1 选择按照数字大小排序还是按照字典序排序，默认是数字
          # 默认情况下（没有ALPHA）会尝试将当前容器中的元素解析为float
            # 如果不能解析则会返回错误！
          # 可以选择升序/降序，默认是升序（从小到大）
        [ALPHA] [ASC|DESC]
# 3. 选择输出那些感兴趣的数据
  # 如果是跟hash键连接，那么可以选择输出hash的哪些字段
[GET pattern1 GET pattern2 ...]
```

<br><br>

### 二、BY other_key_pattern：[·](#目录)
> 即排序的 **参考键**，也是 **依据键**.
>
>> - 就是根据参考键的内容对当前容器中的元素进行排序.
>> - 参考键只能是字符串类型键或者散列类型键.

<br>

- 应用场景：当前容器中的元素为ID，但是想根据该ID的其它数据域对ID进行排序.
- 连接条件：
   - 只能以当前容器中的元素值作为 **参考键键名的一部分**.

<br>

**1.&nbsp; 连接条件中的占位符'\*'的使用：**

1. 参考string键：例如，sort id_list BY id_*\_score.
   - 先迭代处id_list的元素：5 6 7 9
   - 然后用这些元素值替换占位符，找出id_5_score、id_6_score、id_7_score、id_9_score这几个键对应的值：
      - 99、88、91、60
      - 最后根据这4个值的大小顺序对id_list中的元素排序：
         - id_list的排序结果：9 6 7 5
         - 这些ID对应的socre：60 99 99 91
2. 参考hash键：那只能参考hash键的某个字段，连接条件的格式：带通配符的hash键键名->参考的字段名
   - 例如：sort id_list BY id_*->score
      - 那就是用id_list中的ID替换*，根据id_5、id_6、id_7、id_9的score字段值进行排序.

<br>

**2.&nbsp; 如果参考的键或者字段不存在：**

- 比如键名、字段名写错了之类的，那么就 **默认参考值为数字字符串"0"**.

<br>

**3.&nbsp; 常量连接条件：默认不做排序**

<br>

> 即连接条件（依赖键的键名）中没有使用通配符'\*'.
>
>> 因此也叫 **常量键名**.

- 依据一个无法迭代的固定的键来排序是没有意义的，遇到这种情况sort选择不排序，保持原来的顺序.
   - 例如：

```Shell
sort id_list BY id_5_score  # 没有意义，输出结果没有排序，保持id_list的顺序
```

<br>

- Redis判断常量键名的算法步骤：
   1. 检查连接条件中是否包含字符'\*'.
   2. 只要含'\*'，就判断为非常量名，就一定**会排序**！
      - 但是：
         1. 只有'\*'出现在键名中才会被当做占位符.
         2. 如果'\*'出现在字段名中就会被当做普通的纯字符.
   3. 如果不含'\*'，就判定为常量名，就一定**不排序**！

<br>

- 因此，特殊的：sort id_list BY id_17->score\*
   1. 首先'\*'出现在连接条件中了，因此判定为非常量，必须进行排序.
   2. 但是'\*'出现在字段名中，因此不被当做占位符（当做纯字符）.
   3. 因此会找id_17这个hash键中是否存在名叫'score\*'的键.
   4. 结果发现不存在，因此就默认参考值都为"0".
   5. 因此排序参考值全都是"0"，大家都一样，所以只能依据id_list元素自身的值进行排序了.
      - 因此，**最终结果是有序的**.

<br><br>

### 三、GET pattern：[·](#目录)
> 由于连接了另外一个容器键，所以可以选择输出的内容.
>
>> 比如，输出当前容器元素的同时也输出连接键的值（string）或者是某些字段（hash）.

<br>

- 也是使用占位符'\*'，必须出现在键名中.
   - pattern为'\#'表示当前容器键中自身的元素. （**GET #**）

<br><br>

### 四、示例：[·](#目录)

<br>

```Shell
# 排序的是博客ID列表
sort blogs:id LIMIT 4 5  # 只要5-10名
    STORE res_list  # 结果存储到res_list列表中
    BY "blog:*:info->view.count"  # 根据博客的访问量从大到小排序
        [ALPHA] DESC  # 访问量是数字，肯定不能用字典序排了
GET #  # 输出的每条记录中包含自身ID、博客标题、发表时间、访问量
GET "blog:*:info->title"
GET "blog:*:info->publish.time"
GET "blog:*:info->view.count"
```

<br><br>

### 五、sort命令的性能分析：[·](#目录)

<br>

**1.&nbsp; 时间复杂度：**

- O(n + mlogm)
   1. n是待排序的元素个数.
   2. m是要返回的元素个数.
      - 一般情况下（大多数情况下） n >> m，因此复杂度基本是线性.

<br>

**2.&nbsp; 空间复杂度：**

- 会临时开辟一个长度为n的空间暂存待排序的元素（所以时间复杂度很低，基本为线性）.

<br>

**3.&nbsp; 性能优化策略：**

1. 降低n，降低m.
2. 数据量较大时尽量开启store选项将结果缓存.
