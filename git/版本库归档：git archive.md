# 版本库归档：git archive
> 我们只想归档源代码以及说明文档，不想把其它中间文件等也归档了.

<br><br>

## 目录

1. [不能直接对工作区归档]()
2. [git archive命令]()

| 命令 | 说明 |
| --- | --- |
| git archive -o 输出的归档文件名 节点引用 [该节点中想归档的文件或目录列表] | 将指定节点的指定内容压缩成zip格式 |
| git archive **--format=tar** 节点引用 [该节点中想归档的文件或目录列表] **\| gzip > 输出的归档文件名** | tar格式压缩 |

<br><br>

### 一、不能直接对工作区归档：[·](#目录)

1. 会将各种中间文件也归档了（.o、.a等）.
2. 会把git文件也归档了（.git/目录、.gitignore等）.

<br><br>

### 二、git archive命令：[·](#目录)

- 只能对版本库中的**节点**进行归档.
  - 这符合我们的需求，只归档那些被追踪的文件（代码、文档等）.

<br>

- zip格式归档：git archive默认就是这种格式（也**只有这一种**格式）
  - 命令用法：git archive -o 输出的归档文件名 节点引用 [该节点中想归档的文件或目录列表]
    1. 如果[]不写就对整个节点归档.
    2. 示例：git archive -o v1.2.3-module.zip HEAD^^ src doc
      - 只将HEAD^^中的src/和doc/目录归档成v1.2.3-module.zip

<br>

- tar格式归档：
  - 前面说过了，git archive只支持一种zip格式归档，不支持其它格式.
    - 但是，Linux的gzip命令可以将zip格式转换成tar格式，因此需要外界辅助.
  - 命令用法：<br>
  git archive **--format=tar** 节点引用 [该节点中想归档的文件或目录列表] | gzip > 输出的归档文件名
    1. --format选项只有两个合法值，zip和tar，默认是zip，但即便使用tar也无法真正的鬼当成tar格式.
      - 仅仅是一种标记而已，git archive的输出结果只能是zip的.
    2. 将git archive输出的zip格式文件通过管道输送给gzip命令，最终转换成tar格式.
    3. 示例：git archive --format=tar master | gzip > foo-1.0.tar.gz
