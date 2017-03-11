# IdentityHashMap & EnumMap
> 1. IdentityHashMap是**严格按照key的地址判断是否重复**的HashMap.
>   - 即key的equals覆盖了也不起作用（并且hashCode要和equals保持一致）.
>   - 因此要用IdentityHashMap存放的**key的类型最好都不要实现hashCode和equals**.
>     - **就默认使用Object继承而来的hashCode和equals好了.**
>   - 由于是HashMap，因此还是**允许key为nul的**.
> 2. EnumMap和EnumSet一样，**key必须是同一种枚举类型的值且不能为null**.
>   - 只不过**EnumMap只有构造器一种方式构造**.
>   - 不像EnumSet，只能用各种静态工具方法构造.


<br><br>

## 目录

1. [IdentityHashMap](#一identityhashmap)
2. [EnumMap](#二enummap)

<br><br>

### 一、IdentityHashMap：[·](#目录)
> 是一种特殊的HashMap.
>
>> 1. 还是用key的hashCode来决定entry的桶.
>> 2. 但**不用key的equals**来决定槽了.
>>   - 而是**强制使用Object继承而来的equals来决定槽.**
>>   - 即强行使用地址是否相等来决定槽.
>>   - **即使key的类型覆盖了equals也不起作用.**
>>     - Identity的含义就在这里，即通过严格的地址相等来维护不重复.

<br>

- 根据hash结构的使用原则，要求**equals和hashCode保持一致**，因此：
  1. 使用IdentityHashMap时让hashCode返回的也是对象的地址！
  2. 所以一句话总结就是：**不重写equals和hashCode，直接使用Object继承而来的即可**.
    - 使用IdentityHashMap保存的元素最好是彻彻底底地根据地址来判断是否相等.

<br>

- 示例：

```Java
class R {
	int val;

	public R(int val) {
		this.val = val;
	}

	@Override
	public String toString() {
		return "R[val:" + val + "]";
	}

	@Override
	public boolean equals(Object obj) { // equals的标准写法
		if (this == obj) { // 先比较地址
			return true;
		}

		if (obj != null && obj.getClass() == R.class) { // 再比较类型（前提是obj不能为空）
			R r = (R)obj; // 先把类型调整一致（当然可以直接return this.val == ((R)obj).val
						  // 但如果在return之前还要用obj进行一些其它复杂操作那用临时的类型转换太麻烦了
			              // 因此先协调类型才是最标准最合理的做法
			return this.val == r.val;
		}

		return false; // 地址不同 || obj为空 || 类型不一致，那肯定不一样了！
	}

	@Override
	public int hashCode() {
		return this.val;
	}
}

public class Test {

	public static void main(String[] args) {
		IdentityHashMap map = new IdentityHashMap<>();
		map.put(new R(1), "fun");
		map.put(new R(1), "kun");
		R r = new R(3);
		map.put(r, "xxx");
		map.put(r, "yyy");
		System.out.println(map); // {R[val:1]=fun, R[val:3]=yyy, R[val:1]=kun}

		IdentityHashMap map2 = new IdentityHashMap<>();
		map2.put(new String("lala"), "abc");
		map2.put(new String("lala"), "xyz");
		map2.put("mama", "777");
		map2.put("mama", "888");
		System.out.println(map2); // {mama=888, lala=xyz, lala=abc}
	}
}
```

<br><br>

### 二、EnumMap：[·](#目录)
> 实现完全和EnumSet一样，只不过key使用二进制位向量存储的而已.
>
>> 1. key不能是null，而且必须是同一种枚举类型的值.
>> 2. value的类型随意，可以是null.
>>
>>> 除了构造器之外其余没有对Map进行任何的额外扩展，因此就当做普通的Map实用就行了.

<br>

**构造器：**

- EnumMap只有构造器构造的方式，没有静态工具方法可以构造.

```Java
// 1. 用指定枚举类型构造一个空的map
EnumMap(Class<K> keyType);

// 2. 用另一个map构造（复制）
EnumMap(EnumMap<K, ? extends V> m);

/** 3. 用另一个普通的Map对象构造（复制）
 *  
 *  - 如果m是：
 *    1. 也是一个EnumMap，那么就和2.完全一样.
 *    2. 不是EnumMap，那么必须：不满足任何一个就会抛出异常！
 *      1. 其key全部都是同一种枚举类型的值（否则报错！）.
 *      2. 必须不为空，必须至少要有一个元素来判断其key的类型.
 */
EnumMap(Map<K, ? extends V> m);
```
