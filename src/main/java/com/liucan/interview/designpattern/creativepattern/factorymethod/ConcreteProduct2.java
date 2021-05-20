package com.liucan.interview.designpattern.creativepattern.factorymethod;

/**
 * @author liucan
 * @version 19-3-21
 */
public class ConcreteProduct2 implements Product {
    @Override
    public void show() {
        System.out.println(getClass());
    }
}
