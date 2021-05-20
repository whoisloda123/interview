package com.liucan.interview.designpattern.actionpattern.visitor;

/**
 * @author liucan
 * @version 19-3-31
 */
public class Visitor1 implements IVisitor {
    // 访问普通员工，打印工作
    @Override
    public void visit(CommonEmployee commonEmployee) {
        System.out.println(commonEmployee.getJob());
    }

    // 访问部门经理，打印业绩
    @Override
    public void visit(Manager manager) {
        System.out.println(manager.getPerformance());
    }
}
