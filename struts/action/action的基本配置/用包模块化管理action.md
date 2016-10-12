# 用包模块化管理action
> 这里的包要和Java包相区别，严格来说应该称为**action包**，这里就简称包了.

<br><br>

## 目录
1. [如何用包来组织action](#一如何用包来组织action)
2. [package标签的4属性详解](#二package标签的4属性详解)
3. [关于namespace带来的效果](#三关于namespace带来的效果)

<br><br>

### 一、如何用包来组织action：[·](#目录)
- 在struts.xml中：使用package标签

```xml
<package name="action包名" extends="继承了哪个action包" namespace="当前action包的命名空间" abstract="[true/false]是否是抽象包">
    <action>
        ...
    </action>
    <action>
        ...
    </action>
    ...
</package>
```

- 原则：
  1. 应该将类型和功能相似的一组action都放在同一个包中，包就是对action的模块划分.
  2. 之前介绍过，可以为每个包编写一个struts标准配置文件，最后将这些包配置include到struts.xml中.
    - 这样可以模块化管理action配置文件，不会使struts.xml过于臃肿

<br><br>

### 二、package标签的4属性详解：[·](#目录)
1. name：
  - action包名.
  - 是自定义的，名字要尽量取的有意义，能高度概括包中action的作用.
2. extends：
  - 继承另一个包.
  - 一旦继承，那么子包可以继承到父包的拦截器、action等配置，是一种典型的分层模块化管理的思想.
  - 不是Java中的继承哦！
    - struts提供了一个默认的action包struts-default，该包中已经定义了很多拦截器、拦截器栈等基础配置，里面考虑到了很多细节.
    - 因此**一般项目中最最基础、顶级的包应该继承该包，然后其它包再继承这些基础包**.
3. namespace：
  - action包的命名空间.
    - 考虑到项目如果庞大，难免遇到包也重名的问题，因此再为包规定一个命名空间.
4. abstract：
  - [true/false]，该包是否是一个抽象包.
  - 即高度抽象的包，**不能** 定义action.
    - **struts-default就是一个抽象包.**


- **以上4个属性只有name是必选属性，其它几个默认值分别是"struts-defualt"、"/"、"false".**

<br><br>

### 三、关于namespace带来的效果：[·](#目录)
1. 命名空间的**第一个字符必须是/** ，表示一个根路径.
2. 调用action的URL**必须包含命名空间**：/Web根路径/namespace/xxx.action
  - 例如，应用Test的login.action位于"/books"命名空间下，那么调用该action的URL应该为：<br>
  http://localhost:8888/Test/books/login.action


- 其默认值是/，表示Web应用的根路径.
- struts搜索action的规则：以/barspace/bar.action为例
  1. 先找/barspace命名空间中是否有bar.action，如果有就用.
  2. 否则就到默认的/中去查找，再找不到就抛出异常.


- **注意**：namespace中**不要包含二级路径**，如namespace="/service/search"
  - 因为struts规定命名空间**只能有一个级别**.
    - 例如，url(/service/search/get.action).
    - 则会先在/search空间下找，如果没有就直接跳到默认的/下找，而不会到/service里找了！！
