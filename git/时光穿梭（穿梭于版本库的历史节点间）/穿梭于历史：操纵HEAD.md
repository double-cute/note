# 穿梭于历史：操纵HEAD
> 想再版本库的历史节点间穿梭无非就是**对HEAD动手脚**罢了.
>> 比如，回到过去，就是将HEAD指向之前的节点，让当前工作节点变成“过去的节点”.<br>
>> 而回到未来，就是将HEAD指向之后的节点，让当前工作节点变成“未来的节点”.
>>   - 但回到未来的前提肯定是之前“回到过去”过了.

<br><br>

## 目录

1. [操纵HEAD时需要注意哪些问题？](#一操纵head时需要注意哪些问题--)
2. [git穿梭命令对应的HEAD操作](#二git穿梭命令对应的head操作)

| 命令 | 说明 | 对应的HEAD操作 |
| --- | --- | --- |
| git checkout 分支名 | 切换分支（全覆盖，必须干净） | \*HEAD = &branch |
| git reset --soft\|mixed/空\|hard 节点引用 | 重设位置<br>（无/只覆盖stage/全覆盖，无提示） | \*\*HEAD = node_ref  或<br>\*HEAD =  node_ref（断头）|
| git checkout 分支名 | 强行断头重设位置 | 无条件执行\*HEAD = node_ref |

<br><br>

### 一、操纵HEAD时需要注意哪些问题？  [·](#目录)

- 操纵HEAD的一般化实现：考虑到了所有的细节问题

```java
setHEAD(ref) {

@1  if (ref is a branch_ref) { // 如果ref是个分支引用，那就代表切换分支操作
        *HEAD = &ref; // 切换分支
        return ;
    }

@2  if (ref is a node_ref) { // 如果ref是个节点引用，那就代表重设位置操作

@2-1    if (*HEAD is a branch_ref) { // 如果HEAD已经指向某个分支（分支引用）
            **HEAD = ref; // 那HEAD就是一个多重指针
            return ;
        }

@2-2    if (*HEAD is a node_ref) { // 如果HEAD直接指向某个具体节点，发生“断头”的情况
            *HEAD = ref; // 那就直接重设节点位置，此时HEAD是单指针
        }

    }

}
```

<br>

- 总的可以看到两种类型三种情况：
  - 两种类型的操作：
    1. @1：切换分支操作.
    2. @2：重设位置操作.
  - 其中@2(重设位置操作)又分两种情况：
    1. @2-1：HEAD已指向某个分支（分支引用），则直接移动该分支引用即可.
      - 在这种情况下会直接改变HEAD指向的分支引用的值.
        - 例如：已知\*HEAD = &master，那么[OP: \*\*HEAD = \*master^]必然会导致<br>
        [\*master = \*master^]，即HEAD指向的master本身的值的改变.
    2. @2-2：HEAD没有指向某个分支（而是直接指向某个具体的节点），因此直接移动HEAD.

<br>

- “断头HEAD”：就是指@2-2的情形：
  - 常理来说，HEAD通常应该同时包含分支和位置两层信息.
    - 也就是说，[\*HEAD = &branch]是合理、正常的状态.
    - 而@2-2的状态[\*HEAD = &node]直接使HEAD指向某个具体节点的状态是“非合理的”，毕竟缺少了分支信息.
    - 像这样的只有位置信息没有分支信息的HEAD被形象地称为“断头HEAD”.
  - 断头是非常危险的，因为你不知道工作在什么分支上，可能会使提交造成混乱.
    - 因此断头只会在一些特殊情形中应用到：
      1. 一些特殊的git调试.
      2. 一些特殊的git命令实现中会用到（比如git cherry-pick）.
  - 平时正常的代码开发尽量避免断头的发生！！

<br><br>

### 二、git穿梭命令对应的HEAD操作：[·](#目录)

- [@1：直接切换分支]：git checkout 分支名
  1. 对应@1的操作：[OP: \*HEAD = &branch]
  2. 副作用：将工作区和stage用branch的最新节点覆盖.
  3. 命令执行的前提：工作区和stage必须是干净的，即{state:clean}
    - 即stage中的内容必须都从工作区覆盖过（git add），并且stage必须被提交.
    - 即工作区、stage、HEAD完全相同.
    - 这就是{stage:clean}的含义，即干净.
    - 不干净将拒绝执行该命令（报错）.

<br>

- [@2: 重置位置]：git reset
  1. @2-1和@2-2两个逻辑都考虑到了：如果HEAD指向分支引用则移动\*\*HEAD，如果HEAD断头，则直接移动*HEAD.
  2. 该命令的三个版本：

| git reset的选项 | 说明 |
| --- | --- |
| git reset **--soft** 节点引用 | 仅重设HEAD，但工作区和stage不变 |
| git reset **--mixed\|空** 节点引用 | 重设HEAD后，用新HEAD覆盖stage，但工作区保持不变 |
| git reset **--hard** 节点引用 | 重设HEAD后，用新HEAD同时覆盖stage和工作区 |

- **注意！** 这里的重覆盖工作区或stage和git checkout切换分支有所不同：
  1. git reset不管干不干净都直接覆盖，不会拒绝“不干净”的覆盖.
  2. 因此git reset比较危险，容易造成工作区和stage的丢失，**一定要慎用**！！

<br>

- [@#: 强行断头]：git checkout **节点引用**
  - 注意，这里git checkout传入的参数直接是**节点引用**.
  - 该命令不管三七二十一，直接无条件执行[OP: \*HEAD = node_ref]操作.
  - 因此该命令（操作）的结果就是强行丢失分支信息，造成断头.
    - 尽量不用该断头命令，仅用于git调试.
    - 一些git命令的实现中会用该命令作为辅助，如git cherry-pick命令的实现中用到了断头操作.
