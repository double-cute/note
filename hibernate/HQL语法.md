# HQL语法

<br><br>

## 目录
1.

<br><br>

### 一、from和select：
- **from**
  1. 是最基本最简单的HQL语句，所有HQL语句都必须包含from，用于**确定查找目标**.
  2. 目标就是指数据表，但不过在Hibernate中对应的是PO类，因此from紧跟PO类名.
  3. 不加任何限定就表示查找该表的所有记录（即该PO类的所有PO对象）.

```sql
from Person   // 查询Person的所有PO

// 为查询得到的每个PO起一个临时的变量名p
    // 可以在HQL语句的其它部分通过该变量名来访问该PO对象
from Person as p   // as可以省略，但是as能增加可读性
```

- 注意：上面的语句相当于以下Java代码中的"Person p"

```java
for (Iterator it = result.iterator; it.hasNext(); ) {
    Person p = it.next();
}
```

！Person是Java类，就不用说了，p就是一个Java变量，因此这些命名都**要符合Java命名规范**，大小写就更不用说了（必须是大小写敏感的）.

- **select**
  1. 单独的from只能查询PO对象，而加了select限定后就可以查询PO的属性及其各种组合了.
  2. 例如：select p.name, p.content from Person as p
  3. select的选择非常灵活，不仅可以查询属性组合，也可以查询PO对象（和from一样），也能改变返回结果的类型：

```sql
// HQL返回结果永远是List，默认情况下List中元素的类型都是无类型的Object

// List元素类型：Object
select p.name from Person as p   // 正常
select p.name.firstName from Person as p   // 属性链

// List元素类型：Object[]，专门存放组合数据
select p.name, p, p.name.firstName from Person as p

// 为查询项取别名以简化HQL语句：
select p.name as personName from Person as p

// List元素类型：其它类型，使用new关键字
select new list(p.name, p.address) as li from Person as p   // 每个元素的类型为List
select new map(p.name as personName) from Person as p   // 每个元素类型为Map，其中以personName为key，以Person对象为value
select new MyClass(p.name, p.age) from Person as p   // 每个元素类型为自己构造的MyClass
```

<br><br>

### 二、关键字和聚集函数：
- HQL同样支持distinct、all、descend等SQL常用的关键字，用法也和SQL一模一样.
- SQL支持的所有聚集函数HQL同样也支持：count、max、min、sum、avg等

```sql
select count(*) from Person
select max(p.age) from Person as p
...
```

<br><br>

### 三、多态查询：
- HQL不仅会查from指定的PO类，还会查所有该PO类的子类.
- 因此from后可以制定任意Java类或者接口，查询会返回该类或者其所有子类或实现类.

```sql
from java.lang.Object o
from Comparable as p
```
