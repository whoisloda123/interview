package com.liucan.interview.designpattern.structurepattern.bridge;

/**
 * 具体实现化角色
 *
 * @author liucan
 * @version 19-3-26
 */
public class ConcreteImplementorA implements Implementor {
    @Override
    public void OperationImpl() {
        System.out.println("具体实现化(Concrete Implementor)角色被访问");
    }
}
