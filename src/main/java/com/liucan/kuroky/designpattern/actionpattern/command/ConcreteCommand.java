package com.liucan.kuroky.designpattern.actionpattern.command;

/**
 * 具体命令
 *
 * @author liucan
 * @version 19-3-28
 */
public class ConcreteCommand implements Command {

    private Recever recever;

    public ConcreteCommand(Recever recever) {
        this.recever = recever;
    }

    @Override
    public void executed() {
        recever.action();
    }
}
