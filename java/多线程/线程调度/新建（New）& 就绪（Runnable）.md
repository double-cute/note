# Thread线程类 & 新建和就绪


<br><br>

## 目录

1. []()
2. []()

<br><br>

### 一、Thread & Runnable：线程-任务分离的设计模式
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

**1.&nbsp; 看一下代表Java线程的Thread类的大致内容：**

```Java
public class Thread {
    private Runnable task;  // 搭载的任务

    // 构造器当然是给车上一个乘客
    public Thread(Runnable task) {
        this.task = task;
    }

    // 启动（人主动调用），启动后就绪，听从交警安排
    public synchronized void start();

    // 在交警指示下发车（不能人主动调用），送往CPU执行
    public void run() { // 在CPU里了
        if (this.task != null) {
            this.task.run(); // 让任务的内容得到执行
            // 可以看到，线程不会干涉task是怎么run的
            // 只负责把它送进CPU
        }
    }
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

- Thread类并没有返回任务计算结果的方法.
- Thread的run以及Runnable的run也并不能处理异常.
- 因此我们可以**把这两个工作都交给任务本身来完成**.

```Java
public class Task implements Runnable {
    private Data data;  // 任务自己要处理的数据
    private Result result = null;  // 用于接受任务处理后的结果

    // 构造器族：初始化data
    public Task(...) { ... }

    public Result exec() throws XxxException { // 任务执行体，可以抛出异常
        执行任务内容.
        return result;  // 返回任务结果
    }

    @Override
    public void run() { // 不仅保存结果而且处理异常
        try {
            this.result = exec();
        }
        catch (XxxException e) {
            异常处理
        }
    }

    public getResult() { // 让外界可以获取任务结果
        return this.result;
    }
}
```

<br>



1. 创建和启动线程的三种方法：
    1) 继承Thread类；
    2) 实现Runnable接口；
    3) 实现Callable和FutureTask接口；

2. 继承Thread类：
    1) 步骤很简单：
         i. 构造自定义的线程类继承Thread类；
         ii. 实现其中的run方法，run方法就代表了该线程要完成的任务；
         iii. 创建该类的对象，然后调用其start方法启动线程（一旦调用该方法就会并发执行该线程），即开启该线程的同时继续执行下面的代码（线程外的代码）；
    2) Thread类常用的方法：
         i. public void run(); // 该方法需要用户覆盖，编写线程要完成的具体任务
         ii. public synchronized void start(); // 无需覆盖，即开启线程，并且是以同步的方式
         iii. public final String getName(); // 返回该线程的名称，主线程默认是main，其它自定义的线程默认是Thread-X，X是数字，从0开始计；
         iv. public final synchronized void setName(String name); // 为线程设置自定义的名字，并且是以同步的方式
         v. public static native Thread currentThread(); // 该方法使静态方法，不属于对象，该方法将返回当前调用该方法的线程的线程对象
！！一般使用Thread.currentThread().getName()的方法来获取当前线程的名称，特别是要获得主线程的名称就使用这种方法，因为Java并没有直接提供主线程的对象（即Main方法所代表的线程）；
    3) 示例：
public class MyThread extends Thread {

	private int i = 0; // 线程的私有内存空间中的数据再线程之间不能共享

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// super.run();
		while (i < 100) {
			System.out.println(getName() + " " + i++);
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for (int i = 0; i < 100; i++) {
			System.out.println(Thread.currentThread().getName() + " " + i);

			if (i == 20) { // 只在20时启动一次
				new MyThread().start();
				new MyThread().start();
			}
		}
	}

}
！在线程对象中定义的局部变量、成员数据在线程之间不能共享，因为每创建一个线程对象就要构造一个实例（即构造一个i），因此实例的i在线程之间是不能共享的，每个线程都有自己的一个i；

3. Runnable——Thread的target目标：实现任务和线程本身的分离设计模式
    1) 想象一下如果一个程序需要很多很多线程，而每个线程也执行不同的任务，那么如果用之前的方法岂不是要为每种任务都自定义一个线程类，而定义线程类只是为了实现它的一个run方法；
    2) 这就显得非常多余，并且在设计模式上显得非常冗杂，因此Java就提出了任务和线程分离的程序设计模式，即Runnable接口；
    3) 前面讲的直接继承Thread的方法其实很肤浅，并没有理解Thread的机制，因此这里就讲一下Thread的机制：
         i. Thread类其实是一个控制和任务分离的设计模式；
         ii. 每个Thread都必须要有一个target目标，target目标就代表任务；
         iii. Thread的run方法其实就是调用了target的run；
         iv. target是一个实现了Runnable接口的对象，Runnable接口里只有一个接口方法，即run()；
         v. 因此Thread还有另外一个版本的构造器：
             a. Thread(Runnable target); // 指定该线程的任务target
             b. Thread(Runnable target, String name); // 指定任务的同时还指定线程的名称
             c. Thread(String name); // 当然也有只指定线程名称的构造器
         vi. 也就是说如果要用多线程完成多种不同的任务，那么程序员只需要关注任务本身就行了，即为每个任务实现一个Runnable接口（其实就是相当于编写多个C语言run函数了），而不需要编写很多个继承Thread的类了；
！！因此上一节使用的方法默认target是null的，即将Thread的run方法中对target的run的调用直接改为任务本身的内容，这就加大了线程类和任务内容的耦合，不利于模块化开发，因此Runnable这样的线程、任务分离的模式更加优秀，应该收首先考虑使用这种方法；
    4) 这种线程管理、任务内容分离的模式的好处：
         i. 可以让程序员只关注线程要完成的任务；
         ii. 而线程是如何管理的，则是Thread类应该完成的事，这些功能已经在Thread中实现了，都是自动完成的，不需要程序员关心；
         iii. 这样就可以在一定程度上将线程管理+线程任务的问题转化成只需要关注顺序执行（即面向过程变成）的线程任务了；
    5) 使用Runnable的另一个最大的优点：可以使用Lambda表达式，由于Runnable只有一个接口方法，因此可以使用Lambda表达式，大大编程，可以在下面的例子中体现；
    6) 示例：
public class Test {

	static class MyThread implements Runnable {

		private int i = 0;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (i < 100) {
				System.out.println(Thread.currentThread().getName() + " " + i++);
			}
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for (int i = 0; i < 100; i++) {
			System.out.println(Thread.currentThread().getName() + " " + i);
			if (i == 20) {
				MyThread t = new MyThread();
				new Thread(t, "MyThread-1").start();
				new Thread(t, "MyThread-2").start();
				new Thread(() -> {
					for (int j = 0; j < 50; j++) {
						System.out.println(Thread.currentThread().getName() + " " + j++);
					}
				}, "lala").start();
			}
		}
	}

}
！这里分别是用了内部类和Lambda表达式两种形式来实现，明显可以看到Lambda表达式更加简洁，只不过使用内部类可以定义对象的私有变量，而Lambda表达式只能定义局部变量，因此内部类可以完成更加复杂的设计；
！！上面程序的接口可以看到两个使用t作为target的线程的i的变化是连续的，这就说明这两个线程共享了同一个变量i，这是显然的，因为i就在target t中，t这个对象只有一个，因此用到的i必然也是来自于同一个t中，因此多个线程使用同一个target时可以共享target中的私有数据；

4. Callable+FutureTask：
    1) 这种方式最主要解决的问题是线程任务的返回值问题；
    2) 可以看到Runnable为target提供的run方法（包括Thread自己的run方法）都是不能返回值的，这有时可能会不是很方便；
！！或者说如果线程能返回任务的计算结果是多么的方便啊啊啊！否则就要通过其它非常复杂的方法获取线程的计算结果了；
    3) 但是Java的模仿者C#/.Net就允许run返回结果；
    4) 因此从Java 5开始也提供了可返回计算结果的线程开辟方式，并且锦上添花的是还允许抛出异常，异常类型自己决定；
    5) 首先看Callable接口，它的作用就是Runnable的作用，提供一个任务体（只用来实现任务本身的内容），只不过和run相比它能返回结果：
@FunctionalInterface
public interface Callable<V> {
    V call() throws Exception;
}
！可以看到它是一个模板类，里面唯一的方法call就用来实现任务本身的内容，只不过可以返回任意模板所代表的类型V；
！！可以看到抛出的是笼统的Exception异常，具体在代码中抛出什么异常根据需要决定；
    6) 但是Callable并不是Runnable的子接口，这就意味着不能用Callable对象作为Thread的target，因为不论用何种方法建立线程，最终都是要基于Thread的；
    7) 因此Java提供了FutureTask接口，该接口包装了Callable对象，并且该接口是Runnable的子接口，因此FutureTask对象可以作为Thread的target：
        i. 它必须是一个模板类，因为它里面包装的Callable对象就是一个模板对象，它的全称是FutureTask<V>；
        ii. 构造器：FutureTask<V>(Callable<V> callable); // 即用一个callable对象来初始化它
    8) 最后由FutureTask创建的对象就可以作为Thread的target来创建线程了；
    9) 取得任务的计算结果：当Thread.start()返回后，调用FutureTask的get()方法就能返回计算结果了，其原型是：public V get();
    10) 从代码实现角度理解就是：要让任务返回结果不能直接使用run，因此要重新编一个能返回值的方法作为新的任务接口，但这种接口又不是Runnable的子接口，因此又要用一个继承了Runnable的接口来包装该任务实现，为了能获取计算结果需要在该包装器中增加一个能返回结果的方法，道理就是这么简单；
    11) 其实FutureTask这样理解就行，就是指将来要执行的任务，它是Thread的target，它里面封装了可以返回结果的任务，在它的run中调用了call方法，并保存了计算接口供get方法返回；
    12) 示例：
public class Test {

	class Call implements Callable<Integer> {

		@Override
		public Integer call() throws Exception {
			// TODO Auto-generated method stub
			// return null;
			int i = 0;
			while (i < 100){
				System.out.println(Thread.currentThread().getName() + " " + i++);
			}
			return i;
		}

	}

	public void init() {
		FutureTask<Integer> taskTarget1 = new FutureTask<Integer>(new Call());
		FutureTask<Integer> taskTarget2 = new FutureTask<Integer>(() -> {
			int i = 0;
			while (i < 88) {
				System.out.println(Thread.currentThread().getName() + " " + i++);
			}
			return i;
		});

		for (int i = 0; i < 99; i++) {
			System.out.println(Thread.currentThread().getName() + " " + i);
			if (i == 20) {
				new Thread(taskTarget1, "task 1").start();
			}
			if (i == 25) {
				new Thread(taskTarget2, "task 2").start();
			}
		}

		try {
			System.out.println("task 1 = " + taskTarget1.get());
			System.out.println("task 2 = " + taskTarget2.get());
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Test().init();
	}

}
！这里分别使用了普通方式和Lambda表达是的方式实现，可以看到Lambda表达式非常简洁；

！！简单地来讲就是FutureTask包含以一些内容：
FutureTask<V> implements Runnable {
	V returnValue;
	Callable<V> callable;
	FutureTask(Callable<V> callable) { this.callable = callable; }
	void run() { returnValue = callable.call(); }
	V get() { return returnValue; }
}
！即FutureTask包装了一层Runnable，只不过FutureTask还多了一个接受并保存返回值的功能；
！！而线程任务放在Callable里，让FutureTask的run来调用；
！！！其实该问题的解决思路就是多一层包装而已！

！！通常建议使用Runable（不返回值时）和Callable/Future（返回结果/抛出异常），因为其不仅设计模式清晰分工明确，而且多个线程可以共享同一个target，轻松实现线程间的数据共享；
