package com.liucan.kuroky.designpattern.creativepattern.builder;

/**
 * 建造者（builder）模式
 * 将一个复杂的对象分解为多个简单的对象，然后一步一步构建而成。它将变与不变相分离，即产品的组成部分是不变的，但每一部分是可以灵活选择的
 * <p>
 * 主要适用于
 * 1.复杂的对象，组合比较固定，但是具体实现比较多
 * 2.很多个参数当做构造函数的入参，且个数不一样
 * <p>
 * 和抽象工厂模式的区别：
 * 1.抽象工厂模式注重产品的创造过程
 * 2.建造者模式注重的是不同产品的组合过程
 * 3.2者可以组合使用
 * <p>
 * 该模式的主要优点如下：
 * 各个具体的建造者相互独立，有利于系统的扩展。
 * 客户端不必知道产品内部组成的细节，便于控制细节风险。
 * <p>
 * 其缺点如下：
 * 产品的组成部分必须相同，这限制了其使用范围。
 * 如果产品的内部变化复杂，该模式会增加很多的建造者类。
 *
 * @author liucan
 * @version 19-3-24
 */
public abstract class FarmBuilder {
    protected Farm farm;

    public abstract FarmBuilder buildAnima();

    public abstract FarmBuilder buildPlant();

    public abstract Farm build();
}
