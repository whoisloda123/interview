package com.liucan.interview.designpattern.structurepattern.decorator;

/**
 * 具体装饰角色：少女
 *
 * @author liucan
 * @version 19-3-26
 */
public class GirlMorrigan extends ChangeMorrigan {

    public GirlMorrigan(Morrigan morrigan) {
        super(morrigan);
    }

    @Override
    public void display() {
        super.display();
        //其他装饰
    }
}
