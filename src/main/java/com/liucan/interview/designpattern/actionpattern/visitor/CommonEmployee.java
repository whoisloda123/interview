package com.liucan.interview.designpattern.actionpattern.visitor;

import lombok.Builder;
import lombok.Data;

/**
 * @author liucan
 * @version 19-3-31
 */
@Builder
@Data
public class CommonEmployee extends Employee {
    private String job; //工作
    private int age;

    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}
