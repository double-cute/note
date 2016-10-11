1.约定——消灭配置文件：

  a.Struts中的各种映射（mapping），如action名称映射、逻辑视图到物理视图的映射、异常映射等等都需要在struts.xml中配置
！这就导致Struts的配置非常繁琐，没编写一个组件、单元可能都需要到各种配置文件中进行配置

  c.Convention插件——消灭繁琐的配置：
    i.convention即约定的意思，就是一切规则都遵循一种严格的规范，Struts中所有代码的别写只要都遵循这种统一的规范就无需进行任何配置
    ii.Convention插件其实就是一组规范（约定）的集合
    iii.毕竟Struts底层还是需要各种配置文件才能运行的
    iv.只不过Convention插件可以自动按照统一公认的规则自动生成各种配置文件的内容，而你实际编写的代码都是要严格符合这些规则的
    v.一旦使用了Convention插件就不需要使用struts.xml文件了，甚至连Annotation也可以省略

  d.安装Convention插件：在Struts2项目下，struts2-convention-plugin.jar库，将其放入WEB-INF/lib中即可
