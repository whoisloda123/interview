package com.liucan.interview.designpattern.creativepattern.abstractfactory;

/**
 * 抽象工厂模式：工厂方法模式只考虑生产同样的产品，但是大部分情况下是要生成不同类型的产品，
 * 抽象工厂模式就是工厂方法模式的升级版可产生多个类型的产品
 * <p>
 * 当系统中只存在一个类型的产品时，抽象工厂模式将退化到工厂方法模式
 *
 * @author liucan
 * @version 19-3-21
 */
public interface FarmFactory {
    Anima newAnima();

    Plant newPlant();
}
