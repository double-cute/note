

- **resources/**
   - **MyBatis配置文件：** mybatis-config.xml
      - `<mappers>`  (**3种定位mapper的方式，任选一种**) (**可以有多个mappers和多个mapper**)
         1. `<mapper class="${mapper-namespace}" />`
            - 使用权限定类名.
            - `"org.my.bat.batfirst.dao.UserDao"`
         2. `<mapper resource="${mapper-xml-app-path}" />`
            - 相对于 **resource/** 根目录来说的 **绝对路径**.
            - `"org/my/bat/batfirst/dao/UserDao.mapper.xml"`
         3. `<mapper url="${mapper-xml-url}" />`
            - `"file://C:/mapper/UserDao.mapper.xml"`
      - `</mappers>`
   - **MyBatis映射文件的目录：** `org/my/bat/batfirst/dao/`
      1. 1个POJO -> 1个dao（mapper）-> n个SQL语句.
      2. 目录中包含多个POJO对应的mapper-xml.
         - **UserDao.mapper.xml**
         -  `<mapper namespace="org.my.bat.batfirst.dao.UserDao">`
            - namespace为 **mapper的Java类名**.
            - `<select ...`
            - `<insert ...`
            - `...`
         - `</mapper>`

<br><br>

### 、

- MyBatis执行一条SQL语句的全过程：

```XML
<!-- 自动生成了一个org.my.bat.batfirst.dao.MyBatisMapper_Impl_UserDao的Java类 -->
<mapper namespace="org.my.bat.batfirst.dao.UserDao">
    <!-- 自动为该Mapper类生成了一个 public User selectById(int id); 的方法 -->
    <!-- 因此id属性也叫做SQL语句的id，即mapper的SQL查询方法名 -->
    <select id="selectById" parameterType="int" resultType="org.my.bat.batfirst.domain.User">
        <!-- 方法的实现是下面该select语句 -->
        select
            *
        from tb_user
            where id = #{id}
    </select>
</mapper>
```

- 相当于定义了一个Java类：

```Java
package org.my.bat.batfirst.dao;
public class MyBatisMapper_Impl_UserDao {
  public User selectById(int id) {
    return sql_ret{
      select
        *
      from tb_user
        where id = #{id}
    }
  }
}
```

- MyBatis根据mybatis-config中对mapper-xml的定位，读取并编译xml文件生成对应的mapper.class类.

- sqlSession进行查询：

```Java
User user = session.selectOne("org.my.bat.batfirst.dao.UserDao.selectById", 15);
// SqlSession的各种select、insert、update、delete系列方法底层都是通过反射进行了如下两个过程
  // 1. 通过反射先调出 org.my.bat.batfirst.dao.MyBatisMapper_Impl_UserDao 类.
  // 2. 再动过反射调用 该类的selectById方法，并传参selectById(15)
```

- 但由于 **// 1.** 中直接出现了 **MyBatisMapper_Impl_UserDao实现类**，违背了接口和实现分离的原则.
- 因此建议再多加一层接口层：

```Java
package org.my.bat.batfirst.dao;
public interface UserDao {
  User selectById(int id); // 包路径、返回值、方法名、参数等必须和mapper-xml一致
}
```

- MyBatis也提供了面向接口变成的功能：

```Java
UserDao userDao = session.getMapper(UserDao.class);
// getMapper的原理：
  // 根据接口名 "org.my.bat.batfirst.dao.UserDao" 调出相应的实现类 org.my.bat.batfirst.dao.MyBatisMapper_Impl_UserDao
  // 因此这就要求接口层的 包路径、返回值、方法名、参数等统统都要和mapper-xml一致！

// 正常使用
User user = userDao.selectById(15);
```

- 由于一致的地方都是重复的地方，违反了代码重用的精神，因此考虑 **将接口层 & mapper-xml合并**，因此就变成了MyBatis的Annotation注解.
- 将接口和mapper-xml都合并到接口层当中：

```Java
package org.my.bat.batfirst.dao; // 接口 & 类的路径，合二为一了
public interface UserDao {
// 接口 & 类的名字合二为一了，可以通过UserDao推断出MyBatisMapper_Impl_UserDao的类名

    // 1. 原则是简单的语句用注解实现，复杂的语句用mapper-xml实现
    @Select("select * from tb_user where id = #{id}") // 方法实现 & SQL语句合二为一
    User selectByIdAnno(Integer id); // 接口方法和SQL语句声明合二为一

    // 2. 复杂的SQL语句用mapper-xml实现
    List<User> selectByALotOfCondition(Condition condition);
    // 方法的实现在mapper-xml中，因此要求
}
```

- 而session.getMapper的过程也可以简略，在Spring中可以使用依赖注入简略该过程：

```Java
@Autowired
private UserDao userDao;  // 相当于 userDao = session.getMapper(UserDao.class);
```
