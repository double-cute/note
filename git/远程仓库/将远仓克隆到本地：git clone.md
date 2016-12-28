# 将远仓克隆到本地：git clone
> 和git init类似的是，都是在本地创建仓库，都是从无到有的过程，只不过clone出来的不是空的罢了，而是别人的进度.
>   - 克隆之前先要保证你有权限.  （HTTPS通信无所谓，SSH需要先配置好权限）
>
> 克隆的远仓对象可以是工作库也可以是裸库.
>
>> 克隆到本地的结果同样可以是工作库也可以是裸库.
>>   - 即源和目的都可以是工作库或者裸库.



<br><br>

## 目录

1. [克隆的目标（源，即远仓）如何表示？](#一克隆的目标源即远仓如何表示--)
2. [克隆的结果（目的，即克隆出来的本地仓库）如何表示？](#二克隆的结果目的即克隆出来的本地仓库如何表示--)
3. [示例](#三示例)

| 命令 | 说明（远仓工作库的URL建议精确到.git） |
| --- | --- |
| git clone 远仓URL | 在当前目录克隆出一个和远仓同名的工作区 |
| git clone 远仓URL **wks** | 自己指定结果工作区的名字：在当前目录克隆出一个名为wks的工作区 |
| git clone **--bare** 远仓URL | 在当前目录克隆出一个和远仓同名的裸库（xxx.git）|
| git clone **--bare** 远仓URL **path/xxx.git** | 自己指定克隆出的裸库的路径和名字 |

<br><br>

### 一、克隆的目标（源，即远仓）如何表示？  [·](#目录)
> 当然是URL咯，但区别在于远仓到底是工作库还是裸库.

- 目标是**工作库**：
  - URL的末尾既可以是工作区也可以是.git
    - 两者等价，没有任何区别.
    - 示例：
      1. https://acc@xxx.com/acc/repo/test1   # 前提是test1必须是个工作区，底下有个.git才行
      2. file:///user1/code/src/repos/test3/.git

<br>

- 目标是**裸库**：很简单，直接写URL即可，没有其它选择
  - 例如：git@acc1:acc1/test-note.git   # 裸库的.git/暴露在外界，没有其它写法，只能这样写

<br>

- 良好的规范：只是建议并不是强制，但是这种风格很好
  - 不管是工作库还是裸库，URL都以.git结尾，例如：file:///test/a/.git
    - 即如果目标是工作库，把URL也精确到.git算了.
  - 这样做的理由是：毕竟.git/才是真正的版本控制目录，而非工作区，精确到.git后可以一目了然.

<br><br>

### 二、克隆的结果（目的，即克隆出来的本地仓库）如何表示？  [·](#目录)
> 结果如果是工作库则不加任何选项，如果是裸库则要加**\-\-bare**选项.

- 如果结果要求是**工作库**，则直接给出工作区目录的命名即可.
  - 例如：MyWorkSpace
- 如果结果要求是**裸库**，则直接给出xxx.git的路径即可.
  - 例如：/test/abc.git

<br>

- 特殊的，如果目的结果**不写**，则默认和远仓同名：
  - 会直接在当前目录下创建一个工作区（里面有.git/）.
  - 工作区默认命名为远仓的名字：
    1. 如果远仓（目标）是工作库，则生成的工作区名称默认和远仓的工作区名称一样.
      - 例如：远仓是abc/.git，那克隆出来的工作区默认也为abc/
    2. 如果远仓（目标）是裸库，则生成的工作区名称默认为远程裸库的名称.
      - 例如：远仓是abc.git，那么克隆出来的工作区默认为abc/

<br><br>

### 三、示例：[·](#目录)

- 工作库 -> 工作库：
  1. git clone https://xxx.com/code/repo/test
    - 当前目录中生成一个test/的工作区
  2. git clone file:///test/a/.git aaa   # 推荐写法，URL精确到.git
    - 当前目录中生成一个aaa/的工作区
- 工作库 -> 裸库：
  1. git clone --bare a/test/.git  # 推荐写法，URL精确到.git
    - 当前目录下生成一个test.git裸库.
  2. git clone --bare a/test
    - 同样是在当前目录下生成一个test.git裸库.
  3. git clone --bare test/a aa.git
    - 当前目录下生成一个aa.git，前提是test/a必须是一个工作区.
  4. git clone --bare test/b/.git b.git  # 推荐写法，URL精确到.git
    - 当前目录下生成一个b.git
- 裸库 -> 工作库：
  1. git clone git@github.com:double-cute/note.git
    - 在当前目录中生成一个note/工作区
  2. git clone https://acc@github.com/acc/test.git lala
    - 在当前目录中生成一个lala/工作区
- 裸库 -> 裸库：
  1. git clone --bare git@github.com:double-cute/note.git
    - 在当前目录中生成一个note.git裸库.
  2. git clone --bare git@github.com:double-cute/note.git /user/code/test.git
    - 在/user/code/目录中生成一个test.git裸库.
