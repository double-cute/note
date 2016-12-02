# 分支的增删查：git branch.md
> 都是git branch系列的命令.

<br><br>

## 目录
1. [查看分支]()
2. [创建分支]()
3. [删除分支：git branch -d|D branch_name]()
4. [分支重命名：git branch -m|M old_branch new_branch]()

| 命令 | 说明 |
| --- | --- |
| git branch | 罗列版本库中存在的分支（当前分支用*标示） |
| git branch **-r** | 罗列所有远程分支 |
| git branch new_branch [startpoint] | 在startpoint（默认为HEAD）建立一个新分支 |
| git **checkout -b** new_branch [startpoint] | 建立的同时切换到新分支上 |
| git branch -d\|D branch_name | 删除分支（-D强制删除）｜
| git branch -m\|M old_branch new_branch | 分支重命名（-M强制重命名）|

### 一、查看分支：[·](#目录)

1. git branch
  - 简单罗列出版本库中存在的所有分支.
    - 其中当前工作分支会用\*标识出来.
2. git branch **-r**
  - r就是remote的缩写.
  - 罗列远程分支（.git/refs/remote下的**本地远程分支**）.

<br><br>

### 二、创建分支：[·](#目录)

1. git branch new_branch [startpoint]
  1. 创建一个名为branch_name的分支.
    - new_branch就是这个新建分支的分支引用.
  2. startpoint表示在哪个节点上创建该分支.
    - 不写则默认基于当前HEAD.
    - startpoint可以是任何节点引用.
2. git **checkout -b** new_branch [startpoint]
  - 创建分支的**同时切换到**新创建的分支上.

<br><br>

### 三、删除分支：git branch -d|D branch_name  [·](#目录)

1. -D表示强制删除，不做任何提示.
2. -d会检查该分支是否合并过，如果没有合并过则拒绝删除.
  - 毕竟删掉一个没有用过的分支是非常不合理的.
  - 尽量不用-D选项.

<br><br>

### 四、分支重命名：git branch -m|M old_branch new_branch  [·](#目录)
> 将old_branch重新命名为new_branch.

1. -m会检查新名称new_branch是否已经存在（和其它发生冲突），如果冲突则拒绝改名.
2. -M是强制重命名，直接**覆盖**冲突分支，相当危险（尽量不用）.
