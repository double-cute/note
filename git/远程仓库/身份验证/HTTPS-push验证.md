# HTTPS-push验证
> HTTPS作为和远程仓库之间的通信协议时只有在push的时候会进行身份验证，clone、pull、fetch等不会验证（自由使用）.
>
>> 不管什么验证（包括SSH验证），都是git代码托管服务器（后面简称为服务器）和git客户端（后面简称为客户端）相互配合完成的.

<br><br>

## 目录

1. [HTTPS验证 VS SSH验证](#一https验证-vs-ssh验证)
2. [不同OS平台对HTTPS验证的支持](#二不同os平台对https验证的支持)
3. [HTTPS URL验证的完整步骤](#三https-url验证的基本步骤)
4. [git HTTPS URL](#四git-https-url)
5. [git客户端管理HTTPS-push验证：git-credential-helper](#五git客户端管理https-push验证git-credential-helper--)
  1. [当前验证账号](#51-当前验证账号)
  2. [免密模式](#52-免密模式)
  3. [Mac的keychain终身免密机制](#53-mac的keychain终身免密机制)
6. [综合评价](#六综合评价)

| 内容/命令 | 说明 |
| --- | --- |
| https://[账号[:密码]@]git服务器主机域名/远仓路径 | git HTTPS URL书写格式 |
| git config --global credential.helper **cache** | 设置成限时免密模式，默认为15分钟 |
| git config --global credential.helper **'cache --timeout=XXX'** | 指定限时时长，单位是秒 |
| git **credential-cache exit** | 关闭限时模式的cache管理后台进程（短时间离开电脑时使用最佳）|
| git config --global credential.helper **store** | 设置成终身免密模式 |

- 设置模式后需要退出重启git-bash才会生效！

<br><br>

### 一、HTTPS验证 VS SSH验证：[·](#目录)

- **HTTPS URL验证** 是GitHub官方**首荐**的验证方式，理由如下：
  1. HTTPS协议广泛且通用：
    - 基本上任何计算机上的任何OS都支持该协议，每个互联网用户都会用到的基础通信协议.
  2. 传统的**账号-密码**验证方式，简单无脑，无需进行任何复杂配置：
    - 即push的时候需要账号-密码信息进行验证.
    - SSH验证则配置复杂，多账号（同时使用多个GitHub账号）情况下配置更加复杂.
  3. 门槛及低，即使有防火墙或代理的情况下也可以正常使用.
    - SSH验证在有防火墙和代理的情况下需要特殊配置，进一步增加使用门槛.
    - 没有权限管理等复杂功能，仅仅就是无脑的**账号-密码**管理方式.
      - HTTPS适用于普通、零散、设备简陋的用户，仅满足代码的push即可.
      - SSH试用于公司、集团、组织内部的局域网，可以自定义复杂的管理机制（权限管理等，比如clone权限、push权限等）.
- HTTPS URL验证相比于SSH的优势：
  1. 简单无脑，适合小白.
  2. 快速上手，对GitHub的推广很有效.

<br><br>

- HTTPS URL的主要缺点：
  1. 每次push验证都要输入用户名（有时可免用户名）和密码（密码是必须的）非常不方便.
    - 而SSH验证则可以做到免密验证.
      - 如果想做到SSH验证那样那样免密验证，则会付出巨大的安全代价（**密码明文在本地暴露**）.
  2. 只能提供最基础最简单的push验证，不能提供更复杂的功能了.
    - SSH验证则非常灵活，可以经过一系列配置完成更多复杂功能，定制能力和扩展能力强.

3. HTTPS验证在不同平台上有不同的特性，分Unix、Mac、Windows
  - 目前Windows的git客户端实现还有部分平台兼容问题，因此验证会有点儿失常，因此就以Unix为准.
  - 而Mac则对Unix进行了一定的加强.

<br><br>

### 二、不同OS平台对HTTPS验证的支持：[·](#目录)
> 主流分为Unix/Linux、Windows、Mac三种平台.

1. Unix/Linux：
  - 可以说是git的标准，毕竟git本身就是在Linux平台上开发出来的.
  - 一切功能都以Unix/Linux平台为准，其它平台的一些异常现象可以解释为一下几点：
    1. 存在bug.
    2. 对Unix/Linux上的标准支持不足.
    3. 自己进行了一定的扩展，但没有和Unix/Linux标准兼容的很好.
2. Windows：
  - 按照Unix/Linux标准开发的.
  - 但是由于Windows平台本身的Shell并非Unix类型的Shell，因此兼容性有很大的问题.
    - 在Windows上使用git-bash进行HTTPS URL验证时会遇到一些异常情况，但无需担心，一切以Unix/Linux为准.
    - 后面讲解的内容也是基于Unix/Linux平台的.
3. Mac：
  - 在Unix/Linux标准的基础上做了一系列扩展.
  - 可以说是最好用的平台：
    1. 不仅完美支持Unix/Linux上的标准.
    2. 而且做了一系列非常科学的扩展，加强了HTTPS免密验证的安全性.

<br><br>

### 三、HTTPS URL验证的基本步骤：[·](#目录)

- 首先从push操作的2个核心出发：
  1. 仓库的位置信息：即非常普通的仓库HTTPS URL地址，例如，https://github.com/double-cute/test.git
    - 这就不用说了，仓库在哪儿都找不到谈何push呢？
      - 这就和普通上网使用的HTTPS URL没有区别，仓库也是一种资源，就跟PDF、RMVB等一样，正常定位.
  2. 远仓所有者的身份验证：
    - 这也是显然的，只有远程仓库所有者才有push权限，所以git托管服务器必须要确定**本次push代码的人必须是该远仓的所有者**.
    - 远仓所有者是指：
      1. 首先必须是在git托管服务器上注册有账号的人：比如GitHub、Gist等上注册有账号的人.
      2. 在自己的账号下开辟远仓作为托管代码库.
      3. 则这些远仓（托管库）的所有者就是该账户.
    - 身份验证必须要有账号和密码，因此在push的时候，账户-密码、仓库URL需要一并提交.
      - 账户-密码如何提呢？.
        - 方法就是把账户-密码也加到HTTPS URL中，但这样的URL已经不再是标准的HTTPS URL.
        - 只能叫做git HTTPS URL.

<br>

- 因此HTTPS验证的完整步骤：其实就是**服务器校验客户端提交的HTTPS URL**
  1. 客户端：用户push代码，给出（提交）相应的git HTTPS URL.
    - 如果URL本身就写错了就得不到服务器相应，会直接报错的.
    - 以下是服务器端的行为：就是解析客户端提交的git HTTPS URL并进行相应的处理
  2. 解析出仓库地址，确定该仓库是否存在于本服务器中.
  3. 接着再解析出账户名，确定该仓库是否属于该账户.
  4. 最后解析出密码，校验密码是否正确.
    - 以上3步任何一步出错都会拒绝push并将具体的错误信息返回给客户端.
  5. 以上都没问题后从客户端接受push节点的数据提交到远仓中.

<br><br>

### 四、git HTTPS URL：[·](#目录)

- git HTTPS URL应该怎么写？
  - 之前已经说过了，git HTTPS URL不是标准的HTTPS URL，而是专门用于git的HTTPS-push验证的.
  - 书写格式：https://[账号[:密码]@]git托管服务器主机域名/仓库路径
    - 以下示例都是正确的：
      1. https://double-cute@github.com/double-cute/test.git  # 只给出账号
      2. https://user:12345@github.com/user/test.git          # 账号密码都给出
      3. https://github.com/user/test.git                     # 账号密码都不给出
  - git HTTPS URL在传输时会对密码加密：
    - 在push书写git HTTPS URL时没给出密码 **[1.3.]** 也没关系，回车后会让你单独输入账号密码的.
      - 密码输完后会将密码加密插入到git HTTPS URL中.
    - 即使在git HTTPS URL中已经书写了密码 **[2.]** 也会再传输时对密码加密.
    - **及其不推荐[2.]的书写方式，直接在命令行上出现密码的明文，太危险了.**

<br>

- 对于不给出账号的git HTTPS URL服务器如何验证：一般git代码托管服务器都是智能的，都具有以下功能
  1. 由于仓库路径是绝对路径，在服务器中是唯一的.
  2. 因此可以根据该唯一的路径确定仓库所属的账号.
  3. 有了账号再验证密码（密码是无论如何都要给出的）.
- 但**并非所有git服务器都支持该功能**，因此首荐 **[1.]** 的写法.

<br><br>

### 五、git客户端管理HTTPS-push验证：git-credential-helper  [·](#目录)

- 客户端还需要管理HTTPS-push验证？
  - HTTPS-push验证不是服务器该管的事吗？客户端只要写好git HTTPS URL不就完了吗？这关客户端什么事呢？
  - 确实如此，客户端完全没必要管理HTTPS验证，一切都交给服务器没有任何毛病，这种验证机制本身就是为了方便客户端的.
    - 但别忘了每次push都要输入账号-密码，这是很麻烦的！
    - 因此客户端想了很多办法来解决这个问题，比如：
      1. 输入过一次密码之后的15分钟之内的push都可以免密.
      2. 干脆将账户-密码信息保存在一个文件中，只要在git HTTPS URL中给出账号即可，自动根据该文件找到相应的密码，做到终身免密.
    - git客户端专门用git-credential-helper组件来实现上述功能，也就是对HTTPS验证在客户端进行管理.
- 由于git-credential-helper就摆在那儿，你不用也得用，即使你不怕麻烦就是喜欢每次push都输入密码也没办法：
  - 客户端HTTPS验证由git-credential-helper支配着.
  - 你必须要了解其中的机制，否则会遇到一些令人费解的问题，而这些问题的答案就在于git-credential-helper的实现机制.

<br>

- 接下来讲解credential-helper的2个核心：
  1. 当前验证账号：经常由于这个原因出现令人费解的问题.
  2. 免密模式：
    1. 限时免密：比如15分钟之内免密，超过15中又要重新输入密码才能push.
    2. 终身免密：直接将账号-密码记录在文件中.

<br><br>

#### 5.1 &nbsp;&nbsp;当前验证账号：[·](#目录)

- credential-helper有一个环境变量，叫做**当前验证账号**，这里我们用current_push_account来表示，**最终提交的URL中的账号由current_push_account决定**！
  1. 如果用**[1.2.]** 的git HTTPS URL来push，就会把current_push_account赋成URL中给出的账号名.
    - 例如：
      1. push时给出的git HTTPS URL为https://double-cute@github.com/double-cute/test.git
      2. 那么就将当前验证账号切换为double-cute，即current_push_account = double-cute
  2. 如果用**[3.]** 的URL来push，由于URL中没给出账号，因此**current_push_account保持不变**，还是上一次push时的值.
    1. 如果开启的模式是**限时免密**，就会用之前current_push_account对应的密码提交.
      - 限时免密必定会在限定时间内**同时缓存账号和密码的**，不可能只缓存账号不缓存密码，用脚趾头都想得到，但还是在这里多费一句口舌.
    2. 如果开启的是**终身免密**，就会用之前的current_push_account到密码本中去找密码提交.
  3. 因此**[1.2.]** 的写法称为**账号切换-URL**，**[3.]** 的写法称为**账号持续-URL**.
    - 之前所说的**令人费解的问题**就在这两种URL造成的，场景就是**多账号管理**：
      1. 如果你已经往账号A的仓库push了一次，接着想往账号B的仓库push.
      2. 此时如果你采用**账号持续-URL**来push账号B的仓库，由于current_push_account还指向账号A，因此这次push会用A的账号和密码提交到服务器验证，必然会验证失败：
        - 失败信息描述大致为：Permission to B/XXX.git denied to A，即账号B的XXX仓库拒绝了账号A的push.
        - 写得清清楚楚，各自管各自的仓库，不相互妨碍是最基本的原则.
      3. 因此多账号管理必须使用**账号切换-URL**.
        - 除非是git客户端刚刚安装完第一次进行HTTPS-push可以使用**账号持续-URL**（如果是多账号情景下）.
          - 因为此时current_push_account还是空的值，服务器发现没有给出账号则会返回一个账号给客户端，然后客户端再将该账号赋值给current_push_account.
            - 但也不要依赖这个功能，GitHub虽然支持，但其它git代码托管服务器不一定支持.
          - 之后想要push其它账号的仓库就必须使用**账号切换-URL**了！

<br>

- 小结：
  - 如果你只玩儿一个账号，用**账号持续-URL**无妨.
  - 如果你玩儿多个账号，则最好（建议是**必须**）使用**账号切换-URL**.

<br><br>

#### 5.2 &nbsp;&nbsp;免密模式：[·](#目录)

- 免密模式就两种，一种是限时免密另一种是终身免密：
  1. 限时免密：将账号-密码临时保存在缓存（名叫git-credential-cache）中.
    - 并且缓存的寿命是限时的（可以规定时长），超出时长就从缓存中清掉.
    - 缓存只存在于内存中，不会写入硬盘，安全性还行.
    - 缓存的密码是明文，相当危险，但至少是限时的.
  2. 终身免密：将账号-密码永久（写入磁盘）写入~/.git-credentials文件
    - 密码是明文，超级危险，是永久保存的.
  3. 两种模式在push的时候都是根据current_push_account到相应的文件中去找对应的密码提交到服务器，只不过：
    1. 限时免密的“那个文件”是内存中的限时动态清理的cache文件.
    2. 终身免密的“那个文件”是磁盘中永久保存的~/.git-credentials文件.
    3. 两种文件的格式都是一样的：
      1. 一行一个账号-密码对.
      2. 格式是：https://账号:密码@git服务器主机域名
        - 例如：https://spider-man:12356@github.com

<br>

- 因此可以看到，HTTPS免密验证的代价很大，风险很高.
  - 毕竟，这种验证方式已经那么简单无脑了，还要再偷懒，代价是有点，没毛病.

<br>

- 设置免密模式：
  - 两种模式相互排斥的，要么限时、要么终身，不能两个都开启也不能两个都关闭.
  - 默认情况下是限时免密-15分钟.
  - 调成限时模式：切换后账号密码保存在内存中的限时cache文件中
    1. git config --global credential.helper cache    # 默认为15分钟
    2. git config --global credential.helper 'cache --timeout=XXX'  # 设定限时，XXX以秒为单位，比如3600就表示1小时
  - 调成终身模式：切换后账号密码保存在磁盘中的~/.git-credentials
    - git config --global credential.helper store
  - 上面的命令仅仅就是修改了~/.gitconfig配置文件而已，分别多了一句话罢了：要想生效必须**退出git-bash后重新打开**才行
    1. [credential] helper = cache
    2. [credential] helper = cache --timeout=XXX
    3. [credential] helper = store

<br>

- 别忘了，current_push_account在两种模式下都是存在的！
  - 要玩儿多账号一定要用**账号切换-URL**哟！小心出现**令人费解的问题**.

<br>

- 限时免密模式下的后台进程：git-credential-cache.ps
  - 限时模式下会启用一个特殊的后台进程git-credential-cache.ps来管理限时存放密表的cache.
  - 该进程会清理cache中超时的账号-密码.
  - 如果你临时有事想离开电脑一会儿，但又不想锁屏、关控制台，你就可以先关掉该进程防止图谋不轨者.
    1. 关闭该进程会自动清除cache，之后想要再push必须要输入密码.
    2. 关闭后第一次输入密码push后会重新开启该线程.
    3. 关闭命令：git credential-cache exit

<br>

#### 5.3 &nbsp;&nbsp;Mac的keychain终身免密机制：[·](#目录)

> Mac采用keychain-HTTPS免密验证机制，是终身免密的.

- Mac的git客户端的HTTPS验证不使用credential-helper管理，而是使用**keychain**管理.
  - 类似Safari和iCloud的钥匙串，第一次输入密码的时候就会把账号密码记录在keychain中.
  - 而keychain是苹果公司自己的秘钥管理产品，具有很高的安全性，加密措施非常可靠.
  - keychain是终身免密的，因此Mac的HTTPS验证是终身免密的.
    - 当然你也可以关闭keychain功能，强行使用credential-helper，但没有必要，那不是傻吗？

<br><br>

### 六、综合评价：[·](#目录)

1. 虽然keychain安全性比credential-helper高很多，但由于HTTPS协议本身的加密机制（对称式加密）安全性不如SSH验证（RSA加密）.
    - 虽然keychain对密码进行了加密再保存，但由于HTTPS验证密码的方式是对称式的，如果不法分子获取了你加密后的密码也是有很大可能破解的.
    - 但SSH的RSA加密机制验证密码的方式是非对称式的，拿到公钥也无法破解.
2. 因此，出于完全性考虑，要坚持以下原则：
  1. clone用HTTPS（很多项目的代码对于clone也只给出HTTPS URL），毕竟通用.
  2. 其余操所有操作：
    1. Unix/Linux、Windows使用SSH验证.
    2. Mac使用HTTPS无妨，最好还是使用SSH验证.

<br>

- 总结：SSH验证虽然配置麻烦，但使用的时候即方便又安全，因此还是要**以SSH验证为主**.
