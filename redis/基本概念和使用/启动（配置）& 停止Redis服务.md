# 启动（配置）& 停止Redis服务
> 任何数据库在OS中都必须要以服务的形式运行.
>
>> - 即后台进程，需要实时监听用户请求（访问数据库）.
>
>> - redis服务的启动方式有两种：
>>    1. 开发环境下启动：即直接利用redis-cli命令行工具在命令行上临时启动.
>>       - 属于临时启动，退出终端或者重启之后服务都会自动关闭.
>>       - 用于开发环境下的临时调试.
>>    2. 生产环境下启动：利用redis的初始化脚本，让redis随OS启动自动开启.

<br><br>

## 目录

1. [开发环境下启动（直接启动）& 停止Redis服务]()
2. [开机自动启动（生产环境下启动）]()
3. [关于Redis配置]()

<br><br>

### 一、开发环境下启动（直接启动）& 停止Redis服务：[·](#目录)
> **Redis的默认端口是6379，手机键盘上对应MERZ，是意大利女歌手的名字.**

<br>

**1.&nbsp; 直接启动：**

```Shell
$ redis-server [--port 指定一个redis监听的端口号]
```

<br>

**2.&nbsp; 安全停止：不管是在开发环境还是生产环境，都是这个命令停止**

- 向redis发送shutdown命令.
   - 该命令是安全的，即使在Redis正在同步硬盘数据时也能安全关闭.
      - 先断开连接，再继续按照配置执行同步，同步完毕后再关闭服务.
      - 和kill SIGTERM的效果完全一样.

```Shell
$ redis-cli shutdown
# 等价于
$ kill -15 %redis的pid
$ kill -SIGTERM %redis的pid
```

<br><br>

### 二、开机自动启动（生产环境下启动）：[·](#目录)
> 只有Linux、Mac OS X才行，Windows的Cygwin并不是OS（而是虚拟环境）.

<br>

**1.&nbsp; Linux下的配置：Ubuntu & Debian**

<br>

- 设端口号为PORT，默认情况下是PORT=6379，当然可以自由定夺.

<br>

- 1. 复制初始化脚本redis-stable/utils/redis_init_script到/etc/init.d/目录中.
   - 并重命名为redis_$PORT.
   - 同时要让redis_$PORT的端口号和$PORT保持一致.

```Shell
[?]$ cd ~/downloads/redis-stable
[~/downloads/redis-stable]$ cp utils/redis_init_script /etc/init.d/redis_$PORT
[~/downloads/redis-stable]$ vim /etc/init.d/redis_$PORT  # 修改REDISPORT=$PORT
```

- 2. 创建Redis依赖目录：

```Shell
[~/downloads/redis-stable]$ mkdir /etc/redis  # 存放Redis配置文件
[~/downloads/redis-stable]$ mkdir -p /etc/redis/$PORT   # 存放Redis持久化文件
```

- 3. 将redis配置模板文件redis-stable/redis.conf复制到/etc/redis/中，并命名为$PORT.conf
   - 并编辑$PORT.conf文件，修改参数如下：

```Shell
[~/downloads/redis-stable]$ cp redis.conf /etc/redis/$PORT.conf
[~/downloads/redis-stable]$ vim /etc/redis/$PORT.conf
```

| 参数 | 修改后的值 | 说明 |
| --- | --- | --- |
| daemonize | yes | 让Redis以后台进程运行 |
| pidfile | /var/run/reids_$PORT.pid | 指定Redis的pid文件位置 |
| port | $PORT | 指定Redis的监听端口号 |
| dir | /var/redis/$PORT | Redis持久化文件的存放位置 |

- 4. 使用init.d中的启动脚本来启动Redis：

```Shell
[~/downloads/redis-stable]$ /etc/init.d/redis_$PORT start
```

- 5. 设置随系统自动启动：

```Shell
[~/downloads/redis-stable]$ sudo update-rc.d redis_$PORT defaults
```

<br>

**2.&nbsp; Mac OS X下的配置：**

```Shell
$ ln -sfv /user/local/opt/redis/*.plist ~/Library/LaunchAgents
$ launchctl load ~/Library/LaunchAgents/homebrew.mxcl.redis.plist
```

<br><br>

### 三、关于Redis配置：[·](#目录)
> 分为：
>
>> 1. 直接启动时配置.
>> 2. 脚本启动时配置.
>> 3. 运行时配置.

<br>
