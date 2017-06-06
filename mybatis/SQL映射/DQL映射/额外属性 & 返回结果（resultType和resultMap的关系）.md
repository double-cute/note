# 额外属性 & 返回结果（resultType和resultMap的关系）
> select语句返回的结果可能为单个记录也可能或多个记录（记录集合）.
>
> - `<select>`标签的`resultTyp`和`resultMap`就用来声明返回结果的数据类型.
>    - 但要注意：
>       1. 如果返回的是记录集合，那它们只是的集合中元素的Java类型.
>       2. 如果是单值，那就不用说了，就是那个唯一的元素的Java类型.

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

### 、resultType：必须写，resultType-resultMap两者择其一，不能同时写也不能都没有，否则会报错！！！

1. resultType & resultMap如果返回的是集合，那么其中指定的类型必须是 **集合元素的类型** 而不是集合本身的类型.

2. 对于返回集合的SQL语句,如 select * from tb_user；
   - resultType = “map”，则接受返回类型为 List<Map<String, Object>> ，每条记录一个map，记录为<列名, 列值>
   - 建议resultType = "POJO",那么返回类型就为 List<POJO>了，例如List<User>


### resultMap:

- 超级强大：使用resultMap一般用来映射POJO元素！不用map.

```XML
<resultMap id="resultMap的唯一ID，例如，myResultType"
  type="返回元素（同时也是返回list（集合）中的元素的Java类型（权限定或者alias）">
  <!-- 最简单的功能: 解决返回记录的列名和POJO的getter名不一致的问题 -->
  <result property="getter名" column="对应的列名" /> <!-- 完成列到getter的映射 -->
  <!-- 实际可以使用select xxx AS xxx本身解决的，用resultMap有点儿小题大做 -->
  <id property="getter名" column="对应的列名" /> <!-- 作用相同，只不过用于指定主键，提高效率 -->
</resultMap>
```
