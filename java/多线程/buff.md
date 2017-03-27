1. 线程组：
    1) ThreadGroup类，Java用来批量管理线程的集合单位；
    2) 线程组的3大性质：
         i. 每个线程必须要有一个所属的线程组，可以再创建线城时指定（重载版本的构造器），如果没有指定则分配到默认线程组（父线程所在的线程组）；
         ii. 线程所属的线程组一旦确定就不能再改变，Java并没有提供修改线程组的方法；
         iii. 每个线程组必须要有一个名称，而且必须由开发人员指定（构造器中指定），名称一旦确定就不得再修改（没有支持修改的方法）；
    3) 线程组构造器：
         i. ThreadGroup(String name); // 指定线程组的名称
         ii. ThreadGroup(ThreadGroup parent, String name); // 指定父线程组，以及该线程组的名称
！线程组可以拥有父线程组，这使得线程组可以分级管理；
    4) 创建线城时指定所属线程组：第一个参数都是线程组
         i. Thread(ThreadGroup group, Runnable target);
         ii. Thread(ThreadGroup group, Runnable target, String name);
         iii. Thread(ThreadGroup group, String name);
    5) 获得线程组的名称：String ThreadGroup.getName();
    6) 获得一个线程所属的线程组：ThreadGroup Thread.getThreadGroup();
    7) ThreadGroup提供的控制组内线程的常用方法：
         i. int activeCount(); // 返回当前组中处于活动状态的线程数量（就绪、运行、阻塞）；
         ii. void interrupt(); // 中断组内的所有线程
         iii. boolean isDaemon(); // 判断该线程组是否是后台线程组
         iv. void setDaemon(boolean daemon); // 将线程组设置成后台线程组（true），后台线程组的特点是组内最后一个线程死亡后线程组自动死亡
         v. void setMaxPriority(int pri); // 设置组内线程的最大优先级，如果在调用之前组内有些线程的优先级比pri大那么这些线程将不受影响，设置之后里面的线程无论怎么设置，优先级都不会大于pri了
    8) 测试：
public class Test {

	public static void main(String[] args) {
		ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();
		System.out.println("Main thread group: " + mainGroup.getName());
		System.out.println("Main thread group is " + (mainGroup.isDaemon()? "": "not ") + "daemon group");

		ThreadGroup tg = new ThreadGroup("New Daemon Thread Group");
		tg.setDaemon(true);
		Thread t = new Thread(tg, () -> {
			for (int i = 0; i < 10; i++) {
				System.out.println(Thread.currentThread().getName() + " i = " + i);
			}
		}, "New Thread");
		t.start();
		System.out.println(t.getName() + "'s group is " + t.getThreadGroup().getName());
	}
}
！可以看到main方法主线程的线程组名也是main，并且不是后台线程；

2. 线程/组内异常处理：
    1)
