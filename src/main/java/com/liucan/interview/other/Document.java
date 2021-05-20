package com.liucan.interview.other;

/**
 * other学习文档
 * @author liucan
 * @version 2018/9/22
 */
public class Document {
    /* *
     *
     * 4.String
     *   String类是不可改变的，创建了String对象，值就无法改变了。如需要对字符串做很多修改，使用StringBuffer & StringBuilder 类,并且不产生多个对象
     *   StringBuilder是java5提出来的是线程不安全的，而StringBuffer是线程安全的
     *
     * 7.finalize函数是在对象析构之前调用
     *   a.一般在里面做一些释放操作，并非是java,new出来的内存，比如说文件句柄关闭，释放调用c，c++的来分配空间等
     *   b.调用该函数会引起死锁和线程挂起，因为并不存在任何一种机制可以把资源的释放与对象的生命周期完全绑定在一起，如果处理不好还会耗尽资源。
     *   c.java9已经抛弃了,不建议使用
     *
     *
     *  16.volatile
     *      参考：https://www.cnblogs.com/chengxiao/p/6528109.html
     *     a.轻量级锁
     *     b.能保证共享变量对所有线程的可见性，当写一个volatile变量时，JMM会把该线程对应的本地内存中的变量强制刷新到主内存中去
     *     c.禁止指令重排序优化
     *     d.volatile对于单个的共享变量的读/写具有原子性，但是像num++这种复合操作，volatile无法保证其原子性,可以用原子锁
     *
     *  17.fail-fast,fail-safe迭代器
     *      参考：https://blog.csdn.net/m0_37907797/article/details/80499422
     *     a.fail-fast:快速失败，在迭代过程中，如果数据结构被修改（增加、删除）了则抛出异常，实现机制是modCount和expectedCount比较不相等则抛异常
     *       而在插入和删除的时候会更新modCount
     *
     *       注意：快速失败的行为并不能得到正确的保证，一般来说，存在非同步的并发修改时，不可能做出任何坚决的保证的，fail-fast只是尽
     *       最大努力来抛出异常，所以fail-fast仅用来检查程序的bug
     *
     *     b.fail-safe：安全失败，在迭代的时候，先拷贝一份，然后在迭代，不会抛出异常，但是内容可能不是最新的
     *     c.java.util里面的容器都是快速失败，不支持异步，而java.util.concurrent包里面的容器都是安全失败，支持异步
     *
     *
     *  19.JMM（java内存模型）
     *      有时间了解一下？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？
     *
     *  20.各种数据集合
     *      参考：http://www.cnblogs.com/skywang12345/p/3323085.html
     *     一.List
     *      a.Vector和ArrayList都是基于数组的，支持动态增长，Vector是线程安全的（synchronized），ArrayList是不安全的，但效率高
     *      b.LinkList是基于双链表实现的，且可以当做栈，双端队列使用，并实现了相应的接口,而ArrayList没有，因为插入和删除的效率比较低
     *      c.实现List接口的常用类有LinkedList，ArrayList，Vector和Stack,而Stack是继承与Vector
     *      d.ArrayList实现了Serializable接口（ObjectOutputStream,ObjectInputStream操作类）,序列化writeObject先写入size，再写入元素
     *      e.CopyOnWrite（COW，写时复制）
     *      参考：https://www.cnblogs.com/dolphin0520/p/3938914.html
     *          https://yq.aliyun.com/articles/665359
     *          1.CopyOnWriteArrayList
     *             a.是Vector另外一种线程安全的高效list,ArrayList对应的安全同步容器
     *             b.修改时add，set等加锁，复制出一个新的数组进行操作，完成后将数组指针指向新的数组
     *             c.读操作时size，get等不需要加锁，直接读取数组内容
     *             d.优点:读写分离，线程安全，高效
     *             e.缺点:
     *                  只能保证最终一致性，但不能保证实时一致性
     *                  每次修改时，内存会有2分数据，耗内存
     *             f.应用场景：读多写少的并发场景，如网站搜索，会有黑名单搜索不会出现，而黑名单列表每天晚上更新一次，
     *                  用户读黑名单的机会很多，但是更新
     *          2.CopyOnWriteArraySet
     *              a.里面用的是CopyOnWriteArrayList
     *              b.装饰者模式，add的时候调用CopyOnWriteArrayList的addIfAbsent方法，如果元素不存在，才加入容器
     *      f.skiplist跳表，redis的zset就是基于该实现的
     *      g.DelayQueue 延迟队列，继承Delay接口，返回设置的延时时间，放入另外的顺序队列，然后在取的时候取一个的时候，需要等待
     *
     *     二.Map
     *      1.TreeMap
     *          a.是通过红黑树实现的(http://www.cnblogs.com/skywang12345/p/3245399.html)，
     *            每个节点有left,right,parent,color,key,value
     *          b.实现NavigableMap接口，提供导航方法，返回比指定key大于小于的值
     *          c.实现Cloneable,Serializable支持复制，序列化
     *          d.key默认是自增的，可以通过构造函数传入自定义Comparator比较器
     *          e.时间复杂度log(n)
     *          e.非同步，fail-fast迭代器
     *      2.HashMap
     *          a.影响hashMap性能的2个参数，容量和加载因子，容量为桶的大小，加载因子（默认0.75）为当容量在多少时候自动扩容(大约2倍)，重建内部结构
     *          b.是个散列表，采用是拉链法（单链表）来解决hash冲突的，如果链表长度太大，会变成树
     *          c.非同步，fail-fast迭代器
     *          d.解决hash冲突的常用方法
     *          https://pre.iteye.com/blog/2435748
     *              1.开放地址法：对产生后的hash值，再次进行hash，直到不产生冲突为值
     *              2.再哈希法：对key继续用新的hash函数，直到不产生冲突为止
     *              3.拉链法：hashMap实现方式
     *              4.建立公共溢出区：将哈希表分为 基本表 和 溢出表 两部分。凡是和 基本表 发生冲突的记录都被存到 溢出表
     *          e.java1.8里面，如果链表长度大于8时，会变成红黑树
     *      3.Hashtable
     *          和HashMap实现差不多，只是是线程安全
     *      4.WeakHashMap
     *          a.实现和HashMap差不多
     *          b.其键是弱引用键WeakReference,通过WeakReference和ReferenceQueue实现的
     *          c.当某“弱键”不再被其它对象引用，并被GC回收时。在GC回收该“弱键”时，这个“弱键”也同时会被添加到ReferenceQueue(queue)队列中
     *          d.当下一次操作WeakHashMap时，table中保存了全部的键值对，queue中保存被GC回收的键值对；同步它们，就是删除table中被GC回收的键值对。
     *
     *      4.Hashtable和HashMap和ConcurrentHashMap区别
     *          参考：https://www.cnblogs.com/heyonggang/p/9112731.html
     *          a.t没有大写
     *          b.Hashtable是Dictionary的实现，而Dictionary已经被抛弃了，被map代替了
     *          c.Hashtable是线程安全的，实现方式在修改数据时，直接锁住整个Hashtable，效率低基本上被弃用了，而HashMap线程不安全
     *          d.Hashtable不支持key和value为空，而HashMap，key和value都可以为空，所以通过get来判断是否存在会有问题的
     *          e.ConcurrentHashMap采用分段锁
     *          f.散列表采用拉链法，数组+链表，如果链表的长度太大，则会变成树
     *      5.LinkedHashMap
     *          参考：https://blog.csdn.net/justloveyou_/article/details/71713781
     *          a.继承HashMap,是Linked和HashMap的结合,是和HashMap无序不一样，是有序的，用链表来实现
     *          b.在put的时候，除了和HashMap一样的会把Entry放到table数组里面，还会将Entry放到双链表里面
     *          c.Entry里面除了有HashMap的可以，value，next之外，还有before，after指针，用于维护双链表
     *          d.默认顺序是插入顺序，可以设置为操作顺序，可以用来实现LRU（最近最少使用）算法
     *      6.IdentityHashMap
     *          参考：https://blog.csdn.net/f641385712/article/details/81880711
     *          a.HashMap是通过key.hashCode来生成hash值找到对应的桶位置，再通过调用key.equals来和旧值判断是否一样，找到在链表中位置
     *              而Integer,String都是重写了hashCode和equals
     *          b.IdentityHashMap是通过System.IdentityHashCode来生成hash值，通过==指针来和旧值判断是否一样
     *          c.故key为String,Integer的HashMap，put多次是一样的，而IdentityHashMap则不一样，会生成新值
     *      6.ConcurrentHashMap
     *          a.读不需要加锁，因为Entry的的value是volatile能保证value是最新的
     *          b.锁分段技术
     *              1.java1.7采用多个segment组成，segment继承ReentrantLock，一个segment里面包含多个entry，相对于一次锁多个entry，而读是不需要加锁的，所以很快
     *                  ConcurrentHashMap的并发度就是segment的大小，默认为16，这意味着最多同时可以有16条线程操作ConcurrentHashMap
     *              2.java1.8已经舍弃了分段锁
     *                  a.基于加入多个分段锁浪费内存空间
     *                  b.生产环境中， map 在放入时竞争同一个锁的概率非常小，分段锁反而会造成更新等操作的长时间等待。
     *                  c.采用了synchronized和CAS来操作
     *              3.当链表长度大于8时，会变成红黑树，红黑树小于6时会退化为链表
     *          c.fail-safe迭代器
     *          d.put的过程
     *              如果没有初始化就先调用initTable（）方法来进行初始化过程
     *              如果没有hash冲突就直接CAS插入
     *              如果还在进行扩容操作就先进行扩容
     *              如果存在hash冲突，就加锁来保证线程安全，这里有两种情况，一种是链表形式就直接遍历到尾端插入，一种是红黑树就按照红黑树结构插入，
     *              最后一个如果该链表的数量大于阈值8，就要先转换成黑红树的结构，break再一次进入循环(阿里面试官问题，默认的链表大小，超过了这个值就会转换为红黑树);
     *              如果添加成功就调用addCount（）方法统计size，并且检查是否需要扩容
     *
     *     三.Set
     *      1.HashSet
     *          没有重复元素，完全是基于HashMap来实现的，里面有个HashMap对象的引用，直接操作HashMap,只关注HashMap的key
     *      2.TreeSet
     *          没有重复原因，完全是基于TreeMap来实现的，里面有个TreeMap对象的引用，直接操作TreeMap,只关注TreeMap的key
     *      3.LinkedHashSet
     *          和LinkedHashMap一样的
     *
     *     四.并发包/网络编程/多线程编程
     *      1.后续再看？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？
     *
     *  21.序列化
     *      参考：https://www.cnblogs.com/sharkli/p/5607895.html
     *          https://blog.csdn.net/Leon_cx/article/details/81517603
     *      a.必须实现Serializable接口或Externalizable接口的类才能进行序列化
     *      b.transient和static修饰符修饰的成员变量不会参与序列化和反序列化
     *      c.反序列化对象和序列化前的对象的全类名和serialVersionUID必须一致,反序列的时候是通过序列化版本号serialVersionUID判断类是否一致
     *        如果被修改了则会异常，如果类不指定会通过属性方法等默认生成，最好不用默认，指定生成一个，这样不会报错
     *      d.在目标类中添加私有的writeObject和readObject方法可以覆盖默认的序列化和反序列化方法,
     *        objectOutputStream的writeObject和ObjectInputStream的readObject里面会通过反射的方式调用类的private的writeObject和readObject方法
     *      e.在目标类中添加私有的readResolve可以最终修改反序列化回来的对象，可用于单例模式防止序列化导致生成第二个对象的问题
     *
     *  22.主键，外建，主表，从表
     *      参考：https://blog.csdn.net/WICHER_WU/article/details/80263486
     *          https://www.cnblogs.com/Brambling/p/9263377.html
     *          https://blog.csdn.net/relaxyu/article/details/81735450
     *      a.B表里面的一个键是跟随A表的键值修改而修改的，B表里面的那个键叫外键，A表叫主表，B表叫从表，
     *      b.作用是保持数据的一致性,主表更新和删除，子表也会跟着变动
     *      c.主键是唯一能确定不一样的
     *      d.具体用法参考上面地址
     *      e.主表更新，子表也会跟着更新，如果主表删除，子表对应外键哪一行也会跟着删除，即级联删除、更新,从表插入在主表不存在的外键会报错
     *
     *   23.clone
     *      a.浅拷贝，深拷贝
     *      b.继承Cloneable接口，重写clone方法，并设为public,而object的clone方法是native
     *      c.浅拷贝有危险，需要深拷贝,默认super.clone是浅拷贝
     *      d.深拷贝一般有2中方法。1.先把对象序列化，再反序列化，2.调用super.clone方法产生新对象，再手动给拷贝，一般用第一种
     *
     *   24.Native
     *      是调用其他语言接口实现的，JNI（java native interface）
     *
     *   25.RandomAccess
     *      a.空接口，表示list支持快速随机访问
     *      b.RandomAccess接口这个空架子的存在，是为了能够更好地判断集合是否ArrayList或者LinkedList，从而能够更好选择更优的遍历方式，提高性能
     *
     *   26.Reference（强引用，软引用，弱引用，虚引用,引用队列）
     *      参考：https://www.cnblogs.com/huajiezh/p/5835618.html
     *          https://www.cnblogs.com/dreamroute/p/5029899.html
     *          https://blog.csdn.net/woblog/article/details/51332342
     *          http://bylijinnan.iteye.com/blog/2085082
     *          https://blog.csdn.net/qq_33663983/article/details/78349641
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
     *   27.String直接赋值和new区别
     *      参考：https://blog.csdn.net/zqzq310918/article/details/54313262
     *      a.直接赋值是如果不存在常量池（constant pool），就在常量池创建，以后同样的字符串赋值，则直接指向常量池
     *      b.new是每次都在堆中开辟空间
     *      c.常量池:存放字符串常量和基本类型常量（public static final）
     *
     *   28.java8默认方法
     *      Java8中引入的默认方法，充分展示了Java平台中概念的一致性与JDK向前兼容之间的矛盾，并且悲哀地。以牺牲概念的一致性而满足JDK向前兼容
     *      主要是为了向前兼容，但是又不能修改已经发版的版本，所以就搞出了默认方法，为了解决接口的修改与现有的实现不兼容的问题
     *
     *   29.参数传递
     *      java里面都是值传递，复制对象的地址，而基本类型数据的话，是改变不了里面的数据的
     *
     *   30.为何byte取值范围是[-128~127],而不是[-127~127]
     *      参考：https://blog.csdn.net/qq_23418393/article/details/57421688
     *      1.计算机里面是用补码进行数字运算
     *      2.原码第一位表示符号, 其余位表示值，人的正常思维 8（0000 1000），-8（1000 1000）
     *      3.反码，正数是本身，负数是符号位不变其余位取反 8（0000 1000）， -8（1111 0111）
     *      4.补码，正数是本身，负数反码+1 8（0000 1000） -8（1111 1000）
     *      5.用原码来进行加减不能得到正确结果，用反码来进行加减可以得到正确结果，范围是[-127~127],但是会出现+0（0000 0000），-0（1000 0000）都表示0
     *      6.而用补码来进行加减，可以得到正确结果，而且补码计算出来的-128（1000 0000）就是用反码表示的-0，计算出来的+0（0000 0000）可以表示0
     *        所以用补码来进行加减运算既能够解决+0和-0问题，而且能够多表示一位数字-128，所以说为什么byte的取值范围是[-128~127]
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
     *  34.为null对mysql索引的影响，空值和null区别
     *      a.空值和null区别:空值不占用空间，用 = <> != 来判断，而null是占用空间的，用is null或is not null判断
     *      b.null在计算数目或者sum等是不计算在内的
     *      c.null对mysql索引影响
     *          1.null需要占用额外空间
     *          2.null不走索引，且会使索引统计更加复杂一些
     *      d.最好把字段设置为不为null，设置默认值，字符串设为空值'',数字设为0
     *
     *  35.mysql事务(innodb),mysql的引擎中只有innodb支持事务
     *      参考：https://blog.csdn.net/w_linux/article/details/79666086、
     *      https://www.cnblogs.com/cjsblog/p/8365921.html
     *      a.4大特征(ACID)：
     *          原子性（A）:事务是最小单位，不可再分
     *          一致性（C）:所有的DML要么一起成功，要么一起失败
     *          隔离性（I）:事务之间具有隔离性
     *          持久性（D）:事务commit后能持久话到磁盘
     *      b.在事务进行过程中，未结束之前，DML语句是不会更改底层数据，只是将历史操作记录一下，在内存中完成记录。
     *          只有在事务结束的时候，而且是成功的结束的时候，才会修改底层硬盘文件中的数据
     *      c.隔离性（用于在并发事务执行的时候，如果串行的话，不用隔离）
     *          4个隔离级别
     *              1.读未提交（read uncommitted）
     *                  事务A未提交的数据，事务B可以读取到，此种方法会有脏数据
     *                  隔离级别最低，这种级别一般是在理论上存在，数据库隔离级别一般都高于该级别
     *              2.读已提交（read committed）
     *                  事务A提交了数据，事务才B可以读取到，此种方法没有脏数据，但会出现重复读取的时候，可能结果已经变了
     *              3.可重复读（repeatable read）
     *                  事务A和事务B，事务A提交之后的数据，事务B读取不到，不管A事务是否committed了，重复读取都是第一次，
     *                  对方提交之后的数据，我还是读取不到，不可重复读重点在于update和delete
     *                  但是会出现幻像读，就是读取到结果，出现新增，如范围查询，结果变多了
     *                  MySQL默认级别
     *              4.串行化（serializable）
     *                  事务A和事务B，事务A在操作数据库时，事务B只能排队等待
     *                  这种隔离级别很少使用，吞吐量太低，用户体验差
     *                  可以避免“幻像读”，每一次读取的都是数据库中真实存在数据，事务A与事务B串行，而不并发
     *                  幻读的重点在于insert
     *       d.mvcc(多版本并发控制)
     *       https://www.cnblogs.com/chinesern/p/7592537.html
     *       https://blog.csdn.net/w2064004678/article/details/83012387
     *          1.解决不可重复读
     *          2.每一行多了创建事务版本号和删除事务版本号
     *          3.过程
     *             insert-当前的A事务-create_version=1，delete_version=null
     *             update-新插入一行B事务-create_version=2,delete_version=null 同时A事务-delete_version=2
     *             delete 最新的一行C事务-create_version=2,delete_version=3
     *             select 如何查找出A事务的数据
     *                 a.创建版本小于等于当前版本 create_version <= 1，确保读取的行的是在当前事务版本之前的
     *                 b.删除版本大于等于当前版本 delete_version >=1,确保事务之前行没有被删除
     *
     *       e.快照读和当前读
     *          快照读：读取的是快照版本，也就是历史版本
     *          当前读：读取的是最新版本
     *          普通的SELECT就是快照,UPDATE、DELETE、INSERT、SELECT ...  LOCK IN SHARE MODE、SELECT ... FOR UPDATE是当前读
     *          Consistent read（一致性读）是READ COMMITTED和REPEATABLE READ隔离级别下普通SELECT语句默认的模式。
     *          一致性读不会给它所访问的表加任何形式的锁，因为都是读取快照版本，因此其它事务可以同时并发的修改它们
     *       f.锁
     *          Record Locks（记录锁）：在索引记录上加锁
     *          Gap Locks（间隙锁）：在索引记录之间加锁
     *          Next-Key Locks：在索引记录上加锁，并且在索引记录之前的间隙加锁
     *       g.总结
     *         1.利用MVCC实现一致性非锁定读，保证同一个事务中多次读取相同的数据返回的结果是一样的，解决了不可重复读的问题
     *         2.利用Gap Locks和Next-Key可以阻止其它事务在锁定区间内插入数据，因此解决了幻读问题
     *
     *  36.nginx
     *      参考：https://www.cnblogs.com/wcwnina/p/8728391.html
     *      1.正向代理:它代理的是客户端，代客户端发出请求
     *      2.反向代理:它代理的是服务端，代服务端接收请求
     *          a.保证内网的安全，通常将反向代理作为公网访问地址，Web服务器是内网
     *          b.负载均衡，通过反向代理服务器来优化网站的负载
     *
     *  37.rpc(远程过程调用)
     *      一.rpc和restful api区别
     *      参考：https://blog.csdn.net/wangyunpeng0319/article/details/78651998
     *          a.RPC 就像本地方法调用，RESTful API 每一次添加接口都可能需要额外地组织开放接口的数据
     *          b.RESTful API 在应用层使用 HTTP 协议,RPC 传输既可以使用 TCP/UDP，协议一般使用二制度编码，大大降低了数据的大小，减少流量消耗
     *          c.对接异构第三方服务时，通常使用 HTTP/RESTful 等公有协议，对于内部的服务调用，应用选择性能更高的二进制私有协议
     *      二.thrift
     *      参考：https://blog.csdn.net/zkp_java/article/details/81879577
     *          a.thrift是一个典型 的CS结构,支持跨语言,thrift通过IDL(Interface Description Language)来关联客户端和服务端
     *          b.thrift使用socket进行数据传输
     *
     *      三.Finagle
     *      参考：https://www.cnblogs.com/junneyang/p/5383627.html
     *      https://www.infoq.cn/article/2014/05/twitter-finagle-intro
     *
     *      四.dubbo
     *      参考：https://blog.csdn.net/u010664947/article/details/80007767
     *
     *      五.spring cloud
     *      参考：https://blog.csdn.net/valada/article/details/80892573
     *      https://www.cnblogs.com/ityouknow/p/7508306.html
     *
     *  38.jooq
     *      参考：https://www.breakyizhan.com/springboot/3369.html
     *
     *  39.秒杀
     *      参考：https://www.cnblogs.com/wangzhongqiu/p/6557596.html
     *      a.秒杀系统特点是并发量极大，但实际秒杀成功的请求数量却很少
     *      b.设计思路
     *          将请求拦截在系统上游，降低下游压力
     *          后端接口，必须能够支持高并发请求，必须尽可能“快”，在最短的时间里返回用户的请求结果
     *
     *  40.B-tree，B-plus-tree
     *      参考：https://www.cnblogs.com/vincently/p/4526560.html
     *      https://blog.csdn.net/bigtree_3721/article/details/73626663
     *      a.B-tree:升级版的二叉查找树，在二叉查找树的基础上，每个节点可以包含2个以上的key，且里面的key也是顺序的
     *      b.B-plus-tree:在B-tree的基础上
     *          1.非叶子节点只包含导航信息（子节点指针），不包含具体值，具体值保存在所有叶子节点，只有达到叶子结点才命中
     *          2.所有叶子节点是一颗从小到大的顺序链表，便于区间查找和遍历
     *          3.更适合文件索引系统，容纳的查询信息更多，因为磁盘的磁道先统一寻址，再找到具体的值
     *      c.B+树的优点
     *          1.非叶子节点不会带上ROWID，这样，一个块中可以容纳更多的索引项，一是可以降低树的高度。二是一个内部节点可以定位更多的叶子节点
     *          2.叶子节点之间通过指针来连接，范围扫描将十分简单，而对于B树来说，则需要在叶子节点和内部节点不停的往返移动
     *
     *  41.innodb索引
     *      索引：https://www.cnblogs.com/fuyunbiyi/p/2429297.html
     *      mysql中普遍使用B+Tree做索引
     *      聚簇索引：索引的叶节点就是数据节点。确定表中数据的物理顺序，一个表只能包含一个聚集索引
     *      非聚簇索引：叶节点仍然是索引节点，只不过有一个指针指向对应的数据块
     *
     *  42.foreach 循环原理
     *      1.编译中的语义分析过程中，有一个解除语法糖的操作，（语法糖是啥？可以理解成编译器为方便开发人员开发，会对特定代码做一些特殊处理，
     *          方便开发人员使用，除了foreach，java中还有泛型、装箱、拆箱、变长字符串等）
     *      2.对于list，编译器会调用Iterable接口的 iterator方法来循环遍历数组的元素
     *      3.对于数组，就是转化为对数组中的每一个元素的循环引用
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
     *  44.mybatis的#{}和${}区别
     *   参考：http://www.mybatis.cn/archives/70.html
     *      #{}:只是占位符，在sql语句预编译阶段会用？替换，执行的时候才传入值，可防止sql注的过入
     *      ${}:是直接替换，然后sql语句预编译，这样有可能会sql注入，因为SQL注入是发生在预编译程中
     *      使用#{}可以有效的防止SQL注入，提高系统安全性。原因在于：预编译机制。预编译完成之后，SQL的结构已经固定，
     *          即便用户输入非法参数，也不会对SQL的结构产生影响，从而避免了潜在的安全风险\
     *
     *  45.分布式session实现的几种方式
     *  https://blog.csdn.net/woaigaolaoshi/article/details/50902010
     *      1.粘性session，用户每次请求都只落到某个服务节点上，可通过nginx配置
     *      2.session复制，当session发生变化时广播到其他节点上面，可通过tomcat配置
     *      3.缓存方案，redis，memcached
     *      4.数据库方案
     *
     *  46.forward和redirect的区别
     *      1.forward转发：服务器行为，转发的路径必须是同一个web容器下的url，其不能转向到其他的web路径上去，中间传递的是自己的容器内的request
     *          相对于转发到同一个web容器下的servlet程序处理，request是共享的
     *      2.redirect重定向：客户端行为，服务器返回重定向url，客户端从新请求
     *
     *  47.一致性hash算法（分布式缓存负载均衡算法）
     *      https://www.cnblogs.com/moonandstar08/p/5405991.html
     *      1.负载均衡算法
     *      2.常用的算法：取模算法，HashCode对服务节点取模，这种方式简单高效，
     *          但有个致命问题：当服务节点增加和删除，取模需重新计算，会造成命中率大幅度下降
     *      3.一致性哈希算法：
     *          a.一致性哈希环（0 - (2^32)-1），将服务器节点（可通过ip的hash等）放到环上面
     *          b.数据key的hash也能确定在环上面的位置
     *          c.key的hash值位置顺时针找到第一个服务器节点，即为命中节点
     *          d.增加和删除节点，只会影响节点逆时针到下一个节点之间的数据，影响没有取模大
     *          e.服务节点太少，节点分布不均匀会导致数据倾斜，可为每个节点添加很多虚拟节点来解决
     *
     *  48.数据库3大范式规则
     *      1.第一范式：每列是不可在分割单元，且不能有重复
     *      2.第二范式：每列要有主键，相当于有主属性
     *      3.第三范式：每列的非主属性必须直接依赖于主属性，而不是间接依赖
     *
     *  49.MySQL存储引擎-InnoDB&MyISAM区别
     *  https://www.cnblogs.com/liqiangchn/p/9066686.html
     *      两者最大的区别就是InnoDB支持事务，和行锁，而MyISAM是不支持的
     *      1.InnoDB的数据存储基于聚簇索引（数据和索引是存在一起，主键索引就是，不过他的二级索引（非主键索引）必须包含主键列）的，
     *          而MyISAM是基于非聚簇索引进行存储的，索引和数据是分开的
     *      2.InnoDB支持事务，MyISAM不支持
     *      3.InnoDB支持行锁，MyISAM不支持
     *      4.MyisAM支持全文索引（FULLTEXT）、压缩索引，InnoDB不支持
     *      5.InnoDB关注事务和并发，MyISAM关注查询性能
     *
     *  50.Mysql中的各种锁以及死锁
     *  https://www.cnblogs.com/LBSer/p/5183300.html
     *      a.锁：
     *          1.表级锁（MyISAM）：读共享，写互斥
     *          2.页级锁（BDB）：锁定相邻的一组记录
     *          3.行级锁（InnoDB）：共享锁和排他锁，通过索引上的索引项来实现，意味者：只有通过索引条件操作数据，InnoDB才会使用行级锁，否则，InnoDB将使用表锁
     *          4.间隙锁（InnoDB）：防止幻读，锁定一定范围内的数据
     *          5.乐观锁：如mvcc不加锁，mvcc来解决不可重复读
     *          6.意向锁:用来解决行锁和表锁互斥的问题：在意向锁存在的情况下，事务A必须先申请表的意向锁，成功后再申请一行的行锁
     *              如事务A行读锁，事务B表锁，是互斥的，但是如果查找表里面哪一行是行锁，效率很低，于是有了意向锁
     *          7.mysql实现悲观锁和乐观锁
     *          https://www.cnblogs.com/zhiqian-ali/p/6200874.html
     *              a.悲观锁：select for update实现，里面用行锁实现的
     *              b.乐观锁：通过版本号或时间戳来实现，先select出版本号保存在内存，然后update版本号，将其+1，条件是数据库里面的版本号和
     *                  内存里面保存的是一样的，如果一样则获取到锁，否者失败，时间戳实现一样的道理
     *                  其实和cas差不多
     *
     *
     *      b.死锁
     *          1.场景:
     *              a.不同表相同记录行锁冲突:2个事务执行操作2张表，动作一模一样，但是顺序不一样，就和多线程出现死锁一样的场景
     *              b.相同表记录行锁冲突:2个事务执行操作2条数据，动作一模一样，但是顺序不一样，就和多线程出现死锁一样的场景
     *          2.如果避免：
     *              a.以固定的顺序访问表和行
     *              b.为表添加合理的索引。因为操作索引是会使用行锁
     *              c.大事务拆小。大事务更倾向于死锁，如果业务允许，将大事务拆小
     *          3.如何定位死锁成因
     *              a.通过应用业务日志定位到问题代码，找到相应的事务对应的sql
     *              b.确定数据库隔离级别,可以确定数据库的隔离级别，我们数据库的隔离级别是RC，这样可以很大概率排除gap锁造成死锁的嫌疑
     *              c.找DBA执行下show InnoDB STATUS看看最近死锁的日志
     *              d.mysql有机制去检查死锁
     *
     *  51.负载均衡算法
     *  https://www.cnblogs.com/saixing/p/6730201.html
     *      1.应用服务器:只需要转发请求即可
     *          a.Random 随机:
     *              缺点:随机数的特点是在数据量大到一定量时才能保证均衡，所以如果请求量有限的话，可能会达不到均衡负载的要求
     *          b.轮询和加权轮询:
     *          c.最少连接:记录每个应用服务器正在处理的连接数，然后将新来的请求转发到最少的那台上
     *          d.hash地址:根据ip地址hash之后所有请求都是同一个服务器
     *      2.分布式缓存集群:如redis,memcached
     *          a.取模,HashCode对服务节点取模
     *          b.hash算法,redis是ip对的hash值对16384个哈希槽取模
     *          c.一致性hash算法,memcached client使用
     *
     *   52.nio,aio,bio的区别
     *     https://www.cnblogs.com/barrywxx/p/8430790.html
     *     https://blog.csdn.net/lisha006/article/details/82856906
     *      1.BIO:同步阻塞模式
     *      2.NIO：同步非阻塞模式
     *          a.有selector，channel（通道：可写可读），buffer（载体）
     *          b.采用IO多路复用模型，epoll模型代替select模型
     *              select模型：采用轮寻方式，轮寻到有准备好的io后，返回进行后续操作
     *              epoll模型：采用注册通知方式，当有准备好的io后，会回调通知相应的处理
     *      3.AIO:异步非阻塞模式
     *          不需要轮询等待，而是通过操作系统处理后，会自动通知服务处理相应的请求，相对于轮训操作交给操作系统
     *
     *  53.MySQL数据库优化的几种方式
     *  https://blog.51cto.com/yangshufan/2168952?source=drh
     *  my sql如何优化查询
     *  后续再看下
     *
     *  54.覆盖索引：
     *  https://www.jianshu.com/p/77eaad62f974
     *      a.查询语句覆盖了索引时（查询结果和条件里面都只是索引），只通过索引而不用通过获取行数据就可以获取到结果
     *      b.select选择的字段中含有不在索引中的字段 ，即索引没有覆盖全部的列,不会选择覆盖索引查询
     *      c..where条件中不能含有对索引进行like的操作。不会选择覆盖索引查询
     *
     *  55为什么选用自增量作为主键索引:
     *  http://www.cainiaoxueyuan.com/sjk/4571.html
     *      a.innodb的主键索引是聚集索引，是一颗B-plus-tree，在新增数据的时候，插入的时候是自增的，数据可直接插入到树的最后即可，性能高
     *
     *  56.数据库范式和反范式区别和优缺点
     *      范式
     *          优点：
     *              a.范式化的数据库更新起来更加快
     *              b.很少的冗余数据
     *              c.范式化的表更小
     *          缺点：
     *              查询的时候经常需要很多的关联
     *      反范式
     *          优点：可以避免关联，因为所有的数据几乎都可以在一张表上显示
     *          缺点：表格内的冗余较多，删除数据时候会造成表有些有用的信息丢失
     *
     *  57.mysql的分区和分表、
     *  https://www.cnblogs.com/myvic/p/7711498.html
     *      1.一般大于1000万行就可以分表了
     *      2.分区：一个表的物理文件分为，索引文件，数据文件，表结构文件，分区就是把这些文件分成不同的小块
     *      3.分表：把表内容分成不同的表
     *
     *  58.分布式事务
     *  https://www.cnblogs.com/savorboard/p/distributed-system-transaction-consistency.html
     *  https://blog.csdn.net/hanruikai/article/details/82659223
     *      a.目的：因为出现数据库分区分表，服务器soa话，但是本质上分布式事务是为了保证不同数据库的数据一致性
     *      b.分布式理论
     *          1.cap理论:web服务器无法同时满足3个属性
     *              数据的一致性
     *              系统的可用性
     *              分区容错性:单个组件无法可用,操作依然可以完成
     *          2.base理论
     *              理论的核心思想就是：我们无法做到强一致，但每个应用都可以根据自身的业务特点，采用适当的方式来使系统达到最终一致性
     *      c.jta:java-transaction-api是对2阶段提交的实现,有spring实现了jta
     *      d.实现方式:
     *          a.强一致性
     *          b.最终一致性
     *      e.方案：
     *          1.2pc:2阶段提交采用XA协议
     *              流程：
     *                  a.事务管理器对每个事务参与的数据库,询问是否可以提交
     *                  b.事务管理器对每个事务参与的数据库,发出提交命令
     *                  c.如果其中某一个参与者在是否可以提交和提交失败都会失败回滚
     *                  d.属于强一致性
     *              缺点：
     *                  一部分事务参与者收到了提交消息，另一部分事务参与者没收到提交消息，那么就会导致节点间数据的不一致问题
     *                  事务参与值执行了commit后挂掉了，而管理者也挂了，新的管理者不知道挂掉的参与者的信息
     *          2.3pc:对2pc的优化
     *              流程：
     *                  a.相对于2pc来说，多了perCommit和超时机制（参与者和管理者都有）
     *          3.tcc(Try-Confirm-Cancel):补偿事务 和2pc流程差不多
     *              a.针对每个操作都要注册一个与其对应的确认和补偿（撤销操作）
     *          4.mq方式：基于本地事务+mq
     *              其实也是基于2阶段提交，预提交，提交过程，只是交给mq处理了
     *              A事务先二阶段执行，mq会有个回调，如果失败需要回滚就执行回调
     *              A事务执行成功，会修改mq里面的一个状态，然后给B事务发送成功消息，B事务开始执行
     *              属于最终一致性
     *          5.本地消息表
     *          https://www.cnblogs.com/savorboard/p/distributed-system-transaction-consistency.html
     *          有点模糊，后续再看下
     *
     *  59.单点登录（single-single-on SSO）
     *  https://www.cnblogs.com/morethink/p/8047711.html
     *  https://www.cnblogs.com/zhuchenglin/p/8968530.html
     *      概念：用户用户名和密码登录了一个a系统后，登录b系统不需要重新输入了
     *      实现方式：
     *          1.cookie方式
     *              a.用户登录a系统，跳转至sso认证中心拿到返回的cookie，去登录b系统
     *              b.缺点：
     *                  不支持跨域请求（一个域名下的cookie不能拿去请求另一个域名）
     *                  不安全，如果cookie可能会被破解
     *          2.服务节点内部认证的方式
     *              a.用户登录a系统，跳转至sso认证中心登录成功
     *              b.sso认证中心创建全局会话，保存的令牌信息,用户信息，用户名，密码等等，将局部令牌发送给a系统
     *              c.认证中心带着令牌跳转到用户最初请求的地址，a系统拿到令牌信息，到sso认证中心验证是否登录了
     *              d.如果登录了，则保存局部会话
     *              e.用户登录b系统，跳转至sso认证中心发现用户已经登录，则执行c步骤
     *              f.局部会话建立后不会在通过sso认证中心，
     *
     *  60.流量控制与拥塞控制
     *  https://blog.csdn.net/ailunlee/article/details/53716367
     *      流量控制：防止a发送太快，导致b接收不过来，通过滑动窗口实现，b每次会告诉a最多能发送多少
     *      拥塞控制：
     *          a.是A与B之间的网络发生堵塞导致传输过慢或者丢包，来不及传输。防止过多的数据注入到网络中
     *              是一个全局性的过程，涉及到所有的主机、路由器，以及与降低网络性能有关的所有因素
     *          b.实现方式
     *              慢开始和拥塞避免
     *              慢慢的增加发送数据的大小
     *
     *  61.常用的几种分布式id的设计方案
     *  参考：https://www.jianshu.com/p/b2337d954ff0
     *      a.UUID（里面有通过网卡）
     *          优点：简单，高效
     *          缺点：不支持递增，数据比较长
     *      b.数据库自增id
     *          优点：写入效率高
     *          缺点：分布式架构，多个Mysql实例可能会导致ID重复,容易被识破
     *      c.redis
     *          优点：不依赖于数据库，灵活方便，且性能优于数据库
     *          缺点：AOF和RDB依然会存在数据丢失，造成ID重复。
     *      d.zookeeper
     *      e.雪花算法-SnowFlake:机制和UUID差不多，是生成一个long的整数
     *          1.第一位：最高位是符号位0
     *          2.第二部分：41bit时间戳，精确到毫秒级，41位的长度可以使用69年-（一般是当前时间-开始时间）
     *          3.第三部分：10位的机器标识（5位数据标识位，5位机器标识位）
     *          4.第四部分：12位的计数序列号-自增id，最多支持同一毫秒内4096个
     *          优点：高效，有时间，且递增
     *          缺点：依赖与时钟，不能回拨
     *
     *  62.常见限流策略
     *  https://blog.csdn.net/fouy_yun/article/details/81025984
     *      a.令牌桶：以一定速率往令牌桶里面放入令牌，来一个请求拿到令牌，然后处理，拿不到令牌则丢弃
     *      b.漏桶：桶里面以一定速率滴水，入桶的水可以速度很快也很小，反正多了就溢出不管
     *      c.计数器：计算请求的数量，然后限制
     *
     *  63.mysql集群主从复制，主从同步
     *      a.集群中每个节点都是全部内容
     *      b.master节点上面记录的binlog-记录了所有操作，从数据库启动一个io线程复制binlog到从
            c.通过binlog传到中继日志里面
            d.半同步，异步，同步
     *
     *  64.spring-boot如何实现自动配置的
     *  https://www.cnblogs.com/leihuazhe/p/7743479.html
     *      a.通过SpringBootApplication注解下面的EnableAutoConfiguration注解
     *      b.该注解会import一个AutoConfigurationImportSelector，里面会加载META-INF/spring.factories里面配置好的各种类，放到ioc容器里面
     *      c.如redis，es，kafka等等，而这些配置类默认会读取application.yml文件里面的配置
     *
     *  65.https工作流程,ssl协议，对称加密，非对称加密
     *  https://www.cnblogs.com/hai-blog/p/8311671.html
     *      a.对称加密：A（客户端）和B（服务端）都用的是同一个私钥来加密和解密
     *      b.非对称加密：
     *          1.A请求
     *          2.B返回证书和公钥
     *          3.A随机产生一个对称密钥，用公钥对这个对称密钥加密发给B
     *          4.B收到后用私钥解密拿到对称密钥
     *          5.至此A和B都拿到了同一个对称密钥了，相互就可以加密和解密了
     *
     *  66.tcp粘包/拆包，及解决办法
     *  https://www.cnblogs.com/panchanggui/p/9518735.html
     *
     *  67.什么时候索引失效？
     *  https://www.e-learn.cn/content/mysql/1045218
     *      a.like查询%在前面
     *      b.or操作但没有把所有字段加上索引,!=, is null, not in等操作
     *      c.类型是字符串,但是没有用引号包含起来
     *      d.mysql估计全部扫描的时间,比使用索引的时间快时(数据量很大,查询出20%-30%数据,因索引会查找2次才能查到数据)
     *
     *  68.如何防止sql注入？
     *  https://www.cnblogs.com/jiangxueqiao/p/7444127.html
     *      a.转义字符特殊处理，在mysql里面具有特殊含义的字符转义一下
     *      b.Prepared Statement预处理语句(如mybatis#{} 预编译阶段用?代替,然后正在执行的时候在替换)
     *
     *  69.高可用高并发一般方案是什么？7种
     *  https://blog.csdn.net/qq_37651267/article/details/93368908
     *      a.在开发高并发系统时有三把利器用来保护系统：缓存、降级和限流
     *      b.缓存：如redis等提高容量
     *        降级：当用户量大了，选择暂时屏蔽掉某些功能
     *        限流：限制访问的请求数，一般的方案如：令牌桶，漏桶，计数器等
     *        集群：
     *        负载均衡：
     *        mq流量消峰：
     *        数据库分库分表：
     *
     *  71.如何保证mysql和redis，数据一致性，解决数据库与缓存双写的时候数据不一致的情况
     *  https://www.cnblogs.com/lingqin/p/10279393.html
     *      a.延时双删策略
     *          1.如果先更新数据库，再删缓存，会出更新成功，删除缓存失败，造成数据不一致
     *          2.先删redis，再更新mysql，会出现再删除后，另外请求过来，然后拿到了老数据
     *          3.解决上面的问题，先删除缓存，再更新mysql，sleep一段时间（等待另外一个请求读取到老数据，然后更新到缓存里面，返回给前端后），再删除缓存
     *      b.订阅mysql binlog增量消息（只要更新数据就会更新binlog） + mq如kafka + redis
     *          1.订阅mysql binlog增量消息 ，通过kafka发送给redis，然后更新
     *
     *  72.数据库水平切分和垂直切分
     *  https://uule.iteye.com/blog/2122627
     *  https://blog.csdn.net/5hongbing/article/details/78024897
     *      a.垂直切分：垂直一刀，根据不同的业务拆分到不同的数据库，或者比较大的数据单独放一个表
     *          优点：拆分简单，业务明确
     *          缺点.事务不好处理，过度切分导致系统复杂，存在性能问题
     *      b.水平拆分：水平一刀，分表操作，
     *          优点：事务处理比较简单，不会存在性能问题
     *          缺点：分表逻辑不好控制，数据迁移比较麻烦（可采用一致性hash算法），跨节点join，排序等等比较麻烦
     *          一般拆分到数据1000万
     *      c.数据切分应引发的问题
     *          1.分布式事务（垂直切分）
     *          2.跨节点Join的问题，排序等等问题
     *
     *  73.sql优化
     *      a.建立合适的字段
     *      b.建立合适的索引
     *      c.分库分表
     *      d.查询优化
     *      d.运维层面了
     *
     *  74.默认排序算法
     *  https://blog.csdn.net/sinat_35678407/article/details/82974174
     *  timsort算法：基于大部分数据已经是排好序的，采用归并+二分插入算法
     *  如何归并：里面已经排好的数据块不动，没有拍好的进行排序（二分插入），数据块不满规定长度，则用二分插入添加到最小的长度
     *      最后所有数据块进行归并排序
     *
     *  75.innodb下的count(*)和count（1）区别
     *  https://msd.misuland.com/pd/2884250068896976750
     *      1.高版本其实没有任务区别
     *      2.如果表里面索引只有主键，则走主键来统计条数
     *      3.如果表里面二级索引则会走二级索引来统计条数（占用磁盘空间比较小）
     *
     *  76.rpc框架
     *  http://youzhixueyuan.com/implementation-principle-of-rpc-framework.html
     *  我说了主要是协议栈+数据格式+序列化方式，然后需要有服务注册中心管理生产者和消费者。
     *
     *  77.spring如何解决循环依赖的？
     *  https://blog.csdn.net/lzhcoder/article/details/84144381
     *      a.场景：
     *          属性循环依赖：可以解决
     *          构造器依赖：不能解决
     *      b.如何检测：
     *          Bean在创建的时候可以给该Bean打标，如果递归调用回来发现正在创建中的话，即说明了循环依赖了。
     *      c.属性循环依赖如何解决
     *          1.单例对象创建过程：实例化->填充属性->初始化
     *          2.在创建A的时候会把A对应的ObjectFactory放到缓存中(早期引用，只实例化了，但没有填充属性)，在创建A的过程，发现依赖B，然后创建B，发现B
     *              里面依赖A，这个时候如果发现A正在创建中，会从缓存里面拿到创建好的A（没有填充属性）
     *          3.实例化完成后，会填充属性，初始化
     *
     *  78.Java栈什么时候会发生内存溢出
     *      a.递归调用方法
     *      b.死循环调用，里面有局部变量
     *
     *  79.如何判断链表有环
     *  https://blog.csdn.net/N1314N/article/details/90736225
     *      a.用HashSet
     *      b.用快慢指针，慢指针走一步，快指针走两步，如果有环则2个肯定会相遇（因为快指针会在环里面一直循环，直到相遇），否则快指针会为null
     *
     *  80.HTTP2.0和HTTP1.x的区别
     *  https://www.jianshu.com/p/7fc8ca235c9f
     *
     *  81.架构设计4+1视图模型
     *  https://blog.csdn.net/zxs9999/article/details/38703471
     *  https://www.ibm.com/developerworks/cn/rational/06/r-wenyu/index.html
     *      a.1视图是，场景视图（用例视图）,测试角度，关注的是从用户角度，可以通过测试用例表示
     *      b.逻辑视图，关注的是系统的功能逻辑，产品角度，可以通过uml类图表示
     *      c.开发视图，关注的是软件的静态组织，开发人员角度，包括软件可用性，稳定性等等，可以uml组件图等
     *      d.过程视图（处理视图），关注的是软件动态运行时，同步，并发，分布式等，，可以uml活动图等
     *      e.物理视图，关注的是如何部署机器和网络来配合软件达到可用性，运维人员角度，
     *
     *  82.为什么重写equals必须重写hashcode
     *  https://www.cnblogs.com/wang-meng/p/7501378.html
     *      a.通用约定，概论上来说，既然对象都相等了，hashcode肯定也要相等才符合逻辑
     *      b.hashMap里面就是基于该思想来的，如果重新了equals，但没有重写hashcode会出现问题
     *      c.效率问题，先用hashcode（做数字运算）做出一部分筛选，然后在比较equals
     *
     *  83.为什么组合索引是最左原则
     *  https://blog.csdn.net/weixin_41462047/article/details/80261546
     *      a.组合索引在b+树里面存储的结构和普通的索引是一样的
     *      b.其非叶子节点存储的是第一个关键字的索引，只是叶子节点保存的是联合索引的所有索引，然后按照声明的顺序保存的
     *
     *  84.springboot上下文如何加载的
     *   https://blog.csdn.net/qq_34173549/article/details/79929071
     *
     *  85.springcloud了解下
     *
     *  86.http缓存
     *  https://www.jianshu.com/p/227cee9c8d15
     *  https://www.cnblogs.com/ranyonsue/p/8918908.html
     *  浏览器缓存分为强缓存和协商缓存
     *   a.浏览器先根据这个资源的http头信息来判断是否命中强缓存。如果命中则直接加在缓存中的资源，并不会将请求发送到服务器。
     *   b.如果未命中强缓存，则浏览器会将资源加载请求发送到服务器。服务器来判断浏览器本地缓存是否失效。若可以使用(过期时间已经到了，但是服务资源还未更新)
     *   则服务器并不会返回资源信息，浏览器继续从缓存加载资源。
     *   c.如果未命中协商缓存，则服务器会将完整的资源返回给浏览器，浏览器加载新资源，并更新缓存。
     *   d.强缓存cache-control,expires,协商缓存ETag/If-Not-Match 、Last-Modified/If-Modified-Since
     *
     *  87.为何mysql事务隔离级别默认是可重复读，而oracle的隔离级别是都已提交
     *  https://www.iteye.com/blog/pre-2438968
     *      因为mysql的主从复制里面binlog日志的原因，binlog模式，Statement：记录的是可能改变数据的SQL语句
     *      可能会导致主从复制的一致性问题
     *
     *  88.netty,es,spring security
     *
     *
     *
     * /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     *
     *  1.说一下自己的优缺点？
     *      缺点：
     *          a.有时候碰到再解决一个问题的时候，在一个很小的细节上面路上会想，睡觉的时候会想--想的时候会被干扰效率也低，而且用处不大
     *          b.有时候把任务布置给别人，觉着不放心或者达不到自己的要求，会去亲力亲为
     *      优点：
     *          a.适应能力强、上手快,
     *          b.坚持学习，乐于分享，代码洁癖
     *  2.说一下碰到最有难度的问题，如何解决？
     *      a.大促高并发的时候，发现有些用户反应请求比较慢
     *      b.通过
     *      c.随着抓取的量越来越多，抓太多，会被视为机器人概率越来越高，一直没有找到办法，各种方法都试过了，包括请求
     *  3.个人平时怎么提升技术？
     *      回家有时间看下技术方面的，在项目上面运用
     *  4.在工作中和生活中遇见最大的挑战是什么？
     *      a.一天事情比较多，但是又必须自己解决，然后有的事情必须在某个时间点完成，
     *      b.通过分先后级别，然后不重要的先不做
     *  5.什么机缘下转的java-------之前是搞c++的，现在web服务
     *      a.之前搞c++的，然后做个c++服务器编程，公司之前搞从c++的不多，刚好老大问我转不转，然后就转了
     *  6.说有offer，然后带个人
     *  7.说挑战性的项目和精通哪些-很重要
        8.强调一下c++的工作经验
        9.看hashmap源码，各种锁的核心源码流程，提升深度，看mybtis核心源码
         10.看所有的面试题，并深入知道里面的流程
         * 3.如果问比较德行的一些中间件等？怎么回答？
     *  笔试题目->计算机网络，排序，算法的等->项目经验->简历
     *  看下面试总结
     *  写今目标的项目-写一个现在做的项目
     *  https://www.cnblogs.com/szlbm/p/5437498.html
     *  http://youzhixueyuan.com/各种干货
     *  最后记得看下学习视频
     *  9.记得，记得，记得，一定要记得引导面试官朝你熟悉的地方，可以一步一步的引过去---------很重要，
     */
}
