package com.liucan.interview.designpattern.actionpattern.status;

import com.liucan.interview.designpattern.actionpattern.status.command.ThreadCommand;
import com.liucan.interview.designpattern.actionpattern.status.statusenum.*;
import lombok.Getter;

import java.util.HashMap;

/**
 * 状态模式：对有状态的对象，把复杂的“判断逻辑”提取到不同的状态对象中，允许状态对象在其内部状态发生改变时改变其行为
 * <p>
 * 使用场景
 * 当一个对象的行为取决于它的状态，并且它必须在运行时根据状态改变它的行为
 * 一个操作中含有庞大的分支结构，并且这些分支决定于对象的状态时。
 * <p>
 * 此处模拟线程的几个状态：新建状态--start()-->就绪状态--getCpu()-->运行(--suspend()-->阻塞--resume()-->就绪->运行)---stop()-->死亡
 * 和享元模式、命令模式一起
 *
 * @author liucan
 * @version 19-3-31
 */
public class ThreadContext {
    private AbstractThreadStatus currentThreadStatus;
    @Getter
    private ThreadCommand command;

    private HashMap<ThreadStatus, AbstractThreadStatus> statusMap = new HashMap<>();

    public ThreadContext() {
        statusMap.put(ThreadStatus.NEW, new NewThreadStatus());
        statusMap.put(ThreadStatus.RUNNABLE, new RunnableThreadStatus());
        statusMap.put(ThreadStatus.RUNNING, new RunningThreadStatus());
        statusMap.put(ThreadStatus.BLOCKED, new BlockedThreadStatus());
        statusMap.put(ThreadStatus.DEAD, new DeadThreadStatus());

        currentThreadStatus = getStatus(ThreadStatus.NEW);
    }

    public AbstractThreadStatus getStatus(ThreadStatus status) {
        return statusMap.get(status);
    }

    public void changeStatus(AbstractThreadStatus threadStatus) {
        this.currentThreadStatus = threadStatus;
    }

    public void handle(ThreadCommand command) {
        this.command = command;
        currentThreadStatus.handle(this);
    }
}
