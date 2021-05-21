package com.liucan.kuroky.designpattern.structurepattern.decorator;

/**
 * 具体构件
 * <p>
 * 具体构件角色：原身
 *
 * @author liucan
 * @version 19-3-26
 */
public class OriginalMorrigan implements Morrigan {
    @Override
    public void display() {
        System.out.println("原型莫妮卡");
    }
}
