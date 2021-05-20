package com.liucan.interview.designpattern.actionpattern.strategy;

/**
 * 具体策略类：红烧大闸蟹
 *
 * @author liucan
 * @version 19-3-28
 */
public class BraisedCrabsStragegy implements CrabCookingStrategy {
    @Override
    public void cookingMethod() {
        System.out.println("具体策略类：红烧大闸蟹");
    }
}
