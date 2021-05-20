package com.liucan.interview.designpattern.structurepattern.composite;

import java.util.ArrayList;
import java.util.List;

/**
 * 树枝构件
 *
 * @author liucan
 * @version 19-3-28
 */
public class Composite implements Component {

    private List<Component> childrens = new ArrayList<>();

    @Override
    public void add(Component c) {
        childrens.add(c);
    }

    @Override
    public void remove(Component c) {
        childrens.remove(c);
    }

    @Override
    public Component getChild(int i) {
        return childrens.get(i);
    }

    @Override
    public void operation() {
        childrens.forEach(Component::operation);
    }
}
