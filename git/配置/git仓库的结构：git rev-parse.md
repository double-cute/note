# git仓库的结构：git rev-parse
> git用 `.git/` 目录来确定工作区.

<br><br>

## 目录

1. [git对仓库的定义](#一git对仓库的定义)
2. [确定工作区根目录的位置：git rev-parse](#二确定工作区根目录的位置git-rev-parse--)

- 假设仓库的目录结构是：/user/Peter/work/repo/proj1/demo/.git/
  - 当前位于 `demo/a/b/c/` 下.

| 命令 | 说明 | 输出结果 |
| --- | --- | --- |
| git rev-parse **--git-dir** | 确定.git/目录的绝对路径 | /user/Peter/work/repo/proj1/demo/.git |
| git rev-parse **--show-toplevel** | 确定工作区根目录（仓库根目录）的绝对路径 | /user/Peter/work/repo/proj1/demo |
| git rev-parse **--show-prefix** | 确定当前位置和工作区根目录的相对路径差值 | a/b/c/ |

<br><br>

### 一、git对仓库的定义：[·](#目录)

- 定义规则：
  1. 一个 `.git/` 目录确定一个仓库.
  2. `.git/` 目录所在的目录作为git仓库的 **工作区根目录**（也就是仓库的根目录）：
- 示例：`.git/` 位于 `demo/` 目录下，即目录结构是 `···/demo/.git/`
  - 那就意味着：
    1. `demo/` 是该仓库的根目录.
    2. `demo/` 也是该仓库的工作区根目录.
- 工作区的定义：工作区根目录下的所有空间（位置）都属于工作区（包括.git/目录）.
  - 就上例来讲，如果/user/Peter/work/repo/proj1/demo/.git/，那么：
    1. `demo/` 下的任意位置都属于工作区，比如 `/demo/src/codec/`
    2. `demo/` 之外的任何位置都不属于工作区，比如 `proj1/doc/`
  - **只有在工作区中** 使用git命令才有效.
    - 在工作区外使用git命令会报错，提示你当前不在任何一个仓库的工作区中，git命令执行失败.

<br><br>

### 二、确定工作区根目录的位置：git rev-parse  [·](#目录)
> 该命令同样也只能在工作区中使用.

- 假设仓库的目录结构是：`/user/Peter/work/repo/proj1/demo/.git/`
  - 当前位于 `demo/a/b/c/` 下.

| 命令 | 说明 | 输出结果 |
| --- | --- | --- |
| git rev-parse **--git-dir** | 确定.git/目录的绝对路径 | /user/Peter/work/repo/proj1/demo/.git |
| git rev-parse **--show-toplevel** | 确定工作区根目录（仓库根目录）的绝对路径 | /user/Peter/work/repo/proj1/demo |
| git rev-parse **--show-prefix** | 确定当前位置和工作区根目录的相对路径差值 | a/b/c/ |
