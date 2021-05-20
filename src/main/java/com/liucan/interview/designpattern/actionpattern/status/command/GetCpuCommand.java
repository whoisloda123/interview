package com.liucan.interview.designpattern.actionpattern.status.command;

/**
 * @author liucan
 * @version 19-3-31
 */
public class GetCpuCommand implements ThreadCommand {
    private ThreadCommandReceiver receiver;

    public GetCpuCommand(ThreadCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void executed() {
        receiver.getCpu();
    }

    @Override
    public TYPE type() {
        return TYPE.GET_CPU;
    }
}
