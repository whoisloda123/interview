package com.liucan.interview.designpattern.creativepattern.factorymethod;

/**
 * @author liucan
 * @version 19-3-21
 */
public class ConcreteFactory2 implements AbstractFactory {
    @Override
    public Product newProduct() {
        return new ConcreteProduct2();
    }
}
