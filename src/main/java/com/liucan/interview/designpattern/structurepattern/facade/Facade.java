package com.liucan.interview.designpattern.structurepattern.facade;

/**
 * 外观(facade)模式；是一种通过为多个复杂的子系统提供一个一致的接口
 * 结构比较简单，主要是定义了一个高层接口。它包含了对各个子系统的引用
 *
 * @author liucan
 * @version 19-3-26
 */
public class Facade {
    private SubSystem01 obj1 = new SubSystem01();

    private SubSystem02 obj2 = new SubSystem02();

    private SubSystem03 obj3 = new SubSystem03();

    public void method() {
        obj1.method1();
        obj2.method2();
        obj3.method3();
    }
}
