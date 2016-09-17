# 往bean中注入其它bean的成员
> *这里只讨论三种，注入其它bean的getter值、注入其它bean的数据域、注入其它bean的方法返回值.*

<br><br>

## 目录
1. [Spring提供的注入其它bean的成员的工厂类](#一spring提供的注入其它bean的成员的工厂类)
2. [功能以及语法](#二功能以及语法)

<br><br>

### 一、Spring提供的注入其它bean的成员的工厂类：[·](#目录)
- 都是实现了FactoryBean工厂类的：
  1. PropertyPathFactoryBean：注入getter值.
    - 由于getter都是通过一串路径调用的，比如person.son.age（最后调用的是getAge()）.
    - 同时getter域也称为属性域，因此取名为PropertyPath，即属性路径.
  2. FieldRetrievingFactoryBean：注入数据域
    - 属性域是getter，而数据域就是裸的数据成员，比如private int age;
  3. MethodInvokingFactoryBean：注入方法返回值.

<br><br>

### 二、功能以及语法：[·](#目录)
- 上述三种工厂类可以完成两种任务：
  1. 把目标作为一个值注入到其它bean中，称为“导出为值”.
  2. 直接把目标导出为一个独立的bean，称为“导出为bean”.
  3. 其实导出为值是导出为bean的一种特殊情形，值就是没有id的bean而已！！


**1. 导出为bean的语法：**

```html
<bean id="导出bean的id" class="上述工厂类">
    <property name="工厂类的属性1（指向依赖bean的类名）" value="依赖bean的类名（全限定）"/>
    <property name="工厂类的属性2（指向依赖的成员）" value="依赖的成员"/>
</bean>
```

- getter值：targetBeanName和propertyPath

```html
<bean id="son1" class="org.lirx.app.user.Person">
    <property name="age" value="15"/>
</bean>
<bean id="son1age" class="org.springframework.beans.factory.config.PropertyPathFactoryBean">
    <property name="targetBeanName" value="son1"/>
    <property name="propertyPath" value="age"/>
</bean>
<!-- son1age = son1.age -->
```

- 数据域：非为静态和非静态，静态使用targetClass，非静态使用targetObject（就是指定一个bean id）
- 静态：类名必须是全限定类名

```html
<bean id="aStaticFieldBean" class="org.springframework.beans.factory.config.FieldRetrievingFactoryBean">
    <property name="targetClass" value="java.sql.Connection"/>
    <property name="targetField" value="TRANSACTION_SERIALIZABLE"/>
</bean>
<!-- aStaticFieldBean = java.sql.Connection.TRANSACTION_SERIALIZABLE -->
```

- 非静态：注意用ref引用另一个bean

```html
<bean id="anOtherBean" ...>...</bean>
<bean id="aFieldBean" class="org.springframework.beans.factory.config.FieldRetrievingFactoryBean">
    <property name="targetObject" ref="anOtherBean"/>
    <property name="targetField" value="HASH_CODE"/>
</bean>
<!-- aFieldBean = anOtherBean.HASH_CODE -->
```

- 方法返回值：静态使用targetClass/value，非静态使用targetObject/ref，指定方法用targetMethod

```html
<!-- 静态 -->
<bean id="aStaticMethodBean" class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
    <property name="targetClass" value="org.lirx.app.util.ValueGenerator"/>
    <property name="targetMethod" value="getStaticValueInt"/>
</bean>
<!-- aStaticMethodBean = static org.lirx.app.util.ValueGenerator.getStaticValueInt(); -->

<!-- 非静态 -->
<bean id="anOtherBean" ...>...</bean>
<bean id="aMethodBean" class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
    <property name="targetObject" ref="anOtherBean"/>
    <property name="targetMethod" value="getInt"/>
    <!-- 使用property name="arguments"标签还可以为方法传参 -->
        <property name="arguments">
            <value>15</value>
            <list>
                <value>lala</value>
                <value>jiji</value>
            </list>
        </property>
</bean>
<!-- anOtherBean = anOtherBean.getInt(); -->
```


**2. 导出为值的语法：**

- 就是上面的语法中**去掉id**，没有id的bean其实就是一个值咯！

**3. 导出为值的简洁语法：但只适用于属性域和数据域，不适用于方法返回值**

```html
<bean id="目标bean的成员路径（如果是静态属性则必须是全限定路径）" class="上述工厂类"/>
```

- **注意：** 这里的id不能理解成“导出bean的id”，这几个工厂类的处理逻辑是，如果没有传入property，就会把id解析成property！


```html
<!-- 属性域 -->
<property name="age">
    <bean id="person.son.age" class="org.springframework.beans.factory.config.PropertyPathFactoryBean"/>
</property>

<!-- 静态数据域 -->
<property name="age">
    <bean id="java.sql.Connection.TRANSACTION_SERIALIZABLE" class="org.springframework.beans.factory.config.FieldRetrievingFactoryBean"/>
</property>
```
