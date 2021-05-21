package com.liucan.kuroky.designpattern.creativepattern.singleton;

/**
 * 饿汉式单例模式
 * 线程安全
 *
 * @author liucan
 * @version 19-3-21
 */
public class HungrySingleton {
    private final static HungrySingleton instance = new HungrySingleton();

    private HungrySingleton() {
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

    public static HungrySingleton getInstance() {
        return instance;
    }
}
