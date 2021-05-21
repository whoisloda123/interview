package com.liucan.kuroky.designpattern.structurepattern.adapter;

/**
 * 对象适配器
 *
 * @author liucan
 * @version 19-3-25
 */
public class ObjectAdapter implements Target {

    private Adaptee adaptee;

    public ObjectAdapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void request() {
        adaptee.specificRequest();
    }
}
