# Convention
> 约定，即一种严格的规范，只要struts程序按照这个规范编写就可以省略各种配置文件，大大简化工作量.<br>
> 但这种规范需要struts插件支持（Convention插件）.

<br><br>

## 目录
1. []()

<br><br>

### 一、Convention插件安装：
> 通过约定的规则消灭配置文件以减少工作量.

- Convention插件其实就是一组规范（约定）的集合.
  - 其运行原理并不是完全舍弃配置文件，而是自动根据规范集中的规范扫描应用源码并在运行时生成配置信息.
  - 毕竟，没有配置信息还是无法使struts正常运行的.
- 插件一旦启用，就无需编写任何配置文件以及Annotation，只要严格按照Convention定义的规范编写即可.
  - Convention插件定义的规范基本是开发界公认的，非常科学、合理、严谨.

<br>

- **安装部署：** 就是一个jar库，在struts项目的lib/下，struts2-convention-plugin.jar

<br><br>

### 二、action如何定位？

1. Convention默认自动搜索名为**action、actions、struts、struts2**包下的所有Java类，然后再根据一定规则判断是否是action.
  - 例如，org.lirx.app.action包就会被检测，至于其中的类org.lirx.app.action.InfoProc具体是不是action类则会根据其它约定进一步判断.
  - 搜索方式是**递归搜索**，不仅上述包中的类会被检测，其**子包也会被递归地彻底检测**.
2. 判断一个类是否是action类的标准：
  - 是否实现了con.opensymphony.xwork2.Action类 **或者** 类名以"Action"结尾.

<br><br>

### 三、action的名称和命名空间如何确定？

- **名称的确定规则**：通过action类的类名生成
  - 即action标签的name属性.
  - URL当然还是name.action才行.

| action类名 | action名 |
| --- | --- |
| LoginAction | login |
| GetBooksAction | **get-books** |
| MakeInfo（实现了xwork.Action接口）| **make-info** |

<br>

- **命名空间和包（package）的确定规则**：通过action的Java类的包路径生成
  1. 以action、actions、struts、struts2作为根命名空间.
  2. action包就是action类所在的那个Java包.

| action类名 | 命名空间 | action包 | 说明 |
| --- | --- | --- | --- |
| org.lirx.actions.LoginAction | / | conventionDefualt | 是Convention插件提供的顶级抽象包，作用类似于default-package |
| org.lirx.struts.books.GetBook | /books | books | books的父包是conventionDefualt |
| org.lirx.struts2.salary.hr.AddEmployeeAction | /salary/hr | hr | hr的父包是salary，salary的父包是conventionDefualt |

<br><br>

### 四、action结果映射的约定：
- Convention插件规定表现层资源（即各种JSP、HTML等）要放在**WEB-INF/content/** .
- 映射规定：物理视图的路径和名称不能乱写，必须根据action类名确定.
  - 以下例子说明了Convention是如何根据action类名找对应的物理视图的.

    对于名为org.lirx.struts2.salary.hr.AddEmployeeAction的action.
      1. 先找**WEB-INF/content/**salary/hr/add-employee**-resultCode**.**suffix**
      2. 如果找不到，就找WEB-INF/content/salary/hr/add-employee.suffix

- 其中：
  1. resultCode就是逻辑视图：success、error、input等.
  2. suffix就是物理视图的文件后缀：jsp、html、ftl、vm等，由result type决定（dispatcher、plainText等）.
- 小结：
  1. WEB-INF/content/是表现层资源的根目录.
  2. action类名到物理视图映射的一般规则是：别忘了，如果有命名空间**要加上命名空间**<br>
  WEB-INF/content/**${action-namespace}**/${action-name}**[-${resultCode}]**.${suffix}

<br><br>

### 五、action链：

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
