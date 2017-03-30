1. 什么是对象流：序列化/反序列化的概念
    1) 对象流是和字节流/字符流同处于一个概念体系的：
        a. 这么说字节流是流动的字节序列，字符流是流动的字符序列，那么对象流就是流动的对象序列咯？
        b. 概念上确实可以这样理解，对象流就是专门用来传输Java对象的；
        c. 但是字节和字符都是非常直观的二进制码（字节本身就是，而字符是一种二进制编码），二进制码的流动是符合计算机的概念模型的，可是对象是一个抽象的东西，对象怎么能像二进制码那样流动呢？
        d. 其实很好理解，对象流只不过是Java的API而已，表象上（方法调用等）流动的是对象，而实际上在底层肯定都是转换成二进制码流动的；
        e. 具体来说底层是将对象转换成平台无关的Java字节流进行传播的，以为对象流的类名就是ObjectInputStream和ObjectOutputStream，以stream作为后缀必然传递的是字节流；
        f. 只不过在调用对象流的read和write系列方法时不用将对象转换成字节数组传入了，而是可以直接传入对象本身，这些方法内部会自动将对象转化成字节流传递！！
    2) 为什么需要对象流：
        i. 首先，Java本身就是一个面向对象的语言，因此对象比字节/字符的使用更广泛；
        ii. 传递文本当然使用字符流，因此字符流的使用很广泛，即文本、字符串的处理、保存等应用很广，这毋庸置疑，而直接传递字节的应用（比如图像、音频、二进制文件等）可能也非常广泛，但是Java不仅仅是用来处理这两种数据的，Java真正面对最多的还是对象；
        iii. 程序往往需要在各个存储节点间传递Java对象（即Java对象的输入输出），按照传统方法要么就是使用字节流来传递要么就是用字符流来传递：
             a. 首先字符流可以排除，因为对象中可能既包含文本型数据（String等），也可能包含非文本类数据（比如字节、图像等），如果将图像这样的数据也转换成字符的话显然是行不通的；
             b. 那就只能使用字节流了，但是字节流的使用很麻烦，需要自己手动将对象内所有非字节型数据现转换成字节数据，然后将所有转换成字节的成员全部挤进一个字节数组写入（输出），而输入的时候必须先用一整个字节数组读取，然后对数组进行解析，最后再还原成原来的对象；
！！这一看上去就已经难于上青天了，谁都不愿意这样进行对象的I/O；
    3) 因此Java提供了对象流（ObjectInputStream、ObjectOutputStream）——用以自动序列化对象并传输对象：
        i. 首先了解序列化（Serialize）的概念：
           a. 即C++的串行化；
           b. 即不管传递什么数据（字节、字符、对象），在底层都必须是二进制字节流，以为计算机只能识别二进制码，因此字节就不用说了，字符肯定也要转换成二进制编码，同样对象也必须转换成二进制字节序列才能传输；
           c. 序列化就是指，把原本程序中的数据（抽象数据，如字符、对象等）转化成二进制字节序列的过程；
           d. 串行化是数据传输的前提；
！！而反序列化就是将已经保存在存储节点上的序列化的数据读取并还原成原来的Java对象咯！
        ii. Java的对象流首先可以自动序列化对象：
输出：ObjectOutputStream
           a. 当用对象流输出一个对象时会先自动解析对象中的成员；
           b. 然后自动将各个成员序列化成一个个字节数组；
           c. 然后将各数组按照成员的定义顺序拼接成一个完整的字节数组，最后用该数组传递；
输入：ObjectInputStream
           a. 当然输出的时候ObjectOutputStream可定会给对象本身以及每个对象成员做一定的身份标识；
           b. 身份标识其实就是数据的Java类型（还原对象的时候必须要知道Java类型）、以及对象的大小（必须要知道读多少个字节才能刚好把该对象读完）；
           c. 对象输入流就可以根据这些完备的信息从六中还原Java对象；
           d. 底层就是先将完整的序列化对象保存在一个字节数组中，然后根据这些信息解析数组，并还原出一个完整的Java对象；
           e. 那些身份标识其实就是序列化和反序列化的协议了，必须遵守协议才能保证序列化后能正确地反序列化；

2. 使用对象流输入输出的大致过程：
    1) 已经知道了对象流就是ObjectOutputStream和ObjectInputStream，现在介绍使用它们的大致流程；
    2) 首先I/O是有目的地的，即你要从哪儿流向哪儿，其中两点之中一点是确定的，那就是当前的程序，那么就必须指定另外一点了；
    3) 因此第一步就是要确定存储节点，因此ObjectOutputStream和ObjectInputStream是一种高级处理流，必须要包装一个具体的节点流才行；
    4) 它俩的构造器：
        i. ObjectInputStream(InputStream in);
        ii. ObjectOutputStream(OutputStream out);
！！可以用任何节点流来初始化它们；
    5) 接着就是使用对象流的read和write系列方法进行读写了：
        i. 对象流的读写系列方法很简单，不涉及字节、字节数组等；
        ii. read系列：Xxx ObjectInputStream.readXxx();
！！read之后需要根据实际的类型强制转换一下
        iii. write系列：void ObjectOutputStream.writeXxx(Xxx val);
        iv. Xxx是涵盖了Java几乎所有的基础类型（byte、int、char、boolean、double等），其中最重要的就是Object，readObject和writeObject就是输入输出Java对象的关键所在；
！！注意没有String，因为String不是基础类型，String是类，因此读写String直接用readObject、writeObject即可！！
！！也就是说，对象流不仅可以输入输出Java对象，也可以输入输出普通数据的，可见功能之强大！
！！那既然可以输入输出Java对象那为啥还要提供byte、int、double等普通数据类型的版本呢？这是为了可以实现自定义的序列化而提供的！

3. 自定义序列化：必须实现Serializable接口才能序列化
    1) 不是随便调用对象流的read和write就能随随便便输入输出一个对象的，前提必须是这个对象是可序化/可反序列化的！
    2) 即对象流必须知道这个对象该如何序列化以及反序列化，才能正确对该对象进行输入输出；
    3) Serializable接口：
         i. 必须实现该接口才能自动序列化和反序列化；
         ii. 该接口有两个要实现的方法，反别对应如何序列化和反序列化：
             a. 序列化的算法实现：private void writeObject(ObjectOutputStream out) throws IOException;
             b. 反序列化的算法实现：private Object readObject(ObjectInputStream in) throws IOException, ClassNotFoundException;
！！可以看到反序列化时并不是用构造器构造的，而是直接根据输入流生成一坨无类型的数据，然后用强制类型转换转换出来的！因此可以看到该方法会抛出ClassNotFoundException，如果没有准备好相应的类型则会抛出该异常；
！！对象流输入输出底层其实是这样调用的：
       a. ObjectOutputStream oos：oos.writeObject(obj)    ->    obj.wirteObject(oos)
       b. ObjectInputStream ois：ois.readObject(obj)    ->    obj.readObject(ois)
！！可以看到，实现的算法中就是利用obj得到的oos和ois进行输入输出；
    4) 毕竟有些读写并不是规规矩矩地原模原样地读写，比如像密码这样的信息，在输出的时候往往需要加密输出，因此读取的时候也要解密读取，像这样的情况就必须自己定义序列化和反序列化的算法了；
    5) 绝大多数的Java基础类，比如String、Date等都实现了Serializable接口，因此可以直接用对象流的readObject和writeObject读写；
    6) 所有的基础类型（int、double、boolean等）对象流也提供了相应的readXxx和writeXxx进行序列化和反序列化，因此也不用担心；
    7) 因此大多数的自定义类型的对象就要自己实现序列化和反序列化的算法了，而上面提供的基础类型的对象流输入输出就是为自定义准备的，例如以下：

    没有Serializable标记，即使实现了两个方法也不行，在oos.writeObject的时候还是会抛出NotSerializableException异常
class Member implements Serializable {
	String name;
	int age;

	public Member(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name + "(" + age + ")";
	}



	private void writeObject(ObjectOutputStream out) throws IOException { // 输出之前进行加密（序列化算法）
		out.writeObject(new StringBuffer(name).reverse()); // 名字反序加密
		out.writeInt((age << 4) + 13); // 年龄左移4位再加13加密
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException { // 输入解密（反序列化算法，就是序列化的逆过程）
		name = ((StringBuffer)in.readObject()).reverse().toString();
		age = (in.readInt() - 13) >> 4;
	}
}

public class Test implements Serializable {


	public static void print(String s) {
		System.out.println(s);
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("out.txt"))) {
			Member m = new Member("lalala", 15);
			oos.writeObject(m);
			oos.close();
		}
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("out.txt"))) {
			Member m = (Member)ois.readObject();
			print("" + m);
		}

	}
}
！！可以查看一下，虽然序列化的结果是二进制，打开会看到乱码，但不过英文字符部分还是按照Unicode进行编码的，可以看到里面倒序的"alalal"；
    8) 实现序列化和反序列化算法时的注意事项和规范：
         i. 一般要先写序列化厚些反序列化，以为序列化是一种编码过程，而反序列化是一种解码过程，一般编码逻辑优先于解码，解码是编码的逆过程，但通常不会说编码是解码的逆过程；
！！简单地将就是反序列化要参照着序列化来写；
         ii. 反序列化要按照序列化的顺序来，比如序列化是按照先String成员再int成员的顺序，那么反序列化是也是先String后int，因为序列化是一种顺序结构；

4. 自动序列化——递归序列化（其实Serializable只是一个标记接口）：
    1) 之前介绍的自定义序列化就是手动序列化，它的手动体现在需要自己手动实现序列化和反序列化的算法；
    2) 而实际上如果你对象中的所有成员都已经实现了Serializable接口了（已经可序列化了），那么该对象不实现序列化和反序列化算法也可以自动序列化；
    3) 比如：对象a中包含成员对象b，而b中包含成员对象c，c中包含成员对象d...，如果b、c、d...都已经实现了序列化算法，那么a就可以自动序列化而无需实现自己的序列化算法了，当a序列化时会按照如下方式：a -> b.writeObject -> c.writeObject -> d.writeObject....其中a -> b.writeObject表示a在序列化时会自动调用b.writeObject对b进行序列化，而b.writeObject -> c.writeObject是指b.writeObject方法中调用了c.writeObject，以此类推，也就是一层一层自动地递归调用内层对象的writeObject进行序列化，这种调用是自动的；
！具体的例子：A a(String b, B c(Date d, String e))对象，其中A类型的a对象包含成员b（String类型）和成员c（类型B），而成员c包含成员d（Date类型）和成员e（String类型），如果B已经实现了序列化/反序列化算法了，那么序列化a时可以不实现A自己的序列化/反序列化算法而自动序列化，自动序列化方式是：调用b.writeObject，再调用c.writeObject，而如果B没有实现自己的序列化算法（没有实现B的writeObject方法）也没关系，因为其成员d和e也是可序列化的，会在b.writeObject当中自动调用d.writeObject和e.writeObject；
    4) 那为什么达成序列化而可以不实现其接口方法呢？
         i. 其实Serializable是一个标记接口，其中的序列化算法和反序列化算法可以不用实现，该接口只是一个标记，表示该类是可序列话的！
         ii. 让一个类可序列化其实就只要给它一个标记就行了！即使不实现那两个算法也是可序列化的！
    5) 自动序列化的严格定义：
         i. 就是只给一个Serializable的标记，但不实现序列化和反序列化算法，就表示要使用自动序列化功能；
         ii. 如果自己实现了序列化/反序列化算法，那么在序列化/反序列化时就会调用自己实现的算法；
         iii. 如果自己没有实现序列化算法，就会递归调用下一层成员对象的序列化算法（也就是说，如果下一层成员对象也没有实现自己的序列化算法就会自动调用下下层的序列化算法）；
         iv. 因此自动序列化也称作递归序列化；
    6) 递归序列化的前提：既然递归序列化（自动序列化）是一种不自己实现序列化算法的序列化，那么其要求自然就是其所有的成员都必须是可序列化的！
！！这很好理解，如果某个成员不可序列化（没有Serializable标记），那么如何调用其writeObject/readObject方法呢？必然会抛出异常（虽然编译不会有异常！）；
！！一般，如果类中包含的都是Java的基础类或基础类型数据，一般会采用自动序列化，或者成员对象（自定义类型）已经自己实现过序列化算法了也一般会采用自动序列化；
！除非有特殊需求，比如要对对象进行加密等，这种情况下是必须使用手动序列化的（自己实现序列化算法）；

小结：如果一个对象的某个成员不可序列化，那么不管该对象有无Serializable标记都是不可序列化的，如果强行这样做会抛出异常！
    7) 自动序列化示例：很简单，就只需要一个Serializable标记即可
class Member implements Serializable {
	String name; // 可序列化的，Java已经帮你实现了
	int age; // 基础类型也是可序列化的

	public Member(String name, int age) {
		this.name = name;
		this.age = age;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name + "(" + age + ")";
	}
}

public class Test implements Serializable {


	public static void print(String s) {
		System.out.println(s);
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("out.txt"))) {
			Member m = new Member("lalala", 15);
			oos.writeObject(m);
			oos.close();
		}
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("out.txt"))) {
			Member m = (Member)ois.readObject();
			print("" + m);
		}

	}
}
！！正是因为Serializable只是一个标记接口，因此你使用Eclipse的自动override功能的时候找不到那两个序列化算法，因此那两个算法的方法接口必须全部背出来（抛出的异常等）；

5. Java序列化机制——引用序列化编号：
    1) 考虑到如下情形：
Son son = new Son("Tom", 15);
Parent father = new Parent(son, 40);
Parent mother = new Parent(son, 39);
！即两个对象持有相同的成员对象，这里father.son == mother.son（地址也是完全相同的），这种关联应用在Java（特别是数据库应用中）使用特别广泛；
！！现在如果序列化son、father、mother会不会序列化了3次son呢？如果是这样的话，那么反序列化的时候不就得到了三个son吗？那这样反序列化的结果中father、mother持有的就是不同的son了（地址不同了，三个完全不一样的内存空间了），这不就未被了关联性的初衷了吗？
！但还好Java的序列化不是这样的，它可以智能地识别这种持有相同对象的情况，并保证只序列化一次公共持有对象；
    2) Java序列化机制——引用序列化编号：
         i. 在使用writeObject时传入的其实都是引用（引用其实就是指针，而指针的值就是对象的内存地址）；
         ii. 在序列化时会对每个传入的待序列化的对象的引用分配一个序列化编号（即为每个待序列化对象的内存地址映射一个序列化编号）；
         iii. 在序列化之前会先检查该编号对应的对象是否已经序列化过了，如果序列化过了就不再序列化而是只写入该对象的序列化编号，如果没有，那就对该对象的内容进行序列化并和其编号一并写入；
         iv. 即无论是否序列化过都一定要写入编号的，如果之前没有序列化过就序列化，如果序列化过了则不序列化（但还是要写入编号）；
    3) 对于上面的例子，序列化的结果就是：son的编号1（"Tom", 15）、father编号2（编号1, 40）、mother编号3（编号1, 39）
    4) 反序列化也是一样的，会根据编号来确定对象，每个编号只对应一个对象，保证还原的时候相同编号的都还原到同一个对象，不会重复；
    5) 示例：
class Son implements Serializable {
	String name;
	int age;

	public Son(String name, int age) {
		this.name = name;
		this.age = age;
	}
}

class Parent implements Serializable {
	Son son;
	String name;
	int age;

	public Parent(Son son, String name, int age) {
		this.son = son;
		this.name = name;
		this.age = age;
	}
}

public class Test {

	public static void print(String s) {
		System.out.println(s);
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("test.buf"))) {
			Son son = new Son("Tom", 15);
			Parent father = new Parent(son, "Peter", 40);
			Parent mother = new Parent(son, "Mary", 39);
			oos.writeObject(son);
			oos.writeObject(father);
			oos.writeObject(mother);
			oos.close();
		}
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("test.buf"))) {
			Son son = (Son)ois.readObject();
			Parent father = (Parent)ois.readObject();
			Parent mother = (Parent)ois.readObject();
			print("" + (son == father.son)); // 答案都是true
			print("" + (father.son == mother.son));
		}
	}
}


6. 序列化机制的潜在危险：
    1) 由于是根据引用值来进行编号的，这就意味着只有引用（地址）才能决定对象是否会被序列化；
    2) 设想一个可变对象，已经被序列化过了，之后再改变该对象的成员的值然后再对该对象进行序列化，那么改变后的对象也不会被序列化，也只是写入它的编号而已（因为之前已经序列化过一次了）；
    3) 因此Java的对象序列化必须要遵守该法则：保证对象完全确定不再更改后再序列化，序列化过就不要再更改了！！
    4) 测试：
class Son implements Serializable {
	String name;
	int age;

	public Son(String name, int age) {
		this.name = name;
		this.age = age;
	}
}

class Parent implements Serializable {
	Son son;
	String name;
	int age;

	public Parent(Son son, String name, int age) {
		this.son = son;
		this.name = name;
		this.age = age;
	}
}

public class Test {

	public static void print(String s) {
		System.out.println(s);
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("test.buf"))) {
			Son son = new Son("Tom", 15);
			Parent father = new Parent(son, "Peter", 40);
			Parent mother = new Parent(son, "Mary", 39);
			oos.writeObject(son);
			son.name = "ChaCha"; // 序列化过之后进行更改，再尝试序列化
			oos.writeObject(father);
			oos.writeObject(mother);
			oos.close();
		}
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("test.buf"))) {
			Son son = (Son)ois.readObject();
			Parent father = (Parent)ois.readObject();
			Parent mother = (Parent)ois.readObject();
			print(son.name); // 结果是Tom而不是ChaCha，可见第二次没有被真正序列化
			print("" + (father.son == mother.son));
		}
	}
}


7. 序列化版本：
    1) 设想如果你的类在日后扩展、升级了，那你以前存储在节点中的旧版本的对象该如何读取呢？两者兼容吗？如果不兼容应该怎么办？这就涉及到序列化的版本控制问题了；
    2) 序列化类的版本号：
         i. 其实所有标记过Serializable的类都隐藏含有一个版本号：private static final long serialVersionUID;
         ii. 当然这个版本号的值是可以自己显示定义的，比如直接在类定义中写下：private static final long serialVersionUID = 2016L;  // 该类的序列化版本好就是2016了
         iii. 如果自己不显示定义该版本号，那么JVM就会根据一定的算法自己默认生成一个版本号（可能根本毫无意义，一个负20位的整数也有可能）；
    3) 版本号的作用：在序列化时会将对象所对应类的版本号也写入，而在反序列化时，JVM会查看当前该类的版本号是否和当初写入的时候相同，如果不同那么就拒绝序列化而抛出异常！
！！这就引出了一个非常重要的规矩：如果要让软件更加健壮那就必须自己手动显示定义版本号！如果你让JVM自己默认给出版本号的话，也许你换台计算机或者换个其它版本JVM得到的版本号值可能就会不同（JVM根据当前环境来计算该版本号的），这样即使是相同的代码也可能导致反序列化时的版本不兼容！
    4) JDK查看类的序列化版本号的工具：在JDK的bin目录下的serialver命令，其用法：serialver 类名  // 方可返回该类的序列化版本号，注意类名要包含完整包路径的！因此要调整好当前路径再运行该命令
！！或者执行命令：serialver -show，就会打开一个图形界面的对话框，让你填入完整的类路径，然后点显示按钮就可以在下面的文本框中显示出版本号，而该类名同样是针对当前pwd而言的！
    5) 有些对类的升级即使你不改变版本号也会导致序列化失败：
         i. 如果只是升级了方法而不改变数据成员则不受影响； // 这是必然的，因为序列化的仅仅是数据而已，不包括方法
         ii. 如果只是修改了类的静态变量则不受影响； // 这也是显然的，因为序列化的是对象而不是类本身！
         iii. 如果更新后的类只是比旧的少了一些数据成员（其它不变）则不受影响；  // 反序列化是少掉的那几个直接被舍弃
         iv. 如果更新后的类比旧的多了一些数据成员（其它不变）则不受影响；  // 反序列化是多出的数据成员用null或0来填充
！！对于其它情况（比如改变了数据成员的定义顺序，某个变量改变了类型等），即使版本号不变也会导致反序列化失败，原因很明显，那就是这些改变会直接导致旧数据填入对象后会发生成员错位的情况！
！！因此需要为这些改动提供一个新的版本，旧版本的数据就用旧版本的类来读取，要和新版本完全区分开（简单将就是无法用新版本的类去接受旧版本节点上的数据了，即不兼容）；
