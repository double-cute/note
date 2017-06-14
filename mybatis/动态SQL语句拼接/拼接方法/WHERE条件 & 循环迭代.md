# WHERE条件 & 循环迭代
> `<where>`标签以及`WHERE(String conditions)`方法就是用来解决拼接`WHERE条件`时的 **BUG**.
>
> - 其实也是`<if>`、`<choose:when-otherwise>`的BUG：**首尾无关连接符** & **空条件**.

<br><br>

## 目录

1. []()
2. []()

<br><br>

### 一、WHERE条件：`<where>` VS `WHERE()+AND()+OR()`  [·](#目录)
> 它们俩的特点就是可以智能去除首尾多余的连接符（`AND`、`OR`），还有就是当`WHERE`中的条件一个都不成立（一个条件都没有拼接进去时）可以智能忽略`WHERE`关键字.

<br>

**1.&nbsp; 标签拼接：**

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

**2.&nbsp; 注解拼接：**

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

### 二、循环迭代：

<br>
