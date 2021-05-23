package com.liucan.kuroky.datastructure;

/**
 * @author liucan
 * @date 5/22/21
 */
public interface DataStructure {
    /* *
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
     */
}
