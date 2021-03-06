# 额外属性 & 返回结果（resultType和resultMap的关系）
> select语句返回的结果可能为单个记录也可能或多个记录（记录集合）.
>
> - `<select>`标签的`resultType`和`resultMap`就用来声明返回结果的数据类型.
>    - 但要注意：
>       1. 如果返回的是记录集合，那它们只是的集合中元素的Java类型.
>       2. 如果是单值，那就不用说了，就是那个唯一的元素的Java类型.
>
>> - MyBatis规定，如果select返回的是多个记录的集合，则一定使用 `java.util.List` 保存.
>>
>> <br>
>>
> `<resultType>`和`<resultMap>`都使用了`构造注入`和`设值注入`的方式生成`结果对象`.
>
> - 但不过一定是 **先** 使用`构造注入`，**再** 使用`设值注入`的.
>    1. `构造注入`：将返回记录的列值作为构造器的参数初步构造出一个对象.
>    2. `设值注入`：在构造完毕后，进一步调用`setter`将记录的列值注入对象中.

<br><br>

## 目录

1. [`<select>`的额外属性](#一select的额外属性)
2. [`resultType`和`resultMap`之间的关系](#二resulttype和resultmap之间的关系)
3. [`resultType`的具体用法](#三resulttype的具体用法)
4. [使用`<select-resultMap>`和`<resultMap>`完成简单的`列-setter`手动映射](#四使用select-resultmap和resultmap完成简单的列-setter手动映射)
5. [返回结果的第一步永远是构造注入：默认的构造注入方式](#五返回结果的第一步永远是构造注入默认的构造注入方式--)
6. [自己决定构造注入使用的构造器版本：<constructor>](#六自己决定构造注入使用的构造器版本constructor--)

<br><br>

### 一、`<select>`的额外属性：[·](#目录)

<br>

| 属性 | 值 | 说明 |
| --- | --- | --- |
| `useCache` | 默认为`true` | `1. 开启后直接结果被二级缓存.`<br>`2. 一般不需要修改.` |
| `fetchSize` | 默认为依赖底层驱动 | `批量返回的记录行数.` |
| `resultSets` | - | - |
| `resultSetType` | `FORWARD_ONLY`<br>`SCROLL_SENSITIVE`<br>`SCROLL_INSENSITIVE`<br>默认依赖驱动 | - |
| `resultOrdered` | - | - |
| `databaseId` | - | - |

<br><br>

### 二、`resultType`和`resultMap`之间的关系：[·](#目录)
> 两者都是 `<select>` 标签的属性，完成的任务有2个：
>
> 1. 声明select语句返回结果的Java类型.
>    - 如果返回的是集合，那声明的是 **集合元素的Java类型**.
> 2. 指定返回的 **记录的列** 和 **POJO的setter** 之间的映射关系.
>    - 只不过前者是自动映射而后者是手动映射.
>       - 在这种对应关系非常复杂的情况下（**关联映射**，某列外联了其它表之类的）就必须用手动映射了.

<br>

**1.&nbsp; 首先看select语句返回的结果是什么：**

| 返回结果 | 对应的Java类型 | 由于POJO和Map的等价关系 |
| --- | --- | --- |
| 单个记录 | 1个POJO对象 | 1个`Map<String, Object>` |
| 多个记录 | 1个POJO对象的List（`List<POJO>`）| 1个`List<Map<String, Object>>`
| 空 | null | - |

<br>

**2.&nbsp; 对比从Java传参到SQL的过程，select结果返回给Java应该完成哪些步骤：**

1. Java传参到SQL：
   - 通过OGNL的`.`解析POJO的getter填入SQL语句的相应位置.
2. select返回给Java：
   - 必须要将一条条记录包装成一个个POJO返回给Java，因此，必须要解决：
      - **记录的每列对应POJO的哪些setter.**

- 总结：
   1. Java -> SQL：确定getter和OGNL表达式之间的对应关系.
   2. select -> Java：确定列和setter之间的对应关系.

<br>

**3.&nbsp; `resultType`&`resultMap`解决的就是`确定列和setter之间的对应关系`：**

> 只不过`resultType`是自动对应，而`resultMap`是手动对应.

1. 自动映射的`resultType`：
   - 规则很简单：自然映射（同名映射），即列名和同名的setter相对应.
      - 例如：列名`id`就对应到POJO的`setId`上.
2. 手动映射的`resultMap`：
   - 单独定义一个`<resultMap>`标签，定义一个数据记录和POJO的setter之间的对应关系.
   - 然后两一个`<select>`标签的`resultMap`属性引用它的`id`值.
      - 不仅可以用`<result property="..." column="..." />`标签手动完成列和setter的映射.
      - 还可以使用`<association>`、`<collection>`等标签定义更加复杂的映射（**具有外键的关联映射**）.

<br><br>

### 三、`resultType`的具体用法：[·](#目录)

<br>

**1.&nbsp; mapper-xml：**

```Java
User|Map<String, Object> selectOneUserById(Integer id);
List<User>|List<Map<String, Object>> selectAllUsers();
```

```XML
<!-- 1. 返回单个记录 -->
<select id="selectOneUserById" resultType="user|map">
    SELECT * from tb_user where id=#{id}
</select>

<!-- 2. 返回多个记录的List集合 -->
<select id="selectAllUsers" resultType="user|map">
    SELECT * from tb_user
</select>
```

<br>

**2.&nbsp; 注解映射：**

```Java
@Select("SELECT * FROM tb_user WHERE id=#{id}")
User|Map<String, Object> selectOneUserById(Integer id);

@Select("SELECT * FROM tb_user")
List<User>|List<Map<String, Object>> selectAllUsers();
```

<br><br>

### 四、使用`<select-resultMap>`和`<resultMap>`完成简单的`列-setter`手动映射：[·](#目录)
> 复杂的关联映射在后续章节中详解.

<br>

**1.&nbsp; mapper-xml：**

```Java
List<User> selectResultMapTest();
```

```XML
<select id="selectResultMapTest" resultMap="myResultMap" >
    <!-- 故意让返回的记录的列明和POJO的setter名不一样 -->
    SELECT
        id      as  res_id,
        name    as  res_name,
        sex     as  res_sex,
        age     as  res_age
    FROM
        tb_user
</select>

<resultMap id="myResultMap" type="user">
    <!-- 手动映射列和setter -->
    <!-- property是setter名（也是Map.key），column是列名 -->

    <!-- 1. 普通列的映射 -->
    <result property="name" column="res_id" />
    <result property="sex" column="res_sex" />
    <result property="age" column="res_age" />

    <!-- 2. 特殊的，专用于主键列的映射以提高效率 -->
    <id property="id" column="res_id" />
</resultMap>
```

<br>

**2.&nbsp; 注解映射：`@Results-@Result`**

```Java
@Select({
"   SELECT                      ",
"       id     AS  res_id,      ",
"       name   AS  res_name,    ",
"       sex    AS  res_sex,     ",
"       age    AS  res_age      ",
"   FROM                        ",
"       tb_user                 "})
@Results(
    id = "myResultMap",  // 即 <resultMap-id>，在注解映射中可以省略.
        // 省略后，'value = '也可以省略，直接 {...} 就代表value

    value = { // value指定column和setter之间的映射（可以是关联映射）
        // 1. 使用id选项指定主键映射
        @Result(property = "id", column = "res_id", id = true),
        // 2. 普通列的映射
        @Result(property = "name", column = "res_name"),
        @Result(property = "sex", column = "res_sex"),
        @Result(property = "age", column = "res_age")
    }
)
List<User> selectResultMapTest();
```

<br><br>

### 五、返回结果的第一步永远是`构造注入`：默认的构造注入方式  [·](#目录)
> 不管是使用`resultType`还是`resultMap`返回结果，第一步都是调用结果类型的构造器先构造出对象，再根据`<id>、<result>`等调用相应的`setter`进行 **设值注入**.

<br>

- 默认的构造注入方式：
   1. 自动的应用场景：
      1. 使用`resultType`返回结果.
      2. 使用`resultMap`返回结果，但又没有使用`<constructor>`标签指定构造器.
   2. 默认使用的构造器版本：
      - 根据`select语句`的筛选列决定，如果没有对应的版本就调用`无参构造器`构造（再调用`setter`设值）.
      - 例如：
         - `SELECT * FROM tb_user`返回的列有`id, name, sex, age`.
         - 那么MyBatis就会自动寻找`Integer, String, String, Integer`版本的构造器来构造结果对象.
            - 如果找不到就再尝试调用`无参构造器`构造，再找不到就直接报错了！！（**反射失败**）
   3. 因此使用MyBatis框架时最好给各个`domain`框架提供足够完备的构造器，并至少应该提供一个`无参构造器`.

<br>

- 示例：

> 对于下面用到关联映射的情景，由于`列：card_id(int) 外联 tb_card`.
>
>> - 而`SELECT *`的结果是`int str str int int(card_id)`.
>>    - 并非关联映射后的`int str str int Card`.
>>       - 只看select语句的`SELECT`和`FROM`之间的内容.
>>
>>> - 因此就会先找`int str str int int`版本的构造器，找不到就再找`无参构造器`.
>>>    - 再找不到就报错，提示需要一个`int str str int int`版本的构造器.
>>> - 因此这里至少要提供一个`无参构造器`，再根据情况决定是否需要提供`int str str int int`版本的构造器.

```XML
<select id="selectPersonById" resultMap="personResultMap">
    SELECT * FROM tb_person WHERE id = #{id}
</select>

<resultMap id="personResultMap" type="person">
    <id property="id" column="id" />
    <result property="name" column="name" />
    <result property="sex" column="sex" />
    <result property="age" column="age" />
    <association
        property="card" column="card_id"
        select="org.my.bat.batfirst.dao.CardDao.selectCardById"
    />
</resultMap>
```

<br><br>

### 六、自己决定构造注入使用的构造器版本：`<constructor>`  [·](#目录)
> 只能在`<resultMap>`、`<association>`、`<collection>`中使用.
>
>> - 一般 `构造注入`和`设值注入` 两者不要有重叠，重叠部分没什么意义.

<br>

**1.&nbsp; mapper-xml：**

<br>

- 使用`<constructor>`标签调用指定版本的构造器注入结果对象：
   - 该标签要放在`<resultMap>`、`<association>`、`<collection>`的最前面使用.

```XML
<resultMap id="MyResultMap" type="user">
    <!-- 1. 构造注入部分 -->
    <constructor>
        <!-- 根据 javaType 的顺序决定调用哪个版本的构造器 -->
            <!-- column只是指定映射记录的哪列  -->
            <idArg column="id" javaType="int" />        <!-- idArg表示主键列的映射，提高效率 -->
            <arg column="name" javaType="string" />     <!-- arg表示普通列的映射 -->
    </constructor>

    <!-- 2. 设值注入部分 -->
    <result property="sex" column="sex" />  <!-- 构造注入和设值注入不重叠 -->
    <result property="age" column="age" />
</resultMap>
```

<br>

**2.&nbsp; 注解映射：**

1. `@ConstructorArgs`等同于`<constructor>`.
2. `@Arg`子注解等同于`<idArg>`和`<arg>`.
   - 并且用法完全一样.

```Java
@Select("SELECT * FROM tb_user WHERE id = #{id}")
@ConstructorArgs({ // 1. 构造注入
    @Arg(column = "id", javaType = Integer.class, id = true), // id=true 等同于 <idArg>
    @Arg(column = "sex", javaType = String.class)
})
@Results({ // 2. 设值注入
    @Result(property = "name", column = "name"),
    @Result(property = "age", column = "age")
})
User selectByIdCons(Integer id);
```

<br>

**3.&nbsp; 构造注入和设值注入的抉择：**

1. **设值注入为主，构造注入为辅.**
2. 使用设值注入就是为了避免设计繁杂而数量庞大的重载构造器.
3. 因此推荐：只提供一个`无参构造器`，结果返回全部依赖`设值注入`.
