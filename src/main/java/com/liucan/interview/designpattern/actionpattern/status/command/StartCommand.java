package com.liucan.interview.designpattern.actionpattern.status.command;

/**
 * @author liucan
 * @version 19-3-31
 */
public class StartCommand implements ThreadCommand {
    private ThreadCommandReceiver receiver;

    public StartCommand(ThreadCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void executed() {
        receiver.start();
    }

    @Override
    public TYPE type() {
        return TYPE.START;
    }
}
