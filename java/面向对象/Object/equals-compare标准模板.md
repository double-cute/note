2. equals方法的重写模板：
@Override
public boolean equals(Object obj) {
  if (this == obj) { // 先比较地址
    return true;
  }

  if (obj != null && obj.getClass() == ThisType.class) { // other不为空，且类型严格相同再比较值
    ThisType other = (ThisType)obj;
    if (值相等逻辑成立) {
      return true;
    }
  }

  return false;
}
！！equals的正规定义就是：自反性、对称性、传递性，在值满足这三个性质的同时，类型也要满足这三个性质，因此这里使用了obj.getClass() == ThisType.class的严格类型相等，而不是使用obj instanceof ThisType！

### 五、compare的实现原则：



    4) 示例：
class R implements Comparable {
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
	public int compareTo(Object o) { // compareTo的标准写法
		if (this == o) { // 第一步和equals一样，还是先比较地址，这是最基本也是最容易想到的
			return 0;
		}

		if (o == null) { // 由于compareTo比较的是大小，而null是没法比较大小的，因此null就得抛异常
			             // 这里只是个演示，就不实现这一块了
			// 抛出异常
		}

		R r = (R)o; // 不判断类型是否一致，不一致直接抛出异常！就是要暴露异常方便调试（集合中元素都应该保证类型相同）

		return this.val - r.val; // 正常的操作和返回
	}
}

public class Test {

	public static void main(String[] args) {
		TreeMap map = new TreeMap();

		map.put(new R(9), "aaa");
		map.put(new R(5), "bbb");
		map.put(new R(-3), "ccc");
		System.out.println(map); // key: -3, 5, 9

		System.out.println(map.firstEntry()); // key: -3
		System.out.println(map.lastKey()); // key: 9
		System.out.println(map.higherKey(new R(2))); // key: 5
		System.out.println(map.lowerEntry(new R(1))); // key: -3
		System.out.println(map.subMap(new R(-1), new R(10))); // key: 5, 9
	}
}
