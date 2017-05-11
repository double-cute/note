# BitSet
> 二进制位向量.
>
>> 1. 二进制位数组.
>> 2. 可以通过索引访问第index个二进制位.
>> 3. 底层当然是用byte数组保存，但不过是动态可扩展的，因此有capacity物理容量.
>>
>>> - **不会索引越界异常！越界的全部看成是0.**


<br><br>

## 目录

1. [构造器]()
2. [equals、hashCode、clone]()
3. [容量 & 长度 & 判空 & 1的个数 & 2String信息]()
4. [通过索引取值]()
5. [设值]()
6. [和另一个位向量做位运算（集合运算）]()
7. [定位]()
8. [和整型数组相互转换]()

<br><br>

### 一、构造器：[·](#目录)

<br>

- capacity的单位是bit！
   1. 无参默认的初始capacity根据不同JVM不同.
   2. 如果显式指定initialSize，则一定保证真实的初始化 capacity ≥ initialSize.
      - 因为capacity一定为8的倍数，并且当capacity不够时，自动增长的大小也是8的倍数.

```Java
BitSet([int initialSize]);
```

<br><br>

### 二、equals、hashCode、clone：[·](#目录)
> 全部实现了，equals比较位向量内容，hashCode只跟位向量的内容有关，clone是深拷贝.

```Java
boolean equals(Object o);
int hashCode();
Object clone();
```

<br><br>

### 三、容量 & 长度 & 判空 & 1的个数 & 2String信息：[·](#目录)

<br>

**1.&nbsp; 获取当前容量：**

```Java
int size();  // 直接返回当前的capacity
```

<br>

**2.&nbsp; 获取当前长度：**

```Java
int length();  // 返回最高位的1的索引+1
```

<br>

**3.&nbsp; 判空：是否全部都是0**

```Java
boolean isEmpty();
```

<br>

**4.&nbsp; 1的个数：**

```Java
int cardinality();
```

<br>

**5.&nbsp; 字符串信息：**

```Java
// 返回：{为1的索引列表}
String toString();

// 例如：{1, 67, 345}，表示在索引1, 67, 345处的位为1，其余为0
```

<br><br>

### 四、通过索引取值：[·](#目录)

<br>

```Java
// 1. 取单个位
boolean get(int bitIndex);

// 2. 取一个子区间
BitSet get(int fromIndex, int toIndex);
```

<br><br>

### 五、设值：[·](#目录)
> 其中，只有设1的操作可能会增加capacity的大小.
>
>> 1设在超出capacity的位置就会自动增长capacity.

<br>

**1.&nbsp; 自由设值：set，可设0也可以设1**

```Java
// 单点index设值、区间[index, toIndex)设值、默认设1或显式指定value
void set(int index[, int toIndex][, boolean value]);
```

<br>

**2.&nbsp; 清零：clear**

```Java
// 全清（无参）、单点index清零、区间[index, toIndex)清零
void clear([int index[, int toIndex]]);
```

**3.&nbsp; 取反：flip**

```Java
// 单点index取反、区间[index, toIndex)取反
void flip(int index[, int toIndex]);
```

<br><br>

### 六、和另一个位向量做位运算（集合运算）：[·](#目录)
> length小的那个补0，结果会改变this！

<br>

```Java
// 1. 位运算
void and(BitSet set);    // 与
void or(BitSet set);     // 或
void xor(BitSet set);    // 异或（按位加）
void andNot(BitSet set); // 与非（把this中set为1的那些位清零）

// 2. 集合运算
boolean	intersects(BitSet set);  // 两者有交集就返回true（有相同位是1就行）
```

<br><br>

### 七、定位：[·](#目录)

<br>

1. next|previous：表示搜索范围，≥ index | ≤ index
2. set|clear：表示要找的值，为1 | 为0
3. 返回满足条件的离index最近的那个位的索引.

```Java
int next|previous Set|Clear Bit(int index);
```

<br><br>

### 八、和整型数组相互转换：[·](#目录)
> 采用小端模型（低地址存放低字节，高地址存放字节）.
>
>> 只支持byte和long，不支持int！！

```Java
// 1. bits2ints
byte[] toByteArray();
long[] toLongArray();

// 2. ints2bits
static BitSet valueOf(byte[] bytes);
static BitSet valueOf(long[] longs);
```
