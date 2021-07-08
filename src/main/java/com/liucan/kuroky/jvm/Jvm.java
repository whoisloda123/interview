package com.liucan.kuroky.jvm;

/**
 *  1.为何jvm最大内存不建议大于32G
 *   a.设置最大内存小于32G采用指针压缩机制，将指针压缩为4个字节（64位8个字节，32位4个字节）
 *   b.4个字节最大可表示2的32次方4个G的对象数量，而一个对象默认按照8个字节对齐，则最大可表示32G的对象
 *   c.如果jvm最大内存大于32G，则会表示不了，不采用指针压缩机制，这样导致指针其实也占内存
 *
 *  2.java对象内存布局
 *     a.对象头（Header）：哈希码（HashCode），GC分代年龄，锁状态标志，线程持有的 锁，偏向线程ID，偏向时间戳
 *     b.实例数据（Instance Data）：存储的是对象真正有效的信息
 *     c.对齐填充（Padding）：过对齐填充来补全是8字节的整数倍
 *
 *  32.gc
 *    一.触发条件
 *     1.minor gc：
 *        a.分为1个较大的Eden区，2个较小的Survivor区，经过在Survivor区的不停转移来进行gc，Eden区满了，清理并将还存在的对象放入
 *          Survivor1区， Survivor1区满了，清理并将Eden和Survivor1还存在的对象全部放入Survivor2，如此循环反复（Survivor区始终有一个是空的）， 寿命长的对象
 *          被转移到老年区
 *        b.该方法就是停止-复制算法,eden和survivor大小比例默认8:1:1
 *     2.major gc：
 *       a.由Eden区、 from survivor区向to survivor区复制时，对象⼤⼩⼤于to survivor可⽤内存，该对象转存到⽼年代，先尝试Minor GC，如果空间还是不⾜Major GC。
 *       b.如果Major GC后还是不⾜ 就会OOM。
 *       c.发⽣Major GC，通常伴随Minor GC，Major GC的速度⼀般会⽐Minor GC的速度慢10倍以上
 *     3.per gc:
 *        a.该类所有的实例已经被回收
 *        b.加载该类的ClassLoader已经被回收
 *        c.该类对应的java.lang.Class对象没有在任何地方别引用，无法在任何地方通过反射访问该类的方法
 *     4.full gc（minor gc + major gc）
 *           a.调用System.gc时，系统建议执行Full GC，但是不必然执行
 *           b.由Eden区、 from survivor区向to survivor区复制时，对象⼤⼩⼤于to survivor可⽤内存，则把该对象转存到⽼年代，且⽼年代的可⽤内存⼩于该对象⼤⼩
 *           c.通过Minor GC后进⼊⽼年代的平均⼤⼩⼤于⽼年代的可⽤内存
 *           d.老年代空间不足(⽼年代空间只有在新⽣代对象转⼊及创建为⼤对象、⼤数组时才会出现不⾜的现象)
 *           e.方法区空间不足
 *   二.如何判断对象是否可以回收或存活
 *     https://blog.csdn.net/u010002184/article/details/89364618
 *       a.引用计数法：每个对象有个计数引用，如果为0，则可以回收，致命缺点：不能解决循环引用问题
 *       b.可达性分析法
 *         1.通过定义的GC-Root一直对引用的对象向下遍历，形成一个引用链，当发现一些对象不可达时，则认为该对象不可用需要回收
 *         2.GC Root的对象:vm虚拟机栈对象,静态对象,常量对象,native本地方法栈对象
 *       c.被可达性分析法命中的对象不一定会被回收，需要2次标记之后才会被回收
 *           a.可达性分析法后标记
 *           b.在可达性分析法标记的对象，里面判断对象的finalize方法里面是否有重新建立引用链关系（当前对象又被其他地方引用）
 *              如果有则对象逃离本次回收，继续存活（该自救的机会只有一次，因为一个对象的finalize()方法最多只会被系统自动调用一次）
 *    二.算法：
 *          a.停止复制算法:适合对象很快会回收，存活对象小
 *          a.标记-清除算法：标记所有需要回收的对象，再清除标记对象，坏处会产生很多内存碎片
 *          b.标记-整理算法：标记所有需要回收的对象，然后将存活对象向一端一端，清除掉其他内存，好处是不会产生内存碎片，坏处是效率较低需要大量的复制
 *    三.收集器
 *          a.新生代
 *              Serial New收集器(单线程停止复制算法)
 *              ParNew收集器（多线程停止复制算法）
 *              Parallel Scavenge收集器（多线程停止复制算法）
 *            Parallel Scavenge收集器关注的是吞吐量（垃圾器收集的时间和总运行时间比例），虚拟机运行在Server模式下的默认垃圾收集器，
 *            而其他2个关注的是每次停顿时间， 在注重吞吐量以及CPU资源敏感的场合，都可以优先考虑Parallel Scavenge收集器+Parallel Old收集器的组合
 *          b.老年代
 *              Serial Old收集器（单线程标记-整理算法）
 *              Parallel Old收集器（多线程标记-整理算法）
 *              CMS（Concurrent Mark Sweep）收集器
 *                  1.以获取最短回收停顿时间为目标的收集器。使用标记 - 清除算法
 *                  2.过程：
 *                      初始标记：标记gc root对象，速度快，需要stw
 *                      并发标记：在标记的gc root对象里面，通过可达性分析法标出需要存活和回收的对象，耗时长，不需要stw
 *                      再次标记：修正并发标记里面的因用户线程导致变动的对象，需要stw
 *                      并发清除：并发清除需要回收的对象，不需要stw（由于不需要移动存活对象，可以并发）
 *                   由于最耗费时间的并发标记与并发清除阶段都不需要暂停⼯作，所以整体的回收是低停顿的。
 *                  3.优点：并发收集，低停顿
 *                  4.缺点：
 *                    a.同步标记的会使用多线程耗费资源，CMS收集器对CPU资源⾮常敏感
 *                    b.产生内存碎片，收集结束时有大量的内存碎片产生，可能会导致空间不够而提前触发full gc，
 *                      可通过 -XX:+CMSFullGCsBeforeCompaction，设置执行多少次不压缩的Full GC后，来一次压缩整理
 *                      既然会造成内存碎⽚,为什么不把算法换成Mark Compact(标记复制)：因为并发清除需要和用户线程同步进行（保证用户的内存可以用），
 *                    c.无法处理浮动垃圾，并发清理过程中，用户线程又产生新的垃圾，只能等到下一次回收，这部分垃圾称为浮动垃圾
 *                  5.参数：
 *                      -XX:+UseConcMarkSweepGC
 *                      -XX:CMSInitiatingOccupancyFraction： 设置堆内存使⽤率的阀值，⼀旦达到该阀值，便开始进⾏回收cms
 *                      -XX:+UseCMSCompactAtFullCollection：⽤于指定在执⾏完FullGC后对内存空间进⾏压缩整理
 *                      -XX:CMSFullGCsBeforeCompaction：设置在执⾏多少次Full GC后对内存空间进⾏验收整理
 *                      -XX:ParallelCMSThreads：设置CMS的线程数量，CMS默认启动的线程数(CPU数量+3)/4
 *           c.G1（garbage first,全功能垃圾收集器）:标记整理算法
 *              1.概念：
 *                  a.堆内存分为很多区域（几千多个左右），每个分区可能是青年代的eden或survivor区，老年代区，年轻代，老年代的概念还在，只是逻辑上的概念，物理上不分
 *                  b.在延迟可控的情况下获得尽可能⾼的吞吐量
 *                  c.能建⽴可预测的停顿时间模型
 *              2.阶段：
 *                  a.老年代执行阶段：初始标记，并发标记，重新标记，复制/清除，和cms差不多
 *                  b.老年代的清除算法有点像CMS算法，青年代的清除算法有点像停止复制算法，只是在region里面进行的
 *                  c.mixed gc：回收所有年轻代对象和部分老年代对象，由参数 -XX:InitiatingHeapOccupancyPercent=n 决定。默认：45%
 *              3.记忆集 Remembered Set,卡表
 *                  ？
 *              4.优点：
 *                  a.并行，并发
 *                  b.可预测的停顿时间模型：
 *                      1.在给定的停顿时间下，总能选择一组恰当的region来回收，之所以能建⽴可预测的停顿时间模型，是因为它可以有计划地避免在整个java堆中进⾏全区域的垃圾收集
 *                      2.G1跟踪各个Region⾥⾯的垃圾堆积的价值⼤⼩，后台维护优先列表，每次根据允许的收集时间，优先回收价值最⼤的Region
 *                      3.停顿模型是以衰减均值（Decaying Average）为理论基础来实现的(根据每个 Region 的回收耗时、记忆集中的脏卡数量等，分析得出平均值、标准偏差)
 *              5.缺点：
 *                  a.G1内存占⽤和额外执⾏负载都要⽐CMS要⾼
 *                  b.⼩内存应⽤上， CMS ⼤概率会优于 G1，⼤内存应⽤上， G1 则很可能更胜⼀筹，临界值6-8g
 *              6.参数：
 *                  -XX:+UseG1GC
 *                  ‐XX:G1HeapRegionSize：默认是堆内存的1/2000
 *                  ‐XX:MaxGCPauseMillis：默认是200ms
 *                  ‐XX:InitiatingHeapOccupancyPercent ：到堆内存的默认45%则进行gc（老年代的并发标记）
 *                  -XX:ParallelGCThread 设置STW⼯作线程数的值。最多设置为8
 *                  -XX:ConcGCThreads 设置并发标记的线程数。设置为CPU数量的1/4左右
 *
 *  9.jvm空间担保
 *      a.主要是在年轻代在gc的时候，可能会空间不足，用老年代的空间做担保,将新生代存活对象放入老年代,新进入对象放入新生代
 *      b.如：minor GC期间，虚拟机发现eden space的三个对象（6MB）又无法全部放入Survivor空间(Survivor可用内存只有1MB)
 *      a.判断old最大连续space > new max space || old max space > 历次进入的老年代的平均对象大学，.然后才进行yong gc 否则full gc
 *
 *  10.安全点（safe point）和安全域（save region）（或者说，如何让所有线程停止的？）
 *      https://www.cnblogs.com/newAndHui/p/12246015.html
 *      a.安全点
 *          所有用户线程到最近的safe point之后，休眠，然后执行gc
 *          一般选择执行时间比较长的或者异常跳转作为safe point
 *      b.如果检测所有线程都到了safe point 了？
 *          jvm设置一个中断标志，用户线程跑到safe point是轮询标志，如果为true则挂起当前线程
 *          当gc完后中断标志设置为false，线程继续执行
 *      c.安全域：防止线程sleep的时候，无法处理中断请求，在⼀段代码⽚段中，对象的引⽤关系不会发⽣变化，在这个区域中的任何位置开始GC都是安全的
 *
 *  11.运行内存分布
 *      a.堆区
 *      b.方法区：存储已加载的类信息（版本，field，方法，接口等等），常量池（final常量，静态常量）等等
 *      c.虚拟机栈：普通方法栈，方法执行是一个入栈和出栈的过程
 *          a.栈由一系列帧组成（因此Java栈也叫做帧栈）先进后出的数据结构
 *          b.每一次方法调用创建一个新的帧，并压栈
 *      d.本地方法栈：native方法栈
 *      e.程序计数器（PC寄存器）：记录当前线程执行的字节码到第几行
 *      其中堆区和方法区线程共享，其他非线程共享
 *
 *   12.jvm调优
 *      a.常用参数
 *              –Xms4g -Xmx4g –Xmn1200m –Xss512k -XX:NewRatio=4 -XX:SurvivorRatio=8 -XX:PermSize=100m -XX:MaxPermSize=256m -XX:MaxTenuringThreshold=15
 *              -XX:MaxDirectMemorySize=1G -XX:+DisableExplicitGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps
 *              -Xloggc:/home/hadoop/gc.out
 *              -XX:+UseGCLogFileRotation
 *              -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=512k
 *              -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/Users/hadoop
 *      b.为什么一般堆的初始大小和最大大小设为一样的
 *         为了减少堆扩容的时候，消除扩容时候stop-the-world的，表现在应用上面可能是有时候会出现卡顿，建议扩⼤⾄3-4倍FullGC后的⽼年代空间占⽤
 *      c.jdk命令行
 *         jps:查看java进程
 *         jstat：查看java进程相关信息
 *         jmap:查看java堆内存相关信息
 *         jstack：查看java线程堆栈信息
 *         jvisualVM:在线查看运行jvm相关信息，包括内存，堆等信息，基本上包含了上面的命令
 *      d.内存占用高，频繁出现fullgc的原因和解决版办法
 *         a.打开打印gc日志选项，查看gc日志，可以通过gc easy在线网址解析gc日志，分析gc信息
 *         b.top -Hp pid找到进程占用cpu高的线程
 *         c.jstack pid|grep tid找到线程堆栈信息，查看cpu占用高原因
 *
 *   33.类加载
 *    一.过程：jvm类加载过程包括 加载-链接（校验-准备-解析）-初始化
 *      1.加载：
 *          a.class文件加载内存
 *          b.将静态数据结构(数据存在于class文件的结构)转化成方法区中运行时的数据结构(数据存在于JVM时的数据结构)
 *          c.堆中生成java.lang.Class对象，代表加载的对象，作为数据访问入口
 *      2.链接
 *          a.验证：确保加载的类符合规范和安全
 *          b.准备：为static变量分配空间，设置变量初始值，不包括static fanil对象(直接在编译的时候就赋值了)
 *          c.解析：将常量池的符号引用（如符号引用是：类的全路径）转换为直接引用（具体类的指针）
 *      3.初始化
 *          a.执行类构造器<clinit>()方法,对静态变量手动赋值，执行静态语句块static{}
 *      4.clinit和init区别
 *          参考：https://blog.csdn.net/u013309870/article/details/72975536
 *          a.init是对象构造器方法，在new一个对象时候，调用构造函数
 *          b.  1.clinit是类构造器方法，在类加载的初始化阶段，只会加载一次，执行类变量赋值和静态语句块
 *              2.子类clinit执行会保证父类的clinit执行
 *              3.接口的clinit执行不会执行父接口的clinit方法，只有父接口定义变量使用才会初始化
 *              4.接口实现类初始化一样不会执行接口的clinit方法
 *
 *    二.类加载器
 *      1.加载器
 *          a.启动类加载器：Bootstrap ClassLoader,加载java_home/lib下的class类库
 *          b.扩展类加载器：Extension ClassLoader,加载JAVA_HOME/lib/ext下的class类库
 *          c.应用程序类加载器：Application ClassLoader,加载用户路径（classpath）上的类库
 *      2.机制:双亲委派
 *      https://blog.csdn.net/Dopamy_BusyMonkey/article/details/79739748
 *          a.比如加载位于rt.jar包中的类java.lang.Object，不管是哪个加载器加载这个类，最终都是委托
 *            给顶层的启动类加载器进行加载，这样就保证了使用不同的类加载器最终得到的都是同样一个Object对象
 *          b.如果不使用这种委托模式，那我们就可以随时使用自定义的String来动态替代java核心api中定义的类型
 *      3.破坏双亲委派模型
 *          a.某些情况下父类加载器需要加载的class文件由于受到加载范围的限制，父类加载器无法加载到需要的文件需要委托子类加载器进行加载。
 *          b.DriverManager（也由jdk提供,启动类加载器加载）要加载各个第三方实现了Driver接口的实现类，启动类加载器只能委托子类加载器加载
 *          c.通过Class.forName来加载类
 *      4.Class.forName和ClassLoader.loaderClass区别
 *          Class.forName得到的class是已经初始化完成的，ClassLoader.loaderClass得到的class是还没有链接的
 *
 * 三.jit
 *  jvm执行代码
 *      a.解释执行
 *      b.jit:动态编译,在运行时编译生成可运行的数据,只有热点代码才会动态编译（如被多次调用的代码）
 *  逃逸分析：分析一个对象的引用范围
 *      如分析方法内的对象引用是否会被外部引用，来决定是否需要在栈上分配而不是队上
 *
 * 26.Reference（强引用，软引用，弱引用，虚引用,引用队列）
 *      a.StrongReference强引用，经常用到，只要强引用还在就GC不会回收，可用赋值null方式手动回收
 *      b.SoftReference软引用,有用但是不是非必须的对象，只有在内存不足的时候才会回收该对象，可以解决OOM内存溢出情况
 *        可用来实现内存敏感的高速缓存,比如网页缓存、图片缓存等。使用软引用能防止内存泄露
 *      c.WeakReference弱引用,弱引用的生命周期较软引用更加短暂,GC进行回收的时候，不管当前内存空间是否足够，都会回收
 *          a.在“引用计数法”的垃圾回收机制中，能避免“循环引用”，因为 Weak references are not counted in reference counting
 *          b."key-value"形式的数据结构中，key 可以是弱引用。例如 WeakHashMap
 *          c.观察者模式（特别是事件处理）中，观察者或者 listener 如果采用弱引用，则不需要显式的移除
 *          d.缓存
 *      d.PhantomReference虚引用，该应用并不能获取到任何对象，也不会影响对象生命周期，主要是和引用队列一起使用，监控对象被回收的时候，做一些额外处理
 *          a.通过虚引用可以知道一个对象何时被清除出内存。事实上，这也是唯一的途径
 *          b.防止对象在 finalize 方法中被重新“救活”（可参考《深入理解 Java 虚拟机》一书）
 *      e.ReferenceQueue引用队列，当引用对象所引用的值被回收了，该引用对象会被放到引用队列里面，不过需要我们手动处理来回收该引用对象，如WeakHashMap
 *        引用队列一般和软引用，弱引用，虚引用一起用
 *
 *  四.堆外内存
 */
public interface Jvm {

}
