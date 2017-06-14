# WHERE条件 & 循环迭代
> `<where>`标签以及`WHERE(String conditions)`方法就是用来解决拼接`WHERE条件`时的 **BUG**.
>
> - 其实也是`<if>`、`<choose:when-otherwise>`的BUG：**首尾无关连接符** & **空条件**.
>
> <br>
>
> `<foreach>`标签以及Java的`for循环`都可以自动生成大量有规律的SQL片段进行拼接.

<br><br>

## 目录

1. [WHERE条件：`<where>` VS `WHERE()+AND()+OR()`](#一where条件where-vs-whereandor--)
2. [循环迭代：`<foreach>` VS `for循环`](#二循环迭代foreach-vs-for循环--)

<br><br>

### 一、WHERE条件：`<where>` VS `WHERE()+AND()+OR()`  [·](#目录)
> 它们俩的特点就是可以智能去除首尾多余的连接符（`AND`、`OR`），还有就是当`WHERE`中的条件一个都不成立（一个条件都没有拼接进去时）可以智能忽略`WHERE`关键字.

<br>

**1.&nbsp; 标签拼接：`<where>`**

```XML
SELECT * FROM blog
<where> <!-- 当然还可以嵌套<choose:when-otherwise>，所有SQL标签都可以各种灵活地嵌套 -->
    <if test="state != null">
        AND state = #{state}  <!-- 由于可以智能地掐头去尾，因此第一个条件可以加AND|OR前缀 -->
    </if>
    <if test="title != null">
        AND title LIKE #{title}
    </if>
    <if test="author != null and author.name != null">
        AND author_name LIKE #{author.name}
    </if>
</where>
```

<br>

**2.&nbsp; 注解拼接：WHERE+AND+OR**

- 原型：

```Java
// 均可连续拼接
T WHERE(String... conditions);
T OR();
T AND();
```

- 示例：

```Java
SELECT("*");
FROM("tb");

// 1. WHERE可以智能忽略第一个WHERE前端多余连接符
AND(); OR();

// 2. 连续的WHERE 用'( )'包裹 且 用'AND'连接
WHERE("a");WHERE("b");WHERE("c");

// 3. AND|OR就简单代表一个'AND( ) | OR( )'
  // 后接连续的'WHERE'被包裹在'AND( ) | OR( )'中
OR();WHERE("x");WHERE("y");WHERE("z");
AND();WHERE("1");WHERE("2");WHERE("3");

// 4. 再次证实AND|OR就简单代表一个'AND( ) | OR( )'
   // 并且'OR()'和'AND()'之间是相互平行，而非嵌套
   // 想要嵌套就只能自己在Java字符串里手写，例如："AND(a OR b) OR c"
OR();OR();AND();AND();OR();AND();
```

- 输出是：

```SQL
SELECT * FROM tb WHERE (a AND b AND c)  OR (x AND y AND z)  AND (1 AND 2 AND 3)  OR ()  OR ()  AND ()  AND ()  OR ()  AND ()
```

<br><br>

### 二、循环迭代：`<foreach>` VS `for循环`  [·](#目录)
> 用来拼接具有迭代规律的SQL片段，例如：`AND val IN(1, 2, 3, 4, ... 1000)`，那么`1 - 1000`的片段就可以用循环迭代生成.

<br>

**1.&nbsp; 标签拼接：`<foreach>`语法简介**

```XML
<foreach
    collection="OGNL表达式：迭代器（传入SQL的参数必须包含该迭代器）"
    index="当前迭代到的元素的索引名（索引从0计，访问索引值使用`#{索引名}`"
    item="当前迭代到的元素的名字（通过`#{元素名}`访问）"
    open="整个迭代的起始符号（就只有一个）" separator="迭代分隔符" close="整个迭代的终止符（就只有一个）"
/>
```

<br>

**2.&nbsp; `<foreach:collection>`详解：专有临时OGNL-stack**

1. 当进入`<foreach>`标签时，MyBatis会临时地专门为`<foreach>`开辟一个专属的、特有的 **foreach-OGNL-stack**.
2. 然后将传入SQL语句的参数复制到这个 **foreach-OGNL-stack** 中去.
3. 复制参数的过程中会做一些特殊处理：
   1. 如果参数的Java类型为`List`或`Collection`或`Array`，那么就将参数改名后压入 **foreach-OGNL-stack** 栈顶.
      - 分别改名为：`list`、`collection`、`array`.
      - 相当于：
         - 传入的参数：
            1. `List<T> param`
            2. `Collection<T> param`
            3. `T[] param`
         - 压栈：
            1. `stack.push("list", param)`
            2. `stack.push("collection", param)`
            3. `stack.push("array", param)`.
         - 这就意味着`<foreach:collection>`的值应该填`list`、`collection`、`array`.
   2. 否则就默认传入的参数为`POJO`或`map`，迭代器位于其`getter`或`value`中，并且不改名直接压入栈顶.
      - 相当于：
         - 传入的参数：
            1. `Clazz(id:12, students:List<Student>)`
            2. `Map{"id":12, "students":List<Student>}`
         - 压栈：`POJO`和`map`是等价的
            1. `stack.push("id", 12).push("students", List<Student>)`
            2. `stack.push("id", 12).push("students", List<Student>)`
         - 这就意味着`<foreach:collection>`的值应该填`students`，因为没有改名.

- 可以看源码实现：

```Java
private Object wrapCollection(final Object object) {
    if (object instanceof Collection) {
        StrictMap<Object> map = new StrictMap<Object>();
        map.put("collection", object); // Collection类型被改名
        if (object instanceof List) {
            map.put("list", object); // List类型也被改名
        }
        return map;
    } else if (object != null && object.getClass().isArray()) {
        StrictMap<Object> map = new StrictMap<Object>();
        map.put("array", object); // Array类型也被改名
        return map;
    }
    return object; // 其余不改名
}
```

<br>

**3.&nbsp; `<foreach>`应用：**

> 一个最简单的示例，之前讲过，就是insert的多值插入（只有MySQL支持的语法）.

```XML
<insert id="insertUsers" useGeneratedKeys="true" keyProperty="id">
    INSERT
    into
        tb_user
            (name, sex, age)
    values
        <!-- 传入的参数分别为：Collection<User>、List<User>、User[] -->
            <!-- 其中other表示：传入的param里有一个other域，这个域是一个可迭代的集合 -->
        <foreach collection="collection|list|array|other" item="item" separator=",">
            (#{item.name}, #{item.sex}, #{item.age})
        </foreach>
</insert>
```

- 更一般的示例：

```XML
<foreach collection="list"
    index="index" item="item"
    open="[" separator=":" close="]">
    #{index}-#{item.name}
</foreach>
<!-- 得到 "[0-Peter:1-Tom:2-Marry]" -->
```

<br>

**4.&nbsp; 注解拼接：**

- 比较麻烦，使用`for循环`和`String.join`.

```Java
List<String> list = ...;
List<String> sql_list = new ...;
for (String item: list) {
    sql_list.add("(" + item + ")");
}
String sql = "INSERT INTO tb_user " + String.join(",", sql_list);
```
