package com.liucan.interview.designpattern.actionpattern.status.statusenum;

import com.liucan.interview.designpattern.actionpattern.status.ThreadContext;
import com.liucan.interview.designpattern.actionpattern.status.command.ThreadCommand;

/**
 * @author liucan
 * @version 19-3-31
 */
public class RunnableThreadStatus extends AbstractThreadStatus {
    @Override
    public void handle(ThreadContext context) {
        ThreadCommand command = context.getCommand();
        if (command.type() == ThreadCommand.TYPE.GET_CPU) {
            command.executed();
            System.out.println("就绪状态，准备转换为运行状态");
            context.changeStatus(context.getStatus(ThreadStatus.RUNNING));
        } else {
            System.out.println("不是get_cpu命令，不感兴趣");
        }
    }
}
