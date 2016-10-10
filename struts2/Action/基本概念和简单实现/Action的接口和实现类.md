1.Action接口和ActionSupport类：

  a.实际应用中很少能做到Action是纯POJO，或多或少还是会用到一点Struts API以及Web容器API（即Servlet API的），除非是一些深度定制的应用

  b.Struts提供了Action接口，该接口提供了一个Action的实现规范：
    i.定义了5个逻辑视图，都是String，分别是ERROR、INPUT、LOGIN、SUCESS、NONE，对应各自的字符串"error"、"input"...
！因为最早的时候各个开发商返回的逻辑视图非常纷乱，有"welcome"等，没有一个统一，导致混乱的局面，所有Struts干脆就给出了这个统一的规范
    ii.规定了一个excute方法，即public String execute() throws Exception;作为控制器的默认控制逻辑

  c.但是实现一个良好的Action不仅仅需要Action接口的规范，还需要考虑到其它更多的细节，还好Struts也提供了一个实现了Action接口的类ActionSupport
    i.该类已经帮助开发者实现了很多细节，因此就不需要开发者操心这些问题了
    ii.其实就是一个适配器思想
    iii.因此用户开发的Action通常会继承ActionSupport类
    iv.但是ActionSupport类本身就已经非常完备了，完全可以作为一个真正的Action使用（只不过excute永远返回SUCCESS而已，因此ActionSupport就是Action的一个默认实现）
！因此，如果action配置中（struts.xml）<action>标签不指定class属性，则默认使用ActionSupport类作为处理器
