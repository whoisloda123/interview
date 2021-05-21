package com.liucan.kuroky.designpattern.structurepattern.proxy;

/**
 * 真实主题
 *
 * @author liucan
 * @version 19-3-24
 */
public class RealSubject implements Subject {
    @Override
    public void Request() {
        System.out.println("开始请求了");
    }
}
