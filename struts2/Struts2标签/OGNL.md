# OGNL

## 目录


### 一、什么是OGNL：[·](#目录)

1. 是Strut2内建的一种数据访问语言：Object Graph Navigation Language.
2. 其目的是为了提供一种在JSP中简易访问上游Action环境中数据的语法，只能在Strut2标签中使用（标签<br>
属性值等）
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
> 可以看到OGNL更加简洁

* 而上面的person就是OGNL的**`根`**对象，因为可以通过该对象搜索到一切包含在其中的数据，就像树根.
* 虽然看上去OGNL只比Java调用少了get、()这几个字符，但是OGNL最牛叉的功能是不仅可以访问String类型<br>
数据还可以访问**任意**Java类型，只需要在Java类中**满足一定的协议**就无需在JSP中编写多余的处理代码.

<br><br>

### 二、Stack Context和ValueStack：[·](#目录)

* 在学习OGNL语法之前先了解一下OGNL的实现原理，以为它和OGNL语法息息相关.
* OGNL都可以访问哪些数据？主要有两类
  1. 上游Action中的数据：其实这个早就已经用过了，例如<s:property value="name"/>，其中name就是上游<br>
  Action中的一个数据域，而value="..."的值就是一个OGNL表达式，即<s:property value="OGNL_expr"/>
  2. 各种Servlet API对象：reqeust、session、application等，因为这些毕竟太常用了
  
> 一般在Struts2标签中访问上游数据最好只用Strut2自己提供的标签和语法，毕竟兼容性好<br>
> 虽然其它标签库也有提供访问上游数据的功能（EL表达式等），但是Struts2提供的更好更安全<br>
> 因此建议如果使用Strut2框架，那么在JSP中访问上游数据就是用Strut2提供的标签以及语法

<br>

1. 那么底层是这些OGNL可以访问到的数据如何保存呢？
  * 其实底层专门为OGNL创建了一个对象池，对象池中存放着上述可以访问到的对象.
    * 其中Servlet API会永久存放（毕竟，这些对象会在几乎所有Servlet和JSP中都用到），除非服务器停止.
    * 而对于上游Action对象即只能临时存放了（一次请求相应完毕后就清除整个Action链）.
    * 但不管怎么样，它们都一拨儿全部放在Stack Context这个OGNL对象池中.
2. 为什么叫**`Stack`** Context？
  * 那是因为所有这些OGNL对象（即OGNL可以访问到的对象的简称）是按照一定**顺序**存放的.
  * 这个顺序就是对象**加载**的顺序，但是顺序结构的容器还有很多，List等也是顺序结构，可为什么就用<br>
  stack呢？
    * 原因是这样的，先看一个完整的请求相应过程中各个对象的加载顺序（只是一个粗糙大致的顺序）：<br>
    application -> session -> request -> action1 -> action2 -> ... -> actionN -> JSP
    * 一般JSP中会大量访问的就是最近的那几个上游的Action中的数据，而request、application这些对象<br>
    访问频率和次数并不会那么高.
    * 因此如果用stack保存这些对象的话，那么最常用的那些对象（Action中的数据）刚好放在栈顶，<br>
    访问最快速高效.
3. ValueStack：
  * Stack Context中最近（栈顶）的那几个上游Action对象并不是分别独立存放的，而是全部都放在ValueStack<br>
  这一个对象中.
  * 也就是说ValueStack是和application、request、session等平行（平起平坐）的OGNL对象，并且它一直处于<br>
  OGNL Context（就是Stack Context，叫OGNL Context更加贴切）的栈顶.
  * 可以看出来ValueStack对象也是一个stack结构，很显然，上游Action链也是按照FILO顺序进入ValueStack的.
  * 只不过所有Action的Context中的数据（对象）都不是以Action为单位组织的，而是一拨儿（按照纯加载顺序）<br>
  进栈.
  
> 例如：action1中包含数据域name、id，action2中包含数据域passwd，action3的excute中额外往ActionContext中<br>
> put了一个group对象，那么ValueStack对象中数据的入栈顺序就是name -> id -> action -> passwd -> group，<br>
> 其中group处于ValueStack的栈顶.

#### Stack Context（OGNL Context）示意图：栈顶在上
| 栈顶 |
| --- |
| ValueStack |
| request |
| session |
| application |

其中ValueStack的示意图：

| 栈顶 |
| --- |
| action3.group |
| action2.passwd |
| action1.id |
| action1.name |

* 可以在JSP页面中加**`<s:debug/>`**标签，然后在页面中点击**debug**按钮转入一个新的调试页面，查看Stack Context<br>
和ValueStack中的内容

<br><br>

### 三、OGNL基本语法：根对象和数据访问（接上例）[·](#目录)

* **OGNL Context规定只能有一个根对象，唯一的那个根对象就是栈顶的ValueStack.**
  * 访问根对象中的数据就直接（例如）：top.group或者ValueStack.passwd
    * top和ValueStack可以直接省略（默认就使用top、ValueStack作为前缀）：group、passwd
  * 但对于非根对象的访问就比较特殊了，例如request就不能直接一个request这样访问，以为直接<br>
  一个request会被解释成ValueStack.request的，而这显然是错误的.
    * 因此对于非根对象必须使用#作为前缀访问，例如#request.foo，这就表示request不是根对象，<br>
    即不是ValueStack，必须到栈顶之下的其它对象中寻找，而foo则是request的数据域
    
> 即#前缀表示非根对象

* OGNL Context中各种对象的访问：值罗列一些常用的

| 访问语法 | 对象 |
| --- | --- |
| 直接访问 | ValueStack中的对象 |
| \#parameters | 请求参数 |
| \#request | requestScope |
| \#session | sessionScope |
| \#application| applicationScope |
| \#attr | pageContext，可以访问所有scope对象 |

<br><br>
