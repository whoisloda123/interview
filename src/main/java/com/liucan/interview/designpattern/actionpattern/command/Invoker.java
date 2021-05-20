package com.liucan.interview.designpattern.actionpattern.command;

/**
 * 命令执行者
 *
 * @author liucan
 * @version 19-3-28
 */
public class Invoker {

    private Command command;

    public Invoker(Command command) {
        this.command = command;
    }

    public void invoke() {
        command.executed();
    }
}
