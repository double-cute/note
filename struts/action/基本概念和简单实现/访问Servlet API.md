# 访问Servlet API
> 说到底action还是要经常用到Servlet API的，特别是request、application、reponse、session等对象.<br>
> 访问方式分为两种，分别是**直接访问（ServletActionContext）**和**间接访问（ActionContext）**.

<br><br>

## 目录
1. [直接访问和间接访问的区别](#一直接访问和间接访问的区别)
2. [直接访问：ServletActionContext](#二直接访问servletactioncontext)
3. [间接访问：ActionContext](#三间接访问actioncontext)

<br><br>

### 一、直接访问和间接访问的区别：[·](#目录)
1. 直接访问：
  - 直接获得Servlet对象（ServletContext、PageContext、HttpServletRequest、HttpServletResponse等）.
  - 获取后就跟普通的Servlet编程没任何区别了.
  - struts提供的API，静态类**ServletActionContext**就可以实现直接访问.
2. 间接访问：
  - action在内存中有自己的一片**缓存空间**，叫做**ActionContext**.
  - 在该缓存中保存了**大量资源的链接**（比如各种Servlet对象）以便其迅速访问和利用.
  - 只不过这些资源并**非“裸”** 的（即原封不动的PageContext、HttpServletRequest等），而是对其进行了进一步包装，使其更加方便易用.
  - ActionContext将这些Servlet API以及其它资源包装成更加简易高效的数据结构（如Map等）.
  - 通过它们可以间接地访问Servlet API.


- **两者的优缺点比较：**
  - 直接访问：简单暴力，但会使代码和框架耦合，不方便扩展，可以访问response.
  - 间接访问：解耦，方便扩展，但无法访问response.

<br><br>

### 二、直接访问：ServletActionContext[·](#目录)
> 是一个struts提供的静态工具类，直接暴力获取Servlet对象.

| 静态方法 | 返回类型 | 对应的内置对象 |
| --- | --- | --- |
| getServletContext(); | ServletContext | application |
| getPageCotnext(); | PageContext | pageContext |
| getRequest(); | HttpServletRequest | request |
| getResponse(); | HttpServletResponse | response |

- 获取之后就是普通的Servlet编程了.

<br><br>

### 三、间接访问：ActionContext[·](#目录)
1. 第一步是通过ActionContext的静态方法getContext获取**当前**的action的专有缓存：<br>
static ActionContext ActionContext.getContext();
2. 然后通过ActionContext对象的各种getXxx方法获取Servlet的包装器对象：

| 方法 | 返回类型 | 对应的内置对象 |
| --- | --- | --- |
| getApplication(); | Map | application |
| getSession(); | Map | session |
| this本身 | 内部是一个Map结构 | 保存了request |
| getParameters(); | Map | 请求参数 |

- 示例：

```java
ActionContext ac = ActionContext.getContext();
ActionContext request = ac;
Map session = ac.getSession();
Map application = ac.getApplication();
Map params = ac.getParameters();  // put、get直接操作请求参数
// 以上的Map都是Map<String, Object>泛型的，添加键值使用put，获取使用get，例如：
session.put("name", "Peter");
application.put("count", count);
session.get("name");
```
