# 变基：git rebase
> 让一条分支接到另一个分支上从而减少分支数量、让历史瘦身，不至于在git log查看时由于分支过多而眼花缭乱.

<br><br>

## 目录
1. []()

<br><br>

### 一、问题背景：让历史瘦身  [·](#目录)

- 频繁git merge所带来的后果：
  - 如果分支数量本身就很多，再频繁地git merge的话就会导致git log查看历史时眼花缭乱（各种连线绕来绕去）.
    - 在master主分支上这个问题尤为严重：很多分支最后都是要合并到主分支上的.

<br>

- 不合理的解决方法：
  - 第一反应可能是：直接删除合并过的分支不就行了吗？
  - 但这样做的缺点也是很明显的：删除分支会导致分支的提交历史丢失，未来将需要查阅的时候无从查起.

<br>

- git rebase命令应运而生：
  1. 既能减少分支数量（减少连线）.
  2. 又能包住分支的历史.

<br><br>

### 二、git rebase的基础用法：

- git rebase 嫁接点（节点）引用
  - 意义是：
    1. 先找到**基点**：当前HEAD和嫁接点的最近公共祖先节点.
      - 即HEAD所在分支和嫁接点所在分支离这两个点最近的那个重合点.
    2. 然后将(基点, HEAD]区间整个嫁接到嫁接点上.
    3. 嫁接完成后两分支融合，并且融合成HEAD的分支.
  - 举例：
    1. branch(b1): &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; **a** -> b(b1的起始) -> c -> d
    2. branch(b2): ... -> 1 -> 2 -> **a** -> x -> y -> z(HEAD)
    3. git rebase d
      1. 嫁接点是d，基点是a（d和HEAD的最近公共祖先）.
      2. 嫁接结果是：
        - ~~branch(b1):~~                  **a** -> b -> c -> d **-> x -> y -> z(HEAD)**
        - branch(b2): ... -> 1 -> 2 -> **a** ~~-> x -> y -> z(HEAD)~~
        - 等价于：... -> 1 -> 2 -> **a** -> b -> c -> d **-> x -> y -> z(HEAD)**

  c.最简单的嫁接：git rebase 嫁接点ID或引用
    i.意思是将当前HEAD所在的分支嫁接到嫁接点上
    ii.结果就是HEAD的当前分支和嫁接点所在的那个分支完全融合（不是merge合并了），最终嫁接点所在分支消失融合成嫁接前HEAD所在的那个分支

  d.分解动作（left和right表示提交ID或引用）：一般式是git rebase [--onto] <newbase> [<left>] [<right>]
    i.含义是将当前HEAD分支上的(left, right]区间内的提交嫁接到newbase上，和newbase所在的分支融合（注意，区间是左开右闭）
    ii.步骤：
      *1.先将(left, right]区间做一个临时副本临时保存起来
**中间有一部令人费解，暂时就叫*1-2：用HEAD到right逛一下：git checkout <right>
      *2.将HEAD指向newbase：git reset --hard <newbase>
**.明白了*1-2是干什么的吗？这里用的是git reset而不是git checkout，如果reset前HEAD刚好指向分支游标，那么HEAD会跟分支游标一起被reset到新的位置哟！
      *3.然后将临时副本区间(left, right]逐个分拣到newbase上：git cherry-pick (left, right]
**.当然没有区间分拣的命令的，其实就是多步的git cherry-pick
    iii.其实简单用法git rebase <newbase>等价于git rebase --onto <newbase> newbase分支和当前分支的最近公共祖先 当前分支引用
    iv.*1-2的作用：
      *1.由于rebase的初衷是嫁接完成后融合的分支应该是融合前的当前分支，如果是简单嫁接，right默认是融合前当前分支的分支引用
      *2.例如当前在master上，newbase在branch上，那么git rebase branch嫁接完后应该是在master上的
      *3.那是因为上面命令等价于：git rebase branch branch和master的最近公共祖先 master，因此*1-2的步骤是git checkout master
      *4.这就导致了HEAD刚好指向了分支游标master了，因此后面的git reset命令会导致HEAD和分支游标master联动
      *5.这就导致了一系列cherr-pick后HEAD还是指向master的，因此最终融合结果还是在master上，这就达到了目的了

  e.所以一般式的命令一定要慎重使用，因为会出现断头的情况：
    i.如果right不是分支游标而是普通的提交ID，或者是分支右边但不是分支游标的引用（即直接用ID表示，比如master不写成master，而是用master的提交ID）
    ii.这就会导致*1-2步中HEAD无法指向分支游标，而是直接指向一个具体的ID，这就导致后面的一系列步骤都是断头操作
    iii.连带地，最终融合的结果中，HEAD是断头的，因此还需要额外的步骤将头“接回来”！
    iv.所以一般式千万别这么用，而一般式的使用规范是：
      *1.left可以任意选取
      *2.但right最好是当前分支的引用！不要是ID，或者是非当前分支的末端点！这都会导致断头！
      *3.如果right是非末端点，就会导致最后融合完后，末端点后面的部分还未融合进去，还要自己手动继续cherry-pick，并且还是断头的，最后还要把头接回来！
      *4.简单的依据话就是right必须是当前分支的引用就行了！！
    v.尽量只用简单模式，不要用一般模式！


2.变基过程中遇到冲突：

  a.冲突其实就是cherry-pick中遇到的冲突，前面已经讲过了，cherry-pick的冲突其实就是底层merge的冲突，解决方式就和git merge的冲突解决完全一样

  b.但是变基的这种冲突有不太一样，因为变基过程中会发生多次连续的cherry-pick，这就意味着可能会发生连续的多次冲突，需要你一个接一个的解决！

  c.还好git提供了机制：
    i.当发生冲突后会立即中止rebase，并报出冲突，提示你冲突的文件
    ii.和merge冲突一样，会把冲突双方的文件以及合并后标记着<<<<===>>>>冲突的文件一并放在stage中（共三个）
    iii.而工作区中只会保留那个标记<<<<===>>>>冲突版本的文件，等你修改
    iv.修改完成之后加修改文件git add覆盖stage中的那个临时标记文件，接着不用commit，而是git rebase --continue继续嫁接即可（嫁接操作会帮你完成提交）
    v.下一次cherry-pick时又遇到冲突的话就又会中止并提示，接下来就循环i.~iv.即可，完成最终的嫁接
    vi.如果中途发现可能冲突无法自己解决，可能需要商量后待以后解决，可以使用命令git rebase --abort终止，运行后会恢复成git rebase命令之前的状态！


3.嫁接的窍门：

  a.嫁接工作的复杂程度是由cherry-pick的数量决定的，因为分拣的时候可能会发生冲突修改，冲突修改的次数自然决定这嫁接工作的工作量

  b.因此计算复杂度的时候就假设每次分拣都需要冲突修改，那么复杂度就等于分拣数量，也就等于嫁接链的长度了！

  c.因此嫁接的窍门就是短链嫁接到长链上！因为这样嫁接冲突修改的次数更少！

  d.一般约定俗称的是将master主分支嫁接到从分支上，这样可以有效降低从分支的数量：
    i.但实际是灵活的，如果主分支嫁接的时候里两分支的最近公共祖先已经非常远（中间隔了很多次提交了），至少要比从分支长很多
    ii.那么直接将master嫁接到从分支上会导致分拣冲突次数非常多
    iii.所以在这时正确的做法应该是将从分支嫁接到主分支上，最后再将主分支的头master接回到这个从分支上，在删除从分支的引用即可


4.功能强大的交互式变基：git rebase -i

  a.之前介绍的变基命令执行后中间的执行过程不可见，你也很难去插手中间的cherry-pick过程，但是通过交互式变基就可以办到

  b.git rebase -i <newbase>：
    i.会弹出一个交互式脚本文件让你编辑：
pick 347ff9f 4
pick 908408c 5
pick 2decc98 6

# Rebase a0ebe96..2decc98 onto a0ebe96 (4 command(s))
#
# Commands:
# p, pick = use commit
# r, reword = use commit, but edit the commit message
# e, edit = use commit, but stop for amending
# s, squash = use commit, but meld into previous commit
# f, fixup = like "squash", but discard this commit's log message
# x, exec = run command (the rest of the line) using shell
# d, drop = remove commit
#
# These lines can be re-ordered; they are executed from top to bottom.
#
# If you remove a line here THAT COMMIT WILL BE LOST.
#
# However, if you remove everything, the rebase will be aborted.
#
# Note that empty commits are commented out
    ii.其中头几行从上到下分别是嫁接链（如果A嫁接到B，那么该链就是A上的最近公共祖先到A末尾的链）上提交点，按顺序的！
    iii.对于这些一个个要嫁接的提交共有多种cherry-pick的方式，全部都罗列在下面了
    iv.其中最常用的就是pick和squash：
      *1.pick就是普通的应用提交
      *2.squash表示和上一行（即上一个）提交融合成一个提交，并且提交说明也采用上一个提交的
    v.其实该文件就是<left>到<right>的暂存文件（脚本化），你完全可以通过改变头几行的顺序来改变cherry-pick时的顺序
*.也可以删除其中几行以表示在cheryy-pick时忽略那几个提交！！以实现真正的插手嫁接过程！！
    vi.保存退出该脚本文件后会立即执行rebase嫁接
