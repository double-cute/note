如果用HTTPS URL来定位远程仓库

验证只针对push，不针对clone、fetch、pull

1. GitHub官方推荐的验证方式，理由如下：
  1. 相比SSH验证最明显的有点就是使用方便，无需进行任何配置即可验证.
    - 简单无脑，每次验证都需要输入GitHub用户名和密码.
  2. 适用于国际互联网：即使有防火墙或代理的情况下也同样适用.
    - 相比于SSH验证，后者在局域网（或者公司、集团、组织内部）应用更加广泛.
  3. 安全性强：传统的账户密码登录式验证，只要自己不泄露是无解的.

2. 缺点：
  1. 每次验证都要输入用户名和密码非常不方便.
  2. 也是由上一点导致的，如果想记住用户名和密码（做到免密登陆）则需要配置：
    - 如果想像SSH验证那样终身免密，则会付出巨大的安全代价（密码明文在本地暴露）.

3. HTTPS验证在不同平台上有不同的特性，分Unix、Mac、Windows
  - 目前Windows的git客户端实现还有部分平台兼容问题，因此验证会有点儿失常，因此就以Unix为准.
  - 而Mac则对Unix进行了一定的加强.


Unix：
4. HTTPS登陆方式：credential helper cache
  1. 首次验证：第一次push的时候需要你输入远程仓库所在的GitHub账户的密码.
    1. 密码输入后credential helper的cache会默认帮你保留登陆信息（账户、密码）15分钟.
    2. 当然你可以修改这个缓存时间：git config --global credential.helper 'cache --timeout=5'   # 只缓存5秒
      - 超过设定的时间再push就要重新输入密码了.

多账户管理：就是git HTTPS验证的底层机制（隐藏着账户域来表示登陆的GitHub账户）
不写[账户名]域push时，在缓存限时之外，必然会让你同时填写账户名和密码.
也就是说HTTPS验证的两个核心：1.GitHub账户名和密码  2.仓库定位

仓库想定位很容易，一个正常的HTTPS URL就可以定位到https://github.com/path/repo.git
但不是什么人都可以随意push的，必须是仓库的所有者才能push，仓库所有者的完整信息包括 ID+passwd
  - 可为什么不直接用repo_path来推断出用户ID呢？
   - 答案：GitHub确实是repo_path == 用户ID，但其他git托管服务不一定是这样！还有gist等很多git托管服务呢！

5. 切换账户验证：其实HTTPS URL中隐藏着一个登陆账户的信息
  1. https://[账户名[:密码]]@github.com/path/repo.git
    - 表示用[账户名]的GitHub账户的身份登录repo.git仓库
    - 当然切换新用户后肯定是需要输入新的密码才行的.
    - 也可以直接在URL中给出密码，这种是万能的方式，可以随意多GitHub账号切换，但风险是非常大的，密码明文直接见光！！！
      - 同事也暴露在.git/config中.
  2. 如果[账户名[:密码]]不写，就默认用“上一次登陆的账户作为此次登陆的账户”
    1. 假如上一次是git push https://github.com/double-cute/note.git master的话（假设是本地的第一次push）.
    2. 那么下次git push https://github.com/qq-test1/test.git master时会报错，告诉你Permission to qq-test1/test.git denied to double-cute
      - 就是qq-test1/test.git仓库拒绝double-cute的登陆，可见这次登陆的账户是上一次的double-cute，提交的密码无疑也是上一次double-cute的密码.
        - 但这显然是不对的，git还没有那么聪明，不能直接看出qq-test1/test.git这个仓库属于qq-test1这个GitHub账户的.
          - 为什么这么笨看不出来呢？这个规律很明显啊，GitHub上的仓库path就等于GitHub的账户名啊！
           - 那是因为大型的git托管服务并不是只有GitHub一家在做，还有Gist等等好多家做，其他产品并不一定要符合这个规矩啊！
           - 因此git并不会直接将仓库的path作为默认的登陆账号进行登陆的！！
    3. 因此完整的HTTPS验证URL应该是https://[user-id[:passwd]]@git托管服务器主机域名（不一定是Github，也可以是gist）/path/repo.git
      - 表示用名为user-id的账户身份来验证登陆.
      - 新身份必然要输入新的密码咯！

    - 也就是说git bash总有一个“当前验证账户”，加上[账户名]域就相当于切换了”当前验证账户”，不加还是以上一次的登陆账户作为“当前验证账户”。


6. 如果GitHub（或者Gist）账户多了，管理起来就麻烦了，还好有credential-cache机制，可以在15分钟（或其他时长）之内多个账户免密登陆，但是过了这个时间就麻烦了，push又需要重新输入密码.
  - 设想，如果账户非常多，那重新输入密码就是一件工作量很大的事了.
  - 如果能像SSH验证那样永远终身免密该多好啊！
  - git credential有两种模式：一种是限时cache（就是上面所说的那样），另一种就是终身机制（和SSH的效果一样了）.
    - 调成限时cache模式：git config --global credential.helper cache   或者  'cache --timeout=XXX'    # 前者默认15分钟
    - 调成终身免密模式：git config --global credential.helper store
      1. 一听这名字就应该知道终身是怎么实现的了，必然是用一张表记录所有的账户信息（账户名和密码）.
      2. 开启该功能后开始，第一次push一个仓库会让你输入账户信息（账户、密码），如果没有[账户名]域的话则同时输入两者（展示QQ图片）.
        - 然后会将用户信息保存在（**store**）在~/.git-credentials文件中.
        - 形式是，每行一条账户信息：https://账户名:密码明文@github.com
        - 可以看到是明文的密码，相当危险.
  - 之后每次push都会拿当前登录中账户到.git-credentials文件中去找相应的密码登陆，如果找不到就用上一次的登陆信息去登陆.
    - 可是找不到是什么鬼，怎么可能会找不到呢？不是都会记录在.git-credentials中吗？
      - Unix中，如果不给出[账户名]域的话不会弹出对话框让你同事输入账户名和密码的.
      - 会用上一次登陆信息去登陆，比如上次是double-cute@github.com/double-cute/test.git，这次直接是github.com/qq-test1/test.git的话，会拿上一次的double-cute的信息去登陆.
        - 由于本次没有指出登陆使用GitHub账号名，自然在.git-credentials文件中找不到信息，也就只能拿上一次double-cute的信息去登陆这次test.git仓库，但服务器检查显然不通过，因为test.git仓库不属于double-cute这个GitHub账户！！从而发生denied报错.


第一次（比较特殊）：无用户信息不写[账户名]域push时，GitHub服务器根据仓库URL返回所属者的ID，因此本地能确定登陆账号是什么，因此会将当前登陆账号设为该登陆账号.
之后想push其它用户的仓库就不得不加[账户名]域了，否则当前登陆账号指向上一次的账号必定会发生denied错误！


Windows：有问题，当前登陆账户不能及时切换更新，有一定bug


Mac：自己有一个Keychain管理器，就像Safari的钥匙串一样，

keychain机制：只匹配仓库的URL，不保存GitHub账号信息，
 - 例如：https://qq-test1@github.com/qq-test1/test.git和https://github.com/qq-test1/test.git，完全一样，只提取出其中的仓库URL，也就是https://github.com/qq-test1/test.git部分
 - 然后直接录URL-PASSWD对儿到keychain中，而不保存在.git-credentials中，毕竟明文太可怕，因此keychain的目的就是升级git credential的安全性而已.
 - 因此在keychain机制下就没有切换登陆账号一说了，只有URL-PASSWD对儿.
 - 只有你不管用那种方式（上面的左或者右）都是根据URL到keychain中找相应的PASSWD送去远程验证。




 7. 总的原则是：Unix下和Windows下最好只用SSH验证（因为.git-credentials都有效），想要HTTPS免密终身就只能用Mac.
