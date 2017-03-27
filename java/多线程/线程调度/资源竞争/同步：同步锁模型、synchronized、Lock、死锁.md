# 同步：同步锁模型、synchronized、Lock、死锁
> 并发访问导致的 **临界资源竞争**（多线程同时 **修改** 临界资源的内容）问题的解决模型 —— 同步锁模型：
>
>> 解决这个问题的实质是，**对访问临界资源的多个线程进行同步**.
>>
>> - 同步锁模型（3步）：
>>   1. **上锁**：锁定并独占临界资源.
>>   2. **修改**：修改临界资源.
>>   3. **开锁**：释放临界资源的占用权给其它线程使用.
>>
>>> - 同步锁模型的具体实现（2种）：
>>>   1. synchronized：用该关键字指定一个Java对象（**不能是基本类型**）为临界资源.
>>>      - 可以保证在紧接着的 **同步区域** 中对其的访问是 **线程安全** 的.
>>>   2. Lock：同步锁类，synchronized的上锁和开锁是隐式的，而Lock的上锁和开锁过程都是手动显式进行的.
>>>      - 两者没有本质区别，只不过Lock可以使代码更加一目了然，逻辑更加清晰.
>>>      - 但 **synchronized可以修饰对象方法**，所以具有更良好的面向对象的特性.
>>>         - 因此需要根据具体的应用场景来权衡.

<br><br>

## 目录

1. [synchronized指定同步监视器（临界资源）]()
2. [同步锁：Lock]()
3. [释放同步锁的时机：开锁时机]()
4. [死锁]()

<br><br>

### 一、synchronized指定同步监视器（临界资源）：[·](#目录)

<br>

**1.&nbsp; synchronized的一般用法：** 指定一个Java对象为同步监视器（临界资源）

- 在指定的同时还必须 **紧接着在后面** 定义一个 **同步区域（也叫同步代码块、临界区）**.
   - JVM保证 **同一时间** 最多只能有 **一个线程进入同步区域执行代码**.
   - 换句话说，**在同步区域中该临界资源是线程安全的**.
- 对应的 **上锁、修改、开锁** 3步骤模型如下：

```Java
1. 上锁: synchronized (obj) // 指定一个同步监视器（即临界资源）
2. 进门: { // 紧接着必须定义一个同步代码块（临界区），在该区域中obj是线程安全的
3. 修改:     ...  // 对obj的访问和修改
4. 开锁: } // 离开同步区域后便开锁释放临界资源的独占权
```

<br>

**2.&nbsp; synchronized对象方法（同步对象方法）：**

```Java
/**   用synchronized修饰对象方法相当于
 *  
 *    1. 指定this为同步监视器.
 *    2. 定义方法体为临界区.
 */
// 例如：标准的同步方法定义
class A {
	public synchronized void set(int val) {
		this.val = val;
	}
}

// 等价于宏替换：
public void set(int val) {
    synchronized (this) {
        this.val = val;
    }
}

// 使用时
A a = new A();

a.set(10); // 等价于
synchronized (a) {
	a.set(10);
}
```

- 其实Java编译器 **底层做的就是这种简单的宏替换**.

<br>

**3.&nbsp; 同步对象方法的特殊用法 —— 同步run：**

```Java
Runnable task = new Runnable() {
	@Override
	public synchronized void run() {
    // 相当于synchronized (this) { // this指代task对象
		System.out.println(Thread.currentThread().getName());
		for (int i = 0; i < 100; i++) {
			System.out.println("\ti = " + i);
		}
    // }
	}
};

new Thread(task).start();
new Thread(task).start();
// 可以看到两个线程的输出互斥，并非交替输出！
```

<br>

**4.&nbsp; 利用同步对象方法构造线程安全类：**

- 线程安全类的定义：
   1. 首先，该类的对象会作为多线程并发访问的临界资源. （**这个条件是前提**）
   2. 那就意味着，对象的this引用在所有的修改操作（方法）中都必须被同步.
      - 即，**所有会修改对象内容的方法都必须用synchronized限定的类就是线程安全类**.

<br>

1. 不可变类一定是线程安全的，如：StringBuilder、String、Integer等.
2. 对于可变类，最好提供两个版本：
   1. 线程安全版本：用于多线程.
   2. 线程不安全版本：用于单线程，毕竟 **同步方法的执行效率低于不同步的版本**.
      - 同步方法**要和线程调度器通信**，**消耗一定的时间和资源**.

<br>

**5.&nbsp; synchronized静态方法（同步类方法）：**

- 用synchronized修饰静态方法等价于将 **该类的.class域** 指定为同步监视器.
   - 即，**把类作为临界资源**.
   - 比如对静态方法的调用、修改静态数据域等都视为访问临界区（修改临界资源，只不过是**静态临界资源**）.

```Java
// 示例
class Test {
    synchronized static void method() { // 等价于
    // synchronized (Test.class) {
        ...
    //
    }
}
```

- 示例：

```Java
synchronized static void m1() {
	System.out.println("m1:");
	for (int i = 0; i < 100; i++) {
		System.out.println("\ti = " + i);
	}
}
synchronized static void m2() {
	System.out.println("m2:");
	for (int i = 0; i < 100; i++) {
		System.out.println("\ti = " + i);
	}
}
public static void main(String[] args) {
	new Thread(() -> m1()).start();
	new Thread(() -> m2()).start();
}
// 可以看到m1和m2的输出是互斥的，并非交替输出的！
```

<br><br>

### 二、同步锁：Lock  [·](#目录)
> 不像synchronized是隐式指定this或者Type.class为同步监视器，Lock对象本身就是同步监视器.
>
>> 其次，synchronized上锁、开锁的步骤也是隐式的（临界区的前{和后}）.
>>
>> - 而Lock的这两个过程都以显式的方法调用来完成（lock、unlock）.

<br>

- 相比synchronized的优点和缺点：
   1. 优点：
      1. 显式上锁、开锁，逻辑更加清晰，代码更加一目了然.
      2. 更加灵活，有finally块.
         - 不仅在其中可以开锁，而且还可以释放临界区（同步代码块）中开辟的非内存资源.
   2. 缺点：
      1. 没有synchronized简洁.
      2. synchronized构造线程安全类时更方便，面向对象性更好.

<br>

- **Lock仅仅是同步锁的抽象接口**，一般用的最多的实现类是**可重入锁ReentrantLock**.
   1. 可重入锁是最最简单和基础的同步锁了，所有多线程变环境都必须提供.
   2. 它允许在临界区中嵌套加速（即可重入），这就要求开锁时要按照加锁的反序（和栈的顺序一样）.
      - **后加的先开**.

<br>

**1.&nbsp; Lock接口的 2种上锁方法 & 1种开锁方法：**

```Java
/** 1. 等待式上锁
 *  
 *   - 申请获得同步锁，如果此时被其它线程占用，则等待.
 */
void lock();

/** 1. 非等待式上锁
 *  
 *   - 摸一摸：尝试申请一下，如果此时被其他线程占用（或者等待超时）.
 *   - 则返回false，不等待，直接跳到下一句执行.
 */
boolean tryLock(); // 不等待
// 超时不等待
boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

// 3. 开锁，在finally块中进行
void unlock();
```

<br>

**2.&nbsp; 等待式上锁模板：**

```Java
Lock lock = new ReentrantLock();
lock.lock(); // 上锁
try {
	// 临界区
}
finally {
	lock.unlock(); // 开锁
    // 其它善后
}
```

<br>

**3.&nbsp; 非等待式上锁模板：**

```Java
Lock lock = new ReentrantLock();
if (lock.tryLock([...])) { // 尝试上锁
    try {
        // 临界区
    }
    finally {
        lock.unlock();
        // 其它善后
    }
}
else { // 被占用或者等待超时
    // 直接放弃竞争，干其它事情去
}

// 接着干其它事儿
```

<br><br>

### 三、释放同步锁的时机：开锁时机  [·](#目录)

<br>

1. 临界区（同步代码块或方法）正常执行完毕.
2. 遇到continue、break、return等直接越过临界区的语句.
3. 出现未处理的异常或者无法解决的错误.
4. 线程通信：**在临界区中** 同步监视监视器 **发出wait信号** 让当前占用它的线程交出占用权并阻塞.

<br>

- 以下两种情况 **不会** 释放同步锁，因此容易导致死锁，一定要慎用：
   1. **sleep & yield**，虽然暂停，但 **占着锁不释放**.
   2. 调用该 **线程的suspend挂起方法**，同样 **不释放同步锁**.

<br><br>

### 四、死锁：[·](#目录)
> - 即
>    1. 两个线程.
>    2. 同时.
>    3. 等待对方.
>    4. 释放自己想要的同步锁.
>
>> 例如：A线程想用目前B线程锁定的一个资源，而B线程也刚好在等待A线程中锁定的一个资源，这就只能无限等待了.
>>
>> **任何操作系统都无法完全避免死锁**，所以编程时一定要特别注意同步锁的使用，避免死锁发生.

<br>

- 示例：

```Java
public class Test {
	A a = new A();
	B b = new B();

	class A implements Runnable {
		synchronized void tryLockB() {
			System.out.println("In Thread-A, try lock B.");
			b.tryLockA();
			System.out.println("A end.");
		}

		@Override
		public void run() {
			tryLockB();
		}

	}
	class B implements Runnable {
		synchronized void tryLockA() {
			System.out.println("In Thread-B, try lock A");
			a.tryLockB();
			System.out.println("B end!");
		}

		@Override
		public void run() {
			tryLockA();
		}

	}

	public void run() {
		Thread ta = new Thread(a);
		Thread tb = new Thread(b);

        // 死锁，ta占用了a，tb占用了b，双方都想得到对面的b和a
		ta.start();
		tb.start();
	}

	public static void main(String[] args) {
		new Test().run();
	}
}
```
