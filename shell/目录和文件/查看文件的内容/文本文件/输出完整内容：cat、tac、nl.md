# 输出完整内容：cat、tac、nl
> **如果文件内容超出终端高度则只能显示部分.**
>
>> - 如果在：
>>    1. 图形界面下：其余部分只能拉动终端的滚条进行查看.
>>    2. 在命令行界面下：**没有滚条，其余部分压根儿没法查看到**.
>>       - 在这种情况下就只能使用部分查看命令查看了（head、tail、less、more）.
>>
>>> 因此，完整内容查看命令 **适用于内容较少的文件**.

<br><br>

## 目录

<br>

- coreutils:
   - /bin/cat
   - **/usr/bin**
      1. tac
      2. nl

<br>

1. [cat](#一cat)
2. [tac](#二tac)
3. [nl：专业显示行号](#三nl专业显示行号--)

<br><br>

### 一、cat：[·](#目录)
> concatenate files and print on the standard output.
>
>> 从第1行开始顺序输出文件的全部内容.
>>
>>> - 一定是会输出空行的！

<br>

**1.&nbsp; 命令格式：**

- 一次可以输出多个文件的完整内容.
   1. 按照命令中文件出现的先后顺序依次输出.
   2. **文件之间没有间隔，紧密连接**.

```Shell
cat [选项] 文本文件列表
```

<br>

**2.&nbsp; 选项：**

- 行号从1计算.
- 对于一次cat多个文件，**多个文件内容之间的行号是连续**.
   - 遇到下一个文件时行号不会从1重新开始.

| 选项 | 说明 |
| --- | --- |
| -b | 加上行号，空白行输出，但不分配行号 |
| -n | -b的基础上，给空白行分配行号 |
| -s | 将多个连续空行压缩成一个空行输出 |
| -E | 换行符用$表示 |

- -b选项有时候很有用，特别是执行.sql脚本时，报错提示的行号都是忽略空行的！
   - 因此查错时就必须使用-b选项排除空行.

<br><br>

### 二、tac：[·](#目录)
> 就是逆序的cat，tac完全就是cat的倒写嘛！
>
>> 从最后一行开始逆序一行一行输出到第1行.

<br>

**1.&nbsp; 命令格式：没有选项，其余和cat完全一样**

```Shell
tac 文件列表
```

<br>

**2.&nbsp; 没有选项该如何输出行号之类的呢？ 当然是使用管道咯！**

```Shell
# 这样就可以为逆序输出加上行号了，并且可以实现和cat完全相同的选项功能.
tac a.txt | cat -sn
```

<br><br>

### 三、nl：专业显示行号  [·](#目录)
> number lines of files.
>
>> 专注于显示行号，但没有cat那样可以识别换行符、制表符、压缩空行等功能.

<br>

**1.&nbsp; 命令格式：**

```Shell
nl [选项] 文本文件列表
```

<br>

**2.&nbsp; 选项：**

| 选项 | 说明 |
| --- | --- |
| -ba | 为空行分配行号（默认不分配）|
| -nrz | 行号加上前缀0 |
| -w[N] | 行号占用几位（默认占用6位）|

<br>

**3.&nbsp; 示例：**

```Shell
nl -ba -nrz -w3 a.txt
```
