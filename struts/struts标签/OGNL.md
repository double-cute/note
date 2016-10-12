# OGNL

## 目录
1. [什么是OGNL](#一什么是ognl)
2. [Stack Context和ValueStack](#二stack-context和valuestack)
3. [OGNL基本语法](#三ognl基本语法根对象和数据访问接上例)
4. [集合操作](#四集合操作)
5. [访问静态成员](#五访问静态成员)

<br><br>

### 一、什么是OGNL：[·](#目录)

1. 是struts内建的一种数据访问语言：Object Graph Navigation Language.
2. 其目的是为了提供一种在JSP中简易访问上游action数据的语法，只能在struts标签中使用（标签属性值等处）
3. 其翻译过来的名称是"对象图导航语言"，对象导航的含义如下：如果一个类（同时也可以是对象）结构图为
  * person
    * name
    * group
      * id
      * address
        * number
        * mail

> 如果用Java获取最终的mail数据域就是：person.getGroup().getAddress().getMail();<br>
> 但是如果使用OGNL则是：person.group.adress.mail<br>
> 可以看到OGNL更加简洁.

- 而上面的person就是OGNL的**`根`** 对象，因为可以通过该对象搜索到一切包含在其中的数据，就像树根.
- 虽然看上去OGNL只比Java调用少了get、()这几个字符，但是OGNL最牛叉的功能是不仅可以访问String类型数据还可以访问**任意** Java类型.
  - 只需要在Java类中**满足一定的协议**就无需在JSP中编写多余的处理代码.

<br><br>

### 二、Stack Context和ValueStack：[·](#目录)

- 在学习OGNL语法之前先了解一下OGNL的实现原理.
  - **因为它和OGNL语法息息相关.**
- OGNL都可以访问哪些数据？主要有两类
  1. 上游action中的数据.
    - 其实这个早就已经用过了，如\<s:property value="name"/\>
    - 其中name就是上游action中的一个数据域，而value的值就是一个OGNL表达式，即\<s:property value="OGNL_expr"/\>
  2. 各种Servlet对象：reqeust、session、application等.
    - 毕竟太常用了.

> - 一般在struts标签中访问上游数据最好只用struts自己提供的标签和语法，毕竟兼容性好.
> - 虽然其它标签库也有提供访问上游数据的功能（EL表达式等），但struts提供的更好更安全.
> - 因此建议如果使用struts框架，那么在JSP中访问上游数据就**只用**strut提供的标签以及语法.

<br>

1. 那么这些OGNL可以访问到的数据是如何保存的呢？
  - 其实底层专门为OGNL创建了一个对象池**Stack Context**，对象池中存放着这些数据对象.
    - 其中Servlet对象会**全局**、**永久** 地存放.
      - 毕竟，这些对象会在几乎所有Servlet和JSP中用到，除非服务器停止.
    - 而上游action只能临时存放了.
      - 毕竟，一次请求响应完毕后就应该清除掉整个action链了.
  - 但不管怎么样，实际中它们都一拨儿全部放到了Stack Context这个OGNL对象池中.
2. 为什么叫**`Stack`** Context？
  - 那是因为所有这些OGNL对象（即OGNL可以访问到的对象的简称）是按照**栈的顺序**存放的.
  - 先看一个完整的请求响应过程，体会各数据对象的加载顺序（只是一个粗糙大致的顺序）：<br>
    application -> session -> request -> action1 -> action2 -> ... -> actionN -> JSP
    - 一般JSP中会大量访问的就是最近的那几个上游的action中的数据，而request、application这些对象的访问频率和次数并不会那么高.
    - 因此如果用栈来保存的话，最常用的那些对象（action中的数据）刚好放在栈顶，访问效率最高.
3. ValueStack：
  - Stack Context中最近（栈顶）的那几个上游action并不是独立存放的，而是一起打包放在ValueStack这个对象中.
  - 也就是说ValueStack是和application、request、session等平行（平起平坐）的OGNL对象，并且它一直处于Stack Context的栈顶.
  - 显然，ValuesStack也是一个栈的结构，action链也是按照栈的顺序保存在其中的.
    - 特别的是，action的数据并不是以action为单位组织的，而是一拨儿（按照纯加载顺序）部分彼此action进栈.

> 例如：action1中包含数据域name、id，action2中包含数据域passwd，action3的excute中额外往ActionContext中put了一个group对象.<br>
> 那么ValueStack对象中数据的入栈顺序就是name -> id -> passwd -> group.<br>
> 其中group处于ValueStack的栈顶.

<br>

- **Stack Context（OGNL Context）示意图：栈顶在上**

| 栈顶 |
| --- |
| ValueStack |
| request |
| session |
| application |

- 其中ValueStack的示意图：

| 栈顶 |
| --- |
| action3.group |
| action2.passwd |
| action1.id |
| action1.name |

- 可以在JSP页面中加入**`<s:debug/>`** 标签，然后在页面中点击**debug**按钮转入一个新的调试页面，查看Stack Context和ValueStack中的内容.
  - 即栈调试.

<br><br>

### 三、OGNL基本语法：根对象和数据访问（接上例）[·](#目录)

- **OGNL Context规定只能有一个根对象，唯一的那个根对象就是栈顶的ValueStack.**
  - 根对象中的数据可以直接访问：group、passwd
    - 当然也可以加关键字（top、ValueStack限定）：**top**.group、**ValueStack**.passwd
    - top就表示栈顶的ValueStack.
  - 但对于非根对象的访问就比较特殊了：
    - 例如request就不能直接一个request这样访问，因为会被解释成ValueStack.request.
    - 对于非根对象**必须使用\#作为前缀访问**，如#request.foo
      - 这就表示request不是根对象，必须到栈顶之下的其它对象中去找，而foo还是request的数据域.


- OGNL Context中各种对象的访问：只罗列一些常用的

| 访问语法 | 对象 |
| --- | --- |
| 直接访问 | ValueStack中的对象 |
| \#parameters | 请求参数 |
| \#request | requestScope |
| \#session | sessionScope |
| \#application| applicationScope |
| \#attr | pageContext，可以访问所有scope对象 |

<br><br>

### 四、集合操作：[·](#目录)

1. 创建List集合：{ e1, e2, e3, ... }
2. 创建Map集合：#{ key1:val1, key2:val2, key3:val3, ... }
3. OGNL只为集合提供了两个运算符：in、not in
  - 示例："foo" in { "foo", "bar" }
  - 当然写在JSP标签中就是：\<s:if test="'foo' in { 'foo', 'bar' }"\>
4. 除了运算符之外还提供了取子集的操作：
  - ?：取出符合条件的所有元素
  - ^：只取出符合条件的第一个元素
  - $：只取出符合条件的最后一个元素

> 使用以上三种操作OGNL表达式示例：person.friends.{? #this.gender == 'male'}
> - 整个{? #this.gender == 'male'}表达式是对前面的集合friends取子集操作.
> - { }中起始的?表示是何种类型的取子集操作.
> - \#this就代表集合里的元素，是一个迭代对象（对集合中每个元素进行迭代）.
> - \#this.gender == 'male'表示元素的gender是否等于'male'.
>
>
> **因此上面表达式的意思就是取出friends中所有性别为男的元素作为子集.**

<br><br>

### 五、访问静态成员：[·](#目录)

- OGNL允许访问Java类的静态成员，包括静态数据成员和静态方法.
- **前提**：开启访问静态成员的功能
  - struts.ognl.allowStaticMethodAccess=true
- 访问语法：
  1. 访问数据域：@className@staticData
  2. 访问静态方法：@className@staticMethod(var1, var2, ...)


- 示例：

```xml
<s:property value="@java.lang.System@getenv('JAVA_HOME')"/><br>
<s:property value="@java.lang.Math@PI"/>
```
