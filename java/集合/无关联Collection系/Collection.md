### 二、Collection接口：
> 所有无关联集合的根接口，**其方法在所有派生类中都存在（也就是在Set、List、Queue以及其实现类中都可以使用）**.

    2) 接下来介绍的都是Collection的对象方法；
    3) 添加元素：成功添加返回true
         i. boolean add(Object o); // 添加一个元素
         ii. boolean addAll(Collection c);  // 将c中的所有元素加入
    4) 删除元素：成功删除返回true
         i. boolean remove(Object o);  // 删除指定元素，只删除第一个找到的
         ii. boolean removeAll(Collection c);  // 删除c中所有的元素，相当于集合相减
         iii. boolean retainAll(Collection c);  // 删除c中不包含的元素，相当于集合相交
         iv. void clear(); // 清空集合
    5) 检查是否包含某个元素：
         i. boolean contains(Object o);  // 是否包含指定元元素o
         ii. boolean contains(Collection c);  // 是否包含集合c中的所有元素
    6) 功能方法：
         i. 查看集合中元素的个数：int size();
         ii. 判空：boolean isEmpty();
         iii. 把集合转换成一个数组：Object[] toArray();
    7) 示例：
public class Test {

	public static void main(String[] args) {
		Collection c = new ArrayList(); // ArrayList是Collection的一个实现类，默认元素类型为Object

		c.add("mama");
		c.add(5);  // 虽然不能装基本类型，但是Java支持自动包装，将基本类型包装秤包装器类型
		System.out.println(c.size()); // 2
		c.remove(5);
		System.out.println(c.size()); // 1
		System.out.println(c.contains("mama")); // true
		c.add("fafa");
		System.out.println(c); // [mama, fafa]

		Collection d = new HashSet(); // HashSet也是Collection的一个实现类
		d.add("fafa");
		d.add("coco");
		System.out.println(c.containsAll(d)); // false
		c.removeAll(d); // c - d
		System.out.println(c); // [mama]
		c.clear();
		System.out.println(c); // []
		d.retainAll(c);
		System.out.println(d); // []
	}
}
！！这里我们先忽略泛型，因此编译警告先忽略；
