package com.liucan.kuroky.designpattern.actionpattern.status.statusenum;

import com.liucan.kuroky.designpattern.actionpattern.status.ThreadContext;
import com.liucan.kuroky.designpattern.actionpattern.status.command.ThreadCommand;

/**
 * @author liucan
 * @version 19-3-31
 */
public class RunningThreadStatus extends AbstractThreadStatus {
    @Override
    public void handle(ThreadContext context) {
        ThreadCommand command = context.getCommand();
        if (command.type() == ThreadCommand.TYPE.STOP) {
            command.executed();
            System.out.println("运行状态，准备转换为dead状态");
            context.changeStatus(context.getStatus(ThreadStatus.DEAD));
        } else if (command.type() == ThreadCommand.TYPE.SUSPEND) {
            command.executed();
            System.out.println("运行状态，准备转换为suspend状态");
            context.changeStatus(context.getStatus(ThreadStatus.BLOCKED));
        } else {
            System.out.println("不是get_cpu或suspend命令，不感兴趣");
        }
    }
}
