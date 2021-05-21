package com.liucan.kuroky.designpattern.actionpattern.status.command;

/**
 * @author liucan
 * @version 19-3-31
 */
public class SuspendCommand implements ThreadCommand {
    private ThreadCommandReceiver receiver;

    public SuspendCommand(ThreadCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void executed() {
        receiver.suspend();
    }

    @Override
    public TYPE type() {
        return TYPE.SUSPEND;
    }
}
