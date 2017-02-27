# StringBuilder(StringBuffer)
> 1. 可变字符串类（相对于String不可变），可以直接修改其内容.
> 2. Builder单线程，Buffer多线程.
> 3. 单线程情况下用Builder，比多线程效率高.
> 4. 由于都是和String同系，因此**String有的大多数方法它俩也有**.
>
>> 1. 这里**只介绍StringBuilder**，StringBuffer方法完全相同.
>> 2. **记住！**StringBuilder|Buffer中所有修改字符串的方法**不管反不返回新的字符串**都将**改变原字符串**！！！
>> 3. 它俩都**实现了toString**，返回的是String类型.

<br><br>

## 目录

1. [长度和容量](#一长度和容量)
2. [构造器](#二构造器)
3. [最常用的方法：逆序、连接（追加）、插入子串](#三最常用的方法逆序连接追加插入子串--)
4. [次常用：设置单个字符、删除子串、替换](#四次常用设置单个字符删除子串替换--)

<br><br>

### 一、长度和容量：[·](#目录)
> 由于是可变（可修改），因此是具有动态容量属性的，容量可以自己设置.

- 容量和有效长度：
  1. 有效长度即有效字符的个数，即s.length()的返回值.
  2. 容量是指保存字符的空间的预设大小（以字符个数为单位），即**能装填的字符个数上限**，**必须要≥有效长度**.
    - 容量在**只在初始化时确定**，一旦确定不能修改只能查看.

```Java
int length();  // 返回有效长度

void setLength(int newLength);  // 手动强制设置有效长度设置新的长度（小于原长就截断，大于原长则保留原串超出部分用Java的空字符填充）
// 1. 设置后，s.length()就等于newLength了！
// 2. 小于原长就截断，大于原长则超出部分填充二进制0（即空字符）.
// 3. 容量会根据有效长度智能调整，毕竟容量必须要≥有效长度的

int capacity(); // 查看容量（字符个数的上限）
```

<br><br>

### 二、构造器：[·](#目录)
> 构造时必须确定容量大小（当然有默认的容量大小）.
>
>> 可以看到唯一的缺点就是**木有通过二进制序列转换的版本**，如果有这种需求**必须通过String进行中间转换**.

```Java
StringBuilder([int capacity]);  // 单位是字符个数，默认16个字符

StringBuilder(String|CharSequence another);  // StringBuilder、StringBuffer同样是CharSequence
// 容量默认为[another.length() + 16]
```

- **其实String、StringBuilder、StringBuffer、char[]都属于CharSequence，之后就用CharSequence表示所有字符串了.**

<br><br>

### 三、最常用的方法：逆序、连接（追加）、插入子串  [·](#目录)

<br>

**1. 逆序：**

```Java
StringBuilder reverse();
```

<br>

**2. 连接（追加）：** 多个重载版本

```Java
StringBuilder append(CharSequence s[, int start, int end]); // 一般形式，追加s[0, )或s[start, end)
StringBuilder append(char[] str, int offset, int len);  // char[]特供版，追加str[offset, offset + len)，只有char[]有这个版本

StringBuilder append(type val);  // type支持所有的基本数据类型
StringBuilder append(Object obj);  // 多态调用obj.toString
```

<br>

**3. 在指定位置插入：** 和连接的参数列表形式**完全一样**，4种

```Java
// 插入字符串
StringBuilder insert(int offset, CharSequence s[, int start, int end]);  // 一般形式
StringBuilder insert(int offset, char[] str[, int start, int len]);  // char[]特供版

StringBuilder insert(int offset, type val); // 插入一个基本类型
StringBuilder insert(int offset, Object obj);  // 多态插入obj.toString
```

<br><br>

### 四、次常用：设置单个字符、删除子串、替换  [·](#目录)
> 全部都要注意注意**越界异常[IndexOutOfBoundsException]**

<br>

**1. 设置序列中的字符：**

```Java
void setCharAt(int index, char ch);  // 单字符设值，this[index] = ch;
StringBuilder deleteCharAt(int index);  // 单字符删除，删掉this[index]
```

<br>

**2. 删除子串：** 注意越界异常[IndexOutOfBoundsException]

```Java
StringBuilder delete(int start, int end); // 删除this[start, end)，只有这一个版本
```

<br>

**3. 替换：**

```Java
StringBuilder replace(int start, int end, String str); // this[start, end) -> str，只有这一种版本
```
