package com.liucan.interview.designpattern.actionpattern.visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * 此角色一般用的少
 *
 * @author liucan
 * @version 19-3-31
 */
public class ObjectStructure {
    private List<Employee> employees = new ArrayList<>();

    public void accept(IVisitor visitor) {
        for (Employee employee : employees) {
            employee.accept(visitor);
        }
    }

    public void add(Employee employee) {
        employees.add(employee);
    }
}
