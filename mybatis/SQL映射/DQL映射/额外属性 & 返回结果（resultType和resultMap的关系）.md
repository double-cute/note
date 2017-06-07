# 额外属性 & 返回结果（resultType和resultMap的关系）
> select语句返回的结果可能为单个记录也可能或多个记录（记录集合）.
>
> - `<select>`标签的`resultTyp`和`resultMap`就用来声明返回结果的数据类型.
>    - 但要注意：
>       1. 如果返回的是记录集合，那它们只是的集合中元素的Java类型.
>       2. 如果是单值，那就不用说了，就是那个唯一的元素的Java类型.
>
>> - MyBatis规定，如果select返回的是多个记录的集合，则一定使用 `java.util.List` 保存.

<br><br>

## 目录

1. []()
2. []()

<br><br>

### 一、`<select>`的额外属性：

<br>

| 属性 | 值 | 说明 |
| --- | --- | --- |
| `useCache` | 默认为`true` | `1. 开启后直接结果被二级缓存.`<br>`2. 一般不需要修改.` |


###、


- select

| useCache | select中默认为true，其余默认为false，true表示该SQL语句的结果将会被二级缓存 |


- resultType 和 resultMap不能同时使用：

<br><br>

### 二、`resultType`和`resultMap`之间的关系：
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

### 三、`resultType`的具体用法：

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

### 四、使用`<select-resultMap>`和`<resultMap>`完成简单的`列-setter`手动映射：
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
    <id property="getter名" column="对应的列名" />
</resultMap>
```

<br>

**2.&nbsp; 注解映射：`@Results-@Result`**

```Java
@Select("SELECT * FROM tb_user")
@Results(
    id = "myResultMap",  // 即 <resultMap-id>
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
