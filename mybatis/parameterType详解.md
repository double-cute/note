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




<br><br>

### 1、不适用Param注解的情况下：

- 最多只能有一个parameterType属性.


1. 无parameterType：
   1. 如果传入的是 **单个** 原生整型：整型、浮点型（byte-long，float-double及其包装器类型）
      - 则SQL中只能有唯一的占位符${}/#{}，并直接解析.
   2. 其余的所有Java类型（包括String）都将被看做Object类型的POJO，所有的占位符都将视为OGNL表达式：
      1. String -> #{name}  则会报错，提示String类型没有getName方法！
      2. #{id.name} ${name.last}  ->  this.getId().getName() this.getName().getLast()
2. 有parameterType：
   1. 值必须为以下两者中的一种：
      1. 原生类型别名的内核注册：

```Java
public TypeAliasRegistry() {
    registerAlias("string", String.class);

    registerAlias("byte", Byte.class);
    registerAlias("long", Long.class);
    registerAlias("short", Short.class);
    registerAlias("int", Integer.class);
    registerAlias("integer", Integer.class);
    registerAlias("double", Double.class);
    registerAlias("float", Float.class);
    registerAlias("boolean", Boolean.class);

    registerAlias("byte[]", Byte[].class);
    registerAlias("long[]", Long[].class);
    registerAlias("short[]", Short[].class);
    registerAlias("int[]", Integer[].class);
    registerAlias("integer[]", Integer[].class);
    registerAlias("double[]", Double[].class);
    registerAlias("float[]", Float[].class);
    registerAlias("boolean[]", Boolean[].class);

    registerAlias("_byte", byte.class);
    registerAlias("_long", long.class);
    registerAlias("_short", short.class);
    registerAlias("_int", int.class);
    registerAlias("_integer", int.class);
    registerAlias("_double", double.class);
    registerAlias("_float", float.class);
    registerAlias("_boolean", boolean.class);

    registerAlias("_byte[]", byte[].class);
    registerAlias("_long[]", long[].class);
    registerAlias("_short[]", short[].class);
    registerAlias("_int[]", int[].class);
    registerAlias("_integer[]", int[].class);
    registerAlias("_double[]", double[].class);
    registerAlias("_float[]", float[].class);
    registerAlias("_boolean[]", boolean[].class);

    registerAlias("date", Date.class);
    registerAlias("decimal", BigDecimal.class);
    registerAlias("bigdecimal", BigDecimal.class);
    registerAlias("biginteger", BigInteger.class);
    registerAlias("object", Object.class);

    registerAlias("date[]", Date[].class);
    registerAlias("decimal[]", BigDecimal[].class);
    registerAlias("bigdecimal[]", BigDecimal[].class);
    registerAlias("biginteger[]", BigInteger[].class);
    registerAlias("object[]", Object[].class);

    registerAlias("map", Map.class);
    registerAlias("hashmap", HashMap.class);
    registerAlias("list", List.class);
    registerAlias("arraylist", ArrayList.class);
    registerAlias("collection", Collection.class);
    registerAlias("iterator", Iterator.class);

    registerAlias("ResultSet", ResultSet.class);
  }
```

1. parameterType="map":
   1. @Param("id") Integer id, @Param("name") String name -> #{id} #{name}
   2. Map map {"id":"123", "name":"Peter"} -> #{id} #{name} 、 ONGL属性值：<if test="id != null"> id=#{id}、<foreach collection="idList" ..等 取{"idList":{1, 2, 3}}
2. parameterType不写：默认为上述的alias，根据结构层传入参数的Java类型动态决定 （自动推断）！
   - #{id} 传进来的是Integer那就直接解析成Integer，传进来的是String就直接解析成String
   - 传进来是啥就是啥，如果是List、Map等则按照上面的规则解析
      - 或者是根据注解信息进行解析
      - OGNL表达式：@Param("schema") String schema, @Param("user") User user -> ${user.name} #{user.age} ${schema}
3. 如果parameter写，则下属两种情况：
   1. 值为上述的alias：那么接口层传入的参数必须严格地为注册的.class类型！！否则类型冲突报错！
   2. 其余所有的Java类型必须写 **权限定类名**，而且必须是POJO：
      - 所有的#{} ${} <if test="OGNL"等等都是取 POJO的getter.


@Param可以重载：

// 两者构成重载.
func(@Param("a") String s);
func(@Param("b") String s);
