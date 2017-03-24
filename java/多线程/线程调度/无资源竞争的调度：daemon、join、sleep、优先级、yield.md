# 无资源竞争的调度：daemon、join、sleep、优先级、yield
> 这些线程调度方式和资源竞争无关，称为 **无资源竞争的调度**.

<br><br>

## 目录

1. [后台线程：daemon](#一后台线程daemon--)
2. [插入等待：join](#二插入等待join--)
3. [睡（阻塞）：sleep](#三睡阻塞sleep--)
4. [优先级 & 谦让：priority & yield](#四-优先级--谦让priority--yield--)

<br><br>

### 一、后台线程：daemon  [·](#目录)
> **Daemon Thread** 即 **守护线程**（也叫 **精灵线程**），通俗地说就叫 **后台线程**.
>
>> 它的作用是 **给前台的线程提供服务**，比如JVM的后台垃圾处理线程.

<br>

**1.&nbsp; 后台线程标记：** Thread的对象方法

```Java
// on为true标记为后台线程，false标记为前台线程.
final void setDaemon(boolean on);
```

<br>

**2.&nbsp; 设置标记必须在就绪（Runnable）前（即start前）完成：**

- **一旦start了就不能再改动标记**，否则抛出 **线程状态异常[IllegalThreadStateException]**.
- 默认：
   1. 前台线程开出的新线程默认是前台线程.
      - **想转后台**就必须在start之前**标记on = true**.
   2. 后台线程开出的新线程默认是后台线程.
      - **想转前台**就必须在start之前**标记on = false**.

<br>

**3.&nbsp; 查看daemon标记（是否为后台线程）：** Thread的对象方法

```Java
final boolean isDaemon();
```

<br>

**4.&nbsp; 后台线程的死亡时间：**

1. 正常下班：所有前台线程通通死亡后强制死亡.
   - 这个机制是**由后台线程的逻辑性质决定的**，所以也就这样设计了.
   - 后台线程就是给前台服务的，前台都撤了，你还在后台瞎折腾啥？
2. 自然死亡：自己跑完了.
   - 但一般后台线程都不会这样设计，都是持续服务的，永不停息.
3. 异常死亡：抛出异常，发生错误之类的，半路死.

<br>

**5.&nbsp; 示例：**

```Java
public class Test {
	public static void main(String[] args) {
		for (int i = 0; i < 30; i++) {
			if (i == 20) {
				Thread t = new Thread(() -> {
					for (int j = 0; j < 10000; j++) {
						System.out.println(Thread.currentThread().getName() + " " + j);
					}
				}, "Join Thread");
				t.setDaemon(true);
				t.start();
			}
			System.out.println(Thread.currentThread().getName() + " " + i);
		}
	}
}
```

- 可以看到，后台线程根本没跑到10000就早早结束了，main死了它也就被强制下班了.

<br><br>

### 二、插入等待：join  [·](#目录)
> 在这里join的意思是 **邀请别人插在你前面，然后你等他**.
>
>> A调用了B.join()的意思就是让B插在A的当前位置，然后A必须等待B执行完了才能继续往下执行.

<br>

```Java
// 还可以设置最长等待多少毫秒.
   // 0等价于无参版本，即永远等待.
final void join([long millis]) throws InterruptedException;
```

<br>

- 必须start后才能拿它去插别人，不start就join是无效的，即不会等待.
   - 不start就join会被视为已经执行完了，不用等待了.

<br>

- 示例：

```Java
public class Test {
	public static void main(String[] args) {
		for (int i = 0; i < 30; i++) {
            System.out.println(Thread.currentThread().getName() + " " + i);

			if (i == 20) {
				Thread t = new Thread(() -> {
					for (int j = 0; j < 20; j++) {
						System.out.println(Thread.currentThread().getName() + " " + j);
					}
				}, "Join Thread");

				t.start();  // 必须先start才能拿它去插，否则不会等待，直接往下执行

				try {
					t.join(); // 主调线程在i=20处等待一直等待
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
```

<br><br>

### 三、睡（阻塞）：sleep  [·](#目录)
> 是Thread的静态工具方法.

```Java
/**  强睡millis毫秒.
 *  
 *   - 调用一瞬间，调用它的当前线程被放入阻塞队列.
 *   - 睡眠时间到了以后重新放入就绪队列等待送入CPU.
 *
 *   - 睡眠期间并不交出CPU控制权！！其它线程还是不能占用CPU！！！
 *   - 所以sleep浪费CPU资源，要少用，一般用于测试.
 */
static native void sleep(long millis) throws InterruptedException;
```

<br><br>

### 四、 优先级 & 谦让：priority & yield  [·](#目录)
> 优先级高的线程得到CPU的机会更多.
>
>> 同时都处于运行态（Running）的线程里，**优先级高的线程在CPU轮转中获得的执行频率更高**.
>>
>>> 其次，一旦当前占用CPU的线程谦让（yield）了以后，会从就绪队列中挑出优先级最高的给CPU执行.

<br>

**1.&nbsp; 优先级的程序定义：**

1. Java线程调度器定义优先级是一个整数，范围是[1, 10].
   - 但Thread类也提供了三个静态常量表示3个层次量级：
      1. Thread.MAX_PRIORITY = 10：最高优先级.
      2. NORM_PRIORITY = 5：普通优先级.
      3. MIN_PRIORITY = 1：最低优先级.
   - 并不是所有的操作系统都支持Java的10个优先级，例如Windows2000就只提供了其中的7个优先级.
      - 但**任何操作系统必支持1、5、10这三个优先级**.
      - 因此**多用上面3个静态常量**少随意给数字可以**提高可移植性和通用性**.
2. 默认情况下主线程main具有普通优先级（5）.
   - 线程的优先级**默认和创建它的父线程的优先级相同**.

<br>

**2.&nbsp; 设置和查看优先级：** Thread的对象方法

```Java
// 1. 查看优先级
final int getPriority();

/** 2. 设置优先级
 *  
 *   - 可以动态设置：在线程正在运行态时即时设置，设置之后立马原地生效！
 *   - 设置的优先级newPriority必须处在[MIN, MAX]内.
 *      - 否则抛出 非法参数异常[IllegalArgumentException].
 */
final void setPriority(int newPriority);
```

- 示例：

```Java
public class Test {
	public static String name() { // 返回当前线程的名字
		return Thread.currentThread().getName();
	}
	public static int priority() { // 返回当前线程的优先级
		return Thread.currentThread().getPriority();
	}
	static class Task implements Runnable {
		@Override
		public void run() {
			for (int i = 0; i < 50; i++) {
				System.out.println(name() + " Priority value = " + priority() + " " + i);
			}
		}
	}
	public static void main(String[] args) {
		Thread.currentThread().setPriority(6);
		for (int i = 0; i < 50; i++) {
			if (i == 10) {
				Thread low = new Thread(new Task(), "Low Priority Thread");
				// 优先级继承父线程，6
				System.out.println("Low Thread ini-prior = " + low.getPriority());

				low.start(); // 可以在运行态动态改变优先级！
				low.setPriority(Thread.MIN_PRIORITY);

				Thread high = new Thread(new Task(), "High Priority Thread");
				// 同样继承父线程的优先级
				System.out.println("High Thread ini-prior = " + high.getPriority());
				high.start();
				high.setPriority(Thread.MAX_PRIORITY);
                // 最后看到高优先级比低优先级更早执行完，虽然低优先级先启动.
                // 因此说明高优先级在CPU轮转中得到更多执行频率.
			}
		}
	}
}
```

<br>

**3.&nbsp; 谦让：** yield，Thread的**静态工具方法**

```Java
/**  前线程表示谦让一下，请求调度器重新调度一次
 *  
 *   - 步骤：
 *     1. 立即把当前线程提出到就绪队列.
 *     2. 再从就绪队列中挑一个优先级高的送去CPU执行.
 */
static native void yield();
```

- **优先级都相同** 的情况下如何选择 **由调度器自己的实现决定**，无法人为干预.
   - 因此**谦让结果的随机性很高，并且在CPU核心数不同的情况下差异较大**.

- 但yield确实是一种**非常优秀的高效利用CPU资源的策略**：
   - 如果当前线程的重要性不是很高，则可以考虑谦让给其它重要性更高的线程.
