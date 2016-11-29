# 查看历史：git log
> 查看的是版本库中的节点链，并不是提交操作的历史记录！

<br><br>

## 目录：
1. [git log和git reflog的区别](#一git-log和git-reflog的区别)
2. [git log的选项](#二git-log的选项)
3. [订制格式：--pretty=format:'...'](#三订制格式--prettyformat--)


<br><br>

### 一、git log和git reflog的区别：[·](#目录)

- git log仅显示从HEAD（包括HEAD）往前推的所有节点的信息，即物理节点链.
  - 包含两层信息：
    1. **起点是当前HEAD（包括HEAD）**.
    2. 逐个往前遍历节点链.
  - 它不关心两个节点之间有哪些操作.
  - 例如：当前HEAD->e -> d -> c -> b -> a
    - 那么git log的结果就是e -> d -> c -> b -> a
    - 如果HEAD移到了c上，即经过git reset之后变成了：e -> d -> c(<- HEAD) -> b -> a
      - 那么git log的结果就是c -> b -> a
      - 它不关心当前HEAD（c）之前发生过什么，仅仅忠实地记录物理节点链的信息.

<br>

- 而[git reflog](../时光穿梭（穿梭于版本库的历史节点间）/悔棋.md#二取消回退回到未来利用git-reflog--)（之前在“悔棋”一章中讲过了）可以记录提交历史，会把每次提交也记录在其中.
- 两者的关注点不同：
  1. git log关注物理节点链.
  2. git reflog关注提交操作的历史记录.

<br><br>

### 二、git log的选项：[·](#目录)

- 这里只列出一些最常用的：
  - 词缀修饰：
    1. **格式**：表示显示风格
    2. **额外**：表示在某种显示风格（**格式**）的基础上额外显示一些信息
  - git log命令的优良书写格式：git log [-格式选项] [-额外选项]
    - 这样就表示基于某种风格的格式下再显示一些额外信息.

| 选项 | 词缀 | 说明 |
| --- | --- | --- |
| 无 | **格式** | 4行：提交ID+作者+提交时间+提交说明 |
| --oneline | **格式** | 单行：提交ID+提交说明 |
| --pretty=[format](#三订制格式--prettyformat--):'自定义风格' | **格式** | 特殊的格式，可以自定义风格 |
| -<n> | **额外** | 只显示最近的n次提交 |
| --stat | **额外** | 额外添加本次提交时的文件改动（每个文件改动了几行）|
| --abbrev-commit | **额外** | 提交ID简化（不再是40位）|
| --date=relative\|iso | **额外** | 提交时间以相对于当前时间\|ISO标准来显示 |
| --graph | **额外** | 额外以树状图的形式显示（体现出各种分支的情况）|

<br><br>

### 三、订制格式：--pretty=format:'...'  [·](#目录)

- 和C语言的printf函数一样，有各种合法的占位符，这里只介绍几个常用的：

| 占位符 | 含义 |
| --- | --- |
| %H | 40位提交ID |
| %h | 简化的提交ID |
| %an、%ae | 作者名字、作者email |
| %cn、%ce | 提交者名字、提交者email |
| %cd、%cr、%ci | 提交时间，分别是local、relative、iso三种格式 |
| %d | 提交节点引用 |
| %s、%b | 提交说明（title）、提交说明内容（body）|
| %n | 换行 |
| %Cred、%Cgreen、%Cblue、%Creset | 将颜色切换成红、绿、蓝、默认色（白色，如果是黑底的话）|
| %C(color) | git log只支持红绿蓝三种颜色，如果想切换成其他颜色就用该站位符.<br>只不过color必须是控制台支持的颜色 |

- 这里给出一个本人喜欢的自定义format：

```
## 相对时间
git config --global alias.lgr "log --pretty=format:'%Cred%h%Creset -%C(yellow)%d%Creset %s %Cgreen(%cr)%Creset' --graph"
## ISO时间
git config --global alias.lgi "log --pretty=format:'%Cred%h%Creset -%C(yellow)%d%Creset %s %Cgreen(%ci)%Creset' --graph"
## 相对时间+提交者
git config --global alias.lgrc "log --pretty=format:'%Cred%h%Creset -%C(yellow)%d%Creset %s %Cgreen(%cr)%Creset%n--:@%cn(%ce)' --graph"
## ISO时间+提交者
git config --global alias.lgic "log --pretty=format:'%Cred%h%Creset -%C(yellow)%d%Creset %s %Cgreen(%ci)%Creset%n--:@%cn(%ce)' --graph"
```

- 用的时候别忘了加上\-<n>限制几次提交，例如：git lgr -5
