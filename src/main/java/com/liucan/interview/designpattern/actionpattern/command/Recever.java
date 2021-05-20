package com.liucan.interview.designpattern.actionpattern.command;

/**
 * 命令接受者
 *
 * @author liucan
 * @version 19-3-28
 */
public class Recever {
    private String msg;

    public Recever() {
    }

    public Recever(String msg) {
        this.msg = msg;
    }

    public void action() {
        System.out.println("接收者的action()方法被调用..." + msg);
    }
}
