线程间通信也是围绕着临界资源的竞争展开的


1. 线程通信的概念：
    1) 即线程之间以信号的方式（发送/接收信号）相互协调，协调的内容是调度；
！！通常调度都是有调度器完成的，程序员无法插手（完全是透明的），但不过在有些情况下还是有人为控制调度的需求；
    2) 说一个典型的场合：多条线程协同完成一项任务，其中线程B依赖线程A的计算结果，如果A没有计算那么B就需要等待A的结果才能执行，这里可能会想到用join线程，但是join的效率非常低，那必须得完全等待A计算完成后才能执行B，但我们希望所有的线程都能并发执行，那有没有方法能让A、B完全并发的情况下B能聪明地等待A，同时A/B访问临界资源是也不会发生冲突；
    3) 这就需要线程通信技术了，目前Java支持三种线程通信技术：
         i. 传统方法：同步器发送信号让想要访问它的线程等待（wait）或唤醒（notify），达到相互之间协调的目的；
         ii. Condition方法：和传统方法原理一样，只不过是应用在Lock锁上，Lock锁本身就是同步器，但不过Lock锁本身没有wait和notify方法，因此Lock提供了获取Condition的方法，获得的Condition具有wait和notify方法（在Condition中是signal方法）；
         iii. 阻塞队列：这其实是一个信号量模型，线程可以用生产者和消费者代表，生产者往队列里塞东西，东西塞满后就会被阻塞等待（wait），消费者从队列中取东西，取光了就会被阻塞；

2. 传统方法：
    1) 使用Object继承而来的3种方法发送信号以达到通信的目的：只有当对象被定为同步监视器的时候才能使用！！！
         i. public final void Object.wait(); // 监视器让当前控制它的线程（正在临界区执行）暂停等待（直接被阻塞），并交出监视器的控制权
         ii. public final native void Object.notifyAll(); // 监视器通知所有正在阻塞队列中等待获取它的控制权的线程（之前被wait掉的线程，必须是被同一个监控器wait掉的线程），将它们全部唤醒（进入就绪状态），但被唤醒的线程并不会立即执行，必须等到当前线程释放对监控器的锁定后才能被执行
         iii. public final native void Object.notify(); // 和notifyAll只有一点区别，不是唤醒全部被wait的线程，只是随机唤醒其中一条而已，其余都相同
    2) 示例：多个线程并发对同一个账户进行存取款，但是要求只能轮流存取，即必须一次存操作后必须是取操作，一次取操作后必须是存操作（不管是那个线程发起的），不能连续存或者取，这里用一个标志empty表示账户是否为空，存过以后为false，取过以后是true；
public class Test {

	private static boolean empty = true; // 初始化成空

	class Account {
		private void printThread() {
			System.out.println(Thread.currentThread().getName());
		}

		public synchronized void withDraw() { // 取钱
			if (empty) { // 没钱就不能取
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // 只能等待其它线程存
			}
			else {
				empty = true;
				printThread();
				notifyAll(); // 取完了以后就可以唤醒其它线程执行了
			}
		} // 但是必须等当前线程释放锁之后才能真正让其它线程执行

		public synchronized void deposit() { // 存钱
			if (!empty) { // 有钱就不能存
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // 只能等待其它线程取
			}
			else {
				empty = false;
				printThread();
				notifyAll(); // 存完了通知其它线程来取
			}
		}
	}

	private Account ac = new Account();
	public void init() {
		Runnable tarDraw = () -> {
			for (int i = 0; i < 20; i++) {
				ac.withDraw();
			}
		};
		Runnable tarDeposit = () -> {
			for (int i = 0; i < 20; i++) {
				ac.deposit();
			}
		};

		new Thread(tarDraw, "A withdraw").start();
		new Thread(tarDraw, "B withdraw").start();
		new Thread(tarDeposit, "C deposit").start();
		new Thread(tarDeposit, "D deposit").start();
	}

	public static void main(String[] args) {
		new Test().init();
	}
}
！！可以看到A/B/C/D打印顺序是乱七八糟的，但是一定是严格按照存钱->取钱的轮流顺序进行的，因此人为地在一定程度上干预了线程的调度；
！！注意同步方法的监控器就是this；

3. Condition信号：
    1) 专门用于Lock锁，Lock本身没有提供wait、notify等方法，但是Lock可以使用newCondition方法获取一个Condition对象，Condition有类似wait、notify的方法，只不过名字不同了，变成了await、signal、signalAll方法，完成的任务和wait、notify、notifyAll完全一样；
    2) 上述方法的原型：
         i. Condition Lock.newCondition(); // 返回Condition对象
         ii. void Condition.await(); // 同wait
         iii. void Condition.signal(); // 同notify
         iv. void Condition.signalAll(); // 同notifyAll
    3) 示例：上一个例子的Condition版
public class Test {

	private static boolean empty = true; // 初始化成空

	class Account {
		private final Lock lock = new ReentrantLock();
		private final Condition cond = lock.newCondition();

		private void printThread() {
			System.out.println(Thread.currentThread().getName());
		}

		public void withDraw() { // 取钱
			lock.lock();
			try {
				if (empty) { // 没钱就不能取
					try {
						cond.await();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} // 只能等待其它线程存
				}
				else {
					empty = true;
					printThread();
					cond.signalAll(); // 取完了以后就可以唤醒其它线程执行了
				}
			}
			finally {
				lock.unlock();
			}
		} // 但是必须等当前线程释放锁之后才能真正让其它线程执行

		public void deposit() { // 存钱
			lock.lock();
			try {
				if (!empty) { // 有钱就不能存
					try {
						cond.await();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} // 只能等待其它线程取
				}
				else {
					empty = false;
					printThread();
					cond.signalAll(); // 存完了通知其它线程来取
				}
			}
			finally {
				lock.unlock();
			}
		}
	}

	private Account ac = new Account();
	public void init() {
		Runnable tarDraw = () -> {
			for (int i = 0; i < 20; i++) {
				ac.withDraw();
			}
		};
		Runnable tarDeposit = () -> {
			for (int i = 0; i < 20; i++) {
				ac.deposit();
			}
		};

		new Thread(tarDraw, "A withdraw").start();
		new Thread(tarDraw, "B withdraw").start();
		new Thread(tarDeposit, "C deposit").start();
		new Thread(tarDeposit, "D deposit").start();
	}

	public static void main(String[] args) {
		new Test().init();
	}
}


4. 阻塞队列：
    1) BlockingQueue，也是一种队列，同样也实现了Queue接口，表现出队列的所有特征，但不过是用来控制线程通信的；
    2) 阻塞队列本身就是一个监控器，只不过它是一种特殊的监控器，当线程往队列里塞东西塞满时则该线程会被阻塞，当线程从队列里取东西取空时则该线程也会被阻塞；
    3) 通信机制：
         i. 线程被抽象为生产者和消费者，生产者生产东西往队列里塞，消费者从队里拿东西去消费；
         ii. 当队列满时生产者仍要塞东西时就会让队列触发wait让此时想往里塞的线程阻塞等待（让给消费者取东西），一旦队列不满时（之后被人至少取走一样东西，刚好从满到不满）队列就触发notifyAll重新唤醒之前所有因满塞而阻塞的生产者；
         iii. 同理，当队列空时消费者仍要取东西时就会让队列触发wait让此时想取东西的线程阻塞等待（让给生产者塞东西），一旦队列非空（之后被人至少塞进一样东西，刚好从空到非空）队列就触发notifyAll重新唤醒之前所有因空取而阻塞的消费者；
         iv. 这其实是一个信号量的模型（见PV操作），通常信号量控制通信的效率最最高，可以最大程度发挥并发性；
    4) 因此使用阻塞队列的时候既不需要用到synchronized关键字指定同步监视器，也不需要使用Lock显示加锁，就直接将其当普通队列用就行了，什么特殊语法也不需要，当满塞或空取时自动阻塞，状态再次改变时就自动唤醒阻塞线程，就好像没有控制一样；
    5) BlockingQueue的用法：
         i. 它直接继承自Queue模板类：public interface BlockingQueue<E> extends Queue<E>;
         ii. 塞：都是从队尾加一个元素
             a. boolean add(E e);
             b. boolean offer(E e);
             c. void put(E e);
         iii. 取：都是从队首弹一个元素
             a. E remove();
             b. E poll();
             c. E take();
！！塞和取的三个方法效果都是一样的，塞在队列满之前三方法完全一样，当满塞时a会抛出异常、b会返回false、c则会阻塞线程，同样取在队列空之前三个方法完全一样，当空取时a会抛出异常、b会返回false、c会阻塞线程；
！！可见为了达到控制线程的目的必须要用c；
         iv. 当然也保留了观察方法：查看队首元素但不删除
             a. E element(); // 空时抛出异常
             b. E peek(); // 空时返回null
    6) 由于BlockingQueue只是个接口，实际使用时都是用它的实现类，最常用的有三大类：ArrayBlockingQueue<?>（用数组实现的）、LinkedBlockingQueue<?>（用链表实现的）、PriorityBlockingQueue<?>（用优先队列实现的，元素要定义comparable方法）；
    7) 示例：
public class Test {
	private String getThread() {
		return Thread.currentThread().getName();
	}
	private void print(String s) {
		System.out.println(s);
	}

	public void init() {
		BlockingQueue<String> bq = new ArrayBlockingQueue<>(3);
		String[] foods = new String[] {
				"Apple", "Noddles", "Beef", "Corn"
		};

		Runnable producer = () -> {
			for (int i = 0; i < 20; i++) {
				try {
					Thread.sleep(200);
					bq.put(foods[i % 4]);
					print(getThread() + " " + bq);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Runnable consumer = () -> {
			try {
				Thread.sleep(200);
				bq.take();
				print(getThread() + " " + bq);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};

		new Thread(producer, "A produce").start();
		new Thread(producer, "B produce").start();
		new Thread(producer, "C produce").start();

		new Thread(consumer, "D consume").start();
		new Thread(consumer, "E consume").start();
		new Thread(consumer, "F consume").start();
	}

	public static void main(String[] args) {
		new Test().init();
	}
}
！可以看到修改队列的结果和输出结果并不是完全相符，那是因为高度的并发性所导致的；
