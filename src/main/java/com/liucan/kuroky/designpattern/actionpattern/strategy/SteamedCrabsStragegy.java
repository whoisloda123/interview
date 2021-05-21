package com.liucan.kuroky.designpattern.actionpattern.strategy;

/**
 * 具体策略类：清蒸大闸蟹
 *
 * @author liucan
 * @version 19-3-28
 */
public class SteamedCrabsStragegy implements CrabCookingStrategy {
    @Override
    public void cookingMethod() {
        System.out.println("具体策略类：清蒸大闸蟹");
    }
}
