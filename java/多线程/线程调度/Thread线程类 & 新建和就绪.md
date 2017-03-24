# Thread线程类 & 新建和就绪

<br><br>

## 目录

1. [Thread & Runnable：线程-任务分离的设计模式]()
2. [Thread的4大状态（没有阻塞）]()
3. [Thread信息查看]()
4. [检测异常并保存计算结果的任务体：FutureTask-Callable]()
5. [创建 & 启动线程]()

<br><br>

### 一、Thread & Runnable：线程-任务分离的设计模式  [·](#目录)
> **线程就是进程中的子任务**，进程是子任务的合同（即总任务）.
>
>> - 但在实际实现中，Java：
>>   1. 把线程作为 **任务的载体** 来看待.
>>   2. 而线程本身在Java核心类库中仅仅作为 **线程调度器的调度单位** 来看待.
>>
>>> - 具体讲就是：
>>>    - 线程的功能只有两个：
>>>       1. 搭载任务.
>>>       2. 把任务送入CPU执行.
>>>    - 也就是说线程就是一次性的车，一生的目的就是搭上一个任务送去CPU执行，执行完了车就去死.
>>>       - 线程仅仅就是一个交通工具，**不能干涉任何任务的内容**.
>>>       - **任务是乘客，线程是车，调度器是交警.**
>>>    - 任务和线程两者是分离的：
>>>       - 任务有自己的数据、任务的内容（完成任务的各种方法）.
>>>       - 线程负责的应该是：
>>>          1. 载客：Thread(task) { this.task = task; }  // 构造器，task是其成员
>>>          2. 启动：thread.start();
>>>          3. 送去目的地（CPU中执行）：thread.run() { this.task.runningTask(); }
>>>          4. 中途各种听交警指挥的能力.

<br>

- 线程-任务分离的最大好处就是：**多个线程对象可以共享同一个任务，更加灵活！**

<br>

**1.&nbsp; 看一下代表Java线程的Thread类的大致内容：**

```Java
public class Thread {
    private Runnable task;  // 搭载的任务

    // 1. 构造器当然是给车上一个乘客
    public Thread(Runnable task) {
        this.task = task;
    }

    // 2. 启动（人主动调用），启动后就绪，听从交警安排
    public synchronized void start();

    // 3. 在交警指示下发车（不能人主动调用），送往CPU执行
    public void run() { // 在CPU里了
        if (this.task != null) {
            this.task.run(); // 让任务的内容得到执行
            // 可以看到，线程不会干涉task是怎么run的
            // 只负责把它送进CPU
        }
    }

    // 4. 其它听从调度的能力
    ...
}
```

<br>

**2.&nbsp; Runnable代表的是任务：**

- 仅仅就是一个函数式接口，run方法就是任务的执行体.
   - Thread的发车方法run就是把任务的run送进CPU执行的.
      - 两个方法都叫run，太挫了，如果是我就：
         1. 把Thread的run改叫send2CPU.
         2. 把Runnable的run改叫exec.

```Java
public interface Runnable {
    public abstract void run();
}
```

<br>

**3.&nbsp; 自己设计一个可以检测异常并返回计算结果的任务：**

<br>

- Thread & Runnable最明显的缺点：
   1. **无法返回任务的计算结果.**
      - Thread的run和Runnable的run都没有返回值.
   2. **不能抛出异常.**
      - Thread的run和Runnable的run都不能抛出异常.

<br>

- 解决这个问题的思路：可以**把这两个工作都交给任务本身来完成**.
   1. 为任务本身设计一个可以抛出异常并且能返回结果的"run（或者说是exec）方法".
   2. 然后让Runnable的run嵌套调用，在Runnable的run中捕获异常和结果. （exec是嵌套执行体）
      - 可以把结果作为任务的数据成员保存下来.
      - 等线程执行完毕后通过任务的getter方法获得计算结果.

```Java
public class Task implements Runnable {
    private Data data;  // 任务自己要处理的数据
    private Result result = null;  // 用于接受任务处理后的结果

    // 构造器族：初始化data
    public Task(...) { ... }

    // 嵌套执行体，既可以捕获异常也能返回计算结果
    public Result exec() throws XxxException {
        执行任务内容.
        return result;
    }

    @Override
    public void run() { // 嵌套调用exec
        try {
            this.result = exec(); // 不仅保存结果
        }
        catch (XxxException e) { // 还捕获异常
            异常处理
        }
    }

    public getResult() { // 让外界可以获取任务结果
        return this.result;
    }
}
```

- 其实完全不需要自己设计，Java提供了FutureTask<V>（任务类）Callable<V>（嵌套执行体）来满足这个需求.
   - V类型参数表示计算结果的类型.

<br><br>

### 二、Thread的4大状态（没有阻塞）：[·](#目录)

<br>

**1.&nbsp; 新建（New）：就是构造器**

- 默认线程组是主线程的组.
- 主线程默认名为"main"，其它自定义线程默认名为"Thread-N"，**N是数字，从0计**.

```Java
// 1. 空车，run不执行任何东西，空车没意义，很少用
Thread();

// 2. 乘客、车名分别/同时指定
Thread(Runnable task);
Thread(String name);  // 空车很少用，不推荐
Thread(Runnable task, String name);

// 3. 指定线程组的 乘客、车名分别/同时指定
Thread(ThreadGroup group, Runnable task);
Thread(ThreadGroup group, String name);  // 空车很少用，不推荐
Thread(ThreadGroup group, Runnable task, String name);
```

- 由于不推荐空车的使用，**最常用的就是下面两种**：

```Java
Thread(Runnable task[, String name]);
Thread(ThreadGroup group, Runnable task[, String name]);
```

<br>

**2.&nbsp; 就绪（Runnable）：**

```Java
/**   通知调度器把自己加入就绪队列.
 *     - 其中调用了本地方法 native start0() 给调度器发送就绪信号.
 *     - 一般子类不需要重写，除非是深度定制.
 *
 *    - 加入就绪队列后，只能听从调度器安排是否送入CPU执行，无法认为干预.
 *    - 送入CPU时调度器会回调run方法进入运行态（run方法执行任务）.
 *     - 千万不要自己手动调用run！是由调度器回调，调度器将run方法送进CPU执行.
 *
 *    - 最多只能调用1次start，超过一次就抛出IllegalThreadStateException异常（线程状态异常）
 */
synchronized void start();
```

<br>

**3.&nbsp; 运行（Running）：**

```Java
/**   由调度器回调，不要自己手动调用
 *  
 *    - 内部调用的是任务体的执行体this.task.run()
 *    - 可以看到空车什么都不做，所以空车一般没意义.
 */
void run() {
    if (this.task != null) { // 空车什么都不做
        this.task.run();
    }
}
```

<br>

**4.&nbsp; 死亡（Stop）：** 调用枪毙方法stop以强行停车

```Java
// 容易死锁，不推荐使用
@Deprecated
final void stop();
```

<br><br>

### 三、Thread信息查看：[·](#目录)

<br>

- **最常用的：通过Thread的静态工具方法currentThread获取当前线程的句柄**.
   - 想获取主线程句柄就只能通过该方法！

```Java
// 返回调用该方法的线程的引用
static native Thread currentThread();
```

<br>

**1.&nbsp; 描述信息：**

```Java
// Thread[线程名, 优先级[, 所属组组名]]
String toString();
```

<br>

**2.&nbsp; 线程名：**

```Java
// getter
final String getName();
// setter
final synchronized void setName(String name);
```

<br>

**3.&nbsp; 获取线程号：**

```Java
/**  
 *   1. 线程ID是一个正的、long型数.
 *   2. 在新建（New）时分配到id.
 *   3. 保证在父进程的整个线程池中id独一无二.
 *   4. 在线程的整个生命周期中id保持不变（且无法改变）.
 *   5. 线程死亡后该id可以被回收再利用！
 */
long getId();
```

<br>

**4.&nbsp; 检查线程是否活跃（是否处于动态）：**

```Java
// 动态是指：就绪、运行、阻塞
// 静态是指：新建、死亡（就是内存中的一块儿肉）
final native boolean isAlive();
```

<br><br>

### 四、检测异常并保存计算结果的任务体：FutureTask-Callable  [·](#目录)
> 前面讲过了，不必自行设计[检测异常 & 返回计算结果]的任务体了，Java本身就提供了一组工具.
>
>> 即 **FutureTask类** 和 **Callable接口**.

<br>

**1.&nbsp; Callable接口：** 即 **嵌套执行体task.exec**

```Java
/**  
 *   - call就是嵌套执行体exec
 *   - V指代线程计算结果的数据类型.
 *   
 *   - 可以看到，可返回计算结果，也可抛出异常
 */
@FunctionalInterface
public interface Callable<V> {
    V call() throws Exception;
}
```

<br>

**2.&nbsp; FutureTask类：** 负责**保存数据和计算结果**的任务体

```Java
public class FutureTask<V> implements Runnable {
    private Callable<V> callable; // 嵌套执行体
    private V result;  // 接受任务计算结果

    // 1. 上车，强行不能空车
    public FutureTask(Callable<V> callable) {
        if (callable == null) // 空车抛异常
            throw new NullPointerException();
        this.callable = callable;
    }

    // 2. 任务执行体，被线程Thread的run回调
    @Override
    public void run() {
        try { // 保存结果
            this.result = this.callable.call();
        }
        finally {
            ...  // 异常处理
        }
    }

    // 3. 查看任务是否执行完毕
    public boolean isDone();

    /** 4. 取消任务
     *  
     *    - 这种取消只是建议性的，遇到以下情况会失败返回true：
     *      1. 任务已经done了.
     *      2. 之前已经被cancel过了.
     *      3. 调度过程中发生未知错误.
     *
     *    - 可以在任何时候取消：
     *      1. 在非运行态取消，则向调度器正常提交建议. （mayInterruptIfRunning不起作用）
     *      2. 在运行态取消，则须要参考mayInterruptIfRunning.
     *        - 如果mayInterruptIfRunning为true则表示建议stop枪毙线程.
     *      - 犹豫cancel的时机是随机的，因此mayInterruptIfRunning的建议必填！
     */
    boolean cancel(boolean mayInterruptIfRunning);
    /**   检查是否被取消
     *  
     *    - 检测标准是：是否在done之前，成功调用cancel过（返回true）
     *      - 简单地说就是是否成功调用过cancel方法啦！
     */
    boolean isCancelled();

    // 5. 获取计算结果（必须等待线程执行完毕）
      // 在等待结果时任务可能会被打断或出现异常等
    V get([long timeout, TimeUnit unit]) throws
        InterruptedException, ExecutionException[, TimeoutException];
}
```

<br><br>

### 五、创建 & 启动线程：[·](#目录)

<br>

**1.&nbsp; 创建线程的3中方法：**

1. 继承Thread类：
   - 由于Thread本身也实现了Runnable接口，其开车方法run就是对Runnable的实现.
   - 可以把Thread本身看做任务体.
      - 一般不推荐这种方式：线程和任务相耦合.
      - 一般用在深度定制等特殊场合.
2. 自定义Runnable任务体：
   - 这种方式非常灵活，自由度非常大，想设计多复杂就设计多复杂.
   - 当然最懒的就是一个lambda表达式（无法返回结果以及检测异常）.
3. 任务体采用FutureTask-Callable框架：
   - 那就必须检测异常并存储计算结果.
   - 通用性强，但没有自定义任务体来的那么自由无限制.

<br>

**2.&nbsp; 如何选择创建方式：**

1. 继承Thread少用，只有在深度定制下考虑.
2. 不检测异常并返回计算结果时推荐使用自定义Runnable任务体.
   - 复杂自定义最好也使用该方法.
3. 检测异常并返回计算结果，同时没有其它复杂需求就采用FutureTask-Callable框架.

<br>

**3.&nbsp; 继承Thread：**

```Java
public class MyThread extends Thread {

	private int i = 0; // 线程的私有内存空间中的数据再线程之间不能共享

	@Override
	public void run() {
		while (i < 100) {
			System.out.println(getName() + " " + i++);
		}

	}

	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			System.out.println(Thread.currentThread().getName() + " " + i);

			if (i == 20) { // 只在20时启动一次
				new MyThread().start();
				new MyThread().start();
			}
		}
	}
}
```

<br>

**4.&nbsp; 自定义Runnable任务体：**

- 这是**多线程共享同一个任务**的典型应用.

```Java
class Task implements Runnable { // 自定义任务体
	private int i = 0;

	@Override
	public void run() {
		while (i < 100) {
			System.out.println(Thread.currentThread().getName() + " " + i++);
		}
	}
}
public class Test {
	public static void main(String[] args) throws FileNotFoundException {
		for (int i = 0; i < 100; i++) {
			System.out.println(Thread.currentThread().getName() + " " + i);
			if (i == 20) {
				Task task = new Task();

                // 1和2共享同一个任务
				new Thread(task, "MyThread-1").start(); // 相同task对象中的数据i将在两线程间共享
				new Thread(task, "MyThread-2").start(); // 输出时两线程的i值是连续的，证明了这一点

                // 用lambda表达式来定义一个新任务
				new Thread(() -> {
					for (int j = 0; j < 50; j++) { // 局部变量，和外界无关
						System.out.println(Thread.currentThread().getName() + " " + j++);
					}
				}, "lala").start();
			}
		}
	}
}
```

<br>

**5.&nbsp; FutureTask-Callable框架：**

```Java
// 嵌套执行体
class NestedExecutor implements Callable<Integer> {
	@Override
	public Integer call() throws Exception {
		int i = 0;
		while (i < 100){
			System.out.println(Thread.currentThread().getName() + " " + i++);
		}
		return i;
	}
}
public class Test {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		// 用实现类来指定嵌套执行体
		FutureTask<Integer> task1 = new FutureTask<>(new NestedExecutor());
		// 用lambda表达式来指定嵌套执行体
		FutureTask<Integer> task2 = new FutureTask<Integer>(() -> {
			int i = 0;
			while (i < 88) {
				System.out.println(Thread.currentThread().getName() + " " + i++);
			}
			return i;
		});

		for (int i = 0; i < 99; i++) {
			System.out.println(Thread.currentThread().getName() + " " + i);
			if (i == 20) {
				new Thread(task1, "task 1").start();
			}
			if (i == 25) {
				new Thread(task2, "task 2").start();
			}
		}

		// 回收计算结果：100和88
		System.out.println("task 1 = " + task1.get());
		System.out.println("task 2 = " + task2.get());
	}
}
```
