package com.liucan.kuroky.designpattern.actionpattern.strategy;

/**
 * 策略（Strategy）模式：该模式定义了一系列算法，并将每个算法封装起来，使它们可以相互替换
 * 准备一组算法，并将这组算法封装到一系列的策略类里面，作为一个抽象策略类的子类
 * <p>
 * 使用场景
 * 一个系统需要动态地在几种算法中选择一种时，可将每个算法封装到策略类中。
 * 一个类定义了多种行为，并且这些行为在这个类的操作中以多个条件语句的形式出现，可将每个条件分支移入它们各自的策略类中以代替这些条件语句
 *
 * @author liucan
 * @version 19-3-28
 */
//大闸蟹加工策略类
public interface CrabCookingStrategy {
    void cookingMethod();
}
