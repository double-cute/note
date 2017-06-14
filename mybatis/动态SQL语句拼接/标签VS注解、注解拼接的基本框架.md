# 标签VS注解、注解拼接的基本框架
> 使用XML标签拼接SQL语句格式更加自由，就仿佛在直接编写SQL脚本.
>
> - 但如果SQL语句过于复杂还是应该使用XML标签拼接，更加易读可维护（更接近脚本编写，不容易出错）.

<br><br>

## 目录

1. [`注解拼接`以及`标签拼接`的各自特色以及优劣]()
2. [注解拼接的基本框架]()
3. [`org.apache.ibatis.jdbc.SQL`简介]()

<br><br>

### 一、`注解拼接`以及`标签拼接`的各自特色以及优劣：[·](#目录)

<br>

**1.&nbsp; 注解拼接：**

- 特点和优势：
   1. 可以依附Java丰富的语法和强大的功能，很多逻辑实现起来更加符合面向对象的编程风格.
   2. 更加安全，可以依赖Java的语法检查、类型安全检查.
      - 相比于`标签拼接`，后者很多条件判断都是以`OGNL`表达式的形式写在纯字符串中的，存在一定的安全风险.
   3. 但Java缺少对SQL语句本身的包装，所以注解主要提供的是对SQL语句本身的包装.
- 劣势：
   1. Java本身缺乏对SQL语言的包装，想要让Java完全支持SQL的所有语法几乎要再实现一门新的语言.
      - **特别是**，在SQL语句中使用传入的参数还是须要在Java字符串中使用`OGNL`表达式.
         - 因此编程语言的味道也并不是那么纯粹.
   2. 也因为此，MyBatis提供的`注解拼接`中对SQL功能的包装也非常有限.
   3. 要完成一些特别复杂的功能还是要依赖传统的字符串拼接方式：
      1. Java中字符串不能跨行，不能像编写SQL脚本一样在Java字符串中编写SQL语句.
      2. `注解拼接`提供的一些功能不太完备，诸如`LEFT_OUTER_JOIN`等方法，没有提供配套的`ON`条件拼接方法，只能把`ON`条件以纯字符串的形式写在`JOIN`系列方法的参数中：
         - `LEFTER_OUTER_JOIN("tb_user u ON c.user_id = u.id");`

<br>

**2.&nbsp; 标签拼接：**

- 特点和优势：本身就可以 **自由编写SQL脚本**，非常方便直观.
   - 对于特别大型、复杂的SQL语句可以平铺直叙.
- 劣势：
   1. 缺乏条件判断等编程语言具有的功能，因此标签拼接最主要提供的就是这些功能.
      - 但这些功能使用起来比编程语言麻烦很多，有很多属性需要设置.
   2. 没有编程语言的安全检查.

<br><br>

### 二、注解拼接的基本框架：[·](#目录)

<br>

1. 定义Dao接口方法时不使用`@Select`、`@Insert`、`@Update`、`@Delete`修饰方法.
   - 而是使用`@SelectProvider`、`@InsertProvider`、`@UpdateProvider`、`@DeleteProvider`修饰接口方法.
2. 在`@XxxProvider`中指定一个 **SQL语句拼接工具类** 的 **某个拼接方法** 为该Dao接口方法生成SQL语句.
3. 在工具类的拼接方法中使用`org.apache.ibatis.jdbc.SQL`工具类按照业务逻辑进行SQL语句拼接.

<br>

- 总体编程框架应该为：
   1. 一个domain对应一个dao.
   2. 一个dao对应一个sqlProvider.
   3. 在sqlProvider中为每一个dao中的接口方法提供一个对应的SQL语句拼接方法（返回拼接出来的SQL语句）.

<br>

- 示例：

```Java
public interface UserDao {
    // 指定UserDaoSqlProvider类的selectById方法为它生成SQL语句
    @SelectProvider(type=UserDaoSqlProvider.class, method="selectById")
    User selectOneById(Integer id);

    // 同理
    @InsertProvider(type=UserDaoSqlProvider.class, method="insertOne")
    int insertOne(User user);
}

public class UserDaoSqlProvider {
    // 参数要和代理的dao接口方法一致！
    public String selectById(Integer id) {
        return new SQL() {{
            SELECT("*");
            FROM("tb_user");
            WHERE("id = #{id}");
        }}.toString();
    }

    public String insertOne(User user) {
        return new SQL() {{
            INSERT_INTO("tb_user");
            VALUES("name", "#{name}");
            VALUES("sex", "#{sex}");
            VALUES("age", "#{age}");
        }}.toString();
    }
}
```

<br><br>

### 三、`org.apache.ibatis.jdbc.SQL`简介：[·](#目录)
> 只要继承该类就拥有拼接SQL语句的能力.
>
>> - 但通常都是使用匿名类临时使用它拼接一个SQL语句.

<br>

1. 该类包含一个`SQLStatement sql = new SQLStatement();`对象成员.
2. 还有若干拼接该`sql`对象成员的 **对象方法**，诸如`SELECT`、`WHERE`、`OR`等.
3. 直接利用对象成员初始化块，并在其中利用这些拼接方法拼接`sql`即可.
4. 拼接完成后，利用`SQL`的`.toString`方法返回SQL语句的字符串即可.

- 例如：

```Java
public String insertOne(User user) {
    return new SQL() {{
        INSERT_INTO("tb_user");
        VALUES("name", "#{name}");
        VALUES("sex", "#{sex}");
        VALUES("age", "#{age}");
    }}.toString();
}
```
