package com.liucan.interview.designpattern.actionpattern.visitor;

/**
 * @author liucan
 * @version 19-3-31
 */
public interface IVisitor {
    //可以访问普通员工
    void visit(CommonEmployee commonEmployee);

    //可以访问经理
    void visit(Manager manager);
}
