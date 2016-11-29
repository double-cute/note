# 查看状态：git status
> 目标就是查看两层信息：使用git status命令查看
>
>   1. **工作区相对于stage** 有哪些文件发生改动.
>   2. **stage相对于当前HEAD** 有哪些文件发生改动.
>> 注意！这的“改动”是指文件的**添加、删除、修改**这三种信息.

<br><br>

## 目录
1. [详细信息：git status]()
2. [简化信息：git status -s]()
3. [当前处于哪个分支：git status -b]()
4. [跟踪信息：git status --ignored]()

| 命令 | 说明（不加-s都是显示详细信息）|
| --- | --- |
| git status **-s** | 查看简化双状态 |
| **git checkout [HEAD]** | 查看简化单状态（只有stage相对于HEAD的变化）|
| git status **-b -s** | 查看当前处于哪个分支 |
| git status **--ignored -s** | 查看被忽略的文件 |

<br><br>

### 一、详细信息：git status  [·](#目录)
> 不加选项的git status将显示完整、详细的状态信息.

1. 出现"not staged..."字样的表示工作区相对于stage还未git add的文件.
2. 出现"to be committed.."字样的表示stage相对于HEAD还未git commit的文件.
3. 显示的信息非常详尽，不仅会列出改动的文件，还告诉你接下来可以做什么动作.
  - 缺点就是输出信息太多，不够简洁.
  - 适合对于git命令还不是特别熟悉、还处于学习和上升阶段的新手.

<br><br>

### 二、简化信息：git status -s  [·](#目录)
> 加了一个选项-s，表示简化输出信息，s就是simplified简称.

- 输出中每行一个文件，只不过文件的开头会有两个标志位，例如：

```
AM test.txt
 A main.
```

- 标志位的含义：
  1. 第一位表示stage相对于HEAD的变化.
  2. 第二位表示工作区相对于stage的变化.
  3. 标志位的内容：
    1. A：add，添加.
    2. D：delete，删除.
    3. M：modify，修改.

<br>

- 特殊的：git **checkout** [HEAD]
  - 查看简化单状态.
  - 相当于git status -s命令但**只有一个stage相对于HEAD变化的标志位**.
  - [HEAD]要么不加，要加就只能是HEAD.
    1. 这点非常特殊，这有这两种写法才是简化单状态查看.
    2. 如果HEAD改成其它就变成了切换分之或者强制断头了，非常危险.
      - 小白尽量少用.
      - 熟练工、自强用的6没事儿.

<br><br>

### 三、当前处于哪个分支：git status -b  [·](#目录)
> 加了一个-b选项后会显示当前工作在那个分支上.

1. 单纯的一个git status -b命令会显示详细信息：
  1. 会在第一行显示当前工作分支.
  2. 后面的内容和普通的git status命令一样，显示完整、详细的状态信息.
2. 因此场合-s配合使用：git status -b -s：
  1. 同样在第一行显示当前工作分支.
  2. 后面显示git status -s的内容.

<br><br>

### 四、跟踪信息：git status --ignored  [·](#目录)

- 会显示**工作区中**有哪些文件被.gitignore强制忽略了.
  - 但不过显示的详细信息（和git status原理一样），还会提示你可以使用git add -f强制跟踪等.
- 因此常和-s配合使用：git status --ignored -s
  1. 标志位只有一位.
  2. !!：表示被.gitignore忽略的.
  3. ??：表示还未被跟踪的（未被git add过的，但有没有被.gitignore忽略）.
