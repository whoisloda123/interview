package com.liucan.interview.designpattern.actionpattern.status.statusenum;

/**
 * @author liucan
 * @version 19-3-31
 */
public enum ThreadStatus {
    NEW("新建"),
    RUNNABLE("就绪"),
    RUNNING("运行"),
    BLOCKED("阻塞"),
    DEAD("死亡");

    private String msg;

    ThreadStatus(String msg) {
        this.msg = msg;
    }
}
