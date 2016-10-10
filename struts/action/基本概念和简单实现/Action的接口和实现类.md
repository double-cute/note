# action的接口和实现类
> - struts自己提供了一个action接口，用来规范action的行为.
> - 同时struts自己按照该接口的标准提供了一个实现类ActionSupport，功能完备，方便开发人员快速开发Action.

- 实际应用中很少能做到action是纯POJO，或多或少会用到一点struts API以及Web容器API（即Servlet API），除非是一些深度定制的应用.

<br>

- **Action**：struts提供的action接口，定义了一些action通用规范：
  1. 定义了5个逻辑视图，都是String，分别是ERROR、INPUT、LOGIN、SUCESS、NONE，对应各自的字符串"error"、"input"...
    - 最早的时候各个开发商返回的逻辑视图非常纷乱，有"welcome"等，没有一个统一，所以struts干脆就给出了这个统一的规范.
  2. 规定了一个excute方法，即public String execute() throws Exception;作为action的默认控制逻辑

<br><br>

- **ActionSupport**：struts提供的action实现类，实现了Action接口
  - 一个良好的action不仅仅需要Action接口的规范，还需要考虑到其它更多的细节，struts自己实现的ActionSupport就非常完备.
    1. 该类已经帮助开发者实现了很多细节，因此就不需要开发者操心这些问题了.
      - 其实就是一个适配器思想.
    2. 用户开发的action通常会继承ActionSupport类
  - 但是ActionSupport本身就已经非常完备了，完全可以作为一个真正的action使用（只不过excute永远返回SUCCESS而已）.
    - 如果action配置中（struts.xml）不指定class属性（即action的实现类），则默认使用ActionSupport类作为实现类.
