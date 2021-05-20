package com.liucan.interview.designpattern.structurepattern.adapter;

/**
 * 被适配者
 *
 * @author liucan
 * @version 19-3-25
 */
public class Adaptee {

    //原系统的接口
    public void specificRequest() {
        System.out.println("适配者中的业务代码被调用！");
    }
}
