# push和pull的完整形式
> git pull和git push命令的完整形式非常长.
>   - 长命令的坏处就是：
>     1. 编写麻烦，如果命令需要多次重复使用则相当费时费力.
>     2. 命令过长容易写错.
>
>> 但是如果合理的配置远程仓库和本地git客户端通信的方式，可以可以大大简化push和pull命令.
>>   - 说白了，还是让git自动为你补充命令中缺省的部分.
>>   - 配置的意思就是事先约定好命令的缺省部分如何填写.

<br><br>

## 目录

1. [本地分支、远程分支、上游分支、上下游分支不同名现象]()
2. [git pull的完整形式]()
3. [git push的完整形式]()

| 命令 | 说明 |
| --- | --- |
| git pull 远仓地址 远程分支1:本地分支1 远程分支2:本地分支2 ··· | git pull完整形式 |
| git push 远仓地址 本地分支1:远程分支1 本地分支2:远程分支2 ··· | git push完整形式 |
| git push 远仓地址 :远程分支1 :远程分支2 ··· | 删除指定的远程分支 |
| git push 远仓地址 **--delete** 远程分支 | 同样也是删除指定的远程分支 |

<br><br>

### 一、本地分支、远程分支、上游分支、上下游分支不同名现象：[·](#目录)

- 本地分支：
  - 顾名思义，就是本地仓库中的分支.
  - 精确地讲就是**git branch命令输出的所有分支**.

<br>

- 远程分支：
  - 显而易见，就是远程仓库中的分支.

<br>

- 上游分支：精确地讲，应该是“本地分支的上游分支”
  - 是指本地分支对应的远程分支.
  - 这是一个**特殊的动态的概念**，要根据当时的情景来确定一个本地分支的上游分支是什么：
    1. 当你push一个本地分支到远程分支时，那么该远程分支就是该本地分支的**push上游分支**.
    2. 当你pull一个上游分支到本地分支时（精确地讲应该是同步到本地分支），那么该远程分支就是该本地分支的**pull上游分支**.

<br>

- 上下游分支不同名现象：
  - git允许本地分支和其对应的上游分支不同名.
  - 比如：同一个本地分支A，既可以push到远程分支REMOTE_B上也可以push到远程分支REMOTE_C上，不要求其上游分支也和A的名称（A）一样.

<br><br>

### 二、git pull的完整形式：[·](#目录)

- git pull 远仓地址 远程分支1:本地分支1 远程分支2:本地分支2 ···
  - 意思很显然，就是将远仓中的指定分支同步到指定的本地分支上，其同时指出了该命令各个本地分支对应的**pull上游分支**.
    - 其中隐含的信息有：
      1. 可以**只**拉回远仓中的某几个分支同步到本地.
        - 之前简化版的git pull默认拉回远仓的所有分支到本地.
      2. 本地分支的上游分支（远仓中对应的分支）的命名可以和该本地分支**不同**！
        - 例如：git pull git@github.com:double-cute/test.git master:master **bra**:**branch**
        - 这里，本地分支branch的上游分支名为bra，两者可以不同，而master:master就是同名的典型.

<br>

- 异常处理：
  1. 如果远程分支不存在则直接报错“远程仓库中不存在该分支！”
  2. 对于命令中指定的本地分支：
    - 如果存在，则正常拉回.
    - 如果不存在则创建一个新的该本地分支.

<br><br>

### 三、git push的完整形式：[·](#目录)

- git push 远仓地址 本地分支1:远程分支1 本地分支2:远程分支2 ···
  - 将指定的本地分支推送到指定远仓中的指定远程分支上，同时指出了本地分支对应的**push上游分支**.

<br>

- 异常处理：
  1. 对于命令中指定的远程分支：
    - 如果不存在，则会自动**在远仓中**创建一个同名的分支.
      - 例如：git push origin master:curv
        - 如果curv分支在远仓origin中不存在，则会自动在远仓中创建一个名为curv的远程分支.
    - 如果存在，则正常推送.
  2. 如果命令中指定的本地分支不存在，则会直接报错拒绝推送.

<br>

- 特殊的：删除远仓中的远程分支
  - 在push命令中不谢本地分支即可，例如：git push origin :master
    - 这就意味着把**空**分支推送到远程的master上，即删除远程分支master.
  - 当然git也提供了显式删除远程分支的命令：git push 远仓地址 --delete 要删除的远程分支名
    - 示例：git push origin **--delete** master  # 把远仓origin中的远程分支master删除
    - 这两个命令是等价的.
  - 注意！这个删除是真正的删除，可以在github上试一下，删除后就真的在github网页上也找不到这个分支了！！

<br>

- 根据初学时的经验，这两个命令可以省略后面所有内容直接使用（git pull和git push）.
  - 这就意味着远仓地址部分和分支指定部分都可以通过合理的配置省去.
  - 后面的章节中会详细讲解如何配置远仓以化简pull和push命令.
