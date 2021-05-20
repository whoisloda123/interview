package com.liucan.interview.designpattern.actionpattern.observer;

import java.util.Observable;

/**
 * java Observable和Observer实现了观察者模式
 * <p>
 * 具体目标类：原油期货
 *
 * @author liucan
 * @version 19-3-31
 */
public class OilFutures extends Observable {
    private float price;

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        super.setChanged();  //设置内部标志位，注明数据发生变化
        super.notifyObservers(price);    //通知观察者价格改变了
        this.price = price;
    }
}
