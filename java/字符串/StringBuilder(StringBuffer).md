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

1. []()
2. []()

<br><br>

### 一、长度和容量：
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

### 二、构造器：
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

### 三、最常用的方法：逆序、连接（追加）、根据索引设置字符

<br>

**1. 逆序：**

```Java
StringBuilder reverse();
```

<br>

**2. 连接（追加）：**
    1) StringBuilder append(type val);
    2) 追加的类型包括所有Java的基础类型（boolean、int、float等），肯定是先转换成字符串的形式再追加的；
    3) 也可以是引用类型（当然包括String自己啦！），里面有一个版本append(Object obj);，只要该类型实现了toString方法就会将toString的结果追加到字符串末尾；
    4) 还提供了专门追加字符序列的版本：
         i. StringBuilder append(char[] str);
         ii. StringBuilder append(char[] str, int offset, int len);

3. 设置序列中的字符：
    1) 为某个字符重新赋值：void setCharAt(int index, char ch);  // 相当于s[index] = ch;
    2) 删除指定位置的字符：StringBuilder deleteCharAt(int index);



6. 插入：
    1) StringBuilder insert(int offset, type val);  // 在指定索引出插入val的字符串形式
    2) type和上面一样支持所有Java基础类型以及Object（默认调用toString方法）、String；
    3) 插入字符数组的版本：
         i. StringBuilder insert(int offset, char[] str);
         ii. StringBuilder insert(int index, char[] str, int offset, int len); // 从index的位置开始插入，只不过不能同时用两个offset命名罢了，这里的index和上面的offset是一个意思

7. 删除指定区间：StringBuilder delete(int start, int end); // 删除[start, end)，记住！是左闭右开的！！

8. 替换：StringBuilder replace(int start, int end, String str); // 将[start, end)替换成str
