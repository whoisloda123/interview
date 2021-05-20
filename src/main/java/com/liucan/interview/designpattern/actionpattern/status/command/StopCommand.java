package com.liucan.interview.designpattern.actionpattern.status.command;

/**
 * @author liucan
 * @version 19-3-31
 */
public class StopCommand implements ThreadCommand {
    private ThreadCommandReceiver receiver;

    public StopCommand(ThreadCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void executed() {
        receiver.stop();
    }

    @Override
    public TYPE type() {
        return TYPE.STOP;
    }
}
