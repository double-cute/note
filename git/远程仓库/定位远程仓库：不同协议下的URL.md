# 定位远程仓库：不同协议下的URL
> 用统一资源定位器（URL）来定位（描述）远程仓库再合适不过了.
>
>> 首推SSH和HTTPS，应用最广泛，如果从GitHub上clone仓库就只支持SSH和HTTPS两种方式.
>>   - git目前支持的通信协议有：
>>     1. HTTPS
>>     2. SSH
>>     3. FTPS
>>     4. 本地文件系统

<br><br>

## 目录

1. [SSH URL]()
2. [HTTPS URL]()
3. [FTPS URL]()
4. [跨平台的本地文件系统]()
5. [本地文件系统]()

<br><br>

### 一、SSH URL:[·](#目录)

- 一般情况下：通信发起方使用的SSH软件名称@对方主机在本地起的别名[:端口号]:资源路径/资源
- 和GitHub通信的情况下定位托管的远仓：git@主机别名[:端口号]:GitHub账号/xxx.git
  - 示例：git@it_note:double-cute/note.git
    - 其中it_note指向github.com（bending已经配置好~/.ssh/config）.

<br><br>

### 二、HTTPS URL:[·](#目录)

- 一般情况下：https://对方主机域名/资源路径/资源
- 和GitHub通信的情况下定位托管的远仓：https://**[GitHub账号[:登录密码]@]** github.com[:端口号]/GitHub账号/xxx.git
  - 示例：https://double-cute@github.com/double-cute/note.git
    - 底层是credential-helper在管理.

<br><br>

### 三、FTPS URL：[·](#目录)

- 一般情况下：ftps://主机域名[:端口号]/资源路径/资源
- 但GitHub**不使用**该协议，该协议是文件服务器协议，从GitHub克隆仓库只开放了HTTPS和SSH方式.
- 但如果用来定位git远程仓库的话，一般情况下是这样的：ftps://主机域名[:端口号]/仓库路径/仓库

<br><br>

### 四、跨平台的本地文件系统：[·](#目录)
> 使用本地文件系统就是指将本地的仓库作为远仓.

- URL：file://仓库的绝对路径/仓库
  - 示例：file:///code/src/repo/test.git

<br>

- 前缀"file://"并不是表示某种通信协议：
  - 它只是git自己定义的一种"通信协议"，即表示跨平台的本地文件系统.
  - **作用是**：在不同OS平台上用统一的方式来定位仓库.
    - 举例来讲：Windows使用\\作为路径分隔符，而类Unix系统使用/作为分隔符.
    - 但是在"file://"下全部统一使用/作为路径分隔符.

<br>

- **注意：**
  - "file://"后紧跟的**必须**是**绝对路径**，不能是相对路径，没得选择.
  - 即必须是/开头的绝对路径，因此该URL开头必定是"file:///"，其实第三个/是绝对路径中的根目录，不要误解成第三个/也是协议名称的一部分.

<br><br>

### 五、本地文件系统：[·](#目录)
> 直接以本地文件系统的路径来定位远仓，可以使用绝对路径也可以使用相对路径，**不能跨平台**，所以要考虑分隔符不同的问题.

- 示例：
  1. git clone a\\test.git test  # Windows下
  2. git clone /user/code/proj/.git /document/repo/cp_proj  # 类Unix下
