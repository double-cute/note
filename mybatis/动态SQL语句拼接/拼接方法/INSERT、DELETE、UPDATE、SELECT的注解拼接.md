# INSERT、DELETE、UPDATE、SELECT的注解拼接
> 这4种语句的标签拼接就是直接编写SQL脚本，没什么好讲的.

<br><br>

## 目录

1. [INSERT]()
2. [DELETE]()
3. [UPDATE]()
4. [SELECT]()

<br><br>

### 一、INSERT：[·](#目录)
> 只支持单表、单值插入，不支持单表多值插入.

<br>

**1.&nbsp; 命令头：**

```Java
// 只支持单表插入，不能重复拼接
T INSERT_INTO(String tableName);
```

<br>

**2.&nbsp; 两种插值方式：**

```Java
// 都可连续拼接

// 1. setter方式：更加直观，更加铁件编程语言
T VALUES(String columns, String values);

// 2. SQL语法方式：贴近SQL语法
T INTO_COLUMNS(String... columns);
T INTO_VALUES(String... values);
```

<br>

**3.&nbsp; 两种方式可以混合连续拼接：**

```Java
INSERT_INTO("tb_user");
INTO_COLUMNS("a", "b", "c");
INTO_VALUES("1", "2", "3");
INTO_COLUMNS("p", "q", "u");
INTO_VALUES("'x'", "'y'", "'z'");
VALUES("c1", "#{m}"); // 假设param.m、param.n的值为'm'、'n'
VALUES("c2", "#{n}");
```

- 得到：

```SQL
INSERT INTO tb_user  (a, b, c, p, q, u, c1, c2) VALUES (1, 2, 3, 'x', 'y', 'z', 'm', 'n')
```

<br><br>

### 二、DELETE：[·](#目录)
> 只支持单表删除记录，不支持一次删多个表的记录.

<br>

**1.&nbsp; 命令头：**

```Java
// 只支持单表删除，不能重复拼接
T DELETE_FROM(String table);
```

<br>

**2.&nbsp; 示例：**

```Java
DELETE_FROM("tb_user");
WHERE("a");
WHERE("b");
```

- 输出：

```SQL
DELETE FROM tb_user WHERE (a AND b)
```

<br><br>

### 三、UPDATE：[·](#目录)
> 只支持单表更新，不能一次同时更新多张表.

<br>

**1.&nbsp; 命令头：**

```Java
// 只支持单表更新，不能连续拼接
T UPDATE(String table);
```

<br>

**2.&nbsp; 设置：可连续拼接**

```Java
// 可连续拼接
T SET(String... sets);
```

<br>

**3.&nbsp; 示例：**

```Java
UPDATE("tb_user");
SET("a = #{a}"); // 连续拼接
SET("b = #{b}", "c = #{c}");
SET("d = #{d}");
```

- 输出：

```SQL
UPDATE tb_user SET a = ?, b = ?, c = ?, d = ?
```

<br><br>

### 四、SELECT：[·](#目录)

<br>

**1.&nbsp; 命令头：筛选结果列（可连续拼接）**

```Java
T SELECT(String... columns);
T SELECT_DISTINCT(String columns);
```

- 示例：

```Java
SELECT("a", "b AS bb");
SELECT("c");
SELECT("d", "e");
// 输出：SELECT a, b AS bb, c, d, e
```

- 注意`DISTINCT`的用法，只要出现一处就会加到前缀中（唯一）：

```Java
SELECT("a", "b AS bb");
SELECT("c");
SELECT_DISTINCT("x", "y");
SELECT("d", "e");
// 输出：SELECT DISTINCT a, b AS bb, c, x, y, d, e
```

<br>

**2.&nbsp; `FROM`：可连续拼接，连接用到的表**

```Java
T FROM(String... tables);
```

- 示例：

```Java
SELECT("col");
FROM("a", "b");
FROM("c");
// 输出：SELECT col FROM a, b, c
```

<br>

**3.&nbsp; 连接：可拼接**

- 总共提供了4种连接方式：均可拼接
   - 只不过`ON`条件要自己写在`joins`参数中，比较麻烦.
      - 并没有提供`ON`的单独拼接方法.
      - 一般`ON`都是单个等值连接，因此就没有提供.
         - 这是从使用经验的角度设计的.

```Java
// 1. 普通连接
T JOIN(String... joins);
// 2. 自然连接
T INNER_JOIN(String... joins);
// 3. 左外连接
T LEFT_OUTER_JOIN(String... joins);
// 4. 右外连接
T RIGHT_OUTER_JOIN(String... joins);
```

<br>

- 示例：

```Java
SELECT("col");
FROM("a", "b");
JOIN("c ON b.id = c.id", "d ON c.id = d.id");
LEFT_OUTER_JOIN("e ON a.id = e.id");
// 输出：SELECT col FROM a, b JOIN c ON b.id = c.id JOIN d ON c.id = d.id LEFT OUTER JOIN e ON a.id = e.id
```

<br>

**4.&nbsp; 分组 & 排序：均可拼接**

> `HAVING`的用法和`WHERE`完全一样.
>
> - 和`OR`和`AND`的组合也和`WHERE`完全一样.

```Java
// 1. 分组
T GROUP_BY(String... columns);
T HAVING(String... conditions);

// 2. 排序
T ORDER_BY(String... columns);
```

<br>

- 示例：相互顺序可以完全打乱！！

```Java
GROUP_BY("a", "b", "c");
FROM("E", "CS");
SELECT("col", "loc");
JOIN("k ON k.id = b.id");
FROM("a");
INNER_JOIN("q ON q.id = a.id");
HAVING("COUNT(*) > 10", "SUM(*) > 15");
GROUP_BY("d");
WHERE("8 = 9", "a > 83");
HAVING("c");
OR();
ORDER_BY("k DESC");
HAVING("d", "x", "v");
ORDER_BY("a", "b ASC");
```

- 可以看到输出按照标准顺序：

```SQL
SELECT
    col,
    loc
FROM
    E,
    CS,
    a JOIN k ON
        k.id = b.id
    INNER JOIN q ON
        q.id = a.id
WHERE
    (8 = 9 AND a > 83)
GROUP BY
    a,
    b,
    c,
    d
HAVING
    (COUNT(*) > 10 AND SUM(*) > 15 AND c) OR
    (d AND x AND v)
ORDER BY
    k DESC,
    a,
    b ASC
```
