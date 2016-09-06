# hbm文件结构

<br><br>

## 目录

<br><br>

### 一、基本结构：[·](#目录)
1. 根元素<hibernate-mapping>
2. 子元素中每个<class>对应一个PO映射.
3. <hibernate-mapping>可以包含多个<class>映射，**但最好的做法是一个hbm就只有一个<class>映射**，并且在很多情况下是**必须的**.

示例：
```java
<?xml version="1.0" encoding="UTF-8"?>
<!-- Hibernate DTD信息，模板，所有都一样，包括Hibernate5.0也和这个一样 -->
<!DOCTYPE hibernate-mapping PUBLIC
    "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
    "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
<hibernate-mapping package="org.crazyit.app.domain"> <!-- 指定PO所在的Java包路径 -->
    <class name="News" table="news_table"> <!-- 最重要的就是这句，指明了PO到表的映射 -->
        <id name="id">
            <generator class="identity"/>
        </id>
    </class>
</hibernate-mapping>
```

<br><br>

### 二、hibernate-mapping的属性：只列举一些基础的属性 [·](#目录)
- 先介绍那些<hibernate-mapping>和<class>共有的属性，只不过局部覆盖全局下<class>的同名属性覆盖<hibernate-mapping>的.

**罗列共同属性：**

| 属性 | 作用 |
| --- | --- |
| schema | 数据表的Schema前缀 |
| catalog | 数据表的catalog前缀 |
| defualt-lazy | 延迟加载**（很重要，默认开true）** |
| defualt-cascade | 级联策略（默认为none） |
| package（hibernate-mapping独有） | 指定其下PO类所属的Java包路径 |

- defualt-lazy是延迟加载，最好开true，如果加载一个关联了很多表的记录，不开延迟加载的话会将所有关联记录也一并加载，导致性能骤降.
- defualt-cascade是级联策略，在级联删除记录等情境下会应用到.
- package是hibernate-mapping的独有属性，用于指定其PO类所属的Java包路径，指定了该属性，那么class标签的name就无需使用全限定类名了（即完整的包路径）.

<br><br>

### 三、class的属性：只列举一些基础的属性 [·](#目录)
| 属性 | 说明 |
| --- | --- |
| name | **必选**，指定PO的Java类名，如果hibernate-mapping中指定了package，则这里就无需指定全限定类名 |
| table | 映射的表名，如果不写则默认表名为PO的Java类名 |
| mutable | 指定该PO对象是可变还是不可变的，默认为true，不可变意味着只读 |
