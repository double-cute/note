
- 类型别名：别名可以在parameterType、resultType中使用（简话权限定类名）s
   1. mybatis-config.xml中配置：


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

- 这三种方法都成立.

- 第4中是 TypeAliasRegistry中定义的各种别名！！！都可以使用.

<br><br>

###、


- select

| useCache | select中默认为true，其余默认为false，true表示该SQL语句的结果将会被二级缓存 |


- resultType 和 resultMap不能同时使用：

<br><br>

### 、resultType：

1.
