# PO（持久化对象）

<br><br>

## 目录
1. [PO规范](#一po规范)
2. [PO的状态](#二po的状态)
3. [瞬态到持久化态](#三瞬态到持久化态insert-)
4. [从数据库中加载PO](#四从数据库中加载poselect出一条记录到程序中-)
5. [持久化态下修改PO](#五持久化态下修改poupdate-)
6. [脱管状态下修改PO](#六脱管状态下修改po也是update-)
7. [删除PO](#七删除podelete-)

<br><br>

### 一、PO规范：[·](#目录)
1. 必须提供一个无参构造器，并且所有构造器的访问控制符要**大于等于包访问权限**.
  - 这是为了Hibernate在运行时生成代理.
2. 不要将PO定义成final类：同样是为了生成代理，代理时需要自举生成PO的子类，final后则无法做出这一动作.
  - **代理** 是Hibernate**非常重要**的功能，尽量不要和该该功能背道而驰.
2. 最好提供一个标识属性：
  - 就是专门提供一个数据域来映射数据表的主键，那么该数据域就是PO的标识属性.
  - Hibernate不推荐数据表中提供具有实际意义（物理意义）的主键
    - 比如用Student ID作为主键的话，Student ID是有实际意义的，即学生ID.
    - 而是推荐使用不具有任何实际意义的逻辑主键，比如就用一个自增长的int来作为主键，或者是一个SHA1编码作为主键，只要能唯一表示该记录即可.
    - 那既然Student ID已经可以唯一表示一条记录了，为什么还要再多弄一个“逻辑主键”呢？这不是数据冗余吗？
      - 其实不然，如果使用多列作为联合主键，则需要再从表中增加多个列，这比只增加一个“逻辑主键”冗余度大得很多很多！这样看来，“逻辑主键”并不是那么冗余了！
  - 因此，该标识属性代表的只是逻辑主键，无需是一个有意义的名称，名称一般可以随意取.
  - 该属性的类型可以是任何Java基本类型、基本类型的包装器类型、String、Date等Java基本Util类型.
    - **由于基本类型使用不便，还是推荐使用包装器类型.**
3. 为所有属性提供getter、setter、is（即boolean isFoo();，但通常情况下is根据需要决定开不开发）.
4. 实现equals、hashCode：
  - 考虑到有时候会将PO放入Set中.
  - 有时候需要知道两个PO是否对应数据表中的同一条记录.
  - 有时可能会重用托管的PO.
  - **重新强调equals、hashCode、comparable、comparator的作用**:
    1. 前三个应该同生死，即逻辑结果应该保持一致.
    2. equals、hashCode共同决定元素在Java容器中的位置.
    3. comparable用于自然排序，就是元素在大自然中的天然的从小到大的顺序，比如整数中1就是比2小.
    4. comparator用于业务逻辑排序，比如有时候业务要求需要按照ID从大到小排序，有时候需要按照名称从大到小排序，而如果名称相同再按照ID排序等.

<br><br>

### 二、PO的状态：[·](#目录)
1. 瞬态：刚new出来并没有与任何Session关联.
  - 对瞬态对象的任何操作都不会持久化到数据库.
2. 持久化态：
  - 和Session关联了，这就意味着已经被Hibernate追踪.
  - 并且对应到数据表中的某行数据（某条记录）.
  - **最重要的就是拥有了一个持久化标识（identifier）.**
3. 托管：
  - 刚刚关联的又和Session断开了（关闭了Session）.
  - 托管状态下也同样会被Hibernate追踪，期间对PO的操作虽然不会被写入数据库但是会被记录到Hibernate缓存中.
  - 如果重新连接Session，则又可以从脱管状态回到持久化状态，这个时候提交脱管状态下的操作写入数据库.

<br><br>

### 三、瞬态到持久化态：insert [·](#目录)

- 瞬态是一种**从无到有**的过程，毕竟是new出来的嘛！而同时PO又是数据表中记录的化身，这就意味着新创建了一条新的数据记录，如果将该PO转入持久化态就意味着往数据表里插入了一条记录.
  - **因此，瞬态到持久化态底层是一个SQL的insert语句.**
- 标识属性的值自动生成：
  1. 在hbm文件中用id标签指定了PO中的哪个属性是标识属性，并且还可以用id标签的子标签generator指定主键（即标识属性的值）的生成策略，这样就可以在瞬态转入持久化态时让generator自动生成主键值（为标识属性赋值），这就无需自己手动调用setter为PO的标识属性赋值了.
  2. 但如果hbm中定义了PO的标识属性是assigned类型的（即必须手动赋值不能由generator自动赋值）或者是复合主键类型的，那么在转入持久化态之前必须自己手动为标识属性赋值.

**瞬态到持久化态的方法**：由Session提供（都是Session的对象方法）

1. Serializable save(Object po[, Object pk]);
2. void persist(Object po[, Object pk]);


- 说明：
  1. 都是将瞬态的PO转成持久态.
  2. 底层的操作就是将PO对应是数据记录insert到对应的数据表中.
  3. 如果没有指定pk值（即Primary Key，主键值），则由identifier的generator自动生成，如果指定了就相当于自己手动赋予一个主键值（标识属性值）.


- save和persist的区别：
  1. save调用后会立即生成insert语句，并立即执行insert操作将记录插入到数据表中，因此记录的各个列值必须当场确定，因此pk值必须立即确定，所以该方法同时也会返回pk值（必须是一个可串行化的值）.
  2. persist是lazy风格的（延迟加载），即调用后**不会立即生成insert语句并执行**，会一直等到完整的事务流全部被提交后再**一次性生成SQL语句并执行**，只有在事务提交时才会生成pk值（如果没有调用指定pk值版的重载persist）.
    - 这点很重要，这就允许在Session中**封装一个很长的会话流**，这要比一条一条执行SQL语句效率高很多，通常情况下persist使用更多.
    - save是为了照顾JPA用户的习惯，persist才是Hibernate自己的东西.

<br><br>

### 四、从数据库中加载PO：select出一条记录到程序中 [·](#目录)
- 就是原来程序中不存在PO，然后从数据库中select出一条记录到程序中成为PO.
- 必须在Session打开时加载，加载到程序中后该PO必然是出于持久化态的（因为在诞生时就是和数据库中的记录关联着的）.


- **加载方法**：调用Session的load或者get方法
  1. Object load(Class poClazz, Serializable pk[, LockOptions lockOptions]);
  2. Object get(Class poClazz, Serializable pk[, LockOptions lockOptions]);


  - 加载的时候还可以加上锁选项lockOptions，有些记录加载后只是观察（只读），有些加载之后就是为了修改，考虑到线程安全可以加锁.
    - lockOptions都是LockOptions中定义的静态对象有：NONE（不加锁，默认就是这种）、READ（只读）、UPGRADE（修改）.


- 说明：
  1. 都是通过主键值pk查找到目标数据记录，然后将记录加载到程序中，用PO的Java类型将记录包装秤PO对象.
    - 例如：Person p = (Person)load(Person.class, new Integer(15)); // 假设主键值是15
    - 底层的SQL语句就是select * from xxx_table where pk_value=pk_param
  2. load和get的最大区别就跟save和persist一样，load是延迟加载，而get是立即加载（调用后立马生成SQL语句并执行）.
  3. 其它区别：
    - load当数据库无该记录时会抛出异常，get则是返回null.
    - 还是延迟加载方面：如果加载的记录有关联对象（在其它表中有对应的外键），get会将所有关联记录也一并加载，而load并不会立即加载数据，而是先生成一个空的代理对象（即PO的化身，只不过数据域全空），直到最后事务提交时才会将代理对象替换成真实的PO并执行加载动作.
      - 可以看到代理的目的就是加大Hibernate操作数据库的效率！！


- **加载后得到PO就直接处于持久化态了，对其进行的操作都会记录到数据库中.**

> *可以通过设置不用立即执行，而是先缓存起来最后一并执行，但是这些操作肯定都会被记录缓存起来的*

<br><br>

### 五、持久化态下修改PO：update [·](#目录)
- 对持久化态下的PO作的任何操作（修改）都会被转换成update语句并保存起来，最后在Session flush之后统一交给数据库执行.
- 底层的SQL语句：update ... where pk=PO标识属性的值
  - 典型的就是调用setter修改PO属性.

<br><br>

### 六、脱管状态下修改PO：也是update [·](#目录)
- 将托管对象重新持久化调用的是Session的update、updateOrSave或者merge三个方法.
- 在托管期间可以调用PO的各种对PO进行修改，但其处于托管状态，操作无法写入数据库，但是全都会被Hibernate记录下来，待重新回到持久化态后再将托管状态下的修改写入数据库.


**三方法说明**：

1. update：
  - public void update(Object detachedPO) throws HibernateException;
  - 调用该方法前提是PO必须是唯一托管状态
    1. 托管状态：如果update一个本身就是持久化态的PO会直接抛出异常.
    2. 唯一：如果此时还有其它处于持久化状态的PO和该PO的identifier相同（即主键相同）也会抛出异常.
  - 该方法底层执行的SQL语句是update.
2. updateOrSave：
  - public void saveOrUpdate(Object detachedOrNewPO) throws HibernateException;
  - 如果你update的时候不能确定（如果你在开发别人的代码）该PO之前到底是托管的PO还是瞬态的PO的话就调用该方法：
    - 因为update一个非托管PO会抛出异常.
    - 该方法会自动检测该PO的状态，如果是瞬态就调用save，否则调用update.
3. merge：
  - public Object merge(Object object) throws HibernateException;
  - 该方法是将object的数据复制（update）到相同identifier的PO中以完成对数据库中记录的更新.
  - 如果该identifier的记录还没有关联的PO就待更新记录后从数据库中load出来一个关联PO作为merge的返回值.
  - 也就是说该方法并不改变object的状态，object完全可以不是PO，只是一个保存数据的Bean而已.


示例：
```java
News n = sess.load(...); // 加载持久化一个PO
sess.close(); // 关闭Session使n处于托管状态
n.setMessage(newMessage); // 托管期间进行修改
Session another_sess = ...; // 重新开一个Session
another_sess.update(n); // 重新持久化并保存修改记录到数据路中
```

### 七、删除PO：delete [·](#目录)
- 调用Session的delete方法将PO删去：void delete(Object po);
- 因为是Session的对象方法，很显然是要在PO处于持久化状态下才能调用delete，否则会抛出异常.
- **该方法会直接将数据库中的数据记录删除！**，底层执行的SQL语句就是delete.

示例：
```java
News n = Session.load(News.class, new Integer(pk));
sess.delete(n);
```
