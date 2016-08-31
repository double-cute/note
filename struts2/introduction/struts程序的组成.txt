1.struts框架下Web应用的组成：

-应用根目录
 |__.jsp、.html
 |
 |__WEB-INF/
    |
    |__web.xml
    |
    |__lib/(里面包含了各种依赖的类库，其中最重要的就是Struts核心类库
    |
    |__classes/
       |
       |__struts.xml（Struts配置文件，里面规定了请求到action的映射关系）
       |
       |__各种Java类的包，其中最重要的当然是包含action的包



2.Eclipse下Struts应用该如何布置：

  a.Eclipse会默认将src目录下的所有源码编译出的内容在发布或者调试/运行时放到WEB-INF/classes目录下，所以：
    i.编写的各个Java类源码直接放在src目录下（要带包路径）
    ii.虽然struts.xml无需编译，但Eclipse还是遵循这个规则，就是src目录下的所有内容发布时默认加载到WEB-INF/classes下
    iii.所以struts.xml还是直接创建在src目录下
！！小结：记住，Eclipse下，src目录下的内容（目录结构）运行加载时直接对应WEB-INF/classes
！！Eclipse下的WebContent目录对应发布时的应用程序根目录

  b.非JRE/Web类库对应WEB-INF/lib目录：
    i.所有Web应用肯定都需要放在Web容器中运行，因此两者的关系是Web容器包含Web应用，因此Web应用的lib无需包含容器的核心类库
    ii.所以WEB-INF/lib不用存放Tomcat的核心类库
    iii.其次Tomcat、Web应用都依赖JRE运行，因此三者的关系是JRE包含Tomcat，Tomcat包含Web应用，因此Web应用的lib也无需包含JRE类库
    iv.JRE类库是放在整个操作系统的CLASSPATH中的
    v.因此WEB-INF只能存放除了上述两者之外自己需用到的类库：特别是Web应用自己用到Web框架类库（第三方类库，如Struts、Hibernate、Spring等），以及一些自己开发的类库
    vi.正是由于JRE和Tomcat属于“大环境”（所有应用都依赖），所以配置后无需再添加WEB-INF/的加载，而Struts只是应用程序级别的，属于“小环境”，所以在配置后还需要在Deployment中添加WEB-INF/加载才行
！！在上一章"struts安装和搭建.txt"讲过了
！！小结：记住，Eclipse中，只有Libraries下的user libraries在加载是才对应WEB-INF/lib

  c.由于src直接对应WEB-INF/classes，而web.xml应该直接放在应用程序根目录下，因此开发时不能将web.xml创建在src/，应该直接创建在WebContent/下，当然在创建工程的时候可以勾选自动创建web.xml选项直接自动帮你在WebContent中创建了
！在上一章"struts安装和搭建.txt"中讲过了


3.开发Struts程序：

  a.接着上一章，上一章中Struts环境已经搭建完毕

  b.在src中创建struts.xml，即Struts的配置文件，它规定了请求到action的映射，一般约定俗称的映射规则是xxx.action映射到XxxAction类，映射动作的执行是Struts核心Filter的职责

  c.接着创建XxxAction类，继承ActionSupport类（Struts API），是一个控制器，负责解析请求参数，并交由哪个model处理，处理结束后将结果返回给Struts核心Filter

  d.最后核心Filter根据返回结果决定调用哪个JSP页面给用户显示，哪个返回值映射到哪个JSP页面也是在struts.xml中指定的
！！小结：struts.xml其实就是一个声明action类的文件，不仅定义了请求到action的映射关系，而且也定义了该action的返回值到JSP页面的映射关系

  e.小结：在Struts搭建完毕之后的工作就是
    i.添加action以及业务处理model
    ii.在struts.xml中增加相应的action映射（包括返回值映射JSP）
    iii.添加JSP页面，选择哪个JSP页面显示给用户是由action的返回值决定的（JSP中可以使用Struts提供的标签）


4.StrutsTest示例在外面的目录中给出