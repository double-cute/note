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
