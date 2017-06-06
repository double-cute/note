# 4种语句映射的共有属性 & 可重用sql片段
> MyBatis总共提供4种类型SQL语句的映射，分别为它们提供了mapper-xml的标签：
>
> 1. DML：`<insert>`、`<update>`、`<delete>`
> 2. DQL：`<select>`
>
> <br>
>
> `<sql>`标签是一种特殊的标签，它作为一种简单宏替换可以构造可重用的SQL片段.

<br><br>

## 目录

1. [4种语句映射标签的共有属性]()
2. [可重用sql片段：`<sql>`]()

<br><br>

### 一、4种语句映射标签的共有属性：[·](#目录)
> 即 `<insert>`、`<update>`、`<delete>`、`<select>` 所共有的属性.

<br>

| 共有属性 | 说明 |
| :---: | --- |
| `id` | `1. SQL语句的ID.`<br>`2. 映射成mapper-dao类的方法名.` |
| `parameterType` | `1. 传入SQL语句的参数的Java类型（全限定类名）.`<br>`2. 可使用类型别名.`<br>`3. 映射成mapper-dao类方法的参数类型.` |
| `statementType` | `1. 合法值为："STATEMENT"、"PREPARED"、"CALLABLE".`<br>`2. 分别对应 java.sql 的 Statement、PreparedStatement、CallableStatement .`<br>`3. 默认为 "PREPARED" .`<br>`4. 该属性一般不需要修改，"PREPARED" 性能最优.` |
| `timeout` | `1. SQL语句执行的超时等待时间.`<br>`2. 单位是秒.`<br>`3. 超时后抛出异常.`<br>`4. 默认值依赖驱动.` |
| `flushCache` | `1. SQL语句执行是否出发缓存刷新.`<br>`2. 默认为 "false".`<br>`3. "true" 表示一旦被执行就会清空本地缓存和二级缓存.` |

<br><br>

### 二、可重用sql片段：`<sql>`  [·](#目录)
> 在 `<sql>` 中可以编写任意SQL语句片段（**可包含参数**）.
>
>> 然后，该片段可以像C语言的宏一样被其它任何标签（DML、DQL、sql标签自己本身）所引用（可以在任何位置引入）.
>>
>> - 说白了就是用来进行SQL语句的拼接，将最常用的片段抽象成 `<sql>` 片段而已.

<br>

**1.&nbsp; 定义`<sql>`标签：**

1. id还是正常作为SQL语句的标识符.
2. 传入的参数必须使用穿字符串OGNL表达式（`${...}`），不能使用SQL占位符'?'的OGNL表达式（`#{...}`）.

```XML
<sql id="xxx">
    包含各种${arg}的SQL语句片段
</sql>
```

<br>

**2.&nbsp; 引用`<sql>`片段：**

```XML
<include refid="<sql>片段的id">
    <!-- 使用<property>标签传参 -->
        <!-- 非常灵活，value可以使用OGNL（可以传入SQL语句的参数：Java数据）-->
    <property name="arg名" value="arg值" />
    <property name=... />
    <property name=... />
    ...
</include>
```

<br>

**3.&nbsp; 更支持嵌套`<sql>`引用：**

- 即一个`<sql>`的参数值作为另一个`<sql>`的`id`.
   - 常用于`<sql>`的id可变的嵌套.

```XML
<!-- 嵌入到其它<sql>中的<sql> -->
<sql id="embeded">
    ${prefix}_table
</sql>

<!-- 宿主<sql>，其中要嵌入其它<sql> -->
<sql id="host">
    <!-- 嵌入的<sql>不确定，需要通过传入参数的值确定 -->
    from <include refid="${embed}" />
</sql>

<!-- 应用 -->
<select id="select">
    select
        field1,
        field2
    <include refid="host">
        <!-- 嵌入的<sql>的id是"test" -->
        <property name="embed" value="test" />
        <!-- 接着为嵌入的<sql>传参（即传参给"test"<sql>）-->
            <!-- 这步之后变成了 from <include refid="test"> -->
            <!-- 即 from ${prefix}_table 了 -->
            <property name="prefix" value="nor" />
  </include>
</select>
```
