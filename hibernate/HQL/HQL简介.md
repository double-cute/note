# HQL简介
> Hibernate Query Language.<br>
> 是Hibernate提供的一种面向对象的查询语言，比传统的SQL功能更强大.

<br><br>

## 目录
1. [HQL的使用流程](#一hql的使用流程)
2. [额外补充](#二额外补充)
3. [命名查询]()

<br><br>

### 一、HQL的使用流程：[·](#目录)

- SQL操作的是数据表、列的表级数据，而HQL操作的是**PO对象**，完全是**面向对象**的，支持**继承、多态**.
- HQL的使用流程：
  1. 编写HQL语句，就是普通的String字符串.
  2. 调用Session的createQuery方法将HQL语句作为参数创建一个Query类对象（即一个Hibernate的HQL对象）.
    - 如果HQL语句中包含参数（占位符之类的参数），则需要调用Query对象的setXxx为参数赋值.
  3. 最后调用Query对象的list或者uniqueResult方法返回查询结果（PO集合，SQL返回的是记录集，HQL返回的是PO集，彻彻底底的面向对象查询）.

```java
public class Test {
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration().configure();
		SessionFactory sf = conf.buildSessionFactory();
		Session sess = sf.openSession();

		Transaction tx = sess.beginTransaction();

			// 可以使用占位符，?表示无名占位符，:argName表示带名称的占位符
			String hql = "select distinct p from Person p join p.contents where p.name = :argName and p.age < :argAge";
			// 创建Query对象，实际上createQuery是预编译
			Query query = sess.createQuery(hql);
				// 使用各种setXxx给占位符传参，?占位符直接传参数值（单参数），:argName占位符先给出参数名在给出参数值
					// 由于setXxx返回的是Query对象本身，因此可以连续使用setXxx给多个占位符传参
				// xxx支持String、Integer、Date等常用类型
				query.setString("argName", "Peter").setInteger("argAge", 20);
			// 调用list或者uniqueResult返回PO集，list()方法的返回类型就是List，无类型的（无模板类型）！！
			List persons = query.list();

			// 遍历集合的通用代码
			for (Iterator it = persons.iterator(); it.hasNext(); ) {
				Person p = (Person)it.next();
				System.out.println(p.getName());
			}

		tx.commit();

		sess.close();
		sf.close();
	}
}
```

- **如果查询的是组合数据：**
  1. 由于面向对象，因此查询结果可以直接是PO集，如上例所示，直接select了一个Person对象.
  2. 但也有像传统SQL那样查询组合数据，例如：select p.id, p.name from ...
  3. 对于这种组合结果，迭代时每个it.next()返回的将会是一个**Object[]** 数组，每一列数据对应一个数组元素.
  4. 得到该数组后自行解析得到想要的结果.

<br><br>

### 二、额外补充：[·](#目录)
1. Query中的常用方法：
  1. List list();  // 返回结果集
  2. Object uniqueResult();  // 返回唯一结果，如果HQL结果不唯一则抛出异常，查不到则返回null
  3. Query setFirstResult(int firstResult);  // 结果集从第几个记录开始，从0计
  4. Query setMaxResults(int maxResults);  // 设定本次查询结果最多返回多少个记录
2. 大小写问题：
  1. HQL语句本身大小写不敏感：关键字、函数等.
  2. 但语句中的Java类名、包名、属性名等都是符合Java命名规范的，是区分大小写的.

<br><br>

### 三、命名查询：[·](#目录)
- 为了最大程度的解耦，Hibernate允许将HQL语句写在配置文件中，并为其命名.
- 然后在代码中根据这个命名加载HQL语句并进行查询（session的getNamedQuery方法），这使得HQL语句无需和业务逻辑代码相耦合了.

```xml
<!-- 用query标签定义一个HQL查询，用name属性对其命名 -->
<query name="myQuery">
    from Person as p where p.age > 18
</query>
```

```java
session的方法：Query getNamedQuery(String queryName);  // queryName就是query的命名
```
