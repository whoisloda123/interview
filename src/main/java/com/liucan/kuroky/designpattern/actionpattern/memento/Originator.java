package com.liucan.kuroky.designpattern.actionpattern.memento;

import com.liucan.kuroky.designpattern.actionpattern.command.Command;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 发起人角色：记录当前时刻的内部状态信息，提供创建备忘录和恢复备忘录数据的功能
 *
 * @author liucan
 * @version 19-3-30
 */
@AllArgsConstructor
public class Originator {

    private final MementoManager mementoManager;

    /**
     * Originator相对于命令模式的invoker
     */
    public void doCommand(Command command) {
        command.executed();
        mementoManager.addMemento(createCommandMemento(command));
    }

    /*
     * 创建备忘录
     */
    private CommandMemento createCommandMemento(Command command) {
        CommandMemento commandMemento = new CommandMemento();
        commandMemento.setCommand(command);
        commandMemento.setLocalDateTime(LocalDateTime.now());
        return commandMemento;
    }

    /*
     * 撤销
     */
    public void undo() {
        undo(mementoManager.undo());
    }

    public void undo(CommandMemento commandMemento) {
        if (commandMemento == null) {
            return;
        }
        System.out.println("撤销命令，开始执行上一条命令");
        commandMemento.getCommand().executed();
    }

    /*
     * 恢复
     */
    public void resume() {
        System.out.println("恢复命令，开始执行下一条命令");
        mementoManager.resume().getCommand().executed();
    }
}
