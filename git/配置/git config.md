# git config
> **git自身**配置的**查看**和**修改**.

<br><br>

## 目录
1. [git配置文件介绍：INI格式](#一git配置文件介绍ini格式--)
2. [git的合法配置信息](#二git的合法配置信息)
3. [配置信息的查看和修改命令：git config](#三配置信息的查看和修改git-config--)
4. [使用命令编辑模式快速操作git配置文件：git config **-e**](#四使用命令编辑模式快速操作git配置文件git-config--e--)
5. [git配置文件的作用域：git config **--[scope]**](#五git配置文件的作用域git-config---scope--)
6. [配置git命令别名：git config **alias.**](#六配置git命令别名git-config-alias--)

<br><br>

### 一、git配置文件介绍：INI格式  [·](#目录)

- git配置文件中保存有**git这个软件自身**的配置信息.
  - 配置文件采用的数据交换格式是INI（类似XML、JSON）.
  - 格式如：要求键值**全部都是字符串**，可以**循环嵌套**

```
[节1的名称]
    节中的属性1的名称 = 属性值
    节中的属性1的名称 = 属性值
    ...
[节2的名称]
    节中的属性2的名称 = 属性值
    节中的属性2的名称 = 属性值
    ...
...
```

- 示例：

```
[user]
    name = Peter
    email = Peter-example@email.com
[ui]
    color = red
    [panel]
        layout = bag
```

- **数据访问方式**：节名.子节名.子子节名...属性名
  - 以后就将上面的**整体称为属性**，即“节名.子节名.子子节名...属性名”是一个属性.
  - 以上例来说：假设是修改属性的伪代码
    1. set user.name = Tom
    2. set ui.panel.layout = vertical

<br><br>

### 二、git的合法配置信息：[·](#目录)
<br>

- 这里只列举一些常用且非常重要的属性：

| 属性 | 说明 |
| --- | --- |
| user.name<br>user.email | 1. 是push者（推送/提交代码的人）的**身份标识**.<br>2. 远程版本库（例如Github）上显示某次**提交的作者信息**就是这俩. |
| color.ui | 是否开启git命令行的颜色高亮显示功能（true是开启）|
| [alias](#六配置git命令别名git-config-alias--) | 设置命令别名以简化git命令 |

<br><br>

### 三、配置信息的查看和修改：git config  [·](#目录)

1. 查看配置信息：git config **属性**
   - 会直接在命令行输出指定属性的值.
   - 例如：git config user.name
2. 添加/修改配置信息：git config **属性** **值**
   - 如果该属性不存在就是添加，如果已经存在则是覆盖（修改/更新）.
   - 例如：git config user.email peter-example@email.com
3. 删除属性：git config **--unset 属性**
   - 直接删除属性，如果属性原本不存在则什么也不做.

<br><br>

### 四、使用命令编辑模式快速操作git配置文件：git config **-e**  [·](#目录)

- 查看和修改git配置信息不仅可以使用git config命令，更直观的就是直接操作git配置文件了.
- 如果你忘记git配置文件放在哪里，就可以使用git config **-e** 命令直接在命令行用vi打开配置文件查看和编辑，非常方便.

<br><br>

### 五、git配置文件的作用域：`git config --[scope]`  [·](#目录)
> 上述所有命令说白了其实都是在操作git的配置文件，只不过`git config`是通过命令间接操作，而`git config -e`是直接操作罢了.

- git配置文件有三种作用域，对应三种配置文件.
  - 只要在git config系列命令中加上 **`--[scope]`** 选项就可以确定操作的是哪个作用域的配置文件了.
  - 如果你忘了三种作用域对应的配置文件的位置以及名称，可以使用 **`git config --[scope] -e`** 的方式在 **vi界面的信息栏（最底下的一行）** 中看到打开的配置文件的信息（包含名称和位置信息）.
  - 作用域永远都是小范围覆盖大范围，这里当然是：局部 -> OS单用户全局 -> OS全局
    - 箭头表示覆盖（属性同名覆盖）.

| 作用域 | 对应的配置文件 | 说明 | 别名 |
| --- | --- | --- | --- |
| [无] | [repo]/.git/config | 该配置只对**某个仓库**有效，对其它仓库无效 | 局部 |
| --global | ~/.gitconfig | 该配置只对OS的**当前用户**下的**所有仓库**有效 | OS单用户全局 |
| --system | /etc/gitconfig | 该配置对OS的**所有用户**的**所有仓库**有效 | OS全局 |

<br>

### 六、配置git命令别名：`git config alias.`  [·](#目录)
> git配置中有一个特殊的节[alias]，专门用于设置git命令的别名以简化命令.

- 是一种纯粹的宏替换（纯字符替换，小心bug漏洞哟！）：git config alias.别名 被替换的字符串
  - **注意：** 如果被替换的字符串中含有空格，一定要用双引号引起来，否则空格会被当做命令分隔符来处理.
  - 示例：
    1. git config alias.ci "commit -m"：git ci "prompt"  ==  git commit -m "prompt"
    2. git config --global alias.co checkout：git co  ==  git checkout
  - 因为命令别名是一种用户个人习惯，因此一般配置在--global作用域中.


- **还是不建议设置命令别名，不具有一般性，而是直接记忆完整git命令更加清晰易懂.**
