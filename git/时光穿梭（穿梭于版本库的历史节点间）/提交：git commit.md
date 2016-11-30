# 提交：git commit
> 提交也是一种特殊的穿梭操作，它包含两个动作：
>
>   1. 将stage作为节点插入到当前HEAD之后.
>   2. 将HEAD后移一位到刚插入的节点处.
>
>> git commit对工作区不产生任何影响，仅仅是stage -> 版本库的**一次性**买卖.

<br><br>

## 目录
1. [git commit强制要求提交说明](#一git-commit强制要求提交说明)
2. [一条龙提交：git commit -a](#二一条龙提交git-commit--a--)
3. [空提交：git commit --allow-empty](#三空提交git-commit---allow-empty--)

| 命令 | 说明 |
| --- | --- |
| git commit -m comments | 正常提交（不允许空提交）|
| git commit **-a** -m comments | 一条龙提交工作区中被跟踪的文件 |
| git commit **--allow-empty** -m comments | 强制空提交 |

<br><br>

### 一、git commit强制要求提交说明：[·](#目录)
> git commit命令**强制**要求提交的时候附上提交说明.

- 提交说明作为一次提交的总结性描述是非常利于有效的版本控制的.
  1. 可以说没有提交说明就不能从历史中查看出这次提交“做了什么”.
  2. 或者说，从某种程度来讲，没有提交说明就无法进行版本控制.
    - 没有提交说明就是失去了版本控制的意义.
  3. 版本控制的核心就是要了解每次提交都做了什么.

<br>

| 命令 | 说明 |
| --- | --- |
| git commit -m comments | 命令行上直接输入提交说明并提交 |
| git commit | 将强制用vi打开一个临时文件，在里面输入提交说明并保存退出后才能提交 |

<br><br>

### 二、一条龙提交：git commit -a  [·](#目录)

- 该命令完成两步操作：
  1. 首先将工作区中所有**被跟踪**的文件git add到stage中.
  2. 再将stage提交到版本库.
- 总结来说就是将工作区的修改一次性提交到版本库中的一条龙.

<br>

- 注意：
  1. 由于该命令看不到工作区到stage的过程，因此不那么可控.
    - 所以建议小白尽量不用，老司机则慎用.
  2. 该命令也是强制要求加上提交说明的，因此：
    1. git commit -a -m comments   # 命令行说明
    2. git commit -a  # vi编辑临时文件加入提交说明

<br><br>

### 三、空提交：git commit --allow-empty  [·](#目录)
> 如果stage相对于HEAD没有发生任何改变，git默认是不允许提交的.
>> 会直接返回错误，拒绝提交.

- 上述这样的提交（stage相对于HEAD没改变）就叫做空提交.
  1. 空提交一般不会应用到，除了一些特殊场合，比如调试git等.
  2. git专门提供的空提交命令：git commit **--allow-empty**
    - 但同样需要有提交说明（-m命令行，没有-m则vi编辑）.
