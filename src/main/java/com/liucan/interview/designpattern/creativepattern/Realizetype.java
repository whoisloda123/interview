package com.liucan.interview.designpattern.creativepattern;

/**
 * 原型模式：提供复制方式，子类实现
 * 由于 Java 提供了对象的 clone() 方法，所以用 Java 实现原型模式很简单
 *
 * @author liucan
 * @version 19-3-21
 */
public class Realizetype implements Cloneable {

    //默认浅拷贝
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
