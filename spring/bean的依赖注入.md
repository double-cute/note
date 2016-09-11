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
- 它底层对应的Java语句是先调用getter最后调用setter：beanId.getPerson().getName().setFirst("Peter");
  - 这就要求，链上只有最后的那个节点可以为null，前面的结点都不能为null，即person、name都不能为null，否则会引发NullPointerException异常！
- 因此一定要先配置链条前端的bean，再配置链条后端的bean，因为后端依赖前端！！
