package com.liucan.interview.designpattern.structurepattern.bridge;

/**
 * 抽象化角色
 * <p>
 * 桥接（Bridge）模式:将抽象与实现分离,用组合关系代替继承关系来实现，从而降低了抽象和实现这两个可变维度的耦合
 * <p>
 * 桥接（Bridge）模式的优点是：
 * 由于抽象与实现分离，所以扩展能力强；
 * 其实现细节对客户透明。
 * <p>
 * 桥接（Bridge）模式包含以下主要角色。
 * 抽象化（Abstraction）角色：定义抽象类，并包含一个对实现化对象的引用。
 * 扩展抽象化（Refined    Abstraction）角色：是抽象化角色的子类，实现父类中的业务方法，并通过组合关系调用实现化角色中的业务方法。
 * 实现化（Implementor）角色：定义实现化角色的接口，供扩展抽象化角色调用。
 * 具体实现化（Concrete Implementor）角色：给出实现化角色接口的具体实现。
 * <p>
 * <p>
 * 桥接模式通常适用于以下场景。
 * 当一个类存在两个独立变化的维度，且这两个维度都需要进行扩展时。
 * 当一个系统不希望使用继承或因为多层次继承导致系统类的个数急剧增加时。
 * 当一个系统需要在构件的抽象化角色和具体化角色之间增加更多的灵活性时。
 *
 * @author liucan
 * @version 19-3-26
 */
public abstract class Abstraction {
    //可以继续添加其他的具体实现类
    protected Implementor imple;

    protected Abstraction(Implementor imple) {
        this.imple = imple;
    }

    public abstract void Operation();
}
