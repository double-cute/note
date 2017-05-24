# AOF
> Append Only File，通过记录写命令以防止数据丢失.

<br><br>

## 目录

1. [默认情况下不开启AOF功能：必须通过配置开启](#一默认情况下不开启aof功能必须通过配置开启--)
2. [AOF文件的位值以及命名](#二aof文件的位值以及命名)
3. [AOF文件的优化重写](#三aof文件的优化重写)
4. [AOF文件的OS同步问题](#四aof文件的os同步问题)
5. [启动加载AOF文件](#五启动加载aof文件)

<br><br>

### 一、默认情况下不开启AOF功能：必须通过配置开启  [·](#目录)

<br>

```Shell
appendonly yes|no
```

<br><br>

### 二、AOF文件的位值以及命名：[·](#目录)

```Shell
dir $aoffile_dir # 同样也RDB文件的存储目录
appendfilename $aoffile_rename
```

<br><br>

### 三、AOF文件的优化重写：[·](#目录)

- 例如：`set foo 1`，`set foo 2`，`set foo 3`可以优化成一条`set foo 3`.
   - 这样可以压缩AOF文件，以达到优化的目的.

<br>

**1.&nbsp; 主动手工执行重写：bgRewriteAOF命令**

- 该命令通过一定的算法将AOF文件重写，实际算法比上例介绍的复杂得多.
   - 优化只跟内存中的当前数据有关.
      - 以bg开头的肯定都是一部命令，该命令会返回信息：`Background append only file rewriting started`.
- 可惜的是没有类似lastsave之类的命令查看重写是否完成.
   - 即使没完成而异常退出也没关系，只不过AOF文件大一点儿而已.

<br>

**2.&nbsp; 自动重写：配置**  

- 达到条件后调用bgRewriteAOF命令（不输出信息）.

```Shell
# 1. 只有当前.aof大小超过上一次重写时的120%时才能开启重写
auto-aof-rewrite-percentage 120
# 2. 只有当.aof文件大于某个值时（单位是mb）才会开启重写
  # .aof文件过小不影响加载性能
auto-aof-rewrite-min-size 64mb
```

<br><br>

### 四、AOF文件的OS同步问题：[·](#目录)
> 和RDB快照不同的是，AOF的重写是连续性的.
>
>> 1. RDB是一次性的：某一时刻将内存中的数据整体写入硬盘.
>> 2. AOF是实时连续地修改.aof文件：
>>    - 内存中常驻.aof文件内存镜像.
>>    - 一有更新数据的命令就append到.aof文件内存镜像的末尾.
>>    - 每次append都将.aof内容flush进OS缓存.
>>       - 但是OS只会（默认的OS配置下）每隔30秒将缓存同步到硬盘一次.
>>
>>> - 如果无法容忍由于这个原因导致的数据损失，则需要进一步配置Redis.

<br>

```Shell
appendfsync always|everysec|no
```

1. always：每次更改AOF都强制OS同步.
   - 最安全，但特别吃机器性能.
2. everysec：每秒强制OS同步一次.
   - 推荐，特别实在机器性能一般的情况下（经济实惠，损失较小）.
3. no：完全不主动同步，完全依赖OS的默认配置（30秒一次同步）.

<br><br>

### 五、启动加载AOF文件：[·](#目录)

<br>

- 如果开启了AOF功能，那么Redis启动时默认从AOF文件中加载数据库，而不是RDB文件.
   - 因为Redis认为AOF丢失的数据更少.
