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
