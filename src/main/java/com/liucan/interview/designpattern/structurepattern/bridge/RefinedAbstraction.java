package com.liucan.interview.designpattern.structurepattern.bridge;

/**
 * 扩展抽象化角色
 *
 * @author liucan
 * @version 19-3-26
 */
public class RefinedAbstraction extends Abstraction {

    public RefinedAbstraction(Implementor imple) {
        super(imple);
    }

    public void Operation() {
        System.out.println("扩展抽象化(Refined Abstraction)角色被访问");
        imple.OperationImpl();
    }
}
