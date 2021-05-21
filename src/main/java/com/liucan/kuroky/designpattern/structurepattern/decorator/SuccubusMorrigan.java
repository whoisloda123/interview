package com.liucan.kuroky.designpattern.structurepattern.decorator;

/**
 * 具体装饰
 * <p>
 * 具体装饰角色：女妖
 *
 * @author liucan
 * @version 19-3-26
 */
public class SuccubusMorrigan extends ChangeMorrigan {

    public SuccubusMorrigan(Morrigan morrigan) {
        super(morrigan);
    }

    @Override
    public void display() {
        super.display();
        //其他装饰
    }
}
