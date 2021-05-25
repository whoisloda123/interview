package com.liucan.kuroky.base;

/**
 * 1.HashMap的put
 * https://segmentfault.com/a/1190000012926722
 * https://cloud.tencent.com/developer/article/1120823
 *  a.看数组是否被初始化，没有则初始化数组
 *  b.看通过hash获取到数组的位置是否有值，如果没有则直接插入，如果有值在，判断是否是链表第一个值，如果是直接替换
 *    如果不是则便利到对应的值替换，否则插入到结尾，如果是链表且大于8则转换为红黑树
 *  c.如果容量大于负载容量则调用resize扩容，新建2 x size大小的数组，然后进行转移
 *  d.1.7hashmap会出现在多线程扩容情况下造成循环链表(采用头插发翻转链表)，get的时候一直卡主
 *    1.8是用 head 和 tail 来保证链表的顺序和之前一样，这样就不会产生循环引用
 *    都会出现数据被覆盖的情况
 * 2.ConcurrentHashMap
 *  https://zhuanlan.zhihu.com/p/237295675
 * @author liucan
 * @date 5/24/21
 */
public interface Map {
}
