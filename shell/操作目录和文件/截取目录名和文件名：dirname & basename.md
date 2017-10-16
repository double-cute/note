# 截取目录名和文件名：dirname & basename
> 都是纯字符串解析，不会在真实的文件系统中验证项目是否存在！

<br><br>

## 目录

<br>

- coreutils: **/usr/bin/**
   1. dirname
   2. basename

<br>

1. [dirname](#一dirname)
2. [basename](#二basename)

<br><br>

### 一、dirname：[·](#目录)
> 截取目录名.

**1.&nbsp; 格式：**

```Shell
dirname 路径名列表
```

<br>

**2.&nbsp; 示例：**

```Shell
# 1. 正常：
$ dirname dir/ home/user/a.txt
dir
/home/user

# 2. 特殊：
$ dirname / /home/ a.txt dir/
/
/home
.   # 默认为当前目录
.   # dir/没有指定前缀目录，因此同样输出当前目录
```

<br><br>

### 二、basename：[·](#目录)
> 截取文件名.
>
>> 实现：先截取最后一个/（如果没有/就截取全部）之后的所有字符.
>> - 再根据后缀选项截掉指定的后缀.

<br>

- 格式：basename 路径名列表
- 选项 **-s 后缀, --suffix=后缀** 同时还能去掉指定后缀.
   - 注意：后缀只能指定一个，**不能指定多个**.

<br>

- 示例：

```Shell
# 1. 正常：
$ basename -s .h dir/a.h b.h /home/user/c.h
a
b
c

# 2. 特殊：总之，不能为空！
$ basename -s .h / /home/user/ /usr/.h .h
/
user
.h
.h
```
