package com.liucan.interview.designpattern.structurepattern.composite;

/**
 * 组合模式
 *
 * @author liucan
 * @version 19-3-28
 */
public interface Component {
    void add(Component c);

    void remove(Component c);

    Component getChild(int i);

    void operation();
}
