package com.liucan.interview.designpattern.creativepattern.factorymethod;

/**
 * 工厂方法模式:工厂方法模式由抽象工厂、具体工厂、抽象产品和具体产品4个要素构成
 * 1.抽象工厂（Abstract Factory）：提供了创建产品的接口，newProduct() 来创建产品。
 * 2.具体工厂（ConcreteFactory）：实现抽象工厂中的抽象方法，完成具体产品的创建。
 * 3.抽象产品（Product）：定义了产品的规范，描述了产品的主要特性和功能。
 * 4.具体产品（ConcreteProduct）：实现了抽象产品角色所定义的接口，由具体工厂来创建，它同具体工厂之间一一对应。
 * <p>
 * 优点：用户只需要知道具体工厂的名称就可得到所要的产品，无须知道产品的具体创建过程；
 * 增加新产品时只需添加具体产品类和对应的具体工厂类，无须对原工厂进行任何修改，满足开闭原则
 * 缺点：每增加一个产品就要增加一个具体产品类和一个对应的具体工厂类，增加系统的复杂
 * <p>
 * 当需要生成的产品不多且不会增加，一个具体工厂类就可以完成任务时，可删除抽象工厂类。这时工厂方法模式将退化到简单工厂模式
 *
 * @author liucan
 * @version 19-3-21
 */
public interface AbstractFactory {
    Product newProduct();
}
