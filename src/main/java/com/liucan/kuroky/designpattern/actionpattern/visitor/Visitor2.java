package com.liucan.kuroky.designpattern.actionpattern.visitor;

/**
 * @author liucan
 * @version 19-3-31
 */
public class Visitor2 implements IVisitor {
    // 访问普通员工，打印年龄
    @Override
    public void visit(CommonEmployee commonEmployee) {
        System.out.println(commonEmployee.getAge());
    }

    // 访问部门经理，打印地址
    @Override
    public void visit(Manager manager) {
        System.out.println(manager.getAddress());
    }
}
