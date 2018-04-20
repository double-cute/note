# 保存当前工作进度：git stash

<br><br>

## 目录
1. [问题背景](#一问题背景)
2. [保存进度的git stash命令](#二保存进度的git-stash命令)
3. [保存进度：git stash](#三保存进度git-stash--)
4. [查看进度栈中的进度：git stash list](#四查看进度栈中的进度git-stash-list--)
5. [还原进度：git stash pop/apply](#五还原进度git-stash-popapply--)
6. [删除进度栈中的进度](#六删除进度栈中的进度)

| 命令 | 说明 |
| --- | --- |
| git stash | 保存进度 |
| git stash save comments | 保存进度同时附上说明 |
| git stash list | 查看进度栈 |
| git stash pop\|apply | 只还原工作区（删除/不删除进度栈中的相应进度）|
| 加 --index | 同时还原stage |
| 加 stage@{n} | 指定要还原的进度 |
| git stash drop | 删除栈顶进度 |
| git stash drop stash@{n} | 删除指定进度 |
| git stash clear | 清空进度栈 |

<br><br>

### 一、问题背景：[·](#目录)

- 开发过程中常常会遇到这种情况：
  - 工作了一半突然接到一个紧急任务要求你在另一个分支上修改一个bug.
    - 通常都是短时间内要求完成，比如1小时之内.
  - 那现在问题来了，当前的烂摊子如何收拾（只完成一半的工作）.
    - 现实是，当前任务不得不中止，因此需要保存现场待紧急任务完成后再恢复现场继续工作.

<br>

- 需要解决的问题：如何保存现场？
  1. 首先，肯定不能提交当前的任务将现场保存到版本库节点中.
     - 因为按照规矩，版本库节点只能保存完整、测试过的代码.
     - 而这里的“现场”显然是一个未完成、不完整的代码，存入版本库节点显然会造成管理混乱.
  2. 其次，肯定也不能直接切到紧急任务的bugfix分支上.
     - 这是显而易见的，切换分支会丢失当前的工作区和stage.
        - 而 `git checkout branch` 命令在“不干净”的情况下本身也会拒绝切换.
  3. 保存现场的问题解决了，还原现场的问题也就迎刃而解了.
     - 毕竟，保存到哪里就从哪里恢复出来.

<br><br>

### 二、保存进度的git stash命令：[·](#目录)

<br>

- git stash命令的工作原理：
   1. 首先，将当前的 **工作区** 和 **stage** **两者** 同时保存在 `.git/stash` 的 **进度栈** 中.
      - 进度栈明显是一个栈结构，因此可以连续保存多个进度.
   2. 接着，执行 `git reset --hard HEAD` 命令使状态变为“干净”.
      - 就是让工作区/stage和HEAD一致.
      - git stash执行完之后再执行 `git status -s` 发现一切“干净”.
   3. 正因为干净了，所以可以放心地切换到其它分支上工作了.
   4. 未来可以利用 `git stash pop` 命令从进度栈中还原进度（将进度从栈中弹出）.
      - 同时还原 **工作区** 和 **stage**.

<br>

- 总结地来讲，`git stash` 完成了两步：
   1. 保存进度.
   2. 清空现场（干净）.

<br>

- 接下来详细讲解git stash命令的各种用法.

<br><br>

### 三、保存进度：git stash  [·](#目录)

| 命令 | 说明 |
| --- | --- |
| git stash | 直接保存当前进度 |
| git stash save comments | 同时对保存的进度附上一些说明 |

- 示例：`git stash save "music module only 60% completed."`

<br><br>

### 四、查看进度栈中的进度：git stash list  [·](#目录)
> git允许连续多次保存进度，会将进度按照FILO的顺序压入 **进度栈** 中.

- 查看进度栈：git stash list
  1. 每行代表一个进度.
  2. 从上到下为栈的顶到底.
  3. 每行的内容为：
     - stash@{<n>}:进度所属的分支:进度说明
        1. n表示最近的第n个进度，从0计.
        2. 如果当时保存时用save附上了说明则会在这里显示进度说明.

<br><br>

### 五、还原进度：git stash pop/apply  [·](#目录)

1. git stash pop
   - **只** 还原目标进度的**工作区**.
   - 同时**从进度栈中删去相应的进度**.
      - `git stash apply` 和 `git stash pop` 一样，只不过不删除进度栈的相应进度而已.
2. 加上--index
   - 额外 **还原stage**.
   - **如果当前stage和进度中的stage不一致会报错，要求对两者进行合并.**
3. 加上[stash@{<n>}]
   - 这个stash@{<n>}可以从git stash list查得.
   - 指定还原的**目标**进度.
   - 如果不加表示还原栈顶（即最近的那个进度，stash@{0}）.
      - 还原栈底的不影响栈顶的，比如还原stash{5}不会影响栈中的stash@{0~4}.

<br>

- 示例：

```
git stash pop  # 仅还原stash@{0}的工作区，并删除stash@{0}
git stash pop --index stash@{5}  # 还原stash@{5}的工作区和stage，并删除stash@{5}
git stash apply stash@{2}  # 仅还原stash@{2}的工作区，但不删除stash@{2}
git stash apply --index stash@{3}  # 还原stash@{3}的工作区和stage，但不删除stash@{3}
```

<br><br>

### 六、删除进度栈中的进度：[·](#目录)

| 命令 | 说明 |
| --- | --- |
| git stash drop | 只删除栈顶stash@{0} |
| git stash drop stash@{n} | 删除目标进度 |
| git stash clear | 清空进度栈 |
