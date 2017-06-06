# DML映射
> 即insert、update、delete语句的映射.
>
>> - 提示：
>>    1. 使用注解映射需要直接将SQL语句写在Java代码里.
>>    2. 由于Java代码里不允许字符串常量分行编写，因此SQL语句不宜写得过长.
>>    3. 由于受到此限制，还是建议简单语句使用注解映射，复杂语句还是写在mapper-xml为好.

<br><br>

## 目录

1. [`<update>`、`<delete>`没有任何额外的属性](#一updatedelete没有任何额外的属性)
2. [`<insert>`](#二insert)

<br><br>

### 一、`<update>`、`<delete>`没有任何额外的属性：[·](#目录)

<br>

**1.&nbsp; mapper-xml：**

```Java
int updateOneUser(User user);
int deleteOneUserById(Integer id);
```

```XML
<!-- update -->
    <!-- 返回的是int，表示真正修改的记录数量 -->
<update id="updateOneUser">
    UPDATE
        tb_user
    set
        name = #{name},
        sex = #{sex},
        age = #{age}
    where
        id = #{id}
</update>

<!-- delete -->
    <!-- 返回的是int，表示实际删除的记录数量 -->
<delete id="deleteOneUserById">
    DELETE
    from
        tb_user
    where
        id = #{id}
</delete>
```

<br>

**2.&nbsp; 注解映射：`@Update`、`@Delete`**

```Java
@Update("UPDATE tb_user SET name=#{name}, sex=#{sex}, age=#{age} WHERE id=#{id}")
int updateOneUser(User user);

@Delete("DELETE FROM tb_user WHERE id=#{id}")
int deleteOneUserById(Integer id);
```

<br><br>

### 二、`<insert>`：[·](#目录)

<br>

**1.&nbsp; mapper-xml：**

<br>

- 额外属性：**插入时指定一个列为自增长的主键**
   - 这样在insert时可以不指定主键列的值.
   - 自增长主键需要数据库支持.
      - MySQL、SQL Server等支持.

| 属性 | 说明 |
| --- | --- |
| useGeneratedKeys="true" | `开启自增长的主键insert模式` |
| keyProperty="主键列对应的POJO的getter名" | `指定哪一列是自增长主键列<br>`"id"（调用getId()）` |

<br>

- **multi-insert**：即一条insert语句插入多个记录
   - 需要数据库在语法层面上的支持. （**MySQL支持**）
   - 方法是通过集合传入POJO进行insert.
      - 然后使用`<foreach>`标签进行拼接.

<br>

- 示例：

```Java
int insertUsers(List<User>|User[] col);
```

```xml
<!-- 返回int，表示实际插入的记录数量 -->
<insert id="insertUsers" useGeneratedKeys="true" keyProperty="id">
    INSERT
    into
        tb_user
            (name, sex, age)
    values
        <foreach collection="list|array|other" item="item" separator=",">
            (#{item.name}, #{item.sex}, #{item.age})
        </foreach>
</insert>
```

<br>

**2.&nbsp; 注解映射：`@Insert`、`@Options`（传入额外参数）**

- 像`id`、`parameterType`以外的属性可以通过`@Options`注解来指定.

```Java
@Insert("INSERT INTO tb_user(name, sex, age) VALUES(#{name}, #{sex}, #{age})")
@Options(useGeneratedKeys=true, keyProperty="id") // xml中为"true"，注解为true，要注意了！！
int insertOneUserWithoutId(User user);  // insert时user.id将被忽略，交由数据库自增长
```
