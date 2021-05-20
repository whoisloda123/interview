package com.liucan.interview.designpattern.creativepattern.singleton;

/**
 * 懒汉式，在需要的时候才初始化
 * 必须要加上volatile和synchronized否则会有多线程问题
 * 缺点：加锁影响性能
 *
 * @author liucan
 * @version 19-3-21
 */
public class LazySingleton {
    private static volatile LazySingleton instance = null;
    private static final Object lock = new Object();

    private LazySingleton() {
    }

    /**
     * 可用于单例模式反序列化时生成新的对象
     * JVM从内存中反序列化地"组装"一个新对象时,就会自动调用这个 readResolve方法来返回我们指定好的对象了, 单例规则也就得到了保证.
     */
    private Object readResolve() {
        // instead of the object we're on,
        // return the class variable INSTANCE
        return instance;

    }

    public static LazySingleton getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new LazySingleton();
                }
            }
        }
        return instance;
    }
}
