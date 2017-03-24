# 无资源竞争的调度：daemon、join、sleep、yield、优先级
> 这些线程调度方式和资源竞争无关，称为 **无资源竞争的调度**.

<br><br>

## 目录

1. []()
2. []()

<br><br>

### 一、后台线程：daemon
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

### 二、插入等待：join
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

### 三、睡（阻塞）：sleep
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

### 四、 让步 & 优先级：yield & priority
    1) yield表示让线程暂停一下，但这个暂停并不是进入阻塞状态，而只是让调度器申请一下重新调度一次，而线程本身则是进入就绪状态；
    2) 也许调度器又把这个让步的线程第一个调度出来执行，这就跟没暂停一样，如何调度是调度器的事情，你无法控制；
    3) 原型：public static native void Thread.yield();
！！它是一个静态方法，直接调用表示当前使用该代码的线程申请让步；
！！通常建议使用sleep来控制线程而不是yield：
        a. sleep会抛出异常单yield不会，因此sleep更安全；
        b. sleep比yield有更好的可以执行，因为sleep在任何系统上效果都相同，而yield随机性太高，并且在核数不同的CPU上效果有较大的差异；
    3) 虽然无法控制，但是你可以干涉，即给调度器一个参考，提醒调度器那些线程重要度高可以先调度；
    4) 调度器用线程优先级来衡量各个线程的重要性，在多个线程就绪时调度器会挑优先级高的线程先执行（而多个优先级值相等的线程谁应该先调度那就是基于操作系统的实现了，不同操作系统实现不尽相同），如果yield后有其它线程比让步的线程优先级高，那么就会真正的让步（暂停）去执行优先级较高的线程；
！！因此yield是一种合理利用资源的策略，如果在一个程序中，一个线程的重要度不高不低，而其它有些线程亟待处理（重要度特别高），如果这种重要度不高不低的线程直接阻塞去让高优先级线程可能代价太大，因此可以尝试申请让步，因为让步后仍处于就绪状态，不会暂停太久；
！！因此yield是一种利用时间资源的优秀策略；
    5) 人为设定线程的优先级：
        i. Java线程调度器中优先级是一个整数，范围是1~10（[1, 10]闭区间）；
        ii. 但Thread类也提供了三个静态常量表示大致的量级：MAX_PRIORITY（10，最高优先级）、MIN_PRIORITY（1，最低优先级）、NORM_PRIORITY（5，普通优先级）；
        iii. 默认情况下主线程main具有普通优先级（5），每个线程的优先级和创建它的父线程的优先级相同；
        iv. 可以通过Thread类的对象方法改变和获取当前线程对象的优先级：
             a. public final void Thread.setPriority(int newPriority); // 设置新的优先级
             b. public final int Thread.getPriority(); // 获取优先级整数
！！设置优先级时尽量使用三个静态常量而不要直接指定一个整数，因为并不是所有的操作系统都支持Java的10个优先级，比如Windows2000就只提供了7个优先级，但无论如何，任何操作系统都会提供者三个静态常量所代表的优先级，因此尽量使用这三个静态常量保证最可靠的移植性；
    6) 示例：优先级高的线程比低优先级线程得到更多的执行机会
public class Test {

	public static String threadName() {
		return Thread.currentThread().getName();
	}

	public static int threadValue() {
		return Thread.currentThread().getPriority();
	}

	class Target implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			for (int i = 0; i < 50; i++) {
				System.out.println(threadName() + " Priority value = " + threadValue() + " " + i);
			}
		}

	}

	public void init() {
		Thread.currentThread().setPriority(6);
		for (int i = 0; i < 50; i++) {
			if (i == 10) {
				Thread low = new Thread(new Target(), "Low Value Thread");
				low.start();
				System.out.println("Low Thread initial value = " + low.getPriority());
				low.setPriority(Thread.MIN_PRIORITY);

				Thread high = new Thread(new Target(), "High Value Thread");
				high.start();
				System.out.println("High Thread initial value = " + high.getPriority());
				high.setPriority(Thread.MAX_PRIORITY);
			}
		}
	}

	public static void main(String[] args) {
		new Test().init();
	}

}
