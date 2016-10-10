# struts程序的基本组成

<br><br>

## 目录
1. [struts Web应用的基本组成]()
2. [Eclipse是如何管理Web应用程序的：源码到发布]()
3. [一个简单struts程序：struts程序的基本开发流程]()

<br><br>

### 一、struts Web应用的基本组成：

        -WebContent
          -jsp, html（表现层）
          -WEB-INF/
            -web.xml
            -lib/（应用程序的各种依赖库，包括struts库）
            -classes/（应用程序运行时组件）
              -struts.xml（配置Action）
              -各种Java类（包），其种包含Action

<br><br>

### 二、Eclipse是如何管理Web应用程序的：源码到发布  [·](#目录)
- Eclipse会默认将src目录下的所有**源码编译出的内容**在**发布或者调试/运行时**对应到**WEB-INF/classes/** 目录下，因此：
  1. 编写的各个Java类源码要放在src目录下（要带包路径），这就不用多说了.
  2. 虽然struts.xml无需编译，但Eclipse还是会遵循这个管理规则，所以**struts.xml要直接创建在src/目录下**.
    - **小结**：记住，Eclipse下，src/目录下的内容（目录结构）运行加载时直接对应到WEB-INF/classes/
    - 而Eclipse下的**WebContent/** 目录对应发布时的**应用程序根目录**.
- 应用中使用的**Eclipse全局类库**在发布运行时对应到**WEB-INF/lib/** 目录：
  - *JRE和Tomcat是整个Web应用载体，发布时无需将其放在WEB-INF/lib/中，应该将它们的路径放在OS的CLASSPATH中，程序中所有依赖这两个库的地方都是直接通过CLASSPATH来访问它们的.*
  - 应用程序中所有用到的类库：JRE、Server、User Libraries都会全部显示在应用程序根节点中.
    - 都会以一叠图书的图标来示意.
    - 但心里要明白，只有User Library会在运行时放到WEB-INF/lib/下，其余都是应用程序的载体，放在这里只是提醒开发者，程序用到了JRE和Server.
- web.xml只能直接创建在WebContent/下：
  1. 首先不能放在src/下，会对应到WEB-INF/classes/下，这显然不是它该出现的位置.
  2. 在通过Eclipse创建工程的时候可以勾选自动创建web.xml功能，Eclipse会自动帮你在WebContent中创建.
- 小结：Eclipse管理Web应用程序的开发，其中[root]代表发布时的应用程序根目录名称

| 源码 | 发布时对应到 | 说明 |
| --- | --- |
| WebContent/ | 应用程序根目录 | 如果工程名为StrutsTest，那么发布时就会变成StrutsTest，**应该包含web.xml** |
| src/ | [root]/WEB-INF/classes/ | 存放应用程序运行时组件，**应该包含struts.xml** |
| [User Library] | [root]/WEB-INF/lib/ | 存放程序运行时依赖库（**非载体库**）|

<br><br>

### 三、一个简单struts程序：struts程序的基本开发流程  [·](#目录)
> 接着上一章，在上一章中struts环境已经搭建完毕.

- struts程序的基本开发步骤：
  1. 搭建struts开发环境（上一章的内容）.
  2. 设计action，并添加action和相应的业务处理model.
  3. 在struts.xml中配置添加的action，并指定action到物理视图的映射.
  4. 设计并添加相应的物理试图（jsp等）.

- 完整的程序在外面的[StrutsTest]()中，文件结构就是Eclipse中的开发结构.
