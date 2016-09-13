# bean的依赖注入
> bean的**属性**（通过setter设值）以及**构造器参数**（构造注入）都视为bean的依赖.<br>
> *通俗地将就是bean的初始化值.*<br>
> 可以为bean注入各种类型的依赖：***普通值、集合、数组、其它bean*** 等等.

<br><br>

## 目录
1. [注入依赖所使用的各种标签](#一注入依赖所使用的各种标签)
2. [注入普通值：value](#二注入普通值value-)
3. [注入其它bean：ref](#三注入其它beanref-)
4. [注入嵌套bean：bean](#四注入嵌套beanbean-)
5. [注入集合：list、set、map、props](#五注入集合listsetmapprops-)
6. [父子bean的集合合并](#六父子bean的集合合并)
7. [注入组合属性](#七注入组合属性)
8. [往singleton bean中注入prototype bean](#八往singleton-bean中注入prototype-bean实现注入-)

<br><br>

### 一、注入依赖所使用的各种标签：[·](#目录)
- 都是property标签的子标签，用于给bean注入各种类型的依赖（初始化）.
- 罗列：

| 标签 | 说明 |
| --- | --- |
| value | 普通值（Java基本类型以及基础类） |
| ref | 另一个bean |
| bean | 内嵌bean |
| list、set、map、props | 集合，props是指properties结构 |

- 其中value和ref都可以简写，即：

```html
<property ... value | ref="..."/>
<!-- 无须 -->
<property ...>
    <value | ref="..." .../>
</property>
```

<br><br>

### 二、注入普通值：value [·](#目录)

```html
<!-- 数值类型 -->
<value type="int">25</value>

<property name="driver">
    <!-- 没有type默认为String类型 -->
    <value>com.mysql.jdbc.Driver</value>
</property>
```

- **普通类型值指的是Java基础类型以及基础类，想数值型、字符串型等.**

<br><br>

### 三、注入其它bean：ref [·](#目录)
- 有两个可选的属性：
  1. bean：引用的其它bean不在当前XML文件中.
  2. local：引用的其它bean就在当前XML文件中，缺省情况下默认是local.

```html
<property name="axe">
    <ref bean="..."/>
</property>

<!-- 缺省情形，默认是local的 -->
<property name="axe" ref="steelAxe"/>
```

- **和value一样，也可以在construct-arg中使用.**

<br><br>

### 四、注入嵌套bean：bean [·](#目录)
- 嵌套bean其实也是“另一个bean”，只不过没有id属性，因此无法被Spring容器跟踪.

```html
<bean id="person" ...>
    <property name="axe">
        <bean class="org.lirx.app.util.axe.SteelAxe"/>
    </property>
</bean>
```

- 说明：
  1. 可以看到嵌套bean没有id，因此无法通过getBean方法获取，因此提高了程序的**内聚性**.
  2. 它和普通的“其它bean”不同的是，它不用ref引用，ref需要指定id，而嵌套bean是一个纯内部定义的bean.
  3. 因此也称为内部bean，它无法被Spring容器管理（只是创建person时临时创立该bean注入person，注入完成之后容器不再管理该bean，因此无法再访问到该bean）.

<br><br>

### 五、注入集合：list、set、map、props [·](#目录)

**1. 数组、List、Set：**

```html
<!-- private List<String> schools; -->
<!-- 数组和List都用list标签注入，因此也可以是private String[] schools; -->
<property name="schools">
    <list>
        <value>小学</value>
        <value>中学</value>
        <value>大学</value>
    </list>
</property>
```

- **由于集合元素也可以是引用类型，因此list、set中也可以包含value、ref、bean、list、set、map、props.**
- 举一个Set嵌套注入的例子：

```html
<!-- private Set set; -->
<property name="set">
    <set>
        <value>普通文本</value>
        <bean class="org.lirx.app.util.axe.SteelAxe"/>
        <ref local="StoneAxe"/>
        <list>
            <value>123</value>
            <value>lala</value>
        </list>
    </set>
</property>
```

**2. Properties：**

- Properties类型更加简单，因为其本身要求key和value**都必须是字符串**.

```html
<!-- private Properties pr; -->
<property name="pr">
    <props>
        <prop key="身高">180cm</prop>
        <prop key="age">18</prop>
    </props>
</property>
```

**3. Map：**
- 使用map标签注入.
- 需要为每个元素用entry标签注入：
  - 如果是普通类型值则使用key属性和value属性.
  - 如果是引用类型（引用其他bean）则使用key-ref属性和value-ref属性.

```html
<!-- private Map<String, int> scores; -->
<property name="scores">
    <map>
        <entry key="数学" value="80"/>
        <entry key="语文" value="90"/>
    </map>
</property>

<!-- private Map<Person, Axe> map; -->
<property name="map">
    <map>
        <entry key-ref="chinese" value-ref="steelAxe"/>
        <entry key-ref="american" value-ref="stoneAxe"/>
    </map>
</property>
```

- 对于复杂情况（Map的key是一个list、bean等）就只能使用臃肿语法了：
```html
<property name="map">
    <map>
        <entry>
            <key>
                <list>
                    <value>1</value>
                    <value>2</value>
                </list>
            </key>
            <value>
                <bean class="org.lirx.app.util.axe.SteelAxe"/>
            </value>
        </entry>
    </map>
</property>
```

<br><br>

### 六、父子bean的集合合并：[·](#目录)
- Spring支持子bean继承并合并父bean的集合依赖注入.
- 其中子bean集合中的同名元素会覆盖父bean中的.

```html
<bean id="father" abstract="true" class="org.lirx.app.user.Person">
    <property name="prs">
        <props>
            <prop key="name">Peter</prop>
            <prop key="age">15</prop>
        </props>
    </property>
</bean>

<bean id="son" parent="father">
    <property name="prs">
        <props>
            <prop key="name">Tom</prop>
            <prop key="email">example@hot.mail</prop>
        </props>
    </property>
</bean>

<!-- 最终son的prs包含了[name:Tom, age:15, email:example@hot.mail] -->
<!-- 可以看到同名的name条目被覆盖了，并且发生了继承和合并 -->
```

<br><br>

### 七、注入组合属性：[·](#目录)
- 例如：

```html
<property name="person.name.first" value="Peter"/>
```

- 这就是给person bean的name属性（也是个bean）的first属性设置.
- 它底层对应的Java语句是先调用getter最后调用setter：beanId.**getPerson()**.**getName()**.setFirst("Peter");
  - 这就要求，链上只有最后的那个节点可以为null，前面的结点都不能为null，即person、name都不能为null，否则会引发NullPointerException异常！
- 因此一定要先配置链条前端的bean，再配置链条后端的bean，因为后端依赖前端！！

<br><br>

### 八、往singleton bean中注入prototype bean：实现注入 [·](#目录)
- 如果singleton依赖singleton或者prototype依赖singleton那是没有问题，因为singleton是重复利用的，一般无需改变.
- 但如果singleton依赖prototype就会出现问题：
  - prototype就是每次获取时得到的都是新new出来的bean，每次都不一样.
  - 而singleton只会初始化一次，如果singleton依赖prototype的话，那么被依赖的prototype就永远是注入时的那个，因为之后singleton不会再改变，所以通过依赖关系获得的那个prototype bean也永远是注入时的那个，不会是新的了.
  - 这就违背了prototype的初衷了.


- 其实这个问题很好解决，只要**对prototype的getter动一下手脚**即可：

```html
<bean id="steelAxe" class="org.lirx.app.util.axe.SteelAxe" scope="prototype"/>
<bean id="person" class="org.lirx.app.user.Person">
    <property name="axeBeanId" value="steelAxe"/>
</bean>
```

```java
public class Person implements ApplicationContextAware {
    private ApplicationContext actx;
    private String axeBeanId;
    private Axe axe;

    public setApplicationContext(ApplicationContext applicationContext) {
        this.actx = applicationContext;
    }

    public String setAxeBeanId(String id) {
        this.axeBeanId = id;
    }

    public String getAxeBeanId() {
        return this.axeBeanId;
    }

    public Axe getAxe() { // 对于prototype依赖只能实现getter，不能提供setter
    // 因为setter只能一次性设值，不能保证每次getter获得的bean是新new出来的bean

        this.axe = actx.getBean(getAxeBeanId(), Axe.class);
        return this.axe;
    }
}
```

**可以看到bean类不仅需要实现ApplicationContextAware接口已获取容器引用，还需要额外添加一个prototype依赖的bean id属性，这不仅与Spring框架耦合，也和配置文件中的依赖关系相耦合，代码污染度太高.**


- Spring提供的解决方法：**实现注入**

```html
<bean id="steelAxe" class="org.lirx.app.util.axe.SteelAxe" scope="prototype"/>
<bean id="person" class="org.lirx.app.user.Person">
    <!-- 意思是steelAxe这个bean直接通过person bean的getAxe()方法注入给person bean -->
        <!-- 底层的含义是，指定person bean的getAxe()方法的返回值为steelAxe bean -->
            <!-- 这里的bean属性就是上面的axeBeanId了 -->
    <lookup-method name="getAxe" bean="steelAxe"/>
</bean>
```

```java
public abstract class Person { // 无需和Spring框架有任何耦合
    public abstract Axe getAxe(); // 注入依赖关系的方法设为抽象方法，由Spring框架实现！！
// 具体是如何实现的呢？前面已将讲的很清楚了，就是<!-- 底层的含义是，指定person bean的getAxe()方法的返回值为steelAxe bean -->
// 实现内容很明确，就是让getAxe()方法的返回值为steelAxe bean
// 具体怎么返回，就是上面自己手工实现的return actx.getBean(getAxeBeanId(), Axe.class);
    // 这样就可以保证每次获取的steelAxe都是新new出来的了！
// 因此，该方法也可以用于singleton依赖singleton，但不过这样大费周折就没意思了，因此还是只应用于singleton依赖prototype比较好
}
```

- 可以看到Spring框架如此之逆天，回调什么的太弱了，现在可以直接帮你注入方法实现了！！
- 总结就是lookup-method通过name给出要实现的getter，bean则负责注入prototype bean，而getter一定要定义成抽象的，由Spring框架来实现.
- **该方法需要用到CGLIB库，Spring就是利用该库实现客户端二进制代码的注入，jar文件是com.springsource.net.sf.cglib-x.x.x.jar**
- 这里使用的是com.springsource.net.sf.cglib-2.2.0.jar


**示例：**

- 细节注意：实现注入的bean类由于是抽象类，因此业务逻辑中不能直接使用该类型的引用，因此还是需要面向接口编程.

```java
package org.lirx.app.util;

public interface Axe {
	public void chop();
}

package org.lirx.app.util.axe;

import org.lirx.app.util.Axe;

public class SteelAxe implements Axe {

	@Override
	public void chop() {
		System.out.println("Steel Axe");
	}

}

package org.lirx.app.user;

import org.lirx.app.util.Axe;

public interface Man { // 需要增加一层接口
	public Axe getOne();
}

package org.lirx.app.user;

import org.lirx.app.util.Axe;

public abstract class Peter implements Man {
	public abstract Axe getAxe();

	@Override
	public Axe getOne() {
		return getAxe();
	}

}
```

```html
<bean id="steelAxe" class="org.lirx.app.util.axe.SteelAxe" scope="prototype"/>
<bean id="peter" class="org.lirx.app.user.Peter">
	<lookup-method name="getAxe" bean="steelAxe"/>
</bean>
```

```java
public class SpringTest {

	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("bean.xml");

		Man man = ctx.getBean("peter", Man.class); // 抽象类不能定义引用
		System.out.println(man.getOne() == man.getOne()); // false
	}

}
```
