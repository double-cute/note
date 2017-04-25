# MySQL的安装和配置
> 分别介绍以下几种安装配置方式：
>
>> 1. Ubuntu包管理器.
>> 2. Linux下Binary Realease解压安装配置.
>> 3. Mac OS X.
>> 4. Windows

<br><br>

## 目录

1. []()
2. []()

<br><br>

### 一、Ubuntu包管理器：
> 基于Debian-apt软件源的安装.

<br>

```Shell
su - root

# 1. MySQL客户端 的 [内核] & [命令行接口 - 套件]
apt install mysql-client-core-X.XX  # 根据提示安装最新版，这里使用的是5.7

# 2. MySQL服务器 的 [内核] & [命令行接口 - 套件]
apt install mysql-server-core-X.XX  # 目前最新版是5.7
```

- 安装完毕后第一次设置root登录密码：mysql -u root -p
   - 回车后键入密码后生效，之后使用root登录就使用该密码.

- 记得将/usr/share/java/mysql-connector-java.jar加入到eclipse的user library中.



mysql-server   # 服务器
客户端程序
mysql-client #命令行界面
mysql-workbench
sqlyog
