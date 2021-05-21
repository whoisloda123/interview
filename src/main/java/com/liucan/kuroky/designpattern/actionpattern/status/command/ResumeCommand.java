package com.liucan.kuroky.designpattern.actionpattern.status.command;

/**
 * @author liucan
 * @version 19-3-31
 */
public class ResumeCommand implements ThreadCommand {
    private ThreadCommandReceiver receiver;

    public ResumeCommand(ThreadCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void executed() {
        receiver.resume();
    }

    @Override
    public TYPE type() {
        return TYPE.RESUME;
    }
}
