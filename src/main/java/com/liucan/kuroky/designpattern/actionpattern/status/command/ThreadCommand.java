package com.liucan.kuroky.designpattern.actionpattern.status.command;

/**
 * @author liucan
 * @version 19-3-31
 */
public interface ThreadCommand {
    enum TYPE {
        START,
        GET_CPU,
        STOP,
        SUSPEND,
        RESUME
    }

    void executed();

    TYPE type();
}
