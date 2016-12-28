# 定位远程仓库：不同协议下的URL
> 用统一资源定位器（URL）来定位（描述）远程仓库再合适不过了.
>
>> 首推SSH和HTTPS，应用最广泛，如果从GitHub上clone仓库就只支持SSH和HTTPS两种方式.

<br><br>

## 目录

1. []()

<br><br>

SSH URL:

git@主机别名[:端口号]:GitHub账号/xxx.git
- 示例：git@it_note:double-cute/note.git
  - 其中it_note指向github.com

HTTPS URL:

https://GitHub账号@github.com[:端口号]/GitHub账号/xxx.git

- 示例：https://double-cute@github.com/double-cute/note.git

FTPS:

GitHub不使用这种通信协议

ftps://主机域名[:端口号]/path/仓库

跨平台的本地文件系统：

file://仓库的绝对路径/仓库

- 示例：file:///code/src/repo/test.git
- 考虑到不同OS平台上路径的描述不尽相同，比如Windows使用\\作为分隔符，而类Unix系统使用/作为分隔符.
- 使用本地文件系统的方式将全部统一成/作为分隔符
- file://后必须是绝对路径，不能是相对路径，没得选择.

本地文件系统：

直接以本地文件系统的路径来定位远仓，可以使用绝对路径也可以使用相对路径，只不过要注意分隔符的不同

git clone a\\test.git test  # Windows下
git clone /user/code/proj/.git /document/repo/cp_proj  # 类Unix下
