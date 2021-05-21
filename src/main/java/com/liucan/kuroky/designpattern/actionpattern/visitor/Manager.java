package com.liucan.kuroky.designpattern.actionpattern.visitor;

import lombok.Builder;
import lombok.Data;

/**
 * @author liucan
 * @version 19-3-31
 */
@Builder
@Data
public class Manager extends Employee {
    private String performance; //业绩
    private String address;

    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}
