1.Action的作用：

  a.作用：
    i.Struts核心filter将请求发给Action
    ii.那么Action首先需要接受请求参数
    iii.接着应该是解析请求参数
    iv.根据解析的结果调用相应的model进行处理
    v.最后将逻辑视图返回给核心filter
！！逻辑视图不是真正的jsp页面，而是一个字符串代码，比如"success"
！！而核心filter根据struts.xml中action的逻辑视图和物理视图的对应关系决定调用哪个物理视图
！！物理视图就是真正的jsp页面，struts.xml中指明了这种对应关系<result name="success">welcome.jsp</result>

  b.那么从整个Action的作用来看它在实现上应该和Struts框架以及Web容器没有什么直接的关系，即无需调用Struts以及Web容器的API来实现Action
！！通常情况下还是需要调用这些API，比如Servlet的session、request可能还是需要经常用到的，但这里引出了一个设计思想，那就是低侵设计


2.低侵入式设计：

  a.从上面的作用可以看出Action可以完全是一个彻彻底底的POJO（Plain Ordinary Java Object），即普通Java类，它完全可以不使用任何Struts或者Web容器API来实现
！！如果实际中完全不需要用到request、session等

  b.实际上Struts框架API确实不包含Action，Action允许是POJO，但如果需要用到Struts提供的一些方便的功能的话（比如继承ActionSupport、调用Servlet API等），这些功能就属于Struts框架的范畴了
！也就是说Action只是Struts规范中的一个必须的组件，但它并不包含在Struts类库（API）中，因为ACTION完全可以是一个普通Java类

  c.这就是低侵设计，它的好处是可以完全和框架解耦，提高开发安全性和高效性，同时测试的时候可以完全脱离框架测试（因为完全可以是一个POJO），这是一个非常优秀的设计

  d.但是即使可以是POJO，Action还是有一定要求的：
    i.必须为请求参数设定数据域以及getter和setter
    ii.老版本Struts还要求必须至少有一个excute方法，但现在也不需要了，可以在struts.xml配置action的method方法，根据method调用Action中的特定方法来处理请求


3.物理视图可以访问Action中任何有getter和setter的属性：

  a.action返回后会继续交由具体的JSP物理视图完成剩下的响应，而物理视图可以访问上游action中的任何具有getter和setter的数据域

  b.因此action里还可以包含数据处理结果，然后供物理视图显示：例如private String tip; //保存model处理的结果
！但必须提供getter和setter

  c.在物理视图中访问：<s:property value="tip"/>
    i.通过property标签访问，property代表的是上游action的属性，属性名由value给出
    ii.然后通过Java的类反射机制调用相应名称的getter和setter，所以名字和数据域的名称无关，之和setter和getter名称有关
    iii.同样可以访问请求参数，请求参数同样也保留在action中：<s:property value="name"/>  #加入请求参数中包含?name=Peter的话
