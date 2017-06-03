

- sql标签：即一个可以被其它多个select、insert、update、delete、sql复用（宏替换）的SQL语句片段.
   - 用include refid=""应用sql片段，用property标签传入OGNL表达式的值.

```XML
<sql id="xxx">${OGNL表达式}...各种${arg1}${arg2}${arg3}</sql>

<!-- 使用时用include标签在响应的位置进行宏替换 -->
<select ...>
  select
    <include refid="sql-id">
      <property name="arg1" value="v1" />
      <property name="arg2" value="v2" />
      <property name="arg3" value="v3" />
    </include>
  from ...
    ...
</select>
```

- 也支持嵌套替换：
   - 一个sql的property作为另一个sql的id

```XML
<sql id="test">${prefix}_table</sql>

<sql id="injectSqlId">
  from
    <include refid="${inject_id}" />
</sql>

<select id="select">
  select field1, field2
  <include refid="injectSqlId">
    <property name="inject_id" value="test" />
    <!-- 这步之后变成了 from <include refid="test"> -->
    <!-- 即 from ${prefix}_table 了 -->
    <property name="prefix" value="nor" />
  </include>
</select>
```
select、insert、update、delete公用的属性

| 共有属性 | 说明 |
| --- | --- |
| id | SQL语句的ID，同时也是mapper方法名 |
| parameterType | SQL语句参数（同时也是方法参数）的**Java类型** |
| flushCache | 默认为false，如果为true则SQL语句被调用就会清空本地缓存和二级缓存 |
| statementType | STATEMENT、PREPARED、CALLABLE，分别对应Statement、PreparedStatement、CallableStatement，默认为PREPARED |
| timeout | 单位是秒，表示执行多久还没就过就抛出异常，默认为依赖驱动 |



- update、delete没有额外的属性

- insert的额外属性：以下两者配合使用
   - 使用后可以不传入主键列的值.
   - 自增长主键需要数据库支持（MySQL、SQL Server支持）

| 属性 | 说明 |
| --- | --- |
| useGeneratedKeys="true" | 使用自增长的**主键** |
| keyProperty="主键列对应的POJO的getter名" | "id"（调用getId()） |

- 多行insert（MySQL支持）：
   - Collection: 对应的接口层方法参数
      1. "list": List<T>
      2. "array": T[]
      3. "some_str": Map<String, Object>中的{"some_str", List<T>|T[] eles}

```xml
<insert id="insertUsers" useGeneratedKeys="true" keyProperty="id">
        INSERT into
            tb_user
                (name, sex, age)
        values
            <foreach collection="list" item="item" separator=",">
                (#{item.name}, #{item.sex}, #{item.age})
            </foreach>
</insert>
```



- 动态SQL
- 注解
