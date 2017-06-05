# MyBatis的2重映射 & 执行SQL语句的模型抽象
> 和其它ORM框架不同的是：
>
> 1. 其它框架（如Hibernate等）的映射核心是定义SQL表中数据记录和POJO之间的映射.
> 2. MyBatis不仅仅实现了上述基本的映射功能，MyBatis着重关注Java方法和SQL语句之间的映射.
>    - 具体地说，MyBatis可以将SQL语句和Java方法一一对应.
>
>> - MyBatis的设计理念：更加灵活和可扩展，可以使开发者深入SQL数据库的层面使用Java和数据库交互.
>>    - 以完成一些其它ORM框架无法完成的任务.
>>    - 由于需要开发者直接编写SQL语句，因此MyBatis是一种 **半自动化** 的框架（和Hibernate相比）.
>>
>> <br>
>>
>>> MyBatis面向的需求：
>>>
>>> 1. 在实际生产中，很多业务逻辑必须在数据库层通过存储过程实现（如银行业），不允许在应用程序层实现.
>>> 2. 数据量巨大，需要SQL语句层面上的优化.
>>> 3. 处于安全考虑，具体表结构不予以公开，需要直接使用SQL语句完成业务逻辑.
>>>
>>> <br>
>>>
>>> - 由于Hibernate这些全自动化的一站式框架完全隐藏了SQL语句的生成和构造，因此根本无法满足上述需求.
>>> - 面对这样的需求，MyBatis几乎是唯一的解决方案.

<br><br>

## 目录

1. [MyBatis的2重映射]()
2. [执行SQL语句的模型抽象]()
3. [使上述过程面向接口]()
4. [面向接口的过程还可以进一步简化：注解]()

<br><br>

### 一、MyBatis的2重映射：[·](#目录)
> 两种映射都是在MyBatis的 **mapper-xml** 文件中定义.

<br>

1. **SQL语句** 和 **DAO方法** 之间的映射：
   - 将SQL语句包装成Java方法.
      - 定义用到的XML标签有：`<select>、<insert>、<update>、<delete>`
2. **SQL表记录** 和 **Java-POJO** 之间的映射：
   - 将SQL表中的记录包装成Java的POJO.
      - 定义用到的XML标签为：`<resultMap>`

<br><br>

### 二、执行SQL语句的模型抽象：[·](#目录)
> 接下来通过介绍 **MyBatis执行一条SQL语句的全过程** 来展示MyBatis的设计理念.
>
>> 其中包括MyBatis的 **面向接口设计模式** & **注解简化** 的核心概念.

<br>

**1.&nbsp; 将SQL语句转化成Java方法：mapper-xml**

- `org/my/bat/batfirst/dao/UserDao.xml`

```XML
<!-- 自动生成了一个org.my.bat.batfirst.dao.MyBatisMapper_Impl_UserDao的Java类 -->
    <!-- 由于最终是要编译成一个Java类，因此namespace必须是一个标准的Java类名 -->
<mapper namespace="org.my.bat.batfirst.dao.UserDao">
    <!-- 自动为该Mapper类生成了一个 public User selectById(int id) 的方法 -->
        <!-- 因此id属性也叫做SQL语句的id，即mapper的SQL查询方法名 -->
            <!-- SQL语句的类型是select语句，MyBatis在将其转化成Java代码时需要进行一定的特殊处理 -->
    <select id="selectById" parameterType="int" resultType="org.my.bat.batfirst.domain.User">
        <!-- 方法的实现是下面该select语句 -->
        SELECT
            *
        from
            tb_user
        where
            id = #{id}
    </select>
</mapper>
```

- 相当于定义了一个Java类：`MyBatisMapper_Impl_UserDao`
   - 只不过自己用手工方式编写一个SQL查询的Java方法很麻烦，而MyBatis可以帮你省去这样的麻烦.

```Java
package org.my.bat.batfirst.dao;  // mapper:namespace 中定义的包路径
public class MyBatisMapper_Impl_UserDao { // mapper:namespace 中末尾定义的
    // 1. 返回类型User 对应 select:resultType
    // 2. 方法名selectId 对应 select:id
    // 3. 参数类型Integer 对应 select:parameterType
    public User selectById(Integer id) {
        return SQL{ // 4. 方法实现对应 select:SQL语句
            select
                *
            from
                tb_user
            where
                id = #{id}  // 5. #{}为OGNL表达式，对应传入的参数值
        }
    }
}
```

<br>

**2.&nbsp; 使用上述方法进行数据库查询的步骤：**

- 定位上述方法的步骤：
   1. 读取 `mybatis-config.xml` 配置文件中所有注册的 `mapper`.
   2. 解析 `mapper-xml` 并翻译成 `.java` 文件.
   3. 编译 `.java` 生成 `MyBatisMapper_Impl_Xxx.class` 类并加载进JVM.
   4. 愉快地使用这些类及其dao方法访问数据库.

<br>

**3.&nbsp; 在`mybatis-config.xml`中注册`mapper`：**

```XML
<mappers> <!-- 3种注册mapper的方式，任选一种即可 -->
    <!-- 可以有多个 <mappers> 和多个 <mapper> -->

    <!-- 1. 根据mapper-xml文件的URL来定位 -->
    <mapper url="${url:mapper-xml}" />
    <!-- 例如：<mapper url="file://C:/mapper/UserDao.xml" /> -->

    <!-- 2. 根据 maven:resource/ 相对路径定位mapper-xml-->
    <mapper resource="${maven-resource:mapper-xml}" />
    <!-- 例如：<mapper resource="org/my/bat/batfirst/dao/UserDao.xml" /> -->
        <!--
            底层实现还是利用mapper-url：
                <mapper url="
                    file://工程的maven目录/resource/
                        org/my/bat/batfirst/dao/UserDao.xml
                " />
        -->

    <!-- 3. 用MyBatisMapper_Impl_Xxx.class的Java类名注册 -->
    <mapper class="${mapper:namespace}" />
    <!-- i. 由于底层还是使用mapper-url定位的，因此${mapper:namespace}必须使用全限定类名 -->
    <!-- ii. 例如：<mapper class="org.my.bat.batfirst.dao.UserDao" /> -->
        <!--
            底层实现还是利用mapper-url：
                <mapper url="
                    file://工程的maven目录/resource/
                        org/my/bat/batfirst/dao/UserDao.xml
                " />
        -->
    <!-- iii.
        也就是说，仅仅就是做一下简单的字符串处理罢了：
            1. '.'替换成'/'
            2. 末尾加上'.xml'后缀
    -->
    <!-- iv. 因此，这种方法要求${mapper:namespace}和mapper-xml的文件名 *完全* 一致 -->
        <!-- 例如：UserDao的mapper-xml不能取成UserDao.mapper.xml，必须取成UserDao.xml -->
        <!-- 因为MyBatis仅仅就是做一下字符串处理，将'a.b.c.UserDao'处理成'a/b/c/UserDao.xml'
                如果把mapper-xml取成UserDao.mapper.xml就找不到了 -->

</mappers>
```

- 回顾MyBatis的mapper和Java编程之间的关系：

| MyBatis | Java |
| --- | --- |
| mapper | dao |
| mapper:sql语句 | dao.数据库访问方法 |

- Java的1个POJO就需要有1个dao对应.

<br>

**4.&nbsp; MyBatis进行数据库访问的Java步骤：**

```Java
InputStream mybatisConfigXML = Resources.getResourceAsStream("mybatis-config.xml");

// 1. MyBatis读取配置文件，其中包含了mapper的注册信息
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(mybatisConfigXML);
// 2. 打开一个session
SqlSession session = sqlSessionFactory.openSession();

// 3. 利用反射，解析出mapper类为org.my.bat.batfirst.dao.MyBatis_Impl_UserDao.class
// 4. 从配置中寻找是否有注册过 org/my/bat/batfirst/dao/UserDao.xml 的mapper配置文件
// 5. 找到后将其翻译成.java并编译出.class加在进JVM.
// 6. 再利用反射，调用selectById方法，并传入参数15，并返回执行结果.
    // 意思是调用org.my.bat.batfirst.dao.MyBatis_Impl_UserDao类的selectById方法，参数是15.
User user = session.selectOne("org.my.bat.batfirst.dao.UserDao.selectById", 15);
// 翻译成Java就是：
    // 3. resolve-class-name: org.my.bat.batfirst.dao.MyBatis_Impl_UserDao.class
    // 4. find .xml in mybatis-config registries: org/my/bat/batfirst/dao/UserDao.xml
    // 5. translate .xml to .java; javac .java to .class; load .class into JVM
    // 6. reflect: User user = MyBatis_Impl_UserDao.selectById(15);
// 这里执行了SQL语句：select * from tb_user where id = 15

// 7. 提交事务并关闭session
session.commit();
session.close();
```

<br><br>

### 三、使上述过程面向接口：[·](#目录)
> 由于上述过程中直接出现了 **实现类的反射**. （reflect(MyBatis_Impl_UserDao).selectById(15)）
>
> - 违背了接口和实现分离的原则，因此MyBatis建议再多一层接口抽象，并提供了实际的支持.

<br>

**1.&nbsp; 接口层自己手动定义：**

```Java
package org.my.bat.batfirst.dao; // 包路径 和 ${mapper:namespace} 一致
public interface UserDao { // 接口名 和 ${mapper:namespace} 一致
    User selectById(int id); // 返回类型、方法名、参数 和 resultType、id、parameterType 一致
}
```

- 也就是说接口层（UserDao）自己定义，实现交由mapper-xml（MyBatisMapper_Impl_UserDao）定义.

<br>

**2.&nbsp; MyBatis对面向接口的支持：**

- 将 `User user = session.selectOne("org.my.bat.batfirst.dao.UserDao.selectById", 15);` 替换成了：

```Java
// 其实就是：UserDao userDao = reflect(MyBatisMapper_Impl_UserDao);
    // reflect的过程就是从mybatis找注册信息并翻译、编译、加载的那个流程
UserDao userDao = session.getMapper(UserDao.class);

// 然后正常使用
User user = userDao.selectById(15);
```

<br><br>

### 四、面向接口的过程还可以进一步简化：注解  [·](#目录)
> 虽然面向接口是一种良好的设计模式，但是其中存在代码的重复编写：
>
> 1. 包路径相同.
> 2. 类名相同.
> 3. 方法返回类型相同.
> 4. 方法名相同.
> 5. 方法参数类型相同.
>
>> - 可见，mapper-xml仅比接口层多了一个SQL语句的实现，其余所有（共5处）都相同，这些都是重复的代码.
>> - 因此可以进一步合并、简化.
>>
>>> - 因此MyBatis提供了注解完成上述目标，即接口和实现都在接口层中定义！

<br>

- 在调用getMapper时MyBatis会根据 `UserDao` 接口中的注解自动生成 `MyBatisMapper_Impl_UserDao` 实现类.

```Java
package org.my.bat.batfirst.dao; // 1. 的合并
public interface UserDao { // 2. 的合并
    // SQL语句通过注解附加地实现
    @Select("select * from tb_user where id = #{id}")
    User selectByIdAnno(Integer id); // 3. 4. 5. 的合并
    // 注解定义的SQL语句方法免去mapper-xml的定义

    // 复杂的SQL语句用mapper-xml实现
    List<User> selectByALotOfCondition(Condition condition);
    // 没有注解实现就必须在mapper-xml中实现
}
```

<br>

- 而session.getMapper的过程也可以简略，在Spring中可以使用依赖注入：

```Java
@Autowired
private UserDao userDao;  // 相当于 userDao = session.getMapper(UserDao.class);
```
