# action的低侵设计理念
> 从现在开始，规范action的叫法.<br>
> **action指action这个组件，而Action指Action接口或类.**

<br><br>

## 目录
1. [action在struts流程中扮演的角色](#一action在struts流程中扮演的角色)
2. [aciton的低侵入式设计](#二aciton的低侵入式设计)
3. [action的设计规范](#三action的设计规范)

<br><br>

### 一、action在struts流程中扮演的角色：[·](#目录)
- 回顾整个处理流程：
  1. struts核心filter将请求转发给action.
  2. action接受请求参数并解析（在excute方法中进行）.
  3. 根据解析的结果调用相应的model进行业务处理.
  4. 最后根据业务处理的结果决定将指定的逻辑视图返回给核心filter.

- **注意**：
  1. 逻辑视图不是真正的jsp页面，而是一个**字符串代码**，比如"success".
  2. 而核心filter根据struts.xml中action的逻辑视图和物理视图的对应关系决定调用哪个物理视图.
  3. 物理视图就是真正的jsp页面，struts.xml中指明了这种映射关系：

```xml
<result name="success">welcome.jsp</result>
```

- 可以看到action实际上就是请求、处理、显示结果三者之间的调度员和协调员.
  - 而struts的核心就是action.
  - 而"struts"这个英文单词的释义就是“支架”，非常形象，即MVC支架.

<br><br>

### 二、Aciton的低侵入式设计：[·](#目录)
> 从action所扮演的角色来看，它在实现上应该和struts框架以及Web容器没有什么直接的关系，即**无须**调用struts以及Web容器的API来实现Action.
>
> - 但在很多情况下还是需要调用这些API，特别是Servlet的session、request等，但这里引出了一个设计思想，那就是低侵设计.

- 首先，action完全可以是一个彻彻底底的POJO（Plain Ordinary Java Object），即普通Java类.
  - 完全可以不依赖任何struts或者Web容器的API来实现.
  - 但如果实际中需要用到request、session等那就没办法了.
- 也就是说action只是struts规范中的一个必须的组件，但它完全可以是一个普通Java类，不依赖框架和Web容器来实现.
- 这就是**低侵设计理念**，它的好处是：
  1. **完全和框架解耦**.
  2. 方便扩展和维护.
  3. 可以脱离框架进行测试（毕竟是POJO）.

<br><br>

### 三、action的设计规范：[·](#目录)
> 即使可以是POJO，但还是有一定要求的.

1. 必须为请求参数设定数据域以及getter和setter（用以接受和访问请求参数）：
  - 核心filter会回调Action的getter来使Action捕获请求参数.
  - 物理视图（jsp）可以访问Action中任何有getter和setter的属性.
    1. action返回后会继续交由具体的JSP物理视图完成剩下的响应，而物理视图可以访问上游action中的任何具有getter和setter的数据域.
    2. 因此action里还可以包含数据处理结果，然后供物理视图显示：例如private String tip; //保存model处理的结果
    3. 在物理视图中的访问方法：\<s:property value="tip"/\>
      - Java会通过反射机制调用getTip()得到上游action中的属性.
      - property中不仅包含上游action的内容也包含请求参数，例如：<br>
      \<s:property value="name"/\>  #假设请求参数中包含?name=Peter，这里的结果就是Peter这个字符串
2. 老版本struts还要求必须至少有一个excute方法，但现在也不需要了.
  - 可以在struts.xml配置action的method方法，根据method调用action中的特定方法来处理请求.
