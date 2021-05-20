package com.liucan.interview.designpattern.structurepattern.decorator;

/**
 * 抽象装饰
 * <p>
 * 抽象装饰角色：变形
 * 也可以不是抽象装饰，直接在ChangeMorrigan的display里面添加额外的功能
 *
 * @author liucan
 * @version 19-3-26
 */
public class ChangeMorrigan implements Morrigan {

    private Morrigan morrigan;

    public ChangeMorrigan(Morrigan morrigan) {
        this.morrigan = morrigan;
    }

    @Override
    public void display() {
        //有时想用一些现存的组件。这些组件可能只是完成了一些核心功能。但在不改变其结构的情况下，可以动态地扩展其组件功能
        morrigan.display();
    }
}
