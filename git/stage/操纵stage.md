# 操纵stage
> - 其实就是操纵stage中的文件：添加、移动、删除
> - `git rm`、`git mv`均会影响工作区.


<br><br>

## 目录
1. [git add](#一git-add)
2. [git rm](#二git-rm)
3. [git mv](#三git-mv)
4. [操纵stage的规范](#四操纵stage的规范)

<br><br>

### 一、git add：[·](#目录)

<br>

- 就是单纯地将工作区的目标文件加入到stage中.
  - 这个“加入”的含义是：
    1. 如果stage中原本不存在就是新添加.
    2. 如果stage中已经存在同名文件了，则是直接覆盖.

<br>

- stage和跟踪的具体含义：
  - stage中的文件都是被跟踪的文件，**stage中没有的文件都是没被跟踪的文件**.
    - 具体说跟踪文件的定义：
      1. 被跟踪的文件 **只能是工作区中的** 文件，stage和版本库没有这一说法.
      2. 跟踪文件（工作区中被跟踪的文件）就是指那些 **和stage中同名的文件**.
         - 也就是说stage相当于一张名单，名单中的那些文件名都是被跟踪的工作区中的文件.

<br>

- 命令：git add 文件列表
  1. 将目标若干文件 **添加/覆盖** 到stage中.
  2. 特殊的：
     - 如果stage中存在一个文件a.txt，但现在你在工作区中删除了a.txt（rm a.txt).
     - 在这种情况下git add a.txt就会把不存在的a.txt“覆盖”到stage中，相当于删除了stage中的a.txt
     - 因此删除stage中文件的一种方法就是：先工作区中rm，然后再git add
- 命令：git add **-u**
   1. u即可update的意思，即刷新.
   2. 将工作区中和stage同名的文件全部覆盖到stage，即刷新stage.
   3. 换句话说就是刷新一遍“被跟踪的文件”.
- 命令：git add **-A**
  - 将工作区中所有没被gitignore忽略的内容全部刷到stage中.
     1. 被跟踪的刷新到stage中.
     2. 没被跟踪的（同时没被gitignore忽略的）添加stage中.
- 命令：git add **-f** 文件列表
   - 强制将指定的工作区文件加入到stage中，即使是.gitignore中记录的文件.
   - 一旦被加入stage，gitignore就无效了.

<br><br>

### 二、git rm：[·](#目录)

- 命令：git rm 文件列表

1. 直接删除 **stage** 中的指定文件.
   - 也就是说**只能删除被跟踪**的文件.
   - 如果stage中不存在，则命令执行失败！
2. 命令分两步执行：从stage延展到工作区
   1. 删除stage中的指定文件.
   2. 删除工作区中的同名文件.

<br><br>

### 三、git mv：[·](#目录)

- 命令：git mv src dest
  - 移动，其实也是重命名.
  - 简单地说成重命名好了.

1. 直接将 **stage** 中的src文件重命名成dest.
   - 只能重命名 **被跟踪的文件**.
   - 如果src不存在于stage则命令执行失败！
2. 该命令也是分两步执行的：从stage延展到工作区
   1. 在stage中将src重命名成dest（dest必然保存在stage中，因此dest直接被跟踪了）.
   2. 将工作区中的同名src也重命名成dest.

<br><br>

### 四、操纵stage的规范：[·](#目录)

1. 尽量少用 `git rm` 和 `git mv`，因为包含两步动作，从stage联动到工作区.
   - 这在一定层面上破坏了工作区和stage的操作隔离原则，容易混淆这两个区域.
2. 最最合理的做法还是先操作工作区，然后再将变化传递到stage.
   - 毕竟 `git rm` 和 `git mv` 是反向的（从stage传递到工作区），有点儿破坏开发逻辑.
   - `git rm` 对应的合理操作：`rm a b c; git add a b c`
   - `git mv` 对应的合理操作：`mv a b; git add a b`
      - `mv` 的操作其实是先删除a再添加b，因此 `git add` 也必须添加两个对象.
   - 简化版应该是：
      1. `git rm`：`rm a b c; git add -u`    ## 删除一般意味着之前添加到stage过了
      2. `git mv`：`mv a b; git add -A`   ## 新产生的b肯定没有添加到stage过
