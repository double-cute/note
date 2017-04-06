# 查看当前工作目录：pwd
> print current working directory
>
>> 显示当前工作路径

<br><br>

| 用法 | 语法 | 说明 |
| --- | --- | --- |
| 无参 | pwd | 显示当前工作路径（同下）|
| 忽略链接（同上）| ~~pwd -L, --logical~~ | 如果当前目录是链接，则<br>显示**链接本身**的路径 |
| 显示链接指向 | pwd **-P, --physical** | 如果当前目录是链接，则<br>显示**链接所指向的目录**的路径<br>即使是**多层链接**也显示**指向的本体** |

- 示例：

```shell
# 设开始时的路径为$path

mkdir dir

ln -s dir dir.d  # 目录只能创建软链接，单层链接
ln -s dir.d dir.dd  # 多层链接

cd dir.d
pwd  # 或者pwd -L，都输出$path/dir.d
pwd -P  # 输出$path/dir，即dir.d指向的本体

cd ../dir.dd
pwd  # 输出$path/dir.dd
pwd -P  # 多层输出的还是本体，即$path/dir
```
