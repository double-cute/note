#


<br><br>

## 目录

1. []()
2. []()

<br><br>

### 一、事务：

SqlSession：
1. 线程不安全（不能作为静态字段、对象成员）.
2. 因此每个线程都必须拥有它自己的SqlSession（不能被多个线程共享）.
// 默认autoCommit = false
- 即，每一个open一个sqlsession都开启一个事务.

insert、update、delete 底层调用的都是 UPDATE
update会立马导致脏读（dirty = true，实现中的第一句就是）

openSession：开启一个事务
commit([boolean force])：提交事务，只有batch中包含修改操作时才会真正提交. force可以强制提交.
   - 脏读操作必须只有commit后才会在数据库中真正生效.
   - 否则都只是在数据库内存镜像中修改，close后内存镜像消失，不能同步到数据库中.
   - 提交后，将内存镜像的数据同步到数据中，同步的过程是线程安全的.
rollback：在提交之前使用，可以将修改操作回滚掉（只有batch中包含修改操作时才能回滚），提交之后使用才有效.
   - 回滚的是内存镜像.
close:真正回收session的内存空间（包括缓存、数据库内存镜像）.

<br><br>


sqlsessionfactory = 数据库镜像，和数据库中的真是内容自动同步，同步时机由jdbc自己控制.
   - 不能说是数据库镜像，而就是数据库，session才是真正的镜像.
   - 数据库中的内容会时时同步到session事务镜像上.

autoCommit为true时相当于每一条语句都是一个事务.
commit = 提交到factory总镜像.
select、delete、update、insert作用的全部都是session二层镜像.
如果在其它线程中更新了factory镜像，那么factory镜像中的内容会立马向上同步到各个session二层镜像.
session是第二层镜像（称为事务镜像），factory之上的局部镜像，是事务级别的镜像，不影响facotry镜像.
   - factory可以看做真正的数据库.
  delete
  不提交 (此时在)
  select -> null pointer

### 二、以及缓存：sqlSession级别   是指select的结果缓存起来.
> 脏读的范围是内存镜像.

1. session脏读必然导致缓存清空.
2. session重连必导致缓存清空.
