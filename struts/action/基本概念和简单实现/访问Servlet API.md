1.Action访问Servlet API的两种方式：

  a.说到底Action还是要经常用到Servlet API的，特别是request、application、reponse、session等

  b.两种访问方式：ActionContext和ServletActionContext都是Struts API
    i.间接访问：通过ActionContext工具类
！得到的request、application等都不是真正的Servlet中的对象，而是他们保存在Action上下文中的副本，但底层会和真正的Servlet对象交互
    ii.直接访问：通过ServletActionContext工具类
！得到的是真正的Servlet对象
    iii.间接访问解耦性更强，但不能获取response对象，直接访问会导致action和Servlet耦合性更强，但可以访问reponse
！！因此，抉择是不需要response时间接访问，需要response时采用直接访问


2.ActionContext间接访问：

  a.第一步是通过静态方法getContext获取当前Action的上下文环境：static ActionContext ActionContext.getContext();

  b.得到的ActionContext对象类似于Servlet中的ServletContext、PageContext、HttpSession等，都是上下文环境
！只不过它属于当前Action的上下文环境
！！本质还是一个类似于Map的结构，存放Action的各种属性值

  c.接着利用得到的ActionContext对象获取各种Servlet间接对象：
    i.Map getApplication();  //获取application对象
    ii.Map getSession();    //获取session对象
    iii.而ActionContext对象本身也是一种Map对象，它直接代表request对象！！
    iv.Map getParameters();  //获取请求参数Map

  d.注意！以上三者得到的并不是ServletContext、HttpSession等对象，而是Map！！
    i.这说明得到的并不是Servlet对象的直接句柄
    ii.而是经过包装的Servlet对象的副本，而且被包装成了Map对象（比原来的ServletContext、HttpSession等更加容易操作）
    iii.只不过对这些副本的操作最终会在底层对Servlet的真正对象进行更新（两者之间有交互）

  e.示例：
ActionContext ac = ActionContext.getContext();
ActionContext request = ac;
Map session = ac.getSession();
Map application = ac.getApplication();
Map params = ac.getParameters();  // put、get直接操作请求参数
！以上的Map都是Map<String, Object>泛型的，添加键值使用put，获取使用get，例如：
session.put("name", "Peter"); application("count", count); session.get("name");


3.ServletActionContext直接访问：

  a.它是一个静态工具类，直接暴力获取Servlet中的直接对象：
getServletContext();  // application
getPageCotnext();    // pageContext
getRequest();     // request
getResponse();    // response
！！获取相应对象后，操作就和Servlet编程完全一样了，类型也是对应一致的（ServletContext、PageContext、HttpServletRequest、HttpServletResponse）
