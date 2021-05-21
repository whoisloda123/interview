package com.liucan.kuroky.designpattern.structurepattern.adapter;

/**
 * 类适配器
 *
 * @author liucan
 * @version 19-3-25
 */
public class ClassAdapter extends Adaptee implements Target {
    @Override
    public void request() {
        specificRequest();
    }
}
