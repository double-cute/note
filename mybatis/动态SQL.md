> 动态SQL相当于C语言的预编译指令

> 所有标签的属性值都可以使用OGNL表达式，表达式从select、insert、update、delete标签的传入参数中获取.

<br><br>

### 一、if

```XML
where state='ACTIVE'
  <if test="OGNL != null">
    and title = #{OGNL}
  </if>
  <if test="ONGL != null and OGNL.id > 1000">
    and name like #{OGNL.name}
  </if>
```

<br><br>

### 二、choose-when-otherwise：类似Java的switch
> otherwise代替else

1. 每个when没有break，其实when就是一个个顺序执行下来的if
2. 由于没有else标签，所以otherwise刚好完成了else的功能.

```xml
<select id="findActiveBlogLike"
     resultType="Blog">
  SELECT * FROM BLOG WHERE state = ‘ACTIVE’
  <choose>
    <when test="title != null">
      AND title like #{title}
    </when>
    <when test="author != null and author.name != null">
      AND author_name like #{author.name}
    </when>
    <otherwise>
      AND featured = 1
    </otherwise>
  </choose>
</select>
```

<br><br>

### 、where

1. 最大的特色就是：
   1. 如果where内部最终为空，则where自己去掉
   2. 最终的结果可以去掉前置where后的前缀and、or


```xml
<select id="findActiveBlogLike"
     resultType="Blog">
  SELECT * FROM BLOG
  <where> <!-- 当然还可以嵌套choose ，所有SQL标签都可以各种灵活的相互嵌套的 -->
    <if test="state != null">
         state = #{state}
    </if>
    <if test="title != null">
        AND title like #{title}
    </if>
    <if test="author != null and author.name != null">
        AND author_name like #{author.name}
    </if>
  </where>
</select>
```

<br><br>

### 、set

- 特色：
   1. 如果set内部为空，则会去掉set
   2. 可以去掉后缀的','

```xml
<update id="updateAuthorIfNecessary">
  update Author
    <set>
      <if test="username != null">username=#{username},</if>
      <if test="password != null">password=#{password},</if>
      <if test="email != null">email=#{email},</if>
      <if test="bio != null">bio=#{bio}</if>
    </set>
  where id=#{id}
</update>
```


### foreach



```xml
<foreach
  collection="xxx"
  index="索引名"  # 当前迭代到的元素的索引，从0计算
  item="元素名"
  open="("  # 其实符号
  separator="," # 迭代内容的分隔符
  close=")" > # 结束符号

  #{item.name} // item.getName()  #{index} // 0 1 2 3
</foreach>
```

- foreach要迭代的容器也是放在ONGL的临时map中的，想要访问该容器，也需要通过该容器在OGNL map中的key来访问，这个key名就是collection属性！

- collection只跟接口层传入的参数类型有关，跟parameterType没有人任何关系.
  - foreach需要迭代的内容必然要保存在OGNLtop_stack_map的key的value中.
  - foreach维护一个 临时的、局部的ONGL stack-map.
     1. 参数为List，则默认key为"list"，加入topMap
     2. 参数为Collection，则默认key为"collection"，加入topMap
     3. 参数为Array，则默认key为"array"，加入topMap
     4. 其它情况"xxx"，则默认topmap.put("xxx", param.getXxx());
  - 实现如下：


  ```Java
  private Object wrapCollection(final Object object) {
      if (object instanceof Collection) {
        StrictMap<Object> map = new StrictMap<Object>();
        map.put("collection", object);
        if (object instanceof List) {
          map.put("list", object);
        }
        return map;
      } else if (object != null && object.getClass().isArray()) {
        StrictMap<Object> map = new StrictMap<Object>();
        map.put("array", object);
        return map;
      }
      return object;
    }
  ```


<br><br>

### 、bind

- 可以利用OGNL表达式创建一个新的OGNL表达式变量，在bind之后的位置引用该变量
   - \_parameter是ONGL中内置的变量，用来引用传进来的参数.

```xml
<select id="selectBind" resultType="sucker">
        <bind name="pattern" value="'%' + _parameter.getId() + '%' + _parameter.getName() + '%'" /> <!-- 得到 %15%lala% 字符串 -->
        select * from fuck where title like #{pattern}
    </select>
```
