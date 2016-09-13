# 管理bean的生命周期
> - ***只有singleton scope的bean的生命周期可以被Spring容器完整追踪.***
> - prototype scope的bean，Spring容器只负责创建，创建完交给业务逻辑后就完全交由JVM管理器生命周期.
>   - *就跟Java程序中的普通对象一样了.*

<br><br>

## 目录
1. [管理singleton bean生命周期的时机](#一管理singleton-bean生命周期的时机)

<br><br>

### 一、管理singleton bean生命周期的时机：[·](#目录)
- 只有两个时间点，分别是**依赖注入之后**和**bean销毁之前**.


- 依赖注入之后：
  - 两种方法：
    1. bean标签使用**init-method**属性指定一个bean类的实现方法.
    2. 直接让bean类实现Spring提供的InitializingBean接口，实现其中的方法void afterPropertiesSet()方法.
      - 只要Spring检测到bean类实现了该接口就会自动在设值之后调用该方法，无须在bean标签中使用init-method属性显式指定.
  - 如果两种方法同时用，则默认会先调用afteraPropertiesSet再调用init-method，实现时二选一即可.
  - **推荐使用init-method，这样代码不会和Spring框架耦合，低侵.**
  - **实现了ApplicationContextAware、BeanNameAware的bean，会在调用init之前完成对容器引用和bean id的注入！**


- bean销毁之前：
  - 两种方法：
    1. bean标签中使用destroy-method属性指定一个自定义的析构方法.
    2. 让bean类实现Spring提供的DisposableBean接口，其中的方法为void destroy().
  - 两者二选一即可，如果同时使用还是先调用destroy再调用destroy-method.
  - **推荐使用destroy-method，低侵.**
  - 测试的时候，如果是普通的Java Application，则需要让actx注册一个关闭钩才行：
    1. 因为singleton bean是和容器**同生死**的，必须要关闭容器才能关闭singleton bean.
    2. 如果是Web应用，则Web容器提供了优雅的方法保证可以在程序退出之前关闭Spring容器.
    3. 但普通Java Application则没有提供这样的功能，只能让Spring容器向JVM注册一个关闭钩显式关闭才行：

```java
actx.registerShutdownHook();
```

- **beans标签中提供了default-init-method和defualt-destroy-method属性**，如果其下的所有singleton bean都需要管理者两个时间点则可以在beans中设定.
