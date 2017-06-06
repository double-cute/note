# SQL语句传入参数（OGNL）& parameterType属性


<br><br>

## 目录

1. [SQL语句传入参数：使用OGNL栈中转](#一sql语句传入参数使用ognl栈中转--)
2. [SQL标签的parameterType属性详解](#二sql标签的parametertype属性详解)

<br><br>

### 一、SQL语句传入参数：使用OGNL栈中转  [·](#目录)
> 这里就不多说了，之前在 [类型处理器](../基本构架/类型别名%20%26%20类型处理器.md#三类型处理器) 的部分中讲解过了，以及 [Map和POJO为什么是等价的](../基本构架/类型别名%20%26%20类型处理器.md/#四map和pojo之间的类型等价关系)，这里就再提一下.
>
>> - 这里重点介绍一下 **如何在SQL标签中使用这些参数**. （当然是使用OGNL表达式咯）

<br>

1. Java数据传入SQL语句以及SQL结果传回Java程序都需要OGNL栈中转.
2. OGNL栈中每个元素都必须为Map.
   - 因为SQL（数据库端）的数据类型跟Java类型不是很兼容，必须用Map中转才万无一失.
3. 所以Map和POJO等价（`Map<String, Object> <==> POJO(getter_name, getter_return)`）
4. 所以OGNL是Java程序和SQL（数据库）之间数据交换的桥梁.

<br>

- 使用 **OGNL表达式** 在SQL标签中引用传入参数：
   1. 有两种形式：**SQL占位符引入** & **自由引入**
      1. SQL占位符引入：
         1. 符号：`#{ognl-expr}`
         2. 只能作为SQL的'?'的占位符.
            - 只能用于where-condition、insert-value、set-value.
            - 其它地方不能用（表名、模式名之类的）.
      2. 自由引入：
         1. 符号：`${ognl-expr}`
         2. 可以用在任何地方（可以作为SQL语句关键字的一部分）.
            - 即纯粹的宏替换.

<br>

- 所有可以用到OGNL表达式的地方：
   1. 标签内容：即SQL语句部分.
   2. 标签属性值：**使用时不需要`${}`和`#{}`包裹，直接写表达式即可.**
      - 例如：`<foreach collection="idList" ... />`中属性值`idList`其实是一个OGNL表达式.
         - 相当于标签内容的`#{idList}`.

<br>

- OGNL表达式的精髓：`.`的应用
   1. 由于OGNL栈中的元素必须为map，因此`.`运算符的含义是：
      - 左侧表示 `key:string`，右侧表示 `value`.
      - 例如：`#{a.b.c}`就是指`this_map["a"]["b"]["c"]`.
   2. 由于map和POJO是等价的，因此上例如果参数是POJO的话就是指：
      - `this_pojo.getA().getB().getC()`

<br><br>

### 二、SQL标签的`parameterType`属性详解：[·](#目录)
> 可以写也可以不写.

<br>

**1.&nbsp; 如果不写：**

- 默认根据 **Java端传入参数的Java类型** 调用相应的 **MyBatis-TypeHandler** 进行处理.
   - 根据传入参数类型OGNL表达式的意义主要分为3种类型：
      1. 单值型：如int、string等，`#{...}`就代表参数值本身.
      2. 复合型：如object（默认为POJO）以及map，`#{a.b.c}`用`.`运算符取getter或者key-value.
      3. 集合型：如list、array等，可以使用`<foreach>`标签迭代.

<br>

**2.&nbsp; 如果写：**

- 规矩是：**类型一致**
   - `parameterType`指定的Java类型必须和Java端（接口层）传入参数的Java类型严格相同，否则会发生运行时类型冲突！
      - 除了map和POJO是相互兼容的以外，其余必须严格相同.
      - 例如：
         1. 接口层传入`Integer`，`parameterType`声明的是`string`那就错了！
         2. 接口层传入的是`Map<String, Object>`，`parameterType`声明的是一个POJO，这样是对的，两者兼容.
- `parameterType`最多只能有1个，多余1个直接报错！

<br>

**3.&nbsp; 如何传入多参数：**

- `parameterType`可写可不写，写的时候必须和传入参数类型一致.

1. 通过集合（List、Array）传入：
   1. `parameterType`应该为`list`、`array`.
   2. 使用`<foreach>`标签迭代.
   3. 缺点是元素类型必须都相同.
2. 通过Map或POJO传入：
   1. 两者兼容，`parameterType`应该为`map`或`POJO`.
   2. `Map.key`、`POJO.getter_name`为参数名，`Map.value`、`POJO.getter_return`为参数值.
      - 因此可以通过OGNL的`.`运算符访问.
   3. 缺点：需要将多个参数包装成Map或POJO，多了包装的过程.
3. 接口层中通过`@Param`注解传入：
   - 例如：以下两者等价
      - 接口层传入参数：`@Param("id") Integer _id, @Param("name") String _name, @Param("user") User _user`
      - SQL中通过OGNL引入参数：`${id}`对应`_id`，`#{name}`对应`_name`，`#{user.age}`对应`_user.getAge()`
   - `parameterType`应该为`map`，是用Map包装的.

- **通常推荐注解传入，更加方便直观.**
