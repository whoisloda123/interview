package com.liucan.interview.designpattern.structurepattern;

import com.liucan.interview.designpattern.structurepattern.adapter.Adaptee;
import com.liucan.interview.designpattern.structurepattern.adapter.ClassAdapter;
import com.liucan.interview.designpattern.structurepattern.adapter.ObjectAdapter;
import com.liucan.interview.designpattern.structurepattern.bridge.Abstraction;
import com.liucan.interview.designpattern.structurepattern.bridge.ConcreteImplementorA;
import com.liucan.interview.designpattern.structurepattern.bridge.Implementor;
import com.liucan.interview.designpattern.structurepattern.bridge.RefinedAbstraction;
import com.liucan.interview.designpattern.structurepattern.composite.Component;
import com.liucan.interview.designpattern.structurepattern.composite.Composite;
import com.liucan.interview.designpattern.structurepattern.composite.Leaf;
import com.liucan.interview.designpattern.structurepattern.decorator.GirlMorrigan;
import com.liucan.interview.designpattern.structurepattern.decorator.OriginalMorrigan;
import com.liucan.interview.designpattern.structurepattern.decorator.SuccubusMorrigan;
import com.liucan.interview.designpattern.structurepattern.facade.Facade;
import com.liucan.interview.designpattern.structurepattern.proxy.*;

/**
 * 结构型模式：描述如何将类或对象按某种布局组成更大的结构
 * <p>
 * 结构型模式分为以下 7 种：
 * 代理（Proxy）模式：为某对象提供一种代理以控制对该对象的访问。即客户端通过代理间接地访问该对象，从而限制、增强或修改该对象的一些特性。
 * 适配器（Adapter）模式：将一个类的接口转换成客户希望的另外一个接口，使得原本由于接口不兼容而不能一起工作的那些类能一起工作。
 * 桥接（Bridge）模式：将抽象与实现分离，使它们可以独立变化。它是用组合关系代替继承关系来实现的，从而降低了抽象和实现这两个可变维度的耦合度。
 * 装饰（Decorator）模式：动态地给对象增加一些职责，即增加其额外的功能。
 * 外观（Facade）模式：为多个复杂的子系统提供一个一致的接口，使这些子系统更加容易被访问。
 * 享元（Flyweight）模式：运用共享技术来有效地支持大量细粒度对象的复用。
 * 组合（Composite）模式：将对象组合成树状层次结构，使用户对单个对象和组合对象具有一致的访问性。
 *
 * @author liucan
 * @version 19-3-24
 */
public class StructurePatterns {

    public void test() {
        //静态代理模式
        StaticProxy staticProxy = new StaticProxy();
        staticProxy.Request();

        //jdk动态代理模式
//        Subject subject = (Subject) Proxy.newProxyInstance(Subject.class.getClassLoader(),
//                new Class[]{Subject.class},
//                new JdkDynamicProxy(new RealSubject()));
//        subject.Request();
        Subject jdkProxy = (Subject) JdkDynamicProxy.getProxy(new RealSubject());
        jdkProxy.Request();

        //cglib动态代理模式
        Subject cglibProxy = (Subject) CglibDynamicProxy.getProxy(new RealSubject());
        cglibProxy.Request();

        //适配器模式
        new ClassAdapter().request();
        new ObjectAdapter(new Adaptee()).request();

        //bridge桥接模式
        Implementor imple = new ConcreteImplementorA();
        Abstraction abs = new RefinedAbstraction(imple);
        abs.Operation();

        //装饰者模式
        OriginalMorrigan originalMorrigan = new OriginalMorrigan();
        SuccubusMorrigan succubusMorrigan = new SuccubusMorrigan(originalMorrigan);
        succubusMorrigan.display();
        GirlMorrigan girlMorrigan = new GirlMorrigan(originalMorrigan);
        girlMorrigan.display();

        //外观模式
        new Facade().method();

        //组合模式
        Component root = new Composite();
        root.add(new Leaf("leaf1"));
        Composite c1 = new Composite();
        root.add(c1);
        c1.add(new Leaf("leaf2"));
        c1.add(new Leaf("leaf3"));
        root.operation();
    }
}
