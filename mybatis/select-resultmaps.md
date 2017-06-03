
- 类型别名：别名可以在parameterType、resultType中使用（简话权限定类名）s
   1. mybatis-config.xml中配置：


- 这是自定义别名。
   - parameterType、resultType必须都是完全的Java类型.
      - 但权限定太长，写起来捉急，所以MyBatis提供了一发 类型别名的服务.
   - 有内置别名 & 自定义别名.
      - 自定义如下，内置的在TypeAliasRegistry中定义.


      - 自定义的标签其实底层调用了registerAlias("string", String.class);方法进行注册.


```xml
<typeAliases>
        <!-- 全局暴力绑定 -->
        <typeAlias alias="fucker" type="org.my.bat.batfirst.domain.User" />
        <!-- 该包下的所有类的别名默认为 首字母小写的形式，例如User -> user -->
        <package name="org.my.bat.batfirst.domain" />
</typeAliases>
```
   2. 注解：

```Java
@Alias("fucker")  // 和typeAlias标签的效果一样
public class User {
  ...
}
```

- 这三种方法都成立. (typeAlias、注解同时有效，都会覆盖掉package，要用package就不能用其它两个！！)

- 第4中是 TypeAliasRegistry中定义的各种别名！！！都可以使用.

<br><br>

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
