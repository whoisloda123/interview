package com.liucan.kuroky.designpattern.actionpattern.status.command;

/**
 * @author liucan
 * @version 19-3-31
 */
public class ThreadCommandReceiver {

    public void start() {
        System.out.println("线程开始");
    }

    public void getCpu() {
        System.out.println("线程获取cpu时间片");
    }

    public void stop() {
        System.out.println("线程结束");
    }

    public void suspend() {
        System.out.println("线程暂停");
    }

    public void resume() {
        System.out.println("线程恢复");
    }
}
