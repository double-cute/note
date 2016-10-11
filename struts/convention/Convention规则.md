1.Action自动搜索路径：

  a.在没有Convention之前，Struts需要根据struts.xml的配置信息找到action所对应的Java类（根据<action>的class属性）

  b.而Convention约定好会自动搜索位于action、actions、struts、struts2包下的所有Java类，然后再根据一定规则判断一个类是否是一个Action
*.例如：org.lirx.app.action包就会被自动搜索，至于其中的类org.lirx.app.action.InfoProc具体是不是Action则会根据其它约定进一步判断
*.当然还可以包含更长的路径：org.lirx.app.actions.user.InfoProc，可以在actions后包含其它子包


2.判断一个类是否是Action的标准、Action的name属性如何生成：

  a.是否是Action的标准：
    i.实现了con.opensymphony.xwork2.Action的类
    ii.类名以Action结尾的类

  b.name属性的生成规则：
    i.LoginAction -->  login
    ii.GetBooksAction  -->  get-books
    iii.MakeInfo（虽然不是以Action结尾，但是实现了xwork.Action接口）  -->  make-info
    iv.URL结尾当然是name.action咯

  c.识别到这些类就会自动在底层生成struts.xml中<action name="" class=""这两个内容


3.包和命名空间：

  a.命名空间：直接以包路径作为命名空间
    i.其中action、actions、struts、struts2作为根命名空间
    ii.例如：
org.lirx.actions.LoginAction  -->  /login.action
org.lirx.struts.books.getBook  -->  /books/get-books.action
org.lirx.struts2.salary.hr.AddEmployeeAction  -->  /salary/hr/add-employee.action
    iii.别忘了提交表单或者URL的时候加上命名空间哦！form action="salary/hr/add-employee"

  b.包也很方便，直接就是按照包路径规定：org.lirx.struts2.salary.hr.AddEmployeeAction中，add-employee.action的包是hr，hr的父包是salary，salary的父包是conventionDefualt
！conventionDefault是Struts提供的默认包，作为所有包的顶级父包，里面有很多拦截器等基础配置


4.result结果映射的约定：

  a.Convention默认到WEB-INF/content目录下定位物理资源，因此要应用Convention插件就必须将显示层内容都放到WEB-INF/content目录下

  b.映射约定：一个完整的例子
org.lirx.struts2.salary.hr.AddEmployeeAction：
先映射到WEB-INF/content/salary/hr/add-employee-resultCode.suffix
如果找不到该文件就再映射到WEB-INF/content/salary/hr/add-employee.suffix
    i.resultCode就是逻辑视图：success、error、input等
    ii.suffix就是物理视图的后缀：jsp、html、ftl、vm等，是由result type决定的，例如dispatcher就是jsp
    iii.小结：由于WEB-INF/content是“物理视图的更目录”，因此映射结果其实就是"/命名空间/name[-resultCode].suffix
*.上面的例子其实等价于（假如是success并且是jsp）：<result name="success">/salary/hr/add-employee.jsp</result>

  c.！！注意！WEB-INF/content必须与action的命名空间具有相同的目录结构哟！


5.Action链约定:

  a.action chain即Action处理链，一个action返回后并不是直接交给物理视图，而是forward到另一个action继续处理
！Action链也是一种forward方式，就不丢失request，同时也能保留上一个Action的Context

  b.Action链的result type是chain

  c.在Convention下可以方便实现Action链，无需配置，只要按照规定编写即可

  d.Action链的4约定：
    i.物理视图的命名必须是完整形式：name-resultCode.suffix，不能是简写name.suffix！！
    ii.前一个Action返回的逻辑视图不能有对应的物理视图，比如第一个FirstAction返回了"second"，那么在content中不能有first-second.suffix或者first.suffix
    iii.后一个Action必须和前一个Action处于同一包中
    iv.后一个Action的名称必须是FirstSecondAction，即PreviousName+PreviousResultCode
*.因此最终（加入第二个Action返回后就调用物理视图了）物理视图应该为：first-second-resultCode.suffix
