

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

- parameterType：超级灵活，待会儿细讲  
   1. 要么不写，如果写的话必须要写 **全限定类名**.
   2. 如果不写，则会使用MyBatis的TypeHandler进行自动推断.
      - 根据SQL语句中的#{XXX}占位符进行判断.
         1. 内部支持的内部简化Java类型：如int表示Integer等.
         2. 其余必须使用全限定类名.
         3. 如果是其它Java类则该Java类必须是POJO.
      - 占位符解析规则：#{id}则会查找名为id的getter取值来替换占位符.
         - 如果是内部类，则#{id}则直接表示该类的对象的值.
            1. #{xxx}：getXxx替换SQL的?占位符（根据类型来，int、double等直接替换，String要外加''再替换.
            2. ${xxx}：getXxx的纯字符串替换，直接纯字符串宏替换，容易导致SQL注入攻击.
               - 常用于schema也是变量的情景，例如：insert into ${schemaName}.tb_user values(xxx, xxx, xxx);
               - 这里不能用#{schemaName}，因为#{}用于?的SQL占位符，SQL语法规定只有values部分才能?占位符.
      - 最多只支持一个parameterType属性，不持之多于1个的parameterType属性：
         - 超过1个参数MyBatis就强制要求你将它们包装成POJO传入.
         - session的select、insert、update、delete系列方法都最多支持持一个Object类型的参数.
            - 实为POJO类.

OGNL表达式：@Param("schema") String schema, @Param("user") User user -> ${user.name} #{user.age} ${schema}

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

- select

| useCache | select中默认为true，其余默认为false，true表示该SQL语句的结果将会被二级缓存 |





- parameterType
- 动态SQL
- 注解
