package com.liucan.kuroky.base;

/**
 * 1.HashMap的put
 * https://segmentfault.com/a/1190000012926722
 * 一.put流程
 *  a.看数组是否被初始化，没有则初始化数组
 *  b.看通过hash获取到数组的位置是否有值，如果没有则直接插入，如果有值在，判断是否是链表第一个值，如果是直接替换
 *    如果不是则便利到对应的值替换，否则插入到结尾，如果是链表且大于8则转换为红黑树
 *  c.如果容量大于负载容量则调用resize扩容，新建2 x size大小的数组，然后进行转移
 *  d.1.7hashmap会出现在多线程扩容情况下造成循环链表(采用头插发翻转链表)，get的时候一直卡主
 *    1.8是用 head 和 tail 来保证链表的顺序和之前一样，这样就不会产生循环引用
 *    都会出现数据被覆盖的情况
 *  e.hash值为对象的hashcode 和 hashcode的高16异或，主要是为了增大随机性（大部分的hashcode只用到了低16位），
 *  f.size为2的n次方，是因为计算table index的时候为操作比取余速度快，在resize的时候也好计算
 * 2.ConcurrentHashMap
 *  https://zhuanlan.zhihu.com/p/164531596
 *  https://zhuanlan.zhihu.com/p/369568763
 * 一.put流程
 *  a.看数组是否被初始化，没有则初始化数组
 *  b.看通过hash获取到数组的位置是否有值，如果没有则cas插入
 *  c.如果节点是否在扩容，如果在扩容在加入到扩容里面去
 *  d.synchronized锁住当前桶，插入到链表或红黑树尾部，如果链表长度大于8则会转换为红黑树
 *  e.看是否需要扩容，如果当前正在扩容，则加入到扩容里面，和其他线程一起扩容（会根据cpu数和桶大小来平均分给每个线程处理扩容桶数，默认最小是 16）
 * 二.get不需要锁，因为value是volatile类型
 * 三.获取size的实现方式类似于LongAdder实现方式，比CAS效率要高一些
 * @author liucan
 * @date 5/24/21
 */
public interface Map {
}
