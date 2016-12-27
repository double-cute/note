# 配置SSH秘钥
> SSH是非常典型的采用公钥加密技术的通信协议.
>
>> 因此，通信之前必须配置好秘钥对才行.
>> GitHub要求采用SSH通信的**所有**的交互操作（clone、push、pull、fetch）都必须要验证身份，不像HTTPS那样只有push才验证.

<br><br>

## 目录

1. []()

<br><br>

### 一、SSH简介：

1. 全称：Secure Shell
2. 由来：IETF的网络小组（Network Working Group）制定.
3. 应用：是一种建立在应用层基础上的安全协议，专为**远程登录会话**和其他网络服务提供的**安全性协议**.
  - 利用SSH协议可以有效**防止**远程管理过程中的**信息泄露**问题.
4. 该协议适用于多平台，目前市面上大多OS都支持SSH协议.

<br><br>

### 二、SSH通信基本模型：
> 采用公钥加密技术，因此通信双方各持有各自的一对儿秘钥（公-私）.

- 通信准备阶段：
  1. 通信双方先各自生成自己的密钥对.
  2. 双方将各自的公钥交给对方，而各自的私钥自己保管.
- 通信阶段：
  1. 互验身份：
    - 通信发起方用自己的私钥构造数字签名发送给对方，对方验签.
      - 目的是为了让对方确认“我是我”，不是其他人冒充我.
    - 对方验签成功后，对方再用自己的私钥构造数字前面发给通信发起方验签.
      - 目的是为了让我确认“他是他”，不是其他人冒充他.
  2. 正常通信：
    - 双方用对方的公钥加密信息，各自用自己的私钥解密信息.

<br><br>

### 三、生成密钥对：ssh-keygen
> 使用所有类Unix系统都有的命令ssh-keygen自动随机生成一个RSA密钥对.

- ssh-keygen：
  - 即SSH Key Generator的意思，即SSH秘钥生成器.
  - 命令选项：
    1. -t：type，即采用的公钥加密算法类型，合法的有rsa、dsa、rsa1、ecdsa、ed25519这5中.
    2. -b：bytes，即秘钥的长度，即二进制位数.
      - 对于RSA算法，长度就是(n, pub, pri)中n的二进制位数.
      - 一般选择1024、2048、4096，一般不超过4096，毕竟2048已经无法破解了，4096买的只是个安心罢了，再往上走加解密速度就太慢了，不划算.
    3. -C：comments，即密钥的说明，是一种注释信息，就是一个字符串，用来大致说明该秘钥拥有者的信息等，内容随意，拥有者自己安排即可.
      - 注释信息会写在公钥文件中，但**注释信息在身份验证、加解密过程中不起到任何作用**，仅仅是一种给外界了解信息的窗口而已.
  - 示例：

 ![](assets/ssh-keygen.png "ssh-keygen用法示例")

 - 说明：
   1. 密钥对可以放在任意位置并且可以随意命名：第一个红圈处就是让你填写私钥的存放位置以及私钥文件名.
   2. 公钥文件名就是私钥文件名加上一个".pub"后缀，从“Your public key...”的输出中可以看出.
   3. 第二个和第三个红框的位置就是让你填写私钥的passphrase：
     - passphrase就是私钥的密码.
     - 这个密码将是安全的最后防线，在你**每次**进行SSH通信的时候都需要你输入这个私钥密码才行.
       - 这个密码是为了防止你的私钥被盗后被非法使用，坏蛋即使偷走了你的私钥，那在使用的时候也必须输入这个私钥密码才能正常使用.
       - 缺点就是**任何远程操作都需要输入这个密码，失去了SSH通信的免密优势**.
     - Mac的keychain可以记住这个密码，防止你每次操作都需要输入密码，做到终身免密.
     - **当然，在这里可以不输入密码，这样就默认你的私钥没有密码，以后使用的时候也将免去输入密码的环节**.
   4. ssh-keygen随机生成秘钥的机制：
     - 在于**随机性*.
     - 即**每次使用该命令生成的秘钥都是不同的**.
       - 对于RSA来讲就是每次生成的(n, pub, pri)组合都是不同的.
       - 但这个不同并不是绝对的，只是能保证生成**两次相同组合的可能性非常非常小，几乎不可能**.
         - 如果刚好相同的这种倒霉事情发生了，那你就**删了这个秘钥重新生成一次好了**.
   5. 最后的randomart image就是将公钥的内容的二进制代码无脑地转化为ASCII字符形成一张图片，可以让你大致看出两个公钥是否存在明显的不同.
     - 一种纯心里安慰罢了.
     - 因为每次运行ssh-keygen命令生成的秘钥都是不同的，大可放心.

<br><br>

### 四、秘钥文件的内容：

- 对于RSA算法，无非就是：
  1. 公钥包含(n, pub)
  2. 私钥包含(n, pri)
- 但文件中并不是直接写出n、pub、pri这几个数字，需要进行一定的特殊处理，原因是由公钥引起的：
  1. 公钥是公开的，需要对外开放，需要在网络中传播.
  2. 网络传播就意味着要跨越不同的平台.
  3. 跨越不同平台就意味着多平台的兼容性，因此即不能直接用整数的ASCII码编码也不能直接用整数的二进制编码.
    - 考虑到有些平台是大端，有些是小端.
- 由于公钥这种东西比较特殊，不想视频、图片这种文件那么庞大，因此不适用视频、图片等的编码方案.
  - 于是国际标准化组织专门制定了ASN.1编码方案专门来编码公钥.
  - 编码的结果还是用普通的ASCII字符来呈现.
  - 示例如下：

公钥：a_test_key_id_rsa.pub

```
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQDWUG3es1z4AspFwByVpvYML56VjZwkyUJd6S2Ogfiw2OrC18k3ULP7Ll04J1B6REu9lmgAgUQMXjfUzmHzESn9FbybZgCT9zTTMg77CFYcXO4j6sZMU3mbIfhlLhJ1oMaY9Z60Ofw/g497NTf9WuXiCOgED8+198MPj2g/At8+2f+0wA2pjFN6syKVgltxtEMiSEL+fVkqwOuUuzZl7C1tl3eslh3uFhzgX9jKj2C85ow8mnPzTl4Eu+0AGetJ4ezDr4N5BYJJoa1rzwiut+y6+xgsFmHbQumIst61R1tSbeyX+Bvfw/zZeMdhk1nPznkkbmh5IJojRrSW1sKtLTqwX7DQRBydXpv2IlS2fv7/x01W2zkGzxvoMcmCwCIxw3zKH8cZxZEoYcTnsnkvXGGezNkeeJwsWe6RdYXMc5eRiebdqDI/miipjbL/V91MOeyUAI3cpBvs2jj3cH/A0/2ni3a3p5ZkpMlY5nkKR6D3CWlV3P2wN3f4jUSfyDsi1df7M87jgasaIezkiYv9IOa82BU/Tj00HwhFU1cV0mDMBQcTJePb0L+c9+wpapS1HbBIMU9dPdzq22sAXEr1sW9dmX1sYJai51cyIzRw7AHjTtsWDYyNUE2NQb87h8vNTAfojuW90UambTeu+zUjqGWW9Gb8FO70nc+vjlk3ei261w== A Test Key
```

- 其中开头的ssh-rsa和最后的A Test Key都是注释信息，实际使用时没有任何作用，将被忽略.
- 中间的一大堆编码就是表示(n, pub)

私钥：a_test_key_id_rsa

```
-----BEGIN RSA PRIVATE KEY-----
Proc-Type: 4,ENCRYPTED
DEK-Info: AES-128-CBC,5B727487EDCC1EA77EEE31FDD1814AC5

krAoQhlMTaznFnlSGmpJuGDe3FNKkI4Tdphh/57hoqA00ogpWHoQ2++GzW8HmSRd
nPqSNiCvpnx0QbWgYFcw5agnwlE+bjC4izj7qrjW6zQGkGC40qyqNjDKgCFpbs4f
6Klcb3HivXlx4RArnHYXgbkoybUWnwqpf7Pf8FgntwTkXN67KiteMYlpYlUK9wNy
C2YyZEZf2unXkdn7Fif1IhJZa7VH+gX03rSnS1KjNrxkwFwa9UPtJPTWtnY3HLo/
4g/TFeStw2M5L4MTkhCjoN4qTJQDnvmn2/JJRURGdkdcR/x7Fd+ikr42UGCgiNkF
itd6CY1kSY9+8Nqtxr7DzYpwvLqAzw0u...VgGj+Iem35H1WW81atHmoJHRLxNM4
-----END RSA PRIVATE KEY-----
```

- 头4行和最后一行也是注释信息，实际使用时没有任何作用，将被忽略.
  - 其中Proc-Type表示该私钥被加密过了，即passphrase.
  - 私钥密码使用AES算法加密，在DEK-Info中指出.
- 中间的一大堆编码就是表示(n, pri).

<br><br>

### 五、交换公钥：
> 有了SSH秘钥对了之后，通信双方只要把各自的公钥交给对方即可通信了.
>
>> 如果有数字证书则可以免去这个步骤了.

- GitHub的应用：
  1. 像GitHub这样的大网站肯定是有自己的数字证书.
    - git客户端（Windows的git-bash等）在安装时会自动将GitHub的数字证书安装在你电脑里.
      - 像类Unix系统都是默认安装的，git客户端（git命令工具集）也是自动安装的.
    - 因此客户端验证服务器的问题就解决了.
  2. 但GitHub不要求用户拥有自己的数字证书，原因是：
    1. 用户大多数是个人，非常零散，而且数量庞大.
      - 这种情况下，人人拥有数字证书的可能性本来几乎为0.
      - 如果人人都有，证书颁发机构岂不是爆炸.
    2. 注册数字证书成本也是有的，并不是所有人都愿意花这个钱.
  3. 因此GitHub要求用户上传自己的公钥.
    - GitHub自己的技术力量能保证服务器中公钥的安全性.
    - 因此服务器端验证客户端的问题就解决了.

<br>

- 给GitHub上传自己的公钥：公钥绑定账号
  - 登录GitHub -> 右上角"View profile and more" -> Settings -> "SSH and GPG keys" -> "New SSH key" -> 填写一下要上传的公钥的注释说明，并把公钥文件内容复制粘贴进去点击确认 -> 这里会让你输入一下GitHub账号的密码.
  - 这个过程就将**公钥和GitHub账号绑定**在一起了.
  - 之后你和GitHub通信就可以验证你的身份了，但具体是如何验证的后面会详解.

<br><br>

### 六、GitHub多账户管理：
> GitHub多账户管理是指，在本地有多个仓库分别关联着多个不同GitHub账号下的远程仓库.

#### 6.1 SSH公钥的**主机唯一性**：

- 一个错误的解决方案：将一个公钥和多个GitHub账户绑定，就是将同一个公钥上传到多个GitHub账号上
  - GitHub会直接拒绝，你在上传公钥的时候GitHub就会跳出一个警告页面，提示你这个公钥已经存在了，发生冲突了.
    - 这是为什么呢？公钥和账号不是绑定在一起的吗？账号A绑定了公钥pub，账号B为什么就不能绑定pub呢？
    - 因为SSH公钥具有主机唯一性原则：
      1. 国际标准化组织当初制定SSH标准时规定的，SSH公钥在一台主机中必须是唯一的，不能重复.
      2. 而上面账号A和账号B显然都是在一台GitHub.com主机中的.
      3. pub之前绑定了账号A就意味着pub已经存在于主机中了，后面再想绑定B就和主机中已经存在的pub重复了，因此报错.
- 那该怎么解决多账户管理的问题呢？
  - 很简单，再用ssh-keygen生成一对秘钥不就完事儿了吗？把新的公钥上传GitHub即可.
    - 唯一的缺陷就是，每有一个GitHub账号，就有一对秘钥，本地需要保护好多个私钥.

<br>

#### 6.1.2 主机如何快速确定一个公钥是否已经存在：公钥指纹

- 前面已经说过了，公钥文件使用长长的字符串描述了（私钥也是），如果要验证一个公钥是否和已有的冲突，就需要进行大规模的字符串对比（和服务器中已保存的大量公钥进行比较），这是非常耗时的.
  - 最直接的解决方案就是对公钥内容进行字符串Hash，用Hash表作为公钥数据库，方便快速查找.
    - 但实际上并不用那么麻烦，公钥文件本身就描述了(n, pub)两个整数，而用ssh-keygen生成的(n, pub)组合本身就具有唯一性（不可能重复）.
    - 因此**直接提取出(n, pub)本身**作为“Hash值”不是更加直接了当吗？
      - 它本身就可以作为公钥的快速索引，这里Hash表的查找复杂度当然是O(1)，毕竟ssh-keygen命令保证了(n, pub)几乎不可能重复.
- **公钥指纹**：
  - 就是公钥的(n, pub)用二进制格式保存，因此可以唯一确定一个公钥.
    - 本来公钥是用ASN.1规定的字符串形式保存的.
  - 一般为了方便阅读会用十六进制来显示.
    - 相比ASN.1格式大大简化.

![](assets/finger-print.png "公钥指纹示例")

- 上传公钥时服务器判断该公钥是否和已上传的重复的流程：
  1. 解析公钥的ASN.1格式内容，得到(n, pub)的整数值，即公钥指纹.
  2. 接着查看服务器中是否还有相同的公约指纹，如果有则拒绝该公钥的上传.
- 总结一下ASN.1格式和指纹格式的区别：
  1. ASN.1格式是为了方便跨平台、兼容、快速高效传播.
  2. 指纹格式是为了方便管理、快速定位.

<br>

#### 6.2 SSH通信协议下GitHub服务器如何验证客户端的身份？
> 之前讲过客户端上传的公钥是和GitHub账户绑定在一起的，但刚刚也讲了公钥又具有主机唯一性，那岂不是公钥和主机绑定在了一起了.
>   - 两者之间似乎是矛盾的，由于主机唯一性导致了不同账号下不能绑定相同的公钥，这岂不是非常不方便.

- 现在先来解释为什么公钥绑定主机（主机唯一性）的同时GitHub还要将公钥和账号也绑定.
  - 首先，这不是多此一举，这种矛盾是有原因的，这种“看似别扭”的设计其实是为了**在免密的条件下也能验证客户端身份**：
  - 之前介绍过，HTTPS通信方式必须输入账号密码（只有push的时候须要），而SSH则不用.
    - 虽然SSH不用，但背后必定还是躲不过验证账号这个环节的，只不过它智能地替你做了这个环节罢了，毕竟：
      1. GitHub肯定必须要你出示自己的身份（就是GitHub的账号）.
      2. 再检测你要求操作的远程仓库是否属于这个账号.
      3. 如果确实属于，还要确定当前是否是你本人而不是他人冒充（核对你的密码）.

<br>

- 那GitHub是如何做到在不输入账号密码的情况下（免密）也能验证客户端的身份呢？原因有2：
  1. GitHub服务器的特殊组织方式：为每个账户创立一个目录（以账户名作为目录名），账户所托管的仓库都放在各自的目录中.
    - 这就导致了托管仓库所属的账号的ID会直接出现在SSH的URL中：git@github.com:**GitHubAccountID**/xxx.git
    - 这就解决了向服务器出示账号的问题了.
      - 同时也会检测服务器上**GitHubAccountID**目录下是否存在xxx.git仓库，这同时解决了如何验证仓库所属人的问题了.
  2. GitHub要求公钥也要和账户绑定在一起.
    - 这样，GitHub就会根据URL中提供的账号ID去查找该账号下绑定的所有公钥，只要有一把公钥可以验证客户端发来的数字签名就能彻底核实你是不是别人冒充的了.
    - 因此，核实身份冒充的问题也解决了，至此完全实现了免密验证客户端身份的要求了.
      - SSH秘钥对的相互匹配就相当于密码验证.
- 身份验证成功后双方就可以越快地继续通信了（当然是继续SSH公钥加密方式通信）.

<br>

- GitHub其实是一种变相的CA，GitHub下的SSH URL其实是一种变相的数字证书：
  - GitHub**自己管理用户的公钥**，它行使CA的权利，它凭借SSH URL提供的账号ID来查找该用户底下**合法**注册的公钥.

<br>

#### 6.3 本地git客户端如何配置SSH秘钥？
> 虽然前面说了，你用ssh-keygen生成多个密钥对绑定多个不同GitHub账号就可以解决多账号问题，这没毛病.
>
>> 但如果你仅仅只是做了这**一步**的话还是不行滴！会出现各种Permmision denied报错！

- 那是因为：虽然服务器端知道知道你的身份，但客户端却不知道你的身份！
  - 你现在有那么多身份（GitHub账号）了，客户端哪会知道你当前是哪一个身份？
    - 具体的讲就是：客户端需要使用当前账号对应的私钥构造数字前面让GitHub去验证，但是你还没有告诉git客户端该账号的私钥在哪里.
    - 再具体一点就是，你在clone、push、pull、fetch的时候会给出托管仓库的SSH URL，虽然URL中包含了仓库所属的账号（**GitHubAccountID**）.
      - 但你还没有告诉git客户端，该账号所对应的私钥在哪里，没有私钥如何构造数字签名让GitHub服务器验证呢？
      - 再进一步说，不知道私钥在哪儿，该怎么破解对方发来的密文呢？
  - 最先想到的解决方案就是：和GitHub一样，在本地也弄一个账号-私钥绑定机制，比如写一个git的配置文件，文件中每一行都是一个“账号-私钥路径”，然后让git客户端和GitHub远程通信时自动扫描这个文件自动调出相应的私钥构造数字签名即可.
    - 可惜的是git客户端并没有提供这样的功能，原因是：
      1. git客户端进行SSH通信底层调用的是OS提供的SSH通信功能.
        - git并不会花时间去自己重新开发一套SSH通信软件.
      2. 而底层的SSH通信支持并不会知道自己会被谁应用，它当然不会知道GitHub账户之类的东西了.
        - 因此git客户端采用的私钥管理机制实际上是底层SSH通信支持所使用的私钥管理机制.

<br>

- 类Unix系统提供的SSH通信支持默认的私钥管理机制：
  - 就一个原则：SSH密钥的主机唯一性，密钥应该是和主机绑定在一块儿的.
    - 因此，具体做法就是把私钥和对应的主机绑定在一块儿，也就是说上面提到过的配置文件中的每一行都是一个“主机-私钥路径”.
    - 但这样又有一个新问题：如何体现出GitHub的账号-密钥绑定形式呢？
      - 也就是说现在我只能：
        1. GitHub.com - acc1_id_rsa
        2. Gist.com - acc2_id_rsa
      - 但是我的acc1和acc2都是GitHub下的账号呀！不能既GitHub.com \- acc1_id_rsa又GitHub.com \- acc2_id_rsa，因为主机重复了！
  - 但是，SSH通信**支持主机别名**：
    - 在SSH通信中的**主机**有两层属性：
      1. 主机别名：你可以随意取，取成GitHub的账号ID也行.
      2. 主机域名：即该别名实际应该指向的网络中真实的主机的域名.
        - 别名可以用在clone、push、pull、fetch的仓库SSH URL中，而实际通信时用别名对应的真实域名去定位远程仓库.
    - 有了主机别名该问题就迎刃而解了，上面的配置文件可以改成：
      1. haha(GitHub.com) - acc1_id_rsa
      2. lala(GitHub.com) - acc2_id_rsa
    - 而实际上SSH URL中的主机名其实是主机别名，如果写成：git@haha:acc1/test.git
      1. 那么首先，SSH通信软件会先用别名为haha的主机所对应的私钥acc1_id_rsa作为接下来数字签名以及通信解密的工具.
      2. 接着git客户端将acc1作为账户交给服务器端验证.
    - 为了迎合GitHub的账户-密钥绑定机制，可以把上面的haha、lala换成对应的GitHub账号ID（即分别替换为acc1和acc2），**当然也不是强制的**，那么URL相应地变成了：
      1. git@acc1:acc1/test.git
      2. git@acc2:acc2/test.git
        - 即主机别名和账号名重复，看起来有点儿别扭，但实际上是由于git客户端底层使用的是SSH通信基础软件提供的私钥管理机制所造成的.

<br>

- 当然，上面的配置只是一种演示，SSH通信基础软件有严格的私钥配置文件：~/.ssh/config
  - 以'#'开头的表示注释.
  - 它以主机为单位，里面是多个主机的配置，其中每个主机的配置规则如下：

```
Host 主机别名
    HostName 该主机别名实际指向的网络中真实存在的主机的域名
    User 本地作为SSH通信发起方时所使用的SSH通信软件的名称（这里我们使用的是git客户端，因此写git就行了）
    IdentityFile 该主机别名所对应的私钥在本机中的路径
```

- 示例：

```
# GitHub下的第一个账号：acc1
Host acc1
    HostName github.com
    User git
    IdentityFile ~/.ssh/acc1_id_rsa

# GitHub下的第二个账号：acc2
Host acc2
    HostName github.com
    User git
    IdentityFile ~/.ssh/acc2_id_rsa

# Gist下的第一个账号：test1
Host test1_haha
    HostName gist.com
    User git
    IdentityFile ~/.ssh/test1_id_rsa


# Gist下的第二个账号：test2
Host test2_helloworld
    HostName gist.com
    User git
    IdentityFile ~/.ssh/test2_id_rsa

# GitHub下默认使用的私钥
Host github.com   # 主机别名直接为网络域名，可以随意取，没有什么规定的
    HostName github.com
    User git
    IdentityFile ~/.ssh/id_rsa
```

- 总结，SSH URL的含义：
  - 正常：
  - 在GitHub下：repo在服务器中的path其实就是账号名.
