# 简化远仓URL地址-注册远仓（以及其它操作）：git remote
> 对远程仓库的所有配置最终都会写入**.git/config**文件中.
>
>> 所有的远仓配置命令实际上都是在修改.git/config文件，因此最直接的配置方式就是编辑.git/config文件.

<br><br>

## 目录

1. [简化远仓URL地址的基本思路]()
2. [注册远仓使用的命令：git remote add]()
3. [git clone命令会默认注册一个名为origin的远仓]()
4. [远仓的查看、地址更改、重命名远仓：git remote系列]()

| 命令 | 说明 |
| --- | --- |
| git remote add 远仓标识符（远仓别名） 远仓URL地址 | 向本地仓库注册一个远仓<br>特别是git init出来的本地仓库，必须要用该命令来注册第一个远仓 |
| git clone命令 | 会自动为clone的地址注册一个名为origin的远仓 |
| git clone **-o 自己指定的远仓别名** 远仓URL地址 | 自己重新指定git clone注册的远仓别名 |
| git remote | 简化版查看本地版本库中所有的远仓 |
| git remote **-v** | 详细版查看本地版本库中所有的远仓 |
| git remote **set-url** 已存在的远仓的别名 新的URL地址 | 修改远仓的URL地址 |
| git remote **rename** 就的远仓名 新的远仓名 | 远仓重命名 |
| git remote **rm** 远仓别名 | 删除指定远仓（.git/config中删除相应的节而已）|

<br><br>

### 一、简化远仓URL地址的基本思路：[·](#目录)
> 要说简化，首当其冲的当然是**远仓地址**部分了.
>
>> 毕竟远仓URL比较长，字符特别多，因此最应该得到简化.

<br>

- 最容易想到的解决方案就是**用一个符号**来代替远仓地址.
  - 这个符号就是**远仓标识符**，俗称**远仓别名**.
    - 配置合适的符号来代替指定远仓地址的过程就是**在本地仓库中注册远程仓库**.

> 但实际中远仓标识符**绝不仅仅只有代替远仓URL地址的功能**，还有更多的含义和用途！这个后面会细说.

<br><br>

### 二、注册远仓使用的命令：git remote add  [·](#目录)

- **git remote add 远仓标识符（即远仓别名） 远仓URL地址**
  - 示例：git remote add **test_repo** git@github.com:double-cute/test.git
    - 向本地仓库注册了一个**git@github.com:double-cute/test.git**远仓，其别名为**test_repo**.
  - 之所以是别名，那是因为这个别名是由本地仓库的用户自己随意取的，**可以不和真实的远仓名称相同**.
    - 例如：一个远仓git@github.com:double-cute/test.git的真实名称应该是test，但是我可以在本地仓库中用test1来代替该URL，作为远仓别名.
    - 有了标识符，推送或拉回的时候就可以直接用别名来代替冗长的远仓URL了，例如：git push test1 master:master

<br>

- **.git/config中的表现：**
  - 多了一个[remote "远仓别名"]的节，其中的属性url的值为注册时给出的远仓URL地址，例如：

```
[remote "repo_test"]
    url = git@github.com:double-cute/test.git
```

<br>

- 异常处理：
  1. 只检查远仓标识符（别名）是否冲突：如果新注册的远仓别名已经存在则会报错，拒绝注册.
  2. **不检查注册的远仓URL地址是否正确可用！**
    - 即使你git remote add test xxx也不会报错，仅仅是无脑地将xxx作为test的url写入.git/config文件.

<br>

- 灵活用法：
  1. 可以同时注册多个远仓：
    - 例如：一个远仓专门用来pull，一个专门用来push作为测试仓库之类的.
  2. 可以为同一个远仓注册多个不同的别名：
    - 例如，同一个远仓git@github.com:double-cute/test.git在本地同时注册一个release别名和一个bugfix别名.
      - 然后只用releasep推送经过完善测试的发布性分支，只向bugfix推送修复bug的补丁分支.
    - 特别适用于同一个远仓具有多种功能性分支的场景.

<br><br>

### 三、git clone命令会默认注册一个名为origin的远仓：[·](#目录)

- git clone命令会自动为**被clone的远仓**注册一个名为origin的本地别名.
  - 例如：git clone git@github.com:double-cute/test.git
  - 接下来查看test/.git/config文件会看到

```
[remote "origin"]
    url = git@github.com:double-cute/test.git
```

- 但如果想自己指定git clone的远仓别名，则可以使用-o选项指定：
  - git clone -o 自己重新指定的远仓别名 远仓URL地址
  - 示例：git clone -o test git@github.com:double-cute/test.git

<br>

- 对于不是git clone下来的仓库，必须自己手动git remote add来注册远仓了.
  - 特别是git init出来的本地仓库，由于没有注册过远仓，因此必须自己手动git remote add了.

<br><br>

### 四、远仓的查看、地址更改、重命名远仓：git remote系列  [·](#目录)

- 查看本地仓库中注册的所有远仓：
  1. git remote
    - 只列出简单信息：只有远仓别名，每行一个远仓.
  2. git remote **-v**
    - 列出详细信息：每行一个远仓，既有别名，也有远仓对应的URL地址.

<br>

- 更改注册的远仓的URL地址：git remote **set-url** 远仓别名 **新的URL地址**
  - **或者直接修改.git/config中远仓的url属性值.**
  - 示例：git remote set-url origin git@github.com:auser/test.git
    - 将origin的url地址改为git@github.com:auser/test.git
  - 该命令同样不检查新的url是否正确合理，仅仅就是无脑地修改.git/config中远仓的url属性值.

<br>

- 远仓重命名：git remote **rename** 旧远仓别名 新别名
  - 示例：git remote rename origin repo_test
    - 将远仓origin的别名改为repo_test
  - 异常处理：
    - 新别名不能和已有的远仓别名冲突，否则报错，拒绝更改.

<br>

- 删除注册的远仓：git remote **rm** 远仓别名
  - 直接从.git/config中删除相应的远仓节.
  - 异常处理：如果指定的远仓不存在会报错.
    - 但这通常无关紧要.
