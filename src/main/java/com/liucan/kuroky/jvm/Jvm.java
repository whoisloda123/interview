package com.liucan.kuroky.jvm;

/**
 * @author liucan
 * @date 5/22/21
 */
public interface Jvm {
    /* *
     *
     *  19.JMM（java内存模型）
     *      有时间了解一下？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？
     *


     *

     *
     *

     *
     *   31.jvm内存区域分配和gc（garbage collection）机制
     *      参考：https://www.cnblogs.com/zymyes2020/p/9052651.html
     *      https://www.cnblogs.com/xiaoxi/p/6486852.html
     *      gc青年代，老年代，永久代理论是基于大多数对象的很快就会变得不可达，只有极少数情况会出现旧对象持有新对象的引用
     *      java,gc没有使用引用计数法来回收内存，引用计数法简单，高效，但是致命问题不能解决循环引用问题
     *
     *     一.gc
     *      1.stop-the-world:进行gc的时候，除了gc线程外其他线程都必须要停止下来，来进行gc工作，gc调优通常就是为了改善stop-the-world时间
     *      2.新生代：所有刚开始new的对象都会放入此，分为1个较大的Eden区，2个较小的Survivor区，经过在Survivor区的不停转移来进行gc，Eden区满了，清理并将还存在的对象放入
     *        Survivor1区， Survivor1区满了，清理并将Eden和Survivor1还存在的对象全部放入Survivor2，如此循环反复（Survivor区始终有一个是空的）， 寿命长的对象
     *        被转移到老年区，该方法就是停止-复制算法
     *      3.老年代：
     *          a.标记-清除算法：标记所有需要回收的对象，再清除标记对象，坏处会产生很多内存碎片
     *          b.标记-整理算法：标记所有需要回收的对象，然后将存活对象向一端一端，清除掉其他内存，好处是不会产生内存碎片，坏处是效率较低需要大量的复制
     *          c.一般采用标记-整理算法
     *      4.永久代（方法区）：方法区主要回收内容是废弃常量和无用的类，满足回收需要类是无用类，无用类需要满足以下3个条件，
     *          a.该类所有的实例已经被回收
     *          b.加载该类的ClassLoader已经被回收
     *          c.该类对应的java.lang.Class对象没有在任何地方别引用，无法在任何地方通过反射访问该类的方法
     *        满足以上3个可以进行垃圾回收，但并不是马上就回收
     *      5.收集器：一般jvm是HotSpot
     *          新生代一般用停止复制算法，老年代一般用标记-清除和标记-整理算法
     *          a.新生代
     *              Serial New收集器(单线程停止复制算法)
     *              ParNew收集器（多线程停止复制算法）
     *              Parallel Scavenge收集器（多线程停止复制算法）
     *            Parallel Scavenge收集器关注的是吞吐量（垃圾器收集的时间和总运行时间比例），虚拟机运行在Server模式下的默认垃圾收集器，
     *            而其他2个关注的是每次停顿时间
     *          b.老年代
     *              Serial Old收集器（单线程标记-整理算法）
     *              Parallel Old收集器（多线程标记-整理算法）
     *              CMS（Concurrent Mark Sweep）收集器
     *                  1.以获取最短回收停顿时间为目标的收集器。使用标记 - 清除算法
     *                  2.缺点是在同步标记的会使用多线程耗费资源
     *                  3.在同步标记过程中产生新的对象，只能在下一次清除，带来的问题是如果这次失败了，那么下一次会很多，导致stop-the-world的时间很长
     *                  4.执行可以分为四个阶段：初始标记（Initial Mark）、并发标记（Concurrent Mark）、再次标记（Remark）、并发清除
     *              G1收集器:标记整理算法
     *              https://blog.csdn.net/j3T9Z7H/article/details/80074460
     *              https://blog.csdn.net/moakun/article/details/80648253
     *              http://www.importnew.com/23752.html
     *                  1.堆内存分为很多区域（几千多个左右），每个分区可能是青年代的伊甸园区或survivor区，老年代区，
     *                          年轻代，老年代的概念还在，但是只是逻辑上的概念，物理上已经不分了
     *                  2.执行阶段：初始标记，并发标记，重新标记，复制/清除
     *                  3.老年代的清除算法有点像CMS算法，青年代的清除算法有点像停止复制算法
     *            在注重吞吐量以及CPU资源敏感的场合，都可以优先考虑Parallel Scavenge收集器+Parallel Old收集器的组合
     *       6.注意：
     *         可能存在年老代对象引用新生代对象的情况，如果需要执行Young GC，则可能需要查询整个老年代以确定是否可以清理回收，这显然是低效的。解决的方法是，
     *         年老代中维护一个512 byte的块——”card table“，所有老年代对象引用新生代对象的记录都记录在这里。Young GC时，只要查这里即可，
     *         不用再去查全部老年代，因此性能大大提高
     *       7.何时触发young gc和full gc
     *          a.yong gc:伊甸园区满的时候
     *          b.full gc:
     *              1.青年代进入老年代的时候，老年代的剩余空间不足
     *              2.system.gc()
     *              3.永久代的空间不足
     *              4.cms gc时因浮动垃圾太多，空间不足，也会full gc
     *       8.如何判断对象是否可以回收或存活
     *       https://blog.csdn.net/u010002184/article/details/89364618
     *          a.引用计数法：每个对象有个计数引用，如果为0，则可以回收，致命缺点：不能解决循环引用问题
     *          b.可达性分析法
     *              1.通过定义的GC-Root一直对引用的对象向下遍历，形成一个引用链，
     *                  当发现一些对象不可达时，则认为该对象不可用需要回收
     *              2.GC Root的对象
     *                  a.jvm虚拟机栈对象
     *                  b.静态对象
     *                  c.常量对象
     *                  d.native本地方法栈对象
     *      9.jvm空间担保，主要是在年轻代在gs的时候，可能会空间不足，用老年代的空间做担保，判断老年代最大连续的空间是否
     *        大于年轻代最大空间或者老年代的最大空间是否大于历次进入的老年代的平均对象大学，然后才进行yong gc 否则full gc
     *      10.cms和g1的特点和区别
     *       https://juejin.cn/post/6844903974676463629
     *     二.运行内存分布
     *      1.堆区
     *      2.方法区：存储已加载的类信息（版本，field，方法，接口等等），常量池（final常量，静态常量）等等
     *      3.虚拟机栈：普通方法栈，方法执行是一个入栈和出栈的过程
     *          a.栈由一系列帧组成（因此Java栈也叫做帧栈）先进后出的数据结构
     *          b.每一次方法调用创建一个新的帧，并压栈
     *      4.本地方法栈：native方法栈
     *      5.程序计数器（PC寄存器）：记录当前线程执行的字节码到第几行
     *      其中堆区和方法区线程共享，其他非线程共享
     *
     *     三.jvm调优
     *      https://www.jianshu.com/p/4b4519f97c92
     *      1.看下jvm相关的书
     *      2.一般jvm调优的话，就是java -Xmx3550m -Xms3550m -Xmn2g -Xss128k -XX:ParallelGCThreads=20 -XX:+UseConcMarkSweepGC -XX:+UseParNewG
     *      3.调优常用命令：
     *          -Xms:初始内存大小
     *          -Xmx:最大内存大小
     *          -Xss:每个线程堆栈大小
     *          -Xmn:年轻代大小
     *          -XX:NewSize=n设置年轻代大小
     *          -XX:NewRatio=n:设置年轻代和年老代的比值
     *          -XX:MaxPermSize=n:设置持久代大小
     *          -XX:+UseSerialGC:设置串行收集器
     *       4.一般堆的初始大小和最大大小设为一样的
     *       为什么了？为了减少堆扩容的时候，消除扩容时候stop-the-world的，表现在应用上面可能是有时候会出现卡顿
     *       5.频繁出现fullgc的原因和解决版办法？
     *       https://www.jianshu.com/p/e749782fff2b
     *
     *   32.jvm
     *    1.每个java程序运行起来就会产生一个jvm实例，java程序结束jvm实例就会消失
     *
     *   33.类加载
     *      参考：https://www.cnblogs.com/qiuyong/p/6407418.html?utm_source=itdadao&utm_medium=referral
     *      http://www.importnew.com/25295.html
     *      jvm的生命周期一个类只被加载一次
     *    一.过程：jvm类加载过程包括 加载-链接（校验-准备-解析）-初始化
     *      1.加载：
     *          a.class文件加载内存
     *          b.将静态数据结构(数据存在于class文件的结构)转化成方法区中运行时的数据结构(数据存在于JVM时的数据结构)
     *          c.堆中生成java.lang.Class对象，代表加载的对象，作为数据访问入口
     *      2.链接
     *          a.验证：确保加载的类符合规范和安全
     *          b.准备：为static变量分配空间，设置变量初始值
     *          c.解析：将常量池的符号引用（符号可以是任何形式的字面量，只要使用时能无歧义地定位到目标即可）转换为直接引用（指针）
     *      3.初始化
     *          a.执行类构造器<clinit>()方法,它将由编译器自动收集类中的所有类变量的赋值动作(准备阶段的a正是被赋值a)和静态变量与静态语句块static{}合并
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
     *          双亲委派加载，调用父类的加载器加载，如果不行才自己加载，好处是安全，如防止自己写string被jvm当做是系统的string,
     *          而且加载出来的只有一个object类
     *      3.Class.forName和ClassLoader.loaderClass区别
     *          Class.forName得到的class是已经初始化完成的，ClassLoader.loaderClass得到的class是还没有链接的
     *
     *  43.java内存模型：
     *      https://www.cnblogs.com/nexiyi/p/java_memory_model_and_thread.html
     *      1.主内存与工作内存：线程对变量的所有操作（读取、赋值）都必须在工作内存中进行，而不能直接读写主内存中的变量
     *      2.为了优化性能，会对指令进行重排序
     *      3.happens-before：
     *          a.happens-before的概念来指定两个操作之间的执行顺序,两个操作可以在一个线程之内，也可以是在不同线程之间
     *          b.可以通过happens-before关系向程序员提供跨线程的内存可见性保证
     *          c.如果一个操作happens-before另一个操作，那么第一个操作的执行结果将对第二个操作可见，
     *              而且第一个操作的执行顺序排在第二个操作之前(程序员视角)，
     *              有可能会指令重排序（JVM视角）
     *          d.具体规则:有6种
     *      4.voatile可防止指令重排序
     *
     *
     */
}